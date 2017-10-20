package com.feiniu.score.dao.growth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dto.PartnerInfo;
import com.feiniu.score.exception.ScoreException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SearchMemberDaoImpl implements SearchMemberDao {
	

    @Autowired
    @Qualifier("restTemplateSmallTimeout")
    private RestTemplate restTemplateSmallTimeout;

    @Value("${searchMember.url}")
    private String searchMemberUrl;
    @Value("${isPartner.api}")
    private String isPartnerApi;
    @Value("${unionist.tplogin.type}")
    private String unionistTploginType;

    private final static int SUCCESS_CODE = 100;
    private final static String SEARCH_TYPE = "5";

    @Override
    public Map<String, Object> getMemberInfo(String memGuid) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", memGuid);
        params.add("type", SEARCH_TYPE);
        String resultJson = restTemplateSmallTimeout.postForObject(searchMemberUrl, params, String.class);
        JSONObject resultObj = JSONObject.parseObject(resultJson);
        int code = resultObj.getIntValue("code");
        if (SUCCESS_CODE == code) {
            JSONObject data = resultObj.getJSONObject("data");
            int isEmployee = data.getIntValue("IS_EMPLOYEE");
            int isCompany = data.getIntValue("MEM_TYPE");
            Map<String,Object> result = new HashMap<>();
            if(isEmployee == 1){
                result.put("isEmployee",true);
            }else {
                result.put("isEmployee",false);
            }
            if(isCompany == 1){
                result.put("isCompany",true);
            }else {
                result.put("isCompany",false);
            }
            String email = data.getString("MEM_EMAIL");
            String phoneNo = data.getString("MEM_CELL_PHONE");
            String userName = data.getString("MEM_USERNAME");
            result.put("email", stringValue(email));
            result.put("phoneNo", stringValue(phoneNo));
            result.put("userName", stringValue(userName));
            String nickName = data.getString("NICKNAME");
            if(StringUtils.isEmpty(nickName)||StringUtils.equals(nickName, Constant.EDFAULT_DB_EMPTY)){
                nickName= userName;
            }
            result.put("nickName", nickName);


            boolean isTradeUnionist=false;
            JSONArray tpLoginList = data.getJSONArray("TP_LOGIN_LIST");
            if (tpLoginList != null && !tpLoginList.isEmpty()) {
                for (int i = 0; i < tpLoginList.size(); i++) {
                    JSONObject tpLoginInfo = tpLoginList.getJSONObject(i);
                    if(tpLoginInfo!=null&&!tpLoginInfo.isEmpty()&&StringUtils.isNotBlank(tpLoginInfo.getString("TP_LOGIN_TYPE"))) {
                        if(StringUtils.isNotBlank(unionistTploginType)){
                            String [] utts=unionistTploginType.split(Constant.DELIMITER);
                            for(String utt:utts){
                                if(tpLoginInfo.getString("TP_LOGIN_TYPE").equals(utt)){
                                    isTradeUnionist = true;
                                }
                            }
                        }else{
                            throw new ScoreException(ResultCode.GET_IS_TRADE_UNIONIST_ERROR, "缺少工会会员type配置");
                        }
                    }
                }
            }
            result.put("isTradeUnionist", isTradeUnionist);
            return result;
        } else {
            String msg = resultObj.getString("msg");
            throw new ScoreException(ResultCode.GET_IS_TRADE_UNIONIST_ERROR, "查询用户信息异常。msg=" + msg);
        }
    }


    @Override
    public PartnerInfo getIsPartnerInfo(String memGuid) {
        JSONObject paramJson=new JSONObject();
        paramJson.put("memberId", memGuid);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("data", paramJson.toString());
        try{
            String resultJson = restTemplateSmallTimeout.postForObject(isPartnerApi, params, String.class);
            JSONObject resultObj = JSONObject.parseObject(resultJson);
            JSONObject data = resultObj.getJSONObject("Body");
            return new PartnerInfo(data.getBooleanValue("result"),data.getString("time"));
        } catch(Exception e) {
            throw new ScoreException(ResultCode.GET_IS_PARTNER_ERROR, "查询用户是否为合伙人异常",e);
        }
    }

    public String stringValue(String str){
        if(str == null){
            return "";
        }
        return str;
    }
    public static void main(String[] args) {
        SearchMemberDaoImpl checkEmployeeDao = new SearchMemberDaoImpl();
        System.out.println(checkEmployeeDao.getMemberInfo("BB76C26B-B56F-993A-0EB1-E58F5E3ACF95"));
    }


}
