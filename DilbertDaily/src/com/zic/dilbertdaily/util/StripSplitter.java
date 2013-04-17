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
			SplitLowQualityImg(img);
			break;
		case Normal:
			SplitNormalQualityImg(img);
			break;
		case High:
			SplitHighQualityImg(img);
			break;
		default:
			break;
	}
	}

	private void SplitHighQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		
	}

	private void SplitNormalQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		
	}

	private void SplitLowQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		if (img.getHeight() == ImgLowQualityThreePages){
			
			SplitImgThreePages(img, ImgLowQualityWidth, ImgLowQualityThreePages);
		}
		else if (img.getHeight() == ImgLowQualityEightPages){
			
			SplitImgEightPages(img, ImgLowQualityWidth, ImgLowQualityEightPages);
		}
	}
	
	private void SplitImgThreePages(Bitmap img, int imgWidth, int imgHeight){
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
	
	private void SplitImgEightPages(Bitmap img, int imgWidth, int imgHeight){
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
	
	public int GetTotalPages(){
		return StripImages.length;
	}
	
	public Bitmap GetCurrentPage(){
		return StripImages[currentPage];
	}
	
	public Bitmap GetPreviousPage(){
		if (currentPage>0){
			currentPage -= 1;
		}
		return StripImages[currentPage];
	}
	
	public Bitmap GetNextPage(){
		if (currentPage<GetTotalPages()){
			currentPage += 1;
		}
		return StripImages[currentPage];
	}
	
	public int GetCurrentPageCount(){
		return currentPage+1;
	}
	
}
