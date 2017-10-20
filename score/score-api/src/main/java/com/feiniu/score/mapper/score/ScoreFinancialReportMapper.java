package com.feiniu.score.mapper.score;

import com.feiniu.score.entity.score.ScoreReportEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ScoreFinancialReportMapper {

    /**
     * 购物发放积分统计（包括自营，门店，商城）
     */
    List<ScoreReportEntity> shoppingGrantScore(@Param("params") Map<String, Object> params);

    /**
     * 绑定 签到积分（立即生效）
     */
    List<ScoreReportEntity> bindAndSignGrantScore(@Param("params") Map<String, Object> params);

    /**
     * 购物发放积分生效
     */
    List<ScoreReportEntity> shoppingGrantScoreEffect(@Param("params") Map<String, Object> params);

    /**
     * 客人因购买该厂商商品获得的积分，被用来购物的点数
     */
    List<ScoreReportEntity> selfShoppingUseScore(@Param("params") Map<String, Object> params);

    /**
     * 退订还点：客人使用积分购物后又退订(含出货前退订和出货后退订)而返还给客人的积分
     */
    List<ScoreReportEntity> shoppingReturnConsumeScore(@Param("params") Map<String, Object> params);

    /**
     * 专指生效后的失效--积分到期失效和生效后其他情况失效(如生效后退货扣回赠送积分、删除评论扣除积分等)
     */
    List<ScoreReportEntity> recoveryGrantEffectScore(@Param("params") Map<String, Object> params);

    /**
     * 评论相关积分
     */
    List<ScoreReportEntity> commentGrantScore(@Param("params") Map<String, Object> params);

    /**
     * 退货回收评论相关积分
     */
    List<ScoreReportEntity> recoveryCommentGrantScore(@Param("params") Map<String, Object> params);

    int saveReport(@Param("entityList") List<ScoreReportEntity> entityList);

    List<ScoreReportEntity> shoppingToUseScore(@Param("params") Map<String, Object> params);

    int saveUseReport(@Param("entityList") List<ScoreReportEntity> entityList);

    List<Map<String, Object>> feiniuGrantAndRecoveryScore(@Param("params") Map<String, Object> params);

    ScoreReportEntity getStatistics(@Param("sre") ScoreReportEntity sre);

    ScoreReportEntity getUseStatistics(@Param("sre") ScoreReportEntity sre);

    List<Map<String,Object>> chouJangUseScore(@Param("params") Map<String, Object> params);

    List<ScoreReportEntity> otherUseScore(@Param("params")Map<String, Object> params);
}
