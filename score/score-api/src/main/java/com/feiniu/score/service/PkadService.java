package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.feiniu.score.dto.Result;
import com.feiniu.score.entity.mrst.Pkad;

import java.util.Date;
import java.util.Map;

public interface PkadService {


	String getkafkafromCRM(String memGuid, String data);

	Result takePkadByPkadSeqAndMembId(String memGuid, String data);

//	Result getExpiredPkadListByMembId(String memGuid, String data);
//
//	Result getTakenPkadListByMembId(String memGuid, String data);
//
//	Result getNoTakePkadListByMembId(String memGuid, String data);

	//Map<String,Object> takePkadUseOtherAPI(String memGuid, Pkad pkad, String cardSeq, String cardId, String cardType, String dataForErrorLog);

//	Map<String,Object> takePkadUseOtherAPI(String memGuid, Pkad pkad, String cardSeq,String cardType, String dataForErrorLog);
	Map<String,Object> takePkad(String memGuid, Pkad pkad, String cardSeq, String cardType, String dataForErrorLog);

	Map<String,Object> RechargePkadUseOtherAPI(String memGuid, Pkad pkad,String dataForErrorLog);

	Result getCardInfo(String memGuid, String data);

	Result getPkadListBySel(String memGuid, String data);

	Result getPkadListBySelCount(String memGuid, String data);

	Result getMrstUiListBySel(String memGuid, String data);

    Result getCardInfoBatch(String memGuid, String data);

    Result getPkadListBySelForERP(String memGuid, String data);

	void saveTakePkadTokafkaForCRM(String memGuid, Pkad pkad, Date now, JSONArray cardInfo);

	Result getPkadCountBySelForERP(String memGuid, String data);

	void processOutTimePkadLog(String memGuid, String pkadSeq);

	Result getTakenCardInfo(String memGuid, String data);

    Result getTakenCardInfoBatch(String memGuid, String data);

    Result getLastPkad(String memGuid);
}
