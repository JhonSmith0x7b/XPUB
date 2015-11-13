package com.elex;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InviteFragment extends Fragment{
	private Context mContext;
	public InviteFragment(Context context){
		this.mContext=context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String inviteMsg="";
		if(SuspensionButton.getInstance()!=null&&SuspensionButton.getInstance().inviteParams!=null&&SuspensionButton.getInstance().inviteParams.getMsg()!=null){
			inviteMsg=SuspensionButton.getInstance().inviteParams.getMsg();
		}
		InviteGener.init(mContext,inviteMsg);
		View view=InviteGener.getInstance().getView();
		return view;
	}
	
}
