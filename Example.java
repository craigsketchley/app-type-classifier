
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import classifier.AppCategoryClassifier;


public class Example {

	public static void main(String[] args) {
		
		try {
			AppCategoryClassifier app = new AppCategoryClassifier("data/training_data_sm.csv", "data/training_labels_sm.csv");
//			AppCategoryClassifier app = new AppCategoryClassifier("data/temp.csv");

			app.evaluate();
						
	        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("data/model.ser")));
			// do the magic  
			oos.writeObject(app);
			// close the writing.
			oos.close();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}

}
