package com.feiniu.score.consumer;

import com.feiniu.score.consumer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalcMainForWar {

    @Autowired
    private ScoreConsumerService scoreConsumerService;

    @Autowired
    private GradeConsumerService gradeConsumerService;

    @Autowired
    private GrowthConsumerService growthConsumerService;

    @Autowired
    private CancelMallOrderService cancelMallOrderService;

    @Autowired
    private ReturnOrderConfirmService returnOrderConfirmService;

    @Autowired
    private GrowthReceiveOrderService growthReceiveOrderService;

    @Autowired
    private GrowthMemLoginService growthMemLoginService;

    @Autowired
    private RegisteredConsumerService registeredConsumerService;

    @Autowired
    private ScoreStatusCalcService scoreStatusCalcService;

    @Autowired
    private MemberModifyPhoneCalcService memberModifyPhoneCalcService;

    @Autowired
    private MemberModifyEmailCalcService memberModifyEmailCalcService;

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private PkadConsumerService pkadConsumerService;

    @Autowired
    private FillInInterestCalcService fillInInterestCalcService;

    @Autowired
    private ScoreEffectSendILConsumerService scoreEffectSendILConsumerService;

    @Autowired
    private AppUpGradeService appUpGradeService;

    @Autowired
    private GrowthValueNumChangeConsumerService growthValueNumChangeConsumerService;

    @Autowired
    private UnionistBindConsumerService unionistBindConsumerService;

    @Autowired
    private VipScoreRollBackService vipScoreRollBackService;

    public void start() {
        //积分（包括提交订单、付款）
        scoreConsumerService.calcOrderScore();

        gradeConsumerService.calcGradeScore();

        growthConsumerService.calcGrowthScore();

        //取消订单
        //cancelMallOrderService.calcCancelMallOrder();

        //退货确认
        returnOrderConfirmService.calcReturnOrderConfirm();

        // 收货确认
        growthReceiveOrderService.calcGrowthReceiveOrder();

        //登录
        //growthMemLoginService.calcMemLogin();

        //注册 不送积分了
        registeredConsumerService.calcRegistered();

        //订单状态
        scoreStatusCalcService.calcOrderStatus();

        //修改手机
        memberModifyPhoneCalcService.calcModifyPhone();

        //修改邮箱
        memberModifyEmailCalcService.calcModifyEmail();

        //支付完成
        orderPayService.calcOrderPay();

        //礼包
        pkadConsumerService.calcMrsf();

        //完善兴趣爱好
        fillInInterestCalcService.fillInInterest();

        //积分生效站内信提醒
        scoreEffectSendILConsumerService.calcScoreEffectSendIL();

        //app升级送积分
        appUpGradeService.calcAppUpGrade();

        //成长值统计变动
        growthValueNumChangeConsumerService.growthValueNumChange();

        //工会注册送券
        unionistBindConsumerService.calcUnionistBind();

        //积分商城兑券站内券 失败回滚积分
        vipScoreRollBackService.calcScoreRollBack();
    }
}
