package com.example.logprogram;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnConnectionParamBean;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class SSLconnect {
	private static HttpClient httpClient;
    private SSLconnect(){
    	
    }
    public static synchronized HttpClient getHttpClient(){
    	//initial
    	if (null ==httpClient){
    		try{
    			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    			trustStore.load(null,null);
    			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
    			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//允許所有主機的驗證
    			HttpParams params = new BasicHttpParams();
    			HttpProtocolParams.setVersion(params,HttpVersion.HTTP_1_1);		
    			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
    			HttpProtocolParams.setUseExpectContinue(params, true);
    			//設定連接管理器超時
    			ConnManagerParams.setTimeout(params, 10000);
    			HttpConnectionParams.setConnectionTimeout(params, 10000);//設定連線超時
    			HttpConnectionParams.setSoTimeout(params, 10000);
    			SchemeRegistry schReg = new SchemeRegistry();
    			schReg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(),80));
    			schReg.register(new Scheme("http",sf,443));
    			ClientConnectionManager conManager = new ThreadSafeClientConnManager(params,schReg); 
    			httpClient = new DefaultHttpClient(conManager,params);
    		}catch(Exception e){
    			e.printStackTrace();
    			return new DefaultHttpClient();		
    		}
    	}
    	return httpClient;	
    }
    	
  } 
class SSLSocketFactoryEx extends SSLSocketFactory{

	SSLContext sslContext = SSLContext.getInstance("TLS");
	public SSLSocketFactoryEx(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		// TODO Auto-generated constructor stub
        TrustManager tm = new X509TrustManager(){

        	 @Override
             public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                 return null;
             }
  
             @Override
             public void checkClientTrusted(
                     java.security.cert.X509Certificate[] chain, String authType)
                     throws java.security.cert.CertificateException {
  
             }
  
             @Override
             public void checkServerTrusted(
                     java.security.cert.X509Certificate[] chain, String authType)
                     throws java.security.cert.CertificateException {
  
             }  	
        };
        sslContext.init(null, new TrustManager[]{tm}, null);
	}
	 @Override
	    public Socket createSocket(Socket socket, String host, int port,
	            boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port,
	                autoClose);
	    }
	 
	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }

}
