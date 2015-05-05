import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Classifier {

	public static void main(String[] args) {

		HashMap<String, String> map = new HashMap<String, String>();

		try {
			FileReader readerVectors = new FileReader(new File(
					"training_data.csv"));
			FileReader readerLabels = new FileReader(new File(
					"training_labels.csv"));

			PrintWriter writer = new PrintWriter("output.csv", "UTF-8");

			BufferedReader br = new BufferedReader(readerLabels);

			// Variable to hold the one line data
			String line;

			// Read file line by line and print on the console
			while ((line = br.readLine()) != null) {
				String[] pair = line.split(",", 2);
				map.put(pair[0], pair[1]);
			}

			br = new BufferedReader(readerVectors);

			writer.write("class,");
			for (int i = 0; i < 13626; i++) {
				writer.write("" + i);
				if (i != 13625) {
					writer.write(",");
				} else {
					writer.write("\n");
				}
			}
			
			// Read file line by line and print on the console
			while ((line = br.readLine()) != null) {
				String[] pair = line.split(",", 2);
				
				writer.write(map.get(pair[0]) + "," + pair[1] + "\n");
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
