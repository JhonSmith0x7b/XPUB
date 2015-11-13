package com.elex;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elex.dataObject.FBFansWallParam;
import com.elex.dataObject.FBInviteParam;
import com.elex.dataObject.FBShareParam;
import com.elex.suspension.R;
import com.elex.util.CacheImage;
import com.elex.util.FBUtil;
import com.elex.util.Globals;
import com.elex.util.MyFBUtil;
import com.elex.util.XpubUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public class SuspensionButton extends ImageView {
	public static final int REQUESTCODE_FLOATBUTTON_WEBVIEW_ACTIVITY = 9999;
	public static final int REQUESTCODE_FLOATBUTTON_TABLEVIEW_ACTIVITY = 9998;
	public static final int REQUESTCODE_FLOATBUTTON_SHARE_ACTIVITY = 9001;
	public static final int REQUESTCODE_FLOATBUTTON_NOTICEWINDOW_ACTIVITY = 9002;
	public static final int RESULTCODE_GOTO_FBPOST = 9997;
	public static final int RESULTCODE_GOTO_XPUBINVITE = 9996;
	public static final int RESULTCODE_GOTO_XPUBAWARD = 9995;
	public static final int RESULTCODE_GOTO_XPUBSHARE = 9994;
	public static final int RESULTCODE_GOTO_WEBVIEW = 9993;
	/**
	 * 创建全局变量
	 * 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
	 */
	private static WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public static WindowManager.LayoutParams getWindowParams(){
	    return wmParams;
	}
	
	private float mTouchX = 0; 
	private float mTouchY = 0; 
	private float x = 0; 
	private float y = 0; 
	private float mStartX = 0; 
	private float mStartY = 0; 
	
	private float density = 0;
	private int   padding = 15;
	protected boolean closed = false;
	/**
	 * 悬浮窗的生命周期是在以下Activity
	 * GameActivity——————————————————————游戏主Activity
	 * FloatButtonActivity———————————————通过悬浮窗打开的其他Activity
	 * FloatButtonWebVeiw———————————————通过悬浮窗打开的WebView
	 * ...
	 */
	protected int mFloatButtonStatus = 0;// -1 不显示 0宿主Activity在最前端 1 floatButtonWebview在前端  2  其他Activity比如 facebookActivity以及BoardActivity在前端 n btnActivityn在前端
	
	private WindowManager mWindowManager = null;
	private Context mContext = null;
	private boolean isAttached = false;
	private boolean isServerEnabled = false;
	private boolean isCfgDownloaded = false;
	
	private static boolean isFbdataReporting = false;
	private static boolean isFbdataReported = false;
	private static boolean isUserInfoUpdated = false;
	public final static String sVersion = "3.1.6";
	private boolean isLinkparamAdded = false;
	private boolean isFABEnabled = false;  //  具备一定条件后，悬浮窗才可展开
	private JSONArray testuidsArray = null;
	public static String mAppId = null;
	public static String mAppSec = "gyjdxpubmm";
	public static String mUid = null;
	public static String mRoleId = null;
	public static String mFbId = null;
	public static int mToFBIds = 0;//选取并发送的好友数量
	public static String mAccessToken=null;
	public static String mPostUrl="https://xpub.337.com/json.php";
	private static int mPulseCount = 50;     //通讯次数  会乘以100，  默认50*100 = 5000次
	private static List<NameValuePair> sPulsePostParams=new LinkedList<NameValuePair>();
	// 此windowManagerParams变量为获取的全局变量，用以保存悬浮窗口的属性 
	private WindowManager.LayoutParams  windowManagerParams = null;	
	
	private static SuspensionButton theInstance = null;
	public  FBInviteParam inviteParams = null;
	public  FBShareParam shareParams = null;
	public  FBFansWallParam fansWallParams = null;
	public  String mNoticeUrl="";
	private static String returnAndOpenWeb ="";//wx 用来判定返回是否跳转网页
	public static String mStrMsgBus = null;
	private static String dotStatus ="";
	private static boolean suspensionButtonShow=false;
	public static SuspensionButton getInstance() {
		return theInstance;
	}
	
	public Activity getGameActivity(){
		if(mContext!=null) {
			return (Activity)mContext;
		} else
			try {
				throw new Exception("mContext is null,can't get GameContext from SuspensionButton");
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		return null;
	}


	private static int nButtonSize = 40;
	public static final String TAG = "FloatingButton";

			
			
	public static void init(Context context,String appid, int btnSize,String appsec) {
		mAppId =appid;
		mAppSec = appsec;
		if(theInstance == null || theInstance.mContext != context) {
			Log.d(TAG, "new SuspensionButton");
			theInstance =  new SuspensionButton(context);
			theInstance.init();
		}
		nButtonSize = btnSize;
		if(nButtonSize < 40) {
			nButtonSize = 40;
		}
		theInstance.windowManagerParams.width = (int)(nButtonSize * theInstance.density); 
		theInstance.windowManagerParams.height = (int)(nButtonSize * theInstance.density); 
		if(!theInstance.isCfgDownloaded) {
    		Log.i(TAG, "init");
    		theInstance.isCfgDownloaded = true;
    		new Thread(theInstance.runnable).start(); 
    		new Thread(theInstance.runnable2).start(); 
		}
	}
	private static boolean isHelpshiftShouldbeInited = false;

	public SuspensionButton(Context context) { 
		super(context); 
		mContext = context;
		mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE); 
		windowManagerParams = getWindowParams();
		Activity a = (Activity)mContext;
        mListener = (FloatingButtonListener) a;	
        mXpubListener = new XPubListener();
	} 

	private boolean isExpanded = false;
	private ImageView expandView = null;
	private ImageView expandView2 = null;
	private ImageView expandView3 = null;
	
	private void onClick() {
		if(!isFABEnabled) {
			Toast.makeText(mContext, getResources().getString(R.string.floatbutton_loadingdata_tip),Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(myShortUrl == null){
	        //set my shortUrl  需要在branch初始化，即显示界面之后。以及roleid初始化之后。这个时间点刚好与悬浮窗能够展开的条件相同
	        XpubUtils.generateShortUrlAsync(mAppId+"_P_"+mUid+"_P_"+mRoleId, new CallBackFunciton(){
	    		public void call(String url){
	    			myShortUrl = url;
	    		}
	    	}, new CallBackFunciton(){public void call(String msg){}});
		}
		
		//上报 用户 aaa 来自于用户bbb的分享链接 当uid  以及 from_xpub_user_id ok时上报		
        if (from_xpub_user_id != null && !from_xpub_user_id.equals("")) {
        	List<NameValuePair> params2=new LinkedList<NameValuePair>();
			params2.clear();
			params2.add(new BasicNameValuePair("mod", "user"));
			params2.add(new BasicNameValuePair("act", "call"));
			params2.add(new BasicNameValuePair("a", mAppId)); 
			params2.add(new BasicNameValuePair("c", mUid));
			params2.add(new BasicNameValuePair("d", mRoleId));
			params2.add(new BasicNameValuePair("from_xpubuid", from_xpub_user_id));
    		Globals.getInstance().sendRequest(params2);
        }
        if(from_scancode!=null&&!from_scancode.equals("")){
        	List<NameValuePair> params2=new LinkedList<NameValuePair>();
			params2.clear();
			params2.add(new BasicNameValuePair("mod", "user"));
			params2.add(new BasicNameValuePair("act", "scancode"));
			params2.add(new BasicNameValuePair("a", mAppId)); 
			params2.add(new BasicNameValuePair("c", mUid));
			params2.add(new BasicNameValuePair("d", mRoleId));
			params2.add(new BasicNameValuePair("from_scancode", from_scancode));
    		Globals.getInstance().sendRequest(params2);
        }
        
        //解决fb信息不初始化。如果用户处于未登出状态，则登出。
        if((SuspensionButton.mFbId==null||SuspensionButton.mFbId.equals(""))&&FBUtil.isLogin()){
        	FBUtil.callFacebookLogout();
        }
        
		if(isExpanded) {
			shrink();
			Log.d(TAG, "shrink");
		} else {
			expand();
			Log.d(TAG, "expand");
		}
	}
	protected void shrink() {
		if(!suspensionButtonShow){
			return;
		}
		buttonBgIndex=0;
		isExpanded = false;
		this.setImageResource(R.drawable.floatbutton);
		expandView.setVisibility(View.INVISIBLE);
		expandView2.setVisibility(View.GONE);
		expandView3.setVisibility(View.GONE);
		for(int i = 0; i < btnList.size(); ++i) {
			ButtonInfo btnInfo = btnList.get(i);
			if(btnInfo.btn != null) {
				btnInfo.btn.setVisibility(View.INVISIBLE);
			}
			if(btnInfo.btnSplit != null) {
				btnInfo.btnSplit.setVisibility(View.INVISIBLE);
			}
		}
		if(noticeDot!=null){hideNoticeDot();}//wx 隐藏公告小红点
		if(awardDot!=null){hideAwardDot();}//wx 隐藏领奖小红点
		if(!awardDotStatus&&!noticeDotStatus){
			dotStatus="";
		}
		if(!dotStatus.equals("")){showDot();}
	}
	public  ImageView cBg;
	private String[] buttonBgLinkList=new String[30];//wx 存放按钮LINK
	private WindowManager.LayoutParams[] buttonBgParamList=new WindowManager.LayoutParams[30];//wx 存放按钮布局信息
	public int buttonBgIndex=0;
	public void initButtonBg(String link) {
		cBg = new ImageView(mContext);
		for (int i = 0; i < buttonBgIndex; i++) {
			if (!buttonBgLinkList[i].equals("") && buttonBgLinkList[i] != null) {
				if (link != null) {
					if(link.equals("sys337://close"))continue;
					if(link.equals(buttonBgLinkList[i])){
						cBg.setBackgroundResource(R.drawable.floatchildbutton_bg);
						cBg.setVisibility(View.VISIBLE);
						mWindowManager.addView(cBg, buttonBgParamList[i]);
					}
				}
			}
		}
	}
	private void expand() {
		buttonBgIndex=0;
		isExpanded = true;
		boolean expandToRight = true; 
		if(wmParams.x < nButtonSize * 3 * density) {
			expandToRight = true;
			Matrix matrix = new Matrix();  
			//设置旋转角度
			matrix.setRotate(180); 
			
            // 重新绘制Bitmap  
            BitmapDrawable draw = (BitmapDrawable)mContext.getResources().getDrawable(R.drawable.floatbutton_expand);
            Bitmap bitmap = draw.getBitmap();
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);  
            this.setImageBitmap(bitmap);
		} else {
			expandToRight = false;
			this.setImageResource(R.drawable.floatbutton_expand);
		}
		
		//计算实际需要显示的数量确定背景宽
		int nShowSize = 0;
		for(int i = 0; i < btnList.size(); ++i) {
			ButtonInfo btnInfo = btnList.get(i); 
			if(btnInfo.status.equals("1")){
				nShowSize++;
				btnIndex++;//按钮顺序的记录，帮助小红点确定位置
				if(btnInfo.link.startsWith("http://xpub.337.com/announce.php")){
					noticeWindowBtnNo=btnIndex;
				}
				if(btnInfo.link.startsWith("xpub://award")){
					awardBtnNo=btnIndex;
				}
			}
		}
		
		int bgWidth = (int) ((1.0f + nShowSize) * this.density * nButtonSize);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		WindowManager.LayoutParams params2= new WindowManager.LayoutParams();
		WindowManager.LayoutParams params3= new WindowManager.LayoutParams();
		params.copyFrom(windowManagerParams);
//		params.width = bgWidth;
		params.width= nButtonSize*(int)this.density;
		params2.copyFrom(params);
		params3.copyFrom(params);
		params2.width=bgWidth-2*nButtonSize*(int)this.density;
		params3.width=nButtonSize*(int)this.density;
		if(expandToRight) {
			params.x = 0;
			params2.x=params.width;
			params3.x=params2.x+params2.width;
		} else {
			params.x = (int) (wmParams.x - bgWidth + nButtonSize * this.density * 0.25f);
			params2.x=(int) params.x+params.width;
			params3.x=params2.x+params2.width;
			params2.width-=nButtonSize * this.density * 0.1f;
		}
		mWindowManager.updateViewLayout(expandView, params);
		expandView.setVisibility(View.VISIBLE);
		mWindowManager.updateViewLayout(expandView2, params2);
		expandView2.setVisibility(View.VISIBLE);
		mWindowManager.updateViewLayout(expandView3, params3);
		expandView3.setVisibility(View.VISIBLE);
		if(expandToRight) {
			params.x = (int) (wmParams.x);
		} else {
			params.x -= (int)(nButtonSize * this.density); 
		}
		params.width = (int)(nButtonSize * density);
		params.height = (int)(nButtonSize * density);
		
		int nShowed=0;
		//为按钮生成 增加展开方向判断
		if (expandToRight) {
			for (int i = 0; i < btnList.size(); ++i) {
				ButtonInfo btnInfo = btnList.get(i);
				// 根据状态决定是否显示
				if (btnInfo.status.equals("1")) {
					params.x += (int) (nButtonSize * this.density);
					btnInfo.show(params);
					buttonBgLinkList[buttonBgIndex]=btnInfo.link;//存放按钮link用于判定
					WindowManager.LayoutParams cbgParams = new WindowManager.LayoutParams();//用来存储每一个按钮的布局对象
					cbgParams.copyFrom(params);
					buttonBgParamList[buttonBgIndex]=cbgParams;//存放按钮布局信息
					buttonBgIndex++;//用于匹配布局信息与link的序号
					// 最右边一个不显示分隔条
					nShowed++;
					if (nShowed == nShowSize) {
						if (btnInfo.btnSplit != null)
							btnInfo.btnSplit.setVisibility(View.INVISIBLE);
					}
				}

			}
		} else {
			for (int i = 0; i < btnList.size(); ++i) {
				ButtonInfo btnInfo = btnList.get(btnList.size() - (i + 1));
				// 根据状态决定是否显示
				if (btnInfo.status.equals("1")) {
					params.x += (int) (nButtonSize * this.density);
					btnInfo.show(params);
					buttonBgLinkList[buttonBgIndex]=btnInfo.link;//存放按钮link用于判定
					WindowManager.LayoutParams cbgParams = new WindowManager.LayoutParams();//用来存储每一个按钮的布局对象
					cbgParams.copyFrom(params);
					buttonBgParamList[buttonBgIndex]=cbgParams;//存放按钮布局信息
					buttonBgIndex++;//用于匹配布局信息与link的序号
					// 最右边一个不显示分隔条
					nShowed++;
					if (nShowed == nShowSize) {
						if (btnInfo.btnSplit != null)
							btnInfo.btnSplit.setVisibility(View.INVISIBLE);
					}
				}

			}
		}
		if (!childDotInit) {// wx 判定 如果没有生成 则生成消息提示的小红点
			initNoticeDot();
			initAwardDot();
			childDotInit = true;
		}
		if (noticeDot != null&&noticeDotStatus&&dotStatus.equals("notice")) {
			positionNoticeDot();// wx 公告小红点
			showNoticeDot();
		}
		if (awardDot != null&&awardDotStatus&&dotStatus.equals("notice")) {
			positionAwardDot();// wx 领奖小红点
			showAwardDot();
		}
		btnIndex=0;//按钮顺序归零
		hideDot();
	}

	WebView mWebView = null;
	protected void showWebview(String link) {
//		showWebViewActivity(link);
		hideFloatButton();
		Intent intent = new Intent();
		intent.setClass(mContext, ShinBoardActivity.class);
		intent.putExtra("type", "4");
		intent.putExtra("url", link);
		mContext.startActivity(intent);
		/**
		if(link != null && parentView != null && (mContext != null)) {
			shrink();
			this.setImageResource(R.drawable.floatbutton_close);
			if(mWebView == null) {
				mWebView = new WebView((Activity)mContext);
				mWebView.setFocusable(true);
				mWebView.setFocusableInTouchMode(true);
				mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
				mWebView.setWebChromeClient(new WebChromeClient());
				mWebView.setWebViewClient(new WebViewClient());
				WebSettings webSettings = mWebView.getSettings();
				webSettings.setSupportZoom(false);
				if(Build.VERSION.SDK_INT < 8){
					webSettings.setPluginsEnabled(true);
				}else{
					webSettings.setPluginState(PluginState.ON);
				}
				webSettings.setJavaScriptEnabled(true);
				mWebView.setWebViewClient(new MyWebViewClient());
				parentView.addView(mWebView,
						new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.NO_GRAVITY));
			}
			mWebView.loadUrl(link);
		}**/
	}
//	private void getUserTasks (){
//			SuspensionButton.getInstance().showLoading();
//			List<NameValuePair> params =  new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("a",mAppId));
//			params.add(new BasicNameValuePair("c",mUid));
//			params.add(new BasicNameValuePair("d",mRoleId));
//			params.add(new BasicNameValuePair("mod","user"));
//			params.add(new BasicNameValuePair("act","tasks"));
//			Globals.getInstance().sendRequest(params, new onGetUserTasks(),new onGetUserTasksError());
//		
//	};
//	
//	private class onGetUserTasks implements CallBackFunciton{
//		public void call(String json){
//			SuspensionButton.getInstance().removeLoading();
//			SuspensionButton.getInstance().showBoardActivity("2",json);
//		}
//	}
//	private class onGetUserTasksError implements CallBackFunciton{
//		@Override
//		public void call(String json) {
//			SuspensionButton.getInstance().removeLoading();
//			Toast.makeText(mContext, getResources().getString(R.string.noticeboard_questgeterror), Toast.LENGTH_SHORT).show();
//			SuspensionButton.getInstance().showBoardActivity("2","");
//						
//		}
//		
//	}

	public void showBoardActivity(String type){
		hideFloatButton();
		Activity a =(Activity)mContext;
		Intent intent = new Intent(a,
				ShinBoardActivity.class);
		String msg = type;
		intent.putExtra("type", msg);
		a.startActivity(intent);
	}
	public static final String NOTICEWINDOWCHECKKEY="noticeWindowGetAway";
	private boolean showNoticeWindowCheck(){
		if (XpubUtils.getValue(SuspensionButton.NOTICEWINDOWCHECKKEY, mContext) == null
				|| XpubUtils.getValue(SuspensionButton.NOTICEWINDOWCHECKKEY, mContext).equals(
						""))
			return true;
		else if (XpubUtils.getValue(SuspensionButton.NOTICEWINDOWCHECKKEY, mContext)
				.equals(XpubUtils.getCurrentDay()))
			return false;
		return true;
	}
	private int noticeWindowFirstBoom=0;
	public void showNoticeWindow(){
		if(myBusBuBu==null||myBusBuBu.equals("")){
			Toast.makeText(mContext, getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
			return;
		}
		mFloatButtonStatus = 2;
		try{
			hideFloatButton();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "showNoticeWindow hideFloatButton");
		}
		Activity a =(Activity)mContext;
		Intent intent = new Intent(a,
				NoticeWindowActivity.class);
		a.startActivityForResult(intent, REQUESTCODE_FLOATBUTTON_NOTICEWINDOW_ACTIVITY);
	}
	protected void showWebViewActivity(String link){
		hideDot();//wx  显示网页的时候  不显示小红点
		if(link != null  && (mContext != null)) {
			shrink();
			this.setImageResource(R.drawable.floatbutton_close);
		}else{
			return;
		}
		Activity a = (Activity)mContext;
		Intent intent = new Intent(a,FloatButtonWebViewActivity.class);
		//可以把要传递的参数放到一个bundle里传递过去，bundle可以看做一个特殊的map.  
        Bundle bundle=new Bundle();  
        bundle.putString("url", link);  
        intent.putExtras(bundle);  
		a.startActivityForResult(intent,REQUESTCODE_FLOATBUTTON_WEBVIEW_ACTIVITY);
		try{
			new Handler().postDelayed(new Runnable(){
				
				@Override
				public void run() {
					SuspensionButton.getInstance().moveFloatButton(nDm.widthPixels,(int)(77/2*nDm.density));
				}
			}, 300);
		}catch(Exception e){
			Log.d("SuspensionButtonERROR", "webViewFloat");
		}
	}
	protected void hideWebViewActivity(){
		Activity a = (Activity)mContext;
		if(mContext !=null){
			a.finishActivity(REQUESTCODE_FLOATBUTTON_WEBVIEW_ACTIVITY);
		}
		this.setImageResource(R.drawable.floatbutton);
		
	}
	protected void hideWebview() {
		hideWebViewActivity();
		/**
		if(mWebView != null) {
			parentView.removeView(mWebView);
			mWebView = null;
		}
		this.setImageResource(R.drawable.floatbutton);
		setAlpha(255);
		**/
	}
	
//	private boolean canSetAlpha = false;
//	protected void setAlphaDelay() {
//		if(isExpanded) return;
//		canSetAlpha = true;
//		theMsgHandler.postDelayed(new Runnable() {
//			public void run() {
//				if(canSetAlpha && !isExpanded) {
////					AlphaAnimation anim = new AlphaAnimation(1.0f, 0.3f);
////					anim.setDuration(2000);
////					theInstance.startAnimation(anim);
//					theInstance.setAlpha(100);
//				}
//			}
//		}, 2000);
//	}
	private ImageView loadingImg;//进入邀请页等待FB好友数据时的等待图片
	private boolean loadingDirect=false;
	private AlphaAnimation buttonClick = new AlphaAnimation(1F,0.1F);
	ViewGroup frameLayout;
	private boolean loadingAddCheck=false;
	public void showLoading(){
//		loadingImg= new ImageView(mContext);
//		loadingImg.setBackgroundResource(R.drawable.notice_loading);
//		android.view.WindowManager.LayoutParams viewParams = new android.view.WindowManager.LayoutParams();
////		viewParams.copyFrom(windowManagerParams);
//		viewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//		viewParams.format=PixelFormat.RGBA_8888;
//		viewParams.x=0;
//		viewParams.y=0;
//		viewParams.width=300;
//		viewParams.height=300;
//		mWindowManager.addView(loadingImg, viewParams);
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						removeLoading();
					}
				});
			}
		});
		thread.start();
		loadingImg= new ImageView(mContext);
		android.view.WindowManager.LayoutParams viewParams = new android.view.WindowManager.LayoutParams();
		viewParams.x=0;
		viewParams.y=0;
		viewParams.width=(int) (300*nDm.density);
		viewParams.height=(int) (300*nDm.density);
		viewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		viewParams.windowAnimations=android.R.style.Animation_Translucent;
		viewParams.format=PixelFormat.RGBA_8888;
		loadingImg.setImageResource(R.drawable.notice_loading);
		loadingImg.setVisibility(View.VISIBLE);
		buttonClick.setDuration(1000);
		buttonClick.setRepeatCount(20);
		loadingImg.startAnimation(buttonClick);
		loadingDirect=true;
		loadingImg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(loadingDirect){
					Matrix matrix = new Matrix();
					matrix.setRotate(180);
					BitmapDrawable draw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.notice_loading);
					Bitmap bitmap= draw.getBitmap();
					bitmap=Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
					loadingImg.setImageBitmap(bitmap);
					loadingDirect=false;
				}else{
					loadingImg.setImageResource(R.drawable.notice_loading);
					loadingDirect=true;
				}
			}
			
		});
		try{
			frameLayout= new FrameLayout(mContext);
			mWindowManager.addView(frameLayout, viewParams);
			frameLayout.addView(loadingImg);
			loadingAddCheck=true;
		}catch(Exception e){
			Log.d("SuspensionButtonERROR", "addLoading");
		}
	}
	public void removeLoading(){
		if(frameLayout==null)return;
		if(!loadingAddCheck)return;
		try{
			mWindowManager.removeView(frameLayout);
			loadingAddCheck=false;
		}catch(Exception e){
			Log.d("SuspensionButtonERROR", "removeLoading");
		}
	}
	private boolean didCompress=false;
	private int compressX=3;
	private int compressY=0;
	private Runnable compressRunnable;
	protected void setCompressDelay(){
		if(isExpanded)return;
		final android.view.WindowManager.LayoutParams compressViewParams=new android.view.WindowManager.LayoutParams();
		theMsgHandler.postDelayed(compressRunnable=new Runnable() {// 将悬浮窗变为半圆形

			@Override
			public void run() {
				if (!isExpanded &&! didCompress &&mFloatButtonStatus!=1&& mFloatButtonStatus!=2) {
					compressViewParams.copyFrom(windowManagerParams);
					compressViewParams.width=(int) (nButtonSize * theInstance.density)*3/10;
					compressViewParams.height=(int) (nButtonSize * theInstance.density)*9/10;
					compressViewParams.x=compressX;
					compressViewParams.y=compressY;
					try{
						if(childDotDirection.equals("L")){
							Matrix matrix = new Matrix();
							matrix.setRotate(180);
							BitmapDrawable draw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.folatbutton_compress);
							Bitmap bitmap= draw.getBitmap();
							bitmap=Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
							SuspensionButton.getInstance().setImageBitmap(bitmap);
						}else if(childDotDirection.equals("R")){
							SuspensionButton.getInstance().setImageResource(
									R.drawable.folatbutton_compress);
						}else {return;}
						mWindowManager.updateViewLayout(theInstance, compressViewParams);
						didCompress=true;
					}catch(Exception e){
						SuspensionButton.getInstance().setImageResource(R.drawable.floatbutton);
						Log.d("SuspensionButtonERROR", "messCompress");
					}
				}
			}
		}, 5000);
	}
	private int moveBefX=0;//监听按下与抬起的X坐标
	private int moveAftX=0;
	protected void setCompressImmed(){
		if(isExpanded)return;
		final android.view.WindowManager.LayoutParams compressViewParams=new android.view.WindowManager.LayoutParams();
		compressViewParams.copyFrom(windowManagerParams);
		compressViewParams.width=(int) (nButtonSize * theInstance.density)*3/10;
		compressViewParams.height=(int) (nButtonSize * theInstance.density)*9/10;
		compressViewParams.x=compressX;
		compressViewParams.y=compressY;
		if(childDotDirection.equals("L")){
		Matrix matrix = new Matrix();
		matrix.setRotate(180);
		BitmapDrawable draw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.folatbutton_compress);
		Bitmap bitmap= draw.getBitmap();
		bitmap=Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		SuspensionButton.getInstance().setImageBitmap(bitmap);
		}else if(childDotDirection.equals("R")){
		SuspensionButton.getInstance().setImageResource(
				R.drawable.folatbutton_compress);
		}else {return;}
		mWindowManager.updateViewLayout(theInstance, compressViewParams);
		didCompress=true;
	}
	//wx 第一次的静态数据
	public static String myBusBuBu="";
	private boolean myBusCheck=false;
	private void myBusBuBu(String mStrBus){
		if(myBusCheck)return;
		myBusBuBu=mStrBus;
		myBusCheck=true;
	}
	//wx 提示新消息的小红点
	private  TextView dot;
	private WindowManager.LayoutParams nWindowManagerParams;
	private DisplayMetrics nDm=null;
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "RtlHardcoded" })
	public void initDot(){
		nDm = mContext.getResources().getDisplayMetrics();
		dot =new TextView(mContext);
		//noticeIcon.setBackgroundResource(mContext.getResources().getIdentifier("ic_launcher", "drawable", mContext.getPackageName()));
		GradientDrawable gd =new GradientDrawable();
		gd.setColor(Color.RED);
		gd.setCornerRadius(15);
		gd.setShape(GradientDrawable.OVAL);
		if (Build.VERSION.SDK_INT > 10) {		
			dot.setBackground(gd);
		}else{
			dot.setBackgroundDrawable(gd);
		}
		nWindowManagerParams=new WindowManager.LayoutParams();
		nWindowManagerParams.type=WindowManager.LayoutParams.TYPE_PHONE;
		nWindowManagerParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		nWindowManagerParams.gravity=Gravity.LEFT|Gravity.TOP;
		nWindowManagerParams.format=PixelFormat.RGBA_8888;
		nWindowManagerParams.x=0;
		nWindowManagerParams.y=0;
		nWindowManagerParams.width=nButtonSize*(int)density/4;
		nWindowManagerParams.height=nButtonSize*(int)density/4;
		dot.setVisibility(View.GONE);
		//nWindowManagerParams=windowManagerParams;
	}
	public void positionDot(){
		nWindowManagerParams.x=(int) (x - mTouchX);
		nWindowManagerParams.y = (int) (y - mTouchY);
		mWindowManager.updateViewLayout(dot, nWindowManagerParams);
	}

	private void judgePositionDot(int judgeX, int judgeY) {
		// int judgeX=nDm.widthPixels;
		// nWindowManagerParams.x=(int) (x - mTouchX);
		// nWindowManagerParams.y = (int) (y - mTouchY);
		// if(nWindowManagerParams.x>judgeX/2){
		// nWindowManagerParams.x=judgeX;
		// }else{
		// nWindowManagerParams.x=-judgeX;
		// }
		nWindowManagerParams.x = judgeX;
		nWindowManagerParams.y = judgeY;
		Log.d("judgeP", judgeY + "><"+nDm.heightPixels);
		if (judgeY > (nDm.heightPixels - 150)) {
			nWindowManagerParams.y = nDm.heightPixels - 150;
		}
		childDotXPosition=nWindowManagerParams.x;//wx  获取与消息提示红点的相对位置
		childDotYPosition= nWindowManagerParams.y;
		mWindowManager.updateViewLayout(dot, nWindowManagerParams);
	}
	private void showDot(){
		if(dotStatus!=null&&dotStatus.equals("notice")&&mFloatButtonStatus!=1){
			if(dot!=null)
			dot.setVisibility(View.VISIBLE);
		}
	}
	private void hideDot(){
		if(dot!=null)
		dot.setVisibility(View.INVISIBLE);
	}
	//公告以及领奖的子小红点
	private  String childDotDirection ="R";
	private  int childDotYPosition=0;
	private  int childDotXPosition=0;
	private  boolean childDotInit=false;
	//公告按钮小红点
	private TextView noticeDot;
	private boolean noticeDotStatus=false;
	private android.view.WindowManager.LayoutParams nD1WindowManagerParams;
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "RtlHardcoded" })
	public void initNoticeDot(){
		nDm= mContext.getResources().getDisplayMetrics();
		noticeDot = new TextView(mContext);
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.RED);
		gd.setCornerRadius(15);
		gd.setShape(GradientDrawable.OVAL);
		if (Build.VERSION.SDK_INT > 10) {		
			noticeDot.setBackground(gd);
		}else{
			noticeDot.setBackgroundDrawable(gd);
		}
		nD1WindowManagerParams=new WindowManager.LayoutParams();
		nD1WindowManagerParams.type=WindowManager.LayoutParams.TYPE_PHONE;
		nD1WindowManagerParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		nD1WindowManagerParams.gravity=Gravity.LEFT|Gravity.TOP;
		nD1WindowManagerParams.format=PixelFormat.RGBA_8888;
		nD1WindowManagerParams.x=0;
		nD1WindowManagerParams.y=0;
		nD1WindowManagerParams.width=nButtonSize*(int)density/5;
		nD1WindowManagerParams.height=nButtonSize*(int)density/5;
		noticeDot.setVisibility(View.GONE);
		mWindowManager.addView(noticeDot, nD1WindowManagerParams);
	}
	private int noticeWindowBtnNo=0;
	private int btnIndex=0;
	public void positionNoticeDot(){
		if(childDotDirection.equals("R")){
			nD1WindowManagerParams.x=(int) (childDotXPosition+nButtonSize*(noticeWindowBtnNo)*1*nDm.density);
		}
		if(childDotDirection.equals("L")){
			nD1WindowManagerParams.x=(int) (childDotXPosition-nButtonSize*(noticeWindowBtnNo)*1*nDm.density);
		}
		nD1WindowManagerParams.y=childDotYPosition;
		mWindowManager.updateViewLayout(noticeDot, nD1WindowManagerParams);
	}
	public void hideNoticeDot(){
		noticeDot.setVisibility(View.GONE);
	}
	public void showNoticeDot(){
		noticeDot.setVisibility(View.VISIBLE);
	}
	//领奖按钮小红点
	private TextView awardDot;
	private boolean awardDotStatus=false;
	private android.view.WindowManager.LayoutParams nD2WindowManagerParams;

	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "RtlHardcoded" })
	public void initAwardDot() {
		nDm = mContext.getResources().getDisplayMetrics();
		awardDot = new TextView(mContext);
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.RED);
		gd.setCornerRadius(15);
		gd.setShape(GradientDrawable.OVAL);
		if (Build.VERSION.SDK_INT > 10) {		
			awardDot.setBackground(gd);
		}else{
			awardDot.setBackgroundDrawable(gd);
		}
		nD2WindowManagerParams = new WindowManager.LayoutParams();
		nD2WindowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		nD2WindowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		nD2WindowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
		nD2WindowManagerParams.format = PixelFormat.RGBA_8888;
		nD2WindowManagerParams.x = 0;
		nD2WindowManagerParams.y = 0;
		nD2WindowManagerParams.width = nButtonSize * (int) density / 5;
		nD2WindowManagerParams.height = nButtonSize * (int) density / 5;
		awardDot.setVisibility(View.GONE);
		mWindowManager.addView(awardDot, nD2WindowManagerParams);
	}
	private int awardBtnNo=0;
	public void positionAwardDot() {
		if (childDotDirection.equals("R")) {
			nD2WindowManagerParams.x=(int) (childDotXPosition+nButtonSize*(awardBtnNo)*1*nDm.density);
		}
		if (childDotDirection.equals("L")) {
			nD2WindowManagerParams.x=(int) (childDotXPosition-nButtonSize*(awardBtnNo)*1*nDm.density);
		}
		nD2WindowManagerParams.y = childDotYPosition;
		mWindowManager.updateViewLayout(awardDot, nD2WindowManagerParams);
	}

	public void hideAwardDot() {
		awardDot.setVisibility(View.GONE);
	}

	public void showAwardDot() {
		awardDot.setVisibility(View.VISIBLE);
	}
	private void dealNoticeMsg(String mStrBus){
		try {
			JSONObject jObject = new JSONObject(mStrBus);
			String red =jObject.optString("red");
			String red1=jObject.optString("red1");
			String red2=jObject.optString("red2");
			if(red!=null&&red.equals("1")){
				dotStatus = "notice";
			}
			if(red1!=null&&red1.equals("1")){
				awardDotStatus=true;
			}else if(red1!=null&&red1.equals("0")){
				awardDotStatus=false;
			}
			if(red2!=null&&red2.equals("1")){
				noticeDotStatus=true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//____________________________________________________________
	@SuppressLint("RtlHardcoded")
	private void init()
	{	    
		if(mContext == null) {
			return;
		}
		this.setImageResource(R.drawable.floatbutton); // 这里简单的用自带的icon来做演示 
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		this.density = dm.density;
		// 获取WindowManager
		this.mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE); 
		// 设置LayoutParams(全局变量）相关参数 
		this.windowManagerParams = wmParams; 
		this.windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type 
		this.windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明 
		// 设置Window flag 
		this.windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 
		/* 
		* 注意，flag的值可以为： 
		* LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件 
		* LayoutParams.FLAG_NOT_FOCUSABLE 不可聚焦 
		* LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸 
		*/ 
		// 调整悬浮窗口至左上角，便于调整坐标 
		this.windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值 
		this.windowManagerParams.x = 0; 
		this.windowManagerParams.y = 0; 
		// 设置悬浮窗口长宽数据 
		this.windowManagerParams.width = (int)(nButtonSize * density);//LayoutParams.WRAP_CONTENT; 
		this.windowManagerParams.height = (int)(nButtonSize * density);//LayoutParams.WRAP_CONTENT;
		this.setVisibility(View.INVISIBLE);

		expandView = new ImageView(mContext) {
			@Override 
			public boolean onTouchEvent(MotionEvent event) { 
				return false;
			}
		};
		// setImageResource对应XML中src
		// setBackgroundResource对应background
		// src是图片内容（前景），不会拉伸；background是背景，会根据view组件给定的长宽进行拉伸，两者可以同时使用
		expandView2 = new ImageView(mContext) {
			@Override 
			public boolean onTouchEvent(MotionEvent event) { 
				return false;
			}
		};
		expandView3 = new ImageView(mContext) {
			@Override 
			public boolean onTouchEvent(MotionEvent event) { 
				return false;
			}
		};
		Log.d(TAG, "expandView set resource");
		expandView.setBackgroundResource(mContext.getResources().getIdentifier("floatbutton_bg_1", "drawable", mContext.getPackageName()));
		expandView.setVisibility(View.INVISIBLE);
		expandView2.setBackgroundResource(mContext.getResources().getIdentifier("floatbutton_bg_2", "drawable", mContext.getPackageName()));
		expandView2.setVisibility(View.INVISIBLE);
		expandView3.setBackgroundResource(mContext.getResources().getIdentifier("floatbutton_bg_3", "drawable", mContext.getPackageName()));
		expandView3.setVisibility(View.INVISIBLE);
		mWindowManager.addView(expandView, windowManagerParams);
		mWindowManager.addView(expandView2, windowManagerParams);
		mWindowManager.addView(expandView3, windowManagerParams);
	}
	private int runnableRequestFailNum=0;
	private Runnable runnable = new Runnable(){
        @Override  
        public void run() {  
        	//后台进行网络操作
        	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND); //设置线程优先级为后台
    		Log.i(TAG, "runnable");
        	if(theInstance.downloadCfg()) {
        		theMsgHandler.sendEmptyMessage(1);
        	}else{
        		if(runnableRequestFailNum++<3){
        			try {
						Thread.sleep(3, Time.SECOND);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			Thread thread = new Thread(runnable);
        			thread.start();
        		}
        	}
        }
	};
	
	//tick
	private Looper mLooper = null;
	private Handler mHandler=null;
	private HttpClient mHttpClient=null;
	private HttpPost mHttpPost=null;
	private int tickCount =0;

	private Runnable runnable2 = new Runnable(){
			 
        @Override  
        public void run() {
        	if(mLooper == null) {
        		Looper.prepare();
        		mLooper =Looper.myLooper() ;
        	}
        	//定时请求线程的handler
   		 	if(mHandler == null) mHandler = new Handler(); 
        	try{
        	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND); //设置线程优先级为后台
    		if(SuspensionButton.getInstance() == null || SuspensionButton.getInstance().closed ) {
    			
    			mHandler.postDelayed(this, 1000);
    			return;
    		}else{
    			//Log.d(TAG,"Thead name="+Thread.currentThread().getName());
    		}
    		
    		//借用tick 进行条件判断  按钮信息初始化完毕后，才在按钮的link上追加玩家信息
    		if(isUserInfoUpdated && !isLinkparamAdded){
    			if(btnList != null){  
    				String sSimCode="";
    				TelephonyManager phoneManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    				if (phoneManager != null) {
    					sSimCode = phoneManager.getSimOperator();
    				}
	    			for(int i = 0; i < btnList.size(); ++i) {
	    				ButtonInfo info = btnList.get(i);
	    				if(!TextUtils.isEmpty(info.link) && !info.link.equals("sys337://close")){
	    					info.link += info.link.contains("?")?"&":"?";
	    					info.link += 
	    							"uid="+mUid
	    							+"&roleid="+mRoleId
	    							+"&source="+"com.giraffe.guiwusg_tw_offpay"//sPkgName
	    							+"&mccmnc="+sSimCode
	    							+"&platform=android";
	    				}
	    				try{
	    					for(int j=0;j <testuidsArray.length();++j){
	    						if(testuidsArray.getString(j).equals(mUid)){
	    							info.status= "1";
	    							break;
	    						}
	    					}
	    				}catch(Exception e){
	    					Log.e(TAG,"bianli tsetUIdsArary error"+e.getMessage());
	    				}
	    			}
	    			isLinkparamAdded = true;
    			}
    		}
			//增加了连接参数及有了初始化消息后 悬浮窗才可以展开
			if(isLinkparamAdded && mStrMsgBus != null && !isFABEnabled){
    			isFABEnabled = true;
			}
			
			//fbid 及 fb accesstoken及 uid roleid都具备，才上报fbdata
			if(mFbId != null && mAccessToken != null && mUid != null && mRoleId!=null && !isFbdataReported && !isFbdataReporting){
				onCanReportFbdata();
			}

    		//借用tick 进行条件判断 end
    		
    		if(mHttpPost == null) mHttpPost = new HttpPost(mPostUrl);
    		sPulsePostParams.clear();
    		sPulsePostParams.add(new BasicNameValuePair("x2", "1"));  //接口v2  pulse消息
    		sPulsePostParams.add(new BasicNameValuePair("s", mAppSec)); //服务端与客户端约定的应用秘钥——每个应用唯一
    		sPulsePostParams.add(new BasicNameValuePair("c", mUid));
    		sPulsePostParams.add(new BasicNameValuePair("d", mRoleId));
    		sPulsePostParams.add(new BasicNameValuePair("v",sVersion));
    		if(mStrMsgBus == null) sPulsePostParams.add(new BasicNameValuePair("force", "99999"));
    		try {
    			if(tickCount > 0 && tickCount % 5 == 0)	{//每5秒执行一次pulse
    			mHttpPost.setEntity(new UrlEncodedFormEntity(sPulsePostParams, "utf-8"));
    			if(mHttpClient == null) mHttpClient = new DefaultHttpClient();
    			HttpResponse response = mHttpClient.execute(mHttpPost);
     			String sMsgjson = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);	
     			if(mStrMsgBus!=null){//預加載圖片判斷
     				if(!AsyncFetch.fetchCheck){
     					AsyncFetch.fetchImg();
     				}
     			}
				if(sMsgjson != null && sMsgjson.length() > 0) {
						if (mStrMsgBus!=null) {// 小红点判断
							dealNoticeMsg(sMsgjson);
							if (suspensionButtonShow) {
								((Activity) mContext)
										.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												showDot();
											}

										});
							}
						}
					mStrMsgBus = sMsgjson;
					myBusBuBu(sMsgjson);
					JSONObject jsonObjMsg = new JSONObject(sMsgjson);
					String status = jsonObjMsg.getString("status");
						if("h".equals(status)){		
//							((Activity)mContext).runOnUiThread(new Runnable(){
//
//								@Override
//								public void run() {
//									hideFloatButton();
//								}
//								
//							});
							theMsgHandler.sendEmptyMessage(2);
							Log.i(TAG,"hide****************************************");
							} else {
								if (suspensionButtonShow && isFABEnabled) {
									if (noticeWindowFirstBoom++ == 0) {
										if (showNoticeWindowCheck()) {
											JSONObject jObject = new JSONObject(myBusBuBu);
											String bomb = jObject
													.optString("bomb");
											if (bomb != null
													&& "1".equals(bomb)) {
												((Activity) mContext)
														.runOnUiThread(new Runnable() {

															@Override
															public void run() {
																showNoticeWindow();
																Log.i(TAG,
																		"SHOW NOTICEWINDOW");
															}
														});
											}
										}
										// JSONArray banners =
										// jsonObjMsg.getJSONArray("banners");
										// JSONArray tasks =
										// jsonObjMsg.getJSONArray("tasks");
										// JSONArray infos =
										// jsonObjMsg.getJSONArray("infos");
									}
								}
							}
				}else{
					//Log.d(TAG,"pulse response nothing$$$$$$$$$$$$$$$$$$$$$$$$");
				}
    			}
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}  
        	}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			mHandler.postDelayed(this, 1000);
    		}
        	
        	tickCount++;
        	if(tickCount > mPulseCount * 100) {
        		if(mLooper != null ) mLooper.quit();
        	}
    		if(tickCount == 1) Looper.loop();
        }
	};
	
	
	// wx 悬浮窗的隐藏函数
	public void hideFloatButton() {
		try{
			this.shrink();
			this.hideDot();
			this.setVisibility(View.GONE);
			suspensionButtonShow = false;
			if (didCompress) {
				theInstance.setImageResource(R.drawable.floatbutton);
				mWindowManager.updateViewLayout(theInstance,
						windowManagerParams);
				didCompress = false;
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "SuspensionButton HideFloatButton ERROR");
		}
	}


	protected void onCanReportFbdata() {
		isFbdataReporting = true;
		List<NameValuePair> params=new LinkedList<NameValuePair>();
		params.clear();
		params.add(new BasicNameValuePair("mod", "fb"));
		params.add(new BasicNameValuePair("act", "login"));
		params.add(new BasicNameValuePair("a", mAppId)); 
		params.add(new BasicNameValuePair("b", mFbId));
		params.add(new BasicNameValuePair("c", mUid));
		params.add(new BasicNameValuePair("d", mRoleId));
	    params.add(new BasicNameValuePair("k",mAccessToken));
		Globals.getInstance().sendRequest(params,new CallBackFunciton() {
			@Override
			public void call(String json) {
				isFbdataReported = true;
				isFbdataReporting = false;
			}
		},new CallBackFunciton() {
			@Override
			public void call(String json) {
				isFbdataReporting = false;
			}
		});
	}
	
	// wx 悬浮窗的显示函数
	public void showFloatButton() {
		try{
			this.setVisibility(View.VISIBLE);
			suspensionButtonShow = true;
			if (!dotStatus.equals("")) {
				this.showDot();
			}
			if (didCompress) {
				theInstance.setImageResource(R.drawable.floatbutton);
				mWindowManager.updateViewLayout(theInstance,
						windowManagerParams);
				didCompress = false;
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "SuspensionButton ShowFloatButton ERROR");
		}
	}
	//wx 悬浮窗移动函数
	public void moveFloatButton(int x,int y){
		windowManagerParams.x=x;
		windowManagerParams.y=y;
		try{
			mWindowManager.updateViewLayout(theInstance, windowManagerParams);
		}catch(Exception e){
			Log.d(SuspensionButton.TAG, "SuspensionButton moveFloatButton error");
		}
	}

	
	//SuspensionButton.init(context, mAppId,nButtonSize);

	
	/**
	 * 使用悬浮窗的Activity，如果要使用facebook邀请和分享，需要实现以下接口，以便在邀请和分享完成时，加入自己的逻辑
	 * @author gengyj
	 *
	 */
    public interface FloatingButtonListener {
        public void onFbInviteCallback(GameRequestDialog.Result result, FacebookException error);
        public void onFbShareCallback(ShareDialog.Result result, FacebookException error);
        public void onFbProfileTrack(Profile oldProfile, Profile currentProfile);
        public void onFbAccessTokenTrack(AccessToken oldAccessToken,AccessToken currentAccessToken);
    }
    
    /**
	 * xpub listener  
	 * @author gengyj
	 *
	 */
    public class XPubListener {
        public void onFbInviteCallback(GameRequestDialog.Result result, FacebookException error){
        	showFloatButton();
        	
        	if (error == null && result!=null) {	
				final String requestId = result.getRequestId();
				List<NameValuePair> params=new LinkedList<NameValuePair>();
				params.clear();
				params.add(new BasicNameValuePair("mod", "fb"));
				params.add(new BasicNameValuePair("act", "invite"));
				params.add(new BasicNameValuePair("a", mAppId)); 
				params.add(new BasicNameValuePair("b", mFbId));
				params.add(new BasicNameValuePair("r", requestId));
				params.add(new BasicNameValuePair("t", String.valueOf(mToFBIds)));
        		Globals.getInstance().sendRequest(params);
        		mToFBIds=0;//邀请人数，上报后清0
        		
	    		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_fb_invite");
			} 
        }
        public void onFbShareCallback(ShareDialog.Result result, FacebookException error){
        	showFloatButton();
        	if (error == null && result != null) {// When the story is posted, echo the success and the post Id.
    			String postId = result.getPostId();
    			if(postId == null){
    				//发送成功 webdialog能成功返回postid，但sdk返回空得postid 待解决
    				//对这种情况，暂时生成postId。    
    				postId=mFbId+"_"+System.currentTimeMillis();   						
    			}
    			if (postId != null) {
    				List<NameValuePair> params=new LinkedList<NameValuePair>();
    				params.clear();
    				params.add(new BasicNameValuePair("mod", "fb"));
    				params.add(new BasicNameValuePair("act", "share"));
    				params.add(new BasicNameValuePair("a", mAppId)); 
    				params.add(new BasicNameValuePair("b", mFbId));
    				params.add(new BasicNameValuePair("p", postId)); 
            		Globals.getInstance().sendRequest(params);
            		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_fb_post");
    			} 
        	}
        }
    }
    
 // Use this instance of the interface to deliver action events
    public FloatingButtonListener mListener;
    public XPubListener mXpubListener;
    
    //init for branch track
    private Branch branch = null;
    private String from_xpub_user_id=null;
    private String from_scancode="";
    public void onStart(){
		//branch 初始化
//    	branch = Branch.getInstance(getGameActivity(),"key_live_kkbKm4xMUHCrvMbMDgJnUphjyDf0tPVA");
    	branch = Branch.getInstance(getGameActivity().getApplicationContext());
		branch.initSession(new Branch.BranchReferralInitListener() {
			@Override
			public void onInitFinished(JSONObject referringParams, BranchError error) {
				if (error == null) {
					// params are the deep linked params associated with the link that the user clicked before showing up
					Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
					from_xpub_user_id = referringParams.optString("xpub_user_id", "");	
					from_scancode=referringParams.optString("scancode","");
				}
			}
		}, ((Activity)mContext).getIntent().getData(), (Activity)mContext);
    }
    
    public Branch getBranchInstance(){
    	if(branch != null) return branch;
    	else{
    		try {
				throw new Exception("branch is null,can't generate myShortUrl!!");
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
    		return null;
    	}
    }
    
    private String myShortUrl=null;
    public String getMyShortUrl(){
    	return myShortUrl;
    }
    
	public void onRestart(Context context) {
		if(closed) return;
//		onPause();
		Log.d(TAG, "onRestart SuspensionButton");
//		theInstance = null;
//		SuspensionButton.init(context, mAppId,nButtonSize);
//		SuspensionButton.getInstance().onUserInfoUpdate(mUid, mRoleId);
		
	}
	public void destroy() {
		if(accessTokenTracker != null) accessTokenTracker.stopTracking();
		if(profileTracker != null) profileTracker.stopTracking();
		if(FBUtil.isLogin()) FBUtil.callFacebookLogout();
		if(closed) return;
		onPause();
	}
	public void onResume() {		
		MobclickAgent.onResume(mContext);
		if(closed) return;
		Log.d(TAG,"onResume mFloatButtonStatus = "+mFloatButtonStatus);
		if(!(returnAndOpenWeb==null||returnAndOpenWeb.equals(""))){    //页面返回 是否跳转粉丝墙的判断
			mFloatButtonStatus = 1;
			theInstance.showWebview(returnAndOpenWeb);
			returnAndOpenWeb="";
			return;
		}
		if(mFloatButtonStatus > 0){
			mFloatButtonStatus = 0;
			showFloatButton();
			//从其他Activity（属于悬浮窗范围）回GameActivity
			Log.d(TAG,"从其他Activity（属于悬浮窗范围）回GameActivity");
			return;
		}	
		
		//从其他Activity（不属于悬浮窗范围）回GameActivity
		Log.d(TAG,"其他Activity（不属于悬浮窗范围）回GameActivity");
		if(!isAttached && isServerEnabled) {
			// 显示悬浮窗口 
			this.mWindowManager.addView(this, windowManagerParams); 
			this.isAttached = true;
			this.setVisibility(View.VISIBLE);
			suspensionButtonShow=true;
//			this.setAlphaDelay();
			didCompress=false;
			theMsgHandler.removeCallbacks(compressRunnable);
			this.setCompressDelay();//设置压缩图标
			if(!dotStatus.equals("")){
			dot.setVisibility(View.VISIBLE);
			}
			this.mWindowManager.addView(dot, nWindowManagerParams);//wx 回到activity添加小红点
		}
	}
	
	//宿主GameActivity.onCreate时调用，fbsession的生命周期 = 宿主生命周期
	public void onCreate(Bundle savedInstanceState){
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(mContext));
		//Integer.parseInt("asdfadsf");
		
		//Testin
		TestinAgent.init( mContext);
		
		// facebook 初始化
		FacebookSdk.sdkInitialize(mContext);
		getCallbackManager();
		getProfileTracker();
		getAccessTokenTracker();
		
		//初始化Helpshift 
		if(isHelpshiftShouldbeInited){
//			Helpshift.install(((Activity)mContext).getApplication(), Helpshift_apiKey, Helpshift_domainName, Helpshift_appId);
		}
		
		//umeng初始化
		AnalyticsConfig.setAppkey("555ed3b967e58e3ab00001cf");
		MobclickAgent.updateOnlineConfig( mContext );
		suspensionButtonShow=true;
		initDot();//小红点初始化
		
	}
	
	public void onPause() {
		MobclickAgent.onPause(mContext);
		if(closed) return;		
		if(mFloatButtonStatus == 0 || mFloatButtonStatus == -1){
			//去其他Activity（不属于悬浮窗范围）
			Log.d(TAG,"mFloatButtonStatus="+mFloatButtonStatus+" 从GameActivity去其他Activity（不属于悬浮窗范围）");
			if(isAttached) {
				isAttached = false;
		        // 销毁悬浮窗口  
				this.shrink();
				this.mWindowManager.removeView(this);
				this.mWindowManager.removeView(dot);//wx 暂停时消除小红点
			}
			
		}else if(mFloatButtonStatus > 0){
			Log.d(TAG,"mFloatButtonStatus="+mFloatButtonStatus+" 从GameActivity去其他Activity（悬浮窗范围）");
			//去其他Activity（悬浮窗范围）			
		}
		
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "requestCode is "+requestCode+"  Result Code is - " + resultCode );
//		switch(requestCode){
//		case REQUESTCODE_FLOATBUTTON_WEBVIEW_ACTIVITY:
//			this.setImageResource(R.drawable.floatbutton);
//			if(resultCode==RESULTCODE_GOTO_XPUBAWARD){
//				mFloatButtonStatus = 2;
//			}else if(resultCode==RESULTCODE_GOTO_XPUBINVITE){
//				mFloatButtonStatus = 1;
//				FBUtil.inviteFriends(inviteParams.getMsg());
//			}else if(resultCode==RESULTCODE_GOTO_XPUBSHARE){
//				mFloatButtonStatus = 1;
//				Intent intent = new Intent(mContext,SharePageActivity.class);
//				((Activity)mContext).startActivityForResult(intent,REQUESTCODE_FLOATBUTTON_SHARE_ACTIVITY);
//			}
//			break;
//		case REQUESTCODE_FLOATBUTTON_TABLEVIEW_ACTIVITY:
//			showFloatButton();
//			if (resultCode == RESULTCODE_GOTO_WEBVIEW) {
//				String url = data.getStringExtra("url");
//				returnAndOpenWeb = url;
//			}else if(resultCode==RESULTCODE_GOTO_XPUBSHARE){
//				mFloatButtonStatus=1;
//				Intent intent = new Intent(mContext,SharePageActivity.class);
//				((Activity)mContext).startActivityForResult(intent,REQUESTCODE_FLOATBUTTON_SHARE_ACTIVITY);
//			}else if(resultCode==RESULTCODE_GOTO_XPUBINVITE){
//				mFloatButtonStatus=1;
//				FBUtil.inviteFriends(inviteParams.getMsg());
//			}
//			break;
//	
//		case REQUESTCODE_FLOATBUTTON_SHARE_ACTIVITY:
//			showFloatButton();
//			if(resultCode==RESULTCODE_GOTO_FBPOST){
//				mFloatButtonStatus=1;
//				if(SuspensionButton.getInstance().shareParams != null ){
//					FBUtil.publishFeedDialog(SuspensionButton.getInstance().shareParams.getName(),SuspensionButton.getInstance().shareParams.getCaption(),SuspensionButton.getInstance().getMyShortUrl(),SuspensionButton.getInstance().shareParams.getDescription(),SuspensionButton.getInstance().shareParams.getPicture(),9,"");
//				}
//			}
//			break;
//			
//		case REQUESTCODE_FLOATBUTTON_NOTICEWINDOW_ACTIVITY:
//			showFloatButton();
//			if(resultCode == RESULTCODE_GOTO_WEBVIEW){
//				String url = data.getStringExtra("url");
//				returnAndOpenWeb = url;
//			}
//			
//		default:
//			break;	
//		}

		//fb callbackmanager 处理fb activity结果
		callbackManager.onActivityResult(requestCode, resultCode, data);
    }
	
	public void onSaveInstanceState(Bundle saveInstanceState) {
//		mFbLifeHelper.onSaveInstanceState(saveInstanceState);		
	}
	
	
	private static Handler theMsgHandler = new Handler() {  
        public void handleMessage(Message msg) {   
        	if(theInstance.closed) return;
            switch (msg.what) {   
                case 1:  
              	  	theInstance.onResume();
              	  	break; 
                case 2:  
					theInstance.destroy();
					theInstance.closed = true;
              	  	break;
                default:
                   break;
            }
        }   
    };
	//______________________________ OnTouchListener start ______________________________
	private boolean inDargging = false;
	private long touchDownTime = 0;
	private int statusBarHeight = 0;
	@SuppressLint("ClickableViewAccessibility")
	@Override 
	public boolean onTouchEvent(MotionEvent event) { 
		if(closed) return true;
		int touchType = event.getAction();
		if(isExpanded) {
			if(touchType == MotionEvent.ACTION_DOWN) {
				touchDownTime = System.currentTimeMillis();
			}
			else if((touchType == MotionEvent.ACTION_UP) && (System.currentTimeMillis() - touchDownTime <= 500)) {
				this.onClick();
			}
			return true;
		}
		inDargging = true;
		// 获取到状态栏的高度
		Rect frame = new Rect();
		getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY() - statusBarHeight; // statusBarHeight是系统状态栏的高度
		//Log.i("debug", "currX" + x + "====currY" + y);
		switch (touchType) {
		case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
			moveBefX=(int) event.getRawX();
//			canSetAlpha = false;
			if (didCompress) {
				theInstance.setImageResource(R.drawable.floatbutton);// 按下后将隐藏图片更换为正常图标
				mWindowManager.updateViewLayout(theInstance,
						windowManagerParams);// 大小改为正常
			}
			didCompress=false;
			touchDownTime = System.currentTimeMillis();
			// 获取相对View的坐标，即以此View左上角为原点
			mTouchX = event.getX();
			mTouchY = event.getY();
			mStartX = x;
			mStartY = y;
			//Log.i("debug", "mTouchX" + mTouchX + ", mTouchY" + mTouchY);
			//Log.i("debug", "mStartX" + mStartX + ", mStartY" + mStartY);
			if(mFloatButtonStatus!=1){
				setImageResource(R.drawable.floatbutton_click);
			}
			break;
		case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
			if (didCompress) {
				theInstance.setImageResource(R.drawable.floatbutton);// 按下后将隐藏图片更换为正常图标
				mWindowManager.updateViewLayout(theInstance,
						windowManagerParams);// 大小改为正常
			}
			didCompress=false;
			if (System.currentTimeMillis() - touchDownTime > 150) {
				updateViewPosition();
				//Log.d(TAG, "ACTION_MOVE");
			}
			break;
		case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
			inDargging = false;
			mTouchX = mTouchY = 0;
			moveAftX=(int) event.getRawX();
//			if((moveAftX<moveBefX||moveAftX<50)&&moveBefX>65){
//				this.adjustPostion(frame);
//				if(childDotDirection.equals("L")){
//					setCompressImmed();
//				}
//				if(childDotDirection.equals("R")){
//					
//				}
//				break;
//			}
			if(mFloatButtonStatus!=1){
				setImageResource(R.drawable.floatbutton);
			}
			if (System.currentTimeMillis() - touchDownTime <= 500 && Math.abs(x - mStartX) < 15 && Math.abs(y - mStartY) < 15) {
				Log.d(TAG,"mFloatButtonStatus="+mFloatButtonStatus);
				if(mFloatButtonStatus == 0) {
					this.onClick();
				} else {
					this.hideWebview();
				}
//				this.setAlphaDelay();
				this.setCompressDelay();
			} else {
				this.adjustPostion(frame);
				if (mFloatButtonStatus != 1) {
					if (childDotDirection.equals("R")) {
						if (moveAftX < moveBefX && moveBefX - moveAftX > 15/2*theInstance.density
								&& moveBefX < 200/2*theInstance.density) {
							setCompressImmed();
						}
					}
					if (childDotDirection.equals("L")) {
						if (moveAftX > moveBefX && moveAftX - moveBefX > 15/2*theInstance.density
								&& moveBefX > 300/2*theInstance.density) {
							setCompressImmed();
						}
					}
				}
			}
			break;
		}
		return true;
	} 

	private MyTranslateAnimation animTranslate = null;
	private void adjustPostion(Rect frame) {
		int width = frame.width();
		int height = frame.height();
		int statusBarHeight = frame.top;
		int distY = windowManagerParams.y;
		if(windowManagerParams.y < padding + statusBarHeight) {
			distY = padding;
		} else if(distY > height - padding) {
			distY = height - padding;
		}
		if(animTranslate == null) {
			animTranslate = new MyTranslateAnimation();
		}
		if(windowManagerParams.x * 2 < width) {
			//Log.d(TAG, "move to left");
			animTranslate.set(1000, windowManagerParams.x, 3, windowManagerParams.y, distY);
			compressX=3;compressY=distY;//压缩后的坐标
			judgePositionDot((int)(3),distY);//wx 小红点跟随悬浮窗
			childDotDirection="R";//wx 公告领奖小红点的方向判定
		} else {
			//Log.d(TAG, "move to right");
			animTranslate.set(1000, windowManagerParams.x, width - padding, windowManagerParams.y, distY);
			compressX=width-padding;compressY=distY;
			judgePositionDot((int)(width - padding),distY);//wx 小红点跟随悬浮窗
			childDotDirection="L"; //wx 公告领奖小红点的方向判定
		}
		animTranslate.startAnimation();
	}
	@Override 
    protected void onDraw(Canvas canvas) {
		if(closed ) return;
		super.onDraw(canvas);
		if(isAttached && !inDargging && animTranslate != null&&!didCompress) {
			animTranslate.tick();
//			theInstance.setCompressDelay();
		}
		//Log.d(TAG, "onDraw");
    }
	public boolean performClick() {
		//Log.d(TAG, "performClick");
		return super.performClick();
	}
	public boolean performLongClick() {
		//Log.d(TAG, "performLongClick");
		return super.performLongClick();
	}
	
	private void updateViewPosition() {
		// 更新浮动窗口位置参数 
		windowManagerParams.x = (int) (x - mTouchX); 
		windowManagerParams.y = (int) (y - mTouchY);
		mWindowManager.updateViewLayout(this, windowManagerParams); // 刷新显示 
		if(expandView != null) {
			mWindowManager.updateViewLayout(expandView, windowManagerParams);
		}
		if(dot!=null){
			positionDot();
		}
	} 

	//______________________________ OnTouchListener end ______________________________
	
	private ArrayList<ButtonInfo> btnList = null;
	class ButtonInfo {
//		name :按钮要显示的文字。 
//		logo :按钮要显示的图标。 
//		link :跳转链接。
//			其中 sys337:// 开头的链接代表内置功能。目前只定义一个sys337://close。表示 关闭悬浮窗口。
//			前端实现此方法即可。是否启用此按钮,由后端控制。
		String name;
		String logo;
		Bitmap logoImg;
		//BitmapDrawable drawable;
		String link;
		String message = "下次重新启动游戏悬浮窗口会再次打开, 确定现在隐藏掉？";
		String seq = "999999";//显示顺序，数字越大越靠后 默认的那个，是关闭按钮
		String status = "1";//status：0表示测试状态，1表示显示状态。当玩家uid在测试uid列表里时，测试状态的按钮才显示。 对于客户端，显示时做判断
		String extra = "";
		ImageButton btn;
		public ImageButton btnSplit;	
		boolean attached;
		
		public void init() {
			logoImg = null;
			btnSplit = null;
			btn = null;
			//drawable = null; 
			attached = false;
	        try {
	            URL url = new URL(logo);
	            logoImg = CacheImage.get(url);
	            if(logoImg == null){
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();
	            logoImg = CacheImage.set(url,input);
	            }
				btn = new ImageButton(theInstance.mContext);
				btn.setImageBitmap(logoImg);
				btn.setBackgroundColor(0);
				btn.setScaleType(ScaleType.FIT_XY);
				if(!link.equals("sys337://close")) {
					btnSplit = new ImageButton(theInstance.mContext);
					btnSplit.setBackgroundResource(R.drawable.floatbutton_split);
				}
				btn.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction()==MotionEvent.ACTION_DOWN){
							if(link != null) {
								SuspensionButton.getInstance().initButtonBg(link);
							}
						}
						if (event.getAction() == MotionEvent.ACTION_UP) {
							v.performClick();
							if(link != null) {
								if(link.equals("sys337://close")) {
									mFloatButtonStatus = -1;
									try {
										AlertDialog dlg = (new AlertDialog.Builder(theInstance.mContext)).create();
										dlg.setIcon(R.drawable.floatbutton);
										dlg.setMessage(message);
										dlg.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int whichButton) {
													// User clicked OK so do some stuff 
													theMsgHandler.sendEmptyMessage(2);
												}
											});
										dlg.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int whichButton) {
													// User clicked Cancel so do some stuff
													mFloatButtonStatus = 0;
													shrink();
												}
											});
										dlg.show();
									} catch (Exception e) {
									}
								}else if(link.startsWith("callback337") || link.startsWith("xpub")){
									mFloatButtonStatus = 2;
									//根据字符串执行注册的方法 fbinvite fbshare
									if(link.startsWith("callback337://fbinvite")){
											SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
											shrink();
//											MyFBUtil.inviteFriends(inviteParams.getMsg());
											showBoardActivity("1");
									}else if(link.startsWith("callback337://fbshare")){
											SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
											hideFloatButton();
											shrink();
											showBoardActivity("3");
//											FBUtil.publishFeedDialog(shareParams.getName(),shareParams.getCaption(),getMyShortUrl(),shareParams.getDescription(),shareParams.getPicture(),9,"");
									}else if(link.startsWith("callback337://fbfanswall")){
										SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
//										mFloatButtonStatus = 1;
										shrink();
										theInstance.showWebview(fansWallParams.getUrl());
									}else if(link.startsWith("xpub://award")){
										SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
										awardDotStatus=false;
										shrink();
										showBoardActivity("2");
//										SuspensionButton.getInstance().buttonBgIndex=0;
									}else if(link.startsWith("xpub://speedup")){
										MobclickAgent.onEvent(mContext, mAppId+"_speedup");
										SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
										shrink();
										BackgroundKiller bkb=new BackgroundKiller(SuspensionButton.getInstance().getGameActivity());
										bkb.killBackgroundProcess();
										mFloatButtonStatus = 0;

									}else if(link.startsWith("xpub://screenshot")){
										MobclickAgent.onEvent(mContext, mAppId+"_screenshot");
										SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
										shrink();
										mFloatButtonStatus=1;
										ScreenShoter ss=new ScreenShoter(SuspensionButton.getInstance().getGameActivity());
										ss.shot();
									}else if(link.startsWith("xpub://share")){
										SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
										shrink();
										mFloatButtonStatus=1;
										Intent intent = new Intent(SuspensionButton.getInstance().getGameActivity(),SharePageActivity.class);
										SuspensionButton.getInstance().getGameActivity().startActivityForResult(intent,REQUESTCODE_FLOATBUTTON_SHARE_ACTIVITY);
									}
									
								}
								else {
									SuspensionButton.getInstance().mWindowManager.removeView(SuspensionButton.getInstance().cBg);
//									mFloatButtonStatus = 1;
									if(link.startsWith("http://xpub.337.com/announce.php")){
										noticeDotStatus=false;
										shrink();
										showBoardActivity("5");
//										showNoticeWindow();
									}
//									theInstance.showWebview(link);
								}
							}
						}
						return true;
					}
				});
	        } catch (Exception e) {
	            Log.e("debug", e.getMessage());
	        }
	    }
		
		
		
		public void show(android.view.WindowManager.LayoutParams params) {
			if(btn != null) {
				btn.setPadding(0, 0, 0, 0);
				android.view.WindowManager.LayoutParams paramsSplit = null;
				if(btnSplit != null) {
					btnSplit.setVisibility(View.VISIBLE);
					paramsSplit = new android.view.WindowManager.LayoutParams();
					paramsSplit.copyFrom(params);
					paramsSplit.width = 2;
					paramsSplit.x = params.x + (int)(theInstance.density * nButtonSize);
				}
				if(attached) {
					theInstance.mWindowManager.updateViewLayout(btn, params);
					if(paramsSplit != null) {
						theInstance.mWindowManager.updateViewLayout(btnSplit, paramsSplit);
					}
				} else {
					attached = true;
					theInstance.mWindowManager.addView(btn, params);
					if(paramsSplit != null) {
						theInstance.mWindowManager.addView(btnSplit, paramsSplit);
					}
				}
				btn.setVisibility(View.VISIBLE);
			}
		}
	}

	class MyTranslateAnimation {
		private float startX;
		private float startY;
		private float endX;
		private float endY;
		private long startTime;
		private double duration;
		
		public MyTranslateAnimation() {
		}
		
		public void startAnimation() {
			if(inDargging) {
				return;
			}
			//Log.d(TAG, "startAnimation");
			startTime = System.currentTimeMillis();
			theInstance.invalidate();
		}
		
		public void tick() {
			if(inDargging) {
				return;
			}
			long now = System.currentTimeMillis();
			double interpolatedTime = 1.0f;
			if(now - startTime < duration) {
				interpolatedTime = (now - startTime) / duration;
			}
			boolean finish = apply(interpolatedTime);
			if(!finish && interpolatedTime < 1.0f) {
				theInstance.invalidate();
			} else {
				finish = true;
//				theInstance.setAlphaDelay();
				theMsgHandler.removeCallbacks(compressRunnable);
				theInstance.setCompressDelay();
			}
		}

		public void set(long time, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
			startX = fromXDelta;
			startY = fromYDelta;
			endX = toXDelta;
			endY = toYDelta;
			duration = time;
		}

		public boolean apply(double interpolatedTime) {
			//Log.d(TAG, "interpolatedTime," + interpolatedTime);
			boolean finish = true;
			double dx = endX;
			double dy = endY;
			if (Math.abs(startX - endX) <= 1) {
				finish = false;
				dx = startX + ((endX - startX) * interpolatedTime);
			}
			if (Math.abs(startY - endY) <= 1) {
				finish = false;
				dy = startY + ((endY - startY) * interpolatedTime);
			}
			// 更新浮动窗口位置参数 
			theInstance.windowManagerParams.x = (int) (dx); 
			theInstance.windowManagerParams.y = (int) (dy); 
			// 刷新显示 
			theInstance.mWindowManager.updateViewLayout(theInstance, theInstance.windowManagerParams);
//			theInstance.setCompressDelay();
			return finish;
		}
	}

	class MyWebViewClient extends WebViewClient {

		public MyWebViewClient() {
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// open browser when banner mode
			if(url != null && url.equals("sys337://close")) {
				theInstance.hideWebview();
				return true;
			}
			return false;
		}

		@Override
		public void onPageFinished(WebView view, final String url) {
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl) {
		}
	}
	
	public void onUserInfoUpdate(String uid,String roleid)
	{
		/**
		 * 通过webview打开网页。地址后需要传一些参数：		
		 * 		* uid ： 玩家uid
				* roleid ： 玩家角色id
				* source : 包名
				* mccmnc : 移动网络运营商代码
				* platform : 平台类型 ios或android
		 */
        if(uid == null || roleid == null){ Log.e(TAG,"uid or roleid is null.Will not show FloatButton !");return;}
        isUserInfoUpdated = true;
		mUid = uid;
		mRoleId = roleid;
		
		
		//上报app_start数据
		String pkgname = mContext.getApplicationContext().getPackageName() ;
		String deviceid = XpubUtils.getDeiviceId(mContext);
		List<NameValuePair> params=new LinkedList<NameValuePair>();
		params.clear();
		params.add(new BasicNameValuePair("mod", "app"));
		params.add(new BasicNameValuePair("act", "start"));
		params.add(new BasicNameValuePair("a", mAppId));
		params.add(new BasicNameValuePair("c", mUid));
		params.add(new BasicNameValuePair("d", mRoleId));
		params.add(new BasicNameValuePair("pkgname", pkgname));
		params.add(new BasicNameValuePair("deviceid", deviceid));
		Globals.getInstance().sendRequest(params);
		
		MobclickAgent.onEvent(mContext, mAppId+"_app_start");
		
    	
        
	}
	
	
	private boolean downloadCfg() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(mContext));
		//Integer.parseInt("asdfadsf");
		boolean ret = true;
		try {
			Log.i(TAG, "downloadCfg");
			StringBuilder sb = new StringBuilder();
			//http://pay.337.com/payphalcon/api/v1/onebutton?appid=274
			sb.append("http://pay.337.com/payphalcon/api/v1/onebutton?appid=").append(mAppId);
			HttpGet httpget = new HttpGet(sb.toString());
			HttpResponse response = new DefaultHttpClient().execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200)
			{
				/*
		         {
        status: 1,
        msg: "OK",
        data: {
            btns: [
                {
                    name: "储值",
                    logo: "http://337.eleximg.com/337/v3static/img/mobile/fwapi/fwpay.png",
                    url: "http://pay.337.com/payphalcon/page/recharge",
                    msg: "",
                    seq: "0",
                    status: "0"
                },
                {
                    name: "官网",
                    logo: "http://337.eleximg.com/337/v3static/img/mobile/fwapi/fwwebsite.png",
                    url: "http://web.337.com",
                    msg: "",
                    seq: "2",
                    status: "1"
                },
                {
                    name: "关闭",
                    logo: "http://png-4.findicons.com/files/icons/1715/gion/24/dialog_cancel.png",
                    url: "sys337://close",
                    msg: "確定要隱藏浮動按鈕嗎？重新登入遊戲后將再次開啟。",
                    seq: "9",
                    status: "0"
                },
                {
                    name: "切换帐号",
                    logo: "http://png.findicons.com/files/icons/42/basic/64/user.png",
                    url: "callback337://changeaccount",
                    msg: "是否要切换帐号",
                    seq: "3",
                    status: "0"
                },
                {
					name: "邀请",
					logo: "http://png.findicons.com/files/icons/2190/facebook/128/invite.png",
					url: "callback337://fbinvite",
					msg: "",
					seq: "2",
					status: "0"
				},
				{
					name: "分享",
					logo: "http://png.findicons.com/files/icons/2229/social_media_mini/48/share.png",
					url: "callback337://fbshare",
					msg: "",
					seq: "2",
					status: "0"
				}
            ],
            testuids: [
                "elex337_38170082",
                "elex337_38433870",
                "100004420417569",
                "elex337_43739159",
                "elex337_34138975",
                "elex337_45977493",
                "elex337_42891948"
            ]
        }
    }
		       */
			     String mStrCfgjson = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);			     
					if(mStrCfgjson != null && mStrCfgjson.length() > 0) {
						try {
							JSONObject mConfigJson = new JSONObject(mStrCfgjson);
							int status = mConfigJson.getInt("status");
							if(status == 1) {
								isServerEnabled = true;
								JSONObject dataItem = mConfigJson.getJSONObject("data");
								JSONArray btnsArray = dataItem.getJSONArray("btns"); 
								testuidsArray = dataItem.getJSONArray("testuids"); 
								ButtonInfo infoClose = null;
								btnList = new ArrayList<ButtonInfo>();
								Log.d(TAG,"length="+btnsArray.length());
								for(int i = 0; i < btnsArray.length(); ++i) {
									Object obj = btnsArray.get(i);
									if(obj instanceof JSONObject) {
										JSONObject btnInfo = (JSONObject)obj;
										ButtonInfo info = new ButtonInfo();
										info.name = btnInfo.optString("name", "");
										info.logo = btnInfo.optString("logo", "");
										info.link = btnInfo.optString("url", "");
										info.message = btnInfo.optString("msg", "");
										info.seq = btnInfo.optString("seq", "");
										info.status = btnInfo.optString("status", "");
										info.extra = btnInfo.optString("extra", "");
										info.init();
										if(info.link.startsWith("callback337://")){
											//functionArray.push(info.link);
											//游戏可以注册一个本地方法，点击按钮时，就调用这个方法。如果没有注册，则按钮不显示。
											//例如changeaccout
											btnList.add(info);
											//邀请和分享初始化参数
											if(info.link.startsWith("callback337://fbshare")){
												
												JSONObject inviteExtra = btnInfo.getJSONObject("extra");
												shareParams = new FBShareParam();
												shareParams.setName(inviteExtra.optString("name",""));
												shareParams.setCaption(inviteExtra.optString("caption",""));
												shareParams.setDescription(inviteExtra.optString("description",""));
												shareParams.setLink(inviteExtra.optString("link",""));
												shareParams.setPicture(inviteExtra.optString("picture",""));
												shareParams.setInviteMsg(inviteExtra.optString("inviteMsg",""));
												shareParams.setShareMsg(inviteExtra.optString("shareMsg", ""));
												shareParams.setShareIntroduce(inviteExtra.optString("shareIntroduce", ""));
												shareParams.setShareDescribe(inviteExtra.optString("shareDescribe", ""));
											}else if(info.link.startsWith("callback337://fbinvite")){
												JSONObject shareExtra = btnInfo.getJSONObject("extra");
												inviteParams = new FBInviteParam();
												inviteParams.setMsg(shareExtra.optString("message",""));
												inviteParams.setObjectId(shareExtra.optString("objectid",""));
												inviteParams.setActionType(shareExtra.optString("actiontype",""));
											}else if(info.link.startsWith("callback337://fbfanswall")){
												Log.d(TAG,"got fanswall config!");
												JSONObject fanswallExtra = btnInfo.getJSONObject("extra");
												Log.d(TAG,"fanswallExtra:"+fanswallExtra.toString());
												fansWallParams = new FBFansWallParam();
												fansWallParams.setPageid(fanswallExtra.optString("pageid",""));
												Log.d(TAG, "fanpageUrl="+fanswallExtra.optString("url",""));
												fansWallParams.setUrl(fanswallExtra.optString("url",""));
											}else if( info.link.startsWith("xpub://customparams")){
												Log.d(TAG,"got changeaccout config!url="+info.logo);
												mPostUrl=info.logo;
												try{
													int pNo = Integer.parseInt(info.seq);
													if (pNo > 0 ) mPulseCount = pNo ;
												}catch(NumberFormatException e){
													Log.e(TAG,"parse pulseCount error!! msg="+e.getMessage());
												}
											}else if(info.link.startsWith("callback337://noticeurl")){
												Log.d(TAG, "get notice url");
												mNoticeUrl=info.message;
											}
											
										}else if(info.link.equals("sys337://close")) {
											infoClose = info;
										}else {
											btnList.add(info);
										}
									}
								}
								if(infoClose != null) {
									// 最后添加关闭按钮
									btnList.add(infoClose);
								}
								//Sorting
								Collections.sort(btnList, new Comparator<ButtonInfo>() {
								        @Override
								        public int compare(ButtonInfo  a, ButtonInfo  b)
								        {
								            return  a.seq.compareTo(b.seq);
								        }
								    });

							}
						} catch (Exception e) {
							Log.e(TAG, "downloadCfg is fail, " + e.getMessage()+" "+e.getStackTrace());
						}
				}
			}else{
				ret=false;
			}
		} catch (Exception e) {
			isServerEnabled = false;
			e.printStackTrace();
			//访问后台配置出错，不显示悬浮按钮
			Log.e(TAG,"访问后台配置出错，不显示悬浮按钮 msg:"+e.getMessage());
			//访问后台配置失败（不显示悬浮窗）统计
    		MobclickAgent.onEvent(mContext, "loadcfg_fail");
			ret = false;
		}
		
		/**
		if(mFbLifeHelper != null){//邀请和分享初始化参数
			if(inviteParams != null && shareParams != null){ 
				Log.d(TAG,"邀请和分享初始化参数");
				mFbLifeHelper.init(inviteParams, shareParams);
			}
		}
		**/
		return ret;
	}
//////////////////////////////////////////////////////////////////////////////////	
	//fb的实例，由于拿不到GameActivity这个单例本身，只好放在悬浮窗单例类，
	protected CallbackManager callbackManager;
	public CallbackManager getCallbackManager(){
		if(callbackManager == null){
			callbackManager = CallbackManager.Factory.create();
			LoginManager.getInstance().registerCallback(callbackManager,
		            new FacebookCallback<LoginResult>() {
		                @Override
		                public void onSuccess(LoginResult loginResult) {
		                	//登录统计
			        		MobclickAgent.onEvent(mContext, mAppId+"_fb_login");
		                	//已测试无法在这里获得getCurrentProfile
	                		Log.i(TAG, "fb login success");
	                	}

		                @Override
		                public void onCancel() {
		                }

		                @Override
		                public void onError(FacebookException exception) {
		                }
		    });
		}
		return callbackManager;
	}
	
	
	protected ProfileTracker profileTracker;
	public ProfileTracker getProfileTracker(){
		if(profileTracker == null){
			profileTracker = new ProfileTracker() {
		        @Override
		        protected void onCurrentProfileChanged(
		                Profile oldProfile,
		                Profile currentProfile) {
		        	if(currentProfile == null) return;
		        		Log.d(TAG, "fb onCurrentProfileChanged id " + currentProfile.getId());
		        		Log.d(TAG, "fb onCurrentProfileChanged name " + currentProfile.getName());
		        		mFbId = currentProfile.getId();
		        		mListener.onFbProfileTrack(oldProfile, currentProfile);
		        		Log.d(TAG, "fb change FBUtil.preRequest="+FBUtil.preRequest);
		        		Log.d(TAG, "fb onCurrentProfileChanged FBUtil.preRequest="+FBUtil.preRequest);
		        		int requestType = FBUtil.preRequest;
		        		Log.d(TAG, "fb onCurrentProfileChanged requestType="+requestType);
//		        		if(requestType==5){
//		        			Log.d(TAG, "fb onCurrentProfileChanged execute feed");
//		        	    	if(FBUtil.feedType==1 && !AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")){
//		        	    		FBUtil.feedType = 0;
//		        	    	}
//		        			FBUtil.rePublishFeedDialog();
//		        		}else if(requestType == 11){
////		        			showBoardActivity("1","");
//		        		}
		        }
		    };
		    profileTracker.startTracking();
		}
		FBUtil.preRequest = 0;
		return profileTracker;
	}
	
	protected AccessTokenTracker accessTokenTracker;
	public AccessTokenTracker getAccessTokenTracker(){
		if(accessTokenTracker == null){
	    accessTokenTracker = new AccessTokenTracker() {
	         @Override
	         protected void onCurrentAccessTokenChanged(
	                 AccessToken oldAccessToken,
	                 AccessToken currentAccessToken) {
	             // App code
	        	 Log.d(TAG,"The old AccessToken is "+oldAccessToken);
	        	 Log.d(TAG,"The current AccessToken is "+currentAccessToken);
	        	 if(currentAccessToken == null) return;
	        	 mAccessToken=currentAccessToken.getToken();
	        	 mListener.onFbAccessTokenTrack(oldAccessToken, currentAccessToken);
	         }
	    };
		}
		return accessTokenTracker;
	}
}

