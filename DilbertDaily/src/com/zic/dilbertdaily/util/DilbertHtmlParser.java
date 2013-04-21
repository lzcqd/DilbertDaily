package com.zic.dilbertdaily.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class DilbertHtmlParser {
	public String getImageUrl(String htmlUrl) throws IOException{
		Document doc = Jsoup.connect(htmlUrl).get();
		Element gifs = doc.select("img[src$=.gif").first();
		String imageUrl = gifs.attr("src");
		return imageUrl;
	}
}
