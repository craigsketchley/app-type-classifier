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

		int numOfFeatures = 0;
		if (args.length > 0) {
			numOfFeatures = Integer.parseInt(args[0]);			
		}

		try {
			AppCategoryClassifier app = null;
			if (args.length == 2) {
				app = new AppCategoryClassifier(
						ClassifierType.SVM, args[1]);
			} else if (args.length >= 3) {
				app = new AppCategoryClassifier(
						ClassifierType.SVM, args[1], args[2]);
			} else {
				System.out.println("Please provide input files as arguments");
				return;
			}
			
			app.loadData();
			if (numOfFeatures > 0) {
				app.selectFeatures(numOfFeatures);				
			}

			app.saveSelectedFeatures("data/selectedFeatures.ser");
			
			app.evaluate();
			
//			// Testing different classifiers...
//			for (ClassifierType type : ClassifierType.values()) {
//				System.out.println("Running " + type);
//				app.setClassifier(type);
//				app.evaluate();
//			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
