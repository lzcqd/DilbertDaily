package com.zic.dilbertdaily.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StripDateManager {
	private final SimpleDateFormat dilbertDateFormat = new SimpleDateFormat("MMM d, yyyy");
	private Date latestStripDate;
}
