package preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import util.App;

/**
 * 
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public class Preprocessor {

	public static void main(String[] args) {
		if (args.length > 1) {
			if (args[0].equals("-tfidf")) {
				if (args.length >= 3) {
					System.out.println("generating tfidf");
					generateTfIdf(args[1], args[2]);
				}
			} else {
				if (args.length == 2) {
					processTrainingFiles(args[0], args[1]);
				} else if (args.length >= 3) {
					processTrainingFiles(args[0], args[1], args[2]);
				}	
			}
		}
	}
	
	
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
	
	
	
	public static void generateTfIdf(String descriptionFilename, String outputFilename) {
		// Map for all the words in a document and their counts within that document.
		HashMap<String, HashMap<String, Integer>> docMap = new HashMap<String, HashMap<String, Integer>>();
		
		// Map for all words and their document count
		HashMap<String, Integer> wordDocCount = new HashMap<String, Integer>();
				
		try {
			FileReader descReader = new FileReader(new File(descriptionFilename));

			// Variable to hold the one line data
			String line;

			// Read in the tf-idf values
			BufferedReader br = new BufferedReader(descReader);
			
			// Read file line by line...
			while ((line = br.readLine()) != null) {
				// Split the app name from description words
				String[] words = line.split(App.DELIMITER);
				
				docMap.put(words[0], new HashMap<String, Integer>());
				
				// App name is at index 0
				for (int i = 1; i < words.length; i++) {
					if (!docMap.get(words[0]).containsKey(words[i])) {
						if (wordDocCount.containsKey(words[i])) {
							wordDocCount.put(words[i], wordDocCount.get(words[i]) + 1);
						} else {
							wordDocCount.put(words[i], 1);
						}
					}
					if (docMap.get(words[0]).containsKey(words[i])) {
						docMap.get(words[0]).put(words[i], docMap.get(words[0]).get(words[i]) + 1);
					} else {
						docMap.get(words[0]).put(words[i], 1);
					}
				}
			}
			
			br.close();
			br = null;
			
			System.out.println("Done scanning file.");
			
			String[] words = wordDocCount.keySet().toArray(new String[wordDocCount.size()]);
			String[] apps = docMap.keySet().toArray(new String[docMap.size()]);
			
			// Now get the if-idf values...
			double[][] output = new double[apps.length][words.length];
			
			System.out.println("Doc count: " + apps.length);
			System.out.println("Word count: " + words.length);
			
			for (int appIndex = 0; appIndex < output.length; appIndex++) {
				for (int wordIndex = 0; wordIndex < output[appIndex].length; wordIndex++) {
					if (docMap.get(apps[appIndex]).containsKey(words[wordIndex])) {
						output[appIndex][wordIndex] = calcTfIdf(
								docMap.get(apps[appIndex]).get(words[wordIndex]),
								apps.length,
								wordDocCount.get(words[wordIndex]));
					} else {
						output[appIndex][wordIndex] = 0;
					}
				}
			}
			
			// Normalise each app tf-idf vector to unit vector...
			double mag;
			for (int vecIndex = 0; vecIndex < output.length; vecIndex++) {
				mag = 0;
				for (int i = 0; i < output[vecIndex].length; i++) {
					mag += output[vecIndex][i] * output[vecIndex][i];
				}
				mag = Math.sqrt(mag);

				for (int i = 0; i < output[vecIndex].length; i++) {
					double normVal = (mag != 0) ? output[vecIndex][i] / mag : 0;
					output[vecIndex][i] = normVal;
				}
			}
			
			PrintWriter tfidfWriter = new PrintWriter(outputFilename, "UTF-8");

			for (int appIndex = 0; appIndex < output.length; appIndex++) {
				tfidfWriter.write(apps[appIndex]);
				for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
					if (output[appIndex][wordIndex] == 0) {
						tfidfWriter.write(App.DELIMITER + 0);
					} else {
						tfidfWriter.write(App.DELIMITER + output[appIndex][wordIndex]);
					}
				}
				tfidfWriter.write("\n");
			}
			
			tfidfWriter.close();
			
			PrintWriter wordsWriter = new PrintWriter(App.WORDS_FILENAME, "UTF-8");
			
			for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
				wordsWriter.write(words[wordIndex] + ":" + wordDocCount.get(words[wordIndex]));
				if (wordIndex != words.length - 1) {
					wordsWriter.write(",");
				}
			}
			
			wordsWriter.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Input/Ouput problem: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate the tf-idf given the appropriate counts.
	 * @param docWordCount
	 * @param wordDocCount
	 * @param docCount
	 * @return
	 */
	protected static double calcTfIdf(int docWordCount, int wordDocCount, int docCount) {
		return docWordCount * (Math.log(((double) docCount) / wordDocCount));	
	}
	
	
	// Hide constructor, make it a static only class...
	private Preprocessor() {}
}
