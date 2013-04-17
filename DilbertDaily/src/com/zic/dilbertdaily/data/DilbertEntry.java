package com.zic.dilbertdaily.data;

public class DilbertEntry {
	public String title;
	public String link;
	private String description;
	
	public DilbertEntry(String _title, String _link,String _description){
		title = _title;
		link = _link;
		description = GetUrl(_description);
	}
	
	public String GetImgUrl(ImageQualityEnum quality){
		switch (quality){
			case Low:
				return description;
			case Normal:				
				return description.replace("print.", "");
			case High:				
				return description.replace("print", "zoom");
			default:
				return null;
		}
	}
	
	private String GetUrl(String desc){
		String url = null;
		int start = desc.indexOf('"');
		int end = desc.indexOf('"', start+1);
		url = desc.substring(start+1, end);
		return url;
	}
}
