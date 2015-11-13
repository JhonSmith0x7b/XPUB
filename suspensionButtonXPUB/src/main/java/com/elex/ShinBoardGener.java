package com.elex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.elex.suspension.R;
import com.elex.util.Globals;
import com.elex.util.MyFBUtil;
import com.facebook.FacebookException;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ShinBoardGener {
	private static ShinBoardGener sInstance;
	private Context mContext;
	private DisplayMetrics mDm;
	private FragmentManager mFm;
	private AwardFragment mAwardF;
	private InviteFragment mInviteF;
	private SharePageFragment mSharePageF;
	private WebViewFragment mWebViewF;
	private RelativeLayout mRl;
	private LayoutInflater mInflater;
	private String mInviteMsg;
	public String mUrl;
	private String mAwardProcess;
	private ImageView rewardFButtonImage;
	private TextView rewardFButtonText;
	private ImageView rewardFButtonArea;
	private ImageView inviteFButtonImage;
	private TextView inviteFButtonText;
	private ImageView inviteFButtonArea;
	private ImageView shareFButtonImage;
	private TextView shareFButtonText;
	private ImageView loadingImage;
	private ImageView shareFButtonArea;
	private ImageView noticeFButtonImage;
	private TextView noticeFButtonText;
	private ImageView noticeFButtonArea;
	public static ShinBoardGener getInstance(){
		return sInstance;
	}
	public Activity getActivity(){
		if(mContext!=null){
			return (Activity)mContext;
		}else{
			return null;
		}
	}
	public ShinBoardGener(Context context,FragmentManager fm,String url){
		this.mContext=context;
		this.mDm=mContext.getResources().getDisplayMetrics();
		if(fm==null){
			mFm = ((FragmentActivity)mContext).getSupportFragmentManager();
		}else{
			this.mFm=fm;
		}
		this.mInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mUrl=url;
		mRl=(RelativeLayout) ((Activity)mContext).findViewById(R.id.rlShinBoard);
	}
	public Fragment getInviteF(){
		if(this.mInviteF==null){
			this.mInviteF=new InviteFragment(mContext);
		}
		return this.mInviteF;
	}
	public Fragment getAwardF(){
		if(this.mAwardF==null){
			this.mAwardF=new AwardFragment(mContext,mAwardProcess);
		}
		return this.mAwardF;
	}
	public Fragment getSharePageF(){
		if(this.mSharePageF==null){
			this.mSharePageF=new SharePageFragment(mContext);
		}
		return this.mSharePageF;
	}
	public static void init(Context context,FragmentManager fm,String type,String url){
		sInstance=new ShinBoardGener(context,fm,url);
		sInstance.setPortrait(type);
	}
	private void setPortrait(String type){
		try{
		sInstance.setMain();
		sInstance.showBoard(type);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "ShinBoardGener error");
			Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
			((Activity)mContext).finish();
		}
	}
	private void setMain(){
		inviteFButtonImage = (ImageView) mRl.findViewById(R.id.inviteFragmentButtonImage);
		RelativeLayout.LayoutParams rlParams=(LayoutParams) inviteFButtonImage.getLayoutParams();
		rlParams.leftMargin=mDm.widthPixels/5-rlParams.width/2;
		inviteFButtonArea=(ImageView)mRl.findViewById(R.id.inviteFragmentButtonArea);
		RelativeLayout.LayoutParams rlParams6=(LayoutParams) inviteFButtonArea.getLayoutParams();
		rlParams6.leftMargin=mDm.widthPixels/5-rlParams6.width/2;
		inviteFButtonArea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				inviteFButtonImage.startAnimation(buttonClickT);
				showBoard("1");
			}
			
		});
		inviteFButtonText=(TextView) mRl.findViewById(R.id.inviteFragmentButtonText);
		RelativeLayout.LayoutParams rlParams1=(LayoutParams) inviteFButtonText.getLayoutParams();
		rlParams1.leftMargin=mDm.widthPixels/5-rlParams1.width/2;
		rewardFButtonImage=(ImageView) mRl.findViewById(R.id.rewardFragmentButtonImage);
		RelativeLayout.LayoutParams rlParams2=(LayoutParams) rewardFButtonImage.getLayoutParams();
		rlParams2.leftMargin=mDm.widthPixels/5*2 -rlParams2.width/2;
		rewardFButtonArea=(ImageView)mRl.findViewById(R.id.rewardFragmentButtonArea);
		RelativeLayout.LayoutParams rlParams7=(LayoutParams) rewardFButtonArea.getLayoutParams();
		rlParams7.leftMargin=mDm.widthPixels/5*2-rlParams7.width/2;
		rewardFButtonArea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				rewardFButtonImage.startAnimation(buttonClickT);
				showBoard("2");
			}
			
		});
		rewardFButtonText=(TextView) mRl.findViewById(R.id.rewardFragmentButtonText);
		RelativeLayout.LayoutParams rlParams3=(LayoutParams) rewardFButtonText.getLayoutParams();
		rlParams3.leftMargin=mDm.widthPixels/5*2 -rlParams3.width/2;
		shareFButtonImage=(ImageView) mRl.findViewById(R.id.shareFragmentButtonImage);
		RelativeLayout.LayoutParams rlParams4 = (LayoutParams) shareFButtonImage.getLayoutParams();
		rlParams4.leftMargin=mDm.widthPixels/5*3 -rlParams4.width/2;
		shareFButtonArea=(ImageView)mRl.findViewById(R.id.shareFragmentButtonArea);
		RelativeLayout.LayoutParams rlParams8=(LayoutParams) shareFButtonArea.getLayoutParams();
		rlParams8.leftMargin=mDm.widthPixels/5*3-rlParams8.width/2;
		shareFButtonArea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				shareFButtonImage.startAnimation(buttonClickT);
				showBoard("3");
			}
			
		});
		shareFButtonText=(TextView)mRl.findViewById(R.id.shareFragmentButtonText);
		RelativeLayout.LayoutParams rlParams5=(LayoutParams) shareFButtonText.getLayoutParams();
		rlParams5.leftMargin=mDm.widthPixels/5*3 -rlParams5.width/2;
		noticeFButtonImage=(ImageView)mRl.findViewById(R.id.noticeFragmentButtonImage);
		RelativeLayout.LayoutParams rlParmas9=(LayoutParams) noticeFButtonImage.getLayoutParams();
		rlParmas9.leftMargin=mDm.widthPixels/5*4-rlParmas9.width/2;
		noticeFButtonText=(TextView)mRl.findViewById(R.id.noticeFragmentButtonText);
		RelativeLayout.LayoutParams rlParams10=(LayoutParams) noticeFButtonText.getLayoutParams();
		rlParams10.leftMargin=mDm.widthPixels/5*4-rlParams10.width/2;
		noticeFButtonArea=(ImageView)mRl.findViewById(R.id.noticeFragmentButtonArea);
		RelativeLayout.LayoutParams rlParams11=(LayoutParams) noticeFButtonArea.getLayoutParams();
		rlParams11.leftMargin=mDm.widthPixels/5*4-rlParams11.width/2;
		noticeFButtonArea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				noticeFButtonImage.startAnimation(buttonClickT);
				showBoard("5");
			}
			
		});
	}
	public void showBoard(String type){
		if(type!=null&&type.equals("1")){
			addLoadingImage();
			int result =MyFBUtil.inviteFriends("");
			if(result==0){
				removeLoadingImage();
				Toast.makeText(mContext, mContext.getResources().getString(R.string.facebooknologin), Toast.LENGTH_SHORT).show();
			}
		}else if(type!=null&&type.equals("2")){
			MobclickAgent.onEvent(mContext, SuspensionButton.mAppId+"_rewardShow");
			addLoadingImage();
			getUserTasks();
		}else if(type!=null&&type.equals("3")){
			MobclickAgent.onEvent(mContext, SuspensionButton.mAppId+"_sharePageShow");
			showSharePageF();
		}else if(type!=null&&type.equals("4")){
			MobclickAgent.onEvent(mContext, SuspensionButton.mAppId+"_webViewShow");
			showWebViewF();
		}else if(type!=null&&type.equals("5")){
			MobclickAgent.onEvent(mContext, SuspensionButton.mAppId+"_noticeShow");
			showNoticeWindowF();
		}
	}
	public void showInviteF(){
		try{
		MobclickAgent.onEvent(mContext, SuspensionButton.mAppId+"_inviteShow");
		mInviteF=new InviteFragment(mContext);
        FragmentTransaction transaction = mFm.beginTransaction();
        transaction.replace(R.id.frameLayout, mInviteF);
        transaction.commit();
        inviteFButtonImage.getBackground().setColorFilter(0xFF73A1FF, PorterDuff.Mode.SRC_IN);
		inviteFButtonText.setTextColor(0xFF73A1FF);
		rewardFButtonText.setTextColor(0xFF778899);
		rewardFButtonImage.setBackgroundResource(R.drawable.noticeboard_rewardbutton);
		rewardFButtonImage.getBackground().setColorFilter(null);
		shareFButtonText.setTextColor(0xFF778899);
		shareFButtonImage.setBackgroundResource(R.drawable.noticeboard_sharebutton);
		shareFButtonImage.getBackground().setColorFilter(null);
		noticeFButtonText.setTextColor(0xFF778899);
		noticeFButtonImage.setBackgroundResource(R.drawable.noticeboard_noticebutton);
		noticeFButtonImage.getBackground().setColorFilter(null);
        removeLoadingImage();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "ShinBoardGener showInivteF ERROR");
		}
	}
	private void showAwardF(String awardProcess){
		try{
		this.mAwardProcess=awardProcess;
		mAwardF=new AwardFragment(mContext,mAwardProcess);
		FragmentTransaction transaction=mFm.beginTransaction();
		transaction.replace(R.id.frameLayout, mAwardF);
		transaction.commit();
		rewardFButtonImage.getBackground().setColorFilter(0xFF73A1FF, PorterDuff.Mode.SRC_IN);
		rewardFButtonText.setTextColor(0xFF73A1FF);
		inviteFButtonText.setTextColor(0xFF778899);
		inviteFButtonImage.setBackgroundResource(R.drawable.noticeboard_invitebutton);
		inviteFButtonImage.getBackground().setColorFilter(null);
		shareFButtonText.setTextColor(0xFF778899);
		shareFButtonImage.setBackgroundResource(R.drawable.noticeboard_sharebutton);
		shareFButtonImage.getBackground().setColorFilter(null);
		noticeFButtonText.setTextColor(0xFF778899);
		noticeFButtonImage.setBackgroundResource(R.drawable.noticeboard_noticebutton);
		noticeFButtonImage.getBackground().setColorFilter(null);
		removeLoadingImage();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "ShinBoardGener showAwardF ERROR");
		}
	}
	private void showSharePageF(){
		try{
		mSharePageF=new SharePageFragment(mContext);
		FragmentTransaction transaction=mFm.beginTransaction();
		transaction.replace(R.id.frameLayout, mSharePageF);
		shareFButtonImage.getBackground().setColorFilter(0xFF73A1FF, PorterDuff.Mode.SRC_IN);
		shareFButtonText.setTextColor(0xFF73A1FF);
		inviteFButtonText.setTextColor(0xFF778899);
		inviteFButtonImage.setBackgroundResource(R.drawable.noticeboard_invitebutton);
		inviteFButtonImage.getBackground().setColorFilter(null);
		rewardFButtonText.setTextColor(0xFF778899);
		rewardFButtonImage.setBackgroundResource(R.drawable.noticeboard_rewardbutton);
		rewardFButtonImage.getBackground().setColorFilter(null);
		noticeFButtonText.setTextColor(0xFF778899);
		noticeFButtonImage.setBackgroundResource(R.drawable.noticeboard_noticebutton);
		noticeFButtonImage.getBackground().setColorFilter(null);
		transaction.commit();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "ShinBoardGener showSharePageF ERROR");
		}
	}
	private void showWebViewF(){
		try{
		mWebViewF=new WebViewFragment(mContext,mUrl);
		FragmentTransaction transaction=mFm.beginTransaction();
		transaction.replace(R.id.frameLayout, mWebViewF);
		shareFButtonImage.getBackground().setColorFilter(null);
		shareFButtonText.setTextColor(0xFF778899);
		shareFButtonImage.setBackgroundResource(R.drawable.noticeboard_sharebutton);
		inviteFButtonText.setTextColor(0xFF778899);
		inviteFButtonImage.setBackgroundResource(R.drawable.noticeboard_invitebutton);
		inviteFButtonImage.getBackground().setColorFilter(null);
		rewardFButtonText.setTextColor(0xFF778899);
		rewardFButtonImage.setBackgroundResource(R.drawable.noticeboard_rewardbutton);
		rewardFButtonImage.getBackground().setColorFilter(null);
		noticeFButtonText.setTextColor(0xFF778899);
		noticeFButtonImage.setBackgroundResource(R.drawable.noticeboard_noticebutton);
		noticeFButtonImage.getBackground().setColorFilter(null);
		transaction.commit();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "ShinBoardGener showWebViewF ERROR");
		}
	}
	private void showNoticeWindowF(){
		try{
		mUrl=SuspensionButton.getInstance().mNoticeUrl;
		mWebViewF=new WebViewFragment(mContext,mUrl);
		FragmentTransaction transaction=mFm.beginTransaction();
		transaction.replace(R.id.frameLayout, mWebViewF);
		noticeFButtonImage.getBackground().setColorFilter(0xFF73A1FF, PorterDuff.Mode.SRC_IN);
		noticeFButtonText.setTextColor(0xFF73A1FF);
		shareFButtonImage.getBackground().setColorFilter(null);
		shareFButtonText.setTextColor(0xFF778899);
		shareFButtonImage.setBackgroundResource(R.drawable.noticeboard_sharebutton);
		inviteFButtonText.setTextColor(0xFF778899);
		inviteFButtonImage.setBackgroundResource(R.drawable.noticeboard_invitebutton);
		inviteFButtonImage.getBackground().setColorFilter(null);
		rewardFButtonText.setTextColor(0xFF778899);
		rewardFButtonImage.setBackgroundResource(R.drawable.noticeboard_rewardbutton);
		rewardFButtonImage.getBackground().setColorFilter(null);
		transaction.commit();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(SuspensionButton.TAG, "ShinBoardGener showNoticeWindowF ERROR");
		}
	}

	private void getUserTasks() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("a", SuspensionButton.mAppId));
		params.add(new BasicNameValuePair("c", SuspensionButton.mUid));
		params.add(new BasicNameValuePair("d", SuspensionButton.mRoleId));
		params.add(new BasicNameValuePair("mod", "user"));
		params.add(new BasicNameValuePair("act", "tasks"));
		Globals.getInstance().sendRequest(params, new onGetUserTasks(),
				new onGetUserTasksError());

	}
	public void onFbInviteCallback(GameRequestDialog.Result result, FacebookException error){
    	
    	if (error == null && result!=null) {	
			final String requestId = result.getRequestId();
			List<NameValuePair> params=new LinkedList<NameValuePair>();
			params.clear();
			params.add(new BasicNameValuePair("mod", "fb"));
			params.add(new BasicNameValuePair("act", "invite"));
			params.add(new BasicNameValuePair("a", SuspensionButton.mAppId)); 
			params.add(new BasicNameValuePair("b", SuspensionButton.mFbId));
			params.add(new BasicNameValuePair("r", requestId));
			params.add(new BasicNameValuePair("t", String.valueOf(SuspensionButton.mToFBIds)));
    		Globals.getInstance().sendRequest(params);
    		SuspensionButton.mToFBIds=0;//邀请人数，上报后清0
    		
    		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_fb_invite");
		} 
    }
	public void onFbShareCallback(ShareDialog.Result result, FacebookException error){
		ShinBoardGener.getInstance().removeLoadingImage();
    	if (error == null && result != null) {// When the story is posted, echo the success and the post Id.
			String postId = result.getPostId();
			if(postId == null){
				//发送成功 webdialog能成功返回postid，但sdk返回空得postid 待解决
				//对这种情况，暂时生成postId。    
				postId=SuspensionButton.mFbId+"_"+System.currentTimeMillis();   						
			}
			if (postId != null) {
				List<NameValuePair> params=new LinkedList<NameValuePair>();
				params.clear();
				params.add(new BasicNameValuePair("mod", "fb"));
				params.add(new BasicNameValuePair("act", "share"));
				params.add(new BasicNameValuePair("a", SuspensionButton.mAppId)); 
				params.add(new BasicNameValuePair("b", SuspensionButton.mFbId));
				params.add(new BasicNameValuePair("p", postId)); 
        		Globals.getInstance().sendRequest(params);
        		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_fb_post");
			} 
    	}
    }

	private class onGetUserTasks implements CallBackFunciton {
		public void call(String json) {
			showAwardF(json);
		}
	}

	private class onGetUserTasksError implements CallBackFunciton {
		@Override
		public void call(String json) {
			SuspensionButton.getInstance().removeLoading();
			Toast.makeText(
					mContext,
					mContext.getResources()
							.getString(R.string.noticeboard_questgeterror),
					Toast.LENGTH_SHORT).show();
			showAwardF("");
		}

	}
	private AlphaAnimation buttonClick = new AlphaAnimation(1F,0.1F);
	private AlphaAnimation buttonClickT = new AlphaAnimation(1F,0.3F);
	public void addLoadingImage(){
		loadingImage=(ImageView) mRl.findViewById(R.id.loadingImage);
		Bitmap bmOriginal=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notice_loading);
		Bitmap bm=Bitmap.createScaledBitmap(bmOriginal, 250*(int)mDm.density, 250*(int)mDm.density, false);
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
	public void removeLoadingImage(){
		loadingImage.setAnimation(null);
		loadingImage.setVisibility(View.INVISIBLE);
	}
}
