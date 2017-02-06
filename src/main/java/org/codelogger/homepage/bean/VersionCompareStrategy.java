package org.codelogger.homepage.bean;

import java.util.Objects;

public enum VersionCompareStrategy {

    ALL("所有", 0, new Integer[] {-1, 0, 1}),
    LESS_THAN("小于", 1, new Integer[] {-1}),
    LESS_OR_EQUAL("小于等于", 2, new Integer[] {-1, 0}),
    EQUAL("等于", 3, new Integer[] {0}),
    GREATER_OR_EQUAL("大于等于", 4, new Integer[] {0, 1}),
    GREATER_THAN("大于", 5, new Integer[] {1}),
    REG_EXP("正则表达式", 6, new Integer[] {});

    private String description;

    private Integer value;

    private Integer[] valuesOfCompareResult;

    VersionCompareStrategy(String description, Integer value, Integer[] valuesOfCompareResult) {
        this.description = description;
        this.value = value;
        this.valuesOfCompareResult = valuesOfCompareResult;
    }

    public static VersionCompareStrategy getVersionCompareStrategyByValue(Integer value) {
        for (VersionCompareStrategy versionCompareStrategy : VersionCompareStrategy.values()) {
            if (Objects.equals(versionCompareStrategy.getValue(), value))
                return versionCompareStrategy;
        }
        throw new IllegalArgumentException(
            "There were not have any element's value matched given value.");
    }

    public String getDescription() {
        return description;
    }

    public Integer getValue() {
        return value;
    }

    public Integer[] getValuesOfCompareResult() {
        return valuesOfCompareResult;
    }
}
