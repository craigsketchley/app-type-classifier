import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.ZeroR;

import classifier.AppCategoryClassifier;


public class Example {

	public static void main(String[] args) {

		try {
			AppCategoryClassifier app = null;
			if (args.length == 1) {
				app = new AppCategoryClassifier(args[0]);
			} else if (args.length >= 2) {
				app = new AppCategoryClassifier(args[0], args[1]);
			} else {
				System.out.println("Please provide input files as arguments");
				return;
			}

			System.out.println("Running Naive Bayes Classifier");
			app.evaluate();
			

			//START EXTRA FOR TESTING
			System.out.println("Running 3 Nearest Neighbours Classifier");
			app.setClassifier(new KNearestNeighbors(3));
			app.evaluate();
			
			System.out.println("Running 5 Nearest Neighbours Classifier");
			app.setClassifier(new KNearestNeighbors(5));
			app.evaluate();
			
			System.out.println("Running 7 Nearest Neighbours Classifier");
			app.setClassifier(new KNearestNeighbors(7));
			app.evaluate();
			
			System.out.println("Running Zero R");
			app.setClassifier(new ZeroR());
			app.evaluate();
			//END EXTRA FOR TESTING
						
//	        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("data/model.ser")));
//			// do the magic  
//			oos.writeObject(app);
//			// close the writing.
//			oos.close();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}

}
