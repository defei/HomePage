package org.codelogger.homepage.service;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codelogger.homepage.bean.*;
import org.codelogger.homepage.dao.CoverRedisDao;
import org.codelogger.homepage.model.Cover;
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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.codelogger.homepage.bean.ProgramDataPlaceholder.*;
import static org.codelogger.utils.CollectionUtils.isNotEmpty;

/**
 * Created by defei on 5/13/15.
 */
@Service("service.cover")
public class CoverService {

    private static final Logger logger = LoggerFactory.getLogger(CoverService.class);

    @Autowired private CoverRedisDao coverRedisDao;

    @Autowired private CoverDataProvider coverDataProvider;

    private static final Map<String, String> URL_TO_HTML_CONTENT = newHashMap();

    @PostConstruct
    public void postConstruct() {

        updateAllCoverTemplates();

        onListen();
    }

    public Cover getCoverByCityIdAndPosition(CoverReqVo coverReqVo, CoverClientType coverClientType) {

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

    public String getHomePageHtmlContentByCityIdAndShopTypeId(CoverReqVo coverReqVo, CoverClientType coverClientType) {
        Cover coverRoByCityIdAndPosition =
            getCoverByCityIdAndPosition(coverReqVo, coverClientType);
        return getHomePageHtmlContentByCover(coverRoByCityIdAndPosition, false);
    }

    public String getBodyHtmlContentByCityIdAndPosition(CoverReqVo coverReqVo, CoverClientType coverClientType) {
        String homePageHtmlContentByCityIdAndPosition =
            getHomePageHtmlContentByCityIdAndShopTypeId(coverReqVo, coverClientType);
        if (logger.isDebugEnabled()) {
            logger
                .debug("get data with length:{}.", homePageHtmlContentByCityIdAndPosition.length());
        }
        return isNotBlank(homePageHtmlContentByCityIdAndPosition) ?
            Jsoup.parse(homePageHtmlContentByCityIdAndPosition).body().html() :
            homePageHtmlContentByCityIdAndPosition;
    }

    public String getHomePageHtmlContentByCover(Cover coverRo, Boolean disableCache) {
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
                    return renderTemplate(coverRo, htmlContent);
                }
            } catch (Exception e) {
                logger.warn("can not render homepage for cover[{}].", coverRo.getId(), e);
                if (!disableCache)
                    URL_TO_HTML_CONTENT.remove(coverRo.getUrl());
            }
        }
        logger.debug("return empty cover home page html by cover[]", coverRo == null ? null : coverRo.getId());
        return "";
    }

    private String renderTemplate(Cover coverRoByCityIdAndPosition,
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
        processedHtml = replaceAllIfValueIsNull(processedHtml, SALE_PRICE.getPlaceholder(), convertToYuan(
            dataSource.getSalePrice()));
        processedHtml = replaceAllIfValueIsNull(processedHtml, ORIGIN_PRICE.getPlaceholder(), convertToYuan(
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
                        String updateTemplate = renderTemplate(coverRo, htmlContent);
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
