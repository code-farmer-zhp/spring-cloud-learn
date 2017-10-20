package com.feiniu.score.service;

import com.feiniu.score.dao.score.ScoreFinancialReportDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ScoreReportEntity;
import com.feiniu.score.util.ShardUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ReportTableService implements Callable<Map<String, Object>> {

    private String dataScorceName;

    private ScoreFinancialReportDao scoreFinancialReportDao;

    private String yesterday;

    public ReportTableService(String dataScorceName, ScoreFinancialReportDao scoreFinancialReportDao, String yesterday) {
        this.dataScorceName = dataScorceName;
        this.scoreFinancialReportDao = scoreFinancialReportDao;
        this.yesterday = yesterday;
    }

    List<ScoreReportEntity> shoppingGrantScoreList = new LinkedList<>();

    List<ScoreReportEntity> shoppingGrantScoreEffectList = new LinkedList<>();

    List<ScoreReportEntity> selfShoppingUseScoreList = new LinkedList<>();

    List<ScoreReportEntity> shoppingReturnConsumeScoreList = new LinkedList<>();

    List<ScoreReportEntity> recoveryGrantEffectScoreList = new LinkedList<>();

    List<ScoreReportEntity> bindAndSignGrantScoreList = new LinkedList<>();

    List<ScoreReportEntity> commentGrantScoreList = new LinkedList<>();

    List<ScoreReportEntity> recoveryCommentGrantScoreList = new LinkedList<>();

    List<ScoreReportEntity> shoppingToUseScoreList = new LinkedList<>();

    List<Map<Integer, Integer>> feiniuGrantAndRecoveryScoreList = new LinkedList<>();

    List<Map<Integer, Integer>> chouJangUseScoreList = new LinkedList<>();

    @Override
    public Map<String, Object> call() throws Exception {

        DataSourceUtils.setCurrentKey(dataScorceName);
        /**
         * 统计每一个表中的数据
         */
        for (int tableNo = 0; tableNo < ShardUtils.getTableCount(); tableNo++) {


            /**
             *购物发放积分统计（包括自营，门店，商城）
             */
            shoppingGrantScore(tableNo);

            /**
             * 积分生效
             */
            shoppingGrantScoreEffect(tableNo);

            /**
             * 购物使用
             */
            selfShoppingUseScore(tableNo);

            /**
             *抽奖 兑换使用
             */
            otherUseScore(tableNo);

            /**
             * 退订还点
             */
            shoppingReturnConsumeScore(tableNo);


            /**
             * 失效
             */
            recoveryGrantEffectScore(tableNo);


            /**
             * 绑定邮箱 绑定手机  签到
             */
            bindAndSignGrantScore(tableNo);

            /**
             * 评论送积分
             */
            commentGrantScore(tableNo);

            /**
             * 回收评论送的积分
             */
            recoveryCommentGrantScore(tableNo);

            /**
             * 飞牛赠送积分和飞牛回收积分
             */
            feiniuGrantAndRecoveryScore(tableNo);

            /**
             *购物使用积分 客人用来购买该厂商商品所使用的点数
             */
            shoppingToUseScore(tableNo);

            /**
             * 抽奖 兑换 使用积分
             */
            chouJangUseScore(tableNo);


        }
        Map<String, Object> result = new HashMap<>();
        result.put("shoppingGrantScore", summaryShoppingGrantScore());
        result.put("shoppingGrantScoreEffect", summaryShoppingGrantScoreEffect());
        result.put("selfShoppingUseScore", summarySelfShoppingUseScore());
        result.put("shoppingReturnConsumeScore", summaryShoppingReturnConsumeScore());
        result.put("recoveryGrantEffectScore", summaryRecoveryGrantEffectScore());
        result.put("bindAndSignGrantScore", summaryBindAndSignGrantScore());
        result.put("commentGrantScore", summaryCommentGrantScore());
        result.put("recoveryCommentGrantScore", summaryRecoveryCommentGrantScore());
        result.put("feiniuGrantAndRecoveryScore", summaryFeiniuGrantAndRecoveryScore());
        result.put("shoppingToUseScore", summaryShoppingToUseScore());
        result.put("chouJangUseScore",summaryChouJangUseScore());
        return result;
    }




    private void shoppingGrantScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> list = scoreFinancialReportDao.shoppingGrantScore(params);
        if (list.size() > 0) {
            shoppingGrantScoreList.addAll(list);
        }
    }

    private void bindAndSignGrantScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> list = scoreFinancialReportDao.bindAndSignGrantScore(params);
        if (list.size() > 0) {
            bindAndSignGrantScoreList.addAll(list);
        }
    }

    private void shoppingGrantScoreEffect(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("limitTime", yesterday);
        List<ScoreReportEntity> list = scoreFinancialReportDao.shoppingGrantScoreEffect(params);
        if (list.size() > 0) {
            shoppingGrantScoreEffectList.addAll(list);
        }
    }

    private void selfShoppingUseScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> lists = scoreFinancialReportDao.selfShoppingUseScore(params);
        if (lists.size() > 0) {
            selfShoppingUseScoreList.addAll(lists);
        }
    }

    private void otherUseScore(int tableNo){
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> lists = scoreFinancialReportDao.otherUseScore(params);
        if (lists.size() > 0) {
            selfShoppingUseScoreList.addAll(lists);
        }
    }

    private void shoppingReturnConsumeScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> lists = scoreFinancialReportDao.shoppingReturnConsumeScore(params);
        if (lists.size() > 0) {
            shoppingReturnConsumeScoreList.addAll(lists);
        }
    }


    private void recoveryGrantEffectScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> lists = scoreFinancialReportDao.recoveryGrantEffectScore(params);
        if (lists.size() > 0) {
            recoveryGrantEffectScoreList.addAll(lists);
        }
    }


    private void commentGrantScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> lists = scoreFinancialReportDao.commentGrantScore(params);
        if (lists.size() > 0) {
            commentGrantScoreList.addAll(lists);
        }
    }

    private void recoveryCommentGrantScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> lists = scoreFinancialReportDao.recoveryCommentGrantScore(params);
        if (lists.size() > 0) {
            recoveryCommentGrantScoreList.addAll(lists);
        }
    }

    private void shoppingToUseScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<ScoreReportEntity> lists = scoreFinancialReportDao.shoppingToUseScore(params);
        if (lists.size() > 0) {
            shoppingToUseScoreList.addAll(lists);
        }
    }
    private void chouJangUseScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<Map<String, Object>> listMap = scoreFinancialReportDao.chouJangUseScore(params);
        if(listMap.size()>0){
            chouJangUseScoreList.add(getChannelScore(listMap));
        }

    }

    private void feiniuGrantAndRecoveryScore(int tableNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableNo", tableNo);
        params.put("yesterday", yesterday);
        List<Map<String, Object>> listMap = scoreFinancialReportDao.feiniuGrantAndRecoveryScore(params);
        if(listMap.size()>0){
            feiniuGrantAndRecoveryScoreList.add(getChannelScore(listMap));
        }
    }

    private Map<Integer, Integer> getChannelScore(List<Map<String, Object>> listMap) {
        Map<Integer, Integer> scoreMap = new HashMap<>();
        for (Map<String, Object> map : listMap) {
            Integer channelBd = (Integer) map.get("channel");
            BigDecimal scoreBd = (BigDecimal) map.get("score");
            if (channelBd == null || scoreBd == null) {
                continue;
            }
            Integer channel = channelBd.intValue();
            Integer score = scoreBd.intValue();
            Integer haveScore = scoreMap.get(channel);
            if (haveScore == null) {
                scoreMap.put(channel, score);
            } else {
                scoreMap.put(channel, haveScore + score);
            }
        }
        return scoreMap;
    }


    private Map<ScoreReportEntity, Integer> summaryShoppingGrantScore() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : shoppingGrantScoreList) {
            Integer score = result.get(sst);
            if (sst.getSendScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getSendScore());
            } else {
                result.put(sst, sst.getSendScore() + score);
            }
            sst.setSendScore(null);
        }
        return result;
    }

    private Map<ScoreReportEntity, Integer> summaryShoppingGrantScoreEffect() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : shoppingGrantScoreEffectList) {
            Integer score = result.get(sst);
            if (sst.getEffectScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getEffectScore());
            } else {
                result.put(sst, sst.getEffectScore() + score);
            }
            sst.setEffectScore(null);
        }
        return result;
    }

    private Map<ScoreReportEntity, Integer> summarySelfShoppingUseScore() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : selfShoppingUseScoreList) {
            Integer score = result.get(sst);
            if (sst.getUseScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getUseScore());
            } else {
                result.put(sst, sst.getUseScore() + score);
            }
            sst.setUseScore(null);
        }
        return result;
    }

    private Map<ScoreReportEntity, Integer> summaryShoppingReturnConsumeScore() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : shoppingReturnConsumeScoreList) {
            Integer score = result.get(sst);
            if (sst.getReturnScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getReturnScore());
            } else {
                result.put(sst, sst.getReturnScore() + score);
            }
            sst.setReturnScore(null);
        }
        return result;
    }

    private Map<ScoreReportEntity, Integer> summaryRecoveryGrantEffectScore() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : recoveryGrantEffectScoreList) {
            Integer score = result.get(sst);
            if (sst.getInvalidScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getInvalidScore());
            } else {
                result.put(sst, sst.getInvalidScore() + score);
            }
            sst.setInvalidScore(null);
        }
        return result;
    }

    private Map<String, Integer> summaryBindAndSignGrantScore() {
        Map<String, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : bindAndSignGrantScoreList) {
            String scoreType = sst.getScoreType();
            Integer sendScore = sst.getSendScore();
            if (sendScore == null) {
                continue;
            }
            if (result.containsKey(scoreType)) {
                result.put(scoreType, result.get(scoreType) + sendScore);
            } else {
                result.put(scoreType, sendScore);
            }
        }
        return result;
    }

    private Map<ScoreReportEntity, Integer> summaryCommentGrantScore() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : commentGrantScoreList) {
            Integer score = result.get(sst);
            if (sst.getSendScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getSendScore());
            } else {
                result.put(sst, sst.getSendScore() + score);
            }
            sst.setSendScore(null);
        }
        return result;
    }

    private Map<ScoreReportEntity, Integer> summaryRecoveryCommentGrantScore() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : recoveryCommentGrantScoreList) {
            Integer score = result.get(sst);
            if (sst.getInvalidScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getInvalidScore());
            } else {
                result.put(sst, sst.getInvalidScore() + score);
            }
            sst.setInvalidScore(null);
        }
        return result;
    }


    private Map<ScoreReportEntity, Integer> summaryShoppingToUseScore() {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (ScoreReportEntity sst : shoppingToUseScoreList) {
            Integer score = result.get(sst);
            if (sst.getUseScore() == null) {
                continue;
            }
            if (score == null) {
                result.put(sst, sst.getUseScore());
            } else {
                result.put(sst, sst.getUseScore() + score);
            }
            sst.setUseScore(null);
        }
        return result;
    }

    private Map<Integer, Integer> summaryFeiniuGrantAndRecoveryScore() {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map<Integer, Integer> map : feiniuGrantAndRecoveryScoreList) {
            for (Integer key : map.keySet()) {
                Integer score = result.get(key);
                if (score == null) {
                    result.put(key, map.get(key));
                } else {
                    result.put(key, score + map.get(key));
                }
            }
        }
        return result;
    }

    private Map<Integer, Integer> summaryChouJangUseScore() {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map<Integer, Integer> map : chouJangUseScoreList) {
            for (Integer key : map.keySet()) {
                Integer score = result.get(key);
                if (score == null) {
                    result.put(key, map.get(key));
                } else {
                    result.put(key, score + map.get(key));
                }
            }
        }
        return result;
    }


}
