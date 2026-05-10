package com.beaker.mintcraft.api.goods.constant;

/**
 * @Author beaker
 * @Date 2026/5/10 21:06
 * @Description 商品种类
 */
public enum GoodsType {

    /** 藏品 */
    COLLECTION("藏品"),

    /**
     * 盲盒
     */
    BLIND_BOX("盲盒");


    private String value;

    GoodsType(String value) {
        this.value = value;
    }
}
