package com.feiniu.member.web.listener;

import com.feiniu.member.log.CustomLog;
import com.feiniu.member.util.SystemEnv;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map.Entry;
import java.util.Properties;

public class InitListener implements ServletContextListener {

	private static final CustomLog logger = CustomLog.getLogger(InitListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext application = sce.getServletContext();
		Properties systemProperties = SystemEnv.getProperties();
		for (Entry<Object, Object> entry : systemProperties.entrySet()) {
			String name = (String) entry.getKey();
			name = name.replace(".", "_");
			Object value = entry.getValue();
			application.setAttribute(name, value);
			logger.info("Add system property to application: " + name + "=" + value);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}
}
