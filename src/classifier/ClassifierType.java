package classifier;

import net.sf.javaml.classification.*;
import libsvm.*;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.clustering.SOM.GridType;
import net.sf.javaml.clustering.SOM.LearningType;
import net.sf.javaml.clustering.SOM.NeighbourhoodFunction;

/**
 * 
 * @author Nick Hough
 * @author Craig Sketchley
 *
 */
public enum ClassifierType {
	NAIVE_BAYES,
	SVM,
	ZEROR,
	RANDOM_FOREST50,
	RANDOM_FOREST100,
	RANDOM_FOREST200,
	SOM;
	
	@SuppressWarnings("static-access")
	public Classifier getClassifier() {
		Classifier result = null;
		
		switch (this) {
		case NAIVE_BAYES:
			result = new NaiveBayesClassifier(true, true, false);
			break;
		case SVM:
			result = new LibSVM();
			LibSVM svm = ((LibSVM)result);
			svm.setPrintInterface(LibSVM.svm_print_console);
		    svm_parameter pre= new svm_parameter();
		    pre.kernel_type= svm_parameter.RBF;
		    pre.gamma= 3;
		    pre.degree=1;
		    svm.setParameters(pre);
			break;
		case ZEROR:
			result = new ZeroR();
			break;
		case RANDOM_FOREST50:
			result = new RandomForest(50);
			break;
		case RANDOM_FOREST100:
			result = new RandomForest(100);
			break;
		case RANDOM_FOREST200:
			result = new RandomForest(200);
			break;
		case SOM:
			result = new SOM(50, 50, GridType.HEXAGONAL, 1000, 0.9, 20, LearningType.EXPONENTIAL, NeighbourhoodFunction.GAUSSIAN);
			break;
		}
		
		return result;
	}
	
	
}
