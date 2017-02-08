package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/7/17.
 */
public class SliderTemplateData extends CommonHtmlTemplateData {

    private List<CommonHtmlTemplateData> elements;

    /**
     * {@linkplain SliderTemplateData#elements}
     */
    public List<CommonHtmlTemplateData> getElements() {
        return elements;
    }

    /**
     * {@linkplain SliderTemplateData#elements}
     */
    public void setElements(List<CommonHtmlTemplateData> elements) {
        this.elements = elements;
    }
}
