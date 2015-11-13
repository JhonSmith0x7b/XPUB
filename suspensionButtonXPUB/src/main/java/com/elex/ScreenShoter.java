package com.elex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class ScreenShoter {
	Context mContext;
	public ScreenShoter(Context context){
		this.mContext=context;
	}
	public Bitmap shot(){
		View view =((Activity)mContext).getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap screenShot=view.getDrawingCache();
		File directory = new File(((Activity)mContext).getCacheDir(),"lalala");
		if(!directory.exists())directory.mkdirs();
		try {
			FileOutputStream fos = new FileOutputStream(directory.getPath()+"/fbShare.jpg");
			screenShot.compress(Bitmap.CompressFormat.JPEG, 50, fos);
			SharePhoto photo = new SharePhoto.Builder()
					.setBitmap(screenShot)
					.build();
			SharePhotoContent content=new SharePhotoContent.Builder()
					.addPhoto(photo)
					.build();
			ShareDialog.show((Activity)mContext, content);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return screenShot;
	}
}
