package com.ahutpt.lesson;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class NetworkHelper {

	public static String readURL(String u) {
		String str = "";
		try {
			URL url = new URL(u);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.connect();
			InputStream stream = conn.getInputStream();
			byte[] data = new byte[102400];
			int length = stream.read(data);
			str = new String(data, 0, length);
			conn.disconnect();
			stream.close();
		} catch (Exception ee) {
			System.out.print("ee:" + ee.getMessage());
		}
		return str;
	}
	public static String postURL(String URL, List<NameValuePair> params) {
		HttpPost httpRequest = new HttpPost(URL);
		String strResult = null;
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strResult;
	}
}
