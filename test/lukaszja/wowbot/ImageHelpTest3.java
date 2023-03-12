package lukaszja.wowbot;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import lukaszja.wowbot.FloatFinder.FoundFloatInfo;

import org.junit.jupiter.api.BeforeAll;

public class ImageHelpTest3 {
	
	static int imageWidth = 1920;
	static int imageheight = 1080;

	static String screenshotFolder = "C:\\Users\\Lukasz\\Downloads\\World of Warcraft 3.3.5a\\Screenshots";

	static BufferedImage[][] samples = new BufferedImage[11][3];

	@BeforeAll
	public static void beforeAll() throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		for (int i = 1; i <= 11; i++) {
			for (int j = 1; j <= 3; j++) {
				File fileWith = new File(screenshotFolder + "\\" + i + "\\" + j + ".jpg");
				samples[i - 1][j - 1] = ImageIO.read(fileWith);
			}
		}
	}

	@Test
	public void test() throws IOException {
        FloatFinder ff = new FloatFinder();
		FishFinder fishFinder = new FishFinder();
		for(int k = 3; k <= 3; k++) {
			Mat noFloatImage = Utils.bufferedImage2Mat(samples[k][0]);
			
			BufferedImage originalImage = samples[k][1];
			FloatFinder.FoundFloatInfo floatFound = ff.find((Mat) noFloatImage, originalImage);
	        
			Mat fishImage = Utils.bufferedImage2Mat(samples[k][2]);
			fishFinder.isFishCatching(floatFound.image, new Mat(fishImage, floatFound.rect));
	        
		}

        
	}



}
