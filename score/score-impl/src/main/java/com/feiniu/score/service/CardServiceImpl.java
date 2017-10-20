package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chao.zhang1 on 2016/11/16.
 */
@Service
public class CardServiceImpl implements CardService{
    public static final CustomLog log = CustomLog.getLogger(CardServiceImpl.class);
    @Autowired
    protected RestTemplate restTemplate;
    @Value("${batchCardTake.api}")
    private String batchTakeUrl;
    @Value("${batchCardTake.recharge.api}")
    private String batchRechargeTakeUrl;
    @Value("${batchCardTakeQuery.list.api}")
    private String batchTakeQueryUrl;

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

    private String RETURN_CODE="code"; //0 partly success, 1 all success ,2 failed,3 exception cause unknown
    private String TAKEN_MAP="takenMap";
    private String ERROR_REASON="errorReason";

    enum WarnStatusEnum {
        NOTBIND_24(24,99,"您没有绑定手机，无法领取该礼包~"),
        NOTBIND_7(7,99,"您没有绑定手机，无法领取该礼包~"),
        SUSPICIOUS(102,97,"尊敬的客户您好，您的账户存在异常，无法领取该礼包，如有疑问请致电客服。"),
        EMPLOYEE(9,98,"您是员工账户哦，无法领取该礼包~"),
        TAKENALREADY(4,33,"您已经领过了哦~"),
        //防刷，武汉PackageExchange接口  failMsg：非指定用户
        INVALIDMEMBER(90,22,"礼包堵在路上了，过会儿再来领吧~");

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public String getName() {
            return name;
        }


        private String name;
        private int code;
        private int resultCode;
        WarnStatusEnum(int code,int resultCode,String name) {
            this.name = name;
            this.resultCode=resultCode;
            this.code=code;
        }

        public static String getWarnByCode(int code){
            for(WarnStatusEnum warn:WarnStatusEnum.values()){
                if(code==warn.getCode()){
                    return warn.getName();
                }
            }
            return "领取礼包失败";
        }

        public static WarnStatusEnum  parseWarnStatusEnumByCode(int code){
            for(WarnStatusEnum warn:WarnStatusEnum.values()){
                if(code==warn.getCode()){
                   return warn;
                }
            }
            return null;
        }

    }
    @Override
    public JSONObject batchGetCardByCardseq(String guid,JSONArray cardInfo) {
        JSONObject returnJSON = new JSONObject();
        String cardInfoArrayString=JSONArray.toJSONString(cardInfo);
        //convert [ { "couponid": "SC1dd1a4a2-daef-4d64-bff8-c51baa0caef8", "seq": "zc222", "cardtype": "22" }] to [ { "card_id": "SC1dd1a4a2-daef-4d64-bff8-c51baa0caef8", "seq": "zc222", "cardtype": "22" } ]
        //when do query "cardtype": "22" is not necessary
        cardInfoArrayString=cardInfoArrayString.replace("couponid","card_id");
        JSONArray cardInfoArray=JSONObject.parseArray(cardInfoArrayString);
        returnJSON.put(RETURN_CODE, 3);
        returnJSON.put(TAKEN_MAP, new HashMap());
        returnJSON.put(ERROR_REASON, null);
        try {
            //0 partly succeed or query error,1 all succeed ,2 alll failed,3 exception cause unknown
            String cardIdStr = "";
            Map<String, String> takenSeqMap = new HashMap<String, String>();

            MultiValueMap<String, Object> paramsVaMount = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonVaMount = new JSONObject();
            jsonVaMount.put("cards", cardInfoArray);
            jsonVaMount.put("guid", guid);
            paramsVaMount.add("param", jsonVaMount.toString());
            String takenInfoJsonStr = restTemplate.postForObject(batchTakeQueryUrl,
                    paramsVaMount, String.class);
            JSONObject takenInfoJson = (JSONObject) JSONObject.parse(takenInfoJsonStr);
            if (takenInfoJson != null && takenInfoJson.getString(RETURN_CODE) != null && takenInfoJson.getString(RETURN_CODE).equals("0")) {
                JSONArray cardnoArr = takenInfoJson.getJSONArray("data");
                if (cardnoArr != null && !cardnoArr.isEmpty()) {
                    int successCount=0;
                    int failedCount=0;
                    for (int i = 0; i < cardnoArr.size(); i++) {
                        JSONObject cardnoJson = cardnoArr.getJSONObject(i);
                        if (cardnoJson.getBoolean("success")) {
                            String takenCardSeq = cardnoJson.getString("cardNo");
                            String seq = cardnoJson.getString("seq");
                            if (!StringUtils.isEmpty(takenCardSeq)) {
                                takenSeqMap.put(seq,takenCardSeq);
                                successCount++;
                            }
                        } else {
                            failedCount++;
                        }
                    }
                    if(successCount==cardnoArr.size()){
                        returnJSON.put(RETURN_CODE,1);
                    }else if(failedCount==cardnoArr.size()){
                        returnJSON.put(RETURN_CODE,2);
                        returnJSON.put(ERROR_REASON,"未完全领取礼包！");
                    }
                    returnJSON.put(TAKEN_MAP,takenSeqMap);
                } else {
                    returnJSON.put(RETURN_CODE, 2);
                    log.error("batch query taken card info return data error", "batchGetCardByCardId");
                    returnJSON.put(ERROR_REASON,"未完全领取礼包！");
                    return returnJSON;
                }
            } else {
                returnJSON.put(RETURN_CODE, 2);
                returnJSON.put(ERROR_REASON,"未完全领取礼包！");
                log.error("batch query taken card info return code error", "batchGetCardByCardId");
                return returnJSON;
            }

        } catch (Exception e) {
            returnJSON.put(RETURN_CODE, 3);
            returnJSON.put(ERROR_REASON,"未完全领取礼包！");
            log.error("runtiem exception", "batchGetCardByCardseq",e);
        }
        //没有领取卡号，算全部成功
        if(cardInfo.size()==0){
            returnJSON.put(RETURN_CODE, 1);
        }
        return returnJSON;
    }

    @Override
    public JSONObject batchTakeCards(String memGuid, JSONArray cardInfo,String dataForErrorLog,boolean ifRechaege) {
        JSONObject returnJSON=new JSONObject();
        returnJSON.put(RETURN_CODE, 2);
        returnJSON.put(TAKEN_MAP, new JSONObject());
        returnJSON.put(ERROR_REASON,null);
        //防止超时后job还没有扫描到，先校验在领取
        JSONObject returnJson=batchGetCardByCardseq(memGuid,cardInfo);
        JSONObject takenJson=returnJson.getJSONObject(TAKEN_MAP);
        //filter already taken by query api
        if(takenJson.size()>0){
            Iterator<Object> it=cardInfo.iterator();
            while(it.hasNext()){
                JSONObject card=(JSONObject) it.next();
                String seq=card.getString("seq");
                if(!StringUtils.isEmpty(takenJson.getString(seq))){
                    it.remove();
                }
            }
        }
        // if no need to recharge ,populate timeout taken
        if(cardInfo.size()==0&&takenJson.size()>0){
            returnJSON.put(RETURN_CODE, 1);
            returnJSON.put(TAKEN_MAP,takenJson);
            return returnJSON;
        }

        MultiValueMap<String, Object> paramsVa = new LinkedMultiValueMap<>();
        JSONObject paramJson=new JSONObject();
        paramJson.put("guid",memGuid);
        paramJson.put("card",cardInfo);
        paramsVa.add("param", paramJson.toString());
        String result=null;
        String takeUrl=batchTakeUrl;
        if(ifRechaege){
            takeUrl=batchRechargeTakeUrl;
        }
        try {
            result = restTemplate.postForObject(takeUrl,
                    paramsVa, String.class);
        } catch (Exception e) {
            log.error("批量卡券领取接口出现错误," + cardInfo.toJSONString(), "batchTakeCards", e);
            //领取接口错误的话(主要是超时错误)，用接口查询是否已领取成功
            returnJSON=batchGetCardByCardseq(memGuid,cardInfo);
            //如果由于领取接口异常（超时）
            if((int)returnJSON.get(RETURN_CODE)==3){
                scoreExceptionHandler.handlerGrowthException(e, memGuid, dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
            }
            return returnJSON;
        }

        JSONObject returnObj=null;
        if(result!=null) {
            try {
                returnObj = JSONObject.parseObject(result);
            } catch (Exception e) {
                log.error("result parse to JSONObject error!");
                return returnJSON;
            }
            JSONArray successArray = returnObj.getJSONArray("success");
            if(successArray !=null&& successArray.size()>0){
                Map<String,String> seqReturnSeqMap=new HashMap<>();
                for(int i=0;i<successArray.size();i++){
                    JSONObject cardInfoJson=successArray.getJSONObject(i);
                    String seq=cardInfoJson.getString("seq");
                    String cardNo=cardInfoJson.getString("cardNo");
                    if(cardInfoJson.getInteger(RETURN_CODE)==0&&!StringUtils.isEmpty(seq)&&!StringUtils.isEmpty(cardNo)){
                        seqReturnSeqMap.put(seq,cardNo);
                    }
                }
                if(takenJson.size()>0){
                    //add timeout query takenseqs
                    takenJson.putAll(seqReturnSeqMap);
                    returnJSON.put(TAKEN_MAP,takenJson);
                }else{
                    returnJSON.put(TAKEN_MAP,seqReturnSeqMap);
                }
            }
            JSONArray failedArray=returnObj.getJSONArray("fail");
            if(failedArray!=null&&failedArray.size()>0){
                //show the first reason why failed
                boolean firstFailedSet=false;
                for(int j=0;j<failedArray.size();j++){
                    JSONObject failedJson=failedArray.getJSONObject(j);
                    String code=failedJson.getString("code");
                    WarnStatusEnum warnStatusEnum=WarnStatusEnum.parseWarnStatusEnumByCode(Integer.parseInt(code));
                    if(warnStatusEnum!=null&&!firstFailedSet){
                        returnJSON.put(ERROR_REASON,warnStatusEnum);
                        firstFailedSet=true;
                    }
                    log.error("card seq: "+failedJson.getString("seq")+", failedMsg: "+failedJson.getString("failMsg"));
                }
            }
            if(failedArray.size()==0){
                returnJSON.put(RETURN_CODE,1);
            }
            if(failedArray.size()>0&&successArray.size()==0){
                returnJSON.put(RETURN_CODE,2);
            }
            if(failedArray.size()>0&&successArray.size()>0){
                returnJSON.put(RETURN_CODE,0);
            }
        }else{
            scoreExceptionHandler.handlerGrowthException(new ScoreException("batch take API return null!"), memGuid, dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
        }
        return returnJSON;
    }

    public static void main(String[] args){
//        try {
//            JSONArray cardInfoArray=JSONArray.parseArray(CustomFileUtil.readFile("E:\\score_for_release\\score-impl\\src\\main\\java\\com\\feiniu\\score\\service\\handledJson.json","UTF-8"));
//            CardServiceImpl cardService=new CardServiceImpl();
//            cardService.batchTakeCards("9C0EC643-52B6-574F-B9EE-8A5B43256BC9",cardInfoArray,"",false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
