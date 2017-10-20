package com.feiniu.score.util;

import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


public class HttpRequestUtil {
    private String connectTimeout;

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(String readTimeout) {
        this.readTimeout = readTimeout;
    }

    private String readTimeout;

    private static final CustomLog log = CustomLog.getLogger(HttpRequestUtil.class);


    public String sendPostJson(String reqURL, String json) {

        // 将JSON进行UTF-8编码,以便传输中文
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Integer.parseInt(readTimeout)).setConnectTimeout(Integer.parseInt(connectTimeout)).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(reqURL);
        String responseBody = null;
        try {
            httpPost.setConfig(requestConfig);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            StringEntity stringEntity = new StringEntity(json, "UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-type", "application/json");

            responseBody = httpClient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            log.error("IOException from " + reqURL + " with " + json, "sendPostJson", e);
            throw new ScoreException("HTTP CLIENT EXCEPTION : CLASS:HttpClientUtil", e);
        } finally {
            httpPost.releaseConnection();
        }

        return responseBody;

    }

}