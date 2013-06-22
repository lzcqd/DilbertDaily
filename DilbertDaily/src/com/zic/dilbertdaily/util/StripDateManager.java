package com.zic.dilbertdaily.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.zic.dilbertdaily.data.ColorConstants;

import android.graphics.drawable.Drawable;

public class StripDateManager {
	private final SimpleDateFormat dilbertDateFormat = new SimpleDateFormat(
			"MMMMM d, yyyy");
	private final SimpleDateFormat returnDateStringFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private Date latestStripDate;
	private Calendar latestStripCal;
	private Calendar currentStripCal;
	private Date rollbackStripDate;

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
		String dateString = dilbertTitle.replace("Comic for ", "");
		return dilbertDateFormat.parse(dateString);
	}

	public String prevStripDate() {
		rollbackStripDate = currentStripCal.getTime();
		currentStripCal.add(Calendar.DAY_OF_MONTH, -1);
		Date date = currentStripCal.getTime();
		return returnDateStringFormat.format(date);
	}

	public String nextStripDate() {
		rollbackStripDate = currentStripCal.getTime();
		if (currentStripCal.compareTo(latestStripCal) >= 0) {
			return null;
		}
		currentStripCal.add(Calendar.DAY_OF_MONTH, 1);
		Date date = currentStripCal.getTime();
		return returnDateStringFormat.format(date);
	}

	public String getCurrDateTitle() {
		Date date = currentStripCal.getTime();
		return "Comic for " + dilbertDateFormat.format(date);
	}
	
	public void rollbackDate(){
		currentStripCal.setTime(rollbackStripDate);
	}
	
	public int getCurrDateColor(){
		int dayOfWeek = currentStripCal.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek){
			case 1: return ColorConstants.PURPLE;
			case 2: return ColorConstants.RED;
			case 3: return ColorConstants.ORANGE;
			case 4: return ColorConstants.YELLOW;
			case 5: return ColorConstants.GREEN;
			case 6: return ColorConstants.BLUE;
			case 7: return ColorConstants.AZURE;
			default: return ColorConstants.GREY;
		}
			
	}

}
