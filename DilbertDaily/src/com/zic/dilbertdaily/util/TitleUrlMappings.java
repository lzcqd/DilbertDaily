package com.zic.dilbertdaily.util;

import java.util.Date;
import java.util.HashMap;

public class TitleUrlMappings {
	HashMap<String, DilbertImageUrl> mappings = new HashMap<String, DilbertImageUrl>();

	public void addMapping(String date, DilbertImageUrl url) {
		if (!mappings.containsKey(date)) {
			mappings.put(date, url);
		}
	}

	public DilbertImageUrl getMapping(String date) {
		return mappings.get(date);
	}
}
