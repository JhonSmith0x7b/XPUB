package com.elex;

import com.elex.suspension.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class WebViewFragment extends Fragment{
	RelativeLayout mRl;
	DisplayMetrics mDm;
	Button aboveButton;
	WebView webView;
	View contentView;
	private String mUrl="";
	public WebViewFragment(Context context,String url){
		this.mUrl=url;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView=(RelativeLayout) inflater.inflate(R.layout.webview_main_portrait, null);
		mDm=getActivity().getResources().getDisplayMetrics();
		mRl=(RelativeLayout) contentView.findViewById(R.id.rlWebView);
		setMain();
		return contentView;
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void setMain(){
		aboveButton=(Button) mRl.findViewById(R.id.aboveButton);
		RelativeLayout.LayoutParams rlParams=(LayoutParams) aboveButton.getLayoutParams();
		rlParams.width=mDm.widthPixels;
		rlParams.height=mDm.heightPixels/10;
		aboveButton.setLayoutParams(rlParams);
		aboveButton.getBackground().setColorFilter(0xFF73A1FF,PorterDuff.Mode.MULTIPLY);
		aboveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		webView=(WebView) mRl.findViewById(R.id.webViewContent);
		RelativeLayout.LayoutParams rlParams1=(LayoutParams)webView.getLayoutParams();
		rlParams1.width=mDm.widthPixels;
		rlParams1.height=mDm.heightPixels/10*9-(int)mDm.density*60;
		webView.setFocusable(true);
		webView.setFocusableInTouchMode(true);
		webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		WebSettings webSettings=webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		 webSettings.setUseWideViewPort(true);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(mUrl);
	}
	
}
