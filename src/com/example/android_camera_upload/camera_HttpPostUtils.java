package com.example.android_camera_upload;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class camera_HttpPostUtils {

	public camera_HttpPostUtils() {
	}

	/**
	 * 
	 * @param path
	 * @param fileBody
	 * @param fileName
	 * @return
	 */
	public static void sendFormByPost(final String path, final byte[] fileBody,
			final String fileName) {
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(path);
					MultipartEntity entity = new MultipartEntity();
					entity.addPart("myfile", new ByteArrayBody(fileBody,
							fileName));
					httpPost.setEntity((HttpEntity) entity);
					httpClient.execute(httpPost);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}
}
