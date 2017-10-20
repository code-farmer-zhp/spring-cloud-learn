package com.feiniu.score.main;

import com.feiniu.score.service.ReportDbService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 已经为单独的项目job
 */
@Deprecated
public class ScoreReportJobMain {
    private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

    private ApplicationContext applicationContext;

    public static ReportDbService reportDbService;

    public ScoreReportJobMain() {
        this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
    }

    public void start() {
        reportDbService = applicationContext.getBean(ReportDbService.class);
        // 统计财务积分报表job
        reportDbService.start();
    }

    public static void main(String[] args) {
        ScoreReportJobMain main = new ScoreReportJobMain();
        main.start();
        // 强制退出
        System.exit(0);
    }
}
