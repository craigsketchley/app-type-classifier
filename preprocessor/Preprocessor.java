package preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import util.App;
import net.sf.javaml.core.Dataset;

/**
 * 
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public class Preprocessor {

	
	/**
	 * Process the training data and label files into one output file.
	 * 
	 * @param dataFilename
	 * @param labelFilename
	 */
	public static String processTrainingFiles(String dataFilename, String labelFilename) {
		return processTrainingFiles(dataFilename, labelFilename, App.TEMP_FILENAME);
	}

	/**
	 * Process the training data and label files into the output file given.
	 * 
	 * @param dataFilename
	 * @param labelFilename
	 * @param outputFilename
	 */
	public static String processTrainingFiles(String dataFilename, String labelFilename, String outputFilename) {
		HashMap<String, String> map = new HashMap<String, String>();

		try {
			FileReader dataReader = new FileReader(new File(dataFilename));
			FileReader labelsReader = new FileReader(new File(labelFilename));
			PrintWriter writer = new PrintWriter(outputFilename, "UTF-8");

			// Read in the labels first, saving them to a map of App Name to category.
			BufferedReader br = new BufferedReader(labelsReader);

			// Variable to hold the one line data
			String line;

			// Read file line by line...
			while ((line = br.readLine()) != null) {
				// Split the app name from category name
				String[] pair = line.split(App.DELIMITER, 2);
				// Map the app name to the category name
				map.put(pair[0], pair[1]);
			}

			// Read in the tf-idf values
			br = new BufferedReader(dataReader);

			// Read file line by line...
			while ((line = br.readLine()) != null) {
				// Split the app name from tf-idf vector
				String[] pair = line.split(App.DELIMITER, 2);

				// Write the category name and tf-idf values to 
				writer.write(map.get(pair[0]) + App.DELIMITER + pair[1] + "\n");
			}
		
			br.close();
			writer.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("Input/Ouput problem: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		return outputFilename;
	}
	
	// Hide constructor, make it a static only class...
	private Preprocessor() {}
}
