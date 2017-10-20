package com.feiniu.score.mapper.score;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.feiniu.score.vo.SignInfo;
import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.vo.ReturnJsonVo;

public interface ScoreMainLogMapper {

    int saveScoreMainLog(@Param("sl") ScoreMainLog scoreLog,
                         @Param("tableNo") int tableNo);

    ScoreMainLog getScoreMainLog(@Param("memGuid") String memGuid,
                                 @Param("ogSeq") String ogSeq, @Param("channel") Integer channel,
                                 @Param("tableNo") int tableNo);

    ScoreMainLog findScoreMainLog(@Param("memGuid") String memGuid,
                                  @Param("ogSeq") String ogSeq, @Param("rgSeq") String rgSeq,
                                  @Param("channel") Integer channel, @Param("tableNo") int tableNo);

    /**
     * 分页获得用户积分详细信息列表
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    List<Map<String, Object>> getUserScoreDetailList(
            @Param("mapParam") Map<String, Object> mapParam,
            @Param("tableNo") Integer tableNo);

    /**
     * 获得用户积分详细信息列表总数
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    Integer getUserScoreDetailListCount(
            @Param("mapParam") Map<String, Object> mapParam,
            @Param("tableNo") Integer tableNo);

    /**
     * 分页获得积分详细信息列表
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    List<ScoreMainLog> getScoreMainLogList(
            @Param("mapParam") Map<String, Object> mapParam,
            @Param("tableNo") Integer tableNo);

    /**
     * 通过smlSeq获取ScoreMainLog
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    List<ScoreMainLog> getScoreMainLogListBySmlSeq(
            @Param("mapParam") Map<String, Object> mapParam,
            @Param("tableNo") Integer tableNo);

    /**
     * 更新scoreMainLog的rgSeq
     *
     * @param smlSeq
     * @param rgSeq
     * @param tableNo
     * @return
     */
    int updateRgNo(@Param("smlSeq") Integer smlSeq, @Param("rgNo") String rgNo, @Param("tableNo") int tableNo);

    /**
     * 获得用户取消订单或退货 需要返回的积分 scoreMainLog信息
     *
     * @param memGuid
     * @param rgSeq
     * @param channel
     * @param tableNo
     * @return
     */
    ScoreMainLog getScoreMainLogBack(@Param("memGuid") String memGuid,
                                     @Param("rgSeq") String rgSeq, @Param("channel") Integer channel,
                                     @Param("tableNo") int tableNo);

    /**
     * 更新scoreMainLog
     *
     * @param scoreMainLogBack
     * @param tableNo
     * @return
     */
    int updateScoreMainLog(@Param("sml") ScoreMainLog scoreMainLog,
                           @Param("tableNo") int tableNo);

    /**
     * 更新scoreMainLog的job执行状态
     *
     * @param smlSeq
     * @param status
     * @param tableNo
     * @return
     */
    int updateScoreMainLogJobStatus(@Param("smlSeq") Integer smlSeq,
                                    @Param("status") Integer status, @Param("tableNo") int tableNo);

    /**
     * 获得绑定手机获得积分的记录
     *
     * @param memGuid
     * @param channel
     * @param tableNo
     * @return
     */
    Integer getScoreMainLogCountByChannel(
            @Param("memGuid") String memGuid,
            @Param("channel") Integer channel, @Param("tableNo") int tableNo);

    /**
     * 获得当前日期签到获得积分
     */
    Integer getTodayScoreBySign(@Param("uniqueId") String uniqueId, @Param("tableNo") int tableNo);


    /**
     * 根据ID获得ScoreMainLog
     *
     * @param smlSeq
     * @param tableNo
     * @return
     */
    ScoreMainLog getScoreMainLogById(@Param("smlSeq") Integer smlSeq,
                                     @Param("tableNo") int tableNo);

    /**
     * 删除记录
     *
     * @param smlSeq
     * @param tableNo
     * @return
     */
    int deleteScoreMainLogById(@Param("smlSeq") Integer smlSeq,
                               @Param("tableNo") int tableNo);

    /**
     * ERP积分流水分页
     *
     * @param memGuid
     * @param paramMap
     * @param tableNo
     * @return
     */
    List<Map<String, Object>> getUserScoreLogDetailList(
            @Param("memGuid") String memGuid,
            @Param("paramMap") Map<String, Object> paramMap,
            @Param("tableNo") int tableNo);

    /**
     * ERP积分流水分页总数
     *
     * @param memGuid
     * @param paramMap
     * @param tableNo
     * @return
     */
    Integer getUserScoreLogDetailListCount(@Param("memGuid") String memGuid,
                                           @Param("paramMap") Map<String, Object> paramMap,
                                           @Param("tableNo") int tableNo);

    /**
     * 获取订单生效积分
     *
     * @param memGuid
     * @param ogSeq
     * @param tableNo
     * @return
     */
    Integer getOrderAvailableScore(@Param("memGuid") String memGuid, @Param("ogSeq") String ogSeq, @Param("tableNo") int tableNo);

    /**
     * 获取当日退订返点积分
     *
     * @param tableNo
     * @return
     */
    Integer getReciperareScore(@Param("paramMap") Map<String, Object> paramMap,
                               @Param("tableNo") Integer tableNo);

    /**
     * 获取当日待生效积分
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    Integer getLockedScore(@Param("paramMap") Map<String, Object> paramMap,
                           @Param("tableNo") Integer tableNo);

    /**
     * 获取待生效积分
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    Integer getLeftToBeEffective(@Param("paramMap") Map<String, Object> paramMap,
                                 @Param("tableNo") Integer tableNo);

    /**
     * 获取当日生效积分
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    Integer getEffectedScore(@Param("paramMap") Map<String, Object> paramMap,
                             @Param("tableNo") Integer tableNo);

    /**
     * 获取当日失效积分
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    Integer getFailureScore(@Param("paramMap") Map<String, Object> paramMap,
                            @Param("tableNo") Integer tableNo);

    /**
     * 获取当日使用积分
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    Integer getUsedScore(@Param("paramMap") Map<String, Object> paramMap,
                         @Param("tableNo") Integer tableNo);


    /**
     * 得到订单相关的积分变有效记录
     *
     * @param memGuid
     * @param ogSeq
     * @param channel
     * @param tableNo
     * @return
     */
    ScoreMainLog getAvailbaleScoreMainLogAboutOrder(@Param("memGuid") String memGuid,
                                                    @Param("ogSeq") String ogSeq, @Param("channel") Integer channel, @Param("tableNo") int tableNo);

    ScoreMainLog getScoreMainLogForUpdate(@Param("memGuid") String memGuid,
                                          @Param("ogSeq") String ogSeq, @Param("channel") Integer channel,
                                          @Param("tableNo") int tableNo);

    /**
     * 查询最近一笔有效订单的时间
     *
     * @param memGuid
     * @param tableNo
     * @return
     */
    Date getLastEffectiveOrderTime(@Param("memGuid") String memGuid, @Param("tableNo") int tableNo);

    /**
     * 查询最近一笔订单后签到的次数。从迭代代码上线开始计算
     *
     * @param memGuid
     * @param tableNo
     * @param LastEffectiveOrderTime 最近一笔有效订单的时间
     * @param onlineTime             系统上线时间
     * @return
     */
    int getSignCountAfterLastEffectiveOrder(@Param("memGuid") String memGuid, @Param("tableNo") int tableNo, @Param("LastEffectiveOrderTime") String LastEffectiveOrderTime, @Param("onlineTime") String onlineTime);

    /**
     * 按唯一索引查询
     *
     * @param tableNo
     * @param uniqueId
     * @return
     */
    ScoreMainLog getScoreMainLogByUniqueId(@Param("uniqueId") String uniqueId,
                                           @Param("tableNo") int tableNo);

    /**
     * 分页获得积分详细信息的用户GUID列表
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    List<String> getScoreMainLogMemGuidList(
            @Param("mapParam") Map<String, Object> mapParam,
            @Param("tableNo") Integer tableNo);

    /**
     * 获得用户当天解冻的积分及现有可用积分
     *
     * @param upTime
     * @param tableNo
     * @return
     */
    Map<String, Object> getMemUnlockedScoreByUpTime(
            @Param("memGuid") String memGuid,
            @Param("tableNo") Integer tableNo, @Param("upTime") String upTime);

    /**
     * 分页获得当天解冻的用户及其积分信息
     *
     * @param mapParam
     * @param tableNo
     * @return
     */
    List<Map<String, Object>> getUnlockedScoreInfoList(
            @Param("tableNo") Integer tableNo, @Param("mapParam") Map<String, Object> mapParam);

    /**
     * 获得用户最近一次连续签到的信息
     *
     * @param paramMap
     * @param tableNo
     * @return
     */
    SignInfo getLastSignInfo(@Param("memGuid") String memGuid,
                             @Param("tableNo") Integer tableNo, @Param("paramMap") Map<String, Object> paramMap);

    /**
     * 获得用户当月签到的所有日期（只返回日期，不返回年月）
     *
     * @param memGuid
     * @param tableNo
     * @return
     */
    List<String> getSignDateThisMonth(@Param("memGuid") String memGuid, @Param("channel") Integer channel, @Param("tableNo") Integer tableNo);

    ScoreMainLog getScoreMainLogByUniqueIdForUpdate(@Param("memGuid") String memGuid,
                                                    @Param("uniqueId") String uniqueId,
                                                    @Param("tableNo") int tableNo);
}
