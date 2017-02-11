package org.codelogger.homepage.bean;

/**
 * 区域首页数据填充占位符
 */
public enum ProgramDataPlaceholder {

    /*全局约定*/
    DATA_SOURCE("数据来源,请设置为attribute", "hp-source"),
    DATA_FIELD("子数据来源,请设置为attribute", "hp-field"),
    REPEAT_ELEMENT("需要重复的元素,请设置为attribute", "hp-repeat"),

    /*公共*/
    TEXT("text字段", "#text#"),
    SECONDARY_TEXT("tsecondaryText字段", "#secondaryText#"),
    IMG_URL("imgUrl字段", "#imgUrl#"),
    STYLE_CLASS("styleClass字段", "#styleClass#"),
    LINK("link字段", "#link#"),
    ORDER_INDEX("orderIndex字段", "#orderIndex#"),
    ORIGIN_PRICE("originPrice字段", "#originPrice#"),
    SALE_PRICE("salePrice字段", "#salePrice#"),
    SOLD_PERCENT("soldPercent字段", "#soldPercent#"),



    A("", "");


    private String description;

    private String placeholder;

    ProgramDataPlaceholder(String description, String placeholder) {
        this.description = description;
        this.placeholder = placeholder;
    }

    public String getDescription() {
        return description;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
