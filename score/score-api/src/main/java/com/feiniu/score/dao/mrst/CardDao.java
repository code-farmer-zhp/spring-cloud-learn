package com.feiniu.score.dao.mrst;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by yue.teng on 2016/3/24.
 */
public interface CardDao {
    int chkVoucherMount(String memGuid, String cardId);

    int chkBonusMount(String memGuid, String cardId);

    List<JSONObject> getCardNoListByVaSeq(String vaSeq, String memGuid);

    String getCardNoByVaSeq(String vaSeq, String memGuid);

    String takeBonusForPkad(String cardId, String memGuid, String dataForErrorLog);

    String takeVoucher(String cardId, String memGuid, String dataForErrorLog);

    String takeMailCoupon(String cardId, String memGuid, String dataForErrorLog);

    JSONArray getCardInfoByCardIdAndType(JSONArray cardInfoArr,boolean isBatch);

    JSONArray getTakenCardInfoByCardIdAndType(Set<String> takenCardSet,boolean isBatch);

    boolean takeBonusForUnionist(String memGuid, String tpLoginType, Date regOrBindTime, String dataForErrorLog, Integer errorType);
}
