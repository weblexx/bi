import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Parser {

	String args[] = null;
	String fileHeader = null;
	ArrayList<Row> map = new ArrayList<Row>();
	String outfile = null;
	int classAttribute;
	int linecount = 0;
	int rowlength = 0;
	int attributeNo = -1;
	int percentage = -1;
	ArrayList<Integer> ignoreAttr = new ArrayList<Integer>();
	String replacementStrategy;
	MeanCalculator mean;

	public Parser(String[] args) {
		super();
		this.args = args;
		System.out.println("----------------------\nConfiguration:\n1. Inputfile: "+args[0] +"\n2. Outputfile: "+args[1]+"\n3. Replacement strategy: "+args[2]+"\n4. Classification attribute no.:"+args[3]+"\n----------------------\n");
		parseCSV(args[0]);

		outfile = args[1];
		replacementStrategy = args[2];
		classAttribute = Integer.parseInt(args[3]);

		if (!replacementStrategy.equals("ignore")) {
			mean = new MeanCalculator(map, classAttribute);
			if (replacementStrategy.equals("mean")) {
				// calculate overall attribute mean
				mean.calculateMean();

			} else if (replacementStrategy.equals("classMean")) {
				// calculate class attribute mean
				mean.calculateClassMean();
			}
		}

		// input: <inputfile> <outputfile> <percentage>
		// randomly generate missing values across all attributes
		if (args.length == 5) {
			percentage = Integer.parseInt(args[4]);
			generateMissingValuesRandomAttribute(percentage);
		} else {
			// input: <inputfile> <outputfile> <att1> <percentage1> <att2>
			// <percentage2> ...
			// generate missing values with specified percentage for specified
			// attribute
			int i = 4;
			while (i + 1 < args.length) {
				if (args[i] != null && args[i + 1] != null) {
					attributeNo = Integer.parseInt(args[i]);
					percentage = Integer.parseInt(args[i + 1]);
					if (attributeNo > -1 && attributeNo < rowlength && percentage > 0 && percentage <= 100) {
						generateMissingValuesKnownAttribute(attributeNo, percentage);
						if (!ignoreAttr.contains(attributeNo)) {
							ignoreAttr.add(attributeNo);
						}
					}
				} else {
					System.out.println("Please check the parameters!");
					System.exit(-1);
				}
				i += 2;
			}
			if (args.length > 5 && args.length % 2 != 0) {
				System.out.println("\n<<WARNING>>: 1 parameter ignored!\n");
			}
		}
		if (!replacementStrategy.equals("ignore") && !replacementStrategy.equals("no")) {
			performReplacement();
		} else if(replacementStrategy.equals("ignore")) {
			System.out.println("\n----------------------\nReplacement strategy: "+replacementStrategy);
			System.out.println("Attributes with missing values will be ignored. >> finished.\n----------------------\n");
		} else {
			System.out.println("\n----------------------\nReplacement strategy: "+replacementStrategy);
			System.out.println("Missing values won't be replaced. >> finished.\n----------------------\n");
		}
		writeCSVFile();
	}

	void parseCSV(String inputfile) {
		File csvFile = new File(inputfile);
		if (!csvFile.exists()) {
			System.out.println("File " + inputfile + " not found! Please check parameter <inputpath>.\n----------------------\n");
			System.exit(-1);

		} else {
			try {
				System.out.println("Parsing file " + inputfile + "...\n");
				FileReader fr = new FileReader(csvFile);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				fileHeader = line;
				System.out.println("Header: " + fileHeader);
				line = br.readLine();
				while (line != null) {
					if (line.contains(",")) {
						linecount++;
						Row row = new Row(line.split(","));
						map.add(row);
						rowlength = line.split(",").length;
					}
					line = br.readLine();
				}
				br.close();
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Parsing file finished. " + linecount + " lines with " + rowlength + " attributes read.\n----------------------\n");
	}

	void generateMissingValuesKnownAttribute(int attributeNo, int percentage) {
		System.out.print("Generating " + percentage + "% missing values for attribute No. " + attributeNo + ".");
		int curMissingValues = 0;
		long randSeed = System.currentTimeMillis();
		// initialize random number generator with seed
		Random rand = new Random(randSeed);

		// calculate number of wanted missing values
		int optMissingValues = (int) Math.ceil(linecount * percentage / 100);
		System.out.print(" >> No. of missing values: " + optMissingValues);

		while (curMissingValues < optMissingValues) {
			int rowIndex = rand.nextInt(map.size());
			Row row = map.get(rowIndex);
			// System.out.println("row.getData()[attributeNo] = "+row.getData()[attributeNo]);
			if (!row.getData()[attributeNo].equals("?")) {
				row.getData()[attributeNo] = "?";
				curMissingValues++;
				map.set(rowIndex, row);
			}
		}
		System.out.println(" >> finished.");
	}

	/*
	 * generate random number, representing the column of the csv file use this
	 * number and call generateMissingValues(attributeno, percentage) in this
	 * function, a second random number is generated, representing the row stop
	 * when x% of all attribute values are missing values
	 */

	void generateMissingValuesRandomAttribute(int percentage) {
		System.out.print("Generating " + percentage + "% randomly distributed missing values.");

		int curMissingValues = 0;
		long randSeed = System.currentTimeMillis();
		Random rand = new Random(randSeed);

		// calculate number of wanted missing values = row * column, x% of all
		// values should be missing
		int colcount = fileHeader.split(",").length-1;
		int optMissingValues = (int) Math.ceil((linecount * colcount) * percentage / 100);
		System.out.print(" >> No. of missing values: " + optMissingValues);

		while (curMissingValues < optMissingValues) {
			int rowIndex = rand.nextInt(map.size());
			Row row = map.get(rowIndex);

			int colIndex = rand.nextInt(colcount);
			// System.out.println("row.getData()[attributeNo] = "+row.getData()[attributeNo]);
			if (!row.getData()[colIndex].equals("?")) {
				row.getData()[colIndex] = "?";
				curMissingValues++;
				map.set(rowIndex, row);
			}
			if (!ignoreAttr.contains(colIndex)) {
				ignoreAttr.add(colIndex);
			}
		}
		System.out.println(" >> finished.");
	}

	void performReplacement() {
		System.out.println("\n----------------------\nReplacement strategy: "+replacementStrategy);
		System.out.print("Replacing missing values...");
		int i;
		String classOfInstance = "";

		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#.###", dfs);

		for (Row r : map) {
			i = 0;
			for (String s : r.getData()) {
					if (s.equals("?") && replacementStrategy.equals("mean")) {
						r.getData()[i] = "" + df.format(mean.getMeanValue(i));
					} else if (s.equals("?") && replacementStrategy.equals("classMean")) {
						classOfInstance = r.getData()[classAttribute];
						r.getData()[i] = "" + df.format(mean.getClassMeanValue(classOfInstance + "|" + i));
					}
				i++;
			}
		}
		System.out.println(" >> finished.\n----------------------\n");
	}

	void writeCSVFile() {

		File file = new File(outfile);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);

			if (replacementStrategy.equals("ignore")) {

				String newHeader = "";
				int i = 0;
				for (String s : fileHeader.split(",")) {
					if (!ignoreAttr.contains(i)) {
						newHeader += s + ",";
					}
					i++;
				}
				pw.println(newHeader.substring(0, newHeader.length() - 1));
				for (Row r : map) {
					pw.println(r.printRow(ignoreAttr, ","));
				}
			} else {
				pw.println(fileHeader);
				for (Row r : map) {
					pw.println(r.printRow(","));
				}
			}
			pw.flush();
			pw.close();
			fw.close();
			System.out.println("Writing CSV file finished. >> Path: " + outfile + "\n\nParser will exit now!\n----------------------\n");
		} catch (IOException e) {
			System.out.println("\nUnable to write file to " + outfile + "! Please check parameter <outputpath>.\n----------------------\n");
			e.printStackTrace();
		}

	}

}
