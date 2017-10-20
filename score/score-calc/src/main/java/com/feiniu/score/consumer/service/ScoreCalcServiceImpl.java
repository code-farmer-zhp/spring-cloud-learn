package com.feiniu.score.consumer.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.ScoreAndGrowthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class ScoreCalcServiceImpl implements ScoreCalcService {

	private static final CustomLog LOGGER = CustomLog.getLogger(ScoreCalcServiceImpl.class);
	@Autowired
	private ScoreAndGrowthService scoreAndGrowthService;

	@Autowired
	private ScoreExceptionHandler scoreExceptionHandler;

	public void calcScore(String message) {

		String memGuid = null;
		try {
			memGuid = getMemGuid(message);
			scoreAndGrowthService.processingScoreMessage(message,0);
		} catch (Exception e) {
			LOGGER.error("kafka消息处理异常。message=" + message, e);
			try {
				scoreExceptionHandler.handlerScoreException(e, memGuid, message);
			}catch (Exception ex){
				LOGGER.error("保存错误信息异常。",ex);
			}

		}
		// 处理成长值
		scoreAndGrowthService.processingGrowthMessage(message);
	}

	private String getMemGuid(String message) {
		JSONObject info = JSONObject.parseObject(message);
		JSONObject data = info.getJSONObject("data");
		Integer type = info.getInteger("type");
		String memGuid;
		if (Objects.equals(type, Constant.CRM_ABOUT_SCORE)) {
			memGuid = data.getString("memb_id");
		} else {
			memGuid = data.getString("memGuid");
		}
		if (StringUtils.isEmpty(memGuid)) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空。");
		}
		return memGuid;
	}

}
