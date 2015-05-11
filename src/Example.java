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

		try {
			AppCategoryClassifier app = null;
			if (args.length == 1) {
				app = new AppCategoryClassifier(
						ClassifierType.SVM, args[0]);
			} else if (args.length >= 2) {
				app = new AppCategoryClassifier(
						ClassifierType.SVM, args[0], args[1]);
			} else {
				System.out.println("Please provide input files as arguments");
				return;
			}
			
			app.loadData();
			
			app.evaluate();
			
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
