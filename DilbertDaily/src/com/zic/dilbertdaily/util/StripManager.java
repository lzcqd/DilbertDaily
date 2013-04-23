package com.zic.dilbertdaily.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.zic.dilbertdaily.data.DilbertEntry;
import com.zic.dilbertdaily.data.ImageQualityEnum;
import com.zic.dilbertdaily.data.StateEnum;
import com.zic.dilbertdaily.ui.StripPager;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class StripManager {
	private ViewPager mViewPager;
	private TextView CurrentStripPositionView, StripNameView, ErrorTextView;
	private Context currentContext;
	private FragmentManager mFragmentManager;
	List<StateListener> listeners = new ArrayList<StateListener>();
	private static final String DilbertFeedUrl = "http://feed.dilbert.com/dilbert/daily_strip?format=xml";
	private static final String DilberDateUrlPrefix = "http://dilbert.com/strips/comic/";
	private static final String DilbertBaseUrl = "http://dilbert.com";
	protected ImageQualityEnum quality = ImageQualityEnum.Low;
	private StripSplitter splitter = new StripSplitter();
	private StripDateManager dateManager = new StripDateManager();
	private DilbertHtmlParser htmlParser = new DilbertHtmlParser();
	private TitleUrlMappings loadedTitleUrlMappings = new TitleUrlMappings();
	private StripAdapter mAdapter;

	// private StripPager mStripPager;

	public StripManager(Context context, FragmentManager fm,
			TextView... textViews) {
		currentContext = context;
		mFragmentManager = fm;
		StripNameView = textViews[0];
		CurrentStripPositionView = textViews[1];
		ErrorTextView = textViews[2];
		mAdapter = new StripAdapter(fm, splitter.getImages());
	}

	public void setViewPager(ViewPager viewPager) {
		mViewPager = viewPager;
		viewPager.setAdapter(mAdapter);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
	}

	public void Start() {
		new DownloadXmlTask().execute(DilbertFeedUrl);
	}

	private void DownloadStrip(String stripUrl) {
		new DownloadImgTask().execute(stripUrl);
	}

	public void AddListener(StateListener add) {
		listeners.add(add);
	}

	public void NextPage() {
		// mViewPager.setImageBitmap(splitter.getNextPage());
		// UpdateStripPositionCount();
	}

	public void PreviousPage() {
		// mViewPager.setImageBitmap(splitter.getPreviousPage());
		// UpdateStripPositionCount();
	}

	private class DownloadXmlTask extends
			AsyncTask<String, Void, List<DilbertEntry>> {
		Exception error;

		@Override
		protected void onPreExecute() {
			raiseStateChangedEvent(StateEnum.Initialised, StateEnum.Loading);
		}

		@Override
		protected List<DilbertEntry> doInBackground(String... urls) {
			try {

				return loadXmlFromNetwork(urls[0]);
			} catch (NetworkErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				error = e;
				return null;
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				error = e;
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				error = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<DilbertEntry> result) {
			if (result != null) {
				for (DilbertEntry entry : result) {
					loadedTitleUrlMappings.addMapping(entry.title,
							new DilbertImageUrl(entry.description));
				}
				dateManager.initStripDate(result.get(0).title);
				String title = dateManager.getCurrDateTitle();
				StripNameView.setText(title);
				DilbertImageUrl url = loadedTitleUrlMappings.getMapping(title);
				DownloadStrip(url.getUrl(quality));
			} else {
				if (error != null) {
					if (error instanceof NetworkErrorException) {
						SetErrorText("No Network Connection.");
					} else if (error instanceof XmlPullParserException) {
						SetErrorText("Could not parse xml.");
					} else if (error instanceof IOException) {
						SetErrorText("IO Exception occurred.");
					}
					for (StateListener sl : listeners) {
						sl.stateChanged(new StateInfo(StateEnum.Loading,
								StateEnum.Error));
					}
				}
			}
		}

	}

	private class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
		Exception error;

		@Override
		protected void onPreExecute() {
			raiseStateChangedEvent(StateEnum.StripNameLoaded, StateEnum.Loading);
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			InputStream is = null;
			Bitmap bitmap = null;

			try {
				is = downloadUrl(urls[0]);
				bitmap = BitmapFactory.decodeStream(is);
			} catch (NetworkErrorException e) {
				error = e;
				return null;
			} catch (Exception e) {
				error = e;
				return null;
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						error = e;
						return null;
					}
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				splitter.resetPage();
				splitter.split(bitmap, quality);
				mAdapter.setImages(splitter.getImages());
				mAdapter.notifyDataSetChanged();
				mViewPager.setCurrentItem(0, true);
				mViewPager.invalidate();
				// ImageContainer.setImageBitmap(splitter.getCurrentPage());
				// UpdateStripPositionCount();
				raiseStateChangedEvent(StateEnum.Loading, StateEnum.StripLoaded);
			}
		}

	}

	private void checkConnection(Context context) throws NetworkErrorException {
		final ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
			throw new NetworkErrorException();
		}

	}

	private List<DilbertEntry> loadXmlFromNetwork(String urlString)
			throws XmlPullParserException, IOException, NetworkErrorException {
		InputStream stream = null;
		DilbertXmlParser dilbertXmlParser = new DilbertXmlParser();
		List<DilbertEntry> entries = null;

		try {
			stream = downloadUrl(urlString);
			entries = dilbertXmlParser.parse(stream);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return entries;
	}

	private InputStream downloadUrl(String urlString) throws IOException,
			NetworkErrorException {
		URL url = new URL(urlString);
		checkConnection(currentContext);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);

		conn.connect();
		InputStream stream = conn.getInputStream();
		return stream;
	}

	private void SetErrorText(String errorMsg) {
		ErrorTextView.setText(errorMsg + "\nClick to Refresh");
	}

	private void UpdateStripPositionCount(int currentPos) {
		if (splitter != null && splitter.getTotalPages() != 1) {
			CurrentStripPositionView.setText(currentPos + "/"
					+ splitter.getTotalPages());
		}
	}

	private class getImgUrlFromHtmlTask extends AsyncTask<String, Void, String> {
		Exception error;

		@Override
		protected void onPreExecute() {
			raiseStateChangedEvent(StateEnum.StripLoaded, StateEnum.Loading);
		}

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			try {
				return htmlParser.getImageUrl(urls[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				error = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(String imgBaseUrl) {
			if (imgBaseUrl != null) {
				String imgUrl = get_img_url(imgBaseUrl);
				String title = dateManager.getCurrDateTitle();
				DilbertImageUrl url = new DilbertImageUrl(imgUrl);
				loadedTitleUrlMappings.addMapping(title, url);
				StripNameView.setText(title);
				DownloadStrip(url.getUrl(quality));
			} else {

			}
		}

	}

	private String get_xml_by_date(String date) {
		return DilberDateUrlPrefix + date + "/";
	}

	private String get_img_url(String imgUrl) {
		return DilbertBaseUrl + imgUrl;
	}

	private void raiseStateChangedEvent(StateEnum prevState, StateEnum currState) {
		for (StateListener sl : listeners) {
			StateInfo state = new StateInfo(prevState, currState);
			sl.stateChanged(state);
		}
	}

	public void previous_strip() {

		String dateString = dateManager.prevStripDate();
		String title = dateManager.getCurrDateTitle();
		DilbertImageUrl cachedUrl = loadedTitleUrlMappings.getMapping(title);
		if (cachedUrl != null) {
			StripNameView.setText(title);
			DownloadStrip(cachedUrl.getUrl(quality));
		} else {
			String link = get_xml_by_date(dateString);
			new getImgUrlFromHtmlTask().execute(link);
		}

	}

	public void next_strip() {
		String dateString = dateManager.nextStripDate();
		String title = dateManager.getCurrDateTitle();
		DilbertImageUrl cachedUrl = loadedTitleUrlMappings.getMapping(title);
		if (cachedUrl != null) {
			StripNameView.setText(title);
			DownloadStrip(cachedUrl.getUrl(quality));
		} else {
			String link = get_xml_by_date(dateString);
			new getImgUrlFromHtmlTask().execute(link);
		}

	}

	public class StripAdapter extends FragmentStatePagerAdapter {
		private Bitmap[] images;

		public StripAdapter(FragmentManager fm, Bitmap[] strips) {
			super(fm);
			// TODO Auto-generated constructor stub
			images = strips;
		}

		@Override
		public Fragment getItem(int pageNum) {
			// TODO Auto-generated method stub
			if (images != null) {
				// page.setImageView(img);
				return new StripPager(images[pageNum]);
			}
			return new StripPager();

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return splitter.getTotalPages();
		}

		@Override
		public int getItemPosition(Object obj) {
			return POSITION_NONE;
		}
		
		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object){
			super.setPrimaryItem(container, position, object);
			UpdateStripPositionCount(position+1);
		}

		public void setImages(Bitmap[] imgs) {
			images = imgs;
		}

	}

	public class DepthPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.75f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 0) { // [-1,0]
				// Use the default slide transition when moving to the left page
				view.setAlpha(1);
				view.setTranslationX(0);
				view.setScaleX(1);
				view.setScaleY(1);

			} else if (position <= 1) { // (0,1]
				// Fade the page out.
				view.setAlpha(1 - position);

				// Counteract the default slide transition
				view.setTranslationX(pageWidth * -position);

				// Scale the page down (between MIN_SCALE and 1)
				float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
						* (1 - Math.abs(position));
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}
}
