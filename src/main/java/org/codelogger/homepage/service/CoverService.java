package org.codelogger.homepage.service;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codelogger.core.utils.JsonUtils;
import org.codelogger.homepage.bean.*;
import org.codelogger.homepage.dao.CoverRedisDao;
import org.codelogger.homepage.utils.UnicodeEntityUtils;
import org.codelogger.homepage.vo.CoverReqVo;
import org.codelogger.homepage.vo.CoverTemplateData;
import org.codelogger.utils.ArrayUtils;
import org.codelogger.utils.CollectionUtils;
import org.codelogger.utils.exceptions.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.codelogger.homepage.bean.ProgramDataPlaceholder.*;
import static org.codelogger.utils.CollectionUtils.isNotEmpty;
import static org.codelogger.utils.ExceptionUtils.iae;
import static org.codelogger.utils.JudgeUtils.allIsNotNull;
import static org.codelogger.utils.ValueUtils.getValue;

/**
 * Created by defei on 5/13/15.
 */
@Service("service.cover")
public class CoverService {

    private static final Logger logger = LoggerFactory.getLogger(CoverService.class);
    public static final String M_D = "M月d日";
    public static final String DEFAULT_USER_LOGO = "http://s.depotnearby.com/images/face.png";
    public static final String DEFAULT_COMMODITY_LOGO = "http://s.depotnearby.com/images/pic_prod_3.jpg";
    public static final String DEFAULT_ACTIVITY_LOGO =
        "http://s.depotnearby.com/lib/gridy/images/activity_default.jpg";
    public static final String DEFAULT_GROUP_LOGO = "http://s.depotnearby.com/images/face_group_2.jpg";
    public static final String SELECTOR_JOIN_SYMBOL = ".";

    @Autowired private CoverRedisDao coverRedisDao;

    @Autowired private CoverDataProvider coverDataProvider;

    @Autowired private ProgramService programService;

    private static final Map<String, String> URL_TO_HTML_CONTENT = newHashMap();

    @PostConstruct
    public void postConstruct() {

        updateAllCoverTemplates();

        onListen();
    }

    public Cover getCoverByCityIdAndPosition(CoverReqVo coverReqVo,
        CoverClientType coverClientType) {

        String osOfReq =
            coverReqVo.getGlobalParams() == null ? null : coverReqVo.getGlobalParams().getOs();
        String verOfReq =
            coverReqVo.getGlobalParams() == null ? null : coverReqVo.getGlobalParams().getVer();
        List<Cover> coverRos = coverRedisDao.getAllCovers(coverClientType);
        coverRos.addAll(coverRedisDao.getAllCovers(CoverClientType.ALL));
        Collection<Cover> matchedCovers = newArrayList();
        if (isNotEmpty(coverRos)) {
            for (Cover coverRo : coverRos) {
                if (!Objects.equals(coverRo.getStatus(), CommonStatus.ENABLE)) {
                    continue;
                }
                if (Objects.equals(coverRo.getClientType(), coverClientType) || Objects.equals(coverRo.getClientType(), CoverClientType.ALL)) {
                    matchedCovers.add(coverRo);
                }
            }
            return coverClientType == CoverClientType.APP ?
                getValidTopZIndexRo(matchedCovers, osOfReq, verOfReq) :
                getValidTopZIndexRo(matchedCovers, null, null);
        }
        return null;
    }

    public String getHomePageHtmlContentByCityIdAndShopTypeId(CoverReqVo coverReqVo,
        CoverClientType coverClientType, String picHost) {
        Cover coverRoByCityIdAndPosition =
            getCoverByCityIdAndPosition(coverReqVo, coverClientType);
        return getHomePageHtmlContentByCover(coverRoByCityIdAndPosition, picHost, false);
    }

    public String getBodyHtmlContentByCityIdAndPosition(CoverReqVo coverReqVo,
        CoverClientType coverClientType, String picHost) {
        String homePageHtmlContentByCityIdAndPosition =
            getHomePageHtmlContentByCityIdAndShopTypeId(coverReqVo, coverClientType, picHost);
        if (logger.isDebugEnabled()) {
            logger
                .debug("get data with length:{}.", homePageHtmlContentByCityIdAndPosition.length());
        }
        return isNotBlank(homePageHtmlContentByCityIdAndPosition) ?
            Jsoup.parse(homePageHtmlContentByCityIdAndPosition).body().html() :
            homePageHtmlContentByCityIdAndPosition;
    }

    public String getHomePageHtmlContentByCover(Cover coverRo, String picHost,
        Boolean disableCache) {
        if (coverRo != null) {
            try {
                String htmlContent = coverRo.getTemplate();
                if (isNotBlank(coverRo.getUrl())) {
                    htmlContent = disableCache ? null : URL_TO_HTML_CONTENT.get(coverRo.getUrl());
                    if (isBlank(htmlContent)) {
                        htmlContent = downloadUrl(coverRo.getUrl());
                        if (!disableCache) {
                            URL_TO_HTML_CONTENT.put(coverRo.getUrl(), htmlContent);
                        }
                    }
                }
                if (isNotBlank(htmlContent)) {
                    return renderTemplate(picHost, coverRo, htmlContent);
                }
            } catch (Exception e) {
                logger.warn("can not render homepage for cover[{}].", coverRo.getId(), e);
                if (!disableCache)
                    URL_TO_HTML_CONTENT.remove(coverRo.getUrl());
            }
        }
        logger.debug("return empty cover home page html by cover[]",
            coverRo == null ? null : coverRo.getId());
        return "";
    }

    private String renderTemplate(String picHost, Cover coverRoByCityIdAndPosition,
        String htmlContent) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        CoverTemplateData coverTemplateData =
            coverDataProvider.getCoverTemplateData(coverRoByCityIdAndPosition.getId());

        if (coverTemplateData.hasChildren()) {
            Document template = Jsoup.parseBodyFragment(htmlContent);
            Elements elementsOfNeedToBindSource =
                template.getElementsByAttribute(DATA_SOURCE.getPlaceholder());
            if(ArrayUtils.isNotEmpty(elementsOfNeedToBindSource)){
                for (Element elementOfNeedToBindSource : elementsOfNeedToBindSource) {
                    processSourceElement(elementOfNeedToBindSource, coverTemplateData.getChild(elementOfNeedToBindSource.attr(DATA_SOURCE.getPlaceholder())));
                }

            }
            htmlContent = template.body().outerHtml();
        }

        long milliseconds = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.debug(
            "==> render homepage used {} milli seconds for cover[{}, htmlContent length[{}] <==",
            milliseconds, coverRoByCityIdAndPosition.getId(), htmlContent.length());
        return htmlContent;
    }

    private void processSourceElement(Element elementOfNeedToBindSource, CoverTemplateData coverTemplateData){
        try {
            if(elementOfNeedToBindSource == null || coverTemplateData == null){
                return;
            }
            processRepeatElement(elementOfNeedToBindSource, coverTemplateData);
            bindFieldData(elementOfNeedToBindSource, coverTemplateData);
            String outerHtml = bindData(elementOfNeedToBindSource, coverTemplateData);
            if(elementOfNeedToBindSource.parent() != null) {
                elementOfNeedToBindSource.before(outerHtml);
                elementOfNeedToBindSource.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRepeatElement(Element elementOfNeedToBindSource, CoverTemplateData coverTemplateData){

        List<CoverTemplateData> dataSourcesOfCurrentRepeat = coverTemplateData.getElements();
        if(ArrayUtils.isEmpty(dataSourcesOfCurrentRepeat)) return;
        Elements elementsOfNeedToRepeat = findDirectChildrenByAttr(elementOfNeedToBindSource,
            REPEAT_ELEMENT.getPlaceholder());
        if(CollectionUtils.isEmpty(elementsOfNeedToRepeat)) {
            if(elementOfNeedToBindSource != null && elementOfNeedToBindSource.hasAttr(REPEAT_ELEMENT.getPlaceholder())){
                doRepeat(elementOfNeedToBindSource, coverTemplateData);
                return;
            }
        }
        for (Element elementOfNeedToRepeat : elementsOfNeedToRepeat) {
            List<String> dataSelectors = getDataSelectors(elementOfNeedToRepeat);
            CoverTemplateData sourceOfCurrentRepeat = coverTemplateData.getChild(dataSelectors);
            if(CollectionUtils.isEmpty(sourceOfCurrentRepeat.getElements())) continue;
            //为当前需要重复的元素绑定数据
            for (CoverTemplateData dataSource : sourceOfCurrentRepeat.getElements()) {
                //克隆当前需要重复的元素
                Element cloneOfElementOfNeedToRepeat = elementOfNeedToRepeat.clone();
                Elements subRepeatElements = findDirectChildrenByAttr(cloneOfElementOfNeedToRepeat,
                    REPEAT_ELEMENT.getPlaceholder());
                if(ArrayUtils.isNotEmpty(subRepeatElements)) {
                    for (Element subRepeatElement : subRepeatElements) {
                        if (!cloneOfElementOfNeedToRepeat.equals(subRepeatElement)) {
                            CoverTemplateData dataSourceOfSubElement = dataSource.getChild(getDataSelectors(subRepeatElement));
                            processSourceElement(subRepeatElement, dataSourceOfSubElement);
                        }
                    }
                }
                bindFieldData(cloneOfElementOfNeedToRepeat, dataSource);
                String htmlOfElementToRepeat = bindData(cloneOfElementOfNeedToRepeat, dataSource);
                elementOfNeedToRepeat.before(htmlOfElementToRepeat);
            }
            elementOfNeedToRepeat.remove();
        }
    }

    private void doRepeat(Element element, CoverTemplateData coverTemplateData){
        if(element != null && coverTemplateData != null && CollectionUtils.isNotEmpty(coverTemplateData.getElements())){
            element.removeAttr(REPEAT_ELEMENT.getPlaceholder());
            for (CoverTemplateData templateData : coverTemplateData.getElements()) {
                bindFieldData(element, templateData);
                String htmlOfElementToRepeat = bindData(element, templateData);
                element.before(htmlOfElementToRepeat);
            }
            element.remove();
        }
    }

    private void bindFieldData(Element element, CoverTemplateData coverTemplateData){

        Elements fieldElements = findDirectChildrenByAttr(element, DATA_FIELD.getPlaceholder());
        for (Element fieldElement : fieldElements) {
            Elements subFieldElements = findDirectChildrenByAttr(fieldElement, DATA_FIELD.getPlaceholder());
            Element clone = fieldElement.clone();
            CoverTemplateData fieldCoverTemplateData = coverTemplateData.getChild(getDataSelectors(clone));
            for (Element subFieldElement : subFieldElements) {
                bindFieldData(subFieldElement, fieldCoverTemplateData);
            }
            fieldElement.before(bindData(clone, fieldCoverTemplateData));
            fieldElement.remove();
        }

    }

    private String bindData(Element element, CoverTemplateData dataSource){
        String htmlOfElementToRepeat = element.outerHtml();
        if(dataSource == null){
            return htmlOfElementToRepeat;
        }
        String processedHtml = replaceAllIfValueIsNull(htmlOfElementToRepeat, TEXT.getPlaceholder(), dataSource.getText());
        processedHtml = replaceAllIfValueIsNull(processedHtml,SECONDARY_TEXT.getPlaceholder(), dataSource.getSecondaryText());
        processedHtml = replaceAllIfValueIsNull(processedHtml,LINK.getPlaceholder(), dataSource.getLink());
        processedHtml = replaceAllIfValueIsNull(processedHtml,STYLE_CLASS.getPlaceholder(), dataSource.getStyleClass());
        processedHtml = replaceAllIfValueIsNull(processedHtml,IMG_URL.getPlaceholder(), dataSource.getImgUrl());
        processedHtml = replaceAllIfValueIsNull(processedHtml,ORDER_INDEX.getPlaceholder(), dataSource.getOrderIndex());
        processedHtml = replaceAllIfValueIsNull(processedHtml,Showcase_SalePrice.getPlaceholder(), convertToYuan(
            dataSource.getSalePrice()));
        processedHtml = replaceAllIfValueIsNull(processedHtml,Showcase_OriginPrice.getPlaceholder(), convertToYuan(
            dataSource.getOriginPrice()));
        return processedHtml;
    }

    private Elements findDirectChildrenByAttr(Element element, String attr){

        Elements elements = element.getElementsByAttribute(attr);
        if(ArrayUtils.isEmpty(elements)) return elements;
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()){
            Boolean isFirst = true;
            Element repeatElement = iterator.next();
            while (repeatElement != null){
                if(isFirst && Objects.equals(element, repeatElement)){
                    iterator.remove();
                    break;
                }
                isFirst = false;
                repeatElement = repeatElement.parent();
                if(repeatElement == null || Objects.equals(repeatElement, element)){
                    break;
                }
                if(repeatElement.hasAttr(attr)){
                    iterator.remove();
                    break;
                }
            }
        }
        return elements;
    }

    private Double convertToYuan(Integer price){
        return price == null ? null : price / 100D;
    }

    private String replaceAllIfValueIsNull(String originStr, String select, Object value){

        if(originStr == null || select == null || value == null){
            return originStr;
        } else {
            return originStr.replaceAll(select, value.toString());
        }
    }

    private List<String> getDataSelectors(Element element){

        List<String> selectors = newArrayList();
        Element elementNeedToFind = element;
        Boolean repeatTagIsFound = false;
        while (elementNeedToFind != null){
            String repeatField = elementNeedToFind.attr(REPEAT_ELEMENT.getPlaceholder());
            if(StringUtils.isNotBlank(repeatField)){
                if(repeatTagIsFound){
                    break;
                }
                selectors.add(0, repeatField);
                repeatTagIsFound = true;
            } else {
                String dataField = elementNeedToFind.attr(DATA_FIELD.getPlaceholder());
                if(StringUtils.isNotBlank(dataField)){
                    selectors.add(0, dataField);
                }
            }
            elementNeedToFind = elementNeedToFind.parent();
        }
        return selectors;
    }

//    private String renderTemplate1(String picHost, Cover coverRoByCityIdAndPosition,
//        String htmlContent) {
//        Stopwatch stopwatch = Stopwatch.createStarted();
//        List<ProgramRo> programs =
//            programService.getProgramsByCoverId(coverRoByCityIdAndPosition.getId());
//        if (isNotEmpty(programs)) {
//            List<String> htmlElements = newArrayList();
//            for (ProgramRo program : programs) {
//                try {
//                    if (program != null && Objects
//                        .equals(program.getStatus(), CommonStatus.ENABLE)) {
//                        if (Objects.equals(program.getType(), ProgramDataType.HTML)) {
//                            htmlElements.add(program.getHtml());
//                        } else {
//                            String processedTemplateHtml = null;
//                            Document template = Jsoup.parseBodyFragment(htmlContent);
//                            logger.debug("Render {}[{}]", program.getType(), program.getSid());
//                            switch (program.getType()){
//                                case SEARCH_COMPONENT:
//                                    processedTemplateHtml =
//                                        bindSearchDataToTemplate(template, program.getSid(), JsonUtils.fromJson(program.getDataJson(), SearchComponent.class));
//                                    break;
//                                case NAVIGATION:
//                                    processedTemplateHtml = bindNavigationDataToTemplate(template, program.getSid(),
//                                        JsonUtils.fromJson(program.getDataJson(), NavigationComponent.class));
//                                    break;
//                                case SLIDER:
//                                    processedTemplateHtml = bindSliderDataToTemplate(template,
//                                        program.getSid(), JsonUtils
//                                        .fromJson(program.getDataJson(), SliderComponent.class));
//                                    break;
//                                case SECKILL:
//                                    processedTemplateHtml = bindSeckillDataToTemplate(template,
//                                        program.getSid(),
//                                        JsonUtils.fromJson(program.getDataJson(), SecKill.class));
//                                    break;
//                                case CATEGORY_COMPONENT:
//                                    processedTemplateHtml = bindCategoryDataToTemplate(template,
//                                        program.getSid(), JsonUtils
//                                        .fromJson(program.getDataJson(), CategoryComponent.class));
//                                    break;
//                            }
//                            if (isNotBlank(processedTemplateHtml)) {
//                                htmlElements.add(processedTemplateHtml);
//                                htmlContent = processedTemplateHtml;
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
////            htmlContent = StringUtils.substringBefore(htmlContent, "<body>") + "<body>" + CollectionUtils.join(htmlElements, "") + "</body>" + StringUtils.substringAfter(htmlContent, "</body>");
//        }
//
//        long milliseconds = stopwatch.elapsed(TimeUnit.MILLISECONDS);
//        logger.debug(
//            "==> render homepage used {} milli seconds for cover[{}, htmlContent length[{}] <==",
//            milliseconds, coverRoByCityIdAndPosition.getId(), htmlContent.length());
//        return htmlContent;
//    }

    private String bindSearchDataToTemplate(Document template, String sourceId,
        SearchComponent searchComponent) {
        List<String> groupDataList = newArrayList();
        String placeHolderProcessedHtml = template.outerHtml().replaceAll(getHtmlDataSelector(
            sourceId, searchComponent.getSid(), Search_Placehollder.getPlaceholder()),
            searchComponent.getSearchPlaceHolder());
        template = Jsoup.parseBodyFragment(placeHolderProcessedHtml);
        Elements
            elementToRepeat = findElementByAttributeValue(template, REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, searchComponent.getSid()));
        for (Element element : elementToRepeat) {
            String htmlOfElementToRepeat = element.outerHtml();
            for (Navigation hotKey : searchComponent.getHotKeys()) {

                String processedHtml = htmlOfElementToRepeat.replaceAll(
                    getHtmlDataSelector(sourceId, searchComponent.getSid(),
                        TEXT.getPlaceholder()), hotKey.getText());

                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, searchComponent.getSid(),
                        STYLE_CLASS.getPlaceholder()), hotKey.getStyleClass());

                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, searchComponent.getSid(), LINK.getPlaceholder()),
                    hotKey.getLink());
                groupDataList.add(processedHtml);
            }
            element.after(StringUtils.join(groupDataList, ""));
            element.remove();
        }

        return template.body().html();
    }

    private String bindNavigationDataToTemplate(Document template, String sourceId,
        NavigationComponent navigationComponent) {
        List<String> groupDataList = newArrayList();
        Elements
            elementToRepeat = findElementByAttributeValue(template, REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, navigationComponent.getSid()));
        for (Element element : elementToRepeat) {
            String htmlOfElementToRepeat = element.outerHtml();
            for (Navigation navigation : navigationComponent.getNavigations()) {
                String processedHtml = htmlOfElementToRepeat.replaceAll(getHtmlDataSelector(sourceId,
                    navigationComponent.getSid(), TEXT.getPlaceholder()), navigation.getText());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, navigationComponent.getSid(),
                        STYLE_CLASS.getPlaceholder()), navigation.getStyleClass());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, navigationComponent.getSid(),
                        LINK.getPlaceholder()), navigation.getLink());
                processedHtml = processedHtml
                    .replaceAll(getHtmlDataSelector(sourceId, navigationComponent.getSid(),
                        SECONDARY_TEXT.getPlaceholder()), navigation.getSecondaryText() == null ? "" : navigation.getSecondaryText());
                groupDataList.add(processedHtml);
            }
            element.after(StringUtils.join(groupDataList, ""));
            element.remove();
        }
        return template.body().html();
    }

    private String bindSliderDataToTemplate(Document template, String sourceId,
        SliderComponent sliderComponent) {

        String placeHolderProcessedHtml = template.outerHtml()
            .replaceAll(getHtmlDataSelector(sourceId, IMG_URL.getPlaceholder()), sliderComponent.getImgUrl());
        placeHolderProcessedHtml = placeHolderProcessedHtml.replaceAll(getHtmlDataSelector(sourceId, LINK.getPlaceholder()),
            sliderComponent.getLink());
        template = Jsoup.parseBodyFragment(placeHolderProcessedHtml);
        
        List<String> groupDataList = newArrayList();
        Elements
            slideComponents = findElementByAttributeValue(template, REPEAT_ELEMENT.getPlaceholder(),
            sourceId);
        for (Element element : slideComponents) {
            String htmlOfElementToRepeat = element.outerHtml();
            for (SliderTemplateData sliderTemplateData : sliderComponent.getElements()) {
                Document document = Jsoup.parseBodyFragment(htmlOfElementToRepeat);
                Elements elements =
                    findElementByAttributeValue(document, REPEAT_ELEMENT.getPlaceholder(),
                        getHtmlDataSelector(sourceId, sliderComponent.getSid()));
                for (Element subElement : elements) {
                    String htmlOfSubelementToRepeat = subElement.outerHtml();
                    List<String> subGroupDataList = newArrayList();
                    for (HtmlImageAndLinkable htmlImageAndLinkable : sliderTemplateData
                        .getElements()) {
                        String processedHtml = htmlOfSubelementToRepeat.replaceAll(IMG_URL.getPlaceholder(),htmlImageAndLinkable.getImgUrl());
                        processedHtml = processedHtml.replaceAll(LINK.getPlaceholder(), htmlImageAndLinkable.getLink());
                        subGroupDataList.add(processedHtml);
                    }
                    subElement.after(StringUtils.join(subGroupDataList, ""));
                    subElement.remove();
                }
                groupDataList.add(document.body().html());
            }
            element.after(StringUtils.join(groupDataList, ""));
            element.remove();
        }
        return template.body().html();
    }

    private String bindSeckillDataToTemplate(Document template, String sourceId,
        SecKill secKill) {
        List<String> groupDataList = newArrayList();
        String placeHolderProcessedHtml = template.outerHtml()
            .replaceAll(getHtmlDataSelector(sourceId, TEXT.getPlaceholder()), secKill.getText());
        placeHolderProcessedHtml = placeHolderProcessedHtml
            .replaceAll(getHtmlDataSelector(sourceId, SECONDARY_TEXT.getPlaceholder()),
                secKill.getSecondaryText());
        placeHolderProcessedHtml = placeHolderProcessedHtml.replaceAll(
            getHtmlDataSelector(sourceId, IMG_URL.getPlaceholder()), secKill.getImgUrl());
        placeHolderProcessedHtml = placeHolderProcessedHtml
            .replaceAll(getHtmlDataSelector(sourceId, LINK.getPlaceholder()), secKill.getLink());
        template = Jsoup.parseBodyFragment(placeHolderProcessedHtml);
        Elements
            elementToRepeat = findElementByAttributeValue(template, REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, secKill.getSid()));
        for (Element element : elementToRepeat) {
            String htmlOfElementToRepeat = element.outerHtml();
            for (Showcase showcase : secKill.getShowcases()) {
                String processedHtml = htmlOfElementToRepeat.replaceAll(
                    getHtmlDataSelector(sourceId, secKill.getSid(), TEXT.getPlaceholder()),
                    showcase.getText());
                processedHtml = processedHtml.replaceAll(getHtmlDataSelector(sourceId, secKill.getSid(),
                        IMG_URL.getPlaceholder()),
                    showcase.getImgUrl());
                processedHtml = processedHtml.replaceAll(getHtmlDataSelector(sourceId, secKill.getSid(),
                    LINK.getPlaceholder()), showcase.getLink());
                processedHtml = processedHtml.replaceAll(getHtmlDataSelector(sourceId, secKill.getSid(),
                    Showcase_OriginPrice.getPlaceholder()), String.valueOf(showcase.getOriginPrice()/100D));
                processedHtml = processedHtml.replaceAll(getHtmlDataSelector(sourceId, secKill.getSid(),
                    Showcase_SalePrice.getPlaceholder()), String.valueOf(showcase.getSalePrice()/100D));
                processedHtml = processedHtml.replaceAll(getHtmlDataSelector(sourceId, secKill.getSid(),
                    Showcase_SoldPercent.getPlaceholder()), showcase.getSoldPercent().toString());
                groupDataList.add(processedHtml);
            }
            element.after(StringUtils.join(groupDataList, ""));
            element.remove();
        }

        return template.body().html();
    }

    private String bindCategoryDataToTemplate(Document template, String sourceId,
        CategoryComponent categoryComponent) {
        String placeHolderProcessedHtml = template.outerHtml()
            .replaceAll(getHtmlDataSelector(sourceId, TEXT.getPlaceholder()), categoryComponent.getText());
        placeHolderProcessedHtml = placeHolderProcessedHtml.replaceAll(
            getHtmlDataSelector(sourceId, ORDER_INDEX.getPlaceholder()),
            categoryComponent.getOrderIndex().toString());
        template = Jsoup.parseBodyFragment(placeHolderProcessedHtml);

        Elements
            navigationElement = findElementByAttributeValue(template,
            REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_NAVIGATION));
        for (Element element : navigationElement) {
            String htmlOfNavigation = element.outerHtml();
            List<String> navigationList = newArrayList();
            for (Navigation navigation : categoryComponent.getNavigations()) {
                String processedHtml = htmlOfNavigation.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_NAVIGATION, TEXT.getPlaceholder()),
                    navigation.getText());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_NAVIGATION, LINK.getPlaceholder()),
                    navigation.getLink());
                navigationList.add(processedHtml);
            }
            element.after(StringUtils.join(navigationList, ""));
            element.remove();
        }


        Elements brandElement = findElementByAttributeValue(template,
            REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_BRAND));
        for (Element element : brandElement) {
            String htmlOfBrand = element.outerHtml();
            List<String> brandList = newArrayList();
            for (Showcase showcase : categoryComponent.getBrandShowCases()) {
                String processedHtml = htmlOfBrand.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_BRAND,
                        TEXT.getPlaceholder()), showcase.getText());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_BRAND,
                        LINK.getPlaceholder()), showcase.getLink());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_BRAND,
                        IMG_URL.getPlaceholder()), showcase.getImgUrl());
                brandList.add(processedHtml);
            }
            element.after(StringUtils.join(brandList, ""));
            element.remove();
        }


        Elements productElement = findElementByAttributeValue(template,
            REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_PRODUCT));
        for (Element element : productElement) {
            String htmlOfBrand = element.outerHtml();
            List<String> brandList = newArrayList();
            for (Showcase showcase : categoryComponent.getProductShowCases()) {
                String processedHtml = htmlOfBrand.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_PRODUCT,
                        TEXT.getPlaceholder()), showcase.getText());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_PRODUCT,
                        SECONDARY_TEXT.getPlaceholder()), showcase.getSecondaryText());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_PRODUCT,
                        LINK.getPlaceholder()), showcase.getLink());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_PRODUCT,
                        IMG_URL.getPlaceholder()), showcase.getImgUrl());
                processedHtml = processedHtml.replaceAll(
                    getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_PRODUCT,
                        Showcase_SalePrice.getPlaceholder()), String.valueOf(showcase.getSalePrice()/100D));
                processedHtml = processedHtml.replaceAll(STYLE_CLASS.getPlaceholder(), showcase.getStyleClass());
                brandList.add(processedHtml);
            }
            element.after(StringUtils.join(brandList, ""));
            element.remove();
        }

        placeHolderProcessedHtml = template.outerHtml()
            .replaceAll(getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_TOP, TEXT.getPlaceholder()), categoryComponent.getTopProductShowcases().getText());
        template = Jsoup.parseBodyFragment(placeHolderProcessedHtml);
        Elements topProductElements = findElementByAttributeValue(template,
            REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_TOP));
        for (Element element : topProductElements) {
            String htmlOfBrand = element.outerHtml();
            List<String> brandList = newArrayList();
            for (Showcase showcase : categoryComponent.getTopProductShowcases().getProductShowcases()) {
                String processedHtml = htmlOfBrand.replaceAll(TEXT.getPlaceholder(), showcase.getText());
                processedHtml = processedHtml.replaceAll(SECONDARY_TEXT.getPlaceholder(), showcase.getSecondaryText());
                processedHtml = processedHtml.replaceAll(STYLE_CLASS.getPlaceholder(), showcase.getStyleClass());
                processedHtml = processedHtml.replaceAll(LINK.getPlaceholder(), showcase.getLink());
                processedHtml = processedHtml.replaceAll(IMG_URL.getPlaceholder(), showcase.getImgUrl());
                processedHtml = processedHtml.replaceAll(Showcase_SalePrice.getPlaceholder(), String.valueOf(showcase.getSalePrice()/100D));
                processedHtml = processedHtml.replaceAll(ORDER_INDEX.getPlaceholder(), String.valueOf(showcase.getOrderIndex()));
                brandList.add(processedHtml);
            }
            element.after(StringUtils.join(brandList, ""));
            element.remove();
        }


        Elements footElements = findElementByAttributeValue(template,
            REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_FOOT));
        for (Element element : footElements) {
            String htmlOfBrand = element.outerHtml();
            List<String> brandList = newArrayList();
            for (Showcase showcase : categoryComponent.getFootShowcases()) {
                String processedHtml = htmlOfBrand.replaceAll(IMG_URL.getPlaceholder(), showcase.getImgUrl());
                processedHtml = processedHtml.replaceAll(LINK.getPlaceholder(), showcase.getLink());
                brandList.add(processedHtml);
            }
            element.after(StringUtils.join(brandList, ""));
            element.remove();
        }

        Elements historyElements = findElementByAttributeValue(template,
            REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_HISTORY));
        for (Element element : historyElements) {
            String htmlOfBrand = element.outerHtml();
            List<String> brandList = newArrayList();
            for (Showcase showcase : categoryComponent.getHistoryShowcase().getSecondaryShowcases()) {
                String processedHtml = htmlOfBrand.replaceAll(IMG_URL.getPlaceholder(), showcase.getImgUrl());
                processedHtml = processedHtml.replaceAll(LINK.getPlaceholder(), showcase.getLink());
                brandList.add(processedHtml);
            }
            element.after(StringUtils.join(brandList, ""));
            element.remove();
        }

        placeHolderProcessedHtml = template.outerHtml()
            .replaceAll(getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_HISTORY, IMG_URL.getPlaceholder()), categoryComponent.getHistoryShowcase().getMainShowcase().getImgUrl());
        template = Jsoup.parseBodyFragment(placeHolderProcessedHtml);
        Elements recommendElements = findElementByAttributeValue(template,
            REPEAT_ELEMENT.getPlaceholder(),
            getHtmlDataSelector(sourceId, CategoryComponent.SELECTOR_RECOMMEND));
        for (Element element : recommendElements) {
            String htmlOfBrand = element.outerHtml();
            List<String> brandList = newArrayList();
            for (Showcase showcase : categoryComponent.getRecommendProducts()) {
                String processedHtml = htmlOfBrand.replaceAll(TEXT.getPlaceholder(), showcase.getText());
                processedHtml = processedHtml.replaceAll(SECONDARY_TEXT.getPlaceholder(), showcase.getSecondaryText());
                processedHtml = processedHtml.replaceAll(STYLE_CLASS.getPlaceholder(), showcase.getStyleClass());
                processedHtml = processedHtml.replaceAll(LINK.getPlaceholder(), showcase.getLink());
                processedHtml = processedHtml.replaceAll(IMG_URL.getPlaceholder(), showcase.getImgUrl());
                processedHtml = processedHtml.replaceAll(Showcase_OriginPrice.getPlaceholder(), String.valueOf(showcase.getOriginPrice()/100D));
                processedHtml = processedHtml.replaceAll(Showcase_SalePrice.getPlaceholder(), String.valueOf(showcase.getSalePrice()/100D));
                processedHtml = processedHtml.replaceAll(ORDER_INDEX.getPlaceholder(), String.valueOf(showcase.getOrderIndex()));
                brandList.add(processedHtml);
            }
            element.after(StringUtils.join(brandList, ""));
            element.remove();
        }

        return template.body().html();
    }

    private Elements findElementByAttribute(Element document, String attributeKey) {

        if (document != null) {
            Elements elements =
                document.getElementsByAttribute(attributeKey);
            if(elements != null && !elements.isEmpty()){
                for (Element element : elements) {
                    element.removeAttr(attributeKey);
                }
                return elements;
            }
        }
        return null;
    }

    private Elements findElementByAttributeValue(Document document, String attributeKey,
        String attributeValue) {

        if (document != null) {
            Elements elements =
                document.getElementsByAttributeValue(attributeKey, attributeValue);
            if(elements != null && !elements.isEmpty()){
                for (Element element : elements) {
                    element.removeAttr(attributeKey);
                    element.removeAttr(attributeValue);
                }
                return elements;
            }
        }
        return null;
    }

    private Cover getValidTopZIndexRo(Collection<Cover> coverRos, String appPlatform,
        String appVersion) {
        Cover topRo = null;
        if (isNotEmpty(coverRos)) {
            List<Cover> coversOfMatched = newArrayList();
            AppPlatform appPlatformOfReq = isBlank(appPlatform) ?
                null :
                AppPlatform.valueOf(appPlatform.toUpperCase());
            for (Cover coverRo : coverRos) {
                if (Objects.equals(coverRo.getClientType(), CoverClientType.APP)) {
                    try {
                        AppPlatform appPlatformOfCover = coverRo.getClientPlatform();
                        if (appPlatformOfCover != null) {
                            if (Objects.equals(appPlatformOfCover, AppPlatform.ALL)
                                || Objects.equals(appPlatformOfCover, appPlatformOfReq)) {
                                VersionCompareStrategy versionCompareStrategyValue =
                                    coverRo.getVersionCompareStrategy();
                                if (Objects.equals(versionCompareStrategyValue,
                                    VersionCompareStrategy.ALL)) {
                                    coversOfMatched.add(coverRo);
                                } else if (Objects.equals(versionCompareStrategyValue,
                                    VersionCompareStrategy.REG_EXP)) {
                                    String coverVersion = coverRo.getAppVersion();
                                    if (appVersion.matches(coverVersion)) {
                                        coversOfMatched.add(coverRo);
                                    }
                                } else {
                                    Version appVersionOfReq =
                                        isBlank(appVersion) ? null : new Version(appVersion);
                                    if (appVersionOfReq != null) {
                                        Version appVersionOfCover =
                                            new Version(coverRo.getAppVersion());
                                        int compareResult =
                                            appVersionOfReq.compareTo(appVersionOfCover);
                                        if (ArrayUtils.contains(
                                            versionCompareStrategyValue.getValuesOfCompareResult(),
                                            compareResult)) {
                                            coversOfMatched.add(coverRo);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    coversOfMatched.add(coverRo);
                }
            }
            for (Cover ro : coversOfMatched) {
                if (Objects.equals(ro.getStatus(), CommonStatus.ENABLE))
                    topRo = topRo == null ? ro : topRo.getIndex() < ro.getIndex() ? ro : topRo;
            }
        }
        return topRo;
    }

    public void updateAllCoverTemplates() {

        List<Cover> coverRos = coverRedisDao.getAllCovers();
        if (isNotEmpty(coverRos)) {
            for (Cover coverRo : coverRos) {
                if (coverRo != null && Objects.equals(coverRo.getStatus(), CommonStatus.ENABLE)
                    && isNotBlank(coverRo.getUrl())) {
                    logger.info("Start to update cover:'{}' template from url:{}.", coverRo.getId(),
                        coverRo.getUrl());
                    try {
                        String htmlContent = downloadUrl(coverRo.getUrl());
                        String updateTemplate = renderTemplate("", coverRo, htmlContent);
                        URL_TO_HTML_CONTENT.put(coverRo.getUrl(), updateTemplate);
                        logger.info("Update cover:'{}' template success.", coverRo.getId());
                    } catch (Exception e) {
                        logger.warn("update cover:'{}' template cache failed.", coverRo.getId(), e);
                    }
                }
            }
        }
    }

    public void onListen() {

    }

    private String getHtmlDataSelector(String... placeholders){

        iae.throwIfEmptyOrNull(placeholders, "Placeholder can not be empty.");
        return ArrayUtils.join(placeholders, SELECTOR_JOIN_SYMBOL);
    }

    private String buildPicSrc(String picHost, String picPath, String defaultPic) {

        if (allIsNotNull(picHost, picPath)) {
            if (picHost.endsWith("/") || picPath.startsWith("/")) {
                return picHost + picPath;
            } else {
                return picHost + "/" + picPath;
            }
        } else {
            return defaultPic;
        }
    }

    private String downloadUrl(String htmlUrl) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpGet httpGet = new HttpGet(htmlUrl);
        try {
            String htmlContent =
                UnicodeEntityUtils.toString(httpClient.execute(httpGet).getEntity(), "UTF-8");
            String htmlServerPath = htmlUrl;
            if (!htmlUrl.matches(".+/$")) {
                htmlServerPath = htmlUrl.substring(0, htmlUrl.lastIndexOf("/") + 1);
            }
            String replacement = format("$1=$2%s$3", htmlServerPath);
            String urlFixReg = format("%s(.+://)", htmlServerPath);
            String urlRelationFixedContent =
                htmlContent.replaceAll("(src|href)=(['\"])([^'\"]+)", replacement);
            return urlRelationFixedContent.replaceAll(urlFixReg, "$1");
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    private List<Long> parseLongIds(String ids) {
        String[] idArray = ids.split(",");
        List<Long> parsedIds = newArrayList();
        for (String id : idArray) {
            if (isNotBlank(id)) {
                parsedIds.add(Long.valueOf(id));
            }
        }
        return parsedIds;
    }

}
