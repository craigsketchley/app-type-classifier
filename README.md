# How to run in Terminal

## Compiling

Use the following command to compile:

```
javac -cp "lib/*" -d bin src/classifier/*.java src/preprocessor/*.java src/util/*.java src/Example.java
```

## Running

### Classification Evaluation

Use the following command to run:

```
java -cp bin:"lib/*" Example <data file> <label file>
```

So for the full data set...

```
java -cp bin:"lib/*" Example data/training_data.csv data/training_labels.csv
```


### Preprocessing only

To run the preprocessor only, use the following command...

```
java -cp bin:"lib/*" preprocessor.Preprocessor <data file> <label file> <output file>
```

So for the full data set, this would be...

```
java -cp bin:"lib/*" preprocessor.Preprocessor data/training_data.csv data/training_labels.csv
```

The output file is optional and will default to `data/preprocessed.csv`.