package com.feiniu.score.service;

import java.util.Date;
import java.util.Map;

import com.feiniu.score.vo.OrderJsonVo;
import com.feiniu.score.vo.ReturnJsonVo;

public interface GrowthOrderService {

	
	public void orderInput(String memGuid , OrderJsonVo vo);
	
	public void orderPay(String memGuid , OrderJsonVo vo);	

	void receiveOrder(String memGuid, String message, Map<String,Date> keyDate);
	
	public Map<String, Object> computeGrowthValueByOrderJson(OrderJsonVo vo,boolean isEmployee);

	void orderReturn(String memGuid, ReturnJsonVo vo, Map<String, Date> keyDate);

	//OrderJsonVo covertOrderJsonWithGroup(OrderJsonVo vo);
}
