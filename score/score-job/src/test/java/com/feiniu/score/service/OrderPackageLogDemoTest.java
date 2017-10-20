package com.feiniu.score.service;

import com.feiniu.score.job.service.PhoneDataPlanCalServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author wangbing
 * @create 2013-9-24
 */
public class OrderPackageLogDemoTest {
	public static ClassPathXmlApplicationContext context = null;
	public static PhoneDataPlanCalServiceImpl psc= null;

	@BeforeClass
	public  static void init() throws Exception {
		context = new ClassPathXmlApplicationContext("applicationContext_main.xml");
		psc = context.getBean(PhoneDataPlanCalServiceImpl.class);
	}
	@Test
	public  void testCheck(){
		try {
			psc.checkAccountByMonth("201611");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
