package com.elex;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SharePageFragment extends Fragment{
	private Context mContext;
	public SharePageFragment(Context context){
		this.mContext=context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ShareGener.init(mContext);
		View view=ShareGener.getInstance().getView();
		return view;
	}
	
}
