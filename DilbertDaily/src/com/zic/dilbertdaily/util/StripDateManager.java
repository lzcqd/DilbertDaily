package com.zic.dilbertdaily.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StripDateManager {
	private final SimpleDateFormat dilbertDateFormat = new SimpleDateFormat(
			"MMMMM d, yyyy");
	private final SimpleDateFormat returnDateStringFormat = new SimpleDateFormat("yyyy-MM-dd");
	private Date latestStripDate;
	private Calendar latestStripCal;
	private Calendar currentStripCal;

	public StripDateManager() {
		latestStripCal = new GregorianCalendar();
		currentStripCal = new GregorianCalendar();
	}

	public void initStripDate(String stripDate) {
		try {
			Date date = dateConversion(stripDate);
			latestStripDate = date;
			latestStripCal.setTime(latestStripDate);
			currentStripCal.setTime(latestStripDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Date dateConversion(String dilbertTitle) throws ParseException {
		String dateString = dilbertTitle.substring(10);
		return dilbertDateFormat.parse(dateString);
	}

	
	public String prevStripDate(){
		currentStripCal.add(Calendar.DAY_OF_MONTH, -1);
		Date date = currentStripCal.getTime();
		return returnDateStringFormat.format(date);
	}
	
	public String nextStripDate(){
		currentStripCal.add(Calendar.DAY_OF_MONTH, 1);
		Date date = currentStripCal.getTime();
		return returnDateStringFormat.format(date);
	}
	
	public String getCurrDateTitle(){
		Date date = currentStripCal.getTime();
		return "Comic for " + dilbertDateFormat.format(date);
	}


}
