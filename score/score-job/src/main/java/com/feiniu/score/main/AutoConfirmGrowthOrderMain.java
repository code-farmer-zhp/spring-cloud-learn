package com.feiniu.score.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.service.GrowthOrderServiceImpl;

/**
 * 4、自动第10天给支付的订单成长值
 * 定时任务自动为支付了 10 天 还未确认收货的 订单赠送成长值
 * 需求：客人未进行收货确认时，到支付后的第10天赠送成长值（包含支付当日）
 * @return
 */
public class AutoConfirmGrowthOrderMain {
	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;

	public static GrowthOrderServiceImpl growthOrderService;
	
	public AutoConfirmGrowthOrderMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		growthOrderService = applicationContext.getBean(GrowthOrderServiceImpl.class);
		// 自动第10天给支付的订单成长值
		growthOrderService.autoConfirmOrder();
	}

	public static void main(String[] args) {
		AutoConfirmGrowthOrderMain main = new AutoConfirmGrowthOrderMain();
		main.start();
		// 强制退出
		System.exit(-1);
	}
}
