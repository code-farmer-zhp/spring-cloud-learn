package com.feiniu.score.job.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.dao.notice.MailSender;
import com.feiniu.score.entity.dataplan.OrderPackageLog;
import com.feiniu.score.ftp.FtpUtil;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.poi.ExcelUtil;
import com.feiniu.score.service.PhoneDataPlanService;
import com.jcraft.jsch.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhoneDataPlanCalServiceImpl implements PhoneDataPlanCalService{
    private static CustomLog log = CustomLog.getLogger(PhoneDataPlanCalServiceImpl.class);

    @Autowired
    private PhoneDataPlanService phoneDataPlanService;

    @Value("${ftpPort}")
    private String ftpPort;

    @Value("${ftpHost}")
    private String ftpHost;

    @Value("${ftpAgreement}")
    private String ftpAgreement;

    @Value("${ftpUser}")
    private String ftpUser;

    @Value("${ftpPwd}")
    private String ftpPwd;

    @Autowired
    private MailSender mailSender;
    @Override
    public void checkAccountByMonth(String month){
        List<OrderPackageLog> oplList= phoneDataPlanService.getOrderPackageLogByMonth(month,null);//飞牛的数据
        String s = phoneDataPlanService.checkAccountRest(month, "1");
        log.info(month+",调用电信充值列表接口返回，"+s);
        JSONObject jsonObject = JSONObject.parseObject(s);//电信的数据
        try {
            ExcelUtil.createSheet(month,oplList,jsonObject);
        } catch (Exception e) {
           log.error("生成表格时异常");
        }

        //上传文件
        Channel channel = FtpUtil.ftpConnect(ftpHost, Integer.valueOf(ftpPort), ftpUser, ftpPwd, ftpAgreement);
        if (channel != null) {
            FtpUtil.uploadFile(channel, "dianxin_report_"+month+".xls");
            log.info("上传文件到到FCM磁盘成功");
        } else {
            log.info("与FCM磁盘建立连接失败");
        }


        ArrayList<String> emailList = new ArrayList<>();
        emailList.add("chunmei.li@feiniu.com");
        emailList.add("fb11@feiniu.com");
        emailList.add("jiewen.xue@feiniu.com");
        emailList.add("yali.dong@feiniu.com");

        for (int i = 0; i < emailList.size(); i++) {
            String email = emailList.get(i);
            String rtn = mailSender.sendMess("电信充值记录", email,"飞牛网电信充值记录报表!"
                        , "2", "1", "8", "dianxin_report_"+month+".xls");
            log.info(rtn);


        }

    }

    private String getStatusWord(String status){
        switch (status) {
            case "0": return "失败";
            case "1": return "成功";
            default:  return "" ;
        }
    }
}
