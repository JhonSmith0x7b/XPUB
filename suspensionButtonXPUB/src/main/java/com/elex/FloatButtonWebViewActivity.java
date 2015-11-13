package com.elex;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FloatButtonWebViewActivity extends Activity {
	private WebView mWebView = null;
	private String mUrl=null;
	private ValueCallback<Uri> mUploadMessage;  
	private final static int FILECHOOSER_RESULTCODE=1;  

	 @Override  
	 protected void onActivityResult(int requestCode, int resultCode,  
	                                    Intent intent) {  
	  if(requestCode==FILECHOOSER_RESULTCODE)  
	  {  
	   if (null == mUploadMessage) return;  
	            Uri result = intent == null || resultCode != RESULT_OK ? null  
	                    : intent.getData();  
	            mUploadMessage.onReceiveValue(result);  
	            mUploadMessage = null;  
	  }
	  } 

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override  
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));
		Intent intent=getIntent();  
		mUrl=intent.getStringExtra("url");
        if(mWebView == null) {
			mWebView = new WebView(this);
			mWebView.setFocusable(true);
			mWebView.setFocusableInTouchMode(true);
			mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
			mWebView.setWebChromeClient(new WebChromeClient()  
		    {  
		           //The undocumented magic method override  
		           //Eclipse will swear at you if you try to put @Override here  
		        // For Android 3.0+
		        @SuppressWarnings("unused")
				public void openFileChooser(final ValueCallback<Uri> uploadMsg) {  

		            mUploadMessage = uploadMsg;  
		            Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
		            i.addCategory(Intent.CATEGORY_OPENABLE);  
		            i.setType("image/*");  
		            FloatButtonWebViewActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);  

		           }

		        // For Android 3.0+
		           @SuppressWarnings("unused")
				public void openFileChooser( final ValueCallback<Uri> uploadMsg, final String acceptType ) {
		           mUploadMessage = uploadMsg;
		           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		           i.addCategory(Intent.CATEGORY_OPENABLE);
		           i.setType("*/*");
		           FloatButtonWebViewActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"),FILECHOOSER_RESULTCODE);
		           }

		        //For Android 4.1
		           @SuppressWarnings("unused")
				public void openFileChooser(final ValueCallback<Uri> uploadMsg, final String acceptType, final String capture){
		               mUploadMessage = uploadMsg;  
		               Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
		               i.addCategory(Intent.CATEGORY_OPENABLE);  
		               i.setType("image/*");  
		               FloatButtonWebViewActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );

		           }

		    });
			mWebView.setWebViewClient(new WebViewClient());
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setSupportZoom(false);
			if(Build.VERSION.SDK_INT < 8){
//				webSettings.setPluginsEnabled(true);//(target 从4.2.2改到4.4.2时要注释掉这一行)
//				webSettings.setPluginState(PluginState.ON);
			}else{
				webSettings.setPluginState(PluginState.ON);
			}
			webSettings.setJavaScriptEnabled(true);
			//parentView = (ViewGroup)(findViewById(android.R.id.content));
			//parentView.addView(mWebView,new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.NO_GRAVITY));
			setContentView(mWebView);
		}
        mWebView.addJavascriptInterface(new JsObject(), "xpubObj");//"javascript:alert(xpubObj.getInfo())"
		mWebView.loadUrl(mUrl);
	}
	 class JsObject {
		    @JavascriptInterface
		    public String getInfo() { 
		    	if(SuspensionButton.getInstance() == null 
		    			|| SuspensionButton.getInstance().closed 
		    			|| SuspensionButton.mUid == null
		    			|| SuspensionButton.mRoleId == null
		    			|| SuspensionButton.mAppId == null
		    			){
		    		return "{\"err\":\"您还未登录facebook，请在悬浮窗点击邀请或分享登录facebook后再来!!\"}";
		    	}
		    	return "{"+
		    			"\"uid\":\""+SuspensionButton.mUid+"\"," +
		    			"\"roleid\":\""+SuspensionButton.mRoleId+"\"," +
		    			"\"appid\":\""+SuspensionButton.mAppId+"\"," +
		    			"}"; 
		    	}
		    public void doXpubAction(String xpubAction){
		    	if(xpubAction.startsWith("xpub://award")){
		    		Intent intent =new Intent();
		    		setResult(SuspensionButton.RESULTCODE_GOTO_XPUBAWARD,intent);
		    		finish();
		    	}
		    	if(xpubAction.startsWith("xpub://invite")){
		    		Intent intent =new Intent();
		    		setResult(SuspensionButton.RESULTCODE_GOTO_XPUBINVITE,intent);
		    		finish();
		    	}
		    	if(xpubAction.startsWith("xpub://share")){
		    		Intent intent =new Intent();
		    		setResult(SuspensionButton.RESULTCODE_GOTO_XPUBSHARE,intent);
		    		finish();
		    	}
		    }
		 }
	
	class MyWebViewClient extends WebViewClient {

		@Override
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        // TODO Auto-generated method stub
	        super.onPageStarted(view, url, favicon);
	    }

	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        // TODO Auto-generated method stub

	        mWebView.loadUrl(url);
	        return true;

	    }

	    @Override
	    public void onPageFinished(WebView view, String url) {
	        // TODO Auto-generated method stub
	        super.onPageFinished(view, url);
	    }
	}
	
}
