import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

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
//			if (args.length == 1) {
//				app = new AppCategoryClassifier(
//						ClassifierType.SVM, args[0]);
//			} else {
//				System.out.println("Please provide input files as arguments");
//				return;
//			}
			
//			app.loadData();
			
//			app.train();

//			// Save this classifier to file.
//			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(args[0] + ".model")));
//			// do the magic  
//			oos.writeObject(app);
//			// close the writing.
//			oos.close();
			
			// Test of classfication
//			app.classifyDesc("test_input1.csv", App.WORDS_FILENAME);
			
//			app = null;
			
		    Scanner scanner = new Scanner(System.in);

		    //  prompt for the user's name
		    System.out.print("Enter your model file: ");

		    // get their input as a String
		    String modelfilename = scanner.next();
			
			// Loading our trained model...
			ObjectInputStream obj_in = new ObjectInputStream (new FileInputStream(new File(modelfilename)));

			// Read an object
			Object obj = obj_in.readObject();

			if (obj instanceof AppCategoryClassifier) {
				app = (AppCategoryClassifier)obj;
			}

		    //  prompt for the user's name
		    System.out.print("Enter your words file: ");

		    // get their input as a String
		    String wordsfilename = scanner.next();
			
		    //  prompt for the user's name
		    System.out.print("Enter your test data file: ");

		    // get their input as a String
		    String modetestfilename = scanner.next();
			
			// Test of classfication
			app.classifyDesc(modetestfilename, wordsfilename);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
