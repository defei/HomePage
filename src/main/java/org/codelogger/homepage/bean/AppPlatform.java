package org.codelogger.homepage.bean;

/**
 * app平台
 */
public enum AppPlatform {

    ALL("全部", 0),
    IOS("iOS", 1),
    ANDROID("Android", 2);

    private String description;

    private Integer value;

    AppPlatform(String description, Integer value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public Integer getValue() {
        return value;
    }
}
