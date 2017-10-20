package com.feiniu.member.controller.point;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.controller.common.CommonController;
import com.feiniu.member.dto.Result;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.PointService;
import com.feiniu.member.util.IsNumberUtil;
import com.feiniu.member.util.PageUtil;
import com.feiniu.member.util.PicRandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping(value = "/point")
public class PointController extends CommonController{

	public static final CustomLog log = CustomLog.getLogger(PointController.class);

	
	@Value("${www.url}")
	private String wwwUrl;
	@Value("${safe.url}")
	private String safeUrl;
	@Value("${imgInside.url}")
    private String imgBaseUrl;
	@Value("${instruction.url}")
	private String instructionUrl;
	@Value("${storeDomain.url}")
    private String storeDomain;
	@Value("${store.url}")
    private String storeUrl;
	@Value("${getUserScoreInfo.api}")
    private String getUserScoreInfoApi;
	@Value("${getUserScoreDetailList.api}")
    private String getUserScoreDetailListApi;
	@Value("${getOrderDetail.api}")
	private String getOrderDetailApi;
	@Value("${getScoreBind.api}")
	private String getScoreBindApi;
	@Value("${my.url}")
	private String myUrl;
	@Value("${searchMember.api}")
	private String searchMemberApi;
	@Value("${exCard.history.api}")
	private String exCardHistoryApi;
	@Autowired
	private PointService pointService;

	private int pageSize=15;
	private static Random random=new Random();
	
	
	@RequestMapping(value="/pointlist",method = RequestMethod.GET)
	public ModelAndView pointlist(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

			ModelAndView mav =getModel(request, "point/index");
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
			mav.addObject("imgBaseUrl", PicRandomUtil.random(imgBaseUrl));
			mav.addObject("instructionUrl",instructionUrl);
			mav.addObject("storeDomain",storeDomain);
			mav.addObject("basePath",basePath);
			mav.addObject("safeUrl",safeUrl);
			
			String from=request.getParameter("from");
			String type=request.getParameter("type");
			String pageNo=request.getParameter("pageNo");
			String orderno=request.getParameter("orderno");
			String start=request.getParameter("start");
			String end=request.getParameter("end");
			
			if(StringUtils.isBlank(from)||!IsNumberUtil.isInteger(from)){
				from="0";
			}
			if(StringUtils.isBlank(type)||!IsNumberUtil.isInteger(type)){
				type="0";
			}
			String ajaxData="from="+from+"&type="+type;
			if(!StringUtils.isBlank(orderno)){
				ajaxData+="&orderno="+orderno;
			}
			if(!StringUtils.isBlank(start)){
				ajaxData+="&start="+start;
			}
			if(!StringUtils.isBlank(end)){
				ajaxData+="&end="+end;
			}
			if(!StringUtils.isBlank(pageNo)){
				ajaxData+="&pageNo="+pageNo;
			}
			mav.addObject("ajaxData",ajaxData);
			mav.addObject("searchUrl","from="+from+"&type="+type);
			mav.addObject("type",type);
			mav.addObject("activeType"+type,"active");
			mav.addObject("activeFrom"+from,"active");
			Integer phoneBindMemberStatus=null;
			Integer emailBindMemberStatus=null;
			try {
				MultiValueMap<String, String> memparams = new LinkedMultiValueMap<>();
	            memparams.add("name", memGuid);
	            memparams.add("type", "5");
	            String resultJson = restTemplate.postForObject(searchMemberApi, memparams, String.class);
	            if (!StringUtils.isBlank(resultJson)) {
	                JSONObject resultObj = JSONObject.parseObject(resultJson);
	                JSONObject data = resultObj.getJSONObject("data");
	                if (data != null) {
	                	phoneBindMemberStatus = data.getInteger("PHONE_BIND");//0表示绑定
	                	emailBindMemberStatus = data.getInteger("EMAIL_BIND");//0表示绑定
					}
				}
			} catch (Exception e) {
				log.error("查询用户是否绑定失败", e);
			}
			Integer phoneBindScoreDB=null;
			Integer emailBindScoreDB=null;
			try {
				 MultiValueMap<String, String> memparams = new LinkedMultiValueMap<>();
		         memparams.add("memGuid", memGuid);
		         String resultJson = restTemplate.postForObject(getScoreBindApi, memparams, String.class);
		            if (!StringUtils.isBlank(resultJson)) {
		                JSONObject resultObj = JSONObject.parseObject(resultJson);
		                JSONObject data = resultObj.getJSONObject("data");
		                if (data != null) {
		                	phoneBindScoreDB = data.getInteger("bindPhone");//1表示有绑定记录
		                	emailBindScoreDB = data.getInteger("bindEmail");//1表示有绑定记录
						}
					}
			} catch (Exception e) {
				log.error("查询用户是否绑定失败",e);
			}
        	mav.addObject("phoneBindMemberStatus", phoneBindMemberStatus);
        	mav.addObject("emailBindMemberStatus", emailBindMemberStatus);
        	mav.addObject("phoneBindScoreDB", phoneBindScoreDB);
        	mav.addObject("emailBindScoreDB", emailBindScoreDB);


		JSONObject userScoreInfo=pointService.getUserScoreInfo(memGuid);
		if (userScoreInfo != null) {
			mav.addObject("userScoreInfo", userScoreInfo);
		}
			return mav;
	}	
	
	
	@RequestMapping(value="/getScoreList",method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getScoreList(HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
		try{
			String from=request.getParameter("from");
			String type=request.getParameter("type");
			String pageNo=request.getParameter("pageNo");
			String orderno=request.getParameter("orderno");
			String start=request.getParameter("start");
			String end=request.getParameter("end");
			String commentno=request.getParameter("commentno");
			String memGuid=getGuid(request);
			int pageNoInt;
			if (StringUtils.isBlank(pageNo) || !IsNumberUtil.isInteger(pageNo) || Integer.parseInt(pageNo) < 1) {
				pageNoInt=1;
			}else{
				pageNoInt= Integer.parseInt(pageNo);
			}
			int typeInt;
			if(StringUtils.isBlank(type)||!IsNumberUtil.isInteger(type)){
				typeInt=0;
			}else{
				typeInt= Integer.parseInt(type);
			}
			Result result = pointService.getScoreList(from,typeInt,pageNoInt,pageSize,orderno,start,end,commentno,memGuid,false);
			if(StringUtils.isBlank(from)||!IsNumberUtil.isInteger(from)){
				from="0";
			}

			Integer fromInt= Integer.parseInt(from);
			if(result.getCode()==100){
				String ret="500";
				String msg="";
				JSONObject dataJson=(JSONObject) result.getData();
				if(dataJson.getInteger("totalNum")==null||dataJson.getInteger("totalNum")==0){
					switch (fromInt) {
					case 3:
						ret = "300";
						break;
					case 4:
						ret = "400";
						break;
					default:
						ret = "200";
						switch (typeInt) {
						case 0:
							break;
						case 1:
							if(fromInt==2) {
								msg="评论商品赚积分，速去<a href=\""+myUrl+"/comment/myCommentView\">评论</a>吧！";
							}else{
								msg="您还没有积分，赶快去<a href=\""+wwwUrl+"\">赚积分</a>吧！";
							}
							break;
						case 2:
							msg="积分可以当钱花啦，赶快去<a href=\""+wwwUrl+"\">试试</a>吧！";
							break;
						default:
							msg="您还没有积分，赶快去<a href=\""+wwwUrl+"\">赚积分</a>吧！";
							break;
						}
					}
					return ajaxRet(ret,msg,null,null);	
				}else{
					Integer totalSum=dataJson.getInteger("totalNum");
						String queryStr=request.getQueryString();
						String url =myUrl+"/point/pointlist?"+getQueryStrWithout(queryStr,"pageNo=");
	                	if (StringUtils.isEmpty(pageNo)
	        					|| (!IsNumberUtil.isInteger(pageNo))) {
	        				pageNo = "1";
	        			}
	        			if (Integer.parseInt(pageNo) < 1) {
	        				pageNo = "1";
	        			}
		    	        PageUtil pagedata = new PageUtil(Integer.parseInt(pageNo),totalSum, pageSize, url);
					return ajaxRet("200", result.getMsg(), String.valueOf(result.getData()),paging(pagedata));
				}
			}else{
				return ajaxRet("500", "网络失败，请刷新页面重试",null,null);
			}
		} catch (Exception e) {
			return ajaxRet("500", "网络失败，请刷新页面重试",null,null);
		}
	}
	
	@RequestMapping(value="/getScoreDetail",method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getScoreDetail(HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
		try{
			String orderno=request.getParameter("orderno");
			String commentno=request.getParameter("commentno");
			String memGuid=getGuid(request);
			if(StringUtils.isBlank(commentno)||!IsNumberUtil.isInteger(commentno)|| Integer.parseInt(commentno)<1){
				commentno="0";
			}
			
			StringBuffer paramStr=new StringBuffer("{");
			paramStr.append("memGuid:\""+memGuid+ "\",");
			paramStr.append("commentSeq:\""+ Integer.parseInt(commentno)+ "\",");
			if(!StringUtils.isBlank(orderno)){
				paramStr.append("ogSeq:\""+orderno+ "\",");
			}
			paramStr.delete(paramStr.length() - 1,paramStr.length());
			paramStr.append("}");
			
			String resultStr = restTemplate.getForObject(getOrderDetailApi+"?data={data}",String.class,paramStr);
			Result result = JSONObject.parseObject(resultStr,
					Result.class);
			if(result.getCode()==100){
				JSONArray dataArr = null;
				JSONObject dataObj= null;
				try {
					dataArr = (JSONArray) result.getData();
				} catch (Exception e) {
					dataObj = (JSONObject) result.getData();
				}
				if(dataArr==null||dataArr.isEmpty()){
					if(dataObj==null||dataObj.isEmpty()){
						return ajaxRet("200","未能找到相关数据",null,null);	
					}else{
						if(!"0".equals(commentno)) {
							dataObj.put("isCommentDetail","1");
						}
						return ajaxRet("200", result.getMsg(),dataObj.toJSONString(),null);
					}
				}else{
					return ajaxRet("200", result.getMsg(),dataArr.toJSONString(),null);
				}
			}else{
				return ajaxRet("500", "网络失败，请刷新页面重试",null,null);
			}
		} catch (Exception e) {
			return ajaxRet("500", "网络失败，请刷新页面重试",null,null);
		}
	}
	
	@RequestMapping(value="/cardlist",method = RequestMethod.GET)
	public ModelAndView pointCardList(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
		ModelAndView mav =getModel(request, "point/card_list");
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
		String pageNo=request.getParameter("pageNo");
		String ajaxData="pageNo="+pageNo;
		mav.addObject("ajaxData",ajaxData);
		mav.addObject("wwwUrl", wwwUrl);
		mav.addObject("imgBaseUrl", PicRandomUtil.random(imgBaseUrl));
		mav.addObject("instructionUrl",instructionUrl);
		mav.addObject("storeDomain",storeDomain);
		mav.addObject("basePath",basePath);
		mav.addObject("safeUrl",safeUrl);
		
		//storeUrl多取一
		String[] storeUrlSplit = storeUrl.split(";");
        int l=  storeUrlSplit.length ;
        int x =   random.nextInt(l);
        mav.addObject("storeUrl",storeUrlSplit[x]);
		return mav;
	}
	
	@RequestMapping(value="/getExCardHistory",method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getExCardHistory(HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
		try{
			 String pageNo=request.getParameter("pageNo");
			 if (StringUtils.isEmpty(pageNo)|| (!IsNumberUtil.isInteger(pageNo))) {
 				pageNo = "1";
 			}
 			if (Integer.parseInt(pageNo) < 1) {
 				pageNo = "1";
 			}
 			
			 String memGuid=getGuid(request);
			 MultiValueMap<String, String> dataparams = new LinkedMultiValueMap<>();
			 
			 JSONObject dataParams = new JSONObject();
			 dataParams.put("memGuid", memGuid);
	         dataParams.put("pageIndex", pageNo);
			 dataParams.put("onePageNumber", String.valueOf(pageSize));
			 
			 JSONObject inParams = new JSONObject();
			 inParams.put("data", dataParams);
			 
	         dataparams.add("data",JSONObject.toJSONString(inParams));
			String resultStr = restTemplate.postForObject(exCardHistoryApi, dataparams, String.class);
	         if(StringUtils.isBlank(resultStr)){
	        	 return ajaxRet("500", "网络失败，请刷新页面重试",null,null);
	         }
	        JSONObject resultJson=JSONObject.parseObject(resultStr);
	        JSONObject dataJson=resultJson.getJSONObject("data");
	        Integer totalPage=dataJson.getInteger("totalPageNumber");
			if(totalPage==null||totalPage==0){
				return ajaxRet("200","没有领券记录",null,null);	
			}else{
					String queryStr=request.getQueryString();
					String url;
					String returnQuery=getQueryStrWithout(queryStr,"pageNo=");
					if(StringUtils.isBlank(returnQuery)){
						url =myUrl+"/point/cardlist";
					}else{
						url =myUrl+"/point/cardlist?"+getQueryStrWithout(queryStr,"pageNo=");
					}
	    	        PageUtil pagedata = new PageUtil(Integer.parseInt(pageNo),null, pageSize,totalPage, url);
				return ajaxRet("200", resultJson.getString("msg"), dataJson.getString("exchangeHistoryList"),paging(pagedata));
			}
		} catch (Exception e) {
			return ajaxRet("500", "网络失败，请刷新页面重试",null,null);
		}
	}
	
	private Map<String, String> ajaxRet(String code, String msg, String data, Map pageData) {
		Map<String, String> mapRet = new HashMap<String, String>();
		mapRet.put("ret", code);
		mapRet.put("msg", msg);
		mapRet.put("data", data);
		mapRet.put("pageData",JSON.toJSONString(pageData));
		return mapRet;
	}
	
	public Map paging(PageUtil data) {
		Map<String, Object> pageMap = new HashMap<String, Object>();
		if (data.getPageSize()!=null&&data.getPageSize() != 0) {
			if (data.getPageNo() == null) {
				data.setPageNo(1);
			}
			if (data.getPageSize() == null) {
				data.setPageSize(15);
			}
			Integer totalPage;
			if(data.getTotalPage()==null&&data.getTotalSum()!=null){
				Double totalPageDouble = Math.ceil(data.getTotalSum().doubleValue()
					/ data.getPageSize().doubleValue());
				totalPage=totalPageDouble.intValue();
				data.setTotalPage(totalPage.intValue());
			}else if(data.getTotalPage()!=null){
				totalPage=data.getTotalPage();
			}else{
				totalPage=0;
			}
			if (data.getPageNo() > totalPage) {
				data.setPageNo(totalPage);
			}
			if (data.getPageNo() == 1) {
				pageMap.put("fn_prve", "fn_prve off");
			} else {
				pageMap.put("fn_prve", "fn_prve");
			}

			if (data.getPageNo() > 1) {
				if(data.getUrl().contains("?")) {
					pageMap.put("pre_href", data.getUrl() + "&pageNo=" + (data.getPageNo() - 1));
				}else{
					pageMap.put("pre_href", data.getUrl() + "?pageNo=" + (data.getPageNo() - 1));
				}
			} else {
				pageMap.put("pre_href", "javascript:void(0);");
			}

			pageMap.put("pageNo", data.getPageNo());
			pageMap.put("totalpage", data.getTotalPage());

			if (data.getPageNo() >= data.getTotalPage()) {
				pageMap.put("fn_next", "fn_next off");
			} else {
				pageMap.put("fn_next", "fn_next");
			}
			if (data.getPageNo() < data.getTotalPage()) {
				if(data.getUrl().contains("?")) {
					pageMap.put("next_href", data.getUrl() + "&pageNo=" + (data.getPageNo() + 1));
				}else{
					pageMap.put("next_href", data.getUrl() + "?pageNo=" + (data.getPageNo() + 1));
				}
			} else {
				pageMap.put("next_href", "javascript:void(0);");
			}
			pageMap.put("goUrl", data.getUrl());
		}
		return pageMap;
	}
	
	private String getQueryStrWithout(String queryStr, String param) {
		String returnStr;
		if(StringUtils.isBlank(queryStr)||!queryStr.contains(param)){
			returnStr=queryStr;
		}else{
			if(queryStr.contains("&"+param)){
				int pageNoIndex=queryStr.indexOf("&"+param);
				int pageNoNextIndex=queryStr.indexOf("&", pageNoIndex+1);
				if(pageNoNextIndex>pageNoIndex){
					returnStr=queryStr.substring(0,pageNoIndex)+queryStr.substring(pageNoNextIndex,queryStr.length());
				}else{
					returnStr=queryStr.substring(0,pageNoIndex);
				}
			}else{
				int pageNoIndex=queryStr.indexOf(param);
				int pageNoNextIndex=queryStr.indexOf("&", pageNoIndex+1);
				if(pageNoNextIndex>pageNoIndex){
					returnStr=queryStr.substring(0,pageNoIndex)+queryStr.substring(pageNoNextIndex+1,queryStr.length());
				}else{
					returnStr=queryStr.substring(0,pageNoIndex);
				}
			}
		}
		return returnStr;
	}
}
