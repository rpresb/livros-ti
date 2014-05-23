package br.com.presba.livros_ti.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class JSONManager {

	public static String execute(String url) throws IOException {
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		
		if (entity != null) {
		
			InputStream instream = entity.getContent();
			String json = toString(instream);
			instream.close();
			
			//Log.i(Utility.TAG, json);
			
			return json;
		}
		
		return null;
	}
	
	private static String toString(InputStream is) throws IOException {
		
		byte[] bytes = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int lidos;
		
		while ((lidos = is.read(bytes)) > 0) {
			baos.write(bytes, 0, lidos);
		}
		
		return new String(baos.toByteArray());
	}
}
