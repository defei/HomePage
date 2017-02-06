package org.codelogger.homepage.service;

import org.codelogger.core.utils.JsonUtils;
import org.codelogger.homepage.bean.*;
import org.codelogger.utils.DateUtils;
import org.codelogger.utils.MathUtils;
import org.codelogger.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by defei on 2/5/17.
 */
@Service
public class ProgramService {
    public List<ProgramRo> getProgramsByCoverId(Long id) {

        ArrayList<ProgramRo> programs = newArrayList();

        //搜索
        ProgramRo searchComponentProgram = new ProgramRo();
        searchComponentProgram.setSid("tscp");
        searchComponentProgram.setType(ProgramDataType.SEARCH_COMPONENT);
        SearchComponent hotkeyComponent = new SearchComponent("sc");
        List<Navigation> hotKeys = newArrayList();
        for (int i = 0; i < 5; i++) {
            hotKeys.add(
                newNavigation("热词" + StringUtils.getRandomString(MathUtils.randomInt(1,10)) + i, i % 3 == 0 ? "u-mainRedColor" : "", "#hotkey" + i, i));
        }
        hotkeyComponent.setSearchPlaceHolder("搜索占位符");
        hotkeyComponent.setHotKeys(hotKeys);
        searchComponentProgram.setDataJson(JsonUtils.toJson(hotkeyComponent));
        programs.add(searchComponentProgram);

        //导航
        ProgramRo navigationComponentProgram = new ProgramRo();
        navigationComponentProgram.setSid("tncp");
        navigationComponentProgram.setType(ProgramDataType.NAVIGATION);
        NavigationComponent navigationComponent = new NavigationComponent("nc");
        List<Navigation> navigations = newArrayList();
        for (int i = 0; i < 5; i++) {
            Navigation navigation =
                newNavigation("导航" + StringUtils.getRandomString(MathUtils.randomInt(1,10)) + + i, "", "#navigation" + i, i);
            if (i == 0) {
                navigation.setStyleClass("mm-ybubble");
                navigation.setSecondaryText("Hot");
            } else if (i == 3) {
                navigation.setStyleClass("mm-rbubble");
                navigation.setSecondaryText("New");
            }
            navigations.add(navigation);
        }
        navigationComponent.setNavigations(navigations);
        navigationComponentProgram.setDataJson(JsonUtils.toJson(navigationComponent));
        programs.add(navigationComponentProgram);

        //秒杀
        ProgramRo secKillProgram = new ProgramRo();
        secKillProgram.setSid("tskp");
        secKillProgram.setType(ProgramDataType.SECKILL);
        List<Showcase> showcases = newArrayList();
        for (int i = 0; i < 5; i++) {
            Showcase showcase =
                newShowcase("商品" + StringUtils.getRandomString(MathUtils.randomInt(1,10)) + + i, "http://1919.codelogger.org/images/sec-com01.jpg", "#secKill" + i, i);
            showcases.add(showcase);
        }
        SecKill secKill = new SecKill();
        secKill.setSid("sk");
        int currentHour = DateUtils.getHourOfDay(DateUtils.getCurrentDate());
        secKill.setText((currentHour < 12 ? "早间" : currentHour >= 18 ? "晚间" : "午间") + "疯狂秒杀");
        secKill.setSecondaryText("总有你想不到的低价");
        secKill.setShowcases(showcases);
        secKill.setImgUrl("http://1919.codelogger.org/images/ho-br-sec.jpg");
        secKill.setLink("#secKillMoreLink");
        secKillProgram.setDataJson(JsonUtils.toJson(secKill));
        programs.add(secKillProgram);

        
        /*分类楼层*/
        for (int i = 0; i < 5; i++) {
            ProgramRo categoryProgram = new ProgramRo();
            categoryProgram.setSid("tccp" + i);
            categoryProgram.setType(ProgramDataType.CATEGORY_COMPONENT);
            CategoryComponent categoryComponent = new CategoryComponent();
            String categoryName = "分类" + StringUtils.getRandomString(MathUtils.randomInt(2, 5)) + (i + 1);
            categoryComponent.setOrderIndex(i+1);
            categoryComponent.setText(
                categoryName);
            //导航
            List<Navigation> categoryNavigations = newArrayList();
            for (int j = 0; j < 5; j++) {
                Navigation navigation =
                    newNavigation("导航" + StringUtils.getRandomString(MathUtils.randomInt(1,10)) + j, j % 3 == 0 ? "#e30b20" : "#fff", "#category" + i + "Navigation" + j, j);
                categoryNavigations.add(navigation);
            }
            categoryComponent.setNavigations(categoryNavigations);

            //品牌
            List<Showcase> brandShowcases = newArrayList();
            for (int j = 0; j < 8; j++) {
                Showcase showcase =
                    newShowcase("品牌" + StringUtils.getRandomString(MathUtils.randomInt(1,3)) + j, j % 2 ==0 ? "http://1919.codelogger.org/images/home-wineadsM.png" : "http://1919.codelogger.org/images/home-wineadsM02.png", "#brandShowcases" + j, j);
                brandShowcases.add(showcase);
            }
            categoryComponent.setBrandShowCases(brandShowcases);

            //商品
            List<Showcase> productShowcases = newArrayList();
            for (int j = 0; j < 8; j++) {
                Showcase showcase =
                    newShowcase("【店铺" + StringUtils.getRandomString(MathUtils.randomInt(4,10)) + j + "】", "商品" + StringUtils.getRandomString(MathUtils.randomInt(5,20)) + j, "http://1919.codelogger.org/images/home-goodB.png", "#productShowcases" + j, j);
                showcase.setStyleClass(MathUtils.randomInt(10) > 8 ? "ml-time19" : MathUtils.randomInt(10) > 5 ? "ml-time24" : "");
                productShowcases.add(showcase);
            }
            categoryComponent.setProductShowCases(productShowcases);

            //top
            List<Showcase> productShowcasesOfTop = newArrayList();
            for (int j = 0; j < 10; j++) {
                Showcase showcase =
                    newShowcase("【店铺" + StringUtils.getRandomString(MathUtils.randomInt(4,10)) + j + "】", "商品" + StringUtils.getRandomString(MathUtils.randomInt(5,20)) + j, "http://1919.codelogger.org/images/home-goodS.png", "#topProduct" + j, j+1);
                showcase.setStyleClass(j < 2 ? "r-toplist-act" : "");
                productShowcasesOfTop.add(showcase);
            }
            TopProductShowcases topProductShowcases = new TopProductShowcases();
            topProductShowcases.setText(categoryName + " TOP10");
            topProductShowcases.setProductShowcases(productShowcasesOfTop);
            categoryComponent.setTopProductShowcases(topProductShowcases);

            //foot
            List<Showcase> footShowcases = newArrayList();
            for (int j = 0; j < 4; j++) {
                Showcase showcase =
                    newShowcase("【店铺" + StringUtils.getRandomString(MathUtils.randomInt(4,10)) + j + "】", "商品" + StringUtils.getRandomString(MathUtils.randomInt(5,20)) + j, "http://1919.codelogger.org/images/home-wineadsS.png", "#footShowcases" + j, j);
                showcase.setStyleClass(MathUtils.randomInt(10) > 8 ? "ml-time19" : MathUtils.randomInt(10) > 5 ? "ml-time24" : "");
                footShowcases.add(showcase);
            }
            categoryComponent.setFootShowcases(footShowcases);

            //history
            List<Showcase> secondaryShowcases = newArrayList();
            for (int j = 0; j < 3; j++) {
                Showcase showcase =
                    newShowcase("【店铺" + StringUtils.getRandomString(MathUtils.randomInt(4,10)) + j + "】", "商品" + StringUtils.getRandomString(MathUtils.randomInt(5,20)) + j, "http://1919.codelogger.org/images/home-wineadsB.png", "#hisgoryShowCases" + j, j);
                showcase.setStyleClass(MathUtils.randomInt(10) > 8 ? "ml-time19" : MathUtils.randomInt(10) > 5 ? "ml-time24" : "");
                secondaryShowcases.add(showcase);
            }
            HistoryShowcases historyShowcases = new HistoryShowcases();
            historyShowcases.setMainShowcase(newShowcase("【店铺" + StringUtils.getRandomString(MathUtils.randomInt(4,10)) + 0 + "】", "商品" + StringUtils.getRandomString(MathUtils.randomInt(5,20)) + 0, "http://1919.codelogger.org/images/home-wineadsL.png", "#hisgoryShowCases" + 0, 0));
            historyShowcases.setSecondaryShowcases(secondaryShowcases);
            categoryComponent.setHistoryShowcase(historyShowcases);

            //recommend
            List<Showcase> recommendShowcases = newArrayList();
            for (int j = 0; j < 12; j++) {
                Showcase showcase =
                    newShowcase("【店铺" + StringUtils.getRandomString(MathUtils.randomInt(4,10)) + j + "】", "商品" + StringUtils.getRandomString(MathUtils.randomInt(5,20)) + j, "http://1919.codelogger.org/images/home-goodM.png", "#recommendShowcase" + j, j);
                showcase.setStyleClass(MathUtils.randomInt(10) > 8 ? "ml-time19" : MathUtils.randomInt(10) > 5 ? "ml-time24" : "");
                recommendShowcases.add(showcase);
            }
            categoryComponent.setRecommendProducts(recommendShowcases);

            categoryProgram.setDataJson(JsonUtils.toJson(categoryComponent));
            programs.add(categoryProgram);
        }

        return programs;
    }

    private Navigation newNavigation(String text, String styleClass, String link , Integer orderIndex){
        Navigation navigation = new Navigation();
        navigation.setText(text);
        navigation.setStyleClass(styleClass);
        navigation.setLink(link);
        navigation.setOrderIndex(orderIndex);
        return navigation;
    }

    private Showcase newShowcase(String text, String imageUrl, String link, Integer orderIndex){
        return newShowcase(text, null, imageUrl, link, orderIndex);
    }

    private Showcase newShowcase(String text, String secondaryText, String imageUrl, String link, Integer orderIndex){
        Showcase showcase = new Showcase();
        showcase.setText(text);
        showcase.setSecondaryText(secondaryText);
        showcase.setImgUrl(imageUrl);
        showcase.setLink(link);
        showcase.setOriginPrice(MathUtils.randomInt(10000,99999));
        showcase.setSalePrice((int)(showcase.getOriginPrice() * MathUtils.randomDouble()));
        showcase.setSoldPercent(MathUtils.randomInt(100));
        showcase.setOrderIndex(orderIndex);
        return showcase;
    }
}