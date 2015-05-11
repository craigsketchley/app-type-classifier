package classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import libsvm.LibSVM;
import libsvm.svm_parameter;
import preprocessor.Preprocessor;
import util.App;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.featureselection.subset.GreedyForwardSelection;
import net.sf.javaml.filter.RetainAttributes;
import net.sf.javaml.tools.data.FileHandler;

/**
 * 
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public class AppCategoryClassifier implements Serializable {

	// Serialisation version number
	private static final long serialVersionUID = 1L;
	
	private Classifier classifier;
	private String processedFilename;
	private Set<Integer> selectedFeatures;
	private boolean trained;
	private boolean loaded;
	private boolean selected;
	private Dataset data;
	
	/**
	 * Construct a classifier from 2 separate data and labels files.
	 * 
	 * @param trainingDataFilename
	 * @param trainingLabelsFilename
	 * @throws IOException
	 */
	public AppCategoryClassifier(ClassifierType classifierType, String trainingDataFilename, String trainingLabelsFilename) throws IOException {
		if (App.DEBUG) {
			System.out.println("Preprocessing files...");
		}
		
		this.processedFilename = Preprocessor.processTrainingFiles(trainingDataFilename, trainingLabelsFilename);
	
		if (App.DEBUG) {
			System.out.println("Files processed...");
		}
		
		this.setClassifier(classifierType);
	}

	/**
	 * Construct a classifier from a preprocessed data file.
	 * 
	 * @param processedFilename
	 * @throws IOException
	 */
	public AppCategoryClassifier(ClassifierType classifierType, String processedFilename) throws IOException {
		this.processedFilename = processedFilename;
		
		this.setClassifier(classifierType);
	}
	
	/**
	 * Set the classifier type.
	 * 
	 * @param type
	 */
	public void setClassifier(ClassifierType type){
		this.classifier = type.getClassifier();
		this.trained = false;
	}
	
	/**
	 * Load the data from the preprocessed file.
	 * 
	 * @throws IOException
	 */
	public void loadData() throws IOException {
		if (App.DEBUG) {
			System.out.println("Loading processed file...");				
		}
		
		// Load data from file...
		this.data = FileHandler.loadDataset(new File(this.processedFilename), 0, App.DELIMITER);

		if (App.DEBUG) {
			System.out.println("Processed file loaded.");				
		}
		
		this.loaded = true;
	}
	
	public void selectFeatures() {
		selectFeatures(App.NUM_OF_FEATURES);
	}
	
	/**
	 * Selects and filters the data features ready for training.
	 *  
	 */
	public void selectFeatures(int numOfFeatures) {
		if (this.loaded) {
			if (App.DEBUG) {
				System.out.println("Feature selection...");				
				System.out.println("Selecting " + numOfFeatures + " features.");				
			}
			
			// Select features of data...
			GreedyForwardSelection featureSel = new GreedyForwardSelection(
					numOfFeatures,
					new PearsonCorrelationCoefficient());
			featureSel.build(this.data);
			this.selectedFeatures = featureSel.selectedAttributes();
			
			if (App.DEBUG) {
				System.out.println("Filtering selected features...");				
			}
			
			RetainAttributes filter = new RetainAttributes(selectedFeatures);
			filter.build(this.data);
			filter.filter(this.data);			

			if (App.DEBUG) {
				System.out.println("Features selected.");				
			}
			
			this.selected = true;
		} else {
			System.out.println("Need to load the data before selecting features.");
		}
	}
	
	/**
	 * Trains the classifier from the data.
	 * 
	 */
	public void train() {
		if (this.loaded) {
			// Train model...
			if (App.DEBUG) {
				System.out.println("Training model...");				
			}
			
			this.classifier.buildClassifier(data);

			if (App.DEBUG) {
				System.out.println("Model trained...");				
			}
			
			this.trained = true;
		} else {
			System.out.println("Need to load the data before training.");
		}
	}
	
	/**
	 * Runs an evaluation technique on this classifer...
	 * 
	 */
	public void evaluate(int cExp, int gammaExp) {
		if (this.loaded) {
			if (App.DEBUG) {
				System.out.println("Creating evaluator...");				
			}
	
		    svm_parameter pre= new svm_parameter();
		    pre.kernel_type = svm_parameter.RBF;
		    pre.C = Math.pow(2, cExp);
		    pre.gamma = Math.pow(2, gammaExp);
		    ((LibSVM)this.classifier).setParameters(pre);

			CrossValidation cv = new CrossValidation(this.classifier);
			
			if (App.DEBUG) {
				System.out.println("Evaluating...");
			}

			System.out.println("Params: Cexp=" + cExp + " gammaExp=" + gammaExp);
			
			// Perform cross-validation on the data set
			Map<Object, PerformanceMeasure> perform = cv.crossValidation(this.data, App.NUM_OF_FOLDS);
			
//			if (App.DEBUG) {
//				System.out.println("Done evaluating...");
//			}
			
			double precision = 0;
			double recall = 0;
			double accuracy = 0;
			int count = 0;
			
			for (Object c : perform.keySet()) {
//				System.out.println("TF/PN:     " + perform.get(c));
				accuracy += perform.get(c).getAccuracy();
//				System.out.println("Accuracy:  " + perform.get(c).getAccuracy());
				precision += perform.get(c).getPrecision();
//				System.out.println("Precision: " + perform.get(c).getPrecision());
				recall += perform.get(c).getRecall();
//				System.out.println("Recall:    " + perform.get(c).getRecall());
				count++;
			}
			
			System.out.println("Accuracy Avg:  " + (accuracy / count));
			System.out.println("Precision Avg: " + (precision / count));
			System.out.println("Recall Avg:    " + (recall / count));					
			
		} else {
			System.out.println("Need to load the data before evaluating.");
		}
	}

	/**
	 * Classifies the input file, outputs to the default output filename.
	 * 
	 * @param inputFilename
	 * @throws IOException 
	 */
	public void classify(String inputFilename) throws IOException {
		this.classify(inputFilename, App.OUTPUT_FILENAME);
	}

	/**
	 * Classifies the input file, outputs to the specified output filename.
	 * 
	 * @param inputFilename
	 * @throws IOException 
	 */
	public void classify(String inputFilename, String outputFilename) throws IOException {
		if (this.loaded) {
			// Load data...
			FileReader labelsReader = new FileReader(new File(inputFilename));
			PrintWriter writer = new PrintWriter(outputFilename, "UTF-8");

			// Variable to hold the one line data
			String line;

			// Read in the labels first, saving them to a map of App Name to category.
			BufferedReader br = new BufferedReader(labelsReader);
			
			// Read file line by line...
			while ((line = br.readLine()) != null) {
				// Split the app name from tf-idf vector
				String[] pair = line.split(App.DELIMITER, 2);
				
				String[] stringVec = pair[1].split(",");
				
				Instance inst = new DenseInstance(this.selectedFeatures.size());
				
				int i = 0;
				for (Integer iSel : this.selectedFeatures) {
					inst.put(i++, Double.parseDouble(stringVec[iSel]));
				}
				
				// Write the category name and tf-idf values to 
				writer.write(pair[0] + App.DELIMITER + this.classify(inst) + "\n");
			}
		
			br.close();
			writer.close();
		} else {
			System.out.println("Need to load the data before classifying.");
		}
	}
	
	/**
	 * Classify a singular instance if trained.
	 * 
	 * @param inst
	 * @return
	 */
	private String classify(Instance inst) {
		if (this.loaded) {
			return this.classifier.classify(inst).toString();			
		} else if (this.loaded) {
			System.out.println("Need to run feature selection before classifying.");
		} else {
			System.out.println("Need to load the data before classifying.");
		}
		return null;
	}
	
	/**
	 * Save the selected features to use again.
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void saveSelectedFeatures(String filename) throws IOException {
		if (this.loaded) {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(filename)));
			// do the magic  
			oos.writeObject(this.selectedFeatures);
			// close the writing.
			oos.close();			
		} else {
			System.out.println("Data not loaded. Please load data before saving selected features.");
		}
	}
	
	/**
	 * Loads the selected features from a previous feature selection.
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void loadSelectedFeatures(String filename) throws FileNotFoundException, IOException {
		ObjectInputStream oos = new ObjectInputStream(                                 
                new FileInputStream(  new File(filename)) ) ;
		try {
			this.selectedFeatures = (Set<Integer>)oos.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("File is not a recognised format. Is it corrupt?");
		} catch (RuntimeException e) {
			System.out.println("File is not a recognised format. Is it corrupt?");
		}
		oos.close();
	}

}
