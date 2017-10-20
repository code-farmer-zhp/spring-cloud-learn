package com.feiniu.score.service;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.TestBasicService;
import com.feiniu.score.vo.OrderJsonVo;

public class growthTest extends TestBasicService{

	@Override
	public void doTest() {
	}

	/*@Test
	public void myTest(){
		String voStr="{\"memGuid\": \"044A8094-7195-6620-AC07-BF75251F24B6\",\"provinceId\": \"上海\",\"ogSeq\": \"201601CO07035556\",\"ogNo\": \"201601CP07035556\",\"sourceMode\": \"1\",\"siteMode\": \"1\",\"self\": [{\"olSeq\": \"201601C0700311756\",\"itNo\": \"201512CG230000014\",\"qty\": 1,\"realPay\": 499,\"packageNo\": \"1\",\"smSeq\": \"201512CM230000020\",\"kind\": \"1\",\"card\": 0,\"coupons\": 0,\"vouchers\": 0,\"bonus\": 0,\"sellActivity\": 0,\"score\": 0,\"price\": 499,\"fdlSeq\": \"DG000027\",\"id\": \"1\",\"pId\": \"0\",\"aprnVvipPoints\": 0,\"aprnVvip\": 0,\"useBalancePoints\": 0,\"dividendDiscount\": 0,\"scoreMallDiscount\": 0},"
			+"{\"olSeq\": \"201601C0700311757\",\"itNo\": \"201512CG250000012\",\"qty\": 3,\"realPay\": 338.1,\"packageNo\": \"2\",\"smSeq\": \"201512CM250000012\",\"kind\": \"1\",\"card\": 0,\"coupons\": 0,\"vouchers\": 0,\"bonus\": 0,\"sellActivity\": 0,\"score\": 9,\"price\": 112.7,\"fdlSeq\": \"DG000001\",\"id\": \"2\","
				+"\"pId\": \"0\",\"buyMode\": 1,\"aprnVvipPoints\": 0,\"aprnVvip\": 0,\"useBalancePoints\": 0,\"dividendDiscount\": 119.1,\"scoreMallDiscount\": 30}],\"mall\": []}";
        JSONObject voJson=JSONObject.parseObject(voStr);
        System.out.println(voJson);
        OrderJsonVo orderJsonVo=OrderJsonVo.convertJson(voJson);
        growthOrderService.orderPay("044A8094-7195-6620-AC07-BF75251F24B6", orderJsonVo);
	}*/

	/*@Test
	public void saveGrowthkafkafromCRMTest(){
		String message="{\"gtad_id\":\"MRST000128\",\"memb_id\":\"B313DD76-F5D5-4144-7B24-CCC33602AEBD\",\"memb_grade_f\":\"+\",\"gtad_type\":\"2\",\"mrdf_type\":\"A1\",\"mrdf_growth\":\"200\",\"d_gtad\":\"20160127\",\"d_take\":\"null\",\"d_eff_f\":\"null\",\"d_eff_t\":\"20991231\"}";
		growthMemService.saveGrowthkafkafromCRM("B313DD76-F5D5-4144-7B24-CCC33602AEBD",message);
	}
*/
/*	@Test
	public void getUsePriceByActSeqTest(){
		growthCommonDao.getUsePriceByActSeq("ZY000032436");
		//searchMemberDao.getMemberInfo("044A8094-7195-6620-AC07-BF75251F24B6");
	}*/
}
