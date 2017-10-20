package com.feiniu.score.service;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreFinancialReportDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ScoreReportEntity;
import com.feiniu.score.util.ShardUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ReportDbService {
    @Autowired
    private ScoreFinancialReportDao scoreFinancialReportDao;

    @Value("${yesterday}")
    private String yesterdays;

    public void start() {
        try {
            if (StringUtils.isNotEmpty(yesterdays)) {
                if (yesterdays.contains("|")) {
                    String[] days = yesterdays.split("\\|");
                    String start = days[0];
                    String end = days[days.length - 1];
                    FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");
                    Date startDate = sdf.parse(start);
                    Date endDate = sdf.parse(end);
                    do {
                        startOneByOne(sdf.format(startDate));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(startDate);
                        calendar.add(Calendar.DATE, 1);
                        startDate = calendar.getTime();
                    } while (startDate.before(endDate));
                } else if (yesterdays.contains(",")) {
                    String[] days = yesterdays.split(",");
                    for (String day : days) {
                        startOneByOne(day);
                    }

                } else {
                    startOneByOne(yesterdays);
                }
            } else {
                startOneByOne(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startOneByOne(String yesterday) {
        int dbCount = ShardUtils.getDbCount();
        ExecutorService service = Executors.newFixedThreadPool(dbCount);
        try {
            List<Future<Map<String, Object>>> lists = new ArrayList<>();
            for (int i = 0; i < dbCount; i++) {
                //数据源
                String dateSourceName = DataSourceUtils.DATASOURCE_BASE_NAME + i;
                if (StringUtils.isEmpty(yesterday)) {
                    FastDateFormat yyyyMMdd = FastDateFormat.getInstance("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -1);
                    yesterday = yyyyMMdd.format(calendar.getTime());
                }
                Future<Map<String, Object>> submit = service.submit(new ReportTableService(dateSourceName, scoreFinancialReportDao, yesterday));
                lists.add(submit);
            }

            //***************************购物类型********************************
            //收集购物发送列
            Map<ScoreReportEntity, Integer> resultMap = summaryShoppingGrantScore(lists);
            //填充购物发送列
            Set<ScoreReportEntity> shoppingResultSet = mergetShoppingSend(resultMap);
            //收集购物生效列
            Map<ScoreReportEntity, Integer> effectScore = summaryShoppingGrantScoreEffect(lists);

            mergeShoppingEffect(shoppingResultSet, effectScore);

            //收集购物使用列（其中也包括评论使用列）
            Map<ScoreReportEntity, Integer> useScore = summarySelfShoppingUseScore(lists);
            mergeShoppingUse(shoppingResultSet, useScore);

            //收集购物退订返点列（其中包括返还评论的）
            Map<ScoreReportEntity, Integer> returnScore = summaryShoppingReturnConsumeScore(lists);

            mergeShoppingReturnConsumeScore(shoppingResultSet, returnScore);

            //购物积分生效后 退货回收发放的积分。回收发放的积分中包括评论获得，购买获得
            Map<ScoreReportEntity, Integer> invalidScore = summaryRecoveryGrantEffectScore(lists);

            mergeRecoveryGrantEffectScore(shoppingResultSet, invalidScore);
            if (shoppingResultSet.size() > 0) {
                if (StringUtils.isNotEmpty(yesterday)) {
                    for (ScoreReportEntity sre : shoppingResultSet) {
                        sre.setEdate(yesterday);
                    }
                }
                scoreFinancialReportDao.saveReport(new ArrayList<>(shoppingResultSet));
            }
            //***************************绑定邮箱 手机 签到获得积分********************************
            List<ScoreReportEntity> bindAndSignList = summaryBindAndSignGrantScore(lists);
            if (bindAndSignList.size() > 0) {
                if (StringUtils.isNotEmpty(yesterday)) {
                    for (ScoreReportEntity sre : bindAndSignList) {
                        sre.setEdate(yesterday);
                    }
                }
                scoreFinancialReportDao.saveReport(bindAndSignList);
            }
            //****************************评论获得的积分**************************************************
            Set<ScoreReportEntity> commentGrantScoreList = summaryCommentGrantScore(lists);
            //***************************退货回收评论的积分*************************
            Map<ScoreReportEntity, Integer> recoveryCommentGrantScoreList = summaryRecoveryCommentGrantScore(lists);
            //和评论获得的积分合并
            mergetRecoveryCommentGrantScore(commentGrantScoreList, recoveryCommentGrantScoreList);
            if (commentGrantScoreList.size() > 0) {
                if (StringUtils.isNotEmpty(yesterday)) {
                    for (ScoreReportEntity sre : commentGrantScoreList) {
                        sre.setEdate(yesterday);
                    }
                }
                scoreFinancialReportDao.saveReport(new ArrayList<>(commentGrantScoreList));
            }
            //*************************飞牛赠送积分和飞牛回收积分（失效）*****************************************
            List<ScoreReportEntity> feiniuScoreList = summaryFeiniuGrantAndRecoveryScore(lists);
            if (feiniuScoreList.size() > 0) {
                if (StringUtils.isNotEmpty(yesterday)) {
                    for (ScoreReportEntity sre : feiniuScoreList) {
                        sre.setEdate(yesterday);
                    }
                }
                scoreFinancialReportDao.saveReport(feiniuScoreList);
            }
            //*************************积分使用*********************************************
            Map<ScoreReportEntity, Integer> shoppingToUseScoreList = summaryShoppingToUseScore(lists);
            List<ScoreReportEntity> scoreToUseEntities = mergetShoppingToUseScore(shoppingToUseScoreList);
            if (scoreToUseEntities.size() > 0) {
                if (StringUtils.isNotEmpty(yesterday)) {
                    for (ScoreReportEntity sre : scoreToUseEntities) {
                        sre.setEdate(yesterday);
                    }
                }
                scoreFinancialReportDao.saveUseReport(scoreToUseEntities);
            }
            //**************************抽奖和兑换积分使用***********************************
            List<ScoreReportEntity> chouJangUseScoreList = summaryChouJangUseScore(lists);
            if (chouJangUseScoreList.size() > 0) {
                if (StringUtils.isNotEmpty(yesterday)) {
                    for (ScoreReportEntity sre : chouJangUseScoreList) {
                        sre.setEdate(yesterday);
                    }
                }
                scoreFinancialReportDao.saveUseReport(chouJangUseScoreList);
            }
        } catch (Exception e) {
            //有一个失败则任务失败
            service.shutdownNow();
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }


    private Map<ScoreReportEntity, Integer> summaryShoppingGrantScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        return summaryCommon(lists, "shoppingGrantScore");
    }

    private Map<ScoreReportEntity, Integer> summaryShoppingGrantScoreEffect(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        return summaryCommon(lists, "shoppingGrantScoreEffect");
    }

    private Map<ScoreReportEntity, Integer> summarySelfShoppingUseScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        return summaryCommon(lists, "selfShoppingUseScore");
    }

    private Map<ScoreReportEntity, Integer> summaryShoppingReturnConsumeScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        return summaryCommon(lists, "shoppingReturnConsumeScore");
    }

    private Map<ScoreReportEntity, Integer> summaryRecoveryGrantEffectScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        return summaryCommon(lists, "recoveryGrantEffectScore");
    }


    private List<ScoreReportEntity> summaryBindAndSignGrantScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        Map<String, Integer> resultSummary = new HashMap<>();
        for (Future<Map<String, Object>> future : lists) {
            Map<String, Object> resultMap = future.get();
            @SuppressWarnings("unchecked")
            Map<String, Integer> map = (Map<String, Integer>) resultMap.get("bindAndSignGrantScore");
            for (String key : map.keySet()) {
                Integer score = map.get(key);
                if (resultSummary.containsKey(key)) {
                    resultSummary.put(key, resultSummary.get(key) + score);
                } else {
                    resultSummary.put(key, score);
                }
            }
        }
        List<ScoreReportEntity> list = new ArrayList<>();
        for (String key : resultSummary.keySet()) {
            Integer score = resultSummary.get(key);
            ScoreReportEntity sre = new ScoreReportEntity();
            sre.setSendScore(score);
            sre.setEffectScore(score);
            sre.setScoreType(key);
            sre.setSourceType(1);
            list.add(sre);
        }
        return list;
    }

    private Set<ScoreReportEntity> summaryCommentGrantScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        Map<ScoreReportEntity, Integer> commentGrantScoreMap = summaryCommon(lists, "commentGrantScore");
        Set<ScoreReportEntity> set = new HashSet<>();
        for (ScoreReportEntity key : commentGrantScoreMap.keySet()) {
            Integer sendScore = commentGrantScoreMap.get(key);
            key.setSendScore(sendScore);
            key.setEffectScore(sendScore);
            key.setScoreType(Constant.COMMENT);
            set.add(key);
        }
        return set;

    }

    private Map<ScoreReportEntity, Integer> summaryRecoveryCommentGrantScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        return summaryCommon(lists, "recoveryCommentGrantScore");
    }

    private Map<ScoreReportEntity, Integer> summaryShoppingToUseScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        return summaryCommon(lists, "shoppingToUseScore");
    }

    private Map<ScoreReportEntity, Integer> summaryCommon(List<Future<Map<String, Object>>> lists, String majorKey) throws InterruptedException, ExecutionException {
        Map<ScoreReportEntity, Integer> result = new HashMap<>();
        for (Future<Map<String, Object>> future : lists) {
            Map<String, Object> resultMap = future.get();
            @SuppressWarnings("unchecked")
            Map<ScoreReportEntity, Integer> statisticsScore = (Map<ScoreReportEntity, Integer>) resultMap.get(majorKey);
            for (ScoreReportEntity key : statisticsScore.keySet()) {
                Integer resultScore = result.get(key);
                Integer score = statisticsScore.get(key);
                if (resultScore == null) {
                    result.put(key, score);
                } else {
                    result.put(key, score + resultScore);
                }
            }
        }
        return result;
    }

    private List<ScoreReportEntity> summaryFeiniuGrantAndRecoveryScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        List<ScoreReportEntity> result = new ArrayList<>();
        Map<Integer, Integer> tmpMap = new HashMap<>();
        for (Future<Map<String, Object>> future : lists) {
            Map<String, Object> resultMap = future.get();
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> scoreMap = (Map<Integer, Integer>) resultMap.get("feiniuGrantAndRecoveryScore");
            for (Integer key : scoreMap.keySet()) {
                Integer score = tmpMap.get(key);
                if (score == null) {
                    tmpMap.put(key, scoreMap.get(key));
                } else {
                    tmpMap.put(key, score + scoreMap.get(key));
                }
            }

        }
        if (tmpMap.size() > 0) {
            ScoreReportEntity sre = new ScoreReportEntity();
            sre.setScoreType(Constant.FEI_NIU);
            for (Integer key : tmpMap.keySet()) {

                if (Constant.SCORE_CHANNEL_APPROVAL.equals(key)) {
                    //客服赠送
                    feiniuSend(tmpMap, sre, key);
                } else if (Constant.SCORE_CHANNEL_CRM_GIVE.equals(key)) {
                    //CRM赠送积分
                    feiniuSend(tmpMap, sre, key);
                } else if (Constant.SCORE_CHANNEL_CRM_RECOVER.equals(key)) {
                    //CRM回收积分
                    feiniuInvalid(tmpMap, sre, key);
                } else if (Constant.SCORE_CHANNEL_RAFFLE_GIVE.equals(key)) {
                    //抽奖获得
                    feiniuSend(tmpMap, sre, key);
                } else if (Constant.SCORE_CHANNEL_FILL_IN_INTEREST.equals(key)) {
                    //填写爱好赠送
                    feiniuSend(tmpMap, sre, key);
                }
            }
            result.add(sre);
        }
        return result;
    }

    private List<ScoreReportEntity> summaryChouJangUseScore(List<Future<Map<String, Object>>> lists) throws ExecutionException, InterruptedException {
        List<ScoreReportEntity> result = new ArrayList<>();
        Map<Integer, Integer> tmpMap = new HashMap<>();
        for (Future<Map<String, Object>> future : lists) {
            Map<String, Object> resultMap = future.get();
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> scoreMap = (Map<Integer, Integer>) resultMap.get("chouJangUseScore");
            for (Integer key : scoreMap.keySet()) {
                Integer score = tmpMap.get(key);
                if (score == null) {
                    tmpMap.put(key, scoreMap.get(key));
                } else {
                    tmpMap.put(key, score + scoreMap.get(key));
                }
            }

        }
        if (tmpMap.size() > 0) {
            Map<String, ScoreReportEntity> chouJangAndDuiHuanMap = new HashMap<>();

            for (Integer key : tmpMap.keySet()) {

                if (Constant.SCORE_CHANNEL_RAFFLE_COST.equals(key)) {
                    //抽奖使用
                    ScoreReportEntity sre = chouJangAndDuiHuanMap.get(Constant.CHOU_JANG);
                    chouJangAndDuiHuanMap.put(Constant.CHOU_JANG,feiniuUse(tmpMap, sre, key,Constant.CHOU_JANG));
                } else if (Constant.SCORE_CHANNEL_EXCHANGE_GOODS_COST.equals(key)
                        || Constant.SCORE_CHANNEL_EXCHANGE_CARD_COST.equals(key)
                        || Constant.SCORE_CHANNEL_EXCHANGE_VOUCHER_COST.equals(key)) {
                    //兑换商品消耗
                    ScoreReportEntity sre = chouJangAndDuiHuanMap.get(Constant.DUI_HUAN);
                    chouJangAndDuiHuanMap.put(Constant.DUI_HUAN,feiniuUse(tmpMap, sre, key,Constant.DUI_HUAN));
                }
            }
            result.addAll(chouJangAndDuiHuanMap.values());
        }
        return result;
    }

    private void feiniuInvalid(Map<Integer, Integer> tmpMap, ScoreReportEntity sre, Integer key) {
        Integer invalidScore = sre.getInvalidScore();
        if (invalidScore == null) {
            sre.setInvalidScore(Math.abs(tmpMap.get(key)));
        } else {
            sre.setInvalidScore(invalidScore + Math.abs(tmpMap.get(key)));
        }
    }

    private void feiniuSend(Map<Integer, Integer> tmpMap, ScoreReportEntity sre, Integer key) {
        Integer sendScore = sre.getSendScore();
        if (sendScore == null) {
            sre.setSendScore(tmpMap.get(key));
            sre.setEffectScore(tmpMap.get(key));
        } else {
            sre.setSendScore(sendScore + tmpMap.get(key));
            sre.setEffectScore(sendScore + tmpMap.get(key));
        }
    }

    private ScoreReportEntity feiniuUse(Map<Integer, Integer> tmpMap, ScoreReportEntity sre, Integer key,String type) {
        if (sre == null) {
            sre = new ScoreReportEntity();
            sre.setScoreType(type);
            sre.setUseScore(Math.abs(tmpMap.get(key)));
        } else {
            sre.setUseScore(sre.getUseScore()+Math.abs(tmpMap.get(key)));
        }
        return sre;
    }

    private Set<ScoreReportEntity> mergetShoppingSend(Map<ScoreReportEntity, Integer> resultMap) {
        Set<ScoreReportEntity> set = new HashSet<>();
        for (ScoreReportEntity key : resultMap.keySet()) {
            Integer sendScore = resultMap.get(key);
            key.setSendScore(sendScore);
            key.setScoreType(Constant.REPORT_SHOPPING);
            set.add(key);
        }
        return set;
    }

    private List<ScoreReportEntity> mergetShoppingToUseScore(Map<ScoreReportEntity, Integer> resultMap) {
        List<ScoreReportEntity> list = new LinkedList<>();
        for (ScoreReportEntity key : resultMap.keySet()) {
            Integer useScore = resultMap.get(key);
            key.setUseScore(useScore);
            key.setScoreType(Constant.REPORT_SHOPPING);
            list.add(key);
        }
        return list;
    }

    //填充生效列
    private void mergeShoppingEffect(Set<ScoreReportEntity> result, Map<ScoreReportEntity, Integer> effectScoreMap) {
        for (ScoreReportEntity key : effectScoreMap.keySet()) {
            Integer effectScore = effectScoreMap.get(key);
            if (result.contains(key)) {
                for (ScoreReportEntity scoreReportEntity : result) {
                    if (scoreReportEntity.equals(key)) {
                        scoreReportEntity.setEffectScore(effectScore);
                    }
                }
            } else {
                key.setScoreType(Constant.REPORT_SHOPPING);
                key.setEffectScore(effectScore);
                result.add(key);
            }

        }
    }

    private void mergeShoppingUse(Set<ScoreReportEntity> result, Map<ScoreReportEntity, Integer> useScoreMap) {
        //自营0 商城2
        Set<String> shoppingUseSet = new HashSet<>(Arrays.asList("0", "2"));
        for (ScoreReportEntity key : useScoreMap.keySet()) {
            Integer useScore = useScoreMap.get(key);
            if (result.contains(key)) {
                for (ScoreReportEntity scoreReportEntity : result) {
                    if (scoreReportEntity.equals(key) && shoppingUseSet.contains(key.getScoreType())) {
                        scoreReportEntity.setUseScore(useScore);
                    }
                }
            } else {
                if (shoppingUseSet.contains(key.getScoreType())) {
                    key.setScoreType(Constant.REPORT_SHOPPING);
                    key.setUseScore(useScore);
                    result.add(key);
                }
            }

        }
    }

    private void mergeShoppingReturnConsumeScore(Set<ScoreReportEntity> result, Map<ScoreReportEntity, Integer> returnScoreMap) {
        //自营0 商城2
        Set<String> returnScoreSet = new HashSet<>(Arrays.asList("0", "2"));
        for (ScoreReportEntity key : returnScoreMap.keySet()) {
            Integer returnScore = returnScoreMap.get(key);
            if (result.contains(key)) {
                for (ScoreReportEntity scoreReportEntity : result) {
                    if (scoreReportEntity.equals(key) && returnScoreSet.contains(key.getScoreType())) {
                        scoreReportEntity.setReturnScore(returnScore);
                    }
                }
            } else {
                if (returnScoreSet.contains(key.getScoreType())) {
                    key.setScoreType(Constant.REPORT_SHOPPING);
                    key.setReturnScore(returnScore);
                    result.add(key);
                }
            }

        }
    }

    private void mergeRecoveryGrantEffectScore(Set<ScoreReportEntity> result, Map<ScoreReportEntity, Integer> invalidScoreMap) {
        //自营0 商城2
        Set<String> invalidScoreSet = new HashSet<>(Arrays.asList("0", "2"));
        for (ScoreReportEntity key : invalidScoreMap.keySet()) {
            Integer invalidScore = invalidScoreMap.get(key);
            if (result.contains(key)) {
                for (ScoreReportEntity scoreReportEntity : result) {
                    if (scoreReportEntity.equals(key) && invalidScoreSet.contains(key.getScoreType())) {
                        scoreReportEntity.setInvalidScore(invalidScore);
                    }
                }
            } else {
                if (invalidScoreSet.contains(key.getScoreType())) {
                    key.setScoreType(Constant.REPORT_SHOPPING);
                    key.setInvalidScore(invalidScore);
                    result.add(key);
                }
            }

        }
    }

    //填充失效
    private void mergetRecoveryCommentGrantScore(Set<ScoreReportEntity> grant, Map<ScoreReportEntity, Integer> recovery) {

        for (ScoreReportEntity key : recovery.keySet()) {
            Integer invalidScore = recovery.get(key);
            if (grant.contains(key)) {
                for (ScoreReportEntity scoreReportEntity : grant) {
                    if (scoreReportEntity.equals(key)) {
                        scoreReportEntity.setInvalidScore(invalidScore);
                    }
                }
            } else {
                key.setScoreType(Constant.COMMENT);
                key.setInvalidScore(invalidScore);
                grant.add(key);
            }

        }

    }

    public static void main(String[] args) throws ParseException {
        String yesterdays = "2015-10-01|2015-10-06";
        if (yesterdays.contains("|")) {
            String[] days = yesterdays.split("\\|");
            System.out.println(Arrays.toString(days));
            String start = days[0];
            String end = days[days.length - 1];
            FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            do {
                System.out.println(sdf.format(startDate));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.DATE, 1);
                startDate = calendar.getTime();
            } while (startDate.before(endDate));
        }
    }


}
