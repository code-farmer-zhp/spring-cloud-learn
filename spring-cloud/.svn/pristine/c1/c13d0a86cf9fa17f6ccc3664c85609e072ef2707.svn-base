package com.feiniu.member.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * 
 *
 *
 */
public class SmSeqTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -5234234235768536218L;
	private String smSeq;
	public void setSmSeq(String smSeq) {
		this.smSeq = smSeq;
	}

	@Override
	public int doStartTagInternal() throws JspException{
		WebApplicationContext ctx = getRequestContext().getWebApplicationContext();
		SmSeqUtil smSeqUtil = (SmSeqUtil) ctx.getBean("smSeqTag");
		if(StringUtils.isBlank(smSeq) || "null".equals(smSeq) || "_".equals(smSeq)){
			smSeq="";
		}else{
			if(smSeq.contains("/")){
				String[] smSeqSplitList=smSeq.split("/");
				for(int i=smSeqSplitList.length-1;i>=0;i--){
					String smSeqSplit=smSeqSplitList[i];
					if(StringUtils.isNotBlank(smSeqSplit)){
						smSeq=smSeqSplit;
						break;
					}
				}
			}
		}
		String result=smSeqUtil.getMarketUrl(1,smSeq);
        try {  
            pageContext.getOut().write(result);
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        return SKIP_BODY;
    }


}