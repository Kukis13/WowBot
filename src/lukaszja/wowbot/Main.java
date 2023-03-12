package lukaszja.wowbot;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.opencv.core.Core;

public class Main {
	static Robot bot;

	private static LocalDateTime lastStartFishing = LocalDateTime.now();

	static boolean shouldRun = true;
	static boolean foundFloat = false;
	static int floatX = 0;
	static int floatY = 0;

	private static long avgColorOnStart;

	public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		FishingBot fishingBot = new FishingBot();
		fishingBot.start();
	}

}
