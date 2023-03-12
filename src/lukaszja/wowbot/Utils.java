package lukaszja.wowbot;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Utils {
	
	static void log(String string) {
		System.out.println(LocalDateTime.now() + " || " + string);
	}

	static void delayStartIfNeeded() throws Exception {
		sleep(Config.DELAY_ON_START_MS);
	}
	
	static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	static BufferedImage takeImage(Robot robot) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle rectangle = new Rectangle(screenSize.width, screenSize.height);
		return robot.createScreenCapture(rectangle);
	}
	
	public static int screenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}
	
	public static int screenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
	
	public static File saveImage(BufferedImage bufferedImage) {
		try {
			 File file = new File("test" + System.currentTimeMillis() + ".bmp");
			 file.createNewFile();
			 System.out.println("Saving test file at " + file.getAbsolutePath());
			 ImageIO.write(bufferedImage, "bmp", file);
			 return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static void saveColors(Color[][] colors) {
		 try {
			File file = new File("test" + System.currentTimeMillis() + ".txt");
			file.createNewFile();
			System.out.println("Saving test file at " + file.getAbsolutePath());
			
			List<String> redLines = new ArrayList<>();
			List<String> greenLines = new ArrayList<>();
			List<String> blueLines = new ArrayList<>();
		    for(int y=0; y<colors[0].length; y++) {
		    	String newLineRed = "", newLineBlue = "", newLineGreen = "";
		        for(int x=0; x<colors.length; x++) {
		        	if(colors[x][y] != null) {
		        		newLineRed += colors[x][y].getRed() + " ";
		        		newLineBlue += colors[x][y].getBlue() + " ";
		        		newLineGreen += colors[x][y].getGreen() + " ";
		        	}
		        }
		        if(!newLineRed.isEmpty()) {
			        redLines.add(newLineRed);
			        blueLines.add(newLineBlue);
			        greenLines.add(newLineGreen);
		        }
		    }

		    Files.write(file.toPath(), redLines, StandardOpenOption.APPEND);
		    Files.write(file.toPath(), List.of(""), StandardOpenOption.APPEND);
		    Files.write(file.toPath(), blueLines, StandardOpenOption.APPEND);
		    Files.write(file.toPath(), List.of(""), StandardOpenOption.APPEND);
		    Files.write(file.toPath(), greenLines, StandardOpenOption.APPEND);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static Mat bufferedImage2Mat(BufferedImage image) {

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    try {
			ImageIO.write(image, "jpg", byteArrayOutputStream);
		    byteArrayOutputStream.flush();
		    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;

	}
	
	
	public static BufferedImage Mat2BufferedImage(Mat matrix) {
	    MatOfByte mob=new MatOfByte();
	    Imgcodecs.imencode(".bmp", matrix, mob);
	    try {
			return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
	

	public static void enlargeRect(Rect boundingRect, int i) {
		boundingRect.x -= i;
		boundingRect.width += i * 2;
		boundingRect.y -= i;
		boundingRect.height += i * 2;
		
		if(boundingRect.y < 0) {
			boundingRect.y = 0;
		}
	}
	

	public static void showImage(Mat matrix)  {
		try {
			BufferedImage mat2BufferedImage = Utils.Mat2BufferedImage(matrix);
			File savedImage = Utils.saveImage(mat2BufferedImage);
			
		    Desktop dt = Desktop.getDesktop();
		    dt.open(savedImage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static Mat grayScaleToBinary(Mat grayscale, int threshold) {
        // Apply a threshold to create a binary image
        Mat binaryImage = new Mat();
        double maxValue = 255; // Maximum value for the output pixels
        int thresholdType = Imgproc.THRESH_BINARY;
        Imgproc.threshold(grayscale, binaryImage, threshold, maxValue, thresholdType);
        return binaryImage;
	}
	
	public static Mat grayScaleToBinary(Mat grayscale) {
		return grayScaleToBinary(grayscale, 128);
	}
}
