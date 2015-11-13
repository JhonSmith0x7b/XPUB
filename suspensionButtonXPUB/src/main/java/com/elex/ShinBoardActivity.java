package com.elex;

import com.elex.suspension.R;
import com.elex.util.FBUtil;
import com.elex.util.MyFBUtil;
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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ShinBoardActivity extends FragmentActivity{
	private FragmentManager fm;
	private AccessTokenTracker accessTokenTracker;
	private AccessToken accessToken;
	private ProfileTracker profileTracker;
	private static CallbackManager callbackManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		setLoginCallbackManger();
//		setProfileTracker();
//		setAccessTokenTracker();
		setContentView(R.layout.shinnboard_portrait);
		fm=getSupportFragmentManager();
		Intent intent = getIntent();
		String type="4";
		String url="http://www.facebook.com";
		String msg =intent.getStringExtra("type");
		if(msg!=null&&!msg.equals("")){
			type=msg;
			if(type.equals("4")){
				String inUrl=intent.getStringExtra("url");
				if(inUrl!=null&&!inUrl.equals("")){
					url=inUrl;
				}
			}
		}
		ShinBoardGener.init(ShinBoardActivity.this,fm,type,url);
	}
	public static CallbackManager getCallbackManager(){
		if(callbackManager==null){
			callbackManager=CallbackManager.Factory.create();
		}
		return callbackManager;
	}
//	private ProfileTracker setProfileTracker(){
//		if(profileTracker == null){
//			profileTracker = new ProfileTracker() {
//		        @Override
//		        protected void onCurrentProfileChanged(
//		                Profile oldProfile,
//		                Profile currentProfile) {
//		        	if(currentProfile == null) return;
//		        		Log.d(SuspensionButton.TAG, "fb onCurrentProfileChanged id " + currentProfile.getId());
//		        		Log.d(SuspensionButton.TAG, "fb onCurrentProfileChanged name " + currentProfile.getName());
//		        		SuspensionButton.mFbId = currentProfile.getId();
//		        }
//		    };
//		    profileTracker.startTracking();
//		}
//		return profileTracker;
//	}
//	private AccessTokenTracker setAccessTokenTracker(){
//		if(accessTokenTracker == null){
//		    accessTokenTracker = new AccessTokenTracker() {
//		         @Override
//		         protected void onCurrentAccessTokenChanged(
//		                 AccessToken oldAccessToken,
//		                 AccessToken currentAccessToken) {
//		             // App code
//		        	 Log.d(SuspensionButton.TAG,"The old AccessToken is "+oldAccessToken);
//		        	 Log.d(SuspensionButton.TAG,"The current AccessToken is "+currentAccessToken);
//		        	 if(currentAccessToken == null) return;
//		        	 SuspensionButton.mAccessToken=currentAccessToken.getToken();
//		         }
//		    };
//			}
//			return accessTokenTracker;
//	}
	private void setLoginCallbackManger(){
		LoginManager.getInstance().registerCallback(getCallbackManager(), new FacebookCallback<LoginResult>(){

			@Override
			public void onSuccess(LoginResult result) {
				Toast.makeText(ShinBoardActivity.this, getResources().getString(R.string.loginresultsuccess), Toast.LENGTH_SHORT).show();
				if(MyFBUtil.preLoginRquest==1){
					ShinBoardGener.getInstance().showBoard("1");
				}
			}

			@Override
			public void onCancel() {
				Toast.makeText(ShinBoardActivity.this, getResources().getString(R.string.loginresultcancel), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(FacebookException error) {
				Toast.makeText(ShinBoardActivity.this, getResources().getString(R.string.loginresulterror), Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getCallbackManager().onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
