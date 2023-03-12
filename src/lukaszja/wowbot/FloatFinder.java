package lukaszja.wowbot;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import lukaszja.wowbot.FloatFinder.FoundFloatInfo;

public class FloatFinder {

	private static int TOTAL_WIDTH = Utils.screenWidth();
	private static int TOTAL_HEIGHT = Utils.screenHeight();
	private static int VIEWPORT_X_START = Utils.screenWidth() / 6;
	private static int VIEWPORT_Y_START = Utils.screenHeight() / 5;
	static Rect viewPort = new Rect(VIEWPORT_X_START, VIEWPORT_Y_START, TOTAL_WIDTH - VIEWPORT_X_START * 2,
			(TOTAL_HEIGHT / 4) * 2);

	static int errors = 0;
	public FoundFloatInfo find(Mat noFloatImage, BufferedImage originalImage) {
		Mat matOriginal = Utils.bufferedImage2Mat(originalImage);

		Mat diffToProcess = new Mat();
		Core.subtract(noFloatImage, matOriginal, diffToProcess);
		diffToProcess = new Mat(diffToProcess, viewPort);
		for (int i = 0; i < diffToProcess.rows(); i++) {
			for (int j = 0; j < diffToProcess.cols(); j++) {
				double[] pixel = diffToProcess.get(i, j);
				if (isGrayscale(pixel)) {
					diffToProcess.put(i, j, 0, 0, 0);
				} else if (pixel[0] > pixel[1] && pixel[1] > pixel[2] && pixel[0] > 50 && pixel[2] + 30 < pixel[1]) {
					diffToProcess.put(i, j, 255, 255, 255);
				} else {
					diffToProcess.put(i, j, 0, 0, 0);
				}
			}
		}
		
		List<MatOfPoint> countours = new ArrayList<>();
		Imgproc.cvtColor(diffToProcess, diffToProcess, Imgproc.COLOR_BGR2GRAY);
		Imgproc.findContours(diffToProcess, countours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		if(countours.size() == 0) {
			Utils.saveImage(Utils.Mat2BufferedImage(matOriginal));
			errors++;
			if(errors > 100) {
				System.exit(0);
			}
			return null;
		}
		
		
		Rect boundingRect = countours.stream().map(Imgproc::boundingRect).sorted(comparator).findFirst().get();

		boundingRect.x += VIEWPORT_X_START;
		boundingRect.y += VIEWPORT_Y_START;
		Utils.enlargeRect(boundingRect, 15);
		return new FoundFloatInfo(boundingRect, matOriginal);
	}

	private boolean isGrayscale(double[] pixel) {
		return isSimiliar(pixel[0], pixel[1]) && isSimiliar(pixel[0], pixel[2]) && isSimiliar(pixel[1], pixel[2]);
	}

	private boolean isSimiliar(double d, double e) {
		double abs = Math.abs(e - d);
		if (abs / d < 0.4) {
			return true;
		}
		return false;
	}
	
	Comparator<? super Rect> comparator = new Comparator<Rect>() {

		@Override
		public int compare(Rect o1, Rect o2) {
			if(o1.width == o2.width) {
				return 0;
			}
			return o1.width > o2.width ? -1 : 1;
		}
	};

	public static class FoundFloatInfo {
		public Point middle;
		public Rect rect;
		public Mat image;

		public FoundFloatInfo(Rect boundingRect, Mat image) {
			this.middle = new Point(boundingRect.x + boundingRect.width / 2, boundingRect.y + boundingRect.height / 2);
			this.rect = boundingRect;
			this.image = new Mat(image, boundingRect);
		}

	}
}
