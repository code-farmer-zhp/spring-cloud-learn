package com.feiniu.score.dao.score;

import com.feiniu.score.entity.score.ScoreReportEntity;

import java.util.List;
import java.util.Map;

public interface ScoreFinancialReportDao {

    /**
     * 购物发放积分统计（包括自营，门店，商城）
     */
    List<ScoreReportEntity> shoppingGrantScore(Map<String, Object> params);

    /**
     *其他渠道发放的积分（立即生效）
     */
    List<ScoreReportEntity> bindAndSignGrantScore(Map<String, Object> params);

    /**
     * 购物发放积分生效
     */
    List<ScoreReportEntity> shoppingGrantScoreEffect(Map<String, Object> params);

    /**
     *客人因购买该厂商商品获得的积分，被用来购物的点数
     */
    List<ScoreReportEntity> selfShoppingUseScore(Map<String, Object> params);

    /**
     *退订还点：客人使用积分购物后又退订(含出货前退订和出货后退订)而返还给客人的积分
     */
    List<ScoreReportEntity> shoppingReturnConsumeScore(Map<String, Object> params);

    /**
     *专指生效后的失效--积分到期失效和生效后其他情况失效(如生效后退货扣回赠送积分、删除评论扣除积分等)
     */
    List<ScoreReportEntity> recoveryGrantEffectScore(Map<String, Object> params);

    /**
     *评论送积分
     */
    List<ScoreReportEntity> commentGrantScore(Map<String, Object> params);

    /**
     * 退货回收评论相关积分
     */
    List<ScoreReportEntity> recoveryCommentGrantScore(Map<String, Object> params);

    int saveReport(List<ScoreReportEntity> entityList);

    List<ScoreReportEntity> shoppingToUseScore(Map<String, Object> params);

    int saveUseReport(List<ScoreReportEntity> scoreToUseEntities);

    List<Map<String,Object>> feiniuGrantAndRecoveryScore(Map<String, Object> params);

    List<Map<String,Object>>  chouJangUseScore(Map<String, Object> params);

    List<ScoreReportEntity> otherUseScore(Map<String, Object> params);
}
