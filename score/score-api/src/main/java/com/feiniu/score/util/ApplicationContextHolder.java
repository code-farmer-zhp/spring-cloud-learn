package com.feiniu.score.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextHolder implements ApplicationContextAware {

		private static ApplicationContext ctx = null;
		
		@Override
		public void setApplicationContext(ApplicationContext applicationContext)
				throws BeansException {
		
			ctx = applicationContext;
		}
		
		public static Object getBean(String beanName){
			
			return ctx.getBean(beanName);
		}

}
