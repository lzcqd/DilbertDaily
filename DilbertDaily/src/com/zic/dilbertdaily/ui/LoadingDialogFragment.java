package com.zic.dilbertdaily.ui;

import com.zic.dilbertdaily.R;
import com.zic.dilbertdaily.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
public class LoadingDialogFragment extends Dialog {

	public LoadingDialogFragment(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setContentView(R.layout.fragment_loading_dialog);
	}

	public LoadingDialogFragment(Context context,
			int theme) {
		// TODO Auto-generated constructor stub
		super(context,theme);
		this.setContentView(R.layout.fragment_loading_dialog);
	}

	// @Override
	// public Dialog onCreateDialog(Bundle savedInstanceState){
	// AlertDialog.Builder builder = new
	// AlertDialog.Builder(getActivity(),DialogFragment.STYLE_NO_TITLE);
	// LayoutInflater inflator = getActivity().getLayoutInflater();
	// builder.setView(inflator.inflate(R.layout.fragment_loading_dialog,
	// null));
	// return builder.create();
	// }

}
