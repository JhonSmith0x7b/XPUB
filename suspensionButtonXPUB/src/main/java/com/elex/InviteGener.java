package com.elex;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.elex.InviteGener.MyAdapter.ElementHolder;
import com.elex.dataObject.FBFriendPoj;
import com.elex.suspension.R;
import com.elex.util.CacheImage;
import com.elex.util.FBUtil;
import com.elex.util.MyFBUtil;
import com.elex.util.XpubUtils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

public class InviteGener {
	private static InviteGener instance;
	private Context mContext;
	public static JSONArray fbJAarry=null;
	private DisplayMetrics dm;
	private RelativeLayout rl;
	private String inviteMsg;
	private Button aboveButton;
	private ImageView bottomView;
	private Button bottomButton;
	private GridView inviteGrid;
	private Button noDataButton;
	private String []friendsCheck;
	private TextView fbFriendsNumView;
	private CheckBox fbFriendsAllPickCheck;
	private List<FbFriendsHolder> fbFriendsList;
	private List<ElementHolder> holderList;
	private LayoutInflater mInflater;
	private View contentView;
	
	public static InviteGener getInstance(){
		return instance;
	}
	public View getView(){
		return contentView;
	}
	public InviteGener(Context context,String inviteMsg){
		mContext=context;
		this.inviteMsg=inviteMsg;
		dm=((Activity)mContext).getResources().getDisplayMetrics();
		mInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView=mInflater.inflate(R.layout.invite_main_portrait, null);
		rl=(RelativeLayout) contentView.findViewById(R.id.rlInvite);
	}
	public static void init(Context context,String inviteMsg){
		instance=new InviteGener(context,inviteMsg);
		instance.setPortrait();
	}
	private void setPortrait(){
		try{
			setMain();
			setGrid(InviteGener.fbJAarry,inviteMsg);
		}catch(Exception e){
			Log.e(SuspensionButton.TAG, "InviteGener generator");
			e.printStackTrace();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
			((Activity)mContext).finish();
		}
	}
	private void setMain(){
		aboveButton = (Button) rl.findViewById(R.id.aboveButton);
		RelativeLayout.LayoutParams rlParams=(LayoutParams) aboveButton.getLayoutParams();
		rlParams.width=dm.widthPixels;
		rlParams.height=dm.heightPixels/10;
		aboveButton.setLayoutParams(rlParams);
		aboveButton.getBackground().setColorFilter(0xFF73A1FF,PorterDuff.Mode.MULTIPLY);
		aboveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Activity a = (Activity) mContext;
				a.finish();
			}
			
		});
		bottomView = (ImageView) rl.findViewById(R.id.bottomView);
		RelativeLayout.LayoutParams rlParams2=(LayoutParams)bottomView.getLayoutParams();
		rlParams2.width=dm.widthPixels;
		rlParams2.height=dm.heightPixels/10;
		bottomView.setLayoutParams(rlParams2);
		bottomButton = (Button) rl.findViewById(R.id.bottomButton);
		RelativeLayout.LayoutParams rlParams3=(LayoutParams)bottomButton.getLayoutParams();
		rlParams3.width=dm.widthPixels*2/5;
		rlParams3.height=(int) (dm.heightPixels/10*0.8);
		rlParams3.bottomMargin+=(int) (dm.heightPixels/10*0.1);
		rlParams3.rightMargin=dm.widthPixels*1/20;
		bottomButton.setLayoutParams(rlParams3);
		bottomButton.getBackground().setColorFilter(0xFF73A1FF,PorterDuff.Mode.MULTIPLY);
		fbFriendsNumView=(TextView) rl.findViewById(R.id.fbFriendsNum);
		RelativeLayout.LayoutParams rlParams4=(LayoutParams) fbFriendsNumView.getLayoutParams();
		rlParams4.width=dm.widthPixels/5;
		rlParams4.height=(int) (dm.heightPixels/10*0.4);
		rlParams4.bottomMargin+=(int) (dm.heightPixels/10*0.3);
		fbFriendsNumView.setLayoutParams(rlParams4);
		fbFriendsNumView.setText(mContext.getResources().getString(R.string.fbFriendsNumTitle)+"0");
		fbFriendsAllPickCheck=(CheckBox) rl.findViewById(R.id.allPickCheck);
		RelativeLayout.LayoutParams rlParams5=(LayoutParams) fbFriendsAllPickCheck.getLayoutParams();
		rlParams5.width=dm.widthPixels/5;
		rlParams5.height=(int)(dm.heightPixels/10*0.6);
		rlParams5.bottomMargin+=(int)(dm.heightPixels/10*0.2);
		fbFriendsAllPickCheck.setChecked(true);
		fbFriendsAllPickCheck.setLayoutParams(rlParams5);
		holderList=new ArrayList<ElementHolder>();
		fbFriendsAllPickCheck.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				try{
				if(holderList.size()==0){
					return;
				}
				if(isChecked){
					for(int i=0;i<holderList.size();i++){
						holderList.get(i).img1.setVisibility(View.VISIBLE);
					}
					for(int i=0;i<friendsCheck.length;i++){
						friendsCheck[i]="T";
					}
				}
				else if(!isChecked){
					for(int i=0;i<holderList.size();i++){
						holderList.get(i).img1.setVisibility(View.GONE);
					}
					for(int i=0;i<friendsCheck.length;i++){
						friendsCheck[i]="";
					}
				}
				int checkNum=0;
				for(String check :friendsCheck){
					if(check!=null&&check.equals("T")){
						++checkNum;
					}
				}
				fbFriendsNumView.setText(mContext.getResources().getString(R.string.fbFriendsNumTitle)+checkNum);
				}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
				}
			}
			
		});
	}
	private void setGrid(JSONArray jArray,final String inviteMsg){
		if(jArray!=null){
			Gson gson = new Gson();
			fbFriendsList=new ArrayList<FbFriendsHolder>();
			List<FbFriendsHolder> fbFriendsListOriginal=new ArrayList<FbFriendsHolder>();
			fbFriendsListOriginal = new ArrayList<FbFriendsHolder>();
			for(int i = 0 ;i<jArray.length();i++){
				try {
					FbFriendsHolder  fbFriendsHolder= gson.fromJson(jArray.getJSONObject(i).toString(), FbFriendsHolder.class);
					fbFriendsListOriginal.add(fbFriendsHolder);
				} catch (Exception e) {
					Log.e(SuspensionButton.TAG, "InviteGener setGrid");
					e.printStackTrace();
				}
			}
			if(fbFriendsListOriginal.size()==0){
				setNoDataView();
				return;
			}
			String data = XpubUtils.getCurrentDay();
			for(FbFriendsHolder fbFriend:fbFriendsListOriginal){
				String checkValue=XpubUtils.getValue(data+data+fbFriend.name.toString(), SuspensionButton.getInstance().getContext());
				if(checkValue!=null&&checkValue.equals("T")){
					continue;
				}
				fbFriendsList.add(fbFriend);
			}
			if(fbFriendsList.size()==0){
				setNoDataView();
				return;
			}
			friendsCheck=new String[fbFriendsList.size()];
			for(int i=0;i<friendsCheck.length;i++){
				friendsCheck[i]="T";
			}
			fbFriendsNumView.setText(mContext.getResources().getString(R.string.fbFriendsNumTitle)+friendsCheck.length);
			inviteGrid = (GridView) rl.findViewById(R.id.inviteGrid);
			RelativeLayout.LayoutParams  rlParams= (LayoutParams) inviteGrid.getLayoutParams();
			rlParams.width=dm.widthPixels;
			rlParams.height=dm.heightPixels*8/10-60*(int)dm.density;
			inviteGrid.setNumColumns(3);
			inviteGrid.setLayoutParams(rlParams);
			inviteGrid.setAdapter(new MyAdapter(mContext,fbFriendsList));
			bottomButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_invite_click_send");
					int friendsCheckNum=0;
					ArrayList<FBFriendPoj> fbFriendPojs = new ArrayList<FBFriendPoj>();
					invitedFriendsNames=new ArrayList<String>();
					String inviteNames="";
					for(int i =0;i<friendsCheck.length;i++){
						if(friendsCheck[i]!=null&&friendsCheck[i].equals("T")){
							++friendsCheckNum;
							FBFriendPoj fbFriendsPoj= new FBFriendPoj();
							fbFriendsPoj.setUserId(fbFriendsList.get(i).id);
							fbFriendsPoj.setUserName(fbFriendsList.get(i).name);
							invitedFriendsNames.add(fbFriendsList.get(i).name);
							fbFriendPojs.add(fbFriendsPoj);
							inviteNames += (" " + fbFriendsList.get(i).name);
						}
					}
					if(friendsCheckNum==0){
						Toast.makeText(mContext,mContext.getResources().getString(R.string.fbFriendsNumZero), Toast.LENGTH_SHORT).show();
						return;
					}
					SuspensionButton.mToFBIds=friendsCheckNum;
					String alert = mContext.getResources().getString(
							R.string.noticeboard_sendToFriend_1)
							+ SuspensionButton.mToFBIds
							+ mContext.getResources().getString(
									R.string.noticeboard_sendToFriend_2)+inviteNames;;
					if (inviteMsg.equals("") || inviteMsg == null) {
						MyFBUtil.sendFriendRequest(fbFriendPojs, mContext.getResources()
								.getString(R.string.noticeboard_sendMsg), "9");
					} else {
						MyFBUtil.sendFriendRequest(fbFriendPojs,
								inviteMsg.toString(), "9");
					}
					Toast.makeText(mContext, alert, Toast.LENGTH_SHORT).show();
				}
				
			});
		}else{
			setNoDataView();
		}
	}
	private List<String>invitedFriendsNames;
	public void removeInvitedFriends(){
		if(invitedFriendsNames==null||invitedFriendsNames.size()==0){
			return;
		}
		String data =XpubUtils.getCurrentDay();
		for(String name:invitedFriendsNames){
			XpubUtils.setValue(data+data+name, "T", SuspensionButton.getInstance().getContext());
		}
	}
	private AlphaAnimation buttonClickT = new AlphaAnimation(1,0.3F);
	@SuppressWarnings("deprecation")
	private void setNoDataView(){
		noDataButton=(Button) rl.findViewById(R.id.noDataButton);
		RelativeLayout.LayoutParams rlParams = (LayoutParams) noDataButton.getLayoutParams();
		rlParams.width=dm.widthPixels/3;
		rlParams.height=dm.widthPixels/3;
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(0xFF305891);
		gd.setShape(GradientDrawable.OVAL);
		noDataButton.setLayoutParams(rlParams);
		noDataButton.setBackgroundDrawable(gd);
		noDataButton.setVisibility(View.VISIBLE);
		noDataButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				noDataButton.startAnimation(buttonClickT);
				MyFBUtil.inviteFriends("");
			}
		});
	}
	/**
	 * FB好友数据 pojo类
	 * @author JhonSmith
	 *
	 */
	public class FbFriendsHolder{
		public FbFriendsHolder(){
			
		}
		String id;
		String name;
		FbFriendsPictureDataHolder picture;
		public class FbFriendsPictureDataHolder{
			public FbFriendsPictureDataHolder(){
				
			}
			FbFriendsPictureUrlHolder data;
			public class FbFriendsPictureUrlHolder{
				public FbFriendsPictureUrlHolder(){
					
				}
				String url;
			}
		}
	}
	
	public class MyAdapter extends BaseAdapter{
		private Context mContext;
		private LayoutInflater inflater;
		/**
		 * 存放gridView中 每一个单独view的元素
		 * @author JhonSmith
		 *
		 */
		public class ElementHolder{
			ImageView img;
			ImageView img1;
			TextView tv;
		}
		private List<FbFriendsHolder> fbFriendsList=null;
		public MyAdapter(Context context,List<FbFriendsHolder> fbFriendsList){
			this.fbFriendsList=fbFriendsList;
			this.mContext=context;
			this.inflater=(LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return fbFriendsList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try{
			if(fbFriendsList==null)return null;
			ElementHolder holder;
			if(convertView!=null){
				holder=(ElementHolder) convertView.getTag();
			}else{
				convertView=inflater.inflate(R.layout.invited_grid_portrait, parent,false);
				holder=new ElementHolder();
				holder.img=(ImageView) convertView.findViewById(R.id.imageView1);
				holder.img1=(ImageView) convertView.findViewById(R.id.imageView2);
				holder.tv=(TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			}
			if(friendsCheck[position]==null||friendsCheck[position].equals("")){
				holder.img1.setVisibility(View.GONE);
			}else if(friendsCheck[position].equals("T")){
				holder.img1.setVisibility(View.VISIBLE);
			}
			try {
				String picUrl=fbFriendsList.get(position).picture.data.url;
				if(picUrl==null||picUrl.equals("")){
					holder.img.setBackgroundResource(R.drawable.notice_basicimg);
				}else{
					new DownloadImgTask(holder.img).execute(picUrl);
				}
			} catch (Exception e) {
				Log.e(SuspensionButton.TAG, "InviteGener GridView GetView");
				holder.img.setBackgroundResource(R.drawable.notice_basicimg);
				e.printStackTrace();
			}
			holder.tv.setText(fbFriendsList.get(position).name);
			convertView.setOnClickListener(new InviteGridOnClickListener(holder,position));
			holderList.add(holder);
			return convertView;
			}catch(Exception e){
				Log.e(SuspensionButton.TAG, "InviteGrid GetView");
				e.printStackTrace();
				Toast.makeText(mContext, mContext.getResources().getString(R.string.noticeboard_error), Toast.LENGTH_SHORT).show();
				((Activity)mContext).finish();
				return null;
			}
		}
		public class InviteGridOnClickListener implements OnClickListener{
			ElementHolder holder;
			int position;
			public InviteGridOnClickListener(ElementHolder holder,int position){
				this.holder=holder;
				this.position=position;
			}
			@Override
			public void onClick(View v) {
				if(friendsCheck[position]==null||friendsCheck[position].equals("")){
					holder.img1.setVisibility(View.VISIBLE);
					friendsCheck[position]="T";
				}else if(friendsCheck[position].equals("T")){
					holder.img1.setVisibility(View.GONE);
					friendsCheck[position]="";
				}
				int checkNum=0;
				for(String check:friendsCheck){
					if(check!=null&&check.equals("T")){
						++checkNum;
					}
				}
				if(fbFriendsNumView!=null){
					fbFriendsNumView.setText(mContext.getResources().getString(R.string.fbFriendsNumTitle)+checkNum);
				}
			}
			
		}
		
	}
	/**
	 * 下载网络图片工具类
	 * 
	 * @author JhonSmith
	 *
	 */
	public  class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView imageView;

		public DownloadImgTask(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String urlDisplay = urls[0];
//			String urlDisplay="http://img2.imgtn.bdimg.com/it/u=2861270851,2291089313&fm=21&gp=0.jpg";
			Bitmap myIcon = null;
			try {
				myIcon=CacheImage.get(new URL(urlDisplay));
				if(myIcon!=null)return myIcon;
				InputStream is = new java.net.URL(urlDisplay).openStream();
				myIcon=CacheImage.set(new URL(urlDisplay), is);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return myIcon;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Bitmap result) {
			BitmapDrawable ob = new BitmapDrawable(mContext.getResources(),result);
			if(imageView!=null){
				imageView.setBackgroundDrawable(ob);
			}
		}

	}
}
