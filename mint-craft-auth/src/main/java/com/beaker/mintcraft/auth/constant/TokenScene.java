package com.beaker.mintcraft.auth.constant;

import java.util.Arrays;

/**
 * @Author beaker
 * @Date 2026/5/18 16:16
 * @Description token 发放场景
 */
public enum TokenScene {

    /**
     * 下单-藏品
     */
    BUY_COLLECTION("buy"),

    /**
     * 下单-盲盒
     */
    BUY_BLIND_BOX("buyBb");

    /**
     * 场景的值
     */
    private String scene;

    TokenScene(String scene) {
        this.scene = scene;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public static TokenScene getByScene(String scene) {
        return Arrays
                .stream(TokenScene.values())
                .filter(tokenScene -> tokenScene.getScene().equals(scene))
                .findFirst()
                .orElse(null);
    }
}
