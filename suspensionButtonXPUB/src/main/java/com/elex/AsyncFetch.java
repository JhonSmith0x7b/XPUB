package com.elex;

import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.elex.util.CacheImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncFetch extends AsyncTask<String, Void, Bitmap> {
	public static boolean fetchCheck=false;
	@Override
	protected Bitmap doInBackground(String... urls) {
		String urlDisplay = urls[0];
//		String urlDisplay="http://img2.imgtn.bdimg.com/it/u=2861270851,2291089313&fm=21&gp=0.jpg";
		Bitmap myImg = null;
		try {
			myImg=CacheImage.get(new URL(urlDisplay));
			if(myImg!=null)return myImg;
			InputStream is = new java.net.URL(urlDisplay).openStream();
			CacheImage.set(new URL(urlDisplay), is);
			myImg = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myImg;
	}
	/**
	 * 緩存靜態數據中圖片的工具方法
	 */
	public static void fetchImg(){
		fetchCheck=true;
		if(SuspensionButton.mStrMsgBus==null||SuspensionButton.mStrMsgBus.equals(""))return;
		String result = SuspensionButton.mStrMsgBus;
		try {
			JSONObject jObject=new JSONObject(result);
			JSONArray jBannerArray=jObject.getJSONArray("banners");
			for(int i =0;i<jBannerArray.length();i++){
				JSONObject jObjectI = jBannerArray.getJSONObject(i);
				String imgUrl = jObjectI.optString("img");
				new AsyncFetch().execute(imgUrl);
			}
			JSONArray jTaskArray=jObject.getJSONArray("tasks");
			for(int i=0;i<jTaskArray.length();i++){
				JSONObject jObjectI = jTaskArray.getJSONObject(i);
				String imgUrl = jObjectI.optString("img");
				new AsyncFetch().execute(imgUrl);
			}
			JSONArray jInfoArray=jObject.getJSONArray("infos");
			for(int i=0;i<jInfoArray.length();i++){
				JSONObject jObjectI = jInfoArray.getJSONObject(i);
				String imgUrl = jObjectI.optString("img");
				new AsyncFetch().execute(imgUrl);
			}
		} catch (Exception e) {
			Log.d("AsyncFetchERROR", "fetchImg");
		}
	}

}
