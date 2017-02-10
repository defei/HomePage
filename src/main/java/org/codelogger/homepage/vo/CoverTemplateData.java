package org.codelogger.homepage.vo;

import org.codelogger.homepage.bean.CommonHtmlTemplateData;
import org.codelogger.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by defei on 2/8/17.
 */
public class CoverTemplateData extends CommonHtmlTemplateData{


    public CoverTemplateData getChild(String key){

        return children.get(key);
    }

    public void add(String key, CoverTemplateData coverTemplateData) {

        children.put(key, coverTemplateData);
    }

    /**
     * {@linkplain CoverTemplateData#elements}
     */
    public List<CoverTemplateData> getElements() {
        return elements;
    }

    /**
     * {@linkplain CoverTemplateData#elements}
     */
    public void setElements(List<CoverTemplateData> elements) {
        this.elements = elements;
    }

    public CoverTemplateData getChild(List<String> dataSelectors, Integer... level) {
        if(CollectionUtils.isEmpty(dataSelectors)){
            return null;
        }
        try {
            CoverTemplateData child = this;
            for (String dataSelector : dataSelectors) {
                child = child.getChild(dataSelector);
            }
            return child;
        } catch (Exception e) {
            logger.debug("Child not found by select:[{}]", CollectionUtils.join(dataSelectors, "."));
            return null;
        }
    }

    public CoverTemplateData getElement(List<String> dataSelectors, Integer index) {
        CoverTemplateData child = getChild(dataSelectors);
        if(child == null){
            return null;
        } else {
            List<CoverTemplateData> elements = child.getElements();
            return CollectionUtils.getCount(elements) > index ? elements.get(index) : null;
        }
    }

    public Boolean hasChildren(){
        return !children.isEmpty();
    }

    private List<CoverTemplateData> elements = newArrayList();

    private Map<String, CoverTemplateData> children = newHashMap();

    private static final Logger logger = LoggerFactory.getLogger(CoverTemplateData.class);
}
