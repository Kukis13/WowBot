package lukaszja.wowbot.tests;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import lukaszja.wowbot.Utils;

public class FishFinder {
	
	
	private boolean qualifiesAsBlue(double[] pixel) {
		//B G R
		double blue = pixel[0];
		double green = pixel[1];
		double red = pixel[2];
		
		if(blue > 70 && blue < 130)
			if(green > 18 && green < 50)
				if(red == 0)
					return true;
		
		if(blue > 50 && blue < 130)
			if(isSimiliar(green, blue))
				if(red + 35 < blue)
					return true;
		
		if(blue > 20 && blue < 60)
			if(isSimiliar(green, blue))
				if(red < 20)
					return true;
		return false;
	}
	private boolean isSimiliar(double green, double blue) {
		if(green == 0 && blue == 0) {
			return true;
		}
		if(blue == 0 && green > 10) {
			return false;
		}
		if(green == 0 && blue > 10) {
			return false;
		}
		if(green == 0 || blue == 0)
			return true;
		
		double diff = Math.abs(green - blue);
		if(green >= blue) {
			return diff / green < 0.2;
		}
		if(blue > green) {
			return diff / green < 0.2;
		}
		return false;
	}
	public boolean isFishCatching(Mat oldImage, Mat newImage) {
        Mat diffToProcess = new Mat();
        Mat cutImage = new Mat(oldImage, new Rect(0, oldImage.height() / 2, oldImage.width(), oldImage.height() / 2));
        Mat cutImage2 = new Mat(newImage, new Rect(0, oldImage.height() / 2, oldImage.width(), oldImage.height() / 2));
		Core.subtract(cutImage, cutImage2, diffToProcess);
		double blue = 0.0, count = 0.0;
		
		for (int i = 0; i < diffToProcess.rows(); i++) {
			for (int j = 0; j < diffToProcess.cols(); j++) {
				double[] pixel = diffToProcess.get(i, j);
				if (qualifiesAsBlue(pixel)) {
					blue++;
				}
				count++;
			}
		}
		System.out.println("Detected fish? " + blue / count);
		if(blue / count > 0.02) {

			return true;
		}
		return false;

	}

}

