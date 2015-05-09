import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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


			app.evaluate();
						
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
