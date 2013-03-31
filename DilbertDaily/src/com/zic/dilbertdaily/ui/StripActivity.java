package com.zic.dilbertdaily.ui;

import com.zic.dilbertdaily.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class StripActivity extends FragmentActivity {
	private static final String TAG = "StripActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getSupportFragmentManager().findFragmentByTag(TAG)==null){
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(android.R.id.content, new StripFragment(),TAG);
			ft.commit();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
