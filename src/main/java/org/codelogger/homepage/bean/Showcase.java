package org.codelogger.homepage.bean;

/**
 * Created by defei on 2/5/17.
 */
public class Showcase extends CommonHtmlTemplateData {

    private Integer originPrice;

    private Integer salePrice;

    private Integer soldPercent;

    /**
     * {@linkplain Showcase#originPrice}
     */
    public Integer getOriginPrice() {
        return originPrice;
    }

    /**
     * {@linkplain Showcase#originPrice}
     */
    public void setOriginPrice(Integer originPrice) {
        this.originPrice = originPrice;
    }

    /**
     * {@linkplain Showcase#salePrice}
     */
    public Integer getSalePrice() {
        return salePrice;
    }

    /**
     * {@linkplain Showcase#salePrice}
     */
    public void setSalePrice(Integer salePrice) {
        this.salePrice = salePrice;
    }

    /**
     * {@linkplain Showcase#soldPercent}
     */
    public Integer getSoldPercent() {
        return soldPercent;
    }

    /**
     * {@linkplain Showcase#soldPercent}
     */
    public void setSoldPercent(Integer soldPercent) {
        this.soldPercent = soldPercent;
    }

}
