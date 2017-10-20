package com.feiniu.member.httpclient;

import com.feiniu.member.log.CustomLog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

//	private static Log logger = LogFactory.getLog(HttpClientUtil.class);
	private static final CustomLog logger = CustomLog.getLogger(HttpClientUtil.class);
//	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
//	private static final String ENCODING_GZIP = "gzip";
//	private static final String APPLICATION_JSON = "application/json";
//    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	
	
	public static String sendPostJson(String reqURL , String json){
		
		
		logger.info("send json begin =============================================================>");
		logger.info(json);
		logger.info("send json end =============================================================>");
		
		 // 将JSON进行UTF-8编码,以便传输中文
//		 CloseableHttpClient httpClient = HttpClients.createDefault();
		 CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		 RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10*1000).setConnectTimeout(10*1000).build();//设置请求和传输超时时间
		 HttpPost httpPost = new HttpPost(reqURL);
		 
		 String responseBody = null;
		    
		    try {
		    	
		    	 
			     httpPost.setConfig(requestConfig);
				  ResponseHandler<String> responseHandler = new BasicResponseHandler();
		    	
		        StringEntity stringEntity = new StringEntity(json , "UTF-8");
		        stringEntity.setContentType("application/json");
//		        stringEntity.setContentEncoding("UTF-8");
		        httpPost.setEntity(stringEntity);
		        httpPost.setHeader("Content-type", "application/json");
		        
		        responseBody = httpClient.execute(httpPost, responseHandler);
		    } catch (Exception e) {
		        logger.error("IOException from " + reqURL + " with " + json , e);
		    } finally {
//		    	httpClient.getConnectionManager().shutdown();
		    	httpPost.releaseConnection();
		    }
		    
		    
		    logger.info("sendPostJson responseBody="+responseBody);
		    return responseBody;
		
	}
	
	

	public static String sendPostRequest(String reqURL, Map<String, String> params, String encodeCharset, String decodeCharset){
        
		String responseContent = null;
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
//        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        
        HttpPost httpPost = new HttpPost(reqURL);
//        httpPost.setConfig(requestConfig);
        
        List<NameValuePair> formParams = new ArrayList<NameValuePair>(); //创建参数队列
        if(params != null){
        	for(Map.Entry<String,String> entry : params.entrySet()){
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset==null ? "UTF-8" : encodeCharset));
             
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        }catch(Exception e){
        	logger.error("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
        }finally{
        	httpPost.releaseConnection();
        }
        
        logger.info("sendPostRequest responseContent="+responseContent);
        return responseContent;
    }
	
	
}
