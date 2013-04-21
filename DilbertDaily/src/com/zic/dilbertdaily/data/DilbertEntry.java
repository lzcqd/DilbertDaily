package com.zic.dilbertdaily.data;

public class DilbertEntry {
	public String title;
	public String description;
	
	public DilbertEntry(String _title,String _description){
		title = _title;
		description = GetUrl(_description);
	}
	
	private String GetUrl(String desc){
		String url = null;
		int start = desc.indexOf('"');
		int end = desc.indexOf('"', start+1);
		url = desc.substring(start+1, end);
		return url;
	}
}
