package com.zic.dilbertdaily.ui;

import com.zic.dilbertdaily.R;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class StripFragment extends Fragment  {
	private static final String TAG = "StripFragment";
	
	public StripFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		final View v = inflater.inflate(R.layout.strip_fragment, container, false);
		
		final ImageView stripContainer = (ImageView) v.findViewById(R.id.stripContainer);
		stripContainer.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.empty_photo));
		
		return v;
	}
}
