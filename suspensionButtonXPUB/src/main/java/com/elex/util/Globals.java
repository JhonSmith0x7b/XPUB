package com.elex.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.elex.CallBackFunciton;
import com.elex.SuspensionButton;
import com.umeng.analytics.MobclickAgent;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Globals {
	private static Globals sInstance = null;
	private HttpClient mHttpClient=null;
	private HttpPost mHttpPost=null;
	private List<NameValuePair> mPostParams = null;
	private CallBackFunciton mCallbackFunc =null;
	private CallBackFunciton mOnError =null;	
	private ArrayList<RequestObject> mReqList=new ArrayList<RequestObject>();
	
	class RequestObject{
		public RequestObject(List<NameValuePair> params,
				CallBackFunciton callbackFunc, CallBackFunciton onError) {
			mPostParams=params;
			mCallbackFunc = callbackFunc;
			mOnError = onError;
		}
		
		public RequestObject(RequestObject requestObject) {
			mPostParams=requestObject.mPostParams;
			mCallbackFunc = requestObject.mCallbackFunc;
			mOnError = requestObject.mOnError;
		}

		public List<NameValuePair> mPostParams = null;
		public CallBackFunciton mCallbackFunc =null;
		public CallBackFunciton mOnError =null;
		
	}
	
	private Globals(){
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 8000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 8000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		mHttpClient = new DefaultHttpClient(httpParameters);
		mHttpPost = new HttpPost(SuspensionButton.mPostUrl);
	}
	
	public static Globals getInstance(){
		if(sInstance == null){
			sInstance = new Globals();
		}
		return sInstance;
	}
	
	public void sendRequest(final List<NameValuePair> params){
		sendRequest(params,null,null);
	}
	
	private static boolean inHttpProcess = false;
	public  void sendRequest(final List<NameValuePair> params,CallBackFunciton callbackFunc,CallBackFunciton onError){

		if(params == null ) return;
		mReqList.add(new RequestObject(params,callbackFunc,onError));
		doSend();
	}
	
	private void doSend(){
		
		if(mReqList.size() > 0 && !inHttpProcess){
			mPostParams = mReqList.get(0).mPostParams;
			mCallbackFunc = mReqList.get(0).mCallbackFunc;
			mOnError = mReqList.get(0).mOnError;
			mReqList.remove(0);
			 new Thread(httpConnRunable).start();
		}
	}
	
	//结果通过消息返回给另一线程。 通畅是UI线程，来执行callback；
	public static  Handler theMsgHandler = new Handler(){  
        public void handleMessage(Message msg) {
        	switch(msg.what){
        	case 1000:
		        	   String sJson = (String)msg.obj;
		        	   if(Globals.getInstance().mCallbackFunc != null) {
		        		   Globals.getInstance().mCallbackFunc.call(sJson);
		        		   Globals.getInstance().mCallbackFunc = null;
		        		   Globals.getInstance().mOnError = null;
		        	   }
		       	   break;
        	case -1:
	        	   if(Globals.getInstance().mOnError != null) {
	        		   Globals.getInstance().mOnError.call((String)msg.obj);
	        		   Globals.getInstance().mCallbackFunc = null;
	        		   Globals.getInstance().mOnError = null;
	        	   }
        		break;
        		
        	default:
        		break;
        	}
        	
        	inHttpProcess = false;
        	Globals.getInstance().doSend();
        }   
    };
    
 	 private Runnable httpConnRunable = new Runnable(){
 		 
			@Override
			public void run() {
				HttpResponse response = null;
				try {
					
					if(mPostParams.isEmpty()) return;
					HttpEntity httpEntity = new UrlEncodedFormEntity(mPostParams,"utf-8");
					mHttpPost.setEntity(httpEntity);
					inHttpProcess = true;
					 response = mHttpClient.execute(mHttpPost);
					 Log.d(SuspensionButton.TAG,"sendRequest:"+mPostParams.toString());
					mPostParams.clear();
					
					int resCode =response.getStatusLine().getStatusCode(); //获取响应码
					if(resCode != 200){ //处理请求异常
						Log.e(SuspensionButton.TAG,"sendrequest http error "+response.getStatusLine().toString());
						Message msg = Message.obtain(theMsgHandler, -1, response.getStatusLine().toString());
						response.getEntity();
						theMsgHandler.sendMessage(msg);
						return;
					}
					String sMsgjson = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
					Log.d(SuspensionButton.TAG,"sendrequest response:"+sMsgjson);
					Message msg = Message.obtain(theMsgHandler, 1000, sMsgjson);
					theMsgHandler.sendMessage(msg);
				} catch (Exception e) {
					Message msg = Message.obtain(theMsgHandler, -1, "");
					theMsgHandler.sendMessage(msg);
					e.printStackTrace();
					Log.e(SuspensionButton.TAG, "sendrequest time out");
					MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), "sendrequest_exception");
				}finally{
					if(response != null) try{
						response.getEntity();//消费掉response的缓冲
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
		};


}
