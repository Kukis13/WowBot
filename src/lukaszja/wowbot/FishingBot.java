package lukaszja.wowbot;

import static lukaszja.wowbot.Utils.*;

import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.opencv.core.Mat;

public class FishingBot {

	FishingSession fishingSession;
	Robot robot;
	FloatFinder floatFinder = new FloatFinder();
	FishFinder fishFinder = new FishFinder();
	
	void start() throws Exception {
		delayStartIfNeeded();
		log("Starting fishing.");
		
		robot = new Robot();
		fishingSession = new FishingSession();
		loop();
	}

	private void loop() {
		while(true) {
			sleep(Config.LOOP_DELAY);
			switch (fishingSession.status) {
			case IDLE : onIdle(); break;
			case LOOKING_FOR_A_FLOAT: onLookingForAFloat(); break;
			case WAITING_FOR_A_FISH: onWaitingForAFish(); break;
			case COOLDOWN: onCooldown(); break;
			}
			if(ChronoUnit.SECONDS.between(fishingSession.lastCatch, LocalDateTime.now()) >= Config.MAX_TIME_TO_FISH_SECONDS) {
				fishingSession.reset();
			}
		}
		
	}

	private void onIdle() {
		moveMouse(10, 10);
		oncePer10Minutes();
		pressFishing();
		sleep(1300);
		fishingSession.next();
	}

	private void oncePer10Minutes() {
		if(fishingSession.lastBoostApplied == null || ChronoUnit.MINUTES.between(LocalDateTime.now(), fishingSession.lastBoostApplied) <= -10) {
			applyBoost();
			fishingSession.lastBoostApplied = LocalDateTime.now();
			fishingSession.idleImage = Utils.bufferedImage2Mat(Utils.takeImage(robot));
			sleep(3000);
		}
	}

	private void onLookingForAFloat() {
		fishingSession.floatInfo = floatFinder.find(fishingSession.idleImage, Utils.takeImage(robot));
		if(fishingSession.floatInfo != null) {
			log(fishingSession.floatInfo.middle.toString());
			fishingSession.next();
		}
	}

	
	private void onWaitingForAFish() {
        Mat potentialFish = new Mat(Utils.bufferedImage2Mat(Utils.takeImage(robot)), fishingSession.floatInfo.rect);
		boolean catching = fishFinder.isFishCatching(fishingSession.floatInfo.image, potentialFish);
		if(catching) {
			pressFloat();
			fishingSession.next();
		} else {
			fishingSession.waitingForFish += 1;
		}
	}
	
	private void onCooldown() {
		sleep(Config.COOLDOWN_AFTER_CATCH);
		fishingSession.reset();
	}

	// ------------------ Key/Mouse events ------------------ //
	
	private void pressFishing() {
		robot.keyPress(KeyEvent.VK_1);
		robot.keyRelease(KeyEvent.VK_1);
		log("Pressed fishing.");
	}

	private void applyBoost() {
		robot.keyPress(KeyEvent.VK_2);
		robot.keyRelease(KeyEvent.VK_2);
		log("Applied boost.");
	}
	
	private void pressFloat() {
		moveMouse((int)fishingSession.floatInfo.middle.x, (int)fishingSession.floatInfo.middle.y);
		sleep(100);
		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		log("Pressed float at " + (int)fishingSession.floatInfo.middle.x + " , " + (int)fishingSession.floatInfo.middle.y);
	}
	
	private void moveMouse(int x, int y) {
		robot.mouseMove(x, y);
	}

}
