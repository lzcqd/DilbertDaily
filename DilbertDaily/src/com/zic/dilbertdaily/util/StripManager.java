package com.zic.dilbertdaily.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.*;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class StripManager {
	private ViewPager mViewPager;
	private TextView mCurrentStripPositionView, mStripNameView, mErrorTextView;
	private LinearLayout dateContainer;
	private Context currentContext;
	private FragmentManager mFragmentManager;
	List<StateListener> listeners = new ArrayList<StateListener>();
	private static final String DilbertFeedUrl = "http://feed.dilbert.com/dilbert/daily_strip?format=xml";
	private static final String DilberDateUrlPrefix = "http://dilbert.com/strips/comic/";
	private static final String DilbertBaseUrl = "http://dilbert.com";
	protected ImageQualityEnum mQuality = ImageQualityEnum.Low;
	private StripSplitter splitter = new StripSplitter();
	private StripDateManager dateManager = new StripDateManager();
	private DilbertHtmlParser htmlParser = new DilbertHtmlParser();
	private TitleUrlMappings loadedTitleUrlMappings = new TitleUrlMappings();
	private StripAdapter mAdapter;
	private DownloadXmlTask downloadXmlTask;
	private DownloadImgTask downloadImgTask;
	private GetImgUrlFromHtmlTask getImgUrlFromHtmlTask;

	public StripManager(Context context, FragmentManager fm,LinearLayout dateLayout,
			TextView... textViews) {
		currentContext = context;
		mFragmentManager = fm;
		mStripNameView = textViews[0];
		mCurrentStripPositionView = textViews[1];
		mErrorTextView = textViews[2];
		mAdapter = new StripAdapter(fm, splitter.getImages());
		dateContainer = dateLayout;
	}

	public void setViewPager(ViewPager viewPager) {
		mViewPager = viewPager;
		viewPager.setAdapter(mAdapter);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
	}

	public void start() {

		downloadXmlTask = new DownloadXmlTask();

		downloadXmlTask.execute(DilbertFeedUrl);

	}

	private void downloadStrip(String stripUrl) {

		downloadImgTask = new DownloadImgTask();

		downloadImgTask.execute(stripUrl);

	}

	public void addListener(StateListener add) {
		listeners.add(add);
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
				if (isCancelled()) {
					return null;
				}
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
				String latestStripTitle = result.get(0).title;
				if (loadedTitleUrlMappings.getMapping(latestStripTitle) == null) {
					for (DilbertEntry entry : result) {
						loadedTitleUrlMappings.addMapping(entry.title,
								new DilbertImageUrl(entry.description));
					}
					dateManager.initStripDate(latestStripTitle);
					mStripNameView.setText(latestStripTitle);
					
					setDateBackgroundColor();
					
					DilbertImageUrl url = loadedTitleUrlMappings
							.getMapping(latestStripTitle);
					downloadStrip(url.getUrl(mQuality));
				} else {
					raiseStateChangedEvent(StateEnum.StripLoaded,
							StateEnum.AlreadyLoadedLatest);
				}
			} else {
				if (error != null) {
					if (error instanceof NetworkErrorException) {
						setErrorText("No Network Connection.");
					} else if (error instanceof XmlPullParserException) {
						setErrorText("Could not parse xml.");
					} else if (error instanceof IOException) {
						setErrorText("IO Exception occurred.");
					}
					for (StateListener sl : listeners) {
						sl.stateChanged(new StateInfo(StateEnum.Loading,
								StateEnum.Error));
					}
				}
			}
		}

		

		@Override
		protected void onCancelled() {
			// dateManager.rollbackDate();
			// raiseStateChangedEvent(StateEnum.Loading, StateEnum.Cancelled);
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
				if (isCancelled()) {
					return null;
				}
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
				splitter.split(bitmap, mQuality);

				mAdapter.setImages(splitter.getImages());
				mAdapter.notifyDataSetChanged();
				mViewPager.setCurrentItem(0, true);
				mViewPager.invalidate();
				raiseStateChangedEvent(StateEnum.Loading, StateEnum.StripLoaded);
			}
		}

		@Override
		protected void onCancelled() {
			// dateManager.rollbackDate();
			// mStripNameView.setText(dateManager.getCurrDateTitle());
			// raiseStateChangedEvent(StateEnum.Loading, StateEnum.Cancelled);
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

	private void setErrorText(String errorMsg) {
		mErrorTextView.setText(errorMsg + "\nClick to Refresh");
	}

	private void updateStripPositionCount(int currentPos) {
		if (splitter != null && splitter.getTotalPages() != 1) {
			mCurrentStripPositionView.setText(currentPos + "/"
					+ splitter.getTotalPages());
		}
	}

	private class GetImgUrlFromHtmlTask extends AsyncTask<String, Void, String> {
		Exception error;

		@Override
		protected void onPreExecute() {
			raiseStateChangedEvent(StateEnum.StripLoaded, StateEnum.Loading);
		}

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			try {
				if (isCancelled()) {
					return null;
				}
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
				mStripNameView.setText(title);
				setDateBackgroundColor();
				downloadStrip(url.getUrl(mQuality));
			} else {

			}
		}

		@Override
		protected void onCancelled() {
			// dateManager.rollbackDate();
			// raiseStateChangedEvent(StateEnum.Loading, StateEnum.Cancelled);
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
			mStripNameView.setText(title);
			setDateBackgroundColor();
			downloadStrip(cachedUrl.getUrl(mQuality));
		} else {
			downloadHtml(dateString);
		}

	}

	public void next_strip() {
		String dateString = dateManager.nextStripDate();
		if (dateString == null) {
			start();
			return;
		}
		String title = dateManager.getCurrDateTitle();
		DilbertImageUrl cachedUrl = loadedTitleUrlMappings.getMapping(title);
		if (cachedUrl != null) {
			mStripNameView.setText(title);
			setDateBackgroundColor();
			downloadStrip(cachedUrl.getUrl(mQuality));
		} else {
			downloadHtml(dateString);
		}

	}

	private void downloadHtml(String dateString) {
		String link = get_xml_by_date(dateString);

		getImgUrlFromHtmlTask = new GetImgUrlFromHtmlTask();

		getImgUrlFromHtmlTask.execute(link);
	}

	public void onCancelLoading() {
		if (isRunning(downloadXmlTask)) {
			cancelAsyncTask(downloadXmlTask);
			dateManager.rollbackDate();
			raiseStateChangedEvent(StateEnum.Loading, StateEnum.Cancelled);
			downloadXmlTask = null;
		}
		if (isRunning(getImgUrlFromHtmlTask)) {
			cancelAsyncTask(getImgUrlFromHtmlTask);
			dateManager.rollbackDate();
			raiseStateChangedEvent(StateEnum.Loading, StateEnum.Cancelled);
			getImgUrlFromHtmlTask = null;
		}
		if (isRunning(downloadImgTask)) {
			cancelAsyncTask(downloadImgTask);
			dateManager.rollbackDate();
			mStripNameView.setText(dateManager.getCurrDateTitle());
			setDateBackgroundColor();
			raiseStateChangedEvent(StateEnum.Loading, StateEnum.Cancelled);
			downloadImgTask = null;
		}

	}

	private boolean isRunning(AsyncTask asyncTask) {
		return asyncTask != null
				&& asyncTask.getStatus() == AsyncTask.Status.RUNNING;
	}

	private void cancelAsyncTask(AsyncTask asyncTask) {
		// TODO Auto-generated method stub
		asyncTask.cancel(true);
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
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			updateStripPositionCount(position + 1);
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
	
	private void setDateBackgroundColor() {
		Drawable bg = dateContainer.getBackground();
		if (bg instanceof GradientDrawable ){
			GradientDrawable grad = (GradientDrawable) bg;
			int color = dateManager.getCurrDateColor();
			grad.setColor(color);
		}
	}
	
	public void setImgQuality(ImageQualityEnum quality){
		mQuality = quality;
	}
	
	public void reloadStrip(){
		String today = dateManager.getCurrDateTitle();
		String url = loadedTitleUrlMappings.getMapping(today).getUrl(mQuality);
		downloadStrip(url);	
	}
}
