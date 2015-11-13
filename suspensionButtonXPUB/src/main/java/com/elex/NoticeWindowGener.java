package com.elex;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.elex.suspension.R;
import com.elex.util.CacheImage;
import com.elex.util.XpubUtils;
import com.google.gson.Gson;

public class NoticeWindowGener {
	private static NoticeWindowGener instance;
	private Context mContext;
	private DisplayMetrics dm;
	private WindowManager mWindowManager;
	private RelativeLayout rl; 
	private List<InfosHolder> infosHolders;
	private AlphaAnimation buttonClickT = new AlphaAnimation(1F,0.9F);
	private ImageView mainImage;
	private ImageView bound;
	private ImageView bound1;
	private ImageView bound2;
	private ImageView loadingImg;
	private ImageView boardBg;
	private ImageView getAwayImg;
	private TextView getAwayText;
	private ImageView loadingImage;
	@SuppressWarnings("unused")
	private static NoticeWindowGener getInstance(){
		return instance;
	}
	public NoticeWindowGener(Context context){
		mContext = context;
		dm=mContext.getResources().getDisplayMetrics();
		mWindowManager=(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		rl =(RelativeLayout) ((Activity)mContext).findViewById(R.id.rlNoticeWindow);
	}
	public static void init(Context context){
		instance = new NoticeWindowGener(context);
		instance.initPortrait();
	}
	private void initPortrait(){
		try{
		instance.setMain();
		instance.dealData(SuspensionButton.myBusBuBu);
		instance.changeNotice();
		}catch(Exception e){
			Log.e(SuspensionButton.TAG, "NoticeWindowGener initPotrait");
			Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
			((Activity)mContext).finish();
		}
	}
	public void dealData(String result){
		if(result==null||result.equals("")){
			return;
		}
		try {
		JSONObject	jObject=new JSONObject(result);
		JSONArray jArray = jObject.getJSONArray("infos");
		Gson gson = new Gson();
		infosHolders = new ArrayList<InfosHolder>();
		for(int i=0;i<jArray.length();i++){
			JSONObject jObjectI = jArray.getJSONObject(i);
			InfosHolder infosHolder = gson.fromJson(jObjectI.toString(), InfosHolder.class);
			infosHolders.add(infosHolder);
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void setMain(){
		boardBg = (ImageView) rl.findViewById(R.id.boardBg);
		mainImage =(ImageView) rl.findViewById(R.id.mainImage);
		RelativeLayout.LayoutParams rlParams =(LayoutParams) mainImage.getLayoutParams();
		rlParams.width=(int) (dm.widthPixels*9/10-3*dm.density);
		rlParams.height=(int)(dm.heightPixels*9/10-dm.heightPixels*9/10*2/20);
		rlParams.topMargin=(int) (dm.heightPixels*1/10/2+3*dm.density);
		mainImage.setLayoutParams(rlParams);
		mainImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try{
				Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_action)+infosHolders.get(noticeIndex-1).desc, Toast.LENGTH_SHORT).show();
				Activity a = (Activity) mContext;
				Intent intent = new Intent();
				String url="";
				String currentAction = infosHolders.get(noticeIndex-1).action;
				if(currentAction==null||currentAction.equals("")){
					url="https://www.facebook.com";
				}else{
					url=currentAction;
				}
				intent.putExtra("url", url);
				intent.setClass(a, ShinBoardActivity.class);
				intent.putExtra("type", "4");
				intent.putExtra("url", url);
				a.startActivity(intent);
				a.finish();
				}catch(Exception e){
					Log.e(SuspensionButton.TAG, "NoticeWindowGener mainImageOnClick");
					Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
					((Activity)mContext).finish();
				}
			}
			
		});
		mainImage.setBackgroundResource(R.drawable.notice_basicimg);
		bound = (ImageView) rl.findViewById(R.id.bound);
		RelativeLayout.LayoutParams rlParams1 = (LayoutParams) bound.getLayoutParams();
		rlParams1.width=dm.widthPixels*9/10;
		rlParams1.height=dm.heightPixels*9/10*18/20;
		rlParams1.topMargin=dm.heightPixels*1/10/2;
		bound.setLayoutParams(rlParams1);
		bound.setBackgroundResource(R.drawable.noticewindow_board_1_portrait);
		bound1 =(ImageView) rl.findViewById(R.id.bound1);
		RelativeLayout.LayoutParams rlParams2 = (LayoutParams) bound1.getLayoutParams();
		rlParams2.width=dm.widthPixels*9/10*8/10;
		rlParams2.height=dm.heightPixels*9/10*2/20;
		rlParams2.leftMargin=dm.widthPixels*1/10/2;
		bound1.setLayoutParams(rlParams2);
		bound1.setBackgroundResource(R.drawable.noticewindow_board_2_portrait);
		bound2 =(ImageView) rl.findViewById(R.id.bound2);
		RelativeLayout.LayoutParams rlParams3 = (LayoutParams) bound2.getLayoutParams();
		rlParams3.width=dm.widthPixels*9/10*2/10;
		rlParams3.height=dm.heightPixels*9/10*2/20;
		rlParams3.leftMargin=dm.widthPixels-dm.widthPixels*9/10*2/10-dm.widthPixels*1/10/2;
		bound2.setLayoutParams(rlParams3);
		bound2.setOnClickListener(new MyCloseButtonClickListener());
		bound2.setBackgroundResource(R.drawable.noticewindow_board_close_portrait);
		getAwayImg =(ImageView) rl.findViewById(R.id.getAwayImg);
		RelativeLayout.LayoutParams rlParams4 = (LayoutParams) getAwayImg.getLayoutParams();
		rlParams4.width=dm.heightPixels*9/10*2/20/2;
		rlParams4.height=dm.heightPixels*9/10*2/20/2;
		rlParams4.topMargin=dm.heightPixels*9/10*2/20/2/2;
		rlParams4.leftMargin=dm.widthPixels*1/10/2+dm.heightPixels*9/10*2/20/2/2;
		getAwayImg.setLayoutParams(rlParams4);
		getAwayImg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String date = XpubUtils.getCurrentDay();
				XpubUtils.setValue(SuspensionButton.NOTICEWINDOWCHECKKEY, date, SuspensionButton.getInstance().getContext());
				((Activity)mContext).finish();
				return;
			}
			
		});
		getAwayImg.setBackgroundResource(R.drawable.noticewindow_getaway);
		getAwayText =(TextView) rl.findViewById(R.id.getAwayText);
		RelativeLayout.LayoutParams rlParams5 = (LayoutParams) getAwayText.getLayoutParams();
		rlParams5.topMargin=dm.heightPixels*9/10*2/20/2/2*4/3;
		rlParams5.leftMargin=(int) (5*dm.density);
		rlParams5.width=dm.widthPixels*9/10*8/10*4/3;
		rlParams5.height=dm.heightPixels*9/10*2/20/2;
		getAwayText.setLayoutParams(rlParams5);
	}
	private int noticeIndex=0;
	private void changeNotice(){
		if(infosHolders==null){
			((Activity)mContext).finish();
			return;
		}
		if(noticeIndex==infosHolders.size()){
			noticeIndex=0;
			((Activity)mContext).finish();
			return;
		}
		new MyDownloadImg(mainImage).execute(infosHolders.get(noticeIndex++).img);
	}
	private AlphaAnimation buttonClick = new AlphaAnimation(1F,0.1F);
//	private boolean loadingDirect=false;
//	ViewGroup frameLayout;
//	private void addLoading(){
//		loadingImg= new ImageView(mContext);
//		android.view.WindowManager.LayoutParams viewParams = new android.view.WindowManager.LayoutParams();
//		viewParams.x=0;
//		viewParams.y=0;
//		viewParams.width=(int) (200*dm.density);
//		viewParams.height=(int) (200*dm.density);
//		viewParams.format=PixelFormat.RGBA_8888;
//		viewParams.windowAnimations=android.R.style.Animation_Translucent;
//		viewParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//		loadingImg.setImageResource(R.drawable.notice_loading);
//		loadingImg.setVisibility(View.VISIBLE);
//		buttonClick.setDuration(1000);
//		buttonClick.setRepeatCount(20);
//		loadingImg.startAnimation(buttonClick);
//		loadingDirect=true;
//		loadingImg.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				if(loadingDirect){
//					Matrix matrix = new Matrix();
//					matrix.setRotate(180);
//					BitmapDrawable draw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.notice_loading);
//					Bitmap bitmap= draw.getBitmap();
//					bitmap=Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
//					loadingImg.setImageBitmap(bitmap);
//					loadingDirect=false;
//				}else{
//					loadingImg.setImageResource(R.drawable.notice_loading);
//					loadingDirect=true;
//				}
//			}
//			
//		});
//		try{
//			frameLayout= new FrameLayout(mContext);
//			mWindowManager.addView(frameLayout, viewParams);
//			frameLayout.addView(loadingImg);
//		}catch(Exception e){
//			Log.e(SuspensionButton.TAG, "NoticeWindowGen addLoading error");
//		}
//	}
//	private void removeLoading(){
//		if(loadingImg==null)return;
//		try{
//			mWindowManager.removeView(frameLayout);
//		}catch(Exception e){
//			Log.d(SuspensionButton.TAG, "NoticeWindowGen removeLoading error");
//		}
//	}
	private void addLoadingImage(){
		loadingImage=(ImageView) rl.findViewById(R.id.loadingImage);
		loadingImage.getLayoutParams().width=250*(int)dm.density;
		loadingImage.getLayoutParams().height=250*(int)dm.density;
		buttonClick.setDuration(1000);
		buttonClick.setRepeatCount(60);
		loadingImage.setAnimation(buttonClick);
		loadingImage.setVisibility(View.VISIBLE);
		loadingImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.loadingMsg), Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	private void removeLoadingImage(){
		loadingImage.setAnimation(null);
		loadingImage.setVisibility(View.INVISIBLE);
	}
	//Close Button ' s    on Click Listener
	private int focusCloseNum=0;
	public class MyCloseButtonClickListener implements ImageView.OnClickListener{

		@Override
		public void onClick(View v) {
			bound2.startAnimation(buttonClickT);
			if(focusCloseNum==2){
				instance.removeLoadingImage();
				((Activity)mContext).finish();
				return;
			}
			if(!loadingStatus){
				focusCloseNum=0;
				changeNotice();
			}else{
				focusCloseNum++;
				Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeWindow_loading), Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	//info pojo
	public class InfosHolder{
		public InfosHolder(){
			
		}
		String pos;
		String id;
		String name;
		String desc;
		String img;
		String action;
	}
	/**
	 * 下载网络图片工具类
	 * @author JhonSmith
	 *
	 */
	private boolean loadingStatus=false;
	public class MyDownloadImg extends AsyncTask<String,Void,Bitmap>{
		private ImageView imageView;
		public MyDownloadImg(ImageView imageView){
			this.imageView=imageView;
			instance.addLoadingImage();
			loadingStatus=true;
		}
		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap myAdnet=null;
			InputStream is;
			try {
				myAdnet=CacheImage.get(new URL(urls[0]));
				if(myAdnet!=null)return myAdnet;
				HttpURLConnection conn = (HttpURLConnection) new java.net.URL(urls[0]).openConnection();
				conn.setDoInput(true);
				conn.connect();
				is = conn.getInputStream();
				myAdnet=CacheImage.set(new URL(urls[0]), is);
			}  catch (Exception e) {
				e.printStackTrace();
			}
			
			return myAdnet;
		}
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Bitmap result) {
			BitmapDrawable bd = new BitmapDrawable(result);
			BitmapDrawable bgBd = new BitmapDrawable(result);
			imageView.setBackgroundDrawable(bd);
			bgBd.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
			instance.boardBg.setBackgroundDrawable(bgBd);
			instance.removeLoadingImage();
			loadingStatus=false;
			super.onPostExecute(result);
		}
		
	}
}
