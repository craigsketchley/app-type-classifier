import java.io.IOException;

import classifier.AppCategoryClassifier;
import classifier.ClassifierType;

/**
 * 
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public class Example {

	public static void main(String[] args) {

		try {
			AppCategoryClassifier app = null;
			if (args.length == 1) {
				app = new AppCategoryClassifier(
						ClassifierType.NAIVE_BAYES, args[0]);
			} else if (args.length >= 2) {
				app = new AppCategoryClassifier(
						ClassifierType.NAIVE_BAYES, args[0], args[1]);
			} else {
				System.out.println("Please provide input files as arguments");
				return;
			}

			// Testing different classifiers...
			for (ClassifierType type : ClassifierType.values()) {
				System.out.println("Running " + type);
				app.setClassifier(type);
				app.evaluate();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
