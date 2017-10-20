package com.feiniu.score.dao.score;

public interface ScoreFeiniuGiveDao {
    /**
     *用户升级app送积分
     * @param memGuid 用户ID
     * @param type 1 安卓 2 IOS
     * @param version 升级的版本号
     * @param score 赠送的积分
     */
    void appUpGradeGiveScore(String memGuid, int type, String version,Integer score);
}
