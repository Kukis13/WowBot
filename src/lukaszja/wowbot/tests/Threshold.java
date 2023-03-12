package lukaszja.wowbot.tests;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Threshold {
	private static int MAX_VALUE = 255;
	private static int MAX_TYPE = 4;
	private static int MAX_BINARY_VALUE = 255;
	private static final String WINDOW_NAME = "Threshold Demo";
	private static final String TRACKBAR_TYPE = "<html><body>Type: <br> 0: Binary <br> "
			+ "1: Binary Inverted <br> 2: Truncate <br> " + "3: To Zero <br> 4: To Zero Inverted</body></html>";
	private static final String TRACKBAR_VALUE = "Value";
	private int thresholdValue = 0;
	private int thresholdType = 3;
	private Mat src;
	private Mat dst = new Mat();
	private JFrame frame;
	private JLabel imgLabel;

	public Threshold(String[] args) {
		// Load an image
		src = Imgcodecs.imread("C:\\Users\\Lukasz\\Downloads\\World of Warcraft 3.3.5a\\Screenshots\\4\\3.jpg");
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
		if (src.empty()) {
			System.out.println("Empty image: ");
			System.exit(0);
		}
		// Create and set up the window.
		frame = new JFrame(WINDOW_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Set up the content pane.
		Image img = HighGui.toBufferedImage(src);
		addComponentsToPane(frame.getContentPane(), img);
		// Use the content pane's default BorderLayout. No need for
		// setLayout(new BorderLayout());
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private void addComponentsToPane(Container pane, Image img) {
		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
		sliderPanel.add(new JLabel(TRACKBAR_TYPE));
		// Create Trackbar to choose type of Threshold
		JSlider sliderThreshType = new JSlider(0, MAX_TYPE, thresholdType);
		sliderThreshType.setMajorTickSpacing(1);
		sliderThreshType.setMinorTickSpacing(1);
		sliderThreshType.setPaintTicks(true);
		sliderThreshType.setPaintLabels(true);
		sliderPanel.add(sliderThreshType);
		sliderPanel.add(new JLabel(TRACKBAR_VALUE));
		// Create Trackbar to choose Threshold value
		JSlider sliderThreshValue = new JSlider(0, MAX_VALUE, 0);
		sliderThreshValue.setMajorTickSpacing(50);
		sliderThreshValue.setMinorTickSpacing(10);
		sliderThreshValue.setPaintTicks(true);
		sliderThreshValue.setPaintLabels(true);
		sliderPanel.add(sliderThreshValue);
		sliderThreshType.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				thresholdType = source.getValue();
				update();
			}
		});
		sliderThreshValue.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				thresholdValue = source.getValue();
				update();
			}
		});
		pane.add(sliderPanel, BorderLayout.PAGE_START);
		imgLabel = new JLabel(new ImageIcon(img));
		pane.add(imgLabel, BorderLayout.CENTER);
	}

	private void update() {
		Imgproc.threshold(src, dst, thresholdValue, MAX_BINARY_VALUE, thresholdType);
		Image img = HighGui.toBufferedImage(dst);
		imgLabel.setIcon(new ImageIcon(img));
		frame.repaint();
	}

	public static void main(String[] args) {
		// Load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Threshold(args);
			}
		});
	}
}