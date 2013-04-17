package com.zic.dilbertdaily.ui;

import com.zic.dilbertdaily.R;
import com.zic.dilbertdaily.util.StateInfo;
import com.zic.dilbertdaily.util.StateListener;
import com.zic.dilbertdaily.util.StripManager;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StripFragment extends Fragment implements StateListener {
	private static final String TAG = "StripFragment";
	private ImageView StripView;
	private TextView StripPositionCount, StripName;
	private RelativeLayout LoadingPanel;
	private LinearLayout ErrorPanel;
	private StripManager StripManager;

	public StripFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.strip_fragment, container,
				false);

		StripView = (ImageView) v.findViewById(R.id.stripContainer);
		// StripView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(),
		// R.drawable.empty_photo));
		SetupImageGestureCapture(StripView);

		StripPositionCount = (TextView) v.findViewById(R.id.stripPosition);
		StripName = (TextView) v.findViewById(R.id.stripName);

		LoadingPanel = (RelativeLayout) v.findViewById(R.id.loadingPanel);

		ErrorPanel = (LinearLayout) v.findViewById(R.id.errorPanel);

		TextView ErrorTextView = (TextView) v.findViewById(R.id.errorText);

		StripManager = new StripManager(getActivity(), StripView, StripName,
				StripPositionCount, ErrorTextView);

		StripManager.AddListener(this);

		StripManager.Start();
		// ShowLoadingViews();
		// ShowErrorViews();

		return v;
	}

	private void SetupImageGestureCapture(ImageView imageView) {
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
								StripManager.NextPage();
							} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
									&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
								StripManager.PreviousPage();
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

	private void ShowLoadingViews() {
		StripView.setAlpha((float) 0.3);
		StripName.setAlpha((float) 0.3);
		StripPositionCount.setAlpha((float) 0.3);
		ErrorPanel.setVisibility(View.GONE);
		LoadingPanel.setVisibility(View.VISIBLE);
	}

	private void ShowErrorViews() {
		StripView.setVisibility(View.GONE);
		StripPositionCount.setVisibility(View.GONE);
		StripName.setVisibility(View.GONE);
		ErrorPanel.setVisibility(View.VISIBLE);
		LoadingPanel.setVisibility(View.GONE);
	}

	private void ShowStripViews() {
		StripView.setAlpha((float) 1.0);
		StripPositionCount.setAlpha((float) 1.0);
		LoadingPanel.setVisibility(View.GONE);
		ErrorPanel.setVisibility(View.GONE);
	}

	private void ShowStripName() {
		StripName.setAlpha((float) 1.0);
	}

	@Override
	public void StateChanged(StateInfo stateInfo) {
		switch (stateInfo.CurrentState) {
		case Loading:
			ShowLoadingViews();
			break;
		case Error:
			ShowErrorViews();
			break;
		case StripNameLoaded:
			ShowStripName();
			break;
		case StripLoaded:
			ShowStripViews();
			break;
		default:
			break;
		}

	}
}
