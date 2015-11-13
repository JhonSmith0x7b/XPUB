package com.elex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.elex.SuspensionButton;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CacheImage {
	final static String TAG="XpubCacheImage";
	public static Bitmap get(URL url) {
		File directory = new File(SuspensionButton.getInstance().getGameActivity().getCacheDir(), md5(url.getPath()));
		FileInputStream file = null;
		try {
			file = new FileInputStream(directory);
		} catch (FileNotFoundException e) {
			Log.i(TAG, "file "+url+" not cached");
		}
		if(file != null) return BitmapFactory.decodeStream(file);
		else return null;
	}

	public static Bitmap set(URL url, InputStream input) {
	
		File directory = new File(SuspensionButton.getInstance().getGameActivity().getCacheDir(), md5(url.getPath()));
	
		OutputStream file;
		try {
			file = new FileOutputStream(directory);
			int a =0;
			while(( a =input.read()) != -1){
				file.write(a);
			}
			file.close();
			Log.d(TAG, "file "+url+"  cached");
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
         
		try {
			return BitmapFactory.decodeStream(new FileInputStream(directory));
		} catch (FileNotFoundException e) {
			Log.e(TAG, "file "+url+"  cache error!");
		}
		return null;
	}
	
	public static String md5(String str) {
		byte[] bytesOfMessage;
		byte[] thedigest = null;
		MessageDigest md;
		try {
			bytesOfMessage = str.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
			 thedigest = md.digest(bytesOfMessage);
		} catch (NoSuchAlgorithmException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		
		try {
			return new String(thedigest, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	
	
	

}
