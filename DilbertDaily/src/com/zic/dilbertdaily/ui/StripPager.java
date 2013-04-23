package com.zic.dilbertdaily.ui;

import com.zic.dilbertdaily.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class StripPager extends Fragment {
	private Bitmap image;

	public StripPager(Bitmap img) {
		image = img;
	}
	
	public StripPager(){}

	@Override
	public View onCreateView(LayoutInflater inflator, ViewGroup container,
			Bundle savedInstance) {
		final View v = inflator.inflate(R.layout.strip_pager_view_content,
				container, false);
		if (image != null) {
			ImageView imgView = (ImageView) v.findViewById(R.id.stripContainer);
			imgView.setImageBitmap(image);
		}
		return v;
	}
}
