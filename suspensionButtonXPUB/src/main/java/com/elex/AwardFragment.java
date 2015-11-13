package com.elex;

import com.elex.suspension.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AwardFragment extends Fragment{
	private Context mContext;
	private String mAwardProcess;
	public AwardFragment(Context context,String awardProcess){
		this.mContext=context;
		this.mAwardProcess=awardProcess;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		AwardGener.init(mContext, mAwardProcess);
		View view =AwardGener.getInstance().getView();
		return view;
	}
	
}
