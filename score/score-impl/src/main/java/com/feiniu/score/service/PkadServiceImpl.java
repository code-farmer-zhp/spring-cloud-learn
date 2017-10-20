package com.feiniu.score.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.ProducerClient;
import com.feiniu.score.common.*;
import com.feiniu.score.dao.mrst.CardDao;
import com.feiniu.score.dao.score.ScoreCommonDao;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.dto.Result;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.exception.BizException;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;

@Service
public class PkadServiceImpl implements PkadService {
	public static final CustomLog log = CustomLog.getLogger(PkadServiceImpl.class);
	@Autowired
	private PkadBaseServiceWithCache pkadBaseServiceWithCache;
	@Autowired
	private CardDao cardDao;
	@Autowired
	private GrowthMemService growthMemService;
	@Autowired
	private ScoreCommonDao scoreCommonDao;

	@Value("${getCardInfo.api}")
	private String getCardInfoApi;
	@Autowired
	protected RestTemplate restTemplate;
	@Autowired
	@Qualifier("producerGrowthClient")
	private ProducerClient<Object, String> producerClient;
	@Value("${fn.topic.pkad.taken}")
  	private String pkadTakenTopic;
	@Value("${begin.grant.new.mrst.date}")
	private String beginNewMrstDateStr;
	@Value("${last.show.hidden.mrst.date}")
	private String lastShowHiddenMrstDateStr;

	@Value("${needEnActID}")
	private String needEnActID;

	@Value("${encodeActID}")
	private String encodeActID;

	@Autowired
	private CacheUtils cacheUtils;
	@Autowired
	private ScoreExceptionHandler scoreExceptionHandler;
	@Autowired
	private ConstantPkadIsShow constantPkadIsShow;
	@Autowired
	private CardService cardService;
	private String returnSeqKeyForMap ="returnSeq";
	private String RETURN_CODE ="code";//0 partly succeed,1 all succeed
    private String TAKEN_MAP="takenMap";
	private String returnCardInfoKeyForMap ="returnCardInfo";
	private String ERROR_REASON="errorReason";

	/**
	 *  监听kafka 获取 礼包消息
	 * @param memGuid
	 *
	 * @return json
	 */
	@Override
	@DynamicDataSource(index = 0)
	@Transactional(propagation = Propagation.REQUIRED,value = "transactionManagerScore")
	public String getkafkafromCRM(String memGuid ,String data){
		JSONObject jsonObj = JSONObject.parseObject(data);
		String  pkadId = jsonObj.getString("pkad_id");
		String  pkadType = jsonObj.getString("pkad_type");
		String  mrstUi = jsonObj.getString("mrst_ui");
		String  mrstUiName = jsonObj.getString("mrst_uiname");
		String  membGradeF = jsonObj.getString("memb_grade_f");
		String  dPkad = jsonObj.getString("d_pkad");
		//沟通出现错误，数据库中is_take代表是否已被领取。而kafka中代表是否需要领取，T这是显示的礼包，F为直接充值
		String  isTakeFromKafka = jsonObj.getString("is_take");
		String  dTakeF = jsonObj.getString("d_take_f");
		String  dTakeT = jsonObj.getString("d_take_t");
		Integer pkadCnt = jsonObj.getInteger("pkad_cnt");
		String  growthInfo = jsonObj.getString("growth_info");
		String  pointInfo = jsonObj.getString("point_info");
		String  mrclNo = jsonObj.getString("mrcl_no");
		String  cardInfo = jsonObj.getString("card_info");
		if(StringUtils.isEmpty(memGuid)) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为 null");
		}
		if(pkadId==null) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pkad_id 不能为 null");
		}
		if(pkadType==null) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pkad_type 不能为 null");
		}
		if(mrstUi==null) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrst_ui 不能为 null");
		}
//			if(dPkad==null) {
//				throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_pkad 不能为 null");
//			}
		if(dTakeF==null) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_take_f 不能为 null");
		}
		if(pkadCnt==null) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pkad_cnt 不能为 null");
		}
		if(pkadType.equals(ConstantMrst.PKAD_TYPE_HS)&&!StringUtils.isBlank(mrclNo)){
			String cancelPkadId=null;
			String cancelpMembId=null;
			String cancelpMembGradeF=null;
			try {
				JSONObject cancelObj = JSONObject.parseObject(mrclNo);
				cancelPkadId = cancelObj.getString("pkad_id");
				cancelpMembId = cancelObj.getString("memb_id");
				cancelpMembGradeF = cancelObj.getString("memb_grade_f");
			} catch (Exception e) {
				log.error("撤销消息转换失败,mrclNo=" + mrclNo, "getkafkafromCRM");
			}

			if(!StringUtils.isBlank(cancelPkadId)&&!StringUtils.isBlank(cancelpMembId)){
				Pkad cancelPkad= pkadBaseServiceWithCache.getPkadByPkadIdAndMembIdForUpdate(cancelpMembId, cancelPkadId,cancelpMembGradeF);
				if(cancelPkad!=null){
					cancelPkad.setIsCancel(1);
					cancelPkad.setMrclNo(mrclNo);
					pkadBaseServiceWithCache.updatePkad(cancelpMembId, cancelPkad);
					log.info("撤销礼包成功,membId=" + cancelpMembId + " pkadId=" + cancelPkadId + " membGradeF=" + cancelpMembGradeF, "getkafkafromCRM");
				}else{
					throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"撤销礼包失败，未找到相应礼包  cancelPkadId="
							+cancelPkadId+" cancelpMembId="+cancelpMembId+" cancelpMembGradeF="+cancelpMembGradeF);
				}
			}
			//撤销礼包成功，不保存这条撤销的kafka记录。只更新原先的记录
			return "";
		}

		Pkad savePkad=new Pkad();
		savePkad.setPkadId(pkadId);
		savePkad.setMembId(memGuid);
		savePkad.setPkadType(pkadType);
		savePkad.setMrstUi(mrstUi);
		savePkad.setDdPkad(dPkad);
		savePkad.setMembGradeF(membGradeF);
        savePkad.setMrstUiName(mrstUiName);

		//isRecharge是否直充。kafka中is_take为T，数据库中isRecharge为0。
		if(isTakeFromKafka.equals(ConstantMrst.IS_F)){
			savePkad.setIsRecharge(ConstantMrst.IS_T_DB);

		}else{
			savePkad.setIsRecharge(ConstantMrst.IS_F_DB);
		}
		savePkad.setDdTakeF(dTakeF);
		if(!StringUtils.isBlank(dTakeT)){
			savePkad.setDdTakeT(dTakeT);
		}
		savePkad.setPkadCnt(pkadCnt);
		savePkad.setGrowthInfo(growthInfo);
		savePkad.setPointInfo(pointInfo);
		savePkad.setCardInfo(cardInfo);
		savePkad.setMrclNo(mrclNo);

		try {
			pkadBaseServiceWithCache.savePkad(memGuid, savePkad);

		} catch (DuplicateKeyException e) {
			log.error("重复插入,membId=" + memGuid + " pkadId=" + pkadId + " membGradeF="+membGradeF,"getkafkafromCRM");
		}
		//清缓存
		/*cacheUtils.removeCacheData(MD5Util.getMD5Code(memGuid+"get_Last_Libaokey_pkad"));*/

		//若果是直充，做全部领取操作
		if(isTakeFromKafka.equals(ConstantMrst.IS_F)){
			Pkad savedPkad=pkadBaseServiceWithCache.getPkadByPkadIdAndMembId(memGuid,savePkad.getPkadId(),savePkad.getMembGradeF());
			JSONObject dataForErrorLog=new JSONObject();
			dataForErrorLog.put("cardId","");
			dataForErrorLog.put("cardSeq","");
			dataForErrorLog.put("memGuid",memGuid);
			dataForErrorLog.put("pkadSeq",savedPkad.getPkadSeq());
			log.info("savedPkad.getPkadSeq():"+savedPkad.getPkadSeq());
			Map<String,Object> returnMap=RechargePkadUseOtherAPI(memGuid, savedPkad,dataForErrorLog.toJSONString());
			String returnCardSeq=(String)returnMap.get("returnSeq");
			boolean ifSuccess=(boolean)returnMap.get("ifSuccess");
			if(!StringUtils.isBlank(returnCardSeq))
				savedPkad.setTakeCardSeq(returnCardSeq);
			if(ifSuccess){
				savedPkad.setIsTake(ConstantMrst.IS_T_DB);
			}
 			Date now=new Date();
			savedPkad.setDdTake(DateUtil.getFormatDate(now,"yyyy-MM-dd HH:mm:ss"));
			try {
				pkadBaseServiceWithCache.updatePkad(memGuid, savedPkad);
				/*CacheUtils cacheUtils = new CacheUtils();
				cacheUtils.removeCacheData(MD5Util.getMD5Code(memGuid+"get_Last_Libaokey_pkad"));*/
				if(ifSuccess){
					if(returnMap.get(returnCardInfoKeyForMap)!=null){
						JSONArray returnCardInfo=(JSONArray)returnMap.get(returnCardInfoKeyForMap);
						saveTakePkadTokafkaForCRM(memGuid, savedPkad, now,returnCardInfo);
						log.info("save cardInfo to kafka:"+returnCardInfo.toJSONString());
					}else{
						log.error("礼包领取消息发送失败。memGuid=" + memGuid+" pkad="+savedPkad+" now="+now, "getkafakafromCRM");
					}
				}
			} catch (DuplicateKeyException e) {
				log.error("重复插入,尝试更新membId=" + memGuid + " pkadId=" + pkadId + " membGradeF=" + membGradeF, "getkafkafromCRM");
				pkadBaseServiceWithCache.updatePkad(memGuid, savedPkad);
			}
			String failedReason=(String)returnMap.get("partFailReason");
			if(!StringUtils.isEmpty(failedReason)){
				log.error("failedReason during take pkad:"+pkadId+","+failedReason);
			}
			if((boolean)returnMap.get("timeout")){
				return (String)returnMap.get("partFailReason");
			}
		}
		return "";
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave = true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getPkadListBySel(String memGuid ,String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		data=data.trim();
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		/*if(cacheUtils.isPartner(memGuid)){
			JSONObject returnJson=new JSONObject();
			returnJson.put("pkadList", new JSONArray());
			returnJson.put("totalItems", 0);
			returnJson.put("pageNo", 1);
			returnJson.put("totalPage", 0);
			return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
		}*/
		Integer pageNo= jsonObj.getInteger("pageNo");
		if(pageNo==null){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"pageNo 参数错误");
		}
		Integer pageSize= jsonObj.getInteger("pageSize");
		if(pageSize==null){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"pageSize 参数错误");
		}
		Map<String,Object> paramMap= new HashMap<>();
		paramMap.put("membId", memGuid);
		String today=DateUtil.getFormatDate(new Date(),"yyyyMMdd");
		paramMap.put("today", today);
		if(pageNo<1){
			pageNo=1;
		}
		if(pageSize<0){
			pageSize=6;
		}

		String isTake= jsonObj.getString("isTake");
		String isCancel= jsonObj.getString("isCancel");
		String isExpire= jsonObj.getString("isExpire");
		String isRecharge= jsonObj.getString("isRecharge");
		String hiddenOther= jsonObj.getString("hiddenOther");
		if(StringUtils.isNotBlank(isTake)){
			paramMap.put("isTake",Integer.valueOf(isTake));
		}
		if(StringUtils.isNotBlank(isCancel)){
			paramMap.put("isCancel", Integer.valueOf(isCancel));
		}
		if(StringUtils.isNotBlank(isExpire)){
			paramMap.put("isExpire",Integer.valueOf(isExpire));
		}
		if(StringUtils.isBlank(isRecharge)){
			isRecharge="0";
		}
		if(StringUtils.isBlank(hiddenOther)){
			hiddenOther="1";
		}
		//if isTake is "1", isRecharge shouldn't be a filter condition
		if(!"1".equals(isTake)){
			paramMap.put("isRecharge", Integer.valueOf(isRecharge));
		}
		if(StringUtils.equals(hiddenOther, "1")){
/*			paramMap.put("beginNewMrstDate", beginNewMrstDateStr);
			paramMap.put("lastShowHiddenMrstDate",lastShowHiddenMrstDateStr);*/
			paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS);
			/*if(cacheUtils.isPartner(memGuid)){
				paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS_PARTNER);
			}*/
		}
		paramMap.put("start", (pageNo - 1) * pageSize);
		paramMap.put("pageSize", pageSize);

		String order= jsonObj.getString("order");
		String sortType= jsonObj.getString("sortType");

		if(!StringUtils.isBlank(sortType) && !sortType.equals("asc") && !sortType.equals("desc")){
			sortType="desc";
		}

		if(!StringUtils.isBlank(order) &&!StringUtils.isBlank(sortType)){
			paramMap.put("order", order);
			paramMap.put("sortType", sortType);
		}

		List<Pkad> returnPkads= pkadBaseServiceWithCache.getPkadListBySelective(memGuid, paramMap,true);

		int returnListCount= pkadBaseServiceWithCache.getPkadCountBySelective(memGuid, paramMap, true);
		JSONArray returnJsonArr= new JSONArray();
			for (Pkad pkad : returnPkads) {
				try {
					JSONObject pkadJson = (JSONObject) JSONObject.toJSON(pkad);
					pkadJson.put("growthInfo",
							JSONArray.parse(pkad.getGrowthInfo()));
					pkadJson.put("pointInfo",
							JSONArray.parse(pkad.getPointInfo()));
					JSONArray cardArr=null;
					if(StringUtils.isNotBlank(pkad.getCardInfo())){
						cardArr=JSONArray.parseArray(pkad.getCardInfo());
						if(cardArr!=null&&!cardArr.isEmpty()){
							for(int i=0;i<cardArr.size();i++) {
								JSONObject cardArrObj = cardArr.getJSONObject(i);
								if(cardArrObj!=null&&!cardArrObj.isEmpty()) {
									JSONArray cardListArr = cardArrObj.getJSONArray("card_list");
									if(cardListArr!=null&&!cardListArr.isEmpty()) {
										for(int j=0;j<cardListArr.size();j++) {
											JSONObject cardListObj=cardListArr.getJSONObject(j);
											cardListObj.put("card_id", "");
											//加盐MD5加密seq card_seq卡券冒充需求
											cardListObj.put("card_seq",!StringUtils.isEmpty(cardListObj.getString("card_num"))?cardListObj.getString("card_num"):MrstUtil.enCodeCouponSensitive(cardListObj.getString("card_seq")));
											cardListArr.set(j,cardListObj);
										}
									}
									cardArrObj.put("card_list",cardListArr);
								}
								cardArr.set(i,cardArrObj);
							}
						}
					}
					if(cardArr!=null&&!cardArr.isEmpty()){
						pkadJson.put("cardInfo",cardArr);
					}else {
						pkadJson.put("cardInfo",
								JSONArray.parse(pkad.getCardInfo()));
					}
					pkadJson.put("mrclNo", JSONArray.parse(pkad.getMrclNo()));
					/*JSONObject takeCardSeqJson=JSONObject.parseObject(pkad.getTakeCardSeq());
					pkadJson.put("takeCardSeq", takeCardSeqJson);
					if(takeCardSeqJson!=null&&!takeCardSeqJson.isEmpty()){
						pkadJson.put("takeCardSeqKey", takeCardSeqJson.keySet());
					}*/
					pkadJson.remove("takeCardSeq");
					FastDateFormat sdfDb=FastDateFormat.getInstance("yyyyMMdd");
					FastDateFormat sdf=FastDateFormat.getInstance("MM.dd");
					//日期格式化各自捕获，不影响别的字段
			        try {
			        	Date dateF = sdfDb.parse(pkad.getDdTakeF());
						pkadJson.put("ddTakeFShort",sdf.format(dateF));
			        } catch (ParseException e) {
			            log.error("日期格式转换失败，ddTakeF=" + pkad.getDdTakeF(), "getPkadListBySel");
			        }
			        try {
						Date dateT = null;
			        	dateT = sdfDb.parse(pkad.getDdTakeT());
						pkadJson.put("ddTakeTShort",sdf.format(dateT));
			        } catch (ParseException e) {
						log.error("日期格式转换失败，ddTakeT=" + pkad.getDdTakeT(), "getPkadListBySel");
			        }
					try {
						//数据库中默认值为"1980-00-00 00:00:00"
						Date ddTake = null;
						String ddTakeStr="";
						if(pkad.getDdTake()!=null && pkad.getDdTake().length()>19){
							ddTakeStr=pkad.getDdTake().substring(0, 19);
						}
						FastDateFormat sdfDbTime = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
						Date defaultTakeTime = sdfDbTime.parse("1980-01-01 00:00:00");
						ddTake = sdfDbTime.parse(ddTakeStr);
						//早于"1980-00-00 00:00:00"的视为空
						if(!ddTake.after(defaultTakeTime)){
							ddTakeStr="";
						}
						pkadJson.put("ddTake",ddTakeStr);
					} catch (Exception e) {
						log.error("日期格式转换失败，ddTake=" + pkad.getDdTake(), "getPkadListBySel");
					}
					pkadJson.put("mrstUiDesc",ConstantMrst.MRST_UI_0.equals(pkad.getMrstUi())?pkad.getMrstUiName():ConstantMrst.GET_MRSTUI_DESC.get(pkad.getMrstUi()));					returnJsonArr.add(pkadJson);
				} catch (Exception e) {
					log.error("转换为json格式错误 pkad="+pkad+", msg="+e.getMessage(),"getPkadListBySel");
				}
			}
		JSONObject returnJson=new JSONObject();
		returnJson.put("pkadList", returnJsonArr);
		returnJson.put("totalItems", returnListCount);
		returnJson.put("pageNo", pageNo);
		returnJson.put("totalPage", (int) Math.ceil((double) returnListCount / (double) pageSize));

		return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave = true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getPkadListBySelCount(String memGuid ,String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		data=data.trim();
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		/*if(cacheUtils.isPartner(memGuid)){
			JSONObject returnJson=new JSONObject();
			returnJson.put("totalItems", 0);
			return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
		}*/
		Map<String,Object> paramMap= new HashMap<>();
		paramMap.put("membId", memGuid);
		String today=DateUtil.getFormatDate(new Date(),"yyyyMMdd");
		paramMap.put("today", today);
		String isTake= jsonObj.getString("isTake");
		String isCancel= jsonObj.getString("isCancel");
		String isExpire= jsonObj.getString("isExpire");
		String isRecharge= jsonObj.getString("isRecharge");
		String hiddenOther= jsonObj.getString("hiddenOther");
		if(StringUtils.isNotBlank(isTake)){
			paramMap.put("isTake",Integer.valueOf(isTake));
		}
		if(StringUtils.isNotBlank(isCancel)){
			paramMap.put("isCancel", Integer.valueOf(isCancel));
		}
		if(StringUtils.isNotBlank(isExpire)){
			paramMap.put("isExpire", Integer.valueOf(isExpire));
		}
		if(StringUtils.isBlank(isRecharge)){
			isRecharge="0";
		}
		if(StringUtils.isBlank(hiddenOther)){
			hiddenOther="1";
		}
		paramMap.put("isRecharge", Integer.valueOf(isRecharge));
		if(StringUtils.equals(hiddenOther, "1")){
			paramMap.put("beginNewMrstDate", beginNewMrstDateStr);
			paramMap.put("lastShowHiddenMrstDate",lastShowHiddenMrstDateStr);
			paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS);
			/*if(cacheUtils.isPartner(memGuid)){
				paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS_PARTNER);
			}*/
		}

		int returnListCount= pkadBaseServiceWithCache.getPkadCountBySelective(memGuid, paramMap,true);
		JSONObject returnJson=new JSONObject();
		returnJson.put("totalItems", returnListCount);
		return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave =true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getMrstUiListBySel(String memGuid ,String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		//合伙人显示礼包，但是已点亮权益隐藏
		if(cacheUtils.isPartner(memGuid)){
			JSONObject returnJson=new JSONObject();
			returnJson.put("mrstUiList", new JSONArray());
			return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
		}
		Map<String,Object> paramMap= new HashMap<>();
		paramMap.put("membId", memGuid);
		String today=DateUtil.getFormatDate(new Date(),"yyyyMMdd");
		paramMap.put("today", today);

		paramMap.put("isRecharge",ConstantMrst.IS_F_DB);

		paramMap.put("isCancel", ConstantMrst.IS_F_DB);
		Set<String> mrstUis= pkadBaseServiceWithCache.getMrstUisBySelective(memGuid, paramMap, true);

		paramMap.put("isCancel",ConstantMrst.IS_T_DB);
		paramMap.put("isTake", ConstantMrst.IS_T_DB);
		Set<String> mrstUisCancelButTaken= pkadBaseServiceWithCache.getMrstUisBySelective(memGuid, paramMap, true);

		mrstUis.addAll(mrstUisCancelButTaken);
		mrstUis.removeAll(constantPkadIsShow.HIDDEN_MRSTS);
		JSONObject returnJson=new JSONObject();
		returnJson.put("mrstUiList", mrstUis);
		return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
	}

	/*
	返回code在vip前台显示的时候小于100的会显示，大于100的具体信息默认初始化为领取礼包失败
	 */
	@Override
	@DynamicDataSource(index = 0)
	@Transactional(propagation = Propagation.REQUIRED,value = "transactionManagerScore")
	public Result takePkadByPkadSeqAndMembId(String memGuid ,String data){

		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		String pkadSeq= jsonObj.getString("pkadSeq");
		if(StringUtils.isEmpty(pkadSeq)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"pkadSeq 不能为空");
		}

		/*if(cacheUtils.isPartner(memGuid)){
			throw new ScoreException(ResultCode.TAKE_PKAD_BUT_IS_PARTNER,"合伙人不能领取礼包");
		}*/
		Pkad pkad= pkadBaseServiceWithCache.getPkadByPkadSeqAndMembIdForUpdate(memGuid, pkadSeq);
		if(pkad==null){
			return new Result(ResultCode.TAKE_PKAD_BUT_NOT_EXSIT,"该礼包不存在");
		}
		if(ConstantMrst.IS_T_DB.equals(pkad.getIsCancel())){
			cacheUtils.hDel(memGuid + ConstantCache.GET_PKAD);
			return new Result(ResultCode.TAKE_PKAD_BUT_CANCEL,"该礼包已经被撤销");
		}
		if(ConstantMrst.IS_T_DB.equals(pkad.getIsTake())){
			cacheUtils.hDel(memGuid + ConstantCache.GET_PKAD);
			return new Result(ResultCode.TAKE_PKAD_BUT_HAS_TAKEN,"该礼包已经被领取");
		}
		if(!StringUtils.isEmpty(pkad.getDdTakeT())){
			FastDateFormat sFormat = FastDateFormat.getInstance("yyyyMMdd");
			Date TakeTDate=null;
			try {
				TakeTDate = sFormat.parse(pkad.getDdTakeT());
			} catch (ParseException e) {
				log.error("日期转化失败,pkad.getdTakeT=" + pkad.getDdTakeT(),"takePkadByPkadSeqAndMembId");
			}
			Date now=new Date();
			String formatDate = sFormat.format(now);
			Date today = null;
			try {
				today = sFormat.parse(formatDate);
			} catch (ParseException e1) {
				log.error("日期转化失败,formatDate="+formatDate,"takePkadByPkadSeqAndMembId");
			}

			if(TakeTDate!=null&&today!=null&&TakeTDate.before(today)){
				return new Result(ResultCode.TAKE_PKAD_BUT_EXPIRED,"该礼包已经过期");
			}
		}
		Date now=new Date();
		//20160201后原有礼包只剩下新人和升级，别的礼包停止发放。按发放时间判断是否符合规则
		if(constantPkadIsShow.HIDDEN_MRSTS.contains(pkad.getMrstUi())) {
			Date beginNewMrstDate=null;
			Date lastShowHiddenMrstDate=null;
			Date ddPkadDate=null;
			try {
				beginNewMrstDate = DateUtils.parseDate(beginNewMrstDateStr,"yyyy-MM-dd");
				lastShowHiddenMrstDate = DateUtils.parseDate(lastShowHiddenMrstDateStr,"yyyy-MM-dd");
				ddPkadDate = DateUtils.parseDate(pkad.getDdPkad(), "yyyyMMdd");
			} catch (ParseException e) {
				log.error("日期格式转换错误,hiddenOtherMrstDate="+beginNewMrstDateStr+",  ddPkadDate="+pkad.getDdPkad()+",  lastShowHiddenMrstDate="+lastShowHiddenMrstDateStr,"takePkadByPkadSeqAndMembId");
			}
			if (ddPkadDate != null && beginNewMrstDate != null && !ddPkadDate.before(beginNewMrstDate)) {
				return new Result(ResultCode.TAKE_PKAD_BUT_BE_STOP_GRANT, "领取本应停止发放的礼包");
			}
			if (now != null && lastShowHiddenMrstDate != null && !now.before(lastShowHiddenMrstDate)) {
				return new Result(ResultCode.TAKE_PKAD_BUT_NO_SUPPORT_MRST, "领取已到最后兼容日期的老礼包");
			}
		}
		String cardSeq= jsonObj.getString("cardSeq");
		String cardType= jsonObj.getString("cardType");
//		Map<String,Object> returnMap=takePkadUseOtherAPI(memGuid, pkad, cardSeq, cardType,data);
		Map<String,Object> returnMap=takePkad(memGuid, pkad, cardSeq, cardType,data);
		Object returnCardSeq=null;
		JSONArray returnCardInfo=null;
		if(returnMap!=null){
			returnCardSeq=returnMap.get(returnSeqKeyForMap);
			try {
				returnCardInfo = (JSONArray) returnMap
						.get(returnCardInfoKeyForMap);
			} catch (Exception e) {
				log.error("kafka的cardInfo消息转换为JSONArray串出错","takePkadByPkadSeqAndMembId",e);
			}
		}
		if(returnCardSeq!=null && !StringUtils.isEmpty(returnCardSeq.toString())){
			pkad.setTakeCardSeq(returnCardSeq.toString());
		}

		if((int)returnMap.get(RETURN_CODE)==1){
            pkad.setIsTake(ConstantMrst.IS_T_DB);
			pkad.setDdTake(DateUtil.getFormatDate(now,"yyyy-MM-dd HH:mm:ss"));
			try {
				saveTakePkadTokafkaForCRM(memGuid, pkad, now,returnCardInfo);
				log.info("save cardInfo to kafka:"+returnCardInfo.toJSONString());
			} catch (Exception e) {
				log.error("礼包领取消息发送失败。memGuid=" + memGuid+" pkad="+pkad+" now="+now, "takePkadByPkadSeqAndMembId",e);
			}
		}
		pkadBaseServiceWithCache.updatePkad(memGuid, pkad);

		if((int)returnMap.get(RETURN_CODE)==1){
			return new Result(ResultCode.RESULT_STATUS_SUCCESS, "领取礼包成功");
		}else{
			if(returnMap.get(ERROR_REASON)!=null){
				if(returnMap.get(ERROR_REASON)instanceof CardServiceImpl.WarnStatusEnum){
					CardServiceImpl.WarnStatusEnum warnStatusEnum=(CardServiceImpl.WarnStatusEnum)returnMap.get(ERROR_REASON);
					return new Result(warnStatusEnum.getResultCode(),warnStatusEnum.getName());
				}else{
					return new Result(ResultCode.PKAD_NOT_FULLY_TAKEN,(String)returnMap.get(ERROR_REASON));
				}
			}
			return new Result(ResultCode.PKAD_NOT_FULLY_TAKEN,!StringUtils.isEmpty((String)returnMap.get(ERROR_REASON))?(String)returnMap.get(ERROR_REASON):"领取礼包失败");
		}
	}

	/**
	 * 领取礼包接口
	 * 校验卡券是否与数据库中对应,为了多选一的情况
	 * 非多选一的礼包，不传cardSeq，cardId，cardType。默认全领
	 * returnSeq，旧版只领一张返回领取卡券的卡号。新版返回卡券活动号和卡号的json串
	 * @param memGuid guid
	 * @param pkad    礼包流水号
	 * @param cardSeq 卡券流水号，CRM传
	 * @param cardType	 卡券类别
	 * @return json
	 */
//	@Override
//	@DynamicDataSource(index = 0)
//	public Map<String,Object> takePkadUseOtherAPI(String memGuid, Pkad pkad, String cardSeq, String cardType, String dataForErrorLog){
//		String returnSeq=null;
//		JSONArray returnCardInfo=null;//领取成功的kafka消息中的cardInfo
//		Map<String,Object> returnMap=new HashMap<String, Object>();
//		try {
//			JSONArray growthInfoArr = JSONObject.parseArray(pkad
//					.getGrowthInfo());
//			if (growthInfoArr != null && !growthInfoArr.isEmpty()) {
//				for (int i = 0; i < growthInfoArr.size(); i++) {
//					String growthInfo = growthInfoArr.getString(i);
//					growthMemService.saveGrowthfromPkad(memGuid, growthInfo,
//							pkad);
//				}
//			}
//		} catch (Exception e) {
//			throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"调用成长值接口出错",e);
//		}
//		try {
//			JSONArray pointInfoArr = JSONObject.parseArray(pkad.getPointInfo());
//			if (pointInfoArr != null && !pointInfoArr.isEmpty()) {
//				for (int i = 0; i < pointInfoArr.size(); i++) {
//					String pointInfo = pointInfoArr.getString(i);
//					scoreCommonDao.processPkadScore(memGuid, pointInfo, pkad);
//				}
//			}
//		} catch (Exception e) {
//			throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"调用积分接口出错",e);
//		}
//		String  cardId=null;
//		//多选一需要传cardId和cardType
//		if(!StringUtils.isBlank(cardType)&&!StringUtils.isBlank(cardSeq)){
//			try {
//				JSONArray cardInfoArr = JSONObject.parseArray(pkad.getCardInfo());
//				Boolean isRelevant=false; //判断传入的cardId和cardType是否与礼包中的卡券对应
//				Map<String,String> returnCardSeqs = new HashMap<String,String>();
//				if (cardInfoArr != null && !cardInfoArr.isEmpty()) {
//					for (int i = 0; i < cardInfoArr.size(); i++) {
//						String cardInfo = cardInfoArr.getString(i);
//						JSONObject jsonObj = JSONObject.parseObject(cardInfo);
//						String  membIdDb =  jsonObj.getString("memb_id");
//
//						//cardInfoArr and cardListArr'length should be 1 ,because C3 type should be send alone from kafka
//						JSONArray cardListArr=jsonObj.getJSONArray("card_list");
//						for (int j = 0; j < cardListArr.size(); j++) {
//							JSONObject cardListOnj = cardListArr
//									.getJSONObject(j);
//							// String cardSeqDb =
//							// cardListOnj.getString("card_seq");
//							String cardTypeDb = cardListOnj
//									.getString("card_type");
//							String cardSeqDb = cardListOnj
//									.getString("card_seq");
//							String cardIdDb = cardListOnj
//									.getString("card_id");
//							if (cardType.equals(cardTypeDb)
//									&& cardSeq.equals(cardSeqDb)
//									&& memGuid.equals(membIdDb)) {
//								cardId=cardIdDb;
//								isRelevant = true;
//							}
//						}
//					}
//					if(!isRelevant){
//						throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NOT_RELEVANT,"卡券信息与礼包不对应");
//					}
//					String retSeq=takeCard(memGuid,cardSeq,cardType,cardId,dataForErrorLog);
//
//					//将返回的卡号重新包装入cardInfo字符串中，用来发送kafka。
//					for (int i = 0; i < cardInfoArr.size(); i++) {
//						String cardInfo = cardInfoArr.getString(i);
//						JSONObject jsonObj = JSONObject.parseObject(cardInfo);
//						String  membIdDb =  jsonObj.getString("memb_id");
//
//						JSONArray cardListArr=jsonObj.getJSONArray("card_list");
//						for (int j = 0; j < cardListArr.size(); j++) {
//							JSONObject cardListOnj = cardListArr
//									.getJSONObject(j);
//							String cardTypeDb = cardListOnj
//									.getString("card_type");
//							String cardSeqDb = cardListOnj.getString("card_seq");
//							if (cardType.equals(cardTypeDb)
//									&& cardSeq.equals(cardSeqDb)
//									&& memGuid.equals(membIdDb)) {
//								cardListOnj.put("card_seq_rtn", retSeq);
//								cardListArr.set(j, cardListOnj);
//								break;
//							}
//						}
//						jsonObj.put("card_list", cardListArr);
//						cardInfoArr.set(i,jsonObj);
//					}
//					returnCardInfo=cardInfoArr;
//
//					returnCardSeqs.put(cardId,retSeq);
//					if(!returnCardSeqs.isEmpty()&&!returnCardSeqs.values().isEmpty()){
//						returnSeq = JSON.toJSONString(returnCardSeqs);
//					}
//				}else{
//					throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NOT_RELEVANT,"卡券信息与礼包不对应");
//				}
//			} catch (Exception e) {
//				if(e instanceof ScoreException){
//					throw (ScoreException) e;
//				}else{
//					throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"调用卡券接口出现未知错误",e);
//				}
//			}
//		}else{
//			if(!StringUtils.isBlank(pkad.getCardInfo())){
//				        //其余类型，卡券全领
//					JSONArray cardInfoArr=null;
//					try {
//						cardInfoArr = JSONObject.parseArray(pkad
//								.getCardInfo());
//					}catch (Exception e){
//						throw new ScoreException(ResultCode.RESULT_TYPE_CONV_ERROR,"card_info解析错误");
//					}
//					try {
//						Map<String,String> returnCardSeqs = new HashMap<String,String>();
//						if (cardInfoArr != null && !cardInfoArr.isEmpty()) {
//							for (int i = 0; i < cardInfoArr.size(); i++) {
//								String cardInfo = cardInfoArr.getString(i);
//								JSONObject jsonObj = JSONObject
//										.parseObject(cardInfo);
//								String mrdfType = jsonObj
//										.getString("mrdf_type");
//								if(StringUtils.equals(mrdfType,ConstantMrst.MRDF_TYPE_C3)){
//									throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NOT_RELEVANT,"领取多选一礼包未传卡券活动号");
//								}
//								JSONArray cardListArr = jsonObj
//										.getJSONArray("card_list");
//								for (int j = 0; j < cardListArr.size(); j++) {
//									JSONObject cardListOnj = cardListArr
//											.getJSONObject(j);
//									String cardSeqDb = cardListOnj
//											.getString("card_seq");
//									String cardTypeDb = cardListOnj
//											.getString("card_type");
//									String cardIdDb = cardListOnj
//											.getString("card_id");
//									String mrdfTypeInList = cardListOnj
//											.getString("mrdf_type");
//									if(StringUtils.equals(mrdfTypeInList,ConstantMrst.MRDF_TYPE_C3)){
//										throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NOT_RELEVANT,"领取多选一礼包未传卡券号");
//									}
//									String retSeq;
//									//领券接口超时时会记录在unsuccess表中，30秒后重新查询已领成功的券。下次再领取时，已领成功的券跳过
//									//{"20150929C0003":"20151209COL00000088","20150929C0004":"20151008COL00000002"}
//									if(StringUtils.isNotBlank(pkad.getTakeCardSeq())&&(pkad.getTakeCardSeq().contains("\""))) {
//											JSONObject takenCardJson=JSONObject.parseObject(pkad.getTakeCardSeq());
//											if(StringUtils.isNotBlank(takenCardJson.getString(cardIdDb))){
//												retSeq=takenCardJson.getString(cardIdDb);
//											}else{
//												retSeq = takeCard(memGuid, cardSeqDb, cardTypeDb, cardIdDb,dataForErrorLog);
//											}
//									}else {
//										retSeq = takeCard(memGuid, cardSeqDb, cardTypeDb, cardIdDb,dataForErrorLog);
//									}
//									cardListOnj.put("card_seq_rtn", retSeq);
//									cardListArr.set(j, cardListOnj);
//
//									//数据库中take_card_seq字段记录的数据
//									returnCardSeqs.put(cardIdDb,retSeq);
//								}
//								jsonObj.put("card_list", cardListArr);
//								cardInfoArr.set(i,jsonObj);
//							}
//							returnCardInfo=cardInfoArr;
//
//							if(!returnCardSeqs.isEmpty()&&!returnCardSeqs.values().isEmpty()){
//								returnSeq = JSON.toJSONString(returnCardSeqs);
//							}
//						}
//					} catch (Exception e) {
//						if(e instanceof ScoreException){
//							ScoreException scoExc=(ScoreException) e;
//							//存在失败时，抛出未绑定手机、可疑会员和员工。其余只说礼包领取失败
//							if(scoExc.getCode()!=ResultCode.TAKE_PKAD_BUT_NO_BIND_PHONE&&
//									scoExc.getCode()!=ResultCode.TAKE_BONUS_BUT_IS_EMP&&
//									scoExc.getCode()!=ResultCode.TAKE_BONUS_BUT_IS_DOUBTABLE
//								){
//								throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"卡券领取失败",e);
//							}else {
//								throw scoExc;
//							}
//						}else{
//							throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"调用卡券接口出现未知错误",e);
//						}
//					}
//				}
//		}
//		returnMap.put(returnSeqKeyForMap, returnSeq);
//		returnMap.put(returnCardInfoKeyForMap, returnCardInfo);
//		return returnMap;
//	}

	/**
	 * 领取礼包接口
	 * 校验卡券是否与数据库中对应,为了多选一的情况
	 * 非多选一的礼包，不传cardSeq，cardId，cardType。默认全领
	 * @param memGuid guid
	 * @param pkad    礼包流水号
	 * @param cardSeq 卡券流水号，CRM传
	 * @param cardType	 卡券类别
     * @param dataForErrorLog
	 * @return returnMap  包含RETURN_CODE、returnSeqKeyForMap、returnCardInfoKeyForMap
	 */
	@DynamicDataSource(index = 0)
	public Map<String,Object> takePkad(String memGuid, Pkad pkad, String cardSeq, String cardType, String dataForErrorLog){
		Map<String,Object> returnMap=new HashMap<String, Object>();
		String returnSeq=null;
		JSONArray returnCardInfo=null;//领取成功的kafka消息中的cardInfo
        returnMap.put(RETURN_CODE,0);
		returnMap.put(returnSeqKeyForMap, returnSeq);
		returnMap.put(returnCardInfoKeyForMap, returnCardInfo);
		//先领取成长值和积分，再领取卡券，如果takecardseq不为空 则说明积分和成长值都已领取成功
		if(StringUtils.isEmpty(pkad.getTakeCardSeq())) {
			try {
				JSONArray growthInfoArr = JSONObject.parseArray(pkad
						.getGrowthInfo());
				if (growthInfoArr != null && !growthInfoArr.isEmpty()) {
					for (int i = 0; i < growthInfoArr.size(); i++) {
						String growthInfo = growthInfoArr.getString(i);
                            growthMemService.saveGrowthfromPkad(memGuid, growthInfo,
                                    pkad);

                    }
				}
			} catch (Exception e) {
				throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "调用成长值接口出错", e);
			}
			try {
				JSONArray pointInfoArr = JSONObject.parseArray(pkad.getPointInfo());
				if (pointInfoArr != null && !pointInfoArr.isEmpty()) {
					for (int i = 0; i < pointInfoArr.size(); i++) {
						String pointInfo = pointInfoArr.getString(i);
                            scoreCommonDao.processPkadScore(memGuid, pointInfo, pkad);
					}
				}
			} catch (Exception e) {
				throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "调用积分接口出错", e);
			}
		}
		String  cardId=null;

		JSONArray cardInfoArray=new JSONArray();
		JSONArray cardInfoArr=null;
		try {
			cardInfoArr = JSONObject.parseArray(pkad.getCardInfo());
		}catch(Exception e){
			throw new RuntimeException("cardInfo json 解析出错！");
		}
		//多选一需要传cardId和cardType
		if(!StringUtils.isBlank(cardType)&&!StringUtils.isBlank(cardSeq)){
				Boolean isRelevant=false; //判断传入的cardId和cardType是否与礼包中的卡券对应
				Map<String,String> returnCardSeqs = new HashMap<String,String>();
				String oneFromThreeCardSeq="";
				if (cardInfoArr != null && !cardInfoArr.isEmpty()) {
					for (int i = 0; i < cardInfoArr.size(); i++) {
						String cardInfo = cardInfoArr.getString(i);
						JSONObject jsonObj = JSONObject.parseObject(cardInfo);
						String  membIdDb =  jsonObj.getString("memb_id");

						//cardInfoArr and cardListArr'length should be 1 ,because C3 type should be send alone from kafka
						JSONArray cardListArr=jsonObj.getJSONArray("card_list");
						if(cardListArr!=null) {
							for (int j = 0; j < cardListArr.size(); j++) {
								JSONObject cardListOnj = cardListArr
										.getJSONObject(j);
								// String cardSeqDb =
								// cardListOnj.getString("card_seq");
								String cardTypeDb = cardListOnj
										.getString("card_type");
								String cardSeqDb = cardListOnj
										.getString("card_seq");
								String cardIdDb = cardListOnj
										.getString("card_id");
								String cardNum = cardListOnj
										.getString("card_num");
								if (cardType.equals(cardTypeDb)
										&& (cardSeq.equals(MrstUtil.enCodeCouponSensitive(cardSeqDb))||cardSeq.equals(cardNum))
										&& memGuid.equals(membIdDb)) {
									cardId = cardIdDb;
									//新需求用cardNum 替换cardSeq ，两者都起到唯一标识作用
									oneFromThreeCardSeq=cardSeqDb;
									isRelevant = true;
								}
							}
						}
					}
					//三选一类型 如果传入的cardId和cardType和数据库中记录的不相关，则抛异常回滚
					if(!isRelevant){
						throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NOT_RELEVANT,"卡券信息与礼包不对应");
					}
					String takenSeq=pkad.getTakeCardSeq();
					JSONObject cardinfoJson=new JSONObject();
					cardinfoJson.put("couponid",cardId);
					cardinfoJson.put("seq",oneFromThreeCardSeq);
					cardinfoJson.put("cardtype",cardType);
					//包含3选1类型的礼包只有1张（3选1类型），如果takenSeq中包含此cardId说明此礼包已经领完
					if(takenSeq.contains(cardId)){
						return returnMap;
					}
					cardInfoArray.add(cardinfoJson);
				}else{
					//数据库中没有cardInfoArray 抛异常回滚，说明数据库数据不对
					throw new ScoreException(ResultCode.CARDARRAY_IS_EMPTY,"礼包卡券信息为空");
				}
		}else{
			if(!StringUtils.isBlank(pkad.getCardInfo())){
                cardInfoArray.addAll(getCardInfoParamFromCardInfo(pkad, cardInfoArr));
            }
		}
		//filter already taken
		Iterator<Object> it=cardInfoArray.iterator();
		while(it.hasNext()){
			JSONObject cardInfo=(JSONObject) it.next();
			String seq=cardInfo.getString("seq");
			String takenCardSeq=pkad.getTakeCardSeq();
			if(!StringUtils.isEmpty(takenCardSeq)){
				JSONObject jo=JSONObject.parseObject(takenCardSeq);
				if(!StringUtils.isEmpty(jo.getString(seq))){
					it.remove();
				}
			}
		}
		JSONObject returnTakenInfo=cardService.batchTakeCards(memGuid,cardInfoArray,dataForErrorLog,false);
		returnMap.put(RETURN_CODE,returnTakenInfo.get(RETURN_CODE));
		String combinedTakenSeq=populateReturnSeq(pkad.getTakeCardSeq(), returnTakenInfo.getJSONObject(TAKEN_MAP));
		returnMap.put(returnSeqKeyForMap,combinedTakenSeq );
		populateReturnSeqToCardInfo(cardInfoArr,combinedTakenSeq);
		returnMap.put(returnCardInfoKeyForMap, cardInfoArr);
		returnMap.put(ERROR_REASON,returnTakenInfo.get(ERROR_REASON));
		return returnMap;
	}

    private JSONArray getCardInfoParamFromCardInfo(Pkad pkad, JSONArray cardInfoArr) {
        JSONArray  cardInfoArray=new JSONArray();
        //其余类型，卡券全领
        String pkadTakeCardSeq=pkad.getTakeCardSeq();
        JSONObject takenCardJson=null;
        if(!StringUtils.isEmpty(pkadTakeCardSeq)&&pkadTakeCardSeq.contains("\"")){
            try{
                takenCardJson= JSONObject.parseObject(pkadTakeCardSeq);
            }catch(Exception e){
                throw new RuntimeException("解析 takenCardSeqJson 失败！");
            }
        }
        if (cardInfoArr != null && !cardInfoArr.isEmpty()) {
            for (int i = 0; i < cardInfoArr.size(); i++) {
                String cardInfo = cardInfoArr.getString(i);
                JSONObject jsonObj = JSONObject
                        .parseObject(cardInfo);
                String mrdfType = jsonObj
                        .getString("mrdf_type");
                if(StringUtils.equals(mrdfType, ConstantMrst.MRDF_TYPE_C3)){
                    //默认领取却存在3选1，抛异常回滚
                    throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NOT_RELEVANT,"领取多选一礼包缺少卡券活动号");
                }
                JSONArray cardListArr = jsonObj
                        .getJSONArray("card_list");
                if(cardListArr!=null){
                    for (int j = 0; j < cardListArr.size(); j++) {
                        JSONObject cardListOnj = cardListArr
                                .getJSONObject(j);
						//用card_num替换老的cardSeq
                        String cardSeqDb = cardListOnj
                                .getString("card_seq");
                        String cardTypeDb = cardListOnj
                                .getString("card_type");
                        String cardIdDb = cardListOnj
                                .getString("card_id");
                        String mrdfTypeInList = cardListOnj
                                .getString("mrdf_type");
                        if(StringUtils.equals(mrdfTypeInList,ConstantMrst.MRDF_TYPE_C3)){
                            //默认领取却存在3选1，抛异常回滚
                            throw new ScoreException(ResultCode.TAKE_PKAD_BUT_NOT_RELEVANT,"领取多选一礼包未传卡券号");
                        }
                        //领券接口超时时会记录在unsuccess表中，30秒后重新查询已领成功的券。下次再领取时，已领成功的券跳过
                        if(takenCardJson!=null){
                            String returnNo=takenCardJson.getString(cardSeqDb);
                            if(!StringUtils.isEmpty(returnNo)) {
                                //跳过已领取的部分
                                continue;
                            }

                        }
						JSONObject cardInfoObject=new JSONObject();
						if(!StringUtils.isEmpty(cardSeqDb)&&!StringUtils.isEmpty(cardTypeDb)&&!StringUtils.isEmpty(cardIdDb)){
							cardInfoObject.put("seq",cardSeqDb);
							cardInfoObject.put("cardtype",cardTypeDb);
							cardInfoObject.put("couponid",cardIdDb);
							cardInfoArray.add(cardInfoObject);
						}
                    }
                }
            }
        }
        return cardInfoArray;
    }

    private String populateReturnSeq(String returnSeq,JSONObject returnMap){
		if(!StringUtils.isEmpty(returnSeq)&&returnMap.size()>0){
			try{
				JSONObject jsonObject=JSONObject.parseObject(returnSeq);
				jsonObject.putAll(returnMap);
				returnSeq=JSONObject.toJSONString(jsonObject);
			}catch(Exception e){
				log.error("parse returnSeq error.");
			}
		}else{
			if(StringUtils.isEmpty(returnSeq)) {
				returnSeq = returnMap.size()>0?JSONObject.toJSONString(returnMap):"";
			}else{
				//returnMap size is zero ,no need to populate
			}
		}
		return returnSeq;
	}

	private void populateReturnSeqToCardInfo(JSONArray cardInfoArr,String combinedTakenSeq) {
		//将返回的卡号重新包装入cardInfo字符串中，用来发送kafka。
		JSONObject returnSeqJson=null;
		try {
			if(!StringUtils.isEmpty(combinedTakenSeq)){
				returnSeqJson = JSONObject.parseObject(combinedTakenSeq);
			}
		}catch(Exception e){
			throw new RuntimeException(",combinedTakenSeq parse to JSON error!");
		}
		if(returnSeqJson!=null) {
				for(int j=0;j<cardInfoArr.size();j++){
					JSONObject cardInfo=cardInfoArr.getJSONObject(j);
					String mrdfType=cardInfo.getString("mrdf_type");
					JSONArray cardList=cardInfo.getJSONArray("card_list");
					for(int k=0;k<cardList.size();k++){
						JSONObject card=cardList.getJSONObject(k);
						String cardSequence=card.getString("card_seq");
						// if cardSequence exists ,populate "" or returnseq to return cardinfo
						if(!StringUtils.isEmpty(cardSequence)){
							card.put("card_seq_rtn",StringUtils.isEmpty(returnSeqJson.getString(cardSequence))?"":returnSeqJson.getString(cardSequence));
						}
					}
				}
//			}
		}

	}

	/**
	 * 直充礼包接口
	 * 不校验卡券
	 * @param memGuid
	 * @param pkad
	 * @return json
	 */
	@Override
	@DynamicDataSource(index = 0)
	public Map<String,Object> RechargePkadUseOtherAPI(String memGuid, Pkad pkad, String dataForErrorLog){
		Map<String ,Object> returnMap=new HashMap<>();
		returnMap.put("ifSuccess",false);
		returnMap.put("returnSeq","");
		returnMap.put("allSuccess",false);
		returnMap.put("partFailReason","");
		returnMap.put("timeout",false);
		String returnSeq=null;
		Map<String,JSONArray> returnCardSeqs = new HashMap<String ,JSONArray>();
		//parts of these are taken successfully
		boolean ifSuccess=false;
		//all of these are taken successfully
		boolean allSuccess=true;
		StringBuilder partFailReason=new StringBuilder();
		try {
			JSONArray growthInfoArr = JSONObject.parseArray(pkad
					.getGrowthInfo());
			if (growthInfoArr != null && !growthInfoArr.isEmpty()) {
				for (int i = 0; i < growthInfoArr.size(); i++) {
					String growthInfo = growthInfoArr.getString(i);
					growthMemService.saveGrowthfromPkad(memGuid, growthInfo,
							pkad);
					ifSuccess=true;
				}
			}
		} catch (Exception e) {
			//throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"调用成长值接口出错",e);
			allSuccess=allSuccess&false;
			log.error(" 调用成长值接口出错,礼包seq : "+pkad.getPkadId(),e);
			partFailReason.append(" 调用成长值接口出错,礼包seq : "+pkad.getPkadId()+",");
		}
		try {
			JSONArray pointInfoArr = JSONObject.parseArray(pkad.getPointInfo());
			if (pointInfoArr != null && !pointInfoArr.isEmpty()) {
				for (int i = 0; i < pointInfoArr.size(); i++) {
					String pointInfo = pointInfoArr.getString(i);
					scoreCommonDao.processPkadScore(memGuid, pointInfo, pkad);
					ifSuccess=true;
				}
			}
		} catch (Exception e) {
			//throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"调用积分接口出错",e);
			allSuccess=allSuccess&false;
			log.error(" 调用积分接口出错,礼包seq : "+pkad.getPkadId(),e);
			partFailReason.append(" 调用积分接口出错,礼包seq : "+pkad.getPkadId()+",");
		}
		JSONArray cardInfoArr = JSONObject.parseArray(pkad.getCardInfo());
		if (cardInfoArr != null && !cardInfoArr.isEmpty()) {
					try {
                        JSONArray cardInfoParam=getCardInfoParamFromCardInfo(pkad,cardInfoArr);
                        JSONObject returnJson=cardService.batchTakeCards(memGuid,cardInfoParam,dataForErrorLog,true);
						int code=returnJson.getInteger(RETURN_CODE);
						if(code==0||code==1){
							returnMap.put("ifSuccess",true);
						}else{
							returnMap.put("ifSuccess",false|ifSuccess);
						}
						returnMap.put("returnSeq",(returnJson.getJSONObject(TAKEN_MAP)).size()>0?JSONObject.toJSONString(returnJson.getJSONObject(TAKEN_MAP)):"");
						if(code==1){
							returnMap.put("allSuccess",allSuccess&true);
						}else{
							returnMap.put("allSuccess",false);
						}
						if(code==3){
							returnMap.put("timeout",true);
						}

						String combinedTakenSeq=populateReturnSeq(pkad.getTakeCardSeq(), returnJson.getJSONObject(TAKEN_MAP));
						returnMap.put(returnSeqKeyForMap,combinedTakenSeq );
						populateReturnSeqToCardInfo(cardInfoArr,combinedTakenSeq);
						returnMap.put(returnCardInfoKeyForMap, cardInfoArr);

					} catch (Exception e) {
						partFailReason.append("batch taken error:"+e.getMessage());
					}
		}
		returnMap.put("partFailReason",partFailReason.toString());
		return returnMap;
	}

	//takeTimes是全领多张时标志第几张券，使token不一样。防止过于频繁
	private String takeCard(String memGuid,String cardSeq,String cardType,String cardId,String dataForErrorLog){
        if(!NumberUtils.isNumber(cardType)){
        	throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION,"cardType 参数错误");
        }
        if(!ConstantMrst.CARD_TYPE_LIST.contains(cardType)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION,"错误的cardType列类型");
		}


		log.info("领取调用接口开始,cardSeq=" + cardSeq + "  cardType=" + cardType + "  cardId=" + cardId, "takeCard");
        String result=null;
        String returnSeq=null;
		cardId=this.enCardId(cardId);
        //抵用券
        if(cardType.equals(ConstantMrst.CARD_TYPE_DYQ)){
			returnSeq=cardDao.takeBonusForPkad(cardId, memGuid, dataForErrorLog);
        }else if(cardType.equals(ConstantMrst.CARD_TYPE_YHQ)
				||cardType.equals(ConstantMrst.CARD_TYPE_ZY_MYQ)
				||cardType.equals(ConstantMrst.CARD_TYPE_ZY_PP)){
			//优惠券
			returnSeq=cardDao.takeVoucher(cardId,memGuid,dataForErrorLog);
        }else if(cardType.equals(ConstantMrst.CARD_TYPE_SC_YHQ)
				||cardType.equals(ConstantMrst.CARD_TYPE_SC_MYQ)
				||cardType.equals(ConstantMrst.CARD_TYPE_SC_DDMEQ)){
			//商城券
			returnSeq=cardDao.takeMailCoupon(cardId,memGuid,dataForErrorLog);
		}
		return returnSeq;
	}

	/*
	 * (non-Javadoc)
	 * 兼容直接cardInfo和cardInfo中嵌套cardList两种格式
	 */
	@Override
	@DynamicDataSource(index = 0,isReadSlave = true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getCardInfo(String memGuid ,String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		/*if(cacheUtils.isPartner(memGuid)){
			throw new ScoreException(ResultCode.TAKE_PKAD_BUT_IS_PARTNER,"合伙人不能打开礼包");
		}*/
		String pkadSeq= jsonObj.getString("pkadSeq");
		if(StringUtils.isEmpty(pkadSeq)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"pkadSeq 不能为空");
		}
		Pkad pkad= pkadBaseServiceWithCache.getPkadByPkadSeqAndMembId(memGuid,pkadSeq);
		if(pkad==null){
			return new Result(ResultCode.TAKE_PKAD_BUT_NOT_EXSIT,"该礼包不存在");
		}
		if(StringUtils.isBlank(pkad.getCardInfo())){
			return new Result(ResultCode.TAKE_PKAD_BUT_NOT_EXSIT,"该礼包不包含卡券");
		}else{
			JSONArray cardInfoArr = JSONArray.parseArray(pkad.getCardInfo());
			JSONArray returnArr =cardDao.getCardInfoByCardIdAndType(cardInfoArr,false);
			return new Result(ResultCode.RESULT_STATUS_SUCCESS,returnArr,"查询礼包卡券信息成功");
	    }
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave = true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getCardInfoBatch(String memGuid, String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		/*if(cacheUtils.isPartner(memGuid)){
			throw new ScoreException(ResultCode.TAKE_PKAD_BUT_IS_PARTNER,"合伙人不能打开礼包");
		}*/
		String pkadSeqsStr= jsonObj.getString("pkadSeqs");
		if(StringUtils.isEmpty(pkadSeqsStr)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"pkadSeqs 不能为空");
		}
		String[] pkadSeqs = pkadSeqsStr.split(",");
		Set<String> pkadSeqSet=new HashSet<>();
		Collections.addAll(pkadSeqSet, pkadSeqs);
		List<Pkad> pkadList= pkadBaseServiceWithCache.getPkadsByPkadSeqsAndMembId(memGuid,pkadSeqSet);
		if(pkadList==null){
			return new Result(ResultCode.TAKE_PKAD_BUT_NOT_EXSIT,"对应礼包不存在");
		}
		Map<Integer,List<String>> pkadCards=new HashMap<>();
		JSONObject returnData=new JSONObject();
		JSONArray cardInfoArr=new JSONArray();
		for(Pkad pkad :pkadList){
			if(StringUtils.isBlank(pkad.getCardInfo())){
				returnData.put(String.valueOf(pkad.getPkadSeq()),new JSONArray());
			}else{
				cardInfoArr.addAll(JSONArray.parseArray(pkad.getCardInfo()));
			}
			List<String> cardSeqForBatchs=pkadCards.get(pkad.getPkadSeq());
			if(cardSeqForBatchs==null){
				cardSeqForBatchs=new ArrayList<>();
			}
			JSONArray cardArrsOnePkad=JSONArray.parseArray(pkad.getCardInfo());
			if(cardArrsOnePkad!=null&&!cardArrsOnePkad.isEmpty()) {
				for (int i = 0; i < cardArrsOnePkad.size(); i++) {
					JSONObject cardInfoObj = cardArrsOnePkad.getJSONObject(i);
					JSONArray cardListArr = cardInfoObj.getJSONArray("card_list");
					if (cardListArr != null && !cardListArr.isEmpty()) {
						for (int j = 0; j < cardListArr.size(); j++) {
							JSONObject cardListObj = cardListArr.getJSONObject(j);
							cardSeqForBatchs.add(MrstUtil.enCodeCouponSensitive(cardListObj.getString("card_seq")));
						}
					}
				}
			}
			pkadCards.put(pkad.getPkadSeq(),cardSeqForBatchs);
		}
		if(cardInfoArr!=null&&!cardInfoArr.isEmpty()) {
			JSONArray returnArr = cardDao.getCardInfoByCardIdAndType(cardInfoArr, true);
			for (Integer pkadSeq : pkadCards.keySet()) {
				JSONArray cardsOnePkad = new JSONArray();
				for (String cardSeqForBatchs : pkadCards.get(pkadSeq)) {
					for (int i = 0; i < returnArr.size(); i++) {
						JSONObject cardInfoObj = returnArr.getJSONObject(i);
						if (StringUtils.equals(cardSeqForBatchs, cardInfoObj.getString("cardSeqForBatch"))) {
							cardsOnePkad.add(cardInfoObj);
							returnData.put(String.valueOf(pkadSeq), cardsOnePkad);
						}
					}
				}
			}
		}
		return new Result(ResultCode.RESULT_STATUS_SUCCESS,returnData,"批量查询礼包卡券信息成功");
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave =true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getPkadListBySelForERP(String memGuid ,String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		data=data.trim();
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		Integer pageNo= jsonObj.getInteger("PageIndex");
		if(pageNo==null){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"PageIndex 参数错误");
		}
		Integer pageSize= jsonObj.getInteger("RowCount");
		if(pageSize==null||pageSize==0){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"RowCount 参数错误");
		}

		/*if(cacheUtils.isPartner(memGuid)){
			JSONObject returnJson=new JSONObject();
			returnJson.put("pkadList", new JSONArray());
			returnJson.put("TotalItems", 0);
			returnJson.put("PageIndex", 1);
			returnJson.put("TotalPage",0);
			return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
		}*/

		String hiddenOther= jsonObj.getString("hiddenOther");
		Map<String,Object> paramMap= new HashMap<>();
		paramMap.put("membId", memGuid);
		//是否直充，已作废。统一为0
		String isRecharge= jsonObj.getString("isRecharge");
		if(StringUtils.isBlank(isRecharge)){
			isRecharge="0";
		}
		paramMap.put("isRecharge",Integer.valueOf(isRecharge));

		if(StringUtils.isBlank(hiddenOther)){
			hiddenOther="1";
		}
		if(StringUtils.equals(hiddenOther, "1")){
			paramMap.put("beginNewMrstDate", beginNewMrstDateStr);
			paramMap.put("lastShowHiddenMrstDate", lastShowHiddenMrstDateStr);
			paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS);
			/*if(cacheUtils.isPartner(memGuid)){
				paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS_PARTNER);
			}*/
		}


		int returnListCount = pkadBaseServiceWithCache.getPkadCountBySelective(memGuid, paramMap,true);
		paramMap.put("start", (pageNo-1)*pageSize);
		paramMap.put("pageSize", pageSize);	
		
		String order= jsonObj.getString("order");
		String sortType= jsonObj.getString("sortType");
		
		if(!StringUtils.isBlank(sortType) && !sortType.equals("asc") && !sortType.equals("desc")){
			sortType="desc";
		}
		if(!StringUtils.isBlank(order) &&!StringUtils.isBlank(sortType)){
			paramMap.put("order", order);	
			paramMap.put("sortType", sortType);	
		}
		List<Pkad> returnPkads= pkadBaseServiceWithCache.getPkadListBySelective(memGuid, paramMap,true);
		JSONArray returnJsonArr;
			returnJsonArr = new JSONArray();
			for (Pkad pkad : returnPkads) {
					JSONObject pkadJson = new JSONObject();
					FastDateFormat sdfDbDate = FastDateFormat.getInstance("yyyyMMdd");
					FastDateFormat sdf=FastDateFormat.getInstance("yyyy/MM/dd HH:mm:ss");
					String acctiveTimeStr="";
					String expiredTimeStr="";
					String takeTimeStr="";
					String giveTimeStr="";
		        	Date dateF = null; //生效日期
					Date dateT = null; //失效日期
					Date ddTake = null; //领取时间
					Date ddPkad=null; //发放日期
					Calendar cZero = Calendar.getInstance();
					cZero.setTime(new Date());
					cZero.set(Calendar.HOUR_OF_DAY, 0);
					cZero.set(Calendar.MINUTE, 0);
					cZero.set(Calendar.SECOND, 0);
					cZero.set(Calendar.MILLISECOND, 0);
					Date todayZero=cZero.getTime();
					 try {   
						    ddPkad = sdfDbDate.parse(pkad.getDdPkad());   
						    giveTimeStr=sdf.format(ddPkad);
				        } catch (Exception e) {  
				            log.error("日期格式转换失败，ddPkad=" + pkad.getDdPkad(), "getPkadListBySelForERP");
				    } 
			        try {   
			        	dateF = sdfDbDate.parse(pkad.getDdTakeF());   
			        	acctiveTimeStr=sdf.format(dateF);
			        } catch (Exception e) {
						log.error("日期格式转换失败，ddTakeF=" + pkad.getDdTakeF(), "getPkadListBySelForERP");
			        }  
			        try {
			        	dateT = sdfDbDate.parse(pkad.getDdTakeT());
			        	expiredTimeStr=sdf.format(DateUtil.getTimeOf235959Date(dateT));
			        } catch (Exception e) {
						log.error("日期格式转换失败，ddTakeT=" + pkad.getDdTakeT(), "getPkadListBySelForERP");
			        }
			        try {
			        	//数据库中默认值为"1980-00-00 00:00:00"
			        	String ddTakeStr=null;
			        	if(pkad.getDdTake()!=null && pkad.getDdTake().length()>19){
			        		ddTakeStr=pkad.getDdTake().substring(0, 19);
			        	}
			        	FastDateFormat sdfDbTime =FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
			        	Date defaultTakeTime = sdfDbTime.parse("1980-01-01 00:00:00"); 
				        ddTake = sdfDbTime.parse(ddTakeStr); 
				        if(!ddTake.after(defaultTakeTime)){
				        	takeTimeStr="";
				        }else{
				        	takeTimeStr=sdf.format(ddTake);
				        }
			        } catch (Exception e) {  
			        	log.error("日期格式转换失败，ddTake=" + pkad.getDdTake(), "getPkadListBySelForERP");
			        }
			        
			        pkadJson.put("pkadGiveTime",giveTimeStr);
			        pkadJson.put("pkadValidTime",acctiveTimeStr+"-"+expiredTimeStr);
			        pkadJson.put("pkadTakeTime", takeTimeStr);
					pkadJson.put("pkadDesc",ConstantMrst.GET_MRSTUI_DESC.get(pkad.getMrstUi()));
					try {
						JSONObject takeCardSeqJson=JSONObject.parseObject(pkad.getTakeCardSeq());
						pkadJson.put("takeCardSeq", takeCardSeqJson);
					} catch (Exception e) {
						log.error("已领取卡券转换为json失败，takeCardSeq="+pkad.getTakeCardSeq(),"getPkadListBySelForERP");
					}
			        if(ConstantMrst.IS_T_DB.equals(pkad.getIsTake())){
			        	pkadJson.put("pkadStatus", "已领取");
			        }else {
				        if(ConstantMrst.IS_T_DB.equals(pkad.getIsCancel())){
				        	pkadJson.put("pkadStatus", "已取消");
				        }else {
				        	if(dateF!=null && todayZero.before(dateF)){
					        	pkadJson.put("pkadStatus", "未生效");
					        }
					        else if(dateT!=null && todayZero.after(dateT)){
					        	pkadJson.put("pkadStatus", "已过期");
					        }else{
					        	pkadJson.put("pkadStatus", "未领取");
					        }
				        }
			        }
					returnJsonArr.add(pkadJson);
				
			}
		JSONObject returnJson=new JSONObject();
		returnJson.put("pkadList", returnJsonArr);
		returnJson.put("TotalItems", returnListCount);
		returnJson.put("PageIndex", pageNo);
		returnJson.put("TotalPage",(int) Math.ceil((double)returnListCount/(double)pageSize));
		return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
	}
	
	/**
	 *  领取礼包通知CRM
	 * @param memGuid
	 * @return dataJson
	 */
	@Override
	public void saveTakePkadTokafkaForCRM(String memGuid,Pkad pkad,Date now,JSONArray cardInfo){
	       	if(pkad!=null){
				String  dateTaken = null  ;//领取日期
				String  timeTaken = null;//领取时间
				try {
					timeTaken=DateUtil.getFormatDate(now,"HHmmss");
					dateTaken=DateUtil.getFormatDate(now,"yyyyMMdd");
				} catch (Exception e) {
					log.error("error：日期格式转换失败", "saveTakePkadTokafkaForCRM",e);
				}
		        Map<String, Object> info = new HashMap<>();
		        info.put("memb_id", memGuid);
		        info.put("pkad_id", pkad.getPkadId());
		        info.put("memb_grade_f", pkad.getMembGradeF());
		        info.put("d_taken", dateTaken);
		        info.put("t_taken", timeTaken);
		        info.put("is_taken", "T");
		        if(cardInfo!=null&&!cardInfo.isEmpty()){
		        	info.put("card_info", cardInfo);
		        }
		        String message = JSONObject.toJSONString(info);
		        try {
		 			long keys = System.currentTimeMillis();
		 			producerClient.sendMessage(pkadTakenTopic,String.valueOf(keys)+"_takePkad", message);
		 			log.info("礼包领取消息发送成功:"+message,"saveTakePkadTokafkaForCRM");
		        } catch (Exception e) {
		        	log.error("礼包领取消息发送失败。message=" + message,"saveTakePkadTokafkaForCRM", e);
		        	scoreExceptionHandler.handlerBizException(e, memGuid, message , Constant.PKAD_TAKEN_KAFKA);
		        }
	       	}else{
	       		throw new BizException(ResultCode.RESULT_STATUS_EXCEPTION, ": 礼包数据为空");
	       	}
		
	}
	
	@Override
	@DynamicDataSource(index = 0,isReadSlave =true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getPkadCountBySelForERP(String memGuid ,String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		data=data.trim();
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		/*if(cacheUtils.isPartner(memGuid)){
			JSONObject returnJson=new JSONObject();
			returnJson.put("TotalItems", 0);
			return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
		}*/

		Map<String,Object> paramMap= new HashMap<>();
		paramMap.put("membId", memGuid);
		//是否直充，已作废。统一为0
		String isRecharge= jsonObj.getString("isRecharge");
		if(StringUtils.isBlank(isRecharge)){
			isRecharge="0";
		}
		paramMap.put("isRecharge",Integer.valueOf(isRecharge));

		String hiddenOther= jsonObj.getString("hiddenOther");
		if(StringUtils.isBlank(hiddenOther)){
			hiddenOther="1";
		}
		if(StringUtils.equals(hiddenOther, "1")){
			paramMap.put("beginNewMrstDate", beginNewMrstDateStr);
			paramMap.put("lastShowHiddenMrstDate", lastShowHiddenMrstDateStr);
			paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS);
			/*if(cacheUtils.isPartner(memGuid)){
				paramMap.put("showMrsts", constantPkadIsShow.SHOW_MRSTS_PARTNER);
			}*/
		}

		int returnListCount= pkadBaseServiceWithCache.getPkadCountBySelective(memGuid, paramMap,true);
		JSONObject returnJson=new JSONObject();
		returnJson.put("TotalItems", returnListCount);
		return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnJson,"success");
	}

	/*
		读取unsuccess表中的礼包领取失败数据，并校验
		防止出现领取接口超时，但是查询已领卡券接口查询失败或者查询到的数据有延迟（正在生成券号，因此当时查询时是未领取）
	*/
	@Override
	@DynamicDataSource(index = 0)
	@Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
	public void processOutTimePkadLog(String memGuid, String pkadSeq){

		Pkad pkad=pkadBaseServiceWithCache.getPkadByPkadSeqAndMembIdForUpdate(memGuid, pkadSeq);
		if(pkad==null){
			return ;
		}
		if(ConstantMrst.IS_T_DB.equals(pkad.getIsTake()) &&StringUtils.isNotBlank(pkad.getTakeCardSeq())&&(pkad.getTakeCardSeq().contains("\""))) {
			//已领取，takeCardSeq是json格式数据，说明之前的数据正确。
			return;
		}
		JSONArray cardInfoArr=null;
		try {
			cardInfoArr = JSONObject.parseArray(pkad.getCardInfo());
		}catch (Exception e){
			throw new ScoreException(ResultCode.RESULT_TYPE_CONV_ERROR,"card_info解析错误");
		}
		Date takeTime=null;

		String takeTimeStr=null;

		FastDateFormat sdfDbTime = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
		FastDateFormat sdfDbDate = FastDateFormat.getInstance("yyyyMMdd");
		Date ddTake = null;
		try {
			//数据库中默认值为"1980-00-00 00:00:00"，防止timestamp类型错误
			String ddTakeStr=null;
			if(pkad.getDdTake()!=null && pkad.getDdTake().length()>19) {
				ddTakeStr = pkad.getDdTake().substring(0, 19);
				Date defaultTakeTime = sdfDbTime.parse("1980-01-01 00:00:00");
				ddTake = sdfDbTime.parse(ddTakeStr);
				if (!ddTake.after(defaultTakeTime)) {
					takeTimeStr = "";
				} else {
					takeTimeStr = sdfDbTime.format(ddTake);
				}
			}
		} catch (Exception e) {
			log.error("日期格式转换失败，ddTake=" + pkad.getDdTake(),"processOutTimePkadLog");
		}

		if (cardInfoArr != null && !cardInfoArr.isEmpty()) {
            JSONArray cardInfoParam=getCardInfoParamFromCardInfo(pkad,cardInfoArr);
            JSONObject seqReturnseqObject=cardService.batchGetCardByCardseq(memGuid,cardInfoParam);
            JSONObject returnOjb=seqReturnseqObject.getJSONObject(TAKEN_MAP);

            if(returnOjb.size()>0){
                Date dateT = null; //失效日期
                try {
                    dateT = sdfDbDate.parse(pkad.getDdTakeT());
                } catch (Exception e) {
                    log.error("日期格式转换失败，ddTakeT=" + pkad.getDdTakeT(),"processOutTimePkadLog");
                }
                takeTime=new Date();
                if(takeTime.after(dateT)){
                    takeTime=dateT;
                }
                pkad.setDdTake(sdfDbTime.format(takeTime));
                String combinedTakenSeq=populateReturnSeq(pkad.getTakeCardSeq(), returnOjb);
                pkad.setTakeCardSeq(combinedTakenSeq);
                populateReturnSeqToCardInfo(cardInfoArr,combinedTakenSeq);
                if((returnOjb.size()==cardInfoArr.size())&&pkad.getIsTake()==1) {
                    pkad.setIsTake(ConstantMrst.IS_T_DB);
                    saveTakePkadTokafkaForCRM(memGuid, pkad, takeTime, cardInfoArr);
                }
                if(pkad.getIsTake()==0){
                    pkad.setIsTake(ConstantMrst.IS_T_DB);
                    saveTakePkadTokafkaForCRM(memGuid, pkad, takeTime, cardInfoArr);
                }
                pkadBaseServiceWithCache.updatePkad(memGuid, pkad);
            }
		}
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave = true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getTakenCardInfo(String memGuid, String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		String pkadSeq= jsonObj.getString("pkadSeq");
		if(StringUtils.isEmpty(pkadSeq)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"pkadSeq 不能为空");
		}
		Pkad pkad= pkadBaseServiceWithCache.getPkadByPkadSeqAndMembId(memGuid, pkadSeq);
		if(pkad==null){
			return new Result(ResultCode.TAKE_PKAD_BUT_NOT_EXSIT,"该礼包不存在");
		}
		if(StringUtils.isBlank(pkad.getCardInfo())){
			return new Result(ResultCode.TAKE_PKAD_BUT_NOT_EXSIT,"该礼包不包含卡券");
		}else{
		    //map cardseq and cardid  for judge old ({cardId:[returnNo,returnno]} or {cardId:cardno,cardId:cardno}) or new ({cardseq:cardno})
		    Map<String,String> cardSeqCardIdMap=new HashMap<>();
            String cardInfoArrStr=pkad.getCardInfo();
            JSONArray cardInfoArr=JSONArray.parseArray(cardInfoArrStr);
            for(int i=0;i<cardInfoArr.size();i++){
                JSONObject cardInfo=cardInfoArr.getJSONObject(i);
                JSONArray cardList=cardInfo.getJSONArray("card_list");
                if(cardList!=null&&cardList.size()>0){
                    for(int j=0;j<cardList.size();j++){
                        JSONObject card=cardList.getJSONObject(j);
                        String cardSeq=card.getString("card_seq");
                        String cardId=card.getString("card_id");
                        if(!StringUtils.isEmpty(cardSeq)&&!StringUtils.isEmpty(cardId)){
                            cardSeqCardIdMap.put(cardSeq,cardId);
                        }
                    }
                }
            }
            Set<String> cardSeqSet=cardSeqCardIdMap.keySet();
            Set<String> takenKeySet=null;
            JSONObject takeCardJson=JSONObject.parseObject(pkad.getTakeCardSeq());
			JSONArray newReturnArr=new JSONArray();
			if(takeCardJson!=null&&!takeCardJson.isEmpty()) {
				takenKeySet = takeCardJson.keySet();
				//card id shouldn't be shown to front end ,for security
				//judge if key is cardid or cardseq then get cardidset
				Iterator<String> takenKeyIt=takenKeySet.iterator();
				boolean ifSeqAsKey=false;
				if(cardSeqSet.contains(takenKeyIt.next())){
				ifSeqAsKey=true;
			}
				JSONArray returnArr =null;
			if(ifSeqAsKey){
				Set<String> cardIdSet=new HashSet<>();
				//seq ad key in takencardseq
				for(String seq:takenKeySet){
					cardIdSet.add(cardSeqCardIdMap.get(seq));
				}
				returnArr=cardDao.getTakenCardInfoByCardIdAndType(cardIdSet,false);
			}else{
				//cardid as key in taken card seq
				returnArr=cardDao.getTakenCardInfoByCardIdAndType(takenKeySet,false);
			}
				Map<String ,Integer> cardIdCountMap=new HashMap<>();
			if(ifSeqAsKey){
				Iterator<String> it =takenKeySet.iterator();
				while(it.hasNext()){
					String cardId=cardSeqCardIdMap.get(it.next());
					if(cardIdCountMap.get(cardId)==null){
						cardIdCountMap.put(cardId,1);
					}else{
						cardIdCountMap.put(cardId,cardIdCountMap.get(cardId)+1);
					}
				}
			}else{
				//in case of duplicated card_id {"card_id":["returnseq","returnseq"]}
				Iterator<String> it= takenKeySet.iterator();
				while(it.hasNext()){
					String key=it.next();
					try {
						JSONArray ja =takeCardJson.getJSONArray(key);
						cardIdCountMap.put(key, ja.size());
					}catch (Exception e){
						//old format {"card_id":"returnseq"}
						cardIdCountMap.put(key, 1);
					}
				}
			}
            newReturnArr.addAll(returnArr);
            for(int k=0;k<returnArr.size();k++){
                JSONObject jo= returnArr.getJSONObject(k);
                String key =jo.getString("cardId");
				if(cardIdCountMap.get(key)!=null){
					int size=cardIdCountMap.get(key);
					if(size>1){
						for(int j=0;j<size-1;j++){
							JSONObject jodup= new JSONObject(jo);
							newReturnArr.add(jodup);
						}
					}
				}
            }

			for(int k=0;k<newReturnArr.size();k++){
				JSONObject jo=newReturnArr.getJSONObject(k);
				jo.remove("cardId");
			}
			}
			return new Result(ResultCode.RESULT_STATUS_SUCCESS,newReturnArr,"查询礼包已领取卡券信息成功");
		}
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave = true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getTakenCardInfoBatch(String memGuid, String data){
		if(StringUtils.isEmpty(data)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"入参不能为空");
		}
		JSONObject jsonObj = JSONObject.parseObject(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memGuid 不能为空");
		}
		/*if(cacheUtils.isPartner(memGuid)){
			throw new ScoreException(ResultCode.TAKE_PKAD_BUT_IS_PARTNER,"合伙人不能打开礼包");
		}*/
		String pkadSeqsStr= jsonObj.getString("pkadSeqs");
		if(StringUtils.isEmpty(pkadSeqsStr)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"pkadSeqs 不能为空");
		}
		String[] pkadSeqs = pkadSeqsStr.split(",");
		Set<String> pkadSeqSet=new HashSet<>();
		Collections.addAll(pkadSeqSet, pkadSeqs);
		List<Pkad> pkadList= pkadBaseServiceWithCache.getPkadsByPkadSeqsAndMembId(memGuid,pkadSeqSet);
		if(pkadList==null||pkadList.isEmpty()){
			return new Result(ResultCode.TAKE_PKAD_BUT_NOT_EXSIT,"对应礼包不存在");
		}
		Map<Integer,Map<String ,Integer>> pkadCards=new HashMap<>();
		JSONObject returnData=new JSONObject();

		Set<String> takenCardIdSet=new HashSet<>();
		for(Pkad pkad :pkadList){
			Map<String ,Integer> takenCardIdCountMap=new HashMap<>();
			if(StringUtils.isBlank(pkad.getTakeCardSeq())){
				returnData.put(String.valueOf(pkad.getPkadSeq()),new JSONArray());
			}else{
				Map<String,String> cardSeqCardIdMap=new HashMap<>();
				String cardInfoArrStr=pkad.getCardInfo();
				JSONArray cardInfoArr=JSONArray.parseArray(cardInfoArrStr);
				for(int i=0;i<cardInfoArr.size();i++){
					JSONObject cardInfo=cardInfoArr.getJSONObject(i);
					JSONArray cardList=cardInfo.getJSONArray("card_list");
					if(cardList!=null&&cardList.size()>0){
						for(int j=0;j<cardList.size();j++){
							JSONObject card=cardList.getJSONObject(j);
							String cardSeq=card.getString("card_seq");
							String cardId=card.getString("card_id");
							if(!StringUtils.isEmpty(cardSeq)&&!StringUtils.isEmpty(cardId)){
								cardSeqCardIdMap.put(cardSeq,cardId);
							}
						}
					}
				}
				Set<String> cardSeqSet=cardSeqCardIdMap.keySet();
				Set<String> takenKeySet=null;
				JSONObject takeCardJson=JSONObject.parseObject(pkad.getTakeCardSeq());
				if(takeCardJson!=null&&!takeCardJson.isEmpty()) {
					takenKeySet = takeCardJson.keySet();
					//card id shouldn't be shown to front end ,for security
					//judge if key is cardid or cardseq then get cardidset
					Iterator<String> takenKeyIt = takenKeySet.iterator();
					boolean ifSeqAsKey = false;
					if (cardSeqSet.contains(takenKeyIt.next())) {
						ifSeqAsKey = true;
					}
					if(ifSeqAsKey){
						//seq ad key in takencardseq
						for(String seq:takenKeySet){
							takenCardIdSet.add(cardSeqCardIdMap.get(seq));
							String cardId = cardSeqCardIdMap.get(seq);
							if (takenCardIdCountMap.get(cardId) == null) {
								takenCardIdCountMap.put(cardId, 1);
							} else {
								takenCardIdCountMap.put(cardId, takenCardIdCountMap.get(cardId) + 1);
							}
						}
					}else{
						takenCardIdSet.addAll(takenKeySet);
						//in case of duplicated card_id {"card_id":["returnseq","returnseq"]}
						for (String key : takenKeySet) {
							try {
								JSONArray ja = takeCardJson.getJSONArray(key);
								takenCardIdCountMap.put(key, ja.size());
							} catch (Exception e) {
								//old format {"card_id":"returnseq"}
								takenCardIdCountMap.put(key, 1);
							}
						}
					}
					pkadCards.put(pkad.getPkadSeq(),takenCardIdCountMap);
				}
			}
		}

		JSONArray returnArr =cardDao.getTakenCardInfoByCardIdAndType(takenCardIdSet,true);
		for(Integer pkadSeq:pkadCards.keySet()){
			JSONArray cardsOnePkad=new JSONArray();
			Map<String ,Integer> takenCardIdCountMap=pkadCards.get(pkadSeq);
			for(String takenCardId:takenCardIdCountMap.keySet()){
				for(int i=0;i<returnArr.size();i++) {
					JSONObject cardInfoObj = returnArr.getJSONObject(i);
					JSONObject returnCardInfo=JSONObject.parseObject(cardInfoObj.toJSONString());
					if (StringUtils.equals(takenCardId, cardInfoObj.getString("cardId"))) {
						for (int j = 0; j < takenCardIdCountMap.get(takenCardId); j++){
							returnCardInfo.remove("cardId");
							cardsOnePkad.add(returnCardInfo);
						}
						returnData.put(String.valueOf(pkadSeq), cardsOnePkad);
					}
				}
			}
		}
		return new Result(ResultCode.RESULT_STATUS_SUCCESS,returnData,"批量查询礼包已领取卡券信息成功");
	}

	@Override
	@DynamicDataSource(index = 0,isReadSlave =true)
	@Transactional(readOnly=true,value = "transactionManagerScore")
	public Result getLastPkad(String memGuid) {

		//从缓存中去查。因为有过期，所以只缓存一天。key也带上时间
		String mrstUi_mrstUiname = pkadBaseServiceWithCache.getLastPkadMrsuUi(memGuid, true);

		if (StringUtils.isBlank(mrstUi_mrstUiname)) {
			return new Result(ResultCode.GET_LASTPKAD_NOT_EXSIT, "该用户没有未领取的礼包");
		} else {
			HashMap<String, String> map = new HashMap<>();
			if (ConstantCache.NONE_STRING.equals(mrstUi_mrstUiname)) {
				return new Result(ResultCode.GET_LASTPKAD_NOT_EXSIT, "该用户没有未领取的礼包");
			} else {
				String[] mrstUiAndMrstUiname = mrstUi_mrstUiname.split("_");
				String pkadName = "";
				String pkadType = mrstUiAndMrstUiname[0];
				map.put("type", pkadType);
				if (ConstantMrst.MRST_UI_0.equals(pkadType) && mrstUiAndMrstUiname.length > 1) {
					//if selfdefine has name
					pkadName = mrstUiAndMrstUiname[1];
				} else {
					pkadName = ConstantMrst.GET_MRSTUI_DESC.get(mrstUiAndMrstUiname[0]);
				}
				map.put("msg", pkadName);
				return new Result(ResultCode.RESULT_STATUS_SUCCESS, map, "查询礼包成功");
			}

		}
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
}
