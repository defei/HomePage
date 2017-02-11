package org.codelogger.homepage.service;

import org.codelogger.homepage.helper.BeanDiscoverer;
import org.codelogger.homepage.helper.MethodExecutable;
import org.codelogger.homepage.helper.MethodExecutorMethodParam;
import org.codelogger.homepage.vo.CoverTemplateData;
import org.codelogger.utils.MathUtils;
import org.codelogger.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.codelogger.utils.MathUtils.randomInt;
import static org.codelogger.utils.StringUtils.getRandomString;

/**
 * Created by defei on 2/11/17.
 */
@Service
public class ProductDetailListService implements MethodExecutable {

    @PostConstruct
    public void setup(){

        BeanDiscoverer.put(StringUtils.firstCharToLowerCase(getClass().getSimpleName()), this);
    }

    public CoverTemplateData execute(MethodExecutorMethodParam param){

        List<CoverTemplateData> productShowcasesOfTop = newArrayList();
        List<? extends Serializable> dataIds = param.getDataIds();
        Integer index = 0;
        for (Serializable dataId : dataIds) {
            CoverTemplateData showcase =
                newShowcase("【店铺" + param.getIdentity() + getRandomString(randomInt(2,10)) + dataId + "】", "商品" + param.getIdentity() + getRandomString(randomInt(4,16)) + dataId, randomInt() % 2 == 0 ? "http://1919.codelogger.org/images/home-goodB.png" : "http://1919.codelogger.org/images/home-goodM.png", "#" + param.getIdentity() + "_topProduct" + dataId, index + 1);
            showcase.setStyleClass(MathUtils.randomInt(10) > 8 ? "ml-time19" : MathUtils.randomInt(10) > 5 ? "ml-time24" : "");
            productShowcasesOfTop.add(showcase);
        }
        CoverTemplateData productsOfTopCategoryComponent = new CoverTemplateData();
        productsOfTopCategoryComponent.setElements(productShowcasesOfTop);
        return productsOfTopCategoryComponent;
    }

    private CoverTemplateData newShowcase(String text, String secondaryText, String imageUrl, String link, Integer orderIndex){
        CoverTemplateData showcase = new CoverTemplateData();
        showcase.setText(text);
        showcase.setSecondaryText(secondaryText);
        showcase.setImgUrl(imageUrl);
        showcase.setLink(link);
        showcase.setOriginPrice(randomInt(10000, 99999));
        showcase.setSalePrice((int)(showcase.getOriginPrice() * MathUtils.randomDouble()));
        showcase.setSoldPercent(randomInt(100));
        showcase.setOrderIndex(orderIndex);
        return showcase;
    }

}
