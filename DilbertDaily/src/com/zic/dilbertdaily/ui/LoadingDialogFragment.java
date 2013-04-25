package com.zic.dilbertdaily.ui;

import com.zic.dilbertdaily.R;
import com.zic.dilbertdaily.R.layout;
import com.zic.dilbertdaily.util.StripManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link LoadingDialogFragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link LoadingDialogFragment#newInstance}
 * factory method to create an instance of this fragment.
 * 
 */
public class LoadingDialogFragment extends DialogFragment {
	private StripManager mStripManager;
	
	public LoadingDialogFragment(StripManager manager){
		super();
		mStripManager = manager;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setStyle(STYLE_NO_TITLE, android.R.style.Theme);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_loading_dialog, container,false);
		Drawable bg = new ColorDrawable(Color.BLACK);
		bg.setAlpha(150);
		getDialog().getWindow().setBackgroundDrawable(bg);
		return v;
	}
	
	@Override
	public void onCancel(DialogInterface dialog){
		if (mStripManager!=null){
			mStripManager.onCancelLoading();
		}
		super.onCancel(dialog);
	}

}
