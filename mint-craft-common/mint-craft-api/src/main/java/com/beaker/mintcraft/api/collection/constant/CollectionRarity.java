package com.beaker.mintcraft.api.collection.constant;

/**
 * @Author beaker
 * @Date 2026/5/11 18:21
 * @Description 藏品稀有度
 */
public enum CollectionRarity {

    /**
     * 普通
     */
    COMMON("普通"),

    /**
     * 稀有
     */
    RARE("稀有"),

    /**
     * 史诗
     */
    EPIC("史诗"),

    /**
     * 传说
     */
    LEGENDARY("传说"),

    /**
     * 独特
     */
    UNIQUE("独特"),

    /**
     * 神话
     */
    MYTHICAL("神话");

    private String value;

    CollectionRarity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
