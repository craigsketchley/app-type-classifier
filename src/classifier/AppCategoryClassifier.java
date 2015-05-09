package classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import preprocessor.Preprocessor;
import util.App;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.featureselection.subset.GreedyForwardSelection;
import net.sf.javaml.filter.RetainAttributes;
import net.sf.javaml.tools.data.FileHandler;

public class AppCategoryClassifier implements Serializable {

	// Serialisation version number
	private static final long serialVersionUID = 1L;
	
	private Classifier classifier;
	private String processedFilename;
	private Set<Integer> selectedFeatures;
	private boolean trained;
	private Dataset data;
	
	/**
	 * Construct a classifier from 2 separate data and labels files.
	 * 
	 * @param trainingDataFilename
	 * @param trainingLabelsFilename
	 * @throws IOException
	 */
	public AppCategoryClassifier(String trainingDataFilename, String trainingLabelsFilename) throws IOException {
		if (App.DEBUG) {
			System.out.println("Preprocessing files...");				
		}
		
		this.processedFilename = Preprocessor.processTrainingFiles(trainingDataFilename, trainingLabelsFilename);
	
		if (App.DEBUG) {
			System.out.println("Files processed...");				
		}
		
		this.reset();
	}

	/**
	 * Construct a classifier from a preprocessed data file.
	 * 
	 * @param processedFilename
	 * @throws IOException
	 */
	public AppCategoryClassifier(String processedFilename) throws IOException {
		this.processedFilename = processedFilename;
		
		this.reset();
	}
	
	
	/**
	 * Resets classifier, requires training again before use.
	 * @throws IOException 
	 */
	public void reset() throws IOException {
		if (App.DEBUG) {
			System.out.println("Loading processed file...");				
		}
		
		// Load data from file...
		this.data = FileHandler.loadDataset(new File(this.processedFilename), 0, App.DELIMITER);

		if (App.DEBUG) {
			System.out.println("Feature selection...");				
		}
		
		// Select features of data...
		GreedyForwardSelection featureSel = new GreedyForwardSelection(
				App.NUM_OF_FEATURES,
				new PearsonCorrelationCoefficient());
		featureSel.build(this.data);
		this.selectedFeatures = featureSel.selectedAttributes();

		if (App.DEBUG) {
			System.out.println("Filtering selected features...");				
		}
		
		RetainAttributes filter = new RetainAttributes(selectedFeatures);
		filter.build(this.data);
		filter.filter(this.data);

		this.classifier = new NaiveBayesClassifier(true, true, false);
		this.selectedFeatures = null;
		this.trained = false;
	}

	/**
	 * Trains the classifier from the data.
	 * 
	 * @throws IOException
	 */
	public void train() {
		if (!this.trained) {
			// Train model...
			if (App.DEBUG) {
				System.out.println("Training model...");				
			}
			
			this.classifier.buildClassifier(data);

			if (App.DEBUG) {
				System.out.println("Model trained...");				
			}
			
			this.trained = true;
		}
	}
	
	/**
	 * Runs an evaluation technique on this classifer...
	 * 
	 */
	public void evaluate() {
		if (!this.trained) {
			if (App.DEBUG) {
				System.out.println("Creating evaluator...");				
			}
			
			CrossValidation cv = new CrossValidation(this.classifier);

			if (App.DEBUG) {
				System.out.println("Evaluating...");				
			}
			
			// Perform cross-validation on the data set
			Map<Object, PerformanceMeasure> perform = cv.crossValidation(this.data, App.NUM_OF_FOLDS);
			
			if (App.DEBUG) {
				System.out.println("Done evaluating...");				
			}
			
			for (Object c : perform.keySet()) {
				System.out.println(perform.get(c));
			}
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
		if (this.trained) {
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
		}
	}
	
	/**
	 * Classify a singular instance if trained.
	 * 
	 * @param inst
	 * @return
	 */
	private String classify(Instance inst) {
		if (this.trained) {
			return this.classifier.classify(inst).toString();			
		}
		return null;
	}
	

}
