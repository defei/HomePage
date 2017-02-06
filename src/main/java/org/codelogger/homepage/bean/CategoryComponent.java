package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/6/17.
 */
public class CategoryComponent implements HtmlTemplateSelectable {

    public static final String SELECTOR_NAVIGATION = "na";

    public static final String SELECTOR_BRAND = "br";

    public static final String SELECTOR_PRODUCT = "pr";

    public static final String SELECTOR_TOP = "top";

    public static final String SELECTOR_FOOT = "foot";

    public static final String SELECTOR_HISTORY = "history";

    public static final String SELECTOR_RECOMMEND = "recommend";

    private String sid;

    private String text;

    private List<Navigation> navigations;

    private List<Showcase> brandShowCases;

    private List<Showcase> productShowCases;

    private TopProductShowcases topProductShowcases;

    private List<Showcase> footShowcases;

    private HistoryShowcases historyShowcase;

    private List<Showcase> recommendProducts;

    private Integer orderIndex;

    private CommonStatus status;

    /**
     * {@linkplain CategoryComponent#text}
     */
    public String getText() {
        return text;
    }

    /**
     * {@linkplain CategoryComponent#text}
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * {@linkplain CategoryComponent#navigations}
     */
    public List<Navigation> getNavigations() {
        return navigations;
    }

    /**
     * {@linkplain CategoryComponent#navigations}
     */
    public void setNavigations(List<Navigation> navigations) {
        this.navigations = navigations;
    }

    /**
     * {@linkplain CategoryComponent#brandShowCases}
     */
    public List<Showcase> getBrandShowCases() {
        return brandShowCases;
    }

    /**
     * {@linkplain CategoryComponent#brandShowCases}
     */
    public void setBrandShowCases(List<Showcase> brandShowCases) {
        this.brandShowCases = brandShowCases;
    }

    /**
     * {@linkplain CategoryComponent#productShowCases}
     */
    public List<Showcase> getProductShowCases() {
        return productShowCases;
    }

    /**
     * {@linkplain CategoryComponent#productShowCases}
     */
    public void setProductShowCases(List<Showcase> productShowCases) {
        this.productShowCases = productShowCases;
    }

    /**
     * {@linkplain CategoryComponent#topProductShowcases}
     */
    public TopProductShowcases getTopProductShowcases() {
        return topProductShowcases;
    }

    /**
     * {@linkplain CategoryComponent#topProductShowcases}
     */
    public void setTopProductShowcases(TopProductShowcases topProductShowcases) {
        this.topProductShowcases = topProductShowcases;
    }

    /**
     * {@linkplain CategoryComponent#footShowcases}
     */
    public List<Showcase> getFootShowcases() {
        return footShowcases;
    }

    /**
     * {@linkplain CategoryComponent#footShowcases}
     */
    public void setFootShowcases(List<Showcase> footShowcases) {
        this.footShowcases = footShowcases;
    }

    /**
     * {@linkplain CategoryComponent#historyShowcase}
     */
    public HistoryShowcases getHistoryShowcase() {
        return historyShowcase;
    }

    /**
     * {@linkplain CategoryComponent#historyShowcase}
     */
    public void setHistoryShowcase(HistoryShowcases historyShowcase) {
        this.historyShowcase = historyShowcase;
    }

    /**
     * {@linkplain CategoryComponent#recommendProducts}
     */
    public List<Showcase> getRecommendProducts() {
        return recommendProducts;
    }

    /**
     * {@linkplain CategoryComponent#recommendProducts}
     */
    public void setRecommendProducts(List<Showcase> recommendProducts) {
        this.recommendProducts = recommendProducts;
    }

    /**
     * {@linkplain CategoryComponent#orderIndex}
     */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /**
     * {@linkplain CategoryComponent#orderIndex}
     */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * {@linkplain CategoryComponent#status}
     */
    public CommonStatus getStatus() {
        return status;
    }

    /**
     * {@linkplain CategoryComponent#status}
     */
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getSid() {
        return sid;
    }

    /**
     * {@linkplain CategoryComponent#sid}
     */
    public void setSid(String sid) {
        this.sid = sid;
    }
}
