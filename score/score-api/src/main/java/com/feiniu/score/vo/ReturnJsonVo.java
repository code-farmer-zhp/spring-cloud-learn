package com.feiniu.score.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReturnJsonVo {


    private String memGuid;
    private String rgSeq;
    private String rgNo;
    private String ogSeq;
    private String ogNo;

    private List<ReturnDetail> mallList = new ArrayList<>();
    private List<ReturnDetail> selfList = new ArrayList<>();

    public static ReturnJsonVo convertJson(JSONObject obj) {

        ReturnJsonVo vo = new ReturnJsonVo();
        String memGuid = obj.getString("memGuid");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        vo.setMemGuid(memGuid);

        JSONObject order = obj.getJSONObject("return");
        if (order == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "return 不能为空");
        }
        String rgSeq = order.getString("rgSeq");
        if (StringUtils.isEmpty(rgSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "rgSeq 不能为空");
        }
        vo.setRgSeq(rgSeq);

        String rgNo = order.getString("rgNo");
        if (StringUtils.isEmpty(rgNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "rgNo 不能为空");
        }
        vo.setRgNo(rgNo);

        String ogSeq = order.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        vo.setOgSeq(ogSeq);

        String ogNo = order.getString("ogNo");
        if (StringUtils.isEmpty("ogNo")) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogNo 不能为空");
        }
        vo.setOgNo(ogNo);

        JSONArray mall = order.getJSONArray("mall");
        JSONArray self = order.getJSONArray("self");

        List<ReturnDetail> mallList = new ArrayList<>();
        if (mall != null && mall.size() > 0) {
            for (Object aMall : mall) {
                ReturnDetail d = new ReturnDetail();
                JSONObject o = (JSONObject) aMall;
                String ogsSeq = o.getString("ogsSeq");
                if (StringUtils.isEmpty(ogsSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogsSeq 不能为空");
                }
                d.setOgsSeq(ogsSeq);

                String sellerNo = o.getString("sellerNo");
                if (StringUtils.isEmpty(sellerNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "sellerNo 不能为空");
                }
                d.setSellerNo(sellerNo);

                int quantity = o.getIntValue("quantity");
                if (quantity == 0) {
                    continue;
                }
                d.setQuantity(quantity);

                String itNo = o.getString("itNo");
                if (StringUtils.isEmpty(itNo)) {
                    itNo = o.getString("skuSeq");
                    if (StringUtils.isEmpty(itNo)) {
                        throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "itNo或skuSeq 不能为空");
                    }
                }
                d.setItNo(itNo);

                String olSeq = o.getString("olSeq");
                if (StringUtils.isEmpty(olSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "olSeq 不能为空");
                }
                d.setOlSeq(olSeq);

                String rlSeq = o.getString("rlSeq");
                if (StringUtils.isEmpty(rlSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "rlSeq 不能为空");
                }
                d.setRlSeq(rlSeq);

                String packageNo = o.getString("packageNo");
                if (StringUtils.isEmpty(packageNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "packageNo 不能为空");
                }
                d.setPackageNo(packageNo);

                String kind = o.getString("kind");
                if (StringUtils.isEmpty(kind)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "kind 不能为空");
                }
                d.setKind(kind);

                BigDecimal card = o.getBigDecimal("card");
                if (card == null) {
                    card = BigDecimal.ZERO;
                }
                d.setCard(card);
                BigDecimal returnMoney = o.getBigDecimal("returnMoney");
                if (returnMoney == null) {
                    returnMoney = BigDecimal.ZERO;
                }
                d.setReturnMoney(returnMoney);

                BigDecimal price = o.getBigDecimal("price");
                if (price == null) {
                    price = BigDecimal.ZERO;
                }
                d.setPrice(price);

                BigDecimal refundablePrice = o.getBigDecimal("refundablePrice");
                if (refundablePrice == null) {
                    refundablePrice = BigDecimal.ZERO;
                }
                d.setRefundablePrice(refundablePrice);
                mallList.add(d);
            }

        }
        vo.setMallList(mallList);

        List<ReturnDetail> selfList = new ArrayList<>();
        if (self != null) {
            for (Object aSelf : self) {

                ReturnDetail d = new ReturnDetail();
                JSONObject o = (JSONObject) aSelf;
                int quantity = o.getIntValue("quantity");
                if (quantity == 0) {
                    continue;
                }
                d.setQuantity(quantity);

                String itNo = o.getString("skuSeq");
                if (StringUtils.isEmpty(itNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "skuSeq 不能为空");
                }
                d.setItNo(itNo);

                String olSeq = o.getString("olSeq");
                if (StringUtils.isEmpty(olSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "olSeq 不能为空");
                }
                d.setOlSeq(olSeq);

                String rlSeq = o.getString("rlSeq");
                if (StringUtils.isEmpty(rlSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "rlSeq 不能为空");
                }
                d.setRlSeq(rlSeq);

                String packageNo = o.getString("packageNo");
                if (StringUtils.isEmpty(packageNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "packageNo 不能为空");
                }
                d.setPackageNo(packageNo);

                String kind = o.getString("kind");
                if (StringUtils.isEmpty(kind)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "kind 不能为空");
                }
                d.setKind(kind);
                BigDecimal card = o.getBigDecimal("card");
                if (card == null) {
                    card = BigDecimal.ZERO;
                }
                d.setCard(card);
                BigDecimal returnMoney = o.getBigDecimal("returnMoney");
                if (returnMoney == null) {
                    returnMoney = BigDecimal.ZERO;
                }
                d.setReturnMoney(returnMoney);

                Integer returnScore = o.getInteger("returnScore");
                if (returnScore == null) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "returnScore 不能为空");
                }
                d.setReturnScore(returnScore);
                selfList.add(d);
            }
        }
        vo.setSelfList(selfList);

        return vo;
    }


    public static class ReturnDetail {

        private String ogsSeq;
        private String sellerNo;
        private Integer quantity;
        private String itNo;
        private String olSeq;
        private String rlSeq;
        private String packageNo;
        private String kind;

        private BigDecimal card;

        //实退金钱
        private BigDecimal returnMoney;

        //实金额
        private BigDecimal price;

        private Integer returnScore;

        //应退金额
        private BigDecimal refundablePrice;

        public String getOgsSeq() {
            return ogsSeq;
        }

        public void setOgsSeq(String ogsSeq) {
            this.ogsSeq = ogsSeq;
        }

        public String getOlSeq() {
            return olSeq;
        }

        public void setOlSeq(String olSeq) {
            this.olSeq = olSeq;
        }

        public String getSellerNo() {
            return sellerNo;
        }

        public void setSellerNo(String sellerNo) {
            this.sellerNo = sellerNo;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getItNo() {
            return itNo;
        }

        public void setItNo(String itNo) {
            this.itNo = itNo;
        }

        public String getPackageNo() {
            return packageNo;
        }

        public void setPackageNo(String packageNo) {
            this.packageNo = packageNo;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getRlSeq() {
            return rlSeq;
        }

        public void setRlSeq(String rlSeq) {
            this.rlSeq = rlSeq;
        }

        public BigDecimal getCard() {
            return card;
        }

        public void setCard(BigDecimal card) {
            this.card = card;
        }

        public BigDecimal getReturnMoney() {
            return returnMoney;
        }

        public void setReturnMoney(BigDecimal returnMoney) {
            this.returnMoney = returnMoney;
        }

        public BigDecimal getRealReturn() {
            //返回多少钱
            BigDecimal realReturnMoney = returnMoney.add(card);
            if (realReturnMoney.compareTo(BigDecimal.ZERO) < 0) {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "返回金钱小于0！");
            }
            return realReturnMoney;
        }

        public Integer getReturnScore() {
            if (returnScore == null) {
                returnScore = 0;
            }
            return returnScore;
        }

        public void setReturnScore(Integer returnScore) {
            this.returnScore = returnScore;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getRefundablePrice() {
            return refundablePrice;
        }

        public void setRefundablePrice(BigDecimal refundablePrice) {
            this.refundablePrice = refundablePrice;
        }

        @Override
        public String toString() {
            return "ReturnDetail{" +
                    "ogsSeq='" + ogsSeq + '\'' +
                    ", sellerNo='" + sellerNo + '\'' +
                    ", quantity=" + quantity +
                    ", itNo='" + itNo + '\'' +
                    ", olSeq='" + olSeq + '\'' +
                    ", rlSeq='" + rlSeq + '\'' +
                    ", packageNo='" + packageNo + '\'' +
                    ", kind='" + kind + '\'' +
                    ", card=" + card +
                    ", returnMoney=" + returnMoney +
                    ", price=" + price +
                    ", returnScore=" + returnScore +
                    ", refundablePrice=" + refundablePrice +
                    '}';
        }
    }

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid;
    }

    public String getRgSeq() {
        return rgSeq;
    }

    public void setRgSeq(String rgSeq) {
        this.rgSeq = rgSeq;
    }

    public String getRgNo() {
        return rgNo;
    }

    public void setRgNo(String rgNo) {
        this.rgNo = rgNo;
    }

    public String getOgSeq() {
        return ogSeq;
    }

    public void setOgSeq(String ogSeq) {
        this.ogSeq = ogSeq;
    }

    public String getOgNo() {
        return ogNo;
    }


    public void setOgNo(String ogNo) {
        this.ogNo = ogNo;
    }


    public List<ReturnDetail> getMallList() {
        return mallList;
    }


    public void setMallList(List<ReturnDetail> mallList) {
        this.mallList = mallList;
    }


    public List<ReturnDetail> getSelfList() {
        return selfList;
    }

    public void setSelfList(List<ReturnDetail> selfList) {
        this.selfList = selfList;
    }

    @Override
    public String toString() {
        return "ReturnJsonVo{" +
                "memGuid='" + memGuid + '\'' +
                ", rgSeq='" + rgSeq + '\'' +
                ", rgNo='" + rgNo + '\'' +
                ", ogSeq='" + ogSeq + '\'' +
                ", ogNo='" + ogNo + '\'' +
                ", mallList=" + mallList +
                ", selfList=" + selfList +
                '}';
    }

}
