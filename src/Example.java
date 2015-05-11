import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import util.App;
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
			
			app.train();
			
			// Test of classfication
			app.classifyDesc("test_input.csv", App.WORDS_FILENAME);
			
			// Save this classifier to file.
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(App.MODEL_FILENAME)));
			// do the magic  
			oos.writeObject(app);
			// close the writing.
			oos.close();
			
			app = null;
			
			// Loading our trained model...
			ObjectInputStream obj_in = new ObjectInputStream (new FileInputStream(new File(App.MODEL_FILENAME)));

			// Read an object
			Object obj = obj_in.readObject();

			if (obj instanceof AppCategoryClassifier) {
				app = (AppCategoryClassifier)obj;
			}

			// Test of classfication
			app.classifyDesc("test_input.csv", App.WORDS_FILENAME);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
