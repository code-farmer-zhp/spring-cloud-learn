package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.dao.dataplan.OrderPackageLogDao;
import com.feiniu.score.dao.notice.MailSender;
import com.feiniu.score.entity.dataplan.OrderPackageLog;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.phoneDataPlan.Base64;
import com.feiniu.score.util.phoneDataPlan.CertificateHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PhoneDataPlanServiceImpl implements PhoneDataPlanService{
	private static CustomLog log = CustomLog.getLogger(PhoneDataPlanServiceImpl.class);

	@Qualifier("restTemplateBigTimeout")
	@Autowired
	private RestTemplate restTemplateBigTimeout;

	@Autowired
	private OrderPackageLogDao orderPackageLogDao;

	@Value("${data.plan.order.package.url}")
	private String orderPackageUrl;
	@Value("${data.plan.order.package.action.code}")
	private String orderPackageActionCode;

	@Value("${data.plan.query.order.url}")
	private String queryOrderUrl;
	@Value("${data.plan.order.package.action.code}")
	private String queryOrderActionCode;

	@Value("${data.plan.check.account.url}")
	private String checkAccountUrl;
	@Value("${data.plan.check.account.action.code}")
	private String checkAccountActionCode;

	//ztInterSource 渠道号
	@Value("${ztInterSource}")
	private String ztInterSource;
	//staffValue 协销工号ID
	@Value("${staffValue}")
	private String staffValue;
	//offerSpecl 销售品ID
	@Value("${offerSpecl}")
	private String offerSpecl;
	//goodName 销售品名称
	@Value("${goodName}")
	private String goodName;
	//证书路径
	@Value("${certificatePath}")
	private String certificatePath;
	//最多赠送多少用户
	@Value("${successCount}")
	private int successCount;

	//1 成功 0 失败 -1未知错误，需重新检验
	private String SUCCESS_STR ="1";
	private String FAILED_STR ="0";
	private String ERROR_STR ="-1";

	@Value("${ftpPort}")
	private String ftpPort;

	@Value("${ftpHost}")
	private String ftpHost;

	@Value("${ftpAgreement}")
	private String ftpAgreement;

	@Value("${ftpUser}")
	private String ftpUser;

	@Value("${ftpPwd}")
	private String ftpPwd;

	@Autowired
	private MailSender mailSender;

	/**
	 * @todo 赠送流量
	 * @param memGuid
	 * @param orderPhone
	 * @param deviceId
     */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
	public void orderPackage(String memGuid,String orderPhone,String deviceId){

		Map<String,Object> paramMap=new HashMap<>();
		paramMap.put("status",SUCCESS_STR);
		int successOplsCount=orderPackageLogDao.getOrderPackageLogCountBySelective(paramMap);//最多一万个用户
		if(successOplsCount>=successCount){
			log.info("电信定制包注册赠送流量，已送"+successCount+"份，该用户不再赠送,memGuid="+memGuid+",orderPhone="+orderPhone+",deviceId="+deviceId);
			return;
		}

		int count = orderPackageLogDao.selectPhoneOrDevice(orderPhone, deviceId, "1");
		if (count >= 1) {
			log.info("设备号：" + deviceId + ",或手机号："+orderPhone+"已送过流量");
			return;
		}

		Date now=new Date();
		//渠道号+时间(年月日时分秒)+6位随机数
		String reqId=ztInterSource+DateUtil.getFormatDate(new Date(),"yyyyMMddHHmmss")+String.format("%06d", ThreadLocalRandom.current().nextInt(999999));

		String orderPackageResultStr=orderPackageRest(reqId,orderPhone);//充流量
		//1 成功 0 失败 -1未知错误，需重新检验
		String status;
		String errorMessage="";

		if(orderPackageResultStr==null){
			status=ERROR_STR;
			errorMessage="接口充值调用失败";
		}else{
			try {
				JSONObject orderPackageResultJson = JSONObject.parseObject(orderPackageResultStr);
				if ("0".equals(orderPackageResultJson.getString("TSR_RESULT"))) {
					status = SUCCESS_STR;
					errorMessage = "充值成功";
				} else {
					errorMessage=orderPackageResultJson.getString("TSR_MSG");
					status = FAILED_STR;
					//如果返回失败，再次查询结果
					String s = queryOrderRest(reqId);//查询状态
					if (s != null) {
						JSONObject jsonObject = JSONObject.parseObject(s);
						if (jsonObject != null) {
							String tsr_result = jsonObject.getString("TSR_RESULT");
							JSONArray data = jsonObject.getJSONArray("data");
							if ("0".equals(tsr_result) && data != null) {
								status = SUCCESS_STR;
								errorMessage = "充值成功";
							} else {
                        //发短信
								String rtn1 = mailSender.sendMess("充值失败的短信", orderPhone, "尊敬的用户，江苏电信500M省内流量充值失败，详情请咨询店内工作人员。"
										, "1", "1", "8", "");
								log.info("失败的短信"+rtn1);
								errorMessage += "且调用查询状态的接口返回空";
							}
						}
					} else {
						status = ERROR_STR;//未知结果
						errorMessage += "且调用查询状态的接口返回空";
					}

				}
			} catch (Exception e) {
				status = ERROR_STR;
				errorMessage = "调用查询状态的接口报错";
			}
		}
		OrderPackageLog opl=new OrderPackageLog();
		opl.setMemGuid(memGuid);
		opl.setReqId(reqId);
		opl.setOrderPhone(orderPhone);
		opl.setDeviceId(deviceId);
		opl.setOrderTime(now);
		opl.setRegTime(now);
		opl.setErrorMessage(errorMessage);
		opl.setGoodName(getUTF8Words(goodName));
		opl.setOfferSpecl(offerSpecl);
		opl.setStatus(status);
		opl.setStaffValue(staffValue);
		orderPackageLogDao.saveOrderPackageLogDao(opl);//写log
	}

	@Override
	public void queryErrorLogAndUpdate(){
		Map<String,Object> paramMap=new HashMap<>();
		paramMap.put("status",ERROR_STR);
		List<OrderPackageLog> oplLists=orderPackageLogDao.getOrderPackageLogListBySelective(paramMap);
		for(OrderPackageLog opl:oplLists){
			try {
				String status=ERROR_STR;
				String queryOrderStr = queryOrderRest(opl.getReqId());
				try {
					JSONObject queryOrderJson = JSONObject.parseObject(queryOrderStr);
					if ("0".equals(queryOrderJson.getString("TSR_RESULT"))) {
						JSONObject resultDataJson = queryOrderJson.getJSONObject("data");
						if (SUCCESS_STR.equals(resultDataJson.getString("state"))) {
							status = SUCCESS_STR;
						} else if (FAILED_STR.equals(resultDataJson.getString("state"))) {
							status = FAILED_STR;
						}
					}
				} catch (Exception e) {
					log.error("queryErrorLogAndUpdate",e);
				}
				if(!ERROR_STR.equals(status)){
					OrderPackageLog upOpl=new OrderPackageLog();
					upOpl.setStatus(status);
					orderPackageLogDao.updateOrderPackageLog(upOpl);
				}
			}catch (Exception e) {
				log.error("queryErrorLogAndUpdate",e);
			}
		}
	}

	@Override
	public List<OrderPackageLog> getOrderPackageLogByMonth(String month,String status){
		Map<String,Object> paramMap=new HashMap<>();

		JSONObject beginEndOfMonth = getBeginEndOfMonth(month);
		if(beginEndOfMonth!=null) {
			paramMap.put("startTime", beginEndOfMonth.getString("beginTime"));
			paramMap.put("endTime",  beginEndOfMonth.getString("endTime"));
		}
		if(StringUtils.isNotBlank(status)) {
			paramMap.put("status", status);
		}
		return orderPackageLogDao.getOrderPackageLogListBySelective(paramMap);
	}

	private String orderPackageRest(String reqId,String accNbr) {
		try {
			StringBuilder paramStr=new StringBuilder();
			paramStr.append("reqId=").append(reqId).append(";");
			paramStr.append("accNbr=").append(accNbr).append(";");
			paramStr.append("offerSpecl=").append(offerSpecl).append(";");
			paramStr.append("actionCode=").append(orderPackageActionCode).append(";");
			paramStr.append("goodName=").append(getUTF8Words(goodName)).append(";");
			paramStr.append("ztInterSource=").append(ztInterSource).append(";");
			paramStr.append("staffValue=").append(staffValue).append(";");
			paramStr.append("type=1");

			MultiValueMap<String,Object> params=getParams(paramStr.toString());
			String str = restTemplateBigTimeout.postForObject(orderPackageUrl,params,String.class);
			log.info("orderPackageUrl:"+str);
			return str;
		} catch (Exception e) {
			log.error("orderPackage",e);
			return null;
		}
	}

	private String queryOrderRest(String reqId) {
		try {
			//accNbr	协销工号ID  在分销平台注册的手机号
			String paramStr = "reqId=" + reqId + ";" +
					"accNbr=" + staffValue + ";" +
					"actionCode=" + queryOrderActionCode+ ";" +
					"ztInterSource=" + ztInterSource;

			MultiValueMap<String,Object> params=getParams(paramStr);
			String s = restTemplateBigTimeout.postForObject(queryOrderUrl, params, String.class);
			log.info("queryOrderUrl:"+s);
			return s;
		} catch (Exception e) {
			log.error("queryOrder",e);
			return null;
		}
	}

	/**
	 *
	 * @param date	日期：’yyyyMMdd’,’yyyyMM’
	 * @param dateType	 日期类型：’0’按天；’1’：按月
	 * @return
	 */
	@Override
	public String checkAccountRest(String date,String dateType) {
		try {
			StringBuilder paramStr=new StringBuilder();
			paramStr.append("accNbr=").append(staffValue).append(";");
			paramStr.append("actionCode=").append(checkAccountActionCode).append(";");
			paramStr.append("ztInterSource=").append(ztInterSource).append(";");
			paramStr.append("date=").append(date).append(";");
			paramStr.append("dateType=").append(dateType);

			MultiValueMap<String,Object> params=getParams(paramStr.toString());
			return restTemplateBigTimeout.postForObject(checkAccountUrl,params,String.class);
		} catch (Exception e) {
			log.error("checkAccountRest",e);
			return null;
		}
	}

	private MultiValueMap<String,Object> getParams(String paramStr){
		MultiValueMap<String, Object> params=new LinkedMultiValueMap<>();
		byte[] encrypt = new byte[0];
		try {
			encrypt = CertificateHelper.encryptByPublicKey(paramStr.getBytes(),
                    certificatePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.add("para", Base64.encode(encrypt));
		return params;
	}

	private String getUTF8Words(String word){
		String wordStr="";
		try {
			wordStr = new String(word.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			wordStr = word;
		}
		return wordStr;
	}


	private JSONObject getBeginEndOfMonth(String mouth){
		try {
			Calendar instance = Calendar.getInstance();
			SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat yyyyMMDD = new SimpleDateFormat("yyyy-MM-dd");
			Date parse = yyyyMM.parse(mouth);
			instance.setTime(parse);
			instance.set(Calendar.DAY_OF_MONTH,   1);
			Date time = instance.getTime();
			String begin = yyyyMMDD.format(time)+" 00:00:00";


			instance.setTime(parse);
			instance.add(Calendar.MONTH,1);
			instance.add(Calendar.DAY_OF_MONTH,-1);
			Date time1 = instance.getTime();
			String end = yyyyMMDD.format(time1)+ " 23:59:59";

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("beginTime",begin);
			jsonObject.put("endTime",end);

			return jsonObject;
		} catch (Exception e) {
			return null;
		}

	}
}
