package com.feiniu.score.dao.mrst;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.*;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.Mcrypt3Des;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by yue.teng on 2016/3/24.
 */
@Repository
public class CardDaoImpl implements CardDao{
    public static final CustomLog log = CustomLog.getLogger(CardDaoImpl.class);

    @Autowired
    private CacheUtils cacheUtils;

    @Value("${bonusTake.api}")
    private String bonusTakeApi;
    @Value("${voucherTake.api}")
    private String voucherTakeApi;
    @Value("${voucherMount.api}")
    private String voucherMountApi;
    @Value("${bonusMount.api}")
    private String bonusMountApi;
    @Value("${getCardInfo.api}")
    private String getCardInfoApi;
    @Value("${cardno.list.api}")
    private String cardnoListApi;
    @Value("${mailCouponTake.api}")
    private String mailCouponTakeApi;

    @Value("${needEnActID}")
    private String needEnActID;
    @Value("${encodeActID}")
    private String encodeActID;

    @Value("${unionist.tplogin.type}")
    private String unionistTploginType;
    @Value("${unionist.activity.ids}")
    private String bonusActivityIds;
    @Value("${unionist.activity.time}")
    private String unionistActivityTime;

    @Autowired
    @Qualifier("restTemplateSmallTimeout")
    protected RestTemplate restTemplateSmallTimeout;

    @Autowired
    private PkadDao pkadDao;
    @Autowired
    protected RestTemplate restTemplate;
    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;
    @Override
    public int chkVoucherMount(String memGuid, String cardId){
        int vaMount=0;
        MultiValueMap<String, Object> paramsVaMount = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonVaMount = new JSONObject();
        jsonVaMount.put("vaSeq", cardId);
        jsonVaMount.put("memberGuid", memGuid);
        paramsVaMount.add("param", jsonVaMount.toString());
        String vaMountDeResult = restTemplate.postForObject(voucherMountApi,
                paramsVaMount, String.class);
        JSONObject vaMountObj = (JSONObject) JSONObject.parse(vaMountDeResult);
        if(vaMountObj!=null&&vaMountObj.getString("code")!=null&&vaMountObj.getString("code").equals("200")){
            JSONObject body=vaMountObj.getJSONObject("body");
            if(body!=null&&!body.isEmpty()){
                String vaMountStr=body.getString("mount");
                if(!StringUtils.isEmpty(vaMountStr)&& NumberUtils.isNumber(vaMountStr)&&Integer.parseInt(vaMountStr)>0){
                    vaMount=Integer.parseInt(vaMountStr);
                    log.info("领取优惠券成功,cardId" + cardId + ",memGuid=" + memGuid + ",vaMount=" + vaMountStr,"chkVoucherMount");
                }else{
                    log.info("领取优惠券失败,cardId" + cardId + ",memGuid=" + memGuid + ",vaMount="+vaMountStr,"chkVoucherMount");
                    throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"领取优惠券失败");
                }
            }else{
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"查询优惠券领取数量失败");
            }
        }else{
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"查询优惠券领取数量失败");
        }
        return vaMount;
    }

    @Override
    public int chkBonusMount(String memGuid,String cardId){
        Integer boMount=0;
        MultiValueMap<String, Object> paramsBoMount = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonBoMount = new JSONObject();
        jsonBoMount.put("couponId", cardId);
        jsonBoMount.put("memberGuid", memGuid);
        paramsBoMount.add("data", jsonBoMount.toString());
        String boMountDeResult = restTemplate.postForObject(bonusMountApi,
                paramsBoMount, String.class);
        JSONObject boMountObj = (JSONObject) JSONObject.parse(boMountDeResult);
        if(boMountObj!=null&&boMountObj.getString("code")!=null&&boMountObj.getString("code").equals("200")){
                boMount=boMountObj.getInteger("data");
                if(boMount!=null&&boMount>0){
                    log.info("领取抵用券成功,cardId" + cardId + ",memGuid=" + memGuid + ",boMount=" + boMount,"chkBonusMount");
                }else {
                    log.info("领取抵用券失败,cardId" + cardId + ",memGuid=" + memGuid + ",boMount=" + boMount,"chkBonusMount");
                    throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "领取抵用券失败");
                }
        }else{
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"查询抵用券领取数量失败");
        }
        return boMount;
    }

    @Override
    public List<JSONObject> getCardNoListByVaSeq(String vaSeq, String memGuid) {
        List<JSONObject> cardnoList=new ArrayList<>();
        try {
            MultiValueMap<String, Object> paramsVaMount = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonVaMount = new JSONObject();
            jsonVaMount.put("vaSeq", vaSeq);
            jsonVaMount.put("memGuid", memGuid);
            paramsVaMount.add("data", jsonVaMount.toString());
            String cardnoListStr = restTemplate.postForObject(cardnoListApi,
                    paramsVaMount, String.class);
            JSONObject cardnoListJson = (JSONObject) JSONObject.parse(cardnoListStr);
            if (cardnoListJson != null && cardnoListJson.getString("code") != null && cardnoListJson.getString("code").equals("0")) {
                JSONArray cardnoArr = cardnoListJson.getJSONArray("data");
                if (cardnoArr != null && !cardnoArr.isEmpty()) {
                    for (int i = 0; i < cardnoArr.size(); i++) {
                        JSONObject cardnoJson = cardnoArr.getJSONObject(i);
                        JSONObject getCardNoJson=new JSONObject();
                        getCardNoJson.put(ConstantMrst.KEY_OF_CARD_NO, cardnoJson.getString("returnSeq"));
                        getCardNoJson.put(ConstantMrst.KEY_OF_CARD_GET_TIME, cardnoJson.getString("receiveTime"));
                        cardnoList.add(getCardNoJson);
                    }
                }
            } else {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询已领取卡券cardNo失败");
            }
            return cardnoList;
        }catch(Exception e){
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询已领取卡券cardNo失败",e);
        }
    }

    @Override
    public String getCardNoByVaSeq(String vaSeq, String memGuid) {
        String retSeq = null;
        List<String> cardNoList=new ArrayList<>();
        try {
            MultiValueMap<String, Object> paramsVaMount = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonVaMount = new JSONObject();
            jsonVaMount.put("vaSeq", vaSeq);
            jsonVaMount.put("memGuid", memGuid);
            paramsVaMount.add("data", jsonVaMount.toString());
            String cardnoListStr = restTemplate.postForObject(cardnoListApi,
                    paramsVaMount, String.class);
            JSONObject cardnoListJson = (JSONObject) JSONObject.parse(cardnoListStr);
            if (cardnoListJson != null && cardnoListJson.getString("code") != null && cardnoListJson.getString("code").equals("0")) {
                JSONArray cardnoArr = cardnoListJson.getJSONArray("data");
                if (cardnoArr != null && !cardnoArr.isEmpty()) {
                    for (int i = 0; i < cardnoArr.size(); i++) {
                        JSONObject cardnoJson = cardnoArr.getJSONObject(i);
                        cardNoList.add(cardnoJson.getString("cardNo"));
                    }
                }
            } else {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询已领取卡券cardNo失败");
            }
        }catch(Exception e){
            log.error("查询已领取卡券cardNo失败", "getCardNoByVaSeq",e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询已领取卡券cardNo失败",e);
        }

        if (cardNoList != null && cardNoList.size() == 1) {
            retSeq = cardNoList.get(0);
        }

        try {
            //同个活动号领取数量大于1.主要是老数据和beta数据
            if (cardNoList != null && cardNoList.size() > 1) {
                List<String> takeCardNoListDb = pkadDao.getTakeCardNoByMembId(memGuid);
                for (String cardNo : cardNoList) {
                    boolean hasTakenThisCardNo = false;
                    for (String cardNoDb : takeCardNoListDb) {
                        if (cardNoDb.contains(cardNo)) {
                            hasTakenThisCardNo = true;
                            break;
                        }
                    }
                    if (!hasTakenThisCardNo) {
                        retSeq = cardNo;
                        break;
                    }
                }
            }
        }catch(Exception e){
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询已领cardNo,出现多个。排除已领取数据时出现错误",e);
        }
        return retSeq;
    }

    private Map<String,String> takeBonus(String cardId, String memGuid, String dataForErrorLog,Integer errorType) {
        MultiValueMap<String, Object> paramsBa = new LinkedMultiValueMap<>();
        JSONObject jsonBa = new JSONObject();
        jsonBa.put("memGuid", memGuid);
        jsonBa.put("baSeq", cardId);
        paramsBa.add("data", jsonBa.toString());
        Map<String,String> returnMap=new HashMap<>();

        String returnSeq = null;
        paramsBa.add("token", "member");
        String result = null;
        try {
            result = restTemplate.postForObject(bonusTakeApi, paramsBa,
                    String.class);
        } catch (Exception e) {
            log.error("调用领取抵用券接口出现错误,cardId=" + cardId + ",memGuid=" + memGuid, "takeBonus", e);
            //领取接口错误的话(主要是超时错误)，用接口查询是否已领取成功。如接口失败或者mount为0，为领取失败
            try {
                returnSeq = this.getCardNoByVaSeq(cardId, memGuid);
            } catch (Exception e2) {
                scoreExceptionHandler.handlerGrowthException(e2, memGuid, dataForErrorLog, errorType);
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "卡券领取记录查询失败", e2);
            }
            if (returnSeq == null) {
                scoreExceptionHandler.handlerGrowthException(e, memGuid, dataForErrorLog, errorType);
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "卡券没有领取记录");
            }
        }
        returnMap.put("returnSeq",returnSeq);
        returnMap.put("result",result);
        return returnMap;
    }

    @Override
    public String takeBonusForPkad(String cardId, String memGuid, String dataForErrorLog) {
        Map<String,String>  returnMap=takeBonus(cardId,memGuid,dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
        String result=returnMap.get("result");
        String returnSeq=returnMap.get("returnSeq");
        JSONObject returnObj=null;
        if(result!=null) {
            try {
                returnObj = JSONObject.parseObject(result);
            } catch (Exception e) {
                throw new ScoreException(ResultCode.RESULT_TYPE_CONV_ERROR, "转换为json格式错误");
            }
            if (returnObj == null || StringUtils.isEmpty(returnObj.getString("code"))) {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "充值接口没有返回");
            }
            if (returnObj != null && returnObj.getString("code") != null && !returnObj.getString("code").equals("0")) {
                String code = returnObj.getString("code");
                String msg = returnObj.getString("msg");
                //24,未绑定手机。优惠券与抵用券返回码不同，手动改成一样
                if (StringUtils.equals(code, "24")) {
                    throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NO_BIND_PHONE, "用户未绑定手机!");
                } else if (StringUtils.equals(code, "9")) {
                    throw new ScoreException(ResultCode.TAKE_BONUS_BUT_IS_EMP, "您是员工账户哦，无法领取该礼包~");
                } else if (StringUtils.equals(code, "102")) {
                    throw new ScoreException(ResultCode.TAKE_BONUS_BUT_IS_DOUBTABLE, "尊敬的客户您好，您的账户存在异常，无法领取该礼包，如有疑问请致电客服。");
                } else if (StringUtils.equals(code, "14")) {
                    //重复领券的不计入unsuccess表
                    //{\"code\":\"14\",\"sn\":\"0001\",\"msg\":\"此活动您已领取过1张，超过活动所设置的上限。不能再重复领取\",\"data\":null}
                    log.error("cardId" + cardId + " ,memGuid" + memGuid + " ," + msg,"takeBonus");
                    throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "卡券领取失败");
                } else {
                    log.error("cardId" + cardId + " ,memGuid" + memGuid + " ," + msg,"takeBonus");
                    scoreExceptionHandler.handlerGrowthException(new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, result), memGuid, dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
                    throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "卡券领取失败");
                    //throw new ScoreException(Integer.parseInt(code), msg);
                }
            }
            if (returnObj != null && returnObj.getString("code") != null && returnObj.getString("code").equals("0")) {
                log.info("领取抵用券成功,cardId=" + cardId + " ,memGuid"+memGuid,"takeBonus");
                JSONObject data = returnObj.getJSONObject("data");
                if (data != null && !data.isEmpty()) {
                    returnSeq = data.getString("seq");
                }
            } else {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "抵用券充值接口返回值解析失败");
            }
        }
        return returnSeq;
    }

    @Override
    public String takeVoucher(String cardId, String memGuid, String dataForErrorLog) {
        String returnSeq=null;
        MultiValueMap<String, Object> paramsVa = new LinkedMultiValueMap<>();
        JSONObject jsonVa = new JSONObject();
        jsonVa.put("vaSeq", cardId);
        jsonVa.put("vaMemberGuid", memGuid);
        paramsVa.add("param", jsonVa.toString());
        String result=null;
        try {
            result = restTemplate.postForObject(voucherTakeApi,
                    paramsVa, String.class);
        } catch (Exception e) {
            log.error("调用领取优惠券接口出现错误,cardId="+cardId+",memGuid="+memGuid,"takeVoucher",e);
            //领取接口错误的话(主要是超时错误)，用接口查询是否已领取成功。如接口失败或者mount为0，为领取失败
            try {
                returnSeq = this.getCardNoByVaSeq(cardId, memGuid);
            }catch (Exception e2){
                scoreExceptionHandler.handlerGrowthException(e, memGuid,dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"卡券没有领取记录",e2);
            }
            if(returnSeq==null){
                scoreExceptionHandler.handlerGrowthException(e, memGuid,dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"卡券没有领取记录");
            }
        }

        JSONObject returnObj=null;
        if(result!=null) {
            try {
                returnObj = JSONObject.parseObject(result);
            } catch (Exception e) {
                throw new ScoreException(ResultCode.RESULT_TYPE_CONV_ERROR, "转换为json格式错误");
            }
            if (returnObj == null || StringUtils.isEmpty(returnObj.getString("code"))) {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "充值接口没有返回");
            }
            if(returnObj!=null&&returnObj.getString("code")!=null&&returnObj.getString("code").equals("200")){
                JSONObject body=returnObj.getJSONObject("body");
                if(body!=null&&!body.isEmpty()){
                    String vcsCardNo=body.getString("vcsCardNo");
                    String messageCode=body.getString("messageCode");
                    String message=body.getString("message");
                    if(!StringUtils.equals(messageCode,"0")){
                        if (StringUtils.equals(messageCode,"7")){
                            throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NO_BIND_PHONE,"用户未绑定手机!");
                        }else if (StringUtils.equals(messageCode,"102")){
                            throw new ScoreException(ResultCode.TAKE_BONUS_BUT_IS_DOUBTABLE,"尊敬的客户您好，您的账户存在异常，无法领取该礼包，如有疑问请致电客服。");
                        }else {
                            log.error("cardId" + cardId + " ,memGuid" + memGuid + " ," + message, "takeVoucher");
                            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"卡券领取失败");
                        }
                    }else{
                        returnSeq=vcsCardNo;
                    }
                    log.info("领取自营优惠券成功,cardId" + cardId + " ,memGuid" + memGuid, "takeVoucher");
                }else{
                    throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"优惠券充值接口返回值解析失败");
                }
            }else{
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"优惠券充值接口返回值解析失败");
            }
        }
        return returnSeq;
    }

    @Override
    public String takeMailCoupon(String cardId, String memGuid, String dataForErrorLog) {
        String returnSeq=null;
        MultiValueMap<String, Object> paramsMa = new LinkedMultiValueMap<>();
        JSONObject jsonVa = new JSONObject();
        jsonVa.put("couponId", cardId);
        jsonVa.put("userId", Mcrypt3Des.desEncrypt(memGuid));
        paramsMa.add("param", jsonVa.toString());
        String result=null;
        try {
            result = restTemplate.postForObject(mailCouponTakeApi,
                    paramsMa, String.class);
        } catch (Exception e) {
            log.error("调用领取商城券接口出现错误,cardId=" + cardId + ",memGuid=" + memGuid, "takeMailCoupon", e);
            //领取接口错误的话(主要是超时错误)，用接口查询是否已领取成功。如接口失败或者mount为0，为领取失败
            try {
                returnSeq = this.getCardNoByVaSeq(cardId, memGuid);
            }catch (Exception e2){
                scoreExceptionHandler.handlerGrowthException(e, memGuid,dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"卡券没有领取记录",e2);
            }
            if(returnSeq==null){
                scoreExceptionHandler.handlerGrowthException(e, memGuid,dataForErrorLog, Constant.PKAD_TAKEN_UNSUCCESS);
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"卡券没有领取记录");
            }
        }
        JSONObject returnObj=null;
        if(result!=null) {
            try {
                returnObj = JSONObject.parseObject(result);
            } catch (Exception e) {
                throw new ScoreException(ResultCode.RESULT_TYPE_CONV_ERROR, "转换为json格式错误");
            }
            if (returnObj == null || StringUtils.isEmpty(returnObj.getString("code"))) {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "充值接口没有返回");
            }
            if(returnObj!=null&&returnObj.getString("code")!=null&&returnObj.getString("code").equals("200")){
                JSONObject body=returnObj.getJSONObject("body");
                if(body!=null&&!body.isEmpty()){
                    String vcsCardNo=body.getString("vouchers_sequence_num");
                    String messageCode=body.getString("messageCode");
                    String message=body.getString("message");
                    if(!StringUtils.equals(messageCode,"0")){
                        if (StringUtils.equals(messageCode,"7")){
                            throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NO_BIND_PHONE,"用户未绑定手机!");
                        }else if (StringUtils.equals(messageCode,"102")){
                            throw new ScoreException(ResultCode.TAKE_BONUS_BUT_IS_DOUBTABLE,"尊敬的客户您好，您的账户存在异常，无法领取该礼包，如有疑问请致电客服。");
                        }else {
                            log.error("cardId" + cardId + " ,memGuid" + memGuid + " ," + message, "takeMailCoupon");
                            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"卡券领取失败");
                        }
                    }else{
                        returnSeq=vcsCardNo;
                    }
                    log.info("领取商城券成功,cardId"+cardId+" ,memGuid"+memGuid,"takeMailCoupon");
                }else{
                    throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"商城券充值接口返回值解析失败");
                }
            }else{
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"商城券充值接口返回值解析失败");
            }
        }
        return returnSeq;
    }

    @Override
    public JSONArray getCardInfoByCardIdAndType(JSONArray cardInfoArr,boolean isBatch) {
        JSONArray returnArr = new JSONArray();
        try {

            String cardIds = "";
            for (int i = 0; i < cardInfoArr.size(); i++) {
                JSONObject cardInfoObj = cardInfoArr.getJSONObject(i);
                JSONArray cardListArr = cardInfoObj.getJSONArray("card_list");
                if (cardListArr != null && !cardListArr.isEmpty()) {
                    for (int j = 0; j < cardListArr.size(); j++) {
                        JSONObject cardListObj = cardListArr.getJSONObject(j);
                        String enCardId = this.enCardId(cardListObj.getString("card_id"));
                        cardListObj.put("card_id", enCardId);
                        cardListArr.set(j, cardListObj);
                        cardIds += enCardId + ",";
                    }
                }
            }

            if (cardIds.length() > 1) {
                cardIds = cardIds.substring(0, cardIds.length() - 1);
            }
            JSONObject returnObj = getCardInfoApi(cardIds,isBatch);
            JSONArray restArr = returnObj.getJSONArray("body");

            for (int i = 0; i < cardInfoArr.size(); i++) {
                JSONObject cardInfoObj = cardInfoArr.getJSONObject(i);
                JSONArray cardListArr = cardInfoObj.getJSONArray("card_list");
                if (cardListArr != null && !cardListArr.isEmpty()) {
                    for (int i2 = 0; i2 < cardListArr.size(); i2++) {
                        JSONObject cardListObj = cardListArr.getJSONObject(i2);
                        String enCardId = cardListObj.getString("card_id");
                        for (int j2 = 0; j2 < restArr.size(); j2++) {
                            JSONObject couponObj = restArr.getJSONObject(j2);
                            String actSeq = couponObj.getString("couponId");
                            if (enCardId.equals(actSeq)) {
                                JSONObject returnRestObj = returnCardList(couponObj);
                                //cardSeq ,无线需要
                                //批量，需要以cardSeq做唯一键。而cardNum会有重复
                                if(isBatch){
                                    returnRestObj.put("cardSeqForBatch", MrstUtil.enCodeCouponSensitive(cardListObj.getString("card_seq")));
                                }
                                returnRestObj.put("cardSeq", !StringUtils.isEmpty(cardListObj.getString("card_num")) ? cardListObj.getString("card_num") : MrstUtil.enCodeCouponSensitive(cardListObj.getString("card_seq")));
                                returnRestObj.remove("cardId");
                                returnArr.add(returnRestObj);
                                break;
                            }
                        }
                    }
                }
            }
            return returnArr;
        } catch (Exception e) {
            log.error("查询卡券信息失败",e);
            throw new ScoreException("查询卡券信息失败",e);
        }
    }

    @Override
    public JSONArray getTakenCardInfoByCardIdAndType(Set<String> takenCardSet,boolean isBatch) {
        JSONArray returnArr = new JSONArray();

        try {
            String cardIds = "";
            String takenCardIds = "";
            HashSet<String> takenCardHashSet = new HashSet<>();
            if (takenCardSet != null) {
                for (String cardId : takenCardSet) {
                    takenCardHashSet.add(enCardId(cardId));
                }
                takenCardIds = StringUtils.join(takenCardHashSet, ",");
            }
            JSONObject returnObj = getCardInfoApi(takenCardIds,isBatch);
            JSONArray restArr = returnObj.getJSONArray("body");
            for (String enCardId : takenCardHashSet) {
                for (int j2 = 0; j2 < restArr.size(); j2++) {
                    JSONObject couponObj = restArr.getJSONObject(j2);
                    String actSeq = couponObj.getString("couponId");
                    if (enCardId.equals(actSeq)) {
                        returnArr.add(returnCardList(couponObj));
                        break;
                    }
                }
            }
            return returnArr;
        } catch (Exception e) {
            throw new ScoreException("查询卡券信息失败",e);
        }
    }

    private JSONObject getCardInfoApi(String cardIds,boolean isBatch){
        String cacheStr=cacheUtils.getCacheData(cardIds+"_cardInfo");
        if(StringUtils.isNotBlank(cacheStr)){
            JSONObject cacheObj =JSONObject.parseObject(cacheStr);
            if(cacheObj!=null && !cacheObj.isEmpty()&& "200".equals(cacheObj.getString("code"))){
                return cacheObj;
            }
        }
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("param", cardIds);
        String returnStr;
        if(isBatch) {
            returnStr=restTemplate.postForObject(getCardInfoApi, params, String.class);
        }else{
            returnStr=restTemplateSmallTimeout.postForObject(getCardInfoApi, params, String.class);
        }
        JSONObject returnObj =JSONObject.parseObject(returnStr);
        if(returnObj!=null && !returnObj.isEmpty() && "200".equals(returnObj.getString("code"))){
            cacheUtils.putCache(cardIds+"_cardInfo",3600,returnStr);
        }
        return  returnObj;
    }
    private JSONObject returnCardList(JSONObject couponObj){
        JSONObject returnRestObj=new JSONObject();
        returnRestObj.put("seq", "");
        String cardType=ConstantMrst.GET_CARD_TYPE_BY_COUPON_TYPE.get(couponObj.getString("discountType"));
        returnRestObj.put("cardId",couponObj.getString("couponId"));
        returnRestObj.put("cardType", ConstantMrst.GET_CARD_TYPE_BY_COUPON_TYPE.get(couponObj.getString("discountType")));
        if(couponObj.getInteger("baInactive")==null || !ConstantMrst.CARD_TYPE_DYQ.equals(cardType)){
            returnRestObj.put("startDate", DateUtil.getFormatDateFromStr(couponObj.getString("useStartTime"), "yyyyMMddHHmmss", "yyyy/MM/dd"));
            returnRestObj.put("endDate", DateUtil.getFormatDateFromStr(couponObj.getString("useEndTime"), "yyyyMMddHHmmss", "yyyy/MM/dd"));
        }else{
            returnRestObj.put("startDate", "");
            returnRestObj.put("endDate", couponObj.getString("baInactive"));
        }
        if("0".equals(couponObj.getString("man"))){
            returnRestObj.put("price", "");
        }else {
            returnRestObj.put("price", couponObj.getString("man"));
        }
        //去掉小数
        if (couponObj.getString("jian") != null) {
            if (couponObj.getString("jian").indexOf(".") >= 1) {
                returnRestObj.put("discount", couponObj.getString("jian").substring(0, couponObj.getString("jian").indexOf(".")));
            } else {
                returnRestObj.put("discount", couponObj.getString("jian"));
            }
        }
        returnRestObj.put("cardTypeDesc", ConstantMrst.GET_CARD_TYPE_DESC.get((String)returnRestObj.get("cardType")));
        returnRestObj.put("name", couponObj.getString("couponName"));
        returnRestObj.put("scopeDescription", couponObj.getString("scopeDescription"));
        return returnRestObj;
    }

    private String enCardId(String cardId){
        String enCardID=null;
        String[] needEnActIDList=needEnActID.split(",");
        String[] encodeActIDList=encodeActID.split(",");
        for(int i=0;i<encodeActIDList.length;i++){
            if(StringUtils.equals(needEnActIDList[i], cardId)) {
                enCardID = encodeActIDList[i];
            }
        }
        if(StringUtils.isNotBlank(enCardID)){
            return enCardID;
        }else{
            return cardId;
        }
    }

    @Override
    public boolean takeBonusForUnionist(String memGuid, String tpLoginType, Date regOrBindTime, String dataForErrorLog, Integer errorType) {
        if(StringUtils.isNotBlank(tpLoginType)) {
            if (StringUtils.isNotBlank(unionistTploginType)) {
                String[] utts = unionistTploginType.split(Constant.DELIMITER);
                String[] bonusIdsAllTypes = bonusActivityIds.split(Constant.DELIMITER);
                String[] uats = unionistActivityTime.split(Constant.DELIMITER);
                for (int i=0;i<utts.length;i++) {
                    if (tpLoginType.equals(utts[i])) {
                        if (StringUtils.isBlank(bonusActivityIds)||StringUtils.isBlank(bonusIdsAllTypes[i])) {
                            scoreExceptionHandler.handlerGrowthException(new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "缺少赠送抵用券活动号配置"), memGuid, dataForErrorLog, errorType);
                            return false;
                        } else {
                            Date beginSendTime=DateUtil.getDateFromStr(uats[i],"yyyy-MM-dd HH:mm:ss");
                            if(StringUtils.isBlank(uats[i])||(beginSendTime!=null&& !regOrBindTime.before(beginSendTime))){
                                String[] bonusIdsThisType= bonusIdsAllTypes[i].split(",");
                                boolean flag=true;
                                for (String bonusId : bonusIdsThisType) {
                                    try {
                                        takeBonus(bonusId, memGuid, dataForErrorLog, errorType);
                                    } catch (Exception e) {
                                        log.error("工会会员赠送抵用券出现错误," + dataForErrorLog, "takeBonusForUnionist");
                                        flag=false;
                                    }
                                }
                                return flag;
                            }
                        }
                    }
                }
            }else {
                scoreExceptionHandler.handlerGrowthException(new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "缺少工会会员type配置"), memGuid, dataForErrorLog,Constant.UNIONIST_REGISTER_SEND_BONUS);
                return false;
            }
        }
        return true;
    }
}
