package com.zic.dilbertdaily.ui;

import com.zic.dilbertdaily.R;
import com.zic.dilbertdaily.data.ImageQualityEnum;
import com.zic.dilbertdaily.util.StateInfo;
import com.zic.dilbertdaily.util.StateListener;
import com.zic.dilbertdaily.util.StripManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.ViewPager;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import android.widget.TextView;
import android.widget.Toast;

public class StripFragment extends Fragment implements StateListener,
		OnClickListener, PopupMenu.OnMenuItemClickListener {
	private static final String TAG = "StripFragment";
	private ImageView mStripView;
	private TextView mStripPositionCount, mStripName;
	private LinearLayout ErrorPanel;
	private LinearLayout mStripDateLayout;
	private StripManager mStripManager;
	private LoadingDialogFragment mLoadingDialog;
	private ViewPager mPager;
	private PopupMenu popup;

	public StripFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mPager = (ViewPager) getView().findViewById(R.id.stripPager);
		mStripManager.setViewPager(mPager);

	}

	private void setLoadingDialog() {
		mLoadingDialog = new LoadingDialogFragment(mStripManager);
		mLoadingDialog.setTargetFragment(this, getTargetRequestCode());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.strip_fragment, container,
				false);

		mStripPositionCount = (TextView) v.findViewById(R.id.stripPosition);

		mStripName = (TextView) v.findViewById(R.id.stripName);

		ErrorPanel = (LinearLayout) v.findViewById(R.id.errorPanel);

		mStripDateLayout = (LinearLayout) v
				.findViewById(R.id.strip_date_layout);

		TextView ErrorTextView = (TextView) v.findViewById(R.id.errorText);

		ImageButton prevStripButton = (ImageButton) v
				.findViewById(R.id.previous_strip);
		prevStripButton.setOnClickListener(this);

		ImageButton nextStripButton = (ImageButton) v
				.findViewById(R.id.next_strip);
		nextStripButton.setOnClickListener(this);

		mStripManager = new StripManager(getActivity(), getFragmentManager(),
				mStripDateLayout, mStripName, mStripPositionCount,
				ErrorTextView);

		mStripManager.addListener(this);
		setLoadingDialog();
		mStripManager.start();
		// ShowLoadingViews();
		// ShowErrorViews();

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main_menu, menu);
	}

	private void showLoadingViews() {
		if (!mLoadingDialog.isAdded()) {
			mLoadingDialog.show(getFragmentManager(), "LoadingDialog");
		}
		ErrorPanel.setVisibility(View.GONE);
	}

	private void showErrorViews() {

		mStripPositionCount.setVisibility(View.GONE);
		mStripDateLayout.setVisibility(View.GONE);
		ErrorPanel.setVisibility(View.VISIBLE);

	}

	private void showStripViews() {
		mLoadingDialog.dismiss();
		ErrorPanel.setVisibility(View.GONE);
	}

	@Override
	public void stateChanged(StateInfo stateInfo) {
		switch (stateInfo.CurrentState) {
		case Loading:
			showLoadingViews();
			break;
		case Error:
			showErrorViews();
			break;
		case StripNameLoaded:
			break;
		case StripLoaded:
			showStripViews();
			break;
		case AlreadyLoadedLatest:
			showStripViews();
			Toast.makeText(getActivity(), R.string.already_latest_strip,
					Toast.LENGTH_LONG).show();
			break;
		case Cancelled:
			Toast.makeText(getActivity(), R.string.loading_cancelled,
					Toast.LENGTH_LONG).show();
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.previous_strip:
			mStripManager.previous_strip();
			break;
		case R.id.next_strip:
			mStripManager.next_strip();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.img_quality:
			View v = getActivity().findViewById(R.id.img_quality);
			showImgQualityPopup(v);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showImgQualityPopup(View v) {
		// TODO Auto-generated method stub
		if (popup == null) {
			popup = new PopupMenu(getActivity(), v);

			popup.setOnMenuItemClickListener(this);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.image_quality_selection, popup.getMenu());
		}
		popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.quality_low:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mStripManager.setImgQuality(ImageQualityEnum.Low);
			mStripManager.reloadStrip();
			return true;
		case R.id.quality_normal:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mStripManager.setImgQuality(ImageQualityEnum.Normal);
			mStripManager.reloadStrip();
			return true;
		case R.id.quality_high:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mStripManager.setImgQuality(ImageQualityEnum.High);
			mStripManager.reloadStrip();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
