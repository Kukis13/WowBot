package lukaszja.wowbot;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import org.opencv.core.Mat;

import lukaszja.wowbot.FloatFinder.FoundFloatInfo;

public class FishingSession {

	enum Status {
		IDLE,
		COOLDOWN,
		LOOKING_FOR_A_FLOAT,
		WAITING_FOR_A_FISH
	}
	
	Status status;
	Mat idleImage;
	LocalDateTime lastCatch;
	FoundFloatInfo floatInfo;
	LocalDateTime lastBoostApplied;
	public int waitingForFish;
	
	public FishingSession () {
		reset();
	}

	public void next() {
		switch(status) {
		case COOLDOWN: status = Status.IDLE; return;
		case IDLE: status = Status.LOOKING_FOR_A_FLOAT; return;
		case LOOKING_FOR_A_FLOAT: status = Status.WAITING_FOR_A_FISH; return;
		case WAITING_FOR_A_FISH: status = Status.COOLDOWN; return;
		}
		Utils.log("New status " + status);
	}

	public void reset() {
		status = Status.IDLE;
		lastCatch = LocalDateTime.now();
		floatInfo = null;
		waitingForFish = 0;
	}
}
