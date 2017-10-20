package com.feiniu.score.util.phoneDataPlan;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wangbing
 * @create 2013-9-24
 */
public class HttpClientHelperDemo {
	private static final int connectionTimeOut = 2000;

	private static final int soTimeout = 40000;

	private static Logger logger = Logger.getLogger(HttpClientHelperDemo.class);

	public static String post2(String actionUrl, Map<String, String> params) {
		BasicHttpParams bp = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(bp, connectionTimeOut); // 超时时间设置
		HttpConnectionParams.setSoTimeout(bp, soTimeout);
		HttpClient httpclient = new DefaultHttpClient(bp);
		HttpPost httpPost = new HttpPost(actionUrl);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
			String key = entry.getKey();
			String value = entry.getValue();
			list.add(new BasicNameValuePair(key, value));
		}
		HttpResponse httpResponse;
		String responseString = "";
		logger.warn("传入后台的参数：" + list);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			httpResponse = httpclient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				responseString = EntityUtils.toString(httpResponse.getEntity());
				return responseString;
			} else if (httpResponse.getStatusLine().getStatusCode() == 404) {
				logger.warn("actionUrl:{} not found 404!" + actionUrl);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, String> params = new HashMap<String, String>();
		try {

			String certificatePath = "D:/testdata/jszt.cer";
			//String s = "reqId=20041020161116153858703856;accNbr=13122759151;actionCode=order_qixin_001;offerSpecl=300509026206;ztInterSource=200410;goodName=流量优惠/流量赠送（HX）/500M省内流量/当月;staffValue=X;type=1";
			String s = "reqId=20041020161116153858703855;actionCode=order_qixin_006;accNbr=X;ztInterSource=200410";
			//String s = "actionCode=order_qixin_005;accNbr=X;ztInterSource=200410;date=201611;dateType=1";
			System.out.println("123:" + s.length());
			byte[] encrypt = CertificateHelper.encryptByPublicKey(s.getBytes(),
					certificatePath);


			params.put("para", Base64.encode(encrypt));
			//String rs = post2("http://202.102.111.142/jszt/ipauth/orderPackageByQiXin", params);
			String rs = post2("http://202.102.111.142/jszt/ipauth/queryOrder", params);
			//String rs = post2("http://202.102.111.142/jszt/ipauth/checkAccount", params);
			System.out.println(rs);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
