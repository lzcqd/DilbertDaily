package com.zic.dilbertdaily.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;


import com.zic.dilbertdaily.data.DilbertEntry;
import com.zic.dilbertdaily.data.ImageQualityEnum;
import com.zic.dilbertdaily.data.StateEnum;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StripManager {
	private ImageView ImageContainer;
	private TextView CurrentStripPositionView, StripNameView, ErrorTextView;
	private Context currentContext;
	List<StateListener> listeners = new ArrayList<StateListener>();
	public static final String DilbertFeedUrl = "http://feed.dilbert.com/dilbert/daily_strip?format=xml";
	private List<DilbertEntry> lastSevenDaysStrip;
	protected ImageQualityEnum quality = ImageQualityEnum.Low;
	private StripSplitter splitter = new StripSplitter();
	
	public StripManager(Context context, ImageView imageContainer,
			TextView... textViews) {
		currentContext = context;
		ImageContainer = imageContainer;
		StripNameView = textViews[0];
		CurrentStripPositionView = textViews[1];
		ErrorTextView = textViews[2];
	}

	public void Start() {
		for (StateListener sl : listeners) {
			StateInfo state = new StateInfo(StateEnum.Initialised,
					StateEnum.Loading);
			sl.StateChanged(state);
		}

		new DownloadXmlTask().execute(DilbertFeedUrl);
	}

	private void DownloadLatestStrip() {
		new DownloadImgTask().execute(lastSevenDaysStrip.get(0).GetImgUrl(quality));
	}

	public void AddListener(StateListener add) {
		listeners.add(add);
	}
	
	public void NextPage(){
		ImageContainer.setImageBitmap(splitter.GetNextPage());
		UpdateStripPositionCount();
	}
	
	public void PreviousPage(){
		ImageContainer.setImageBitmap(splitter.GetPreviousPage());
		UpdateStripPositionCount();
	}
	
	private class DownloadXmlTask extends
			AsyncTask<String, Void, List<DilbertEntry>> {
		Exception error;

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
				lastSevenDaysStrip = result;
				StripNameView.setText(lastSevenDaysStrip.get(0).title);
				for (StateListener sl : listeners) {
					sl.StateChanged(new StateInfo(StateEnum.Loading,
							StateEnum.StripNameLoaded));
				}
				DownloadLatestStrip();
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
						sl.StateChanged(new StateInfo(StateEnum.Loading,
								StateEnum.Error));
					}
				}
			}
		}

	}

	private class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
		Exception error;
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
				splitter.split(bitmap, quality);
				ImageContainer.setImageBitmap(splitter.GetCurrentPage());
				UpdateStripPositionCount();
				for (StateListener sl : listeners){
					sl.StateChanged(new StateInfo(StateEnum.StripNameLoaded,StateEnum.StripLoaded));
				}
			}
		}

	}

	private void CheckConnection(Context context) throws NetworkErrorException {
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

		String title = null;
		String link = null;
		String description = null;
		Calendar rightNow = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("MMM d h:mmaa");

		StringBuilder htmlString = new StringBuilder();
		htmlString.append("<h3>" + formatter.format(rightNow.getTime())
				+ "</h3>");

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
		CheckConnection(currentContext);
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
	
	private void UpdateStripPositionCount(){
		CurrentStripPositionView.setText(splitter.GetCurrentPageCount()+"/"+splitter.GetTotalPages());
	}
}
