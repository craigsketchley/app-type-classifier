# How to run in Terminal

## Compiling

Use the following command to compile:

```
javac -cp "lib/*" -d bin src/classifier/*.java src/preprocessor/*.java src/util/*.java src/AppClassifier.java
```

## Running

Use the following command to run:

```
java -cp bin:"lib/*" AppClassifier
```

You will be prompted to evaluate the classifier, train a model, or load a model. Please follow the on screen instructions.

An example of evaluation mode would be:

```
Would you like to:
    (e)valuate our classifier?
    (t)rain our classifier and input test data?
    (l)oad a previously trained model and input test data?
e
Evaluation Mode
Enter training_desc filename and path: data/training_desc_500.csv
Enter training_labels filename and path: data/training_labels.csv
Preprocessing files...
```