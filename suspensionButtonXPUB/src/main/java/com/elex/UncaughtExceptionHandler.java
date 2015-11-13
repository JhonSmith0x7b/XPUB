package com.elex;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler{
	private final String LINE_SEPARATOR = "\n";

    public static final String TAG = "UncaughtExceptionHandler";  
    private Context mContext = null;
  public UncaughtExceptionHandler(Context context){
		mContext = context;
	}
	
	
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @SuppressWarnings("deprecation")
	@Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
    	if (ex == null) {  
            return ;  
        }  
        //使用Toast来显示异常信息  
        new Thread() {  
            @Override  
            public void run() { 
                Looper.prepare();  
                Toast.makeText(mContext, "FloatingButton can't be present .Enjoy game!", Toast.LENGTH_LONG).show();  
                Looper.loop();  
            }  
        }.start();  
        StringWriter stackTrace = new StringWriter();
		ex.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR); 
		
		try {  
            PackageManager pm = mContext.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null" : pi.versionName;  
                String versionCode = pi.versionCode + ""; 
                errorReport.append("versionName: ");
        		errorReport.append(versionName);
        		errorReport.append(LINE_SEPARATOR); 
        		errorReport.append("versionCode: ");
        		errorReport.append(versionCode);
        		errorReport.append(LINE_SEPARATOR); 
            }  
        } catch (NameNotFoundException e) {  
            Log.e(TAG, "an error occured when collect package info", e);  
        }  
		
        Log.e(TAG,errorReport.toString()); 
    }   
}  


