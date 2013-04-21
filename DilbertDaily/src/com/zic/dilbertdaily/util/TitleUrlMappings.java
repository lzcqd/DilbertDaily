package com.zic.dilbertdaily.util;

import java.util.HashMap;

public class TitleUrlMappings {
	HashMap<String, DilbertImageUrl> mappings = new HashMap<String, DilbertImageUrl>();
	
	public void addMapping(String title,DilbertImageUrl url){
		mappings.put(title, url);
	}
	
	public DilbertImageUrl getMapping(String title){
		return mappings.get(title);
	}
}
