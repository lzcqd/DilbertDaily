package com.zic.dilbertdaily.util;

import com.zic.dilbertdaily.data.ImageQualityEnum;

import android.graphics.Bitmap;

public class StripSplitter {
	private int currentPage = 0;;
	private Bitmap[] StripImages;
	public static final int ImgLowQualityWidth = 560,
			ImgLowQualityThreePages = 174, ImgLowQualityEightPages = 251,
			ImgNormalQualityThreePagesWidth = 640,ImgNormalQualityThreePagesHeight = 199,
			ImgNormalQualityEightPagesWidth = 1757,ImgNormalQualityEightPagesHeight = 191,
			ImgHighQualityWidth = 1000,
			ImgHighQualityThreePagesHeight = 311,ImgHighQualityEightPagesHeight = 449;

	public StripSplitter() {
	}

	public StripSplitter(Bitmap img, ImageQualityEnum quality) {
		split(img, quality);
	}

	public void split(Bitmap img, ImageQualityEnum quality) {
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
		if (img.getHeight() == ImgHighQualityThreePagesHeight) {
			splitImgThreePagesHigh(img, ImgHighQualityWidth, ImgHighQualityThreePagesHeight);
		} else if (img.getHeight() == ImgHighQualityEightPagesHeight) {
			splitImgEightPagesHigh(img, ImgHighQualityWidth, ImgHighQualityEightPagesHeight);
		}
	}

	private void splitImgEightPagesHigh(Bitmap img, int imgWidth,
			int imgHeight) {
		// TODO Auto-generated method stub
		StripImages = new Bitmap[8];
		for (int i = 0; i < 8; i++) {
			int row = i / 4;
			int col = i % 4;

			int x;
			int width;

			width = (imgWidth-48) / 4;
			x = width * col + 16 * col;
			
			int height = (imgHeight - 13) / 2;
			int y = height * row + 13 * row;
			
			StripImages[i] = Bitmap.createBitmap(img, x, y, width, height);
		}
	}

	private void splitImgThreePagesHigh(Bitmap img, int imgWidth,
			int imgHeight) {
		// TODO Auto-generated method stub
		StripImages = new Bitmap[3];
		for (int i = 0; i < 3; i++) {

			int x;
			int width;

			width = (imgWidth - 48) / 3;
			x = width * i + 24 * i;

			StripImages[i] = Bitmap.createBitmap(img, x, 0, width, imgHeight);
		}
	}

	private void splitNormalQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		if (img.getHeight() == ImgNormalQualityThreePagesHeight){
			splitImgThreePagesNormal(img, ImgNormalQualityThreePagesWidth, ImgNormalQualityThreePagesHeight);
		} else if (img.getHeight() == ImgNormalQualityEightPagesHeight){
			splitImgEightPagesNormal(img,ImgNormalQualityEightPagesWidth,ImgNormalQualityEightPagesHeight);
		}
	}


	private void splitLowQualityImg(Bitmap img) {
		// TODO Auto-generated method stub
		if (img.getHeight() == ImgLowQualityThreePages) {
			splitImgThreePagesLow(img, ImgLowQualityWidth, ImgLowQualityThreePages);
		} else if (img.getHeight() == ImgLowQualityEightPages) {
			splitImgEightPagesLow(img, ImgLowQualityWidth, ImgLowQualityEightPages);
		}
	}

	private void splitImgThreePagesLow(Bitmap img, int imgWidth, int imgHeight) {
		StripImages = new Bitmap[3];
		for (int i = 0; i < 3; i++) {
			int x;

			x = imgWidth / 3 * i + i * 5;

			int width;
			if (i < 2) {
				width = imgWidth / 3 - 7;
			} else {
				width = imgWidth / 3 - 8;
			}
			StripImages[i] = Bitmap.createBitmap(img, x, 0, width, imgHeight);
		}
	}

	private void splitImgEightPagesLow(Bitmap img, int imgWidth, int imgHeight) {
		StripImages = new Bitmap[8];
		for (int i = 0; i < 8; i++) {
			int row = i / 4;
			int col = i % 4;

			int x;
			int width;

			width = imgWidth / 4 - 6;
			x = imgWidth / 4 * col + 2 * col;

			int y = imgHeight / 2 * row + 4 * row;
			int height = imgHeight / 2 - 3;
			StripImages[i] = Bitmap.createBitmap(img, x, y, width, height);
		}
	}
	
	private void splitImgThreePagesNormal(Bitmap img,
			int imgWidth,
			int imgHeight) {
		// TODO Auto-generated method stub
		StripImages = new Bitmap[3];
		for (int i = 0; i < 3; i++) {

			int x;
			int width;

			width = (imgWidth - 28) / 3;
			x = width * i + 14 * i;

			StripImages[i] = Bitmap.createBitmap(img, x, 0, width, imgHeight);
		}
	}
	private void splitImgEightPagesNormal(Bitmap img,
			int imgWidth,
			int imgHeight) {
		// TODO Auto-generated method stub
		StripImages = new Bitmap[8];
		for (int i = 0; i < 8; i++) {

			int x;
			int width;

			width = (imgWidth - 77) / 8;
			x = width * i + 11 * i;

			StripImages[i] = Bitmap.createBitmap(img, x, 0, width, imgHeight);
		}
	}
	
	public int getTotalPages() {
		if (StripImages == null) {
			return 1;
		}
		return StripImages.length;
	}

	public Bitmap[] getImages() {
		return StripImages;
	}

}
