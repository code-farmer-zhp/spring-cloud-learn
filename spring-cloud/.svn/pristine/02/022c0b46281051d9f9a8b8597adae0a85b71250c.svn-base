package com.feiniu.member.controller.growth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.common.ResultCodeWeb;
import com.feiniu.member.controller.common.CommonController;
import com.feiniu.member.dto.Result;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.util.HttpRequestUtil;
import com.feiniu.member.util.IsNumberUtil;
import com.feiniu.member.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/growth")
public class GrowthController extends CommonController{

	public static final CustomLog log = CustomLog.getLogger(GrowthController.class);

	@Value("${memGrowthInfo.api}")
	private String memGrowthInfoApi;
	@Value("${levelCount.api}")
	private String RecordCountApi;
	@Value("${level.api}")
	private String GrowthDetailListApi;
	@Value("${www.url}")
	private String wwwUrl;
	@Value("${safe.url}")
	private String safeUrl;
	@Value("${headImg.url}")
	private String headImgUrl;
	@Value("${defaultHeadImg}")
	private String defaultHeadImg;
	@Value("${searchMember.api}")
	private String searchMemberApi;
	@Value("${instruction.url}")
	private String instructionUrl;
	@Value("${storeDomain.url}")
    private String storeDomain;
	@Value("${getPkadList.api}")
	private String getPkadListApi;
	@Value("${getPkadListCount.api}")
	private String getPkadListCountApi;
	@Value("${getMrstUiList.api}")
	private String getMrstUiListApi;
	@Value("${takePkad.api}")
    private String takePkadApi;
	@Value("${getCardInfo.api}")
	private String getCardInfoApi;
	@Value("${getTakenCardInfo.api}")
	private String getTakenCardInfoApi;

	@Value("${vipGrade.Url}")
	private String vipGradeUrl;

	@Value("${vipGift.Url}")
	private String vipGiftUrl;

/*
	@RequestMapping(value="/list/{gridType}/{pageNo}",method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response,@PathVariable String gridType,@PathVariable String pageNo) throws Exception {

			ModelAndView mav =getModel(request, "growth/list");
			if(mav.getViewName().equals("redirect:"+loginUrl))
	        {
	            return mav;
	        }
			if(mav.getModel().isEmpty()){
				return new ModelAndView("redirect:" + loginUrl);
			}
			String memGuid="";
			if(mav.getModel().get("memGuid")!=null){
				memGuid=mav.getModel().get("memGuid").toString();
			}
			String basePath="";
			if(mav.getModel().get("basePath")!=null){
				basePath=mav.getModel().get("basePath").toString();
			}
			mav.addObject("wwwUrl", wwwUrl);
			mav.addObject("levelDesc", ConstantGrowth.GET_LEVEL_DESC);
			mav.addObject("level0", ConstantGrowth.LEVEL_OF_0);
			mav.addObject("level1", ConstantGrowth.LEVEL_OF_1);
			mav.addObject("imgBaseUrl", imgBaseUrl);
			mav.addObject("defaultHeadImg",defaultHeadImg);
			mav.addObject("instructionUrl",instructionUrl);
			mav.addObject("storeDomain",storeDomain);
			MultiValueMap<String, String> memparams = new LinkedMultiValueMap<>();
			memparams.add("name", memGuid);
			memparams.add("type", "5");
			memparams.add("returnType", "1");
			String resultJson = restTemplate.postForObject(searchMemberApi, memparams,
					String.class);
			if (!StringUtils.isBlank(resultJson)) {
				JSONObject resultObj = JSONObject.parseObject(resultJson);
				JSONObject body = (JSONObject) resultObj.get("Body");
				JSONObject data = (JSONObject) body.get("Data");
				if (data != null) {
					String headImage= data.getString("HEAD_PORTRAIT");
					mav.addObject("headImage", headImage);
				}
			}
			
			String url = basePath+"growth/list";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("memGuid", memGuid);
			
			// String memGrowthInfo=HttpRequestUtil.sendPost(memGrowthInfoApi,
			// HttpRequestUtil.assemblyJsonParam(params),"");
			String memGrowthInfo = restTemplate.postForObject(memGrowthInfoApi,
					HttpRequestUtil.assemblyJsonParam(params), String.class);
			Result growthInfoReslut = JSONObject.parseObject(memGrowthInfo,
					Result.class);
			if(growthInfoReslut.getData()!=null){
				mav.addObject("memResult", growthInfoReslut.getData());
			}
			else{
			    JSONObject memReslutJson=new JSONObject();
				memReslutJson.put("overPercent", "0%");
				memReslutJson.put("memLevel", "T0");
				memReslutJson.put("growthValue", "0");
				memReslutJson.put("expiryDate", "永久");
				mav.addObject("memResult", memReslutJson);
			}
			//String pageNo = request.getParameter("pageno");
			//String pageSize = request.getParameter("pagesize");
			String pageSize="15";
			//String gridType = request.getParameter("gridType");
			if (StringUtils.isEmpty(gridType)
					|| (!IsNumberUtil.isInteger(gridType))) {
				gridType = "0";
			}
			mav.addObject("gridType", gridType);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 1);
			String startDate = sdf.format(calendar.getTime());
			String endDate = sdf.format(new Date());
			if (StringUtils.isEmpty(pageNo)
					|| (!IsNumberUtil.isInteger(pageNo))) {
				pageNo = "1";
			}
			Map thisMonthMap = getGrowthByDate(memGuid, startDate, endDate,
					pageNo, pageSize, url, "0");
			mav.addObject("detailResult", thisMonthMap.get("growthDetailList"));
			mav.addObject("pageData", thisMonthMap.get("pageData"));

			String startDateBefore = "0000-00-00 00:00:00";
			String endDateBefore = sdf.format(calendar.getTime());
			
			
			Map beforeMonthMap = getGrowthByDate(memGuid, startDateBefore,
					endDateBefore, pageNo, pageSize, url, "1");
			mav.addObject("detailResultBefore",
					beforeMonthMap.get("growthDetailList"));
			mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
			
			return mav;
	}
*/
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return list( request, response,"0","1");
	}
	@RequestMapping(value="/list/{gridType}/{pageNo}",method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String gridType, @PathVariable String pageNo) throws Exception {
		return new ModelAndView("redirect:" + vipGiftUrl);

//			ModelAndView mav =getModel(request, "growth/list");
//			if(mav.getViewName().equals("redirect:"+loginUrl))
//	        {
//	            return mav;
//	        }
//			if(mav.getModel().isEmpty()){
//				return new ModelAndView("redirect:" + loginUrl);
//			}
//			String memGuid="";
//			if(mav.getModel().get("memGuid")!=null){
//				memGuid=mav.getModel().get("memGuid").toString();
//			}
//			String basePath="";
//			if(mav.getModel().get("basePath")!=null){
//				basePath=mav.getModel().get("basePath").toString();
//			}
//			mav.addObject("wwwUrl", wwwUrl);
//			mav.addObject("levelDesc", ConstantGrowth.GET_LEVEL_DESC);
//			mav.addObject("level0", ConstantGrowth.LEVEL_OF_0);
//			mav.addObject("level1", ConstantGrowth.LEVEL_OF_1);
//			mav.addObject("levelP", ConstantGrowth.LEVEL_OF_P);
//			mav.addObject("imgBaseUrl", imgBaseUrl);
//			String[] headUrls = headImgUrl.split(",");
//			mav.addObject("headImgUrl", headUrls[new Random().nextInt(headUrls.length)]);
//			mav.addObject("defaultHeadImg",defaultHeadImg);
//			mav.addObject("instructionUrl",instructionUrl);
//			mav.addObject("storeDomain",storeDomain);
//
//			try {
//	            MultiValueMap<String, String> memparams = new LinkedMultiValueMap<>();
//	            memparams.add("name", memGuid);
//	            memparams.add("type", "5");
//	            String resultJson = restTemplate.postForObject(searchMemberApi, memparams, String.class);
//	            if (!StringUtils.isBlank(resultJson)) {
//	                JSONObject resultObj = JSONObject.parseObject(resultJson);
//	                JSONObject data = resultObj.getJSONObject("data");
//	                if (data != null) {
//	                	String headImage = data.getString("HEAD_PORTRAIT");
//	                	mav.addObject("headImage", headImage);
//					}
//				}
//			} catch (Exception e) {
//				errorLog("查询用户信息头像失败",e);
//			}
//			String url = basePath+"growth/list";
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("memGuid", memGuid);
//
//			try {
//				// String memGrowthInfo=HttpRequestUtil.sendPost(memGrowthInfoApi,
//				// HttpRequestUtil.assemblyJsonParam(params),"");
//				String memGrowthInfo = restTemplate
//						.postForObject(memGrowthInfoApi,
//								HttpRequestUtil.assemblyJsonParam(params),
//								String.class);
//				Result growthInfoReslut = JSONObject.parseObject(memGrowthInfo,
//						Result.class);
//				if (growthInfoReslut.getData() != null) {
//					mav.addObject("memResult", growthInfoReslut.getData());
//				} else {
//					JSONObject memReslutJson = new JSONObject();
//					memReslutJson.put("overPercent", "0%");
//					memReslutJson.put("memLevel", "T0");
//					memReslutJson.put("growthValue", "0");
//					memReslutJson.put("expiryDate", "永久");
//					mav.addObject("memResult", memReslutJson);
//				}
//			} catch (Exception e) {
//				errorLog("查询用户成长值信息失败",e);
//			}
//			try {
//				//String pageNo = request.getParameter("pageno");
//				//String pageSize = request.getParameter("pagesize");
//				String pageSize = "15";
//				//String gridType = request.getParameter("gridType");
//				if (StringUtils.isEmpty(gridType)
//						|| (!IsNumberUtil.isInteger(gridType))) {
//					gridType = "0";
//				}
//				mav.addObject("gridType", gridType);
//				SimpleDateFormat sdf = new SimpleDateFormat(
//						"yyyy-MM-dd HH:mm:ss");
//				Calendar calendarBefore = Calendar.getInstance();
//				calendarBefore.setTime(new Date());
//				calendarBefore.add(Calendar.MONTH, -1);
//				calendarBefore.add(Calendar.DATE,1);
//				calendarBefore.set(Calendar.HOUR, 0);
//				calendarBefore.set(Calendar.MINUTE, 0);
//				calendarBefore.set(Calendar.SECOND, 0);
//				calendarBefore.set(Calendar.MILLISECOND,0);
//				String startDate = sdf.format(calendarBefore.getTime());
//				String endDate = sdf.format(new Date());
//				if (StringUtils.isEmpty(pageNo)
//						|| (!IsNumberUtil.isInteger(pageNo))) {
//					pageNo = "1";
//				}
//				if(StringUtils.equals(gridType,"0")) {
//					Map thisMonthMap = getGrowthByDate(memGuid, startDate, endDate,
//							pageNo, pageSize, url, "0", true);
//					mav.addObject("detailResult",
//							thisMonthMap.get("growthDetailList"));
//					mav.addObject("pageData", thisMonthMap.get("pageData"));
//				}
//				if(StringUtils.equals(gridType,"1")) {
//					String startDateBefore = "0000-00-00 00:00:00";
//					String endDateBefore = sdf.format(calendarBefore.getTime());
//					Map beforeMonthMap = getGrowthByDate(memGuid, startDateBefore,
//							endDateBefore, pageNo, pageSize, url, "1", true);
//					mav.addObject("detailResultBefore",
//							beforeMonthMap.get("growthDetailList"));
//					mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
//				}
//				mav.addObject("goUrl", url);
//			} catch (Exception e) {
//				errorLog("查询用户成长值明细失败",e);
//			}
//			try {
//				MultiValueMap<String, Object> mrstUiParams = new LinkedMultiValueMap<String, Object>();
//				JSONObject MrstUiJson = new JSONObject();
//				MrstUiJson.put("memGuid", memGuid);
//				MrstUiJson.put("isCancel", ConstantGrowth.IS_F_STR);
//				mrstUiParams.add("data", MrstUiJson.toString());
//				String mrstUiResult = restTemplate.postForObject(
//						getMrstUiListApi, mrstUiParams, String.class);
//				mav.addObject("mrstUiResult", JSONObject.parse(mrstUiResult));
//				if (mrstUiResult != null) {
//					JSONObject uiJson = JSONObject.parseObject(mrstUiResult);
//					JSONObject dataJson = uiJson.getJSONObject("data");
//					JSONArray mrstUiArrs = dataJson.getJSONArray("mrstUiList");
//					String[] mrstUiStrs = mrstUiArrs
//							.toArray(new String[mrstUiArrs.size()]);
//					Map<String, Object> mrstUiMap = new HashMap<String, Object>();
//					for (String mrstui : mrstUiStrs) {
//						mrstUiMap.put(mrstui, "1");
//					}
//					mav.addObject("mrstUiMap", JSONObject.toJSON(mrstUiMap));
//				}
//			} catch (Exception e) {
//				errorLog("查询已点亮权益失败", e);
//			}
//	        mav.addObject("uiForShort", ConstantGrowth.GET_UI_FOR_SHORT_DESC);
//
//	        try {
//				MultiValueMap<String, Object> getNoTakeParams = new LinkedMultiValueMap<String, Object>();
//				JSONObject noTokenJson = new JSONObject();
//				noTokenJson.put("memGuid", memGuid);
//				noTokenJson.put("pageNo", 1);
//				noTokenJson.put("pageSize",
//						ConstantGrowth.PKAD_MAX_PAGE_SIZE);
//				noTokenJson.put("isExpire", ConstantGrowth.IS_F_STR);
//				noTokenJson.put("isTake", ConstantGrowth.IS_F_STR);
//				noTokenJson.put("isCancel", ConstantGrowth.IS_F_STR);
//				getNoTakeParams.add("data", noTokenJson.toString());
//				String noTakeResult = restTemplate.postForObject(
//						getPkadListCountApi, getNoTakeParams, String.class);
//				mav.addObject("noTakeResult", JSONObject.parse(noTakeResult));
//			} catch (Exception e) {
//				errorLog("查询未领取礼包失败", e);
//			}
//			return mav;
	}

	public Map getGrowthByDate(String memGuid, String startDate,
                               String endDate, String pageNo, String pageSize, String url,
                               String gridType, boolean isWithDetail) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("memGuid", memGuid);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		// MultiValueMap<String,String> paramsMap = new LinkedMultiValueMap<>();
		// paramsMap.add("data", JSONObject.toJSONString(params));
		String levelCount = restTemplate.postForObject(RecordCountApi,
				HttpRequestUtil.assemblyJsonParam(params), String.class);
		// String levelCount=HttpRequestUtil.sendPost(RecordCountApi,
		// HttpRequestUtil.assemblyJsonParam(params),"");

		Result countReslut = JSONObject.parseObject(levelCount, Result.class);
		JSONObject dataJson = JSONObject.parseObject(countReslut.getData()
				.toString());
		Integer totalSum = (Integer) dataJson.get("TotalItems");

		if (totalSum >= 0) {
			if (StringUtils.isEmpty(pageNo)
					|| (!IsNumberUtil.isInteger(pageNo))) {
				pageNo = "1";
			}
			if (Integer.parseInt(pageNo) < 1) {
				pageNo = "1";
			}

			if (StringUtils.isEmpty(pageSize)
					|| (!IsNumberUtil.isInteger(pageSize))) {
				pageSize = "15";
			}
			if (Integer.parseInt(pageSize) < 1) {
				pageSize = "15";
			}
			Double totalPage = Math.ceil(totalSum.doubleValue()
					/ Double.valueOf(pageSize));
			Integer total = totalPage.intValue();
			if (Integer.parseInt(pageNo) > total && total>=1) {
				pageNo = total.toString();
			}
			params.put("PageIndex", Integer.parseInt(pageNo));
			params.put("RowCount", Integer.parseInt(pageSize));
			if(isWithDetail) {
				String getGrowthDetailList = restTemplate.postForObject(
						GrowthDetailListApi,
						HttpRequestUtil.assemblyJsonParam(params), String.class);

				Result detailListReslut = JSONObject.parseObject(
						getGrowthDetailList, Result.class);
				returnMap.put("growthDetailList", detailListReslut.getData());
			}
			PageUtil pagedata = new PageUtil(Integer.parseInt(pageNo),
					totalSum, Integer.parseInt(pageSize), url);

			returnMap.put("pageData", paging(pagedata, gridType));
		}
		return returnMap;
	}

	public Map paging(PageUtil data, String gridType) {
		Map<String, Object> pageMap = new HashMap<String, Object>();
		if (data.getPageSize() != 0 && data.getTotalSum() > 0) {
			if (data.getPageNo() == null) {
				data.setPageNo(1);
			}
			if (data.getPageSize() == null) {
				data.setPageSize(15);
			}
			Double totalPage = Math.ceil(data.getTotalSum().doubleValue()
					/ data.getPageSize().doubleValue());
			data.setTotalPage(totalPage.intValue());

			if (data.getPageNo() > totalPage.intValue()) {
				data.setPageNo(totalPage.intValue());
			}
			if (data.getPageNo() <= 1) {
				pageMap.put("fn_prve", "0");
				pageMap.put("pre_href", "");
			} else {
				pageMap.put("fn_prve", "1");
				pageMap.put("pre_href",data.getUrl() + "/" + gridType+"/"+(data.getPageNo() - 1));
			}

			pageMap.put("pageNo", data.getPageNo());
			pageMap.put("totalpage", data.getTotalPage());

			if (data.getPageNo() >= data.getTotalPage()) {
				pageMap.put("fn_next", "0");
				pageMap.put("next_href", "");
			} else {
				pageMap.put("fn_next", "1");
				pageMap.put("next_href",
						data.getUrl() + "/" + gridType+"/"+(data.getPageNo() + 1));
			}
			pageMap.put("goUrl", data.getUrl());
			pageMap.put("gridType", gridType);
		}
		return pageMap;
	}

	
	/*@RequestMapping(value="/pkad",method = RequestMethod.GET)
	public ModelAndView pkad(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

			ModelAndView mav =getModel(request, "growth/pkad");
			if(mav.getViewName().equals("redirect:"+loginUrl))
	        {
	            return mav;
	        }
			if(mav.getModel().isEmpty()){
				return new ModelAndView("redirect:" + loginUrl);
			}
			String memGuid="";
			if(mav.getModel().get("memGuid")!=null){
				memGuid=mav.getModel().get("memGuid").toString();
			}String basePath="";
			if(mav.getModel().get("basePath")!=null){
				basePath=mav.getModel().get("basePath").toString();
			}
			mav.addObject("wwwUrl", wwwUrl);
			mav.addObject("safeUrl", safeUrl);
			mav.addObject("levelDesc", ConstantGrowth.GET_LEVEL_DESC);
			mav.addObject("level0", ConstantGrowth.LEVEL_OF_0);
			mav.addObject("level1", ConstantGrowth.LEVEL_OF_1);
			mav.addObject("imgBaseUrl", imgBaseUrl);
			String[] headUrls = headImgUrl.split(",");
			mav.addObject("headImgUrl", headUrls[new Random().nextInt(headUrls.length)]);
			mav.addObject("defaultHeadImg",defaultHeadImg);
			
			mav.addObject("storeDomain",storeDomain);
			try {
				MultiValueMap<String, String> memparams = new LinkedMultiValueMap<>();
				memparams.add("name", memGuid);
				memparams.add("type", "5");
	            String resultJson = restTemplate.postForObject(searchMemberApi, memparams, String.class);
	            if (!StringUtils.isBlank(resultJson)) {
	                JSONObject resultObj = JSONObject.parseObject(resultJson);
	                JSONObject data = resultObj.getJSONObject("data");
	                if (data != null) {
	                	String headImage = data.getString("HEAD_PORTRAIT");
	                	mav.addObject("headImage", headImage);
					}
				}
			} catch (Exception e) {
				errorLog("查询用户信息头像失败",e);
			}
			String url = basePath+"growth/pkad";
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("memGuid", memGuid);
				String memGrowthInfo = restTemplate
						.postForObject(memGrowthInfoApi,
								HttpRequestUtil.assemblyJsonParam(params),
								String.class);
				Result growthInfoReslut = JSONObject.parseObject(memGrowthInfo,
						Result.class);
				if (growthInfoReslut.getData() != null) {
					mav.addObject("memResult", growthInfoReslut.getData());
				} else {
					JSONObject memReslutJson = new JSONObject();
					memReslutJson.put("overPercent", "0%");
					memReslutJson.put("memLevel", "T0");
					memReslutJson.put("growthValue", "0");
					memReslutJson.put("expiryDate", "永久");
					mav.addObject("memResult", memReslutJson);
				}
			} catch (Exception e) {
				errorLog("查询用户成长值信息失败",e);
			}
			try {
				MultiValueMap<String, Object> getNoTakeParams = new LinkedMultiValueMap<String, Object>();
				JSONObject noTokenJson = new JSONObject();
				noTokenJson.put("memGuid", memGuid);
				noTokenJson.put("pageNo", 1);
				noTokenJson.put("pageSize",
						ConstantGrowth.PKAD_MAX_PAGE_SIZE);
				noTokenJson.put("isExpire", ConstantGrowth.IS_F_STR);
				noTokenJson.put("isTake", ConstantGrowth.IS_F_STR);
				noTokenJson.put("isCancel", ConstantGrowth.IS_F_STR);
				noTokenJson.put("order","d_take_f");
				noTokenJson.put("sortType", "desc");
				getNoTakeParams.add("data", noTokenJson.toString());
				String noTakeResult = restTemplate.postForObject(
						getPkadListApi, getNoTakeParams, String.class);
				mav.addObject("noTakeResult", JSONObject.parse(noTakeResult));
			} catch (Exception e) {
				errorLog("查询未领取礼包失败",e);
			}
			try {
				MultiValueMap<String, Object> getTakenParams = new LinkedMultiValueMap<String, Object>();
				JSONObject takenJson = new JSONObject();
				takenJson.put("memGuid", memGuid);
				takenJson.put("pageNo", 1);
				takenJson.put("pageSize", ConstantGrowth.PKAD_DEFAULT_PAGE_SIZE);
				takenJson.put("isTake", ConstantGrowth.IS_T_STR);
				takenJson.put("order","d_take");
				takenJson.put("sortType", "desc");
				getTakenParams.add("data", takenJson.toString());
				String takenResult = restTemplate.postForObject(getPkadListApi,
						getTakenParams, String.class);
				mav.addObject("takenResult", JSONObject.parse(takenResult));
			} catch (Exception e) {
				errorLog("查询已领取礼包失败",e);
			}
			try {
				MultiValueMap<String, Object> getExpiredParams = new LinkedMultiValueMap<String, Object>();
				JSONObject expiredJson = new JSONObject();
				expiredJson.put("memGuid", memGuid);
				expiredJson.put("pageNo", 1);
				expiredJson.put("pageSize",
						ConstantGrowth.PKAD_DEFAULT_PAGE_SIZE);
				expiredJson.put("isExpire", ConstantGrowth.IS_T_STR);
				expiredJson.put("isTake", ConstantGrowth.IS_F_STR);
				expiredJson.put("isCancel", ConstantGrowth.IS_F_STR);
				expiredJson.put("order","d_take_t");
				expiredJson.put("sortType", "desc");
				getExpiredParams.add("data", expiredJson.toString());
				String expiredResult = restTemplate.postForObject(
						getPkadListApi, getExpiredParams, String.class);
				mav.addObject("expiredResult", JSONObject.parse(expiredResult));
			} catch (Exception e) {
				errorLog("查询未过期礼包失败",e);
			}
			try {
				MultiValueMap<String, Object> mrstUiParams = new LinkedMultiValueMap<String, Object>();
				JSONObject MrstUiJson = new JSONObject();
				MrstUiJson.put("memGuid", memGuid);
				MrstUiJson.put("pageNo", 1);
				MrstUiJson.put("pageSize",
						ConstantGrowth.PKAD_DEFAULT_PAGE_SIZE);
				MrstUiJson.put("isCancel", ConstantGrowth.IS_F_STR);
				mrstUiParams.add("data", MrstUiJson.toString());
				String mrstUiResult = restTemplate.postForObject(
						getMrstUiListApi, mrstUiParams, String.class);
				mav.addObject("mrstUiResult", JSONObject.parse(mrstUiResult));
				if (mrstUiResult != null) {
					JSONObject uiJson = JSONObject.parseObject(mrstUiResult);
					JSONObject dataJson = uiJson.getJSONObject("data");
					JSONArray mrstUiArrs = dataJson.getJSONArray("mrstUiList");
					String[] mrstUiStrs = mrstUiArrs
							.toArray(new String[mrstUiArrs.size()]);
					Map<String, Object> mrstUiMap = new HashMap<String, Object>();
					for (String mrstui : mrstUiStrs) {
						mrstUiMap.put(mrstui, "1");
					}
					mav.addObject("mrstUiMap", JSONObject.toJSON(mrstUiMap));
				}
			} catch (Exception e) {
				errorLog("查询已点亮权益失败", e);
			}
	        
	        mav.addObject("uiDesc", ConstantGrowth.GET_MRSTUI_DESC);
	        mav.addObject("uiForShort", ConstantGrowth.GET_UI_FOR_SHORT_DESC);
	        mav.addObject("uiForBtnDateType", ConstantGrowth.GET_MRSTUI_FOR_BTN_DATE_TYPE);
	        mav.addObject("uiForDiaDateType", ConstantGrowth.GET_MRSTUI_FOR_DIALOG_DATE_TYPE);
	        if(request.getParameter("cur")!=null&&!StringUtils.isBlank(request.getParameter("cur"))){
	        	mav.addObject("curTab", request.getParameter("cur"));
	        };
			return mav;
	}*/
	
	@RequestMapping(value="/pkad",method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView pkad(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
		return new ModelAndView("redirect:" + vipGradeUrl);
//			ModelAndView mav =getModel(request, "growth/pkad");
//			if(mav.getViewName().equals("redirect:"+loginUrl))
//	        {
//	            return mav;
//	        }
//			if(mav.getModel().isEmpty()){
//				return new ModelAndView("redirect:" + loginUrl);
//			}
//			String memGuid="";
//			if(mav.getModel().get("memGuid")!=null){
//				memGuid=mav.getModel().get("memGuid").toString();
//			}String basePath="";
//			if(mav.getModel().get("basePath")!=null){
//				basePath=mav.getModel().get("basePath").toString();
//			}
//			mav.addObject("wwwUrl", wwwUrl);
//			mav.addObject("safeUrl", safeUrl);
//			mav.addObject("levelDesc", ConstantGrowth.GET_LEVEL_DESC);
//			mav.addObject("level0", ConstantGrowth.LEVEL_OF_0);
//			mav.addObject("level1", ConstantGrowth.LEVEL_OF_1);
//			mav.addObject("levelP", ConstantGrowth.LEVEL_OF_P);
//			mav.addObject("imgBaseUrl", imgBaseUrl);
//			String[] headUrls = headImgUrl.split(",");
//			mav.addObject("headImgUrl", headUrls[new Random().nextInt(headUrls.length)]);
//			mav.addObject("defaultHeadImg",defaultHeadImg);
//
//			mav.addObject("storeDomain",storeDomain);
//			try {
//				MultiValueMap<String, String> memparams = new LinkedMultiValueMap<>();
//				memparams.add("name", memGuid);
//				memparams.add("type", "5");
//	            String resultJson = restTemplate.postForObject(searchMemberApi, memparams, String.class);
//	            if (!StringUtils.isBlank(resultJson)) {
//	                JSONObject resultObj = JSONObject.parseObject(resultJson);
//	                JSONObject data = resultObj.getJSONObject("data");
//	                if (data != null) {
//	                	String headImage = data.getString("HEAD_PORTRAIT");
//	                	mav.addObject("headImage", headImage);
//					}
//				}
//			} catch (Exception e) {
//				errorLog("查询用户信息头像失败",e);
//			}
//			String url = basePath+"growth/pkad";
//			try {
//				Map<String, Object> params = new HashMap<String, Object>();
//				params.put("memGuid", memGuid);
//				String memGrowthInfo = restTemplate
//						.postForObject(memGrowthInfoApi,
//								HttpRequestUtil.assemblyJsonParam(params),
//								String.class);
//				Result growthInfoReslut = JSONObject.parseObject(memGrowthInfo,
//						Result.class);
//				if (growthInfoReslut.getData() != null) {
//					mav.addObject("memResult", growthInfoReslut.getData());
//				} else {
//					JSONObject memReslutJson = new JSONObject();
//					memReslutJson.put("overPercent", "0%");
//					memReslutJson.put("memLevel", "T0");
//					memReslutJson.put("growthValue", "0");
//					memReslutJson.put("expiryDate", "永久");
//					mav.addObject("memResult", memReslutJson);
//				}
//			} catch (Exception e) {
//				errorLog("查询用户成长值信息失败",e);
//			}
//			try {
//				MultiValueMap<String, Object> getNoTakeParams = new LinkedMultiValueMap<String, Object>();
//				JSONObject noTokenJson = new JSONObject();
//				noTokenJson.put("memGuid", memGuid);
//				noTokenJson.put("pageNo", 1);
//				noTokenJson.put("pageSize",
//						ConstantGrowth.PKAD_MAX_PAGE_SIZE);
//				noTokenJson.put("isExpire", ConstantGrowth.IS_F_STR);
//				noTokenJson.put("isTake", ConstantGrowth.IS_F_STR);
//				noTokenJson.put("isCancel", ConstantGrowth.IS_F_STR);
//				noTokenJson.put("order","d_take_f");
//				noTokenJson.put("sortType", "desc");
//				getNoTakeParams.add("data", noTokenJson.toString());
//				String noTakeResult = restTemplate.postForObject(
//						getPkadListApi, getNoTakeParams, String.class);
//				mav.addObject("noTakeResult", JSONObject.parseObject(noTakeResult));
//			} catch (Exception e) {
//				errorLog("查询未领取礼包失败",e);
//			}
//			try {
//				MultiValueMap<String, Object> getTakenParams = new LinkedMultiValueMap<String, Object>();
//				JSONObject takenJson = new JSONObject();
//				takenJson.put("memGuid", memGuid);
//				takenJson.put("pageNo", 1);
//				takenJson.put("pageSize", ConstantGrowth.PKAD_DEFAULT_SHOW_SIZE);
//				takenJson.put("isTake", ConstantGrowth.IS_T_STR);
//				takenJson.put("order","d_take");
//				takenJson.put("sortType", "desc");
//				getTakenParams.add("data", takenJson.toString());
//				String takenResult = restTemplate.postForObject(getPkadListApi,
//						getTakenParams, String.class);
//				JSONObject takenResultJson = populateContent(memGuid,
//						takenResult,true);
//				mav.addObject("takenResult", takenResultJson);
//			} catch (Exception e) {
//				errorLog("查询已领取礼包失败",e);
//			}
//			try {
//				MultiValueMap<String, Object> getExpiredParams = new LinkedMultiValueMap<String, Object>();
//				JSONObject expiredJson = new JSONObject();
//				expiredJson.put("memGuid", memGuid);
//				expiredJson.put("pageNo", 1);
//				expiredJson.put("pageSize",
//						ConstantGrowth.PKAD_DEFAULT_SHOW_SIZE);
//				expiredJson.put("isExpire", ConstantGrowth.IS_T_STR);
//				expiredJson.put("isTake", ConstantGrowth.IS_F_STR);
//				expiredJson.put("isCancel", ConstantGrowth.IS_F_STR);
//				expiredJson.put("order","d_take_t");
//				expiredJson.put("sortType", "desc");
//				getExpiredParams.add("data", expiredJson.toString());
//				String expiredResult = restTemplate.postForObject(
//						getPkadListApi, getExpiredParams, String.class);
//				JSONObject expiredResultJson = populateContent(memGuid,
//						expiredResult,false);
//				mav.addObject("expiredResult", expiredResultJson);
//			} catch (Exception e) {
//				errorLog("查询已过期礼包失败",e);
//			}
//			try {
//				MultiValueMap<String, Object> mrstUiParams = new LinkedMultiValueMap<String, Object>();
//				JSONObject MrstUiJson = new JSONObject();
//				MrstUiJson.put("memGuid", memGuid);
//				MrstUiJson.put("pageNo", 1);
//				MrstUiJson.put("pageSize",
//						ConstantGrowth.PKAD_DEFAULT_PAGE_SIZE);
//				MrstUiJson.put("isCancel", ConstantGrowth.IS_F_STR);
//				mrstUiParams.add("data", MrstUiJson.toString());
//				String mrstUiResult = restTemplate.postForObject(
//						getMrstUiListApi, mrstUiParams, String.class);
//				mav.addObject("mrstUiResult", JSONObject.parse(mrstUiResult));
//				if (mrstUiResult != null) {
//					JSONObject uiJson = JSONObject.parseObject(mrstUiResult);
//					JSONObject dataJson = uiJson.getJSONObject("data");
//					JSONArray mrstUiArrs = dataJson.getJSONArray("mrstUiList");
//					String[] mrstUiStrs = mrstUiArrs
//							.toArray(new String[mrstUiArrs.size()]);
//					Map<String, Object> mrstUiMap = new HashMap<String, Object>();
//					for (String mrstui : mrstUiStrs) {
//						mrstUiMap.put(mrstui, "1");
//					}
//					mav.addObject("mrstUiMap", JSONObject.toJSON(mrstUiMap));
//				}
//			} catch (Exception e) {
//				errorLog("查询已点亮权益失败", e);
//			}
//	        mav.addObject("uiForShort", ConstantGrowth.GET_UI_FOR_SHORT_DESC);
//	        mav.addObject("mrstuiForBgimg", ConstantGrowth.GET_MRSTUI_FOR_BGIMG);
//	        mav.addObject("mrstuiForNotakenBgimg", ConstantGrowth.GET_MRSTUI_FOR_NOTAKEN_BGIMG);
//	        mav.addObject("uiForBtnDateType", ConstantGrowth.GET_MRSTUI_FOR_BTN_DATE_TYPE);
//	        mav.addObject("uiForDiaDateType", ConstantGrowth.GET_MRSTUI_FOR_DIALOG_DATE_TYPE);
//	        if(request.getParameter("cur")!=null&&!StringUtils.isBlank(request.getParameter("cur"))){
//	        	mav.addObject("curTab", request.getParameter("cur"));
//	        };
//			return mav;
	}
	
	private JSONObject populateContent(String memGuid, String packetResult, boolean ifTaken) {
		JSONObject packetResultJson=JSONObject.parseObject(packetResult);
		JSONArray pkadList = packetResultJson.getJSONObject("data").getJSONArray("pkadList");
		for(int i=0;i<pkadList.size();i++){
			JSONObject carsContentInfo=new JSONObject();
			List<String> content=new ArrayList<String>();
			JSONObject pkad=pkadList.getJSONObject(i);
			String pkadSeq=pkad.getString("pkadSeq");
			JSONArray cardInfoJsonArray=pkad.getJSONArray("cardInfo");
			if(cardInfoJsonArray!=null){
			JSONObject firstCardInfoJson=cardInfoJsonArray.getJSONObject(0);
			String mrdfType=firstCardInfoJson==null?null:firstCardInfoJson.getString("mrdf_type");
			boolean ifPickOneFromThree=false;
			if(mrdfType!=null &&"C3".equals(mrdfType)){
				ifPickOneFromThree=true;
			}
			if(pkadSeq!=null){
				JSONObject paramJson=new JSONObject();
				paramJson.put("memGuid",memGuid);
				paramJson.put("pkadSeq",pkadSeq );
				MultiValueMap<String, Object> getTakenCardInfoParams = new LinkedMultiValueMap<String, Object>();
				getTakenCardInfoParams.add("data", paramJson.toString());
				String cardInfo =null;
				try{
					if(ifTaken){
						cardInfo= restTemplate.postForObject(getTakenCardInfoApi,
								getTakenCardInfoParams, String.class);
					}else{
						cardInfo= restTemplate.postForObject(getCardInfoApi,
								getTakenCardInfoParams, String.class);
					}
					
				}catch(Exception e){
					log.error("查询卡券信息失败",e);
				}
				JSONObject cardInfoJson=JSONObject.parseObject(cardInfo);
				if("100".equals(cardInfoJson.getString("code"))){
				JSONArray data=cardInfoJson.getJSONArray("data");
				for(int j=0;j<data.size();j++){
					JSONObject card=data.getJSONObject(j);
					String cardType=card.getString("cardType");
					String price=card.getString("price");
					String discount=card.getString("discount");
					String contextStr="";
					if(price==null||"".equals(price)||"0".equals(price)){
						contextStr="立减"+discount+"元";
					}else{
						contextStr="满"+price+"减"+discount;
					}
					if(discount!=null&&!"".equals(discount)&&!"0".equals(discount)) {
						if ("1".equals(cardType)) {
							content.add(contextStr + "抵用券");
						} else if ("2".equals(cardType)||"6".equals(cardType)) {
							content.add(contextStr + "优惠券");
						} else if("5".equals(cardType)||"25".equals(cardType)){
							content.add(contextStr + "免邮券");
						}else if("29".equals(cardType)||"22".equals(cardType)){
							content.add(contextStr+"商城优惠券");
						}else if("6".equals(cardType)){
							content.add(contextStr + "品牌券");
						}else if("7".equals(cardType)){
							content.add(contextStr + "礼品券");
						}
					}
				}
			}else{
				log.error("查询卡券信息失败");
			}
			/*if(pointInfo!=null){
				for(int k=0;k<pointInfo.size();k++){
					JSONObject point=pointInfo.getJSONObject(k);
					String mrdfPoint=point.getString("mrdf_point");
					String mrdfGrowth=point.getString("mrdf_growth");
					if(!StringUtils.isEmpty(mrdfPoint)){
						pkad.put("point", mrdfPoint+"积分");
					}else if(!StringUtils.isEmpty(mrdfGrowth)){
						pkad.put("growthPoint",mrdfGrowth+"成长值");
					}
				}
			}*/
			carsContentInfo.put("cardInfo", content);
			carsContentInfo.put("ifC3", ifPickOneFromThree);
			pkad.put("carsContentInfo", carsContentInfo);
			}else{
				log.error("查询礼包序列号信息失败");
			}
		}
		}
		return packetResultJson;
	}
	
	
	@RequestMapping(value="/getCardInfo",method = RequestMethod.POST)
    @ResponseBody
	public Map<String,String> getCardInfo(HttpServletRequest request,
                                          HttpServletResponse response, String pkadSeq) throws Exception {
		try {
			MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
			JSONObject json = new JSONObject();
			String memGuid=getGuid(request);
			if(StringUtils.isBlank(memGuid)){
				return ajaxRet(ResultCodeWeb.RESULT_EXCEPTION, "获取用户信息失败，请重新登录");
			}
			json.put("memGuid", memGuid);
	        json.put("pkadSeq",pkadSeq);
			params.add("data", json.toString());
			String returnStr = restTemplate.postForObject(getCardInfoApi,
					params, String.class);
//			String returnStr ="{\"code\":505,\"msg\":\"查询卡券返回值转换为json格式错误\",\"data\":null}";
//			String returnStr ="{\"code\":504,\"msg\":\"该礼包不包含卡券\",\"data\":null}";
			if (returnStr != null) {
				JSONObject returnObj = null;
				try {
					returnObj = (JSONObject) JSONObject.parse(returnStr);
				} catch (Exception e) {
					return ajaxRet(
							ResultCodeWeb.RESULT_IN_PARA_ILLEGAL_EXCEPTION,
							"查询卡券返回值解析错误");
				}
				if (returnObj != null && returnObj.get("data") != null) {
					String returnData=returnObj.getString("data");
					return ajaxRet(ResultCodeWeb.RESULT_SUCCESS,returnData);
				}
				if (returnObj != null && returnObj.get("code") != null) {
					Integer returnCode=returnObj.getInteger("code");
					if(returnCode==504){ //不包含礼券的情况，返回空jsonArr，防止js解析出错
						JSONArray ja=new JSONArray();
						return ajaxRet(ResultCodeWeb.RESULT_SUCCESS,ja.toJSONString());
					}
				}
			}
			return ajaxRet(ResultCodeWeb.RESULT_EXCEPTION, "查询礼包卡券信息失败");
		} catch (Exception e) {
			log.error("查询礼包卡券信息失败",e);
			return ajaxRet(ResultCodeWeb.RESULT_EXCEPTION, "查询礼包卡券信息失败");
		}
	}
	/*
	@RequestMapping(value="/pkad/getNoTake",method = RequestMethod.GET)
	@ResponseBody
	public String getNoTakePkad(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
	        JSONObject json = new JSONObject();
	        json.put("memGuid",getGuid(request));
	        json.put("pageNo", 1);
	        json.put("pageSize", ConstantGrowth.PKAD_MAX_PAGE_SIZE);
	        json.put("isExpire", ConstantGrowth.IS_F_STR);
	        json.put("isTake", ConstantGrowth.IS_F_STR);
	        json.put("isCancel", ConstantGrowth.IS_F_STR);
	        params.add("data", json.toString());
	        String result = restTemplate.postForObject(getPkadListApi, params, String.class);
	        return result;
		} catch (Exception e) {
			errorLog("查询未领取礼包失败",e);
			return null;
		}
	}
	@RequestMapping(value="/pkad/getTaken",method = RequestMethod.GET)
	@ResponseBody
	public String getTakenPkad(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try{
			MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
	        JSONObject json = new JSONObject();
	        json.put("memGuid",getGuid(request));
	        json.put("pageNo", 1);
	        json.put("pageSize", ConstantGrowth.PKAD_DEFAULT_PAGE_SIZE);
	        json.put("isTake",ConstantGrowth.IS_T_STR);
	        params.add("data", json.toString());
	        String result = restTemplate.postForObject(getPkadListApi, params, String.class);
	        return result;
		} catch (Exception e) {
			errorLog("查询已领取礼包失败",e);
			return null;
		}
	}
	@RequestMapping(value="/pkad/getExpired",method = RequestMethod.GET)
	@ResponseBody
	public String getExpiredPkad(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try{
			MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
	        JSONObject json = new JSONObject();
	        json.put("memGuid",getGuid(request));
	        json.put("pageNo", 1);
	        json.put("pageSize", ConstantGrowth.PKAD_DEFAULT_PAGE_SIZE);
	        json.put("isExpire", ConstantGrowth.IS_T_STR);
	        json.put("isTake", ConstantGrowth.IS_F_STR);
	        json.put("isCancel", ConstantGrowth.IS_F_STR);
	        params.add("data", json.toString());
	        String result = restTemplate.postForObject(getPkadListApi, params, String.class);
	        return result;
		} catch (Exception e) {
			errorLog("查询已领取礼包失败",e);
			return null;
		}
	}

	@RequestMapping(value="/pkad/getAll",method = RequestMethod.GET)
	@ResponseBody
	public String getAll(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject json = new JSONObject();
        json.put("memGuid",getGuid(request));
        json.put("pageNo", 1);
        json.put("pageSize", ConstantGrowth.PKAD_DEFAULT_PAGE_SIZE);
        json.put("isCancel", ConstantGrowth.IS_F_STR);
        params.add("data", json.toString());
        String result = restTemplate.postForObject(getPkadListApi, params, String.class);
        return result;
	}*/
	
	@RequestMapping(value="/pkad/takePkad",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> takePkad(HttpServletRequest request, String pkadSeq, String cardSeq, String cardId, String cardType){
		try{
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject json = new JSONObject();
		String memGuid=getGuid(request);
		if(StringUtils.isBlank(memGuid)){
			return ajaxRet(ResultCodeWeb.RESULT_EXCEPTION, "获取用户信息失败，请重新登录");
		}
        json.put("memGuid", memGuid);
		log.error("/pkad/takePkad入参 {memGuid=" + memGuid + ",pkadSeq = " + pkadSeq + ",cardSeq = " + cardSeq + ",cardId = " + cardId + ",cardType = " + cardType);
		json.put("pkadSeq", pkadSeq);
		if("null".equals(cardSeq)){
			cardSeq=null;
		}
		if("null".equals(cardId)){
			cardId=null;
		}
		if("null".equals(cardType)){
			cardType=null;
		}
        json.put("cardSeq", cardSeq);
        json.put("cardId", cardId);
        json.put("cardType", cardType); 
        params.add("data", json.toString()); 
        String returnStr = restTemplate.postForObject(takePkadApi, params, String.class);
		if (returnStr != null) {
			JSONObject returnObj = null;
			try {
				returnObj = (JSONObject) JSONObject.parse(returnStr);
			} catch (Exception e) {
				return ajaxRet(
						ResultCodeWeb.RESULT_IN_PARA_ILLEGAL_EXCEPTION,
						"领取礼包返回值解析错误");
			}
			if (returnObj != null && returnObj.getInteger("code").equals(ResultCodeWeb.REST_SUCCESS)) {
				return ajaxRet(ResultCodeWeb.RESULT_SUCCESS,"领取礼包成功");
			}
			if (returnObj != null && returnObj.getInteger("code")!=null) {
				Integer codeInt= Integer.parseInt(returnObj.getString("code"));
				if(codeInt<100){
					return ajaxRet(returnObj.getString("code"),returnObj.getString("msg"));
				}else{
					return ajaxRet(ResultCodeWeb.RESULT_EXCEPTION, "领取礼包失败");
				}
			}
		}
		return ajaxRet(ResultCodeWeb.RESULT_EXCEPTION, "领取礼包");
		} catch (Exception e) {
			log.error("领取礼包失败",e);
			return ajaxRet(ResultCodeWeb.RESULT_EXCEPTION, "领取礼包失败");
		}
	}
	
	
	private Map<String, String> ajaxRet(String code, String info) {
		Map<String, String> mapRet = new HashMap<String, String>();
		mapRet.put("code", code);
		mapRet.put("info", info);
		return mapRet;
	}
	
}
