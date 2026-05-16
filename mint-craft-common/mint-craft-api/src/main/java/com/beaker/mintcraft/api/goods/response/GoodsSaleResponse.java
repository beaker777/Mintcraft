package com.beaker.mintcraft.api.goods.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

import static com.beaker.mintcraft.base.exception.biz.BizErrorCode.DUPLICATED;

/**
 * @Author beaker
 * @Date 2026/5/16 13:29
 * @Description 商品销售响应类
 */
@Data
public class GoodsSaleResponse extends BaseResponse {

    /**
     * 持有藏品id
     */
    private Long heldCollectionId;

    public static class GoodsResponseBuilder {
        private Long heldCollectionId;

        public GoodsSaleResponse.GoodsResponseBuilder heldCollectionId(Long heldCollectionId) {
            this.heldCollectionId = heldCollectionId;
            return this;
        }

        public GoodsSaleResponse buildSuccess() {
            GoodsSaleResponse goodsSaleResponse = new GoodsSaleResponse();
            goodsSaleResponse.setHeldCollectionId(heldCollectionId);
            goodsSaleResponse.setSuccess(true);

            return goodsSaleResponse;
        }

        public GoodsSaleResponse buildDuplicated() {
            GoodsSaleResponse goodsSaleResponse = new GoodsSaleResponse();
            goodsSaleResponse.setHeldCollectionId(heldCollectionId);
            goodsSaleResponse.setSuccess(true);

            goodsSaleResponse.setResponseCode(DUPLICATED.getCode());
            goodsSaleResponse.setResponseMessage(DUPLICATED.getMessage());

            return goodsSaleResponse;
        }

        public GoodsSaleResponse buildFail(String code, String msg) {
            GoodsSaleResponse goodsSaleResponse = new GoodsSaleResponse();
            goodsSaleResponse.setHeldCollectionId(heldCollectionId);
            goodsSaleResponse.setSuccess(false);

            goodsSaleResponse.setResponseCode(code);
            goodsSaleResponse.setResponseMessage(msg);

            return goodsSaleResponse;
        }
    }
}
