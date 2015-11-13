package com.elex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.elex.suspension.R;
import com.elex.util.CacheImage;
import com.elex.util.FBUtil;
import com.elex.util.Globals;
import com.elex.util.MyFBUtil;
import com.elex.util.XpubUtils;
import com.facebook.share.widget.LikeView;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class AwardGener {
	private static AwardGener instance;
	private Context mContext;
	private DisplayMetrics dm;
	private WindowManager mWindowManager;
	private LayoutInflater inflater;
	private Handler mHandler;
	private RelativeLayout rl;
	private String awardProcess;
	private Button aboveButton;
//	private ImageView bannerL;
//	private ImageView bannerR;
	private GridView noDrawGrid;
	private GridView drawedGrid;
	private ViewPager vp;
	private TextView viewPagerSelectorL;
	private TextView viewPagerSelectorR;
	private ImageView slideSign;
	private ImageView loadingImg;
	private String[] finishCheckes;
	private String[] drawCheckes;
	private PopupWindow pw;
	private View pwView;
	private Button pwClose;
	private LikeView fbLikeButton;
	private ViewPager bannerVp;
	private ImageView loadingImage;
	private View contentView;
	public static AwardGener getInstance(){
		return instance;
	}
	public View getView(){
		return contentView;
	}
	public AwardGener(Context context){
		this.mContext=context;
		dm=mContext.getResources().getDisplayMetrics();
		mWindowManager=(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.quest_main_portrait, null);
		rl=(RelativeLayout) contentView.findViewById(R.id.rlQuest);
		mHandler=new Handler();
	}
	public static void init(Context context,String awardProcess){
		instance = new AwardGener(context);
		instance.awardProcess=awardProcess;
		instance.setPortrait();
	}
	private void setPortrait(){
		try{
		setMain();
//		setBanner(SuspensionButton.myBusBuBu);
		setBannerLoop(SuspensionButton.myBusBuBu);
		setGridView(SuspensionButton.myBusBuBu,awardProcess);
		setPopWindow();
		setViewPager();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "AwardGener error");
			Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
			((Activity)mContext).finish();
		}
	}
	private void setMain(){
		aboveButton = (Button) rl.findViewById(R.id.aboveButton);
		RelativeLayout.LayoutParams rlParams = (LayoutParams) aboveButton.getLayoutParams();
		rlParams.width=dm.widthPixels;
		rlParams.height=dm.heightPixels/10;
		aboveButton.getBackground().setColorFilter(0xFF73A1FF,PorterDuff.Mode.MULTIPLY);
		aboveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Activity a = (Activity) mContext;
				a.finish();
			}
			
		});
		
	}
	public class BannerHolder{
		public BannerHolder(){
			
		}
		String id;
		String pos;
		String name;
		String desc;
		String img;
		String action;
	}
//	private void setBanner(String result){
//		if(result==null||result.equals("")){
//			return;
//		}
//		List<BannerHolder> bannerHolderList=new ArrayList<BannerHolder>();
//		try {
//			JSONObject jObject = new JSONObject(result);
//			JSONArray jArray = jObject.getJSONArray("banners");
//			Gson gson = new Gson();
//			for(int i = 0 ;i <jArray.length();i++){
//				BannerHolder bannerHolder = gson.fromJson(jArray.get(i).toString(), BannerHolder.class);
//				bannerHolderList.add(bannerHolder);
//			}
//		} catch (Exception e) {
//			Log.e(SuspensionButton.TAG, "AwardGener setBanner JSON ERROR");
//			e.printStackTrace();
//		}
//		if(bannerHolderList.size()==0){
//			return;
//		}
//		BannerHolder bannerHolderI=null;
//		BannerHolder bannerHolderII=null;
//		for(BannerHolder bannerHolder:bannerHolderList){
//			if(bannerHolderI==null){
//				bannerHolderI=bannerHolder;
//			}else if(bannerHolderII==null){
//				bannerHolderII=bannerHolder;
//			}
//		}
//		if(bannerHolderII==null){
//			bannerHolderII=bannerHolderI;
//		}
//		BannerHolder bannerHolderL=null;
//		BannerHolder bannerHolderR=null;
//		try{
//			if(Integer.parseInt(bannerHolderI.pos)<Integer.parseInt(bannerHolderII.pos)){
//				bannerHolderL=bannerHolderI;
//				bannerHolderR=bannerHolderII;
//			}else{
//				bannerHolderL=bannerHolderII;
//				bannerHolderR=bannerHolderI;
//			}
//		}catch(Exception e){
//			bannerHolderL=bannerHolderI;
//			bannerHolderR=bannerHolderII;
//		}
//		bannerL=(ImageView) rl.findViewById(R.id.bannerL);
//		RelativeLayout.LayoutParams rlParams = (LayoutParams) bannerL.getLayoutParams();
//		rlParams.width=dm.widthPixels/2;
//		rlParams.height=dm.heightPixels/10;
//		new DownloadImgTask(bannerL).execute(bannerHolderL.img);
//		bannerL.setLayoutParams(rlParams);
//		bannerL.setOnClickListener(new MyBannerOnClickListener(bannerHolderL));
//		bannerR=(ImageView)rl.findViewById(R.id.bannerR);
//		RelativeLayout.LayoutParams rlParams1= (LayoutParams)bannerR.getLayoutParams();
//		rlParams1.width=dm.widthPixels/2;
//		rlParams1.height=dm.heightPixels/10;
//		new DownloadImgTask(bannerR).execute(bannerHolderR.img);
//		bannerR.setLayoutParams(rlParams1);
//		bannerR.setOnClickListener(new MyBannerOnClickListener(bannerHolderR));
//	}
	private int mBannerSpotSign=0;
	private void setBannerLoop(String result){
		if(result==null||result.equals("")){
			return;
		}
		List<BannerHolder> bannerHolderList=new ArrayList<BannerHolder>();
		try {
			JSONObject jObject = new JSONObject(result);
			JSONArray jArray = jObject.getJSONArray("banners");
			Gson gson = new Gson();
			for(int i = 0 ;i <jArray.length();i++){
				BannerHolder bannerHolder = gson.fromJson(jArray.get(i).toString(), BannerHolder.class);
				bannerHolderList.add(bannerHolder);
			}
		} catch (Exception e) {
			Log.e(SuspensionButton.TAG, "AwardGener setBanner JSON ERROR");
			e.printStackTrace();
		}
		if(bannerHolderList.size()==0){
			return;
		}
		bannerVp=(ViewPager) rl.findViewById(R.id.bannerLoop);
		RelativeLayout.LayoutParams rlParams=(LayoutParams) bannerVp.getLayoutParams();
		rlParams.width=dm.widthPixels;
		rlParams.height=dm.heightPixels/10;
		bannerVp.setLayoutParams(rlParams);
		List<View> viewList = new ArrayList<View>();
		for(BannerHolder bannerHolder:bannerHolderList){
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new LayoutParams(dm.widthPixels,dm.heightPixels/10));
			imageView.setOnClickListener(new MyBannerOnClickListener(bannerHolder));
			new DownloadImgTask(imageView).execute(bannerHolder.img);
			viewList.add(imageView);
		}
		bannerVp.setAdapter(new MyPagerAdapter(viewList));
		final int bannerSize=bannerHolderList.size();
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				if(bannerSize<1){
					return;
				}
				if(mBannerSpotSign<bannerSize-1){
					mBannerSpotSign++;
					((Activity)mContext).runOnUiThread(new Runnable(){

						@Override
						public void run() {
							bannerVp.setCurrentItem(mBannerSpotSign, true);
						}
						
					});
				}else if(mBannerSpotSign==bannerSize-1){
					mBannerSpotSign=0;
					((Activity)mContext).runOnUiThread(new Runnable(){

						@Override
						public void run() {
							bannerVp.setCurrentItem(mBannerSpotSign, true);
						}
						
					});
				}
				mHandler.postDelayed(this, 6000);
			}
			
		});
		thread.start();
		bannerVp.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				
			}
			
		});
		
	}

	private AlphaAnimation buttonClick = new AlphaAnimation(1F,0.1F);
	private AlphaAnimation buttonClickT = new AlphaAnimation(1F,0.3F);
//	private boolean loadingDirect=false;
//	ViewGroup frameLayout;
//	private boolean loadingAddCheck=false;
//	private void addLoading(){
//		Thread thread = new Thread(new Runnable(){
//
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				((Activity) mContext).runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						removeLoading();
//					}
//				});
//			}
//		});
//		thread.start();
//		loadingImg= new ImageView(mContext);
//		android.view.WindowManager.LayoutParams viewParams = new android.view.WindowManager.LayoutParams();
//		viewParams.x=0;
//		viewParams.y=0;
//		viewParams.width=(int) (300*dm.density);
//		viewParams.height=(int) (300*dm.density);
//		viewParams.format=PixelFormat.RGBA_8888;
//		viewParams.windowAnimations=android.R.style.Animation_Translucent;
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
//			loadingAddCheck=true;
//		}catch(Exception e){
//			Log.e(SuspensionButton.TAG, "AwardGen addLoading error");
//		}
//	}
//	private void removeLoading(){
//		if(frameLayout==null)return;
//		if(!loadingAddCheck)return;
//		try{
//			mWindowManager.removeView(frameLayout);
//			loadingAddCheck=false;
//		}catch(Exception e){
//			e.printStackTrace();
//			Log.d(SuspensionButton.TAG, "AwardGen removeLoading error");
//		}
//	}
	//new loading
	private void addLoadingImage(){
		loadingImage=(ImageView) rl.findViewById(R.id.loadingImage);
		Bitmap bmOriginal=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notice_loading);
		Bitmap bm=Bitmap.createScaledBitmap(bmOriginal, 250*(int)dm.density, 250*(int)dm.density, false);
		loadingImage.setScaleType(ScaleType.CENTER_INSIDE);
		loadingImage.setImageBitmap(bm);
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
	/**
	 * 领取的线程
	 */
	public class DoAwardRunnable implements Runnable{
		Handler handler;
		Message msg = new Message();
		public DoAwardRunnable(Handler handler){
			this.handler=handler;
		}
		@Override
		public void run() {
			HttpPost httpPost = new HttpPost(SuspensionButton.mPostUrl);
			List<NameValuePair> params =  new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mod","event"));
			params.add(new BasicNameValuePair("act","award"));
			params.add(new BasicNameValuePair("a",SuspensionButton.mAppId));
			params.add(new BasicNameValuePair("c",SuspensionButton.mUid));
			params.add(new BasicNameValuePair("d",SuspensionButton.mRoleId));
			params.add(new BasicNameValuePair("taskid",taskId));
			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(params,"utf-8");
				httpPost.setEntity(httpEntity);
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 8000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 8000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient httpClient = new DefaultHttpClient(httpParameters);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				InputStream  is = httpResponse.getEntity().getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
				StringBuffer sb = new StringBuffer();
				String line ="";
				while((line=br.readLine())!=null){
					sb.append(line);
				}
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					awardBundle=dealAwardResult(sb.toString());
				}else{
					awardBundle=dealAwardResult("false");
				}
				msg.obj="T";
				handler.sendMessage(msg);
			} catch (Exception e) {
				awardBundle=dealAwardResult("false");
				msg.obj="T";
				handler.sendMessage(msg);
				e.printStackTrace();
				Log.e("NoticeBoardERROR", "time out");
			}
		}
		
	}
	public boolean awardResponseCheck=false;
	private Bundle awardBundle;
	private String taskId="";
	private Bundle doAward(String taskId,Handler handler){
		this.taskId=taskId;
		Thread awardThread = new Thread(new DoAwardRunnable(handler));
		awardThread.start();
		return awardBundle;
	}
	@SuppressLint("DefaultLocale")
	private Bundle dealAwardResult(String awardResult){
		if(awardResult==null||awardResult.equals("")){
			awardResponseCheck=false;
			return null;
		}
		if(awardResult.equals("false")){
			awardResponseCheck=false;
			return null;
		}
		awardResponseCheck=true;
		Bundle bundle= new Bundle();
		try {
			JSONObject jObject = new JSONObject(awardResult);
			bundle.putString("status", jObject.optString("status"));
			String language=mContext.getResources().getConfiguration().locale.getLanguage().toLowerCase();
			String country=mContext.getResources().getConfiguration().locale.getCountry().toLowerCase();
			if(language.equals("en")){
				bundle.putString("msg", jObject.optString("msg1"));
			}else if(language.equals("zh")){
				if(country.equals("cn")){
					bundle.putString("msg", jObject.optString("msg"));
				}else{
					bundle.putString("msg", jObject.optString("msg"));
				}
			}else{
				bundle.putString("msg", jObject.optString("msg1"));
			}
		} catch (JSONException e) {
			Log.e(SuspensionButton.TAG, "AwardGener dealAwardResult JSON ERROR");
			e.printStackTrace();
		}
		return bundle;
	}
	public class MyBannerOnClickListener implements ImageView.OnClickListener{
		private BannerHolder bannerHolder;
		public MyBannerOnClickListener(BannerHolder bannerHolder){
			this.bannerHolder=bannerHolder;
		}
		@Override
		public void onClick(View v) {
			try{
			Toast.makeText(mContext,
					mContext.getResources().getString(R.string.noticeboard_action) + bannerHolder.desc,
					Toast.LENGTH_SHORT).show();
			String url="";
			if(bannerHolder.action==null||bannerHolder.action.equals("")){
				url="http://www.facebook.com";
			}else{url=bannerHolder.action;}
			ShinBoardGener.getInstance().mUrl=url;
			ShinBoardGener.getInstance().showBoard("4");
//			Activity a = (Activity) mContext;
//			Intent intent = new Intent();
//			intent.putExtra("url", url);
//			a.setResult(SuspensionButton.RESULTCODE_GOTO_WEBVIEW, intent);
//			a.finish();
			}catch(Exception e){
				e.printStackTrace();
				Log.e(SuspensionButton.TAG, "AwardGener BannerOnClick ERROR");
				Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	public class TaskHolder{
		public TaskHolder(){
			
		}
		String name;
		String pos;
		String taskid;
		String taskdesc;
		String img;
		String action;
		String goal;
	}
	public class TaskProcessHolder{
		public TaskProcessHolder(){
			
		}
		String taskid;
		String award;
		String goal;
		String progress;
	}
	public class TaskExHolder{
		public TaskExHolder(TaskProcessHolder taskProcessHolder,TaskHolder taskHolder){
			this.taskProcessHolder=taskProcessHolder;
			this.taskHolder=taskHolder;
		}
		TaskProcessHolder taskProcessHolder;
		TaskHolder taskHolder;
	}
	private void setGridView(String result,String  awardResult){
		if (result == null || result.equals("")) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.noticeboard_error), Toast.LENGTH_SHORT)
					.show();
			MobclickAgent.onEvent(SuspensionButton.getInstance()
					.getGameActivity(), "award_board_failed");
			((Activity) mContext).finish();
			return;
		}
		List<TaskHolder> taskHolderList = new ArrayList<TaskHolder>();
		try {
			Gson gson = new Gson();
			JSONObject jObject= new JSONObject(result);
			JSONArray jArray=jObject.getJSONArray("tasks");
			for(int i =0 ; i<jArray.length() ;i++){
				TaskHolder taskHolder = gson.fromJson(jArray.getJSONObject(i).toString(), TaskHolder.class);
				taskHolderList.add(taskHolder);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "AwardGener setGridView JSONERROR");
		}
		if(taskHolderList.size()==0){
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.noticeboard_error), Toast.LENGTH_SHORT)
					.show();
			((Activity) mContext).finish();
			return;
		}
		List<TaskProcessHolder> taskProcessHolderList = new ArrayList<TaskProcessHolder>();
		if(awardResult!=null&&!awardResult.equals("")){
			try {
				JSONArray jArray = new JSONArray(awardResult);
				Gson gson = new Gson();
				for(int i = 0 ; i <jArray.length();i++){
					TaskProcessHolder taskProcessHolder = gson.fromJson(jArray.getJSONObject(i).toString(), TaskProcessHolder.class);
					taskProcessHolderList.add(taskProcessHolder);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(SuspensionButton.TAG, "AwardGener setGridView JSON PROCESS ERROR");
			}
		}
		if(taskProcessHolderList.size()!=0){
			List<TaskExHolder> taskExHolderList= new ArrayList<TaskExHolder>();
			List<TaskHolder> taskDrawedHolderList=new ArrayList<TaskHolder>();
			for(TaskHolder taskHolder:taskHolderList){
				boolean matchSign=false;
				for(TaskProcessHolder taskProcessHolder:taskProcessHolderList){
					if (taskProcessHolder.taskid != null
							&& !taskProcessHolder.taskid.equals("")
							&& taskHolder.taskid != null
							&& !taskHolder.taskid.equals("")
							&& taskHolder.taskid.equals(taskProcessHolder.taskid)) {
						if(taskProcessHolder.award!=null&&taskProcessHolder.award.equals("2")){
							matchSign=true;
							taskDrawedHolderList.add(taskHolder);
							break;
						}else{
							matchSign=true;
							taskExHolderList.add(new TaskExHolder(taskProcessHolder,taskHolder));
							break;
						}
					}
				}
				if(!matchSign){
					taskExHolderList.add(new TaskExHolder(null,taskHolder));
				}
			}
			finishCheckes=new String[taskExHolderList.size()];
			drawCheckes=new String[taskExHolderList.size()];
			noDrawGrid =new GridView(mContext);
			drawedGrid=new GridView(mContext);
			noDrawGrid.setAdapter(new MyAdapter(taskExHolderList,"EX"));
			noDrawGrid.setVerticalSpacing((int)(10*dm.density));
			noDrawGrid.setNumColumns(1);
			drawedGrid=new GridView(mContext);
			drawedGrid.setAdapter(new MyAdapter(taskDrawedHolderList));
			drawedGrid.setVerticalSpacing((int)(10*dm.density));
			drawedGrid.setNumColumns(1);
		}else{
			List<TaskExHolder> taskExHolderList= new ArrayList<TaskExHolder>();
			for(TaskHolder taskHolder:taskHolderList){
				taskExHolderList.add(new TaskExHolder(null,taskHolder));
			}
			finishCheckes=new String[taskExHolderList.size()];
			drawCheckes=new String[taskExHolderList.size()];
			noDrawGrid =new GridView(mContext);
			drawedGrid=new GridView(mContext);
			noDrawGrid.setAdapter(new MyAdapter(taskExHolderList,"EX"));
			noDrawGrid.setVerticalSpacing((int)(10*dm.density));
			noDrawGrid.setNumColumns(1);
			drawedGrid=new GridView(mContext);
			drawedGrid.setVerticalSpacing((int)(10*dm.density));
			drawedGrid.setNumColumns(1);
		}
		
	}
	private Handler likeHandler =null;
	private void doFbLike(Handler handler){
		likeHandler = handler;
		List<NameValuePair> params =  new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mod","fb"));
		params.add(new BasicNameValuePair("act","like"));
		params.add(new BasicNameValuePair("a",SuspensionButton.mAppId));
		params.add(new BasicNameValuePair("b",SuspensionButton.mFbId));
		params.add(new BasicNameValuePair("c",SuspensionButton.mUid));
		params.add(new BasicNameValuePair("d",SuspensionButton.mRoleId));
		params.add(new BasicNameValuePair("i","1"));
		Globals.getInstance().sendRequest(params, new OnFbLike() , new OnFbLikeError() );
	}
	private class OnFbLike implements CallBackFunciton{

		@Override
		public void call(String json) {
			Message msg = new Message();
			msg.obj="T";
			likeHandler.sendMessage(msg);
		}
		
	}
	private class OnFbLikeError implements CallBackFunciton{

		@Override
		public void call(String json) {
			Message msg = new Message();
			msg.obj="F";
			likeHandler.sendMessage(msg);
		}
		
	}
	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	private void setPopWindow(){
		pw=new PopupWindow(mContext);
		pwView=inflater.inflate(R.layout.myfacebooklike_view_portrait, null, false);
		pwClose= (Button) pwView.findViewById(R.id.closeButton);
		pwClose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				pw.dismiss();
			}
		});
		fbLikeButton =(LikeView) pwView.findViewById(R.id.fbLikeButton);
		if(SuspensionButton.getInstance().fansWallParams == null) return;
		fbLikeButton.setObjectIdAndType(SuspensionButton.getInstance().fansWallParams.getUrl(),LikeView.ObjectType.PAGE);
		fbLikeButton.setLikeViewStyle(LikeView.Style.STANDARD);
		pw.setContentView(pwView);
		pw.setWidth(dm.widthPixels*2/3);
		pw.setHeight(dm.widthPixels*2/3);
		GradientDrawable gd = new GradientDrawable();
		gd.setShape(GradientDrawable.RECTANGLE);
		gd.setCornerRadius(30);
		gd.setColor(0xFFFAFAD2);
		pw.setBackgroundDrawable(gd);
		pw.setFocusable(true);
	}
	private void doCpbClick(){
		List<NameValuePair> params =  new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mod","cpb"));
		params.add(new BasicNameValuePair("act","click"));
		params.add(new BasicNameValuePair("a",SuspensionButton.mAppId));
		params.add(new BasicNameValuePair("b",SuspensionButton.mFbId));
		params.add(new BasicNameValuePair("c",SuspensionButton.mUid));
		params.add(new BasicNameValuePair("deviceid",XpubUtils.getDeiviceId(mContext)));
		Globals.getInstance().sendRequest(params, null, null);
	}
	private void setViewPager(){
		viewPagerSelectorL=(TextView) rl.findViewById(R.id.viewPagerSelectorL);
		RelativeLayout.LayoutParams rlParams =(LayoutParams)viewPagerSelectorL.getLayoutParams();
		rlParams.width=dm.widthPixels/2;
		rlParams.height=dm.heightPixels/10;
		viewPagerSelectorL.setLayoutParams(rlParams);
		viewPagerSelectorL.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try{
					vp.setCurrentItem(0);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(SuspensionButton.TAG, "AwardGener ViewPager OnClick");
				}
			}
			
		});
		viewPagerSelectorR=(TextView)rl.findViewById(R.id.viewPagerSelectorR);
		RelativeLayout.LayoutParams rlParams1=(LayoutParams) viewPagerSelectorR.getLayoutParams();
		rlParams1.width=dm.widthPixels/2;
		rlParams1.height=dm.heightPixels/10;
		viewPagerSelectorR.setLayoutParams(rlParams1);
		viewPagerSelectorR.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try{
					vp.setCurrentItem(1);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(SuspensionButton.TAG, "AwardGener ViewPager OnClick");
				}
			}
			
		});
		slideSign = (ImageView) rl.findViewById(R.id.slideSign);
		RelativeLayout.LayoutParams rlParams2 = (LayoutParams) slideSign.getLayoutParams();
		rlParams2.width=dm.widthPixels/10;
		rlParams2.height=(int)(3*dm.density);
		rlParams2.leftMargin=dm.widthPixels/4-dm.widthPixels/10/2;
		slideSign.setLayoutParams(rlParams2);
		vp=(ViewPager) rl.findViewById(R.id.pagerContent);
		RelativeLayout.LayoutParams rlParams3 = (LayoutParams) vp.getLayoutParams();
		rlParams3.width=dm.widthPixels;
		rlParams3.height=dm.heightPixels*14/20-60*(int)dm.density;
		vp.setLayoutParams(rlParams3);
		List<View>viewList = new ArrayList<View>();
		viewList.add(noDrawGrid);
		viewList.add(drawedGrid);
		vp.setAdapter(new MyPagerAdapter(viewList));
		vp.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				if(slideSign!=null){
					TranslateAnimation trAnime;
					switch (arg0) {
					case 0:
						trAnime = new TranslateAnimation(dm.widthPixels / 2, 0,
								0, 0);
						trAnime.setFillAfter(true);
						trAnime.setDuration(500);
						slideSign.setAnimation(trAnime);
						break;
					case 1:
						trAnime = new TranslateAnimation(0, dm.widthPixels / 2,
								0, 0);
						trAnime.setFillAfter(true);
						trAnime.setDuration(500);
						slideSign.setAnimation(trAnime);
						break;
					}
				}
			}
			
		});
		
	}
	public class MyPagerAdapter extends PagerAdapter{
		private List<View> viewList;
		
		public MyPagerAdapter (List<View> viewList){
			this.viewList=viewList;
		}
		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView(viewList.get(position));
		}
		@Override
		public Object instantiateItem(View container, int position) {
			try{
			((ViewPager)container).addView(viewList.get(position));
			return viewList.get(position);
			}catch(Exception e){
				return null;
			}
		}
		
		
		
	}
	
	public class MyAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private List<TaskExHolder> taskExHolderList=null;
		private List<TaskHolder> taskDrawedHolderList=null;
		private int ListCount=0;
		public MyAdapter(List<TaskExHolder> taskExHolderList,String nanimona){
			this.taskExHolderList=taskExHolderList;
			ListCount=this.taskExHolderList.size();
			inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public MyAdapter(List<TaskHolder>taskDrawedHolderList){
			this.taskDrawedHolderList=taskDrawedHolderList;
			ListCount=this.taskDrawedHolderList.size();
			inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return ListCount;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		public class Holder{
			ImageView img;
			ImageView img1;
//			ImageView img2;
			TextView drawButton;
//			ImageView img3;
			TextView gotoButton;
//			ImageView img4;
			TextView achieveButton;
			TextView tv;
			TextView tv1;
		}
		public class GetViewOnTouchListener implements OnTouchListener{
			private Holder holder;
			private int position;
			public GetViewOnTouchListener (Holder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@SuppressLint({ "ClickableViewAccessibility", "HandlerLeak" })
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					return true;
				}
				if(event.getAction()==MotionEvent.ACTION_UP){
					holder.drawButton.startAnimation(buttonClickT);
					addLoadingImage();
					Handler handler = new Handler(){

						@Override
						public void handleMessage(Message msg) {
							if(msg.obj.toString().equals("T")){
								removeLoadingImage();
								if (awardBundle == null||awardBundle.getString("status")==null) {
									Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error),
											Toast.LENGTH_SHORT).show();
									return;
								}
								if (awardBundle.getString("status").equals("1")) {
									Toast.makeText(mContext, awardBundle.getString("msg"),
											Toast.LENGTH_SHORT).show();
									drawCheckes[position]="1";
									setDrawed(holder,position);
								} else if (awardBundle.getString("status").equals("0")) {
									Toast.makeText(mContext, awardBundle.getString("msg"),
											Toast.LENGTH_SHORT).show();
								}
							}
						}
						
					};
					doAward(taskExHolderList.get(position).taskHolder.taskid,handler);
				}
				return true;
			}
			
		}
		private void setNotDo(Holder holder,int position){
			holder.img
					.setBackgroundResource(R.drawable.noticeboard_award_maincando);
			holder.drawButton.setVisibility(View.GONE);
			holder.gotoButton.setVisibility(View.VISIBLE);
			holder.achieveButton.setVisibility(View.GONE);
			holder.tv
					.setText(taskExHolderList.get(position).taskHolder.taskdesc);
			holder.tv1.setText(mContext.getResources().getString(
					R.string.noticeboard_questNoCompelete_1)
					+ taskExHolderList.get(position).taskHolder.goal
					+ mContext.getResources().getString(
							R.string.noticeboard_questNoCompelete_2));
			holder.tv1.setTextColor(0xFFFE394B);
		}
		private void setNotComplete(Holder holder,int position){
			holder.img
					.setBackgroundResource(R.drawable.noticeboard_award_maincando);
			holder.drawButton.setVisibility(View.GONE);
			holder.gotoButton.setVisibility(View.VISIBLE);
			holder.achieveButton.setVisibility(View.GONE);
			holder.tv
					.setText(taskExHolderList.get(position).taskHolder.taskdesc);
			holder.tv1
					.setText(mContext.getResources().getString(
							R.string.noticeboard_questNoCompelete_1)
							+ (Integer.parseInt(taskExHolderList.get(position).taskProcessHolder.goal) - Integer
									.parseInt(taskExHolderList.get(position).taskProcessHolder.progress))
							+ mContext.getResources().getString(
									R.string.noticeboard_questNoCompelete_2));
			holder.tv1.setTextColor(0xFFFE394B);
		}
		private void setNotDraw(Holder holder,int position){
			holder.img
					.setBackgroundResource(R.drawable.noticeboard_award_maincandraw);
			holder.drawButton.setVisibility(View.VISIBLE);
			holder.gotoButton.setVisibility(View.GONE);
			holder.achieveButton.setVisibility(View.GONE);
			holder.tv
					.setText(taskExHolderList.get(position).taskHolder.taskdesc);
			holder.tv1.setText(mContext.getResources().getString(
					R.string.noticeboard_questCanDraw));
			holder.tv1.setTextColor(0xFF63D6B7);
		}
		private void setDrawed(Holder holder,int position){
			holder.img.setBackgroundResource(R.drawable.noticeboard_award_maindid);
			holder.drawButton.setVisibility(View.GONE);
			holder.gotoButton.setVisibility(View.GONE);
			holder.achieveButton.setVisibility(View.VISIBLE);
			holder.tv.setText(taskExHolderList.get(position).taskHolder.taskdesc);
			holder.tv1.setText(mContext.getResources().getString(R.string.noticeboard_questCompelete));
			holder.tv1.setTextColor(0xFF696969);
		}
		public class MyShareListener implements OnClickListener{
			private Holder holder;
			private int position;
			public MyShareListener(Holder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@Override
			public void onClick(View v) {
				try{
					holder.gotoButton.startAnimation(buttonClickT);
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.gotoShare),
							Toast.LENGTH_SHORT).show();
					ShinBoardGener.getInstance().showBoard("3");
					return;
					}catch(Exception e){
						e.printStackTrace();
						Log.e(SuspensionButton.TAG, "AwardGener FBShare ONClick ERROR:"+e.getMessage());
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.noticeboard_error),
								Toast.LENGTH_SHORT).show();
					}
			}
			
		}
		public class MyFbInviteListener implements OnClickListener{
			private Holder holder;
			private int position;
			public MyFbInviteListener(Holder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@Override
			public void onClick(View v) {
				try{
				holder.gotoButton.startAnimation(buttonClickT);
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.gotoInvite),
						Toast.LENGTH_SHORT).show();
				ShinBoardGener.getInstance().showBoard("1");
				return;
				}catch(Exception e){
					e.printStackTrace();
					Log.e(SuspensionButton.TAG, "AwardGener FBInvite ONClick ERROR");
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_error),
							Toast.LENGTH_SHORT).show();
				}
			}
			
		}
		public class MyFbLikeListener implements OnClickListener{
			private Holder holder;
			private int position;
			public MyFbLikeListener(Holder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public void onClick(View v) {
				try{
				holder.gotoButton.startAnimation(buttonClickT);
				if(!MyFBUtil.isLogin()){
					Toast.makeText(mContext, mContext.getResources().getString(R.string.facebooknologin), Toast.LENGTH_SHORT).show();
					MyFBUtil.Login();
					return;
				}
				if(SuspensionButton.getInstance().fansWallParams == null) return;
				fbLikeButton.setObjectIdAndType(SuspensionButton.getInstance().fansWallParams.getUrl(),LikeView.ObjectType.PAGE);
				fbLikeButton.setLikeViewStyle(LikeView.Style.STANDARD);
				pw.showAtLocation(rl, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
				fbLikeButton.likeButton.setOnTouchListener(new OnTouchListener(){

					@SuppressLint({ "ClickableViewAccessibility", "HandlerLeak" })
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						try{
						if(event.getAction()==MotionEvent.ACTION_UP){
							Handler handler = new Handler(){

								@Override
								public void handleMessage(Message msg) {
									super.handleMessage(msg);
									if(msg.obj.equals("T")){
										finishCheckes[position]="1";
										setNotDraw(holder,position);
										
									}else if(msg.obj.equals("F")){
										Toast.makeText(mContext,mContext.getResources().getString(R.string.fblike_error) , Toast.LENGTH_SHORT).show();
									}
								}
								
							};
							doFbLike(handler);
						}}catch(Exception e){
							e.printStackTrace();
							Log.e(SuspensionButton.TAG, "AwardGener FBLIKE ONTOUCH ERROR");
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.noticeboard_error),
									Toast.LENGTH_SHORT).show();
						}
						return false;
					}
					
				});}catch(Exception e){
					e.printStackTrace();
					Log.e(SuspensionButton.TAG, "AwardGener FBLIKE ONClick ERROR");
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_error),
							Toast.LENGTH_SHORT).show();
				}
			}
			
		}
		public class MyDownLoadListener implements OnClickListener{
			private Holder holder;
			private int position;
			public MyDownLoadListener(Holder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@Override
			public void onClick(View v) {
				try{
				holder.gotoButton.startAnimation(buttonClickT);
				doCpbClick();
//				Activity a = (Activity) mContext;
//				Intent intent = new Intent();
				String url = "";
				if(taskExHolderList.get(position).taskHolder.action==null||taskExHolderList.get(position).taskHolder.action.equals("")){
					url="www.facebook.com";
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_action)
									+ url,
							Toast.LENGTH_SHORT).show();
				}else{
					url = taskExHolderList.get(position).taskHolder.action;
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_action)
									+ taskExHolderList.get(position).taskHolder.action,
							Toast.LENGTH_SHORT).show();
				}
//				intent.putExtra("url",url);
//				a.setResult(SuspensionButton.RESULTCODE_GOTO_WEBVIEW, intent);
//				a.finish();
				ShinBoardGener.getInstance().mUrl=url;
				ShinBoardGener.getInstance().showBoard("4");
				return;
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(SuspensionButton.TAG, "AwardGener GotoClick Error");
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_error),
							Toast.LENGTH_SHORT).show();
				}
			}
			
		}
		public class MyGotoListener implements OnClickListener{
			private Holder holder;
			private int position;
			public MyGotoListener(Holder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@Override
			public void onClick(View v) {
				try{
				holder.gotoButton.startAnimation(buttonClickT);
//				Activity a = (Activity) mContext;
//				Intent intent = new Intent();
				String url = "";
				if(taskExHolderList.get(position).taskHolder.action==null||taskExHolderList.get(position).taskHolder.action.equals("")){
					url="www.facebook.com";
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_action)
									+ url,
							Toast.LENGTH_SHORT).show();
				}else{
					url = taskExHolderList.get(position).taskHolder.action;
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_action)
									+ taskExHolderList.get(position).taskHolder.action,
							Toast.LENGTH_SHORT).show();
				}
//				intent.putExtra("url",url);
//				a.setResult(SuspensionButton.RESULTCODE_GOTO_WEBVIEW, intent);
//				a.finish();
				ShinBoardGener.getInstance().mUrl=url;
				ShinBoardGener.getInstance().showBoard("4");
				return;
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(SuspensionButton.TAG, "AwardGener GotoClick Error");
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.noticeboard_error),
							Toast.LENGTH_SHORT).show();
				}
			}
			
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try{
			if(taskExHolderList!=null){
				Holder holder=null;
				if(convertView!=null){
					holder =(Holder) convertView.getTag();
				}else{
					convertView=inflater.inflate(R.layout.quest_grid_portrait_gener, parent, false);
					holder=new Holder();
					holder.img=(ImageView) convertView.findViewById(R.id.imageView);
					holder.img1=(ImageView)convertView.findViewById(R.id.imageView1);
					holder.drawButton=(TextView)convertView.findViewById(R.id.drawbutton);
					holder.gotoButton=(TextView)convertView.findViewById(R.id.gotobutton);
					holder.achieveButton=(TextView)convertView.findViewById(R.id.achievebutton);
					holder.tv=(TextView)convertView.findViewById(R.id.textView);
					holder.tv1=(TextView)convertView.findViewById(R.id.textView1);
					convertView.setTag(holder);
				}
				RelativeLayout.LayoutParams rlParams = (LayoutParams) holder.img1.getLayoutParams(); 
				rlParams.width=dm.widthPixels;
				holder.img1.setLayoutParams(rlParams);
				RelativeLayout.LayoutParams rlParams1=(LayoutParams) holder.tv.getLayoutParams();
				rlParams1.width=(int) (dm.widthPixels-200*dm.density);
				holder.tv.setLayoutParams(rlParams1);
				RelativeLayout.LayoutParams rlParams2=(LayoutParams) holder.tv1.getLayoutParams();
				rlParams2.width=(int) (dm.widthPixels-200*dm.density);
				holder.tv1.setLayoutParams(rlParams2);
				holder.drawButton.setOnTouchListener(new GetViewOnTouchListener(holder,position));
				if (taskExHolderList.get(position).taskProcessHolder == null) {
					setNotDo(holder,position);
				} else {
					if (taskExHolderList.get(position).taskProcessHolder.award
							.equals("1")) {
						setNotDraw(holder,position);
					} else {
						setNotComplete(holder,position);
					}
				}
				if(finishCheckes[position]!=null&&finishCheckes[position].equals("1")){
					setNotDraw(holder,position);
				}
				if(drawCheckes[position]!=null&&drawCheckes[position].equals("1")){
					setDrawed(holder,position);
				}
				if (taskExHolderList.get(position).taskHolder.taskid.equals("9")) {
					holder.gotoButton.setOnClickListener(new MyDownLoadListener(holder,position));
				}else if(taskExHolderList.get(position).taskHolder.action.startsWith("xpub://fblike")){
					holder.gotoButton.setOnClickListener(new MyFbLikeListener(holder,position));
				}else if (taskExHolderList.get(position).taskHolder.action.startsWith("xpub://fbinvite")) {
					holder.gotoButton.setOnClickListener(new MyFbInviteListener(holder,position));
				}else if(taskExHolderList.get(position).taskHolder.action.startsWith("xpub://share")){
					holder.gotoButton.setOnClickListener(new MyShareListener(holder,position));
				}else{
					holder.gotoButton.setOnClickListener(new MyGotoListener(holder,position));
				}
			}else if (taskDrawedHolderList!=null){
				Holder holder=null;
				if(convertView!=null){
					holder =(Holder) convertView.getTag();
				}else{
					convertView=inflater.inflate(R.layout.quest_grid_portrait_gener, parent, false);
					holder=new Holder();
					holder.img=(ImageView) convertView.findViewById(R.id.imageView);
					holder.img1=(ImageView)convertView.findViewById(R.id.imageView1);
					holder.drawButton=(TextView)convertView.findViewById(R.id.drawbutton);
					holder.gotoButton=(TextView)convertView.findViewById(R.id.gotobutton);
					holder.achieveButton=(TextView)convertView.findViewById(R.id.achievebutton);
					holder.tv=(TextView)convertView.findViewById(R.id.textView);
					holder.tv1=(TextView)convertView.findViewById(R.id.textView1);
					convertView.setTag(holder);
				}
				RelativeLayout.LayoutParams rlParams = (LayoutParams) holder.img1.getLayoutParams(); 
				rlParams.width=dm.widthPixels;
				holder.img1.setLayoutParams(rlParams);
				RelativeLayout.LayoutParams rlParams1=(LayoutParams) holder.tv.getLayoutParams();
				rlParams1.width=(int) (dm.widthPixels-200*dm.density);
				holder.tv.setLayoutParams(rlParams1);
				RelativeLayout.LayoutParams rlParams2=(LayoutParams) holder.tv1.getLayoutParams();
				rlParams2.width=(int) (dm.widthPixels-200*dm.density);
				holder.tv1.setLayoutParams(rlParams2);
				holder.img.setBackgroundResource(R.drawable.noticeboard_award_maindid);
				holder.drawButton.setVisibility(View.GONE);
				holder.gotoButton.setVisibility(View.GONE);
				holder.achieveButton.setVisibility(View.VISIBLE);
				holder.tv.setText(taskDrawedHolderList.get(position).taskdesc);
				holder.tv1.setText(mContext.getResources().getString(R.string.noticeboard_questDrawed));
				holder.tv1.setTextColor(0xFF696969);
			}
			}catch(Exception e){
				e.printStackTrace();
				Log.e(SuspensionButton.TAG, "AwardGener GETVIEW error");
				Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
				((Activity)mContext).finish();
			}
			return convertView;
		}
		
	}
	
	/**
	 * 下载网络图片工具类
	 * 
	 * @author JhonSmith
	 *
	 */
	public  class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView imageView;

		public DownloadImgTask(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String urlDisplay = urls[0];
//			String urlDisplay="http://img2.imgtn.bdimg.com/it/u=2861270851,2291089313&fm=21&gp=0.jpg";
			Bitmap myIcon = null;
			try {
				myIcon=CacheImage.get(new URL(urlDisplay));
				if(myIcon!=null)return myIcon;
				InputStream is = new java.net.URL(urlDisplay).openStream();
				myIcon=CacheImage.set(new URL(urlDisplay), is);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return myIcon;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Bitmap result) {
			BitmapDrawable ob = new BitmapDrawable(mContext.getResources(),result);
			if(imageView!=null){
				imageView.setBackgroundDrawable(ob);
			}
		}

	}
}
