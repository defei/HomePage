package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/7/17.
 */
public class SliderComponent extends CommonHtmlTemplateData {

    private List<SliderTemplateData> elements;

    /**
     * {@linkplain SliderComponent#elements}
     */
    public List<SliderTemplateData> getElements() {
        return elements;
    }

    /**
     * {@linkplain SliderComponent#elements}
     */
    public void setElements(List<SliderTemplateData> elements) {
        this.elements = elements;
    }
}
