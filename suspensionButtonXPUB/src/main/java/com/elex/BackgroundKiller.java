package com.elex;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.widget.Toast;

public class BackgroundKiller {
	Context mContext;
	ActivityManager am;
	public BackgroundKiller(Context context){
		this.mContext=context;
		am=(ActivityManager)((Activity)mContext).getSystemService(Context.ACTIVITY_SERVICE);
	}
	public void killBackgroundProcess(){
		String killBeforeMemory=getSurplusMemorySize()+"";
		List<RunningAppProcessInfo> appInfoList=am.getRunningAppProcesses();
		int killCounter=0;
		if(appInfoList!=null){
			for(RunningAppProcessInfo appInfo:appInfoList){
				if(appInfo.importance>RunningAppProcessInfo.IMPORTANCE_SERVICE){
					for(String pkg:appInfo.pkgList){
						am.killBackgroundProcesses(pkg);
						killCounter++;
					}
				}
			}
		}
		String killAfterMemory=getSurplusMemorySize()+"";
		Toast.makeText(mContext,killBeforeMemory+"-----"+killAfterMemory+"  Via Kill  "+killCounter+ " process", Toast.LENGTH_LONG).show();
	}
	private long getSurplusMemorySize(){
		MemoryInfo mi=new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem/1024/1024;
	}
}
