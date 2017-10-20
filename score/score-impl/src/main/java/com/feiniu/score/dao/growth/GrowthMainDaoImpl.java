package com.feiniu.score.dao.growth;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.ProducerClient;
import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreDefalutTableDao;
import com.feiniu.score.entity.growth.GrowthMain;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.mapper.growth.GrowthMainMapper;
import com.feiniu.score.util.ExceptionMsgUtil;
import com.feiniu.score.util.ShardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GrowthMainDaoImpl implements GrowthMainDao {

	private static final CustomLog log = CustomLog.getLogger(GrowthMainDaoImpl.class);
	@Autowired
	private GrowthMainMapper growthMainMapper;
	@Autowired
	@Qualifier("producerGrowthClient")
	private ProducerClient<Object, String> producerClient;

	@Value("${fn.topic.growth.value.num}")
	private String growthTopic;

	@Autowired
	private ScoreDefalutTableDao scoreDefalutTableDao;

	@Override
	public int deleteGrowthMainById(String memGuid, Long gmSeq) {
		int changedRows = 0;
		GrowthMain growthMain = growthMainMapper.getGrowthMainById(gmSeq,
				ShardUtils.getTableNo(memGuid));
		if (growthMain != null) {
			changedRows = growthMainMapper.deleteGrowthMainById(gmSeq,
					ShardUtils.getTableNo(memGuid));
			if(changedRows>0){
				//growthValueNumDao.duceTableGrowthValueNum(growthMain.getGrowthValue());
				growthValueNumChangeToKafka(memGuid,growthMain.getGrowthValue(),null);
			}
		}
		return changedRows;
	}

	@Override
	public int saveGrowthMain(String memGuid, GrowthMain gm) {
		int changedRows = growthMainMapper.saveGrowthMain(gm,
				ShardUtils.getTableNo(memGuid));
		if(changedRows>0){
			//growthValueNumDao.addTableGrowthValueNum(gm.getGrowthValue());
			growthValueNumChangeToKafka(memGuid, null, gm.getGrowthValue());
		}
		return changedRows;
	}

	@Override
	public GrowthMain getGrowthMainById(String memGuid, Long gmSeq) {
		return growthMainMapper.getGrowthMainById(gmSeq,
				ShardUtils.getTableNo(memGuid));
	}

	@Override
	public int updateGrowthMain(String memGuid, GrowthMain gm) {

		GrowthMain growthMainSel = growthMainMapper
				.getGrowthMainByMemGuid(memGuid,
						ShardUtils.getTableNo(memGuid));
		int changedRows = growthMainMapper.updateGrowthMain(gm,
				ShardUtils.getTableNo(memGuid));
		if (changedRows>0) {
			if (growthMainSel != null) {
				if(gm.getChangedGrowthValue()!=null) {
					int myGrowthValueOld = growthMainSel.getGrowthValue();
					int myGrowthValueNew = myGrowthValueOld+gm.getChangedGrowthValue();
					if (myGrowthValueNew != myGrowthValueOld) {
						/*growthValueNumDao.changeTableGrowthValueNum(myGrowthValueOld,
								myGrowthValueNew);*/
						growthValueNumChangeToKafka(memGuid, myGrowthValueOld, myGrowthValueNew);
					}
				}else if(gm.getGrowthValue()!=null){
					int myGrowthValueOld = growthMainSel.getGrowthValue();
					int myGrowthValueNew = gm.getGrowthValue();
					if (myGrowthValueNew != myGrowthValueOld) {
						/*growthValueNumDao.changeTableGrowthValueNum(myGrowthValueOld,
								myGrowthValueNew);*/
						growthValueNumChangeToKafka(memGuid, myGrowthValueOld, myGrowthValueNew);
					}
				}
			}
			//growthLogDao.updateLog(memGuid, gm, gm.getGmSeq(), "growth_main");
		}
		return changedRows;
	}

	@Override
	public List<GrowthMain> getGrowthMainList(String memGuid,
			Map<String, Object> paramMap) {
		return growthMainMapper.getGrowthMainListBymemGuid(memGuid, paramMap,
				ShardUtils.getTableNo(memGuid));
	}

	@Override
	public GrowthMain getGrowthMainByMemGuid(String memGuid) {
		return growthMainMapper.getGrowthMainByMemGuid(memGuid,ShardUtils.getTableNo(memGuid));
	}

	@Override
	public int getGrowthMainListCount(String memGuid) {
		return growthMainMapper.getGrowthMainListCountBymemGuid(memGuid,
				ShardUtils.getTableNo(memGuid));
	}

	@Override
	public int saveGrowthValueWithValueZero(String memGuid) {
			int changedRows = 0;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("start", 0);
			paramMap.put("pageSize", 5);
			paramMap.put("memGuid", memGuid);
			List<GrowthMain> growthMainList = growthMainMapper
					.getGrowthMainListBymemGuid(memGuid, paramMap,
							ShardUtils.getTableNo(memGuid));

			if (growthMainList == null || growthMainList.size() == 0) {
				GrowthMain gm = new GrowthMain();
				gm.setMemGuid(memGuid);
				gm.setGrowthValue(0);
				changedRows = growthMainMapper.saveGrowthMain(gm,
						ShardUtils.getTableNo(memGuid));
				if (changedRows > 0) {
					//growthValueNumDao.addTableGrowthValueNum(0);
					growthValueNumChangeToKafka(memGuid, null, 0);
				}
			}
		return changedRows;
	}


	@Override
	public int changeGrowthValue(String memGuid, int changedGrowthValue) {
		int changedRows = 0;
		if (changedGrowthValue != 0) {
			GrowthMain growthMainSel = growthMainMapper
					.getGrowthMainByMemGuid(memGuid,
							ShardUtils.getTableNo(memGuid));
			if (growthMainSel != null) {
				int myGrowthValueOld = growthMainSel.getGrowthValue();
				int myGrowthValueNew = myGrowthValueOld + changedGrowthValue;
				changedRows = growthMainMapper.changeGrowthValue(memGuid,
						changedGrowthValue, ShardUtils.getTableNo(memGuid));
				if (changedRows > 0) {
					if (myGrowthValueNew != myGrowthValueOld) {
						/*growthValueNumDao.changeTableGrowthValueNum(myGrowthValueOld,
								myGrowthValueNew);*/
						growthValueNumChangeToKafka(memGuid, myGrowthValueOld, myGrowthValueNew);
					}
				}
			}
			else {
				GrowthMain gm = new GrowthMain();
				gm.setMemGuid(memGuid);
				gm.setGrowthValue(changedGrowthValue);
				changedRows = growthMainMapper.saveGrowthMain(gm,
						ShardUtils.getTableNo(memGuid));
				if (changedRows > 0) {
					//growthValueNumDao.addTableGrowthValueNum(changedGrowthValue);
					growthValueNumChangeToKafka(memGuid, null, changedGrowthValue);
				}
			}
		}
		return changedRows;
	}

	@Override
	public List<GrowthMain> getGrowthMainListForUpdate(Map<String, Object> mapParam, int tableNo) {
		return growthMainMapper.getGrowthMainListForUpdate(mapParam,tableNo);
	}

	@Override
	public GrowthMain getGrowthMainByGuidForUpdate(String memGuid) {
		return growthMainMapper.getGrowthMainByIdForUpdate(memGuid,ShardUtils.getTableNo(memGuid));
	}

	//growthValueDuce 数量减一的成长值
	//growthValueAdd 数量加一的成长值
	private void growthValueNumChangeToKafka(String memGuid,Integer growthValueDuce,Integer growthValueAdd){
		Map<String, Object> dataJson = new HashMap<>();
		dataJson.put("memGuid",memGuid);
		dataJson.put("growthValueDuce",growthValueDuce);
		dataJson.put("growthValueAdd",growthValueAdd);
		String message = JSONObject.toJSONString(dataJson);
		try {
			producerClient.sendMessage(growthTopic, System.currentTimeMillis() + "", message);
			log.info("成长值统计变动message:" + message,"growthValueNumChangeToKafka");
		} catch (Exception e) {
			log.error("成长值统计变动，发送kafka消息失败。","growthValueNumChangeToKafka", e);
			String errorMsg = ExceptionMsgUtil.getMsg(e);
			scoreDefalutTableDao.handleFailMessage(memGuid, message, Constant.GROWTH_VALUE_NUM_CHANGE, errorMsg);
		}
	}
}
