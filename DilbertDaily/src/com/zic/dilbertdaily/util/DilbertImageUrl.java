package com.zic.dilbertdaily.util;

import com.zic.dilbertdaily.data.ImageQualityEnum;

public class DilbertImageUrl {
	private String lowQualityUrl;
	private String normalQualityUrl;
	private String highQualityUrl;

	public DilbertImageUrl(String imageUrl){
		if (imageUrl.contains("print")){
			setLowQualityUrl(imageUrl);
			setNormalQualityUrl(imageUrl.replace("print.", ""));
			setHighQualityUrl(imageUrl.replace("print", "zoom"));
		}
		else if (imageUrl.contains("zoom")){
			setLowQualityUrl(imageUrl.replace("zoom", "print"));
			setNormalQualityUrl(imageUrl.replace("zoom", ""));
			setHighQualityUrl(imageUrl);
		}
		else{
			setLowQualityUrl(imageUrl.replace("gif", "print.gif"));
			setNormalQualityUrl(imageUrl);
			setHighQualityUrl(imageUrl.replace("gif", "zoom.gif"));
		}
	}
	
	public String getUrl(ImageQualityEnum quality){
		switch (quality){
		case Low:
			return lowQualityUrl;
		case Normal:
			return normalQualityUrl;
		case High:
			return highQualityUrl;
		default:
			return null;
		}
	}

	public void setLowQualityUrl(String lowQualityUrl) {
		this.lowQualityUrl = lowQualityUrl;
	}

	public void setNormalQualityUrl(String normalQualityUrl) {
		this.normalQualityUrl = normalQualityUrl;
	}

	public void setHighQualityUrl(String highQualityUrl) {
		this.highQualityUrl = highQualityUrl;
	}
}
