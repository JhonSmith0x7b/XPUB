package com.elex.popupdemo;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.elex.BackgroundKiller;
import com.elex.ScreenShoter;
import com.elex.SharePageActivity;
import com.elex.SuspensionButton;
import com.elex.util.FBUtil;
import com.elex.util.XpubUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Profile;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;

public class MainActivity extends Activity implements SuspensionButton.FloatingButtonListener{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//	     for generate KeyHash
	   try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.elex.popupdemo", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    } 
	    
        
        //fb init
        //FacebookSdk.sdkInitialize(this.getApplicationContext());
        Activity testParam = null;
        Log.d("debug", "onCreate");
        setContentView(R.layout.activity_main); 
		final String deviceID=XpubUtils.getDeiviceId(this);
		View loginButton =  findViewById(R.id.LoginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				onLoginCallback("elex337_38170082","234");
			    //在能够获得用户信息的点调用  传入用户数据悬浮按钮可展开
				SuspensionButton.getInstance().onUserInfoUpdate(deviceID, deviceID);
			}
		});
		
		View testButton = findViewById(R.id.TestButton);
		testButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "I was clicked .UI still alive!", Toast.LENGTH_LONG).show();  
			}
		});
		// wx 悬浮窗隐藏按钮
		View hideButton = findViewById(R.id.hideButton);
		hideButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SuspensionButton.getInstance().hideFloatButton();
			}
		});
		// wx 悬浮窗显示按钮
		View showButton = findViewById(R.id.showButton);
		showButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SuspensionButton.getInstance().showFloatButton();
			}
		});
		
		View FbLogin = findViewById(R.id.FbLogin);
		FbLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FBUtil.Login();
			}
		});
		
		View FbLogout = findViewById(R.id.FbLogout);
		FbLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FBUtil.callFacebookLogout();
			}
		});
		
		View StoryShare = findViewById(R.id.StoryShare);
		StoryShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				FBUtilies::fbPostMethod("feed", "link", "http://cok.elex.com/opengraph/opengraph.php?id=treasure_help&feedback=treasure_help", "actions", "{ \"name\":\"hi test\", \"link\":\"http://cok.elex.com/opengraph/opengraph.php?id=common&feedback=call_for_help\"}", "cal_for_hp");
//				FBUtil.facebookPostMethod(method, objectType, objectValue, actions, actionsValue, ref);
				FBUtil.facebookPostMethod("feed", "link", "http://xpub.337.com/opengraph/opengraph.php?id=treasure_help&feedback=treasure_help", "actions", "{ \"name\":\"hi test\", \"link\":\"http://cok.elex.com/opengraph/opengraph.php?id=common&feedback=call_for_help\"}", "cal_for_hp");
			}
		});
		
		//wx 显示公告按钮
		View noticeShow = findViewById(R.id.NoticeButton);
		noticeShow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
			
		});
		//wx 截屏按钮 
		View screenShotButton=findViewById(R.id.screenshotButton);
		screenShotButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ScreenShoter ss=new ScreenShoter(MainActivity.this);
				ss.shot();
			}
		});
		//wx 加速按钮
		View cleanButton=findViewById(R.id.cleanButton);
		cleanButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BackgroundKiller bkb=new BackgroundKiller(MainActivity.this);
				bkb.killBackgroundProcess();
			}
		});
		//wx 分享按钮
		View shareButton = findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,SharePageActivity.class);
				startActivity(intent);
			}
		});
		//初始化时传入AppId，按钮尺寸————————————————————————————悬浮窗初始化begin
        SuspensionButton.init(this, "4061", 50,"5HugGrmD");
//        SuspensionButton.init(this, "3760", 50,"J52wDbsjz9wx");//怪談 豎屏
		SuspensionButton.getInstance().onCreate(savedInstanceState);
		
		SuspensionButton.getInstance().onUserInfoUpdate(deviceID, deviceID);//在能够获得用户信息的点调用  传入用户数据悬浮按钮可展开
		
    }
    

	@Override
	protected void onResume() {
		super.onResume();
        Log.d("debug", "onResume");
        SuspensionButton.getInstance().onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
        Log.d("debug", "onPause");
        SuspensionButton.getInstance().onPause();
	}
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("debug", "onDestroy");
        if(FBUtil.isLogin()) FBUtil.callFacebookLogout();
        SuspensionButton.getInstance().destroy();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "onRestart");
        SuspensionButton.getInstance().onRestart(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){   	
    	Log.d("debug", "onActivityResult");
    	if(SuspensionButton.getInstance() != null){
    		SuspensionButton.getInstance().onActivityResult(requestCode, resultCode, data);
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override 
    protected void onSaveInstanceState(Bundle saveInstanceState){
    	super.onSaveInstanceState(saveInstanceState);
    	Log.d("debug", "onSaveInstanceState");
    	SuspensionButton.getInstance().onSaveInstanceState(saveInstanceState);
    }
    

	@Override
	public void onFbInviteCallback(GameRequestDialog.Result result, FacebookException error) {
		//这里写入fb邀请的回调处理代码
		if(result == null && error == null){
			Toast.makeText(this.getApplicationContext(),"Request cancelled",Toast.LENGTH_SHORT).show();
		}else if (error != null) {
				if (error instanceof FacebookOperationCanceledException) {
					Toast.makeText(this.getApplicationContext(),"Request cancelled",Toast.LENGTH_SHORT).show();
					//Log.i(TAG, "Request cancelled");
				} else {
					Toast.makeText(this.getApplicationContext(),"Network Error", Toast.LENGTH_SHORT).show();
				}
		} else {
				final String requestId = result.getRequestId();
				if (requestId != null) {
					Toast.makeText(this.getApplicationContext(),"Request sent", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this.getApplicationContext(),"requestId null",Toast.LENGTH_SHORT).show();
				}
			} 		
	}

	@Override
	public void onFbShareCallback(ShareDialog.Result result, FacebookException error) {
		//这里写入fb分享的回调处理代码
		if(result == null && error == null){
			Toast.makeText(this.getApplicationContext(),"Publish cancelled",Toast.LENGTH_SHORT).show();
		}else if (error == null) {// When the story is posted, echo the success and the post Id.
			final String postId = result.getPostId();
			if (postId != null) {
				Toast.makeText(this,"Posted story, id: "+postId,Toast.LENGTH_SHORT).show();
			} else {// User clicked the Cancel button
				Toast.makeText(this,"Posted story",Toast.LENGTH_SHORT).show();
			}
		} else if (error instanceof FacebookOperationCanceledException) {// User clicked the "x" button
			Toast.makeText(this.getApplicationContext(),"Publish cancelled", Toast.LENGTH_SHORT).show();
		} else {// Generic, ex: network error
			Toast.makeText(this.getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onFbProfileTrack(Profile oldProfile, Profile currentProfile) {
		// TODO 自动生成的方法存根
		if(currentProfile != null){
			Log.d("floatbutton", "fb onCurrentProfileChanged id " + currentProfile.getId());
			Log.d("floatbutton", "fb onCurrentProfileChanged name " + currentProfile.getName());
		}
		
		if(oldProfile != null){
		Log.d("floatbutton", "fb oldProfile id " + oldProfile.getId());
		Log.d("floatbutton", "fb oldProfile name " + oldProfile.getName());
		}
		
	}

	@Override
	public void onFbAccessTokenTrack(AccessToken oldAccessToken,
			AccessToken currentAccessToken) {
		// TODO 自动生成的方法存根
		if(oldAccessToken != null)Log.d("floatbutton","The old AccessToken is "+oldAccessToken.getToken());
   	 	if(currentAccessToken != null)Log.d("floatbutton","The current AccessToken is "+currentAccessToken.getToken());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Branch.getInstance(getApplicationContext()).initSession();
		SuspensionButton.getInstance().onStart();
	}

	@Override
	public void onNewIntent(Intent intent) {
		this.setIntent(intent);
	}
}
