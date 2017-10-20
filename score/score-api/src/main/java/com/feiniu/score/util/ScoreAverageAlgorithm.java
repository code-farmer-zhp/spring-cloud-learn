package com.feiniu.score.util;

import com.feiniu.score.vo.OrderJsonVo.OrderDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * 对消费积分进行平摊
 */
public class ScoreAverageAlgorithm {

    public static void amorizate(List<OrderDetail> allList, int mount) {

        for (OrderDetail orderDetail : allList) {
            BigDecimal realConsumeScore = orderDetail.getScore().multiply(new BigDecimal(100));
            orderDetail.setConsumeScore(realConsumeScore.intValue());
        }

       /* assert allList != null;
        if (allList.size() == 0 || mount == 0) {
            return;
        }

        //总金额
        BigDecimal totalAmount = BigDecimal.ZERO;

        //已经分摊积分
        int amorizated = 0;


        for (OrderDetail item : allList) {
            totalAmount = totalAmount.add(item.getRealPay());
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal bigDecimalMount = new BigDecimal(mount);
        //计算分摊
        for (OrderDetail item : allList) {

            //跳过0元商品
            BigDecimal realPay = item.getRealPay();
            if (realPay.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            //算平摊积分
            int avg = (realPay.multiply(bigDecimalMount)).divide(totalAmount, BigDecimal.ROUND_DOWN).intValue();
            amorizated += avg;
            item.setConsumeScore(avg);

        }
        //计算剩余分摊
        for (OrderDetail item : allList) {

            if (amorizated == mount) {
                break;
            }
            //跳过0元商品
            BigDecimal realPay = item.getRealPay();
            if (realPay.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            int consumeScore = item.getConsumeScore();
            item.setConsumeScore(consumeScore + 1);
            amorizated += 1;
        }*/

    }
}
