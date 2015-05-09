package util;

/**
 * Static class containing project constants.
 * 
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public final class App {

	public static final String TEMP_FILENAME = "data/preprocessed.csv";
	public static final String OUTPUT_FILENAME = "data/output.csv";
	public static final String DELIMITER = ",";

	public static final int NUM_OF_FEATURES = 200;
	public static final int NUM_OF_FOLDS = 10;
	
	public static final boolean DEBUG = true;
	
	// Hide constructor by making it private
	private App() {
        throw new AssertionError();
	}
}
