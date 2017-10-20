package com.feiniu.score.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.BizException;
import com.feiniu.score.exception.ScoreException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderJsonVo {

    private String memGuid;
    private String provinceId;
    private String ogSeq;
    private String ogNo;
    private Integer sourceMode;
    private String siteMode;
    private String groupId;
    private String adrId;
    private Integer virtual;


    private List<OrderDetail> mallList;
    private List<OrderDetail> selfList;

    /**
     * 如果没有消费积分则返回true
     */
    public boolean isNotConsumeScore() {
        BigDecimal consumeScore = BigDecimal.ZERO;
        if (mallList != null && mallList.size() > 0) {
            for (OrderDetail orderDetail : mallList) {
                BigDecimal score = orderDetail.getScore();
                if (score != null) {
                    consumeScore = consumeScore.add(score);
                }
            }
        }
        if (selfList != null && selfList.size() > 0) {
            for (OrderDetail orderDetail : selfList) {
                BigDecimal score = orderDetail.getScore();
                if (score != null) {
                    consumeScore = consumeScore.add(score);
                }
            }
        }
        return consumeScore.compareTo(BigDecimal.ZERO) == 0;
    }

    public static OrderJsonVo convertJson(JSONObject order) {

        OrderJsonVo vo = new OrderJsonVo();

        String memGuid = order.getString("memGuid");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        vo.setMemGuid(memGuid);
        vo.setProvinceId(order.getString("provinceId"));

        String ogSeq = order.getString("ogSeq");
        if (StringUtils.isEmpty("ogSeq")) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        vo.setOgSeq(ogSeq);

        String ogNo = order.getString("ogNo");
        if (StringUtils.isEmpty(ogNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogNo 不能为空");
        }
        vo.setOgNo(ogNo);
        vo.setSourceMode(order.getInteger("sourceMode"));
        vo.setSiteMode(order.getString("siteMode"));
        vo.setAdrId(order.getString("adrId"));
        vo.setGroupId(order.getString("groupId"));
        vo.setVirtual(order.getInteger("isVirtual"));
        JSONArray mall = order.getJSONArray("mall");
        JSONArray self = order.getJSONArray("self");

        List<OrderDetail> mallList = new ArrayList<>();
        if (mall != null && mall.size() > 0) {
            Iterator<Object> mallIt = mall.iterator();
            while (mallIt.hasNext()) {
                OrderDetail d = new OrderDetail();
                JSONObject o = (JSONObject) mallIt.next();
                String ogsSeq = o.getString("ogsSeq");
                if (StringUtils.isEmpty(ogsSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogsSeq 不能为空");
                }
                d.setOgsSeq(ogsSeq);

                String olsSeq = o.getString("olsSeq");
                if (StringUtils.isEmpty(olsSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "olsSeq 不能为空");
                }
                d.setOlSeq(olsSeq);
                String itNo = o.getString("itNo");
                if (StringUtils.isEmpty(itNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "itNo 不能为空");
                }
                d.setItNo(itNo);

                int qty = o.getIntValue("qty");
                if (qty == 0) {
                    continue;
                }
                d.setQuantity(qty);
                String merchantId = o.getString("merchantId");
                if (StringUtils.isEmpty(merchantId)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "merchantId 不能为空");
                }
                d.setSellerNo(merchantId);
                String packageNo = o.getString("packageNo");
                if (StringUtils.isEmpty(packageNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "packageNo 不能为空");
                }
                d.setPackageNo(packageNo);
                d.setSmSeq(o.getString("smSeq"));

                String kind = o.getString("kind");
                if (StringUtils.isEmpty(kind)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "kind 不能为空");
                }
                d.setKind(kind);
                d.setCard(o.getBigDecimal("card"));
                d.setCoupons(o.getBigDecimal("coupons"));
                d.setVouchers(o.getBigDecimal("vouchers"));
                d.setBonus(o.getBigDecimal("bonus"));
                BigDecimal score = o.getBigDecimal("score");
                if (score == null) {
                    score = BigDecimal.ZERO;
                }
                d.setScore(score);
                d.setPrice(o.getBigDecimal("price"));
                d.setSellActivity(o.getBigDecimal("sellActivity"));
                //跨境标识
                d.setOversea(o.getIntValue("oversea"));
                d.setBuyMode(o.getString("buyMode"));
                d.setSsmGrade(o.getString("ssmGrade"));
                d.setAprnVvip(o.getBigDecimal("aprnVvip"));
                d.setAprnVvipPoints(o.getBigDecimal("aprnVvipPoints"));
                d.setUseBalancePoints(o.getBigDecimal("useBalancePoints"));
                d.setDividendDiscount(o.getBigDecimal("dividendDiscount"));
                d.setScoreMallDiscount(o.getBigDecimal("scoreMallDiscount"));
                d.setBrandCoupons(o.getBigDecimal("brandCoupons"));
                d.setMarketGiftCoupons(o.getBigDecimal("marketGiftCoupons"));
                d.setCommodityGiftCoupons(o.getBigDecimal("commodityGiftCoupons"));
                //团购批次号
                d.setGroupBatchNo(o.getString("groupBatchNo"));
                d.setActSeq(o.getString("actSeq"));
                d.setCpSeq(o.getString("cpSeq"));
                mallList.add(d);
            }

        }
        vo.setMallList(mallList);

        List<OrderDetail> selfList = new ArrayList<>();
        if (self != null && self.size() > 0) {
            Iterator<Object> sellIt = self.iterator();
            while (sellIt.hasNext()) {
                OrderDetail d = new OrderDetail();
                JSONObject o = (JSONObject) sellIt.next();
                String olSeq = o.getString("olSeq");
                if (StringUtils.isEmpty(olSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "olSeq 不能为空");
                }
                d.setOlSeq(olSeq);
                String itNo = o.getString("itNo");
                if (StringUtils.isEmpty(itNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "itNo 不能为空");
                }
                d.setItNo(itNo);
                int qty = o.getIntValue("qty");
                if (qty == 0) {
                    continue;
                }
                d.setQuantity(qty);
                String packageNo = o.getString("packageNo");
                if (StringUtils.isEmpty(packageNo)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "packageNo 不能为空");
                }
                d.setPackageNo(packageNo);

                String smSeq = o.getString("smSeq");
                if (StringUtils.isEmpty(smSeq)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "smSeq 不能为空");
                }
                d.setSmSeq(smSeq);
                String kind = o.getString("kind");
                if (StringUtils.isEmpty(kind)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "kind 不能为空");
                }
                d.setKind(kind);
                String fdlSeq = o.getString("fdlSeq");
                if (StringUtils.isEmpty(fdlSeq)) {
                    fdlSeq = null;
                    //throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"fdlSeq 不能为空");
                }
                d.setFdlSeq(fdlSeq);

                String id = o.getString("id");
                if (StringUtils.isEmpty(id)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "id 不能为空");
                }
                d.setId(id);

                String parentId = o.getString("pId");
                if (StringUtils.isEmpty(parentId)) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pId 不能为空");
                }
                d.setParentId(parentId);


                d.setCard(o.getBigDecimal("card"));
                d.setCoupons(o.getBigDecimal("coupons"));
                d.setVouchers(o.getBigDecimal("vouchers"));
                d.setBonus(o.getBigDecimal("bonus"));
                BigDecimal score = o.getBigDecimal("score");
                if (score == null) {
                    score = BigDecimal.ZERO;
                }
                d.setScore(score);
                d.setPrice(o.getBigDecimal("price"));
                d.setSellActivity(o.getBigDecimal("sellActivity"));
                //跨境标识
                d.setOversea(o.getIntValue("oversea"));
                d.setBuyMode(o.getString("buyMode"));
                d.setSsmGrade(o.getString("ssmGrade"));
                d.setAprnVvip(o.getBigDecimal("aprnVvip"));
                d.setAprnVvipPoints(o.getBigDecimal("aprnVvipPoints"));
                d.setUseBalancePoints(o.getBigDecimal("useBalancePoints"));
                d.setDividendDiscount(o.getBigDecimal("dividendDiscount"));
                d.setScoreMallDiscount(o.getBigDecimal("scoreMallDiscount"));
                d.setBrandCoupons(o.getBigDecimal("brandCoupons"));
                d.setMarketGiftCoupons(o.getBigDecimal("marketGiftCoupons"));
                d.setCommodityGiftCoupons(o.getBigDecimal("commodityGiftCoupons"));
                //团购批次号
                d.setGroupBatchNo(o.getString("groupBatchNo"));
                d.setActSeq(o.getString("actSeq"));
                d.setCpSeq(o.getString("cpSeq"));
                selfList.add(d);
            }

        }
        vo.setSelfList(selfList);
        if (mallList.size() == 0 && selfList.size() == 0) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询订单详细信息错误，没有订单数据。");
        }
        return vo;
    }

    public static class OrderDetail {

        private String ogsSeq;
        private String sellerNo;
        private int quantity;
        private String olSeq;
        private String itNo;
        private int consumeScore;
        private String packageNo;
        private String smSeq;
        private String fdlSeq;
        private String kind;
        private BigDecimal realPay;
        private BigDecimal card;  //购物卡
        private BigDecimal coupons; //优惠券(飞牛券)
        private BigDecimal vouchers;  //购物金
        private BigDecimal bonus;  //抵用劵
        private BigDecimal score;  //积分积分抵扣
        private BigDecimal price;
        private BigDecimal sellActivity; //行销活动抵扣
        private BigDecimal aprnVvip;//VVIP折扣
        private BigDecimal aprnVvipPoints;//VVIP乐汇点数
        private BigDecimal useBalancePoints;  //飞牛账户余额支付
        private BigDecimal dividendDiscount;  //红利折扣
        private BigDecimal scoreMallDiscount; //积分商城折扣
        private BigDecimal brandCoupons; //品牌券
        private BigDecimal marketGiftCoupons;//礼品券 （市场部优惠）
        private BigDecimal commodityGiftCoupons;//礼品券（商品部折扣）
        private int oversea;//跨境
        private String buyMode;//兑换商品
        private String ssmGrade;//促销等级
        private String groupBatchNo;//团购批次号
        private String parentId;
        private String id;
        private String actSeq;
        private String cpSeq;

        public BigDecimal computeRealPay() {

            OrderDetail d = this;

            BigDecimal price = d.getPrice();
            int quantity = d.getQuantity();
            if (price == null) {
                throw new BizException("商品价格 must not null ");
            }
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                //throw new BizException("商品价格 不能小于 0");
                price = BigDecimal.ZERO;
            }
            if (quantity <= 0) {
                quantity = 0;
                //throw new BizException("商品购买数量不能 小于 0 ");
            }

            BigDecimal realPay = price.multiply(new BigDecimal(quantity));
            if (d.getCoupons() != null) {
                realPay = realPay.subtract(d.getCoupons());
            }
            if (d.getVouchers() != null) {
                realPay = realPay.subtract(d.getVouchers());
            }
            if (d.getBonus() != null) {
                realPay = realPay.subtract(d.getBonus());
            }
            if (d.getScore() != null) {
                realPay = realPay.subtract(d.getScore());
            }
            if (d.getSellActivity() != null) {
                realPay = realPay.subtract(d.getSellActivity());
            }
            if (d.getAprnVvip() != null) {
                realPay = realPay.subtract(d.getAprnVvip());
            }
            if (d.getDividendDiscount() != null) {
                realPay = realPay.subtract(d.getDividendDiscount());
            }
            if (d.getScoreMallDiscount() != null) {
                realPay = realPay.subtract(d.getScoreMallDiscount());
            }
            if (d.getBrandCoupons() != null) {
                realPay = realPay.subtract(d.getBrandCoupons());
            }
            if (d.getMarketGiftCoupons() != null) {
                realPay = realPay.subtract(d.getMarketGiftCoupons());
            }
            if (d.getCommodityGiftCoupons() != null) {
                realPay = realPay.subtract(d.getCommodityGiftCoupons());
            }
            if (realPay.compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "实际支付为负");
            }
            return realPay;
        }

        public String getOgsSeq() {
            return ogsSeq;
        }

        public void setOgsSeq(String ogsSeq) {
            this.ogsSeq = ogsSeq;
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

        public String getSmSeq() {
            return smSeq;
        }

        public void setSmSeq(String smSeq) {
            this.smSeq = smSeq;
        }

        public String getSellerNo() {
            return sellerNo;
        }

        public void setSellerNo(String sellerNo) {
            this.sellerNo = sellerNo;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getOlSeq() {
            return olSeq;
        }

        public void setOlSeq(String olSeq) {
            this.olSeq = olSeq;
        }

        public BigDecimal getCard() {
            return card;
        }

        public void setCard(BigDecimal card) {
            this.card = card;
        }

        public BigDecimal getCoupons() {
            return coupons;
        }

        public void setCoupons(BigDecimal coupons) {
            this.coupons = coupons;
        }

        public BigDecimal getVouchers() {
            return vouchers;
        }

        public void setVouchers(BigDecimal vouchers) {
            this.vouchers = vouchers;
        }

        public BigDecimal getBonus() {
            return bonus;
        }

        public void setBonus(BigDecimal bonus) {
            this.bonus = bonus;
        }

        public BigDecimal getScore() {
            if (score == null) {
                score = BigDecimal.ZERO;
            }
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public int getConsumeScore() {
            return consumeScore;
        }

        public void setConsumeScore(int consumeScore) {
            this.consumeScore = consumeScore;
        }

        public BigDecimal getRealPay() {
            if (realPay == null) {
                realPay = computeRealPay();
            }
            return realPay;
        }

        public void setRealPay(BigDecimal realPay) {
            this.realPay = realPay;
        }

        public BigDecimal getSellActivity() {
            return sellActivity;
        }

        public void setSellActivity(BigDecimal sellActivity) {
            this.sellActivity = sellActivity;
        }

        public BigDecimal getAprnVvip() {
            return aprnVvip;
        }

        public void setAprnVvip(BigDecimal aprnVvip) {
            this.aprnVvip = aprnVvip;
        }

        public BigDecimal getAprnVvipPoints() {
            return aprnVvipPoints;
        }

        public void setAprnVvipPoints(BigDecimal aprnVvipPoints) {
            this.aprnVvipPoints = aprnVvipPoints;
        }

        public String getFdlSeq() {
            return fdlSeq;
        }

        public void setFdlSeq(String fdlSeq) {
            this.fdlSeq = fdlSeq;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getOversea() {
            return oversea;
        }

        public void setOversea(int oversea) {
            this.oversea = oversea;
        }

        public String getBuyMode() {
            return buyMode;
        }

        public void setBuyMode(String buyMode) {
            this.buyMode = buyMode;
        }


        public String getSsmGrade() {
            return ssmGrade;
        }

        public void setSsmGrade(String ssmGrade) {
            this.ssmGrade = ssmGrade;
        }

        public BigDecimal getUseBalancePoints() {
            return useBalancePoints;
        }

        public void setUseBalancePoints(BigDecimal useBalancePoints) {
            this.useBalancePoints = useBalancePoints;
        }

        public BigDecimal getDividendDiscount() {
            return dividendDiscount;
        }

        public void setDividendDiscount(BigDecimal dividendDiscount) {
            this.dividendDiscount = dividendDiscount;
        }

        public BigDecimal getScoreMallDiscount() {
            return scoreMallDiscount;
        }

        public void setScoreMallDiscount(BigDecimal scoreMallDiscount) {
            this.scoreMallDiscount = scoreMallDiscount;
        }

        public BigDecimal getBrandCoupons() {
            return brandCoupons;
        }

        public void setBrandCoupons(BigDecimal brandCoupons) {
            this.brandCoupons = brandCoupons;
        }

        public String getGroupBatchNo() {
            return groupBatchNo;
        }

        public void setGroupBatchNo(String groupBatchNo) {
            this.groupBatchNo = groupBatchNo;
        }


        public String getActSeq() {
            return actSeq;
        }

        public void setActSeq(String actSeq) {
            this.actSeq = actSeq;
        }

        public String getCpSeq() {
            return cpSeq;
        }

        public void setCpSeq(String cpSeq) {
            this.cpSeq = cpSeq;
        }

        public BigDecimal getMarketGiftCoupons() {
            return marketGiftCoupons;
        }

        public void setMarketGiftCoupons(BigDecimal marketGiftCoupons) {
            this.marketGiftCoupons = marketGiftCoupons;
        }

        public BigDecimal getCommodityGiftCoupons() {
            return commodityGiftCoupons;
        }

        public void setCommodityGiftCoupons(BigDecimal commodityGiftCoupons) {
            this.commodityGiftCoupons = commodityGiftCoupons;
        }

        @Override
        public String toString() {
            return "OrderDetail{" +
                    "ogsSeq='" + ogsSeq + '\'' +
                    ", sellerNo='" + sellerNo + '\'' +
                    ", quantity=" + quantity +
                    ", olSeq='" + olSeq + '\'' +
                    ", itNo='" + itNo + '\'' +
                    ", consumeScore=" + consumeScore +
                    ", packageNo='" + packageNo + '\'' +
                    ", smSeq='" + smSeq + '\'' +
                    ", fdlSeq='" + fdlSeq + '\'' +
                    ", kind='" + kind + '\'' +
                    ", realPay=" + realPay +
                    ", card=" + card +
                    ", coupons=" + coupons +
                    ", vouchers=" + vouchers +
                    ", bonus=" + bonus +
                    ", score=" + score +
                    ", price=" + price +
                    ", sellActivity=" + sellActivity +
                    ", aprnVvip=" + aprnVvip +
                    ", aprnVvipPoints=" + aprnVvipPoints +
                    ", useBalancePoints=" + useBalancePoints +
                    ", dividendDiscount=" + dividendDiscount +
                    ", scoreMallDiscount=" + scoreMallDiscount +
                    ", brandCoupons=" + brandCoupons +
                    ", marketGiftCoupons=" + marketGiftCoupons +
                    ", commodityGiftCoupons=" + commodityGiftCoupons +
                    ", oversea=" + oversea +
                    ", buyMode='" + buyMode + '\'' +
                    ", ssmGrade='" + ssmGrade + '\'' +
                    ", groupBatchNo='" + groupBatchNo + '\'' +
                    ", parentId='" + parentId + '\'' +
                    ", id='" + id + '\'' +
                    ", actSeq='" + actSeq + '\'' +
                    ", cpSeq='" + cpSeq + '\'' +
                    '}';
        }
    }


    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid;
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

    public Integer getSourceMode() {
        return sourceMode;
    }

    public void setSourceMode(Integer sourceMode) {
        this.sourceMode = sourceMode;
    }

    public String getSiteMode() {
        return siteMode;
    }

    public void setSiteMode(String siteMode) {
        this.siteMode = siteMode;
    }

    public String getAdrId() {
        return adrId;
    }

    public void setAdrId(String adrId) {
        this.adrId = adrId;
    }

    public List<OrderDetail> getMallList() {
        return mallList;
    }

    public void setMallList(List<OrderDetail> mallList) {
        this.mallList = mallList;
    }

    public List<OrderDetail> getSelfList() {
        return selfList;
    }

    public void setSelfList(List<OrderDetail> selfList) {
        this.selfList = selfList;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getVirtual() {
        return virtual;
    }

    public void setVirtual(Integer virtual) {
        this.virtual = virtual;
    }

    @Override
    public String toString() {
        return "OrderJsonVo{" +
                "memGuid='" + memGuid + '\'' +
                ", provinceId='" + provinceId + '\'' +
                ", ogSeq='" + ogSeq + '\'' +
                ", ogNo='" + ogNo + '\'' +
                ", sourceMode=" + sourceMode +
                ", siteMode='" + siteMode + '\'' +
                ", groupId='" + groupId + '\'' +
                ", adrId='" + adrId + '\'' +
                ", virtual=" + virtual +
                ", mallList=" + mallList +
                ", selfList=" + selfList +
                '}';
    }
}
