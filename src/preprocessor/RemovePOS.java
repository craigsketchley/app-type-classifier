package preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import util.App;

/**
 * Our NLP processing class.
 * 
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public class RemovePOS {

	/**
	 * Quick check to see if a word only contains alpha characters.
	 * @param name
	 * @return
	 */
	public static boolean isAlpha(String name) {
		char[] chars = name.toCharArray();

		for (char c : chars) {
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Processes the description file, filtering out words based on NLP techniques.
	 * 
	 * @param descFilename
	 * @param outputFilename
	 * @throws FileNotFoundException
	 */
	public static void processDescriptions(String descFilename, String outputFilename) throws FileNotFoundException {

		final String DESCRIPTION_FILE = descFilename;
		final String FILTERED_OUTPUT_FILE = outputFilename;
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();

		// Common nouns
		map.put("NN", true);
		map.put("NNS", true);
		map.put("NN:U", true);
		map.put("NN:UN", true);

		// TODO Auto-generated method stub
		InputStream modelIn;
		InputStream modelIn2 = null;
		modelIn = new FileInputStream("en-token.bin");
		modelIn2 = new FileInputStream("en-pos-maxent.bin");
		try {
			FileReader descReader = new FileReader(new File(DESCRIPTION_FILE));
			BufferedReader br = new BufferedReader(descReader);
			PrintWriter writer = new PrintWriter(FILTERED_OUTPUT_FILE, "UTF-8");

			TokenizerModel model = new TokenizerModel(modelIn);
			POSModel model2 = new POSModel(modelIn2);
			Tokenizer tokenizer = new TokenizerME(model);
			POSTaggerME tagger = new POSTaggerME(model2);

			// Variable to hold the one line data
			String line;

			// Read file line by line...
			if (App.DEBUG) {
				System.out.println("Starting Description Filtering");
			}
			while ((line = br.readLine()) != null) {

				// Split the app name from category name
				String[] pair = line.split(",", 2);

				// Split the description into words
				String tokens[] = tokenizer.tokenize(pair[1]);

				// Tag each word with it's part-of-speech
				String tags[] = tagger.tag(tokens);
				writer.write(pair[0]);
				for (int j = 0; j < tokens.length; j++) {

					// Writes it to a file if alphanumeric and a common noun
					if (map.get(tags[j]) != null && tokens[j].length() > 2
							&& isAlpha(tokens[j])) {
						if (tokens[j].length() > 3) {

							// Removes common plurals from data
							if (tokens[j].substring(tokens[j].length() - 3)
									.equalsIgnoreCase("ies")) {
								tokens[j] = tokens[j].substring(0,
										tokens[j].length() - 3)
										+ "y";
							} else if (tokens[j].substring(
									tokens[j].length() - 1).equalsIgnoreCase(
									"s")) {
								tokens[j] = tokens[j].substring(0,
										tokens[j].length() - 1);
							}
						}
						writer.write("," + tokens[j].toLowerCase());
					}
				}
				writer.write("\n");
			}
			if (App.DEBUG) {
				System.out.println("Finished Description Filtering");
			}
			br.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
			if (modelIn2 != null) {
				try {
					modelIn2.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
