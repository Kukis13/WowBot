package lukaszja.wowbot;

import org.opencv.core.Core;

public class Main {

	public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		FishingBot fishingBot = new FishingBot();
		fishingBot.start();
	}

}
