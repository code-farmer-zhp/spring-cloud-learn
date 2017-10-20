package com.feiniu.member.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Random;

public class PicTag extends TagSupport{


	private static final long serialVersionUID = -558960795066536218L;
    
    private String picUrl;
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

    private String storeDomainUrl;
	public void setStoreDomainUrl(String storeDomainUrl) {
		this.storeDomainUrl = storeDomainUrl;
	}

	private String size;
	public void setSize(String size) {
		this.size = size;
	}

	private String type;
	public void setType(String type) {this.type = type ;}

	private String imgInsideUrl;
	public void setImgInsideUrl(String imgInsideUrl) {this.imgInsideUrl = imgInsideUrl ;}

	private static Random random=new Random();

	public int doStartTag() throws JspException{
		if(StringUtils.isBlank(picUrl) || "null".equals(picUrl) ){
			picUrl="";
		}else{
			picUrl=picTransform(picUrl,storeDomainUrl,size,type,imgInsideUrl);
		}
        try {  
            pageContext.getOut().write(picUrl);  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        return super.doStartTag();
    }


	public static String picTransform(String picUrl, String storeUrl, String size, String type, String imgInsideUrl) {
		if(type.equals("1")){
		    if(StringUtils.isNotBlank(size)){
		        picUrl = picUrl.replace(".","_"+size+".");
		    }else{
		        picUrl = picUrl.replace(".","_80x80.");
		    }
			picUrl = imgInsideUrl + picUrl;
		}else{
			picUrl = picUrl.replace(".jpg_60x60.jpg", "_60x60.jpg");
			picUrl = picUrl.replace("http://wh-image01.fn.com:80/", "http://imgsvr01.beta1.fn/");
			picUrl = picUrl.replace("http://wh-image01.fn.com/", "http://imgsvr01.beta1.fn/");
			picUrl = picUrl.replace("http://10.211.64.68", "http://imgsvr01.beta1.fn");
			picUrl = picUrl.replace("img10","img"+ Integer.toString(random.nextInt(3)+16));
			String[] split = picUrl.split("\\.");
			if(split[split.length-2].indexOf("_60x60")<0){
				picUrl = picUrl.replace("."+split[split.length-1],"_"+size+"."+split[split.length-1]);
			}
			if(picUrl.indexOf("http") < 0){
				String[] strings = picUrl.split("/");
				if(!"pic".equals(strings[1])) {
					picUrl="/pic"+picUrl;
				}
				String[] storeUrlSplit = storeUrl.split(";");
				int l=  storeUrlSplit.length ;
				int x =   random.nextInt(l);
				picUrl=storeUrlSplit[x]+picUrl;
			}
		}

		return picUrl;
	}
}