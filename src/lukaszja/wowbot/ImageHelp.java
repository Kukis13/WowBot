package lukaszja.wowbot;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ImageHelp {

	static Point findFloat(BufferedImage currentImage, BufferedImage previousImage) {
		
		Map<Point, Float> candidates = new HashMap<>();
		
		Color[][][] colors = new Color[2][Utils.screenWidth()][Utils.screenHeight()];
		
		
		for(int x = Config.CROP_FROM_SIDES; x < Utils.screenWidth() - Config.CROP_FROM_SIDES; x+=4) {
			for(int y = 0; y < Utils.screenHeight() - Config.HEIGHT_OF_BARS_IN_BOTTOM; y+=4) {
				int current = currentImage.getRGB(x, y);
				int previous = previousImage.getRGB(x, y);
				colors[0][x][y] = new Color(current);
				colors[1][x][y] = new Color(previous);
				
				boolean diff = diffBetweenColors(colors[0][x][y], colors[1][x][y], 80);
				if(diff) {
					candidates.put(new Point(x, y), 1.0f);
				}
				
			}
		}
		for(Point p : candidates.keySet()) {
			List<Point> collectNearbyPoints = collectNearbyPoints(p, 5, 4);
			float score = calculateNearbyPointsScore(collectNearbyPoints, colors[0]) * 0.01f;
			if(score <= 1) {
				continue;
			}
			score += calculateScoreForDistanceToMiddle(p);
			candidates.put(p, score);
		}
		if(candidates.isEmpty()) {
			return null;
		}
		
		return Collections.max(candidates.entrySet(), Map.Entry.comparingByValue()).getKey();
	}

	
	private static int calculateScoreForDistanceToMiddle(Point p) {
		int distanceFromMiddleX = Math.abs(Utils.screenWidth()/2 - p.x);
		int distanceFromMiddleY = Math.abs(Utils.screenHeight()/2 - p.y);
		return 1500 - (distanceFromMiddleX + distanceFromMiddleY);
	}


	private static int calculateNearbyPointsScore(List<Point> collectNearbyPoints, Color[][] colors) {
		int score = 0;
		
		for(Point p : collectNearbyPoints) {
			if(colors[p.x][p.y] == null) {
				continue;
			}
			if(colors[p.x][p.y].getRed() >= 140) {
				score += 255 - Math.abs(colors[p.x][p.y].getRed() - 140);
			} else if(colors[p.x][p.y].getRed() <= 30) {
				score += 255 -Math.abs(colors[p.x][p.y].getRed() - 30);
			}
			
			if(colors[p.x][p.y].getBlue() <= 30) {
				int scoreBlue = 255 -Math.abs(colors[p.x][p.y].getBlue() - 30);
				score = score + scoreBlue * 2;
			}
		}
		return score;
	}


	private static List<Point> collectNearbyPoints(Point p, int radius, int step) {
		List<Point> nearbyPoints = new ArrayList<>();
		for(int x = p.x - (radius * step); x < p.x + (radius * step); x += step) {
			if(x > Config.CROP_FROM_SIDES && x < Utils.screenWidth() - Config.CROP_FROM_SIDES) {
				for(int y = p.y - (radius * step); y <p.y + (radius * 4); y += step) {
					if (y > 0) {
						nearbyPoints.add(new Point(x, y));
					}
				}
			}

		}
		return nearbyPoints;
	}


	private static boolean diffBetweenColors(Color color, Color color2, int threshold) {
		if(Math.abs(color.getRed() - color2.getRed()) > threshold) {
			return true;
		}
		if(Math.abs(color.getBlue() - color2.getBlue()) > threshold) {
			return true;
		}
		if(Math.abs(color.getGreen() - color2.getGreen()) > threshold) {
			return true;
		}

		return false;
	}


	public static int calculateFishScore(Point pointToWatch, BufferedImage currentImage, BufferedImage floatImage) {
		List<Point> nearbyPoints = collectNearbyPoints(pointToWatch, 7, 1);
		
		Color[][][] colors = new Color[2][Utils.screenWidth()][Utils.screenHeight()];
		
		int numberOfDifferntPixels = 0;
		
		for(Point p : nearbyPoints) {
			colors[0][p.x][p.y] = new Color(currentImage.getRGB(p.x, p.y));
			colors[1][p.x][p.y] = new Color(floatImage.getRGB(p.x, p.y));
			
			boolean diff = diffBetweenColors(colors[0][p.x][p.y], colors[1][p.x][p.y], 50);
			if(diff) {
				numberOfDifferntPixels++;
			}
		}
		return numberOfDifferntPixels;
	}
	
	
	/*
	 * 		LinkedHashMap<Point,Float> collect = candidates.entrySet().stream()
			.filter(e -> e.getValue() > 0.0f)
			.sorted(Entry.comparingByValue())
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
		
		collect.forEach((p, f) -> System.out.println(p + " value " + f));
	 */
}
