package com.elex;


import com.elex.suspension.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class BoardActivity extends Activity {
	public String invitedMsg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Intent intent = getIntent();
		String msg = intent.getStringExtra("boardType");
		invitedMsg=intent.getStringExtra("invitedMsg");
		if (msg.equals("1")||"1".equals(msg)) {
			//打开邀请面板
    		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_xpub_invite");
			setContentView(R.layout.invite_main_portrait);
			InviteGener.init(BoardActivity.this,invitedMsg);
		} else {
			//打开领奖面板
    		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_xpub_award");
			String awardProcess =intent.getStringExtra("awardProcess");
			setContentView(R.layout.quest_main_portrait);
			AwardGener.init(BoardActivity.this,awardProcess);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	

}
