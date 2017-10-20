package com.feiniu.member.util;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 用于页面jstl时间格式化
 */
public class StrDateTag extends TagSupport {

    private static final long serialVersionUID = -3354015162721342312L;
    private String value;

    public void setValue(String value) {
        this.value = value;
    }


    public int doStartTag() throws JspException {
        SimpleDateFormat dateformatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date s = dateformatAll.parse(value);
            Date dd = Calendar.getInstance().getTime();
            int month =(int) DateUtil.getMonth(s, dd);
            if(month<=3){
                return EVAL_BODY_INCLUDE;
            }else{
                return SKIP_BODY; //跳过标签体，不处理
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.doStartTag();
    }

}