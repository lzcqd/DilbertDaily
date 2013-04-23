package com.zic.dilbertdaily.ui;



import com.zic.dilbertdaily.R;
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

import android.support.v4.view.ViewPager;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

public class StripFragment extends Fragment implements StateListener, OnClickListener{
	private static final String TAG = "StripFragment";
	private ImageView mStripView;
	private TextView mStripPositionCount, mStripName;
	private LinearLayout ErrorPanel;
	private LinearLayout mStripDateLayout;
	private StripManager mStripManager;
	private LoadingDialogFragment mLoadingDialog;
	private ViewPager mPager;
	//private StripAdapter mAdapter;
	
	public StripFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		//mAdapter = new StripAdapter(getFragmentManager());
		mPager = (ViewPager) getView().findViewById(R.id.stripPager);
		mStripManager.setViewPager(mPager);
		//mPager.setAdapter(mAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setLoadingDialog();
	}

	private void setLoadingDialog() {
		mLoadingDialog = new LoadingDialogFragment(getActivity(),android.R.style.Theme_Black_NoTitleBar);
		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(150);
		mLoadingDialog.getWindow().setBackgroundDrawable(d);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.strip_fragment, container,
				false);
		
		//mStripView = (ImageView) v.findViewById(R.id.stripContainer);
		// StripView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(),
		// R.drawable.empty_photo));
		//setupImageGestureCapture(mStripView);
		
		mStripPositionCount = (TextView) v.findViewById(R.id.stripPosition);

		mStripName = (TextView) v.findViewById(R.id.stripName);
		
		ErrorPanel = (LinearLayout) v.findViewById(R.id.errorPanel);
		
		mStripDateLayout = (LinearLayout) v.findViewById(R.id.strip_date_layout);
		
		TextView ErrorTextView = (TextView) v.findViewById(R.id.errorText);
		
		ImageButton prevStripButton = (ImageButton) v.findViewById(R.id.previous_strip);
		prevStripButton.setOnClickListener(this);
		
		ImageButton nextStripButton = (ImageButton) v.findViewById(R.id.next_strip);
		nextStripButton.setOnClickListener(this);
		
		mStripManager = new StripManager(getActivity(), getFragmentManager() , mStripName,
				mStripPositionCount, ErrorTextView);
		
		mStripManager.AddListener(this);
		
		mStripManager.Start();
		// ShowLoadingViews();
		// ShowErrorViews();

		return v;
	}


	private void setupImageGestureCapture(ImageView imageView) {
		// TODO Auto-generated method stub
		final GestureDetector gesture = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						final int SWIPE_MIN_DISTANCE = 50;
						final int SWIPE_MAX_OFF_PATH = 250;
						final int SWIPE_THRESHOLD_VELOCITY = 100;
						try {
							if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
								return false;
							if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
									&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
								mStripManager.NextPage();
							} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
									&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
								mStripManager.PreviousPage();
							}
						} catch (Exception e) {
							// nothing
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});

		imageView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gesture.onTouchEvent(event);
			}
		});
	}

	private void showLoadingViews() {
		mLoadingDialog.show();		
		ErrorPanel.setVisibility(View.GONE);
	}

	private void showErrorViews() {
		//mStripView.setVisibility(View.GONE);
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
			Toast.makeText(getActivity(), R.string.already_latest_strip, Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}

	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.previous_strip:
				mStripManager.previous_strip();
				break;
			case R.id.next_strip:
				mStripManager.next_strip();
		}
		
	}
	
	
}
