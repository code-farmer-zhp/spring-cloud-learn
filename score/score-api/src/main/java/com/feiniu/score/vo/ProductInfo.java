package com.feiniu.score.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductInfo {
    private String memGuid;
    private String areaSeq;

    private List<OrderScoreInfo> selfList;

    private List<OrderScoreInfo> mallList;

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid;
    }

    public String getAreaSeq() {
        return areaSeq;
    }

    public void setAreaSeq(String areaSeq) {
        this.areaSeq = areaSeq;
    }

    public List<OrderScoreInfo> getSelfList() {
        return selfList;
    }

    public void setSelfList(List<OrderScoreInfo> selfList) {
        this.selfList = selfList;
    }

    public List<OrderScoreInfo> getMallList() {
        return mallList;
    }

    public void setMallList(List<OrderScoreInfo> mallList) {
        this.mallList = mallList;
    }

    public static ProductInfo parseJson(String json) {
        if (StringUtils.isEmpty(json)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空。");
        }
        ProductInfo productInfo = new ProductInfo();
        JSONObject jsonObject = JSONObject.parseObject(json);
        String memGuid = jsonObject.getString("memGuid");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        String areaSeq = jsonObject.getString("areaSeq");
        if (StringUtils.isEmpty(areaSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "areaSeq 不能为空");
        }
        productInfo.setMemGuid(memGuid);
        productInfo.setAreaSeq(areaSeq);
        JSONArray selfArray = jsonObject.getJSONArray("self");
        List<OrderScoreInfo> selfList = getOrderScoreInfos(selfArray);
        productInfo.setSelfList(selfList);

        JSONArray mallArray = jsonObject.getJSONArray("mall");
        List<OrderScoreInfo> mallList = getOrderScoreInfos(mallArray);
        productInfo.setMallList(mallList);

        return productInfo;
    }

    private static List<OrderScoreInfo> getOrderScoreInfos(JSONArray orderArray) {
        List<OrderScoreInfo> orderList = new ArrayList<>();
        if (orderArray != null && orderArray.size() > 0) {
            for (int i = 0; i < orderArray.size(); i++) {
                JSONObject orderListObj = orderArray.getJSONObject(i);

                //如果是商城商品。没有smSeq
                String smSeq = orderListObj.getString("smSeq");

                String itNo = orderListObj.getString("itNo");
                if (StringUtils.isEmpty(itNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "itNo 不能为空");
                }
                BigDecimal realPay = orderListObj.getBigDecimal("realPay");
                if (realPay == null) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "realPay 不能为空");
                }

                String parentId = orderListObj.getString("parentId");
                String ssmGrade = orderListObj.getString("ssmGrade");
                String ssmType = orderListObj.getString("ssmType");
                OrderScoreInfo orderScoreInfo = new OrderScoreInfo();
                orderScoreInfo.setSmSeq(smSeq);
                orderScoreInfo.setItNo(itNo);
                orderScoreInfo.setRealPay(realPay);
                orderScoreInfo.setParentId(parentId);
                orderScoreInfo.setSsmGrade(ssmGrade);
                orderScoreInfo.setSsmType(ssmType);
                orderScoreInfo.setCpSeq(orderListObj.getString("cpSeq"));
                orderList.add(orderScoreInfo);
            }
        }
        return orderList;
    }

    public static class OrderScoreInfo {
        /**
         * 卖场ID
         */
        private String smSeq;
        /**
         * 商品ID
         */
        private String itNo;
        /**
         * 商品类别
         */
        private String cpSeq;
        /**
         * 付款总金额
         */
        private BigDecimal realPay;

        /**
         * 商品数量
         */
        private int quantity;

        /**
         * 是否可以使用积分
         */
        private boolean canUserScore;

        private String parentId;
        /**
         * 促销等级
         */
        private String ssmGrade;

        /**
         * 判断是否是团购的类型标识
         */
        private String ssmType;

        public String getSmSeq() {
            return smSeq;
        }

        public void setSmSeq(String smSeq) {
            this.smSeq = smSeq;
        }

        public String getItNo() {
            return itNo;
        }

        public void setItNo(String itNo) {
            this.itNo = itNo;
        }

        public BigDecimal getRealPay() {
            return realPay;
        }

        public void setRealPay(BigDecimal realPay) {
            this.realPay = realPay;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public boolean isCanUserScore() {
            return canUserScore;
        }

        public void setCanUserScore(boolean canUserScore) {
            this.canUserScore = canUserScore;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getSsmGrade() {
			return ssmGrade;
		}

		public void setSsmGrade(String ssmGrade) {
			this.ssmGrade = ssmGrade;
		}

        public String getSsmType() {
            return ssmType;
        }

        public void setSsmType(String ssmType) {
            this.ssmType = ssmType;
        }

        public String getCpSeq() {
            return cpSeq;
        }

        public void setCpSeq(String cpSeq) {
            this.cpSeq = cpSeq;
        }

        @Override
        public String toString() {
            return "OrderScoreInfo{" +
                    "smSeq='" + smSeq + '\'' +
                    ", itNo='" + itNo + '\'' +
                    ", cpSeq='" + cpSeq + '\'' +
                    ", realPay=" + realPay +
                    ", quantity=" + quantity +
                    ", canUserScore=" + canUserScore +
                    ", parentId='" + parentId + '\'' +
                    ", ssmGrade='" + ssmGrade + '\'' +
                    ", ssmType='" + ssmType + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProductInfo{" +
                "memGuid='" + memGuid + '\'' +
                ", areaSeq='" + areaSeq + '\'' +
                ", selfList=" + selfList +
                ", mallList=" + mallList +
                '}';
    }
}
