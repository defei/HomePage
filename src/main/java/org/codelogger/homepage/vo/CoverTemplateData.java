package org.codelogger.homepage.vo;

import org.codelogger.homepage.bean.CommonHtmlTemplateData;
import org.codelogger.homepage.helper.MethodExecutor;
import org.codelogger.homepage.helper.MethodExecutorMethodParam;
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
        if(elementsLoadType == ElementsLoadType.DYNAMIC){
            return new MethodExecutor(methodExecutor, methodExecutorMethodParam).execute()
                    .getElements();
        }
        return elements;
    }

    /**
     * {@linkplain CoverTemplateData#elements}
     */
    public void setElements(List<CoverTemplateData> elements) {
        this.elements = elements;
    }

    public CoverTemplateData getChild(List<String> dataSelectors) {
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

    /**
     * {@linkplain CoverTemplateData#children}
     */
    public Map<String, CoverTemplateData> getChildren() {
        return children;
    }

    /**
     * {@linkplain CoverTemplateData#children}
     */
    public void setChildren(Map<String, CoverTemplateData> children) {
        this.children = children;
    }


    public Boolean hasChildren(){
        return !children.isEmpty();
    }

    /**
     * {@linkplain CoverTemplateData#methodExecutor}
     */
    public String getMethodExecutor() {
        return methodExecutor;
    }

    /**
     * {@linkplain CoverTemplateData#methodExecutor}
     */
    public void setMethodExecutor(String methodExecutor) {
        elementsLoadType = ElementsLoadType.DYNAMIC;
        this.methodExecutor = methodExecutor;
    }

    /**
     * {@linkplain CoverTemplateData#methodExecutorMethodParam}
     */
    public MethodExecutorMethodParam getMethodExecutorMethodParam() {
        return methodExecutorMethodParam;
    }

    /**
     * {@linkplain CoverTemplateData#methodExecutorMethodParam}
     */
    public void setMethodExecutorMethodParam(MethodExecutorMethodParam methodExecutorMethodParam) {
        this.methodExecutorMethodParam = methodExecutorMethodParam;
    }

    /**
     * {@linkplain CoverTemplateData#elementsLoadType}
     */
    public ElementsLoadType getElementsLoadType() {
        return elementsLoadType;
    }

    /**
     * {@linkplain CoverTemplateData#elementsLoadType}
     */
    public void setElementsLoadType(ElementsLoadType elementsLoadType) {
        this.elementsLoadType = elementsLoadType;
    }

    private MethodExecutorMethodParam methodExecutorMethodParam;

    private String methodExecutor;

    private ElementsLoadType elementsLoadType = ElementsLoadType.STATIC;

    private List<CoverTemplateData> elements = newArrayList();

    private Map<String, CoverTemplateData> children = newHashMap();

    private static final Logger logger = LoggerFactory.getLogger(CoverTemplateData.class);

}
