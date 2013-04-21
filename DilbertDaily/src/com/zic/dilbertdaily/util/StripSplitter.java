package com.zic.dilbertdaily.util;

import com.zic.dilbertdaily.data.ImageQualityEnum;

import android.graphics.Bitmap;

public class StripSplitter {
	private int currentPage=0;;
	private Bitmap[] StripImages;
	public static final int ImgLowQualityWidth = 560, ImgLowQualityThreePages = 174, ImgLowQualityEightPages = 251;
	
	public StripSplitter(){}
	
	public StripSplitter(Bitmap img, ImageQualityEnum quality){
		split(img, quality);
	}
	
	public void split(Bitmap img, ImageQualityEnum quality){
		switch (quality) {
		case Low:
			splitLowQualityImg(img);
			break;
		case Normal:
			splitNormalQualityImg(img);
			break;
		case High:
			splitHighQualityImg(img);
			break;
		default:
			break;
	}
	}

	private void splitHighQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		
	}

	private void splitNormalQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		
	}

	private void splitLowQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		if (img.getHeight() == ImgLowQualityThreePages){
			
			splitImgThreePages(img, ImgLowQualityWidth, ImgLowQualityThreePages);
		}
		else if (img.getHeight() == ImgLowQualityEightPages){
			
			splitImgEightPages(img, ImgLowQualityWidth, ImgLowQualityEightPages);
		}
	}
	
	private void splitImgThreePages(Bitmap img, int imgWidth, int imgHeight){
		StripImages = new Bitmap[3];
		for (int i=0;i<3;i++){
			int x;
			
			x = imgWidth / 3 * i + i * 5;
			
			int width;
			if (i<2) {
				width = imgWidth / 3 - 7;
			}
			else{
				width = imgWidth / 3 - 8;
			}
			StripImages[i] = Bitmap.createBitmap(img, x, 0, width, imgHeight);
		}
	}
	
	private void splitImgEightPages(Bitmap img, int imgWidth, int imgHeight){
		StripImages = new Bitmap[8];
		for (int i=0;i<8;i++){
			int row = i / 4;
			int col = i % 4;
			
			int x;
			if (col ==3 ){
				x = imgWidth / 4 * col + 2;
			}
			else{
				x = imgWidth / 4 * col + 1 * col;
			}
			int y = imgHeight / 2 * row + 4 * row;
			int width = imgWidth / 4 - 2;
			int height = imgHeight / 2 - 3;				
			StripImages[i] = Bitmap.createBitmap(img,x,y,width,height);
		}
	}
	
	public int getTotalPages(){
		return StripImages.length;
	}
	
	public Bitmap getCurrentPage(){
		return StripImages[currentPage];
	}
	
	public Bitmap getPreviousPage(){
		if (currentPage>0){
			currentPage -= 1;
		}
		return StripImages[currentPage];
	}
	
	public Bitmap getNextPage(){
		if (currentPage<getTotalPages()){
			currentPage += 1;
		}
		return StripImages[currentPage];
	}
	
	public int getCurrentPageCount(){
		return currentPage+1;
	}
	
	public void resetPage(){
		currentPage = 0;
	}
	
}
