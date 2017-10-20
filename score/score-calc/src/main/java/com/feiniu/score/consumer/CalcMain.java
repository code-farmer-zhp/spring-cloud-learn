package com.feiniu.score.consumer;

import com.feiniu.score.consumer.service.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CalcMain {

	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext.xml";

	private ApplicationContext applicationContext;
	
	public CalcMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		//积分（包括提交订单、付款）
		ScoreConsumerService scoreConsumerService = applicationContext.getBean(ScoreConsumerService.class);
		scoreConsumerService.calcOrderScore();

		GradeConsumerService gradeConsumerService = applicationContext.getBean(GradeConsumerService.class);
		gradeConsumerService.calcGradeScore();
        
		GrowthConsumerService growthConsumerService = applicationContext.getBean(GrowthConsumerService.class);
		growthConsumerService.calcGrowthScore();

		//取消订单
		//CancelMallOrderService cancelMallOrderService = applicationContext.getBean(CancelMallOrderService.class);
		//cancelMallOrderService.calcCancelMallOrder();

		//退货确认
		ReturnOrderConfirmService returnOrderConfirmService = applicationContext.getBean(ReturnOrderConfirmService.class);
		returnOrderConfirmService.calcReturnOrderConfirm();
		
		// 收货确认
		GrowthReceiveOrderService growthReceiveOrderService = applicationContext.getBean(GrowthReceiveOrderService.class);
		growthReceiveOrderService.calcGrowthReceiveOrder();

		//登录
		//GrowthMemLoginService memLoginService = applicationContext.getBean(GrowthMemLoginService.class);
		//memLoginService.calcMemLogin();

		//注册 暂时不送积分了
		RegisteredConsumerService registeredService = applicationContext.getBean(RegisteredConsumerService.class);
		registeredService.calcRegistered();
		//礼包
		PkadConsumerService pkadConsumerService = applicationContext.getBean(PkadConsumerService.class);
		pkadConsumerService.calcMrsf();

		//订单状态
		ScoreStatusCalcService statusCalcService = applicationContext.getBean(ScoreStatusCalcService.class);
		statusCalcService.calcOrderStatus();

		//修改手机
		MemberModifyPhoneCalcService modifyPhoneCalcService = applicationContext.getBean(MemberModifyPhoneCalcService.class);
		modifyPhoneCalcService.calcModifyPhone();

		//修改邮箱
		MemberModifyEmailCalcService modifyEmailCalcService = applicationContext.getBean(MemberModifyEmailCalcService.class);
		modifyEmailCalcService.calcModifyEmail();

		//支付完成
		OrderPayService orderPayService = applicationContext.getBean(OrderPayService.class);
		orderPayService.calcOrderPay();
		
		//完善兴趣爱好
		FillInInterestCalcService fillInInterestCalcService = applicationContext.getBean(FillInInterestCalcService.class);
		fillInInterestCalcService.fillInInterest();

		//积分生效站内信提醒
		ScoreEffectSendILConsumerService scoreEffectSendILConsumerService = applicationContext.getBean(ScoreEffectSendILConsumerService.class);
		scoreEffectSendILConsumerService.calcScoreEffectSendIL();

		//app升级送积分
		AppUpGradeService appUpGradeService = applicationContext.getBean(AppUpGradeService.class);
		appUpGradeService.calcAppUpGrade();

		//成长值统计变动
		GrowthValueNumChangeConsumerService growthValueNumChangeConsumerService = applicationContext.getBean(GrowthValueNumChangeConsumerService.class);
		growthValueNumChangeConsumerService.growthValueNumChange();

		//工会注册送券
		UnionistBindConsumerService unionistBindConsumerService = applicationContext.getBean(UnionistBindConsumerService.class);
		unionistBindConsumerService.calcUnionistBind();

		//积分商城兑券站内券 失败回滚积分
		VipScoreRollBackService vipScoreRollBackService = applicationContext.getBean(VipScoreRollBackService.class);
		vipScoreRollBackService.calcScoreRollBack();
	}

	public static void main(String[] args) {
		CalcMain main = new CalcMain();
		main.start();
	}
	
}
