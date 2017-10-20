package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by chao.zhang1 on 2016/11/16.
 */
public interface CardService {
    JSONObject batchGetCardByCardseq(String guid, JSONArray cardInfo);

    JSONObject batchTakeCards(String memGuid, JSONArray cardInfo,String dataForErrorLog,boolean ifRecharge);
}
