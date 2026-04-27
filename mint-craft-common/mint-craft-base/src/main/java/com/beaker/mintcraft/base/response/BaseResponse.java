package com.beaker.mintcraft.base.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author beaker
 * @Date 2026/4/26 20:56
 * @Description 通用出参
 */
@Getter
@Setter
@ToString
public class BaseResponse implements Serializable {

    public static final long serialVersionUID = 1L;

    private Boolean success;

    private String responseCode;

    private String responseMessage;
}
