import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import classifier.AppCategoryClassifier;
import classifier.ClassifierType;

/**
 * Simple CLI for our classifier.
 *  
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public class AppClassifier {

	public static void main(String[] args) {

		try {
			AppCategoryClassifier app = null;

			Scanner scanner = new Scanner(System.in);
			
		    //  prompt for the user's name
		    System.out.print("Would you like to:"
		    		+ "\n\t(e)valuate our classifier?"
		    		+ "\n\t(t)rain our classifier and input test data?"
		    		+ "\n\t(l)oad a previously trained model and input test data?"
		    		+ "\n");

		    // get their input as a String
		    String cmd = scanner.next();
		    
		    if (cmd.toLowerCase().charAt(0) == 'e') {
		    	// Evaluate a classifier...
			    System.out.println("Evaluation Mode");
			    System.out.print("Enter training_desc filename and path: ");

			    // get their input as a String
			    String descfilename = scanner.next();
			    
			    System.out.print("Enter training_labels filename and path: ");

			    // get their input as a String
			    String labelfilename = scanner.next();
			    
			    app = new AppCategoryClassifier(ClassifierType.SVM, descfilename, labelfilename);
				
			    app.loadData();
			    
			    app.evaluate();
		    	
		    } else if (cmd.toLowerCase().charAt(0) == 't') {
		    	// Train the model...
			    System.out.println("Training Mode");
			    System.out.print("Enter training_desc filename and path: ");

			    // get their input as a String
			    String descfilename = scanner.next();
			    
			    System.out.print("Enter training_labels filename and path: ");

			    // get their input as a String
			    String labelfilename = scanner.next();
			    
			    app = new AppCategoryClassifier(ClassifierType.SVM, descfilename, labelfilename);
				
			    app.loadData();
			    
		    	app.train();
		    	
		    	System.out.println("Saving model");
		    	
		    	// Save this classifier to file.
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(descfilename + ".model")));
				// do the magic  
				oos.writeObject(app);
				// close the writing.
				oos.close();
		    	
		    	System.out.println("Model saved to: " + descfilename + ".model");
		    	
		    	String testFilename = null;
		    	
		    	System.out.print("Enter test_desc filename and path: ");
		    	while ((testFilename = scanner.next()) != null) {
		    		// get their input as a String
		    		app.classifyDesc(testFilename);
		    		
		    		System.out.println("Classified. Output saved to: " + testFilename + ".class");
		    		
		    		System.out.print("Another? Enter test_desc filename and path: ");
		    	}
		    	
		    } else if (cmd.toLowerCase().charAt(0) == 'l') {
		    	// Load a model...
			    System.out.println("Loading Mode");
			    //  prompt for the mode filename
			    System.out.print("Enter model filename and path: ");

			    // get their input as a String
			    String modelfilename = scanner.next();
				
				// Loading our trained model...
				ObjectInputStream obj_in = new ObjectInputStream (new FileInputStream(new File(modelfilename)));

				// Read an object
				Object obj = obj_in.readObject();
				obj_in.close();
				
				if (obj instanceof AppCategoryClassifier) {
					app = (AppCategoryClassifier)obj;
				}

		    	String testFilename = null;
		    	
		    	System.out.print("Enter test_desc filename and path: ");
		    	while ((testFilename = scanner.next()) != null) {
		    		// get their input as a String
		    		app.classifyDesc(testFilename);
		    		
		    		System.out.println("Input classified. Output saved to: " + testFilename + ".class");
		    		
		    		System.out.print("Another? Enter test_desc filename and path: ");
		    	}
		    	
		    }

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
