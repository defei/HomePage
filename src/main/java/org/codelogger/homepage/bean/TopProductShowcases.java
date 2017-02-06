package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/7/17.
 */
public class TopProductShowcases extends CommonHtmlTemplateData {

    private List<Showcase> productShowcases;

    /**
     * {@linkplain TopProductShowcases#productShowcases}
     */
    public List<Showcase> getProductShowcases() {
        return productShowcases;
    }

    /**
     * {@linkplain TopProductShowcases#productShowcases}
     */
    public void setProductShowcases(List<Showcase> productShowcases) {
        this.productShowcases = productShowcases;
    }
}
