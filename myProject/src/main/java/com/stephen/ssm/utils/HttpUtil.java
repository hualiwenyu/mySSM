package com.stephen.ssm.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author stephen
 * @ClassName: HttpUtil
 * @Description: HTTP 请求工具类
 * @date 2016年10月19日 下午7:49:30
 */
public class HttpUtil {

	private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

	private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 5000;

	static {

		SSLContext sslcontext = null;
		try {
			sslcontext = createIgnoreVerifySSL();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 设置协议http和https对应的处理socket链接工厂的对象
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext)).build();

		// 采用绕过验证的方式处理https请求
		connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		// 设置连接池大小
		connMgr.setMaxTotal(200);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();

		// 启动connectionMonitor守护线程
		IdleConnectionMonitorThread idleConnectionMonitorThread = new IdleConnectionMonitorThread(connMgr);
		idleConnectionMonitorThread.start();
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），K-V形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param params
	 *            参数map
	 * @return
	 */
	public static String doPost(String apiUrl, Map<String, Object> params) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr)
				.setDefaultRequestConfig(requestConfig).build();
		String httpStr = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setConfig(requestConfig);
		List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
			pairList.add(pair);
		}
		httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, "utf-8");

			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					LOG.error("error when consume response entity",e);
				}
			}
		} catch (Exception e) {
			httpPost.abort();
			LOG.error("error when do httpPost",e);
			return null;
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return httpStr;
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），JSON形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param json
	 *            JSON对象
	 * @return
	 */
	public static String doPost(String apiUrl, String json) throws Exception {
		String httpStr = null;
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr)
				.setDefaultRequestConfig(requestConfig).build();
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setConfig(requestConfig);
		StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("application/json;charset=utf-8");
		httpPost.setEntity(stringEntity);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, "utf-8");

			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					LOG.error("error when consume response entity",e);
				}
			}
		} catch (Exception e) {
			httpPost.abort();
			LOG.error("error when do httpPost",e);
			return null;
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return httpStr;
	}
	
	 /**
     * 发送 GET 请求（HTTP）
     *
     * @param apiUrl API接口URL
     * @return
     */
    public static String doGet(String apiUrl) throws Exception {
    	String httpStr = null;
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr)
				.setDefaultRequestConfig(requestConfig).build();
		HttpGet httpGet = new HttpGet(apiUrl);
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, "utf-8");

			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					LOG.error("error when consume response entity",e);
				}
			}
		} catch (Exception e) {
			httpGet.abort();
			LOG.error("error when do httpGet",e);
			return null;
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return httpStr;

    }

	/**
	 * 绕过验证
	 *
	 * @return
	 * @throws Exception
	 */
	public static SSLContext createIgnoreVerifySSL() throws Exception {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
//			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

//			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

//			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(doPost("http://www.baidu.com", "aa"));
	}
	
}

class IdleConnectionMonitorThread extends Thread {
	private final PoolingHttpClientConnectionManager connMgr;
	private volatile boolean shutdown;

	public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr) {
		super();
		this.setName("idle-connection-monitor");
		this.setDaemon(true);
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				synchronized (this) {
					wait(5000);
					// Close expired connections
					connMgr.closeExpiredConnections();
					// Optionally, close connections
					// that have been idle longer than 30 sec
					connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
				}
			}
		} catch (InterruptedException ex) {
			// terminate
		}
	}

	public void shutdown() {
		synchronized (this) {
			shutdown = true;
			notifyAll();
		}
	}
	
}