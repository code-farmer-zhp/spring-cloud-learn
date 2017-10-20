package com.feiniu.score.poi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.entity.dataplan.OrderPackageLog;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.phoneDataPlan.Base64;
import com.feiniu.score.util.phoneDataPlan.CertificateHelper;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by code72 on 2016/11/23.
 */
public  class ExcelUtil {
    private static final CustomLog log = CustomLog.getLogger(ExcelUtil.class);
    public static void createSheet(String month , List<OrderPackageLog> feiniuList, JSONObject dianxinList){

        HSSFWorkbook wb = new HSSFWorkbook();
        //飞牛充值记录sheet
        HSSFSheet sheet = wb.createSheet();
        wb.setSheetName(0,"飞牛充值记录");
        sheet.setDefaultRowHeight((short) 350);
        sheet.setDefaultColumnWidth(16);


        // 创建标题栏样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 居中

        HSSFCellStyle styleTitle = wb.createCellStyle();
        HSSFFont fontTitle = wb.createFont();
        fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleTitle.setFont(fontTitle);
        styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        //创建第0行
        HSSFRow rowTitle0 = sheet.createRow(0);
        HSSFCell cellTitle0 = rowTitle0.createCell(0);
        cellTitle0.setCellValue("注册时间");
        cellTitle0.setCellStyle(styleTitle);


        HSSFCell cell = rowTitle0.createCell(1);
        cell.setCellValue("手机号");
        cell.setCellStyle(styleTitle);

        cell = rowTitle0.createCell(2);
        cell.setCellValue("充值商品类型");
        cell.setCellStyle(styleTitle);

        cell = rowTitle0.createCell(3);
        cell.setCellValue("充值商品名称");
        cell.setCellStyle(styleTitle);

        cell = rowTitle0.createCell(4);
        cell.setCellValue("充值状态");
        cell.setCellStyle(styleTitle);

        if(feiniuList!=null) {
            int size = feiniuList.size();
            for(int i=0;i<size;i++) {
                OrderPackageLog orderPackageLog = feiniuList.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                FastDateFormat sdt = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
                String format = sdt.format(orderPackageLog.getRegTime());
                cell.setCellValue(format);
                cell.setCellStyle(style);

                cell = row.createCell(1);
                cell.setCellValue(orderPackageLog.getOrderPhone());
                cell.setCellStyle(style);

                cell = row.createCell(2);
                cell.setCellValue(orderPackageLog.getOfferSpecl());
                cell.setCellStyle(style);

                cell = row.createCell(3);
                cell.setCellValue(orderPackageLog.getGoodName());
                cell.setCellStyle(style);

                cell = row.createCell(4);
                String statusStr="";
                String status = orderPackageLog.getStatus();
                switch (status){
                    case "1":
                        statusStr="成功";
                        break;
                    case "0":
                        statusStr="失败";
                        break;
                    case "-1":
                        statusStr="返回结果失败";
                        break;
                    default:
                        statusStr="";
                }
                cell.setCellValue(statusStr);
                cell.setCellStyle(style);
            }
        }


        //电信充值记录sheet
        sheet = wb.createSheet();
        wb.setSheetName(1,"电信充值记录");
        sheet.setDefaultRowHeight((short) 350);
        sheet.setDefaultColumnWidth(24);


        rowTitle0 = sheet.createRow(0);
        cellTitle0 = rowTitle0.createCell(0);
        cellTitle0.setCellValue("注册时间(orderDate)");
        cellTitle0.setCellStyle(styleTitle);


        cell = rowTitle0.createCell(1);
        cell.setCellValue("手机号(telPhone)");
        cell.setCellStyle(styleTitle);

        cell = rowTitle0.createCell(2);
        cell.setCellValue("充值商品类型(productType)");
        cell.setCellStyle(styleTitle);

        cell = rowTitle0.createCell(3);
        cell.setCellValue("充值商品名称(productName)");
        cell.setCellStyle(styleTitle);

        cell = rowTitle0.createCell(4);
        cell.setCellValue("充值状态(state)");
        cell.setCellStyle(styleTitle);

        if(dianxinList!=null) {
                JSONArray data = dianxinList.getJSONArray("data");
                if (data != null) {
                    int size = data.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            JSONObject o = (JSONObject) data.get(i);
                            HSSFRow row = sheet.createRow(i + 1);
                            cell = row.createCell(0);
                            try {
                                FastDateFormat simpleDateFormat = FastDateFormat.getInstance("yyyyMMddHHmmss");
                                Date orderDate = simpleDateFormat.parse(o.getString("orderDate"));
                                FastDateFormat simpleDateFormat2 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
                                String format = simpleDateFormat2.format(orderDate);
                                cell.setCellValue(format);
                                cell.setCellStyle(style);
                            }catch (Exception e) {
                                log.error("日期转化失败:"+o.getString("orderDate"));
                            }

                            cell = row.createCell(1);
                            cell.setCellValue(o.getString("telPhone"));
                            cell.setCellStyle(style);

                            cell = row.createCell(2);
                            cell.setCellValue(o.getString("productType"));
                            cell.setCellStyle(style);

                            cell = row.createCell(3);
                            cell.setCellValue(o.getString("productName"));
                            cell.setCellStyle(style);

                            cell = row.createCell(4);
                            String statusStr = "";
                            String status = o.getString("state");
                            switch (status) {
                                case "1":
                                    statusStr = "成功";
                                    break;
                                case "0":
                                    statusStr = "失败";
                                    break;
                                default:
                                    statusStr = "";
                            }
                            cell.setCellValue(statusStr);
                            cell.setCellStyle(style);
                        }
                    }
                } else {
                    log.info("查询的电信数据为空,month:"+month);
                }
        } else {
            log.info("查询的电信数据失败,month:"+month);
        }

        try {
            FileOutputStream fout = new FileOutputStream("/tmp/dianxin_report_"+month+".xls");
            wb.write(fout);
            fout.close();
        } catch (Exception e) {
            log.error("生成excel失败");
        }

    }

    public static void main(String[] args) {

        String ss = "aPRa5zXf+8qBd87n6RyNAnjSr5WGcn5yVVQ6HlIBr1EXWsukab2jYDwQXORgHLpXZXvvIOrFsUVR7mKfC8X5vZfv/07gE+OJLBV/sv1hrWDljvNCovnqotLNYNC8aXraOMz3jILVFdXiAfZmpPLiC0hu12cNK9VEYuW6q/s1Oaw=";
        StringBuilder paramStr=new StringBuilder();
        paramStr.append("accNbr=").append("13613676768").append(";");
        paramStr.append("actionCode=").append("order_qixin_005").append(";");
        paramStr.append("ztInterSource=").append("200410").append(";");
        paramStr.append("date=").append("201612").append(";");
        paramStr.append("dateType=").append("1");
        System.out.println(paramStr.toString());
        String sd="E:\\jszt.cer";
        MultiValueMap<String, Object> params=new LinkedMultiValueMap<>();
        byte[] encrypt = new byte[0];
        try {
            encrypt = CertificateHelper.encryptByPublicKey(paramStr.toString().getBytes(),
                    sd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(encrypt);
        System.out.println( Base64.encode(encrypt));

    }
}
