package com.zic.dilbertdaily.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.zic.dilbertdaily.data.DilbertEntry;

import android.util.Xml;

public class DilbertXmlParser {
private static final String ns = null;
	
	public List parse(InputStream in) throws XmlPullParserException, IOException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			parser.nextTag();
			return readFeed(parser);
		}
		finally{
			in.close();
		}
	}
	
	private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException{
		List entries = new ArrayList();
		
		parser.require(XmlPullParser.START_TAG, ns, "channel");
		while (parser.next()!=XmlPullParser.END_TAG){
			if (parser.getEventType()!=XmlPullParser.START_TAG){
				continue;
			}
			
			String name = parser.getName();
			
			if (name.equals("item")){
				entries.add(readEntry(parser));
			}
			else{
				skip(parser);
			}
		}
		return entries;
	}
	
	private DilbertEntry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "item");
		String title = null;
		String description = null;
		
		while (parser.next()!=XmlPullParser.END_TAG){
			if (parser.getEventType()!=XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")){
				title = readTitle(parser);
			}
			
			else if (name.equals("description")){
				description = readDescription(parser);
			}
			else{
				skip(parser);
			}		
		}
		return new DilbertEntry(title,description);
	}
	
	private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}
		
	private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "description");
		String description = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "description");
		return description;
	}
	
	private String readText(XmlPullParser parser) throws XmlPullParserException, IOException{
		String result = "";
		if (parser.next()==XmlPullParser.TEXT){
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException{
		if (parser.getEventType()!=XmlPullParser.START_TAG){
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth!=0){
			switch (parser.next()){
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
