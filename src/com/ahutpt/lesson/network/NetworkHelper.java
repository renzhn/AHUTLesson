package com.ahutpt.lesson.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class NetworkHelper {

	public static String readURL(String u) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(u);
		try {
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			StringBuffer s = new StringBuffer("");
			String str = "";
			while ((str = in.readLine()) != null) {
				s.append(str);
			}
			httpClient.getConnectionManager().shutdown();
			return s.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
