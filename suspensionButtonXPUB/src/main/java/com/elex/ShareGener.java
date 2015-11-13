package com.elex;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.elex.suspension.R;
import com.elex.util.MyFBUtil;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

public class ShareGener {
	private static ShareGener sInstance;
	private DisplayMetrics mDm;
	private Context mContext;
	private LayoutInflater mInflater;
	private AlphaAnimation mButtonClick;
	private ClipboardManager mClipboard;
	private String mShortLink="";
	RelativeLayout rl;
	Button aboveButton;
	ImageView contentImageView;
	TextView linkTextView;
	TextView contentTextView;
	Button copyButton;
	Button fbShareButton;
	ProgressDialog progress;
	PopupWindow pw;
	View pwView;
	PopupWindow sharePw;
	private View contentView;
	public static ShareGener getInstance(){
		return sInstance;
	}
	public View getView(){
		return contentView;
	}
	public ShareGener(Context context){
		this.mContext=context;
		mInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDm = mContext.getResources().getDisplayMetrics();
		contentView=mInflater.inflate(R.layout.share_page_portrait, null);
		rl=(RelativeLayout) contentView.findViewById(R.id.rlShare);
		mButtonClick=new AlphaAnimation(1f,0.1f);
		mClipboard=(ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
	}
	public static void init(Context context){
		sInstance=new ShareGener(context);
		sInstance.setPortrait();
	}
	private void setPortrait(){
		try{
			setMain();
			setShareDescribeWindow();
			setSharePop();
		}catch(Exception e){
			Log.e(SuspensionButton.TAG, "ShareGenerGenERROR");
			Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
			((Activity)mContext).finish();
		}
	}
	private void setMain(){
		aboveButton =(Button) rl.findViewById(R.id.aboveButton);
		RelativeLayout.LayoutParams rlParams = (LayoutParams) aboveButton.getLayoutParams();
		rlParams.width=mDm.widthPixels;
		rlParams.height=mDm.heightPixels/10;
		aboveButton.setLayoutParams(rlParams);
		aboveButton.getBackground().setColorFilter(0xFF73A1FF,PorterDuff.Mode.MULTIPLY);
		aboveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Activity a = (Activity) mContext;
				a.finish();
			}
			
		});
		contentImageView =(ImageView) rl.findViewById(R.id.contentImageView);
		RelativeLayout.LayoutParams rlParams1=(LayoutParams) contentImageView.getLayoutParams();
		rlParams1.width=(int) (mDm.widthPixels/3*1.16*1.3);
		rlParams1.height=(int) (mDm.widthPixels/3*1.3);
		rlParams1.topMargin=mDm.heightPixels*1/10;
		contentImageView.setLayoutParams(rlParams1);
		linkTextView = (TextView) rl.findViewById(R.id.linkTextView);
		RelativeLayout.LayoutParams rlParams2=(LayoutParams) linkTextView.getLayoutParams();
		rlParams2.width=(int) (mDm.widthPixels/3*1.2);
//		rlParams2.height=mDm.heightPixels/10;
//		rlParams2.topMargin=mDm.heightPixels*1/30;
		rlParams2.height=LayoutParams.WRAP_CONTENT;
		rlParams2.height=LayoutParams.WRAP_CONTENT;
		mShortLink =SuspensionButton.getInstance().getMyShortUrl();
		if(mShortLink==null||mShortLink.equals("")){
			mShortLink="http://www.facebook.com";
		}
		if (SuspensionButton.getInstance().shareParams != null
				&& SuspensionButton.getInstance().shareParams.getShareMsg() != null
				&& !SuspensionButton.getInstance().shareParams.getShareMsg()
						.equals("")) {
			linkTextView.setText(SuspensionButton.getInstance().shareParams.getShareMsg()+mShortLink);
		}else{
			linkTextView.setText(mContext.getResources().getString(R.string.sharePageDescribe)+" "+mShortLink);
		}
		linkTextView.setLayoutParams(rlParams2);
		contentTextView=(TextView)rl.findViewById(R.id.contentTextView);
		RelativeLayout.LayoutParams rlParams3=(LayoutParams)contentTextView.getLayoutParams();
//		rlParams3.width=(int) (mDm.widthPixels*0.8);
//		rlParams3.height=mDm.heightPixels*2/20;
		rlParams3.width=LayoutParams.WRAP_CONTENT;
		rlParams3.height=LayoutParams.WRAP_CONTENT;
		rlParams3.topMargin=(int) (10*mDm.density);
		if (SuspensionButton.getInstance().shareParams != null
				&& SuspensionButton.getInstance().shareParams
						.getShareIntroduce() != null
				&& !SuspensionButton.getInstance().shareParams
						.getShareIntroduce().equals("")) {
			contentTextView.setText(SuspensionButton.getInstance().shareParams.getShareIntroduce());
		}
		contentTextView.setLayoutParams(rlParams3);
		contentTextView.setOnClickListener(new OnClickListener(){

			@SuppressLint("InflateParams")
			@Override
			public void onClick(View v) {
				pw.showAtLocation(rl, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
			}
			
		});
		copyButton=(Button) rl.findViewById(R.id.copyView);
		RelativeLayout.LayoutParams rlParams4=(LayoutParams) copyButton.getLayoutParams();
		rlParams4.width=(int) (mDm.widthPixels*0.33);
		rlParams4.height=(int) (rlParams4.width*0.6);
		rlParams4.leftMargin=(int) (mDm.widthPixels*0.04);
		rlParams4.topMargin=(int) (10*mDm.density);
		copyButton.setLayoutParams(rlParams4);
		copyButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				copyButton.startAnimation(mButtonClick);
				
				if (SuspensionButton.getInstance().shareParams != null
						&& SuspensionButton.getInstance().shareParams
								.getInviteMsg() != null
						&& !SuspensionButton.getInstance().shareParams
								.getInviteMsg().equals("")) {
					mClipboard.setPrimaryClip(ClipData.newPlainText(
							"ShareGener",
							SuspensionButton.getInstance().shareParams
									.getInviteMsg() + mShortLink));
				}else{
					mClipboard.setPrimaryClip(ClipData.newPlainText("ShareGener",mContext.getResources().getString(R.string.shareButtonContent)+mShortLink ));
				}
				sharePw.showAtLocation(contentView, Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, 0);
				Toast.makeText(mContext, mContext.getResources().getString(R.string.shareButtonResult), Toast.LENGTH_SHORT).show();
				//点击统计
	    		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_cp_promo_url");

			}
			
		});
		fbShareButton=(Button) rl.findViewById(R.id.fbshareView);
		RelativeLayout.LayoutParams rlParams5=(LayoutParams) fbShareButton.getLayoutParams();
		rlParams5.width=(int) (mDm.widthPixels*0.55);
		rlParams5.height=(int)(rlParams4.width*0.6);
		rlParams5.rightMargin=(int) (mDm.widthPixels*0.04);
		rlParams5.topMargin=(int) (10*mDm.density);
		fbShareButton.setLayoutParams(rlParams5);
		fbShareButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//点击统计
				fbShareButton.startAnimation(mButtonClick);
	    		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_clk_fbpost");
				if(SuspensionButton.getInstance().shareParams==null&&SuspensionButton.getInstance().shareParams.equals("")){
					return;
				}
				ShinBoardGener.getInstance().addLoadingImage();
	    		MyFBUtil.publicFeed(SuspensionButton.getInstance().shareParams,mShortLink);
			}
			
		});
		
	}
	private void setShareDescribeWindow(){
		pw= new PopupWindow(mContext);
		pwView=mInflater.inflate(R.layout.mysharepagedescribe_view_portrait, null,false);
		Button closeButton=(Button) pwView.findViewById(R.id.closeButton);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pw.dismiss();
			}
			
		});
		TextView contentView=(TextView) pwView.findViewById(R.id.shareDescribeContent);
		if (SuspensionButton.getInstance().shareParams != null
				&& SuspensionButton.getInstance().shareParams
						.getShareDescribe() != null
				&& !SuspensionButton.getInstance().shareParams
						.getShareDescribe().equals("")) {
			contentView.setText(SuspensionButton.getInstance().shareParams.getShareDescribe());
		}
		pw.setContentView(pwView);
		pw.setWidth(mDm.widthPixels*2/3);
		pw.setHeight(mDm.widthPixels*2/3);
		GradientDrawable gd = new GradientDrawable();
		gd.setShape(GradientDrawable.RECTANGLE);
		gd.setCornerRadius(30);
		gd.setColor(0xFFFAFAD2);
		pw.setBackgroundDrawable(gd);
		pw.setFocusable(true);
	}
	private void setSharePop(){
//		contentView.findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				sharePw.showAtLocation(contentView, Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, 0);
//			}
//		});
		sharePw=new PopupWindow(mContext);
		View pwView=mInflater.inflate(R.layout.sharepage_popup_layout, null);
		GridView gv=(GridView) pwView.findViewById(R.id.gv);
		List<PopObjectHolder> popObjectList=new ArrayList<PopObjectHolder>();
		String data="[{'name':'twitter','url':'https://www.twitter.com','pic':'twitter.jpg'},{'name':'tumblr','url':'https://www.tumblr.com/','pic':'tumblr.jpg'},{'name':'google+','url':'https://www.google.com/+','pic':'gplus.jpg'}]";
		try {
			JSONArray jsonArray =new JSONArray(data);
			Gson gson=new Gson();
			for(int i =0;i<jsonArray.length();i++){
				PopObjectHolder popObject=gson.fromJson(jsonArray.getJSONObject(i).toString(), PopObjectHolder.class);
				popObjectList.add(popObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(popObjectList.size()==0){
			
		}
		gv.setAdapter(new MyAdapter(popObjectList));
		gv.setNumColumns(3);
		sharePw.setContentView(pwView);
		sharePw.setWidth(LayoutParams.WRAP_CONTENT);
		sharePw.setHeight(200*(int)mDm.density);
		sharePw.setFocusable(true);
	}
	public class PopObjectHolder{
		String name;
		String url;
		String pic;
	}
	
	public class MyAdapter extends BaseAdapter{
		private int size;
		List<PopObjectHolder> popObjectList;
		public MyAdapter(List<PopObjectHolder> popObjectList){
			this.size=popObjectList.size();
			this.popObjectList=popObjectList;
		}
		public class Holder{
			ImageView img;
			TextView tv;
		}
		@Override
		public int getCount() {
			return size;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		public class urlOnClickListener implements View.OnClickListener{
			private Holder holder;
			private int position;
			public urlOnClickListener(Holder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@Override
			public void onClick(View v) {
				try{
					Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(popObjectList.get(position).url));
					mContext.startActivity(intent);
				}catch(Exception e){
					
				}
			}
			
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder=null;
			if(convertView!=null){
				holder=(Holder) convertView.getTag();
			}else{
				convertView=mInflater.inflate(R.layout.sharepage_gridview_layout, null);
				holder = new Holder();
				holder.img=(ImageView) convertView.findViewById(R.id.img);
				if(popObjectList.get(position).pic.startsWith("tw")){
					holder.img.setBackgroundResource(R.drawable.sharetwitter);
				}else if(popObjectList.get(position).pic.startsWith("tu")){
					holder.img.setBackgroundResource(R.drawable.sharetumblr);
				}else if(popObjectList.get(position).pic.startsWith("g")){
					holder.img.setBackgroundResource(R.drawable.sharegplus);
				}
				holder.tv=(TextView) convertView.findViewById(R.id.tv);
				holder.tv.setText(popObjectList.get(position).name);
				convertView.setTag(holder);
			}
			holder.img.setOnClickListener(new urlOnClickListener(holder,position));
			return convertView;
		}
		
	}
}
