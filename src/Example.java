import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

		int cExp = 0, gammaExp = 0;
		if (args.length > 1) {
			cExp = Integer.parseInt(args[0]);			
			gammaExp = Integer.parseInt(args[1]);			
		}

		try {
			AppCategoryClassifier app = null;
			if (args.length == 3) {
				app = new AppCategoryClassifier(
						ClassifierType.SVM, args[2]);
			} else if (args.length >= 4) {
				app = new AppCategoryClassifier(
						ClassifierType.SVM, args[2], args[3]);
			} else {
				System.out.println("Please provide input files as arguments");
				return;
			}
			
			app.loadData();
			
			app.evaluate(cExp, gammaExp);
			
//			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("data/svm.ser")));
//			// do the magic  
//			oos.writeObject(app);
//			// close the writing.
//			oos.close();			
			
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
