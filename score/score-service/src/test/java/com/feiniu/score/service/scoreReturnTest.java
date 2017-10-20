package com.feiniu.score.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.TestBasicService;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.vo.OrderJsonVo;
import com.feiniu.score.vo.ReturnJsonVo;
import org.junit.Test;

public class scoreReturnTest extends TestBasicService{

    @Override
    public void doTest() {
    }

    @Test
    public void myTest(){
        //  String voStr=" {\"memGuid\": \"044A8094-7195-6620-AC07-BF75251F24B6\",\"provinceId\": \"涓婃捣\",\"ogSeq\": \"201601CO06035363\",\"ogNo\": \"201601CP06035363\",\"sourceMode\": \"1\",\"siteMode\": \"1\",\"self\": [{\"olSeq\": \"201601C0600310994\",\"itNo\": \"201508CG220000001\",\"qty\": 1,\"realPay\": 0,\"packageNo\": \"1\",\"smSeq\": \"201508CM220000001\",\"kind\": \"2\",\"card\": 0,\"coupons\": 0,\"vouchers\": 0,\"bonus\": 0,\"sellActivity\": 0,\"score\": 0,\"price\": 0,\"fdlSeq\": \"DG000001\",\"id\": \"1\",\"pId\": \"0\",\"aprnVvipPoints\": 0,\"aprnVvip\": 0,\"useBalancePoints\": 0,\"dividendDiscount\": 0,\"scoreMallDiscount\": 0},{\"olSeq\": \"201601C0600310995\",\"itNo\": \"201512CG250000011\",\"qty\": 6,\"realPay\": 588,\"packageNo\": \"2\",\"smSeq\": \"201512CM250000011\",\"kind\": \"1\",\"card\": 0,\"coupons\": 0,\"vouchers\": 0,\"bonus\": 0,\"sellActivity\": 0,\"score\": 0,\"price\": 98,\"fdlSeq\": \"DG000035\",\"id\": \"2\",\"pId\": \"0\",\"aprnVvipPoints\": 0,\"aprnVvip\": 0,\"useBalancePoints\": 0,\"dividendDiscount\": 0,\"scoreMallDiscount\": 0}],\"mall\": []}";
//		String voStr="{\"memGuid\": \"044A8094-7195-6620-AC07-BF75251F24B6\",\"provinceId\": \"上海\",\"ogSeq\": \"201601CO06035373\",\"ogNo\": \"201601CP06035373\",\"sourceMode\": \"1\",\"siteMode\": \"1\",\"self\": [{\"olSeq\": \"201601C0600311020\",\"itNo\": \"201508CG220000001\",\"qty\": 1,\"realPay\": 0,\"packageNo\": \"1\",\"smSeq\": \"201508CM220000001\",\"kind\": \"2\",\"card\": 0,\"coupons\": 0,\"vouchers\": 0,\"bonus\": 0,\"sellActivity\": 0,\"score\": 0,\"price\": 0,\"fdlSeq\": \"DG000001\",\"id\": \"1\",\"pId\": \"0\",\"aprnVvipPoints\": 0,\"aprnVvip\": 0,\"useBalancePoints\": 0,\"dividendDiscount\": 0,\"scoreMallDiscount\": 0},{\"olSeq\": \"201601C0600311021\",\"itNo\": \"201512CG230000014\",\"qty\": 1,\"realPay\": 499,\"packageNo\": \"2\",\"smSeq\": \"201512CM230000020\",\"kind\": \"1\",\"card\": 0,\"coupons\": 0,\"vouchers\": 0,\"bonus\": 0,\"sellActivity\": 0,"+
//			"\"score\": 0,\"price\": 499,\"fdlSeq\": \"DG000027\",\"id\": \"2\",\"pId\": \"0\",\"aprnVvipPoints\": 0,\"aprnVvip\": 0,\"useBalancePoints\": 0,\"dividendDiscount\": 0,\"scoreMallDiscount\": 0},{\"olSeq\": \"201601C0600311022\",\"itNo\": \"201512CG290000049\",\"qty\": 2,\"realPay\": 200,\"packageNo\": \"3\",\"smSeq\": \"201512CM290000062\",\"kind\": \"1\",\"card\": 0,\"coupons\": 0,\"vouchers\": 0,\"bonus\": 0,\"sellActivity\": 0,\"score\": 0,\"price\": 100,\"fdlSeq\": \"DG000035\",\"id\": \"3\",\"pId\": \"0\",\"aprnVvipPoints\": 0,\"aprnVvip\": 0,\"useBalancePoints\": 0,\"dividendDiscount\": 0,\"scoreMallDiscount\": 0}"+
//			"],\"mall\": [{\"ogsSeq\": \"201601COGS0610005484\",\"olsSeq\": \"201601COLS0610012503\",\"itNo\": \"100136342\",\"qty\": 1,\"realPay\": 300,\"packageNo\": \"4\",\"smSeq\": \"100136342\",\"kind\": \"0\",\"card\": 0,\"coupons\": 0,\"sellActivity\": 0,\"score\": 0,\"price\": 300,\"merchantId\": \"200204\",\"id\":\"100136342\",\"oversea\": 0,\"aprnVvipPoints\": 0,\"aprnVvip\": 0}]}";

        String voStr="{\"memGuid\":\"46B3CDB2-4E33-FE28-8A36-9B1C824C5CE1\",\"return\":{\"rgSeq\":\"201602CS01004929\",\"rgNo\":\"201602CQ01004929\",\"ogSeq\":\"201602CO01039255\",\"ogNo\":\"201602CP01039255\",\"mall\":null,\"self\":[{\"ogsSeq\":null,\"sellerNo\":null,\"quantity\":1,\"olSeq\":\"201602C0100321929\",\"itNo\":\"201512CG280000028\",\"rlSeq\":\"201602C0100004124\",\"packageNo\":\"1\",\"kind\":\"1\",\"card\":0,\"returnMoney\":35.0,\"returnScore\":50,\"price\":null,\"refundablePrice\":null},{\"ogsSeq\":null,\"sellerNo\":null,\"quantity\":1,\"olSeq\":\"201602C0100321930\",\"itNo\":\"201512CG280000028\",\"rlSeq\":\"201602C0100004125\",\"packageNo\":\"1\",\"kind\":\"2\",\"card\":0,\"returnMoney\":0,\"returnScore\":0,\"price\":null,\"refundablePrice\":null}]}}";
        JSONObject voJson=JSONObject.parseObject(voStr);
        System.out.println(voJson);
        ReturnJsonVo orderJsonVo=ReturnJsonVo.convertJson(voJson);
        scoreCommonDao.processReturnOrderScore("46B3CDB2-4E33-FE28-8A36-9B1C824C5CE1", orderJsonVo, "1");

/*
        ScoreMainLog scoreMainLogBuy = scoreMainLogDao.getScoreMainLog("46B3CDB2-4E33-FE28-8A36-9B1C824C5CE1", "201602CO01039255",0);
        System.out.println(scoreMainLogBuy);*/
    }
}
