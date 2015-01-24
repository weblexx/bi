package at.tuwien.busiws14.g30.lab2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

public class CSVProcessor {
	private static File inputFile = null;
	private static File outputFile = null;
	private static CommandLine cli = null;
	private static String command = null;
	private static long seed = 123L;
	private static ArrayList<Float> percentagesByColumn = null;
	private static float percentage = 0;
	private static Options options = null;
	private static CSVTools csv = null;
	
	private static Integer classColumn = null;
	
	private static String replacement = "?";
	
	public static void printUsageAndExit() {
		System.out.println("Usage: CSVProcessor -i {inputfile} -o {outputfile} -c {command} [other arguments dependent on command...]");
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("These are the available options.", options);
		System.exit(0);
	}
	public static void main(String[] args) throws ParseException {
		// create Options object
		options = new Options();
		// add options
		options.addOption("h", "help", false, "Show help.");
		options.addOption("i", "input", true, "the input CSV file");
		options.addOption("o", "output", true, "the output CSV file");
		options.addOption("c", "command", true, "The command to execute. Can be: "
				+ "sparsifyByColumn, sparsify, sparsifyByColumnMedian, sparsifyByColumnMean, "
				+ "replaceEmptyWith, sparsifyByColumnMedianClass, sparsifyByColumnMeanClass, "
				+ "removeMissing (removes all rows with missing data),"
				+ "replaceEmptyMeanByClass, replaceEmptyMedianByClass, "
				+ "replaceEmptyMean, replaceEmptyMedian");
		options.addOption("s", "seed", true, "(optional) The seed for the random number generator. 123L by default");
		options.addOption("pbc", "percentageByColumn", true, "assign float values to each columns between 0 and 1 seperated by \";\". Example: 0;0.22;0.33. Must have as many entries as there are columns in the input csv. Also percentages that are 0 can be used to select columns to process as those will not be attempted.");
		options.addOption("p", "percentage", true, "a float percentage between 0 and 1. Nothing happens when it's 0");
		options.addOption("r", "replacement", true, "a string replacement (for replaceEmptyWith)");
		options.addOption("cl", "classColumn", true, "The column with the classes (index starts with 0) (for all class-related commands)");

		
		CommandLineParser cp = new BasicParser();

		cli = cp.parse(options, args);
		
		// print help if help or no options available
		if (cli.hasOption("help") || cli.getOptions().length==0) {
			printUsageAndExit();
		}
		
		if (cli.hasOption("input")) {
			inputFile = new File(cli.getOptionValue("input"));
		} else {
			System.out.println("No input file");
			printUsageAndExit();
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("These are the available options.", options);
			System.exit(0);
		}
		
		if (cli.hasOption("output")) {
			outputFile = new File(cli.getOptionValue("output"));
		} else {
			System.out.println("No output file");
			printUsageAndExit();
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("These are the available options.", options);
			System.exit(0);
		}
		
		if (cli.hasOption("seed")) {
			try {
				seed = Long.parseLong(cli.getOptionValue("seed"));
			} catch(NumberFormatException e) {
				System.out.println("Seed "+cli.getOptionValue("seed")+" must be a (long) number.");
				System.exit(-1);
			}
		}
		
		if (cli.hasOption("replacement")) {
				replacement = cli.getOptionValue("replacement");
		}
		
		
		
		csv = new CSVTools(inputFile, ",",true,"#############");
		
		if (cli.hasOption("classColumn")) {
			classColumn = Integer.parseInt(cli.getOptionValue("classColumn"));
			if (classColumn < 0) {
				System.out.println("ClassColumn must be larger than 0");
				printUsageAndExit();
			}
			
			if (classColumn >= csv.getNumberOfColumns()) {
				System.out.println("ClassColumn must be smaller than the number of columns in the input file. "+csv.getNumberOfColumns());
				printUsageAndExit();
			}
				
		}
		
		if (cli.hasOption("command")) {
			command = cli.getOptionValue("command");
			if (command.equalsIgnoreCase("removeMissing")) {
				removeMissing();
			} else if (command.equalsIgnoreCase("sparsify")) {
				if (!cli.hasOption("percentage")) {
					System.out.println("percentage missing");
					printUsageAndExit();
				}
				try{
					percentage = Float.parseFloat(cli.getOptionValue("percentage"));
				} catch(NumberFormatException e) {
					System.out.println("percentage not a float");
					printUsageAndExit();
				}
				if (percentage < 0) {
					System.out.println("percentage must be >= 0");
					printUsageAndExit();
				}
				if (percentage > 1) {
					System.out.println("percentage must be <= 1");
					printUsageAndExit();
				}
				sparsify();
			}
			else if (command.equalsIgnoreCase("sparsifyByColumn")) {
				
				if (!cli.hasOption("percentageByColumn")) {
					System.out.println("percentageByColumn missing");
					printUsageAndExit();
				}
				String[] splits = StringUtils.split(cli.getOptionValue("percentageByColumn"), ";");

				percentagesByColumn = new ArrayList<Float>();
				for (String s : splits) {
					//System.out.println("splitstring "+s);
					percentagesByColumn.add(Float.parseFloat(s));
				}
				if (percentagesByColumn.size() != csv.getNumberOfColumns()) {
					System.out.println("The number of columns in the input ("+csv.getNumberOfColumns()+") does not match the number of columns in percentageByColumn ("+percentagesByColumn.size()+")");
					System.exit(-1);
				}
				
				sparsifyByColumn();
			} else if(command.equalsIgnoreCase("sparsifyByColumnMedian")) {
				if (!cli.hasOption("percentageByColumn")) {
					System.out.println("percentageByColumn missing");
					printUsageAndExit();
				}
				String[] splits = StringUtils.split(cli.getOptionValue("percentageByColumn"), ";");

				percentagesByColumn = new ArrayList<Float>();
				for (String s : splits) {
					//System.out.println("splitstring "+s);
					percentagesByColumn.add(Float.parseFloat(s));
				}
				if (percentagesByColumn.size() != csv.getNumberOfColumns()) {
					System.out.println("The number of columns in the input ("+csv.getNumberOfColumns()+") does not match the number of columns in percentageByColumn ("+percentagesByColumn.size()+")");
					System.exit(-1);
				}
				
				sparsifyByColumnMedian();
			} else if(command.equalsIgnoreCase("sparsifyByColumnMedianClass")) {
				if (classColumn == null) {
					System.out.println("classColumn missing");
					printUsageAndExit();
				}
				if (!cli.hasOption("percentageByColumn")) {
					System.out.println("percentageByColumn missing");
					printUsageAndExit();
				}
				String[] splits = StringUtils.split(cli.getOptionValue("percentageByColumn"), ";");

				percentagesByColumn = new ArrayList<Float>();
				for (String s : splits) {
					//System.out.println("splitstring "+s);
					percentagesByColumn.add(Float.parseFloat(s));
				}
				if (percentagesByColumn.size() != csv.getNumberOfColumns()) {
					System.out.println("The number of columns in the input ("+csv.getNumberOfColumns()+") does not match the number of columns in percentageByColumn ("+percentagesByColumn.size()+")");
					System.exit(-1);
				}
				
				sparsifyByColumnMedianClass();
			} else if(command.equalsIgnoreCase("sparsifyByColumnMean")) {
				if (!cli.hasOption("percentageByColumn")) {
					System.out.println("percentageByColumn missing");
					printUsageAndExit();
				}
				String[] splits = StringUtils.split(cli.getOptionValue("percentageByColumn"), ";");

				percentagesByColumn = new ArrayList<Float>();
				for (String s : splits) {
					//System.out.println("splitstring "+s);
					percentagesByColumn.add(Float.parseFloat(s));
				}
				if (percentagesByColumn.size() != csv.getNumberOfColumns()) {
					System.out.println("The number of columns in the input ("+csv.getNumberOfColumns()+") does not match the number of columns in percentageByColumn ("+percentagesByColumn.size()+")");
					System.exit(-1);
				}
				
				sparsifyByColumnMean();
			}  else if(command.equalsIgnoreCase("sparsifyByColumnMeanClass")) {
				if (classColumn == null) {
					System.out.println("classColumn missing");
					printUsageAndExit();
				}
				if (!cli.hasOption("percentageByColumn")) {
					System.out.println("percentageByColumn missing");
					printUsageAndExit();
				}
				String[] splits = StringUtils.split(cli.getOptionValue("percentageByColumn"), ";");

				percentagesByColumn = new ArrayList<Float>();
				for (String s : splits) {
					//System.out.println("splitstring "+s);
					percentagesByColumn.add(Float.parseFloat(s));
				}
				if (percentagesByColumn.size() != csv.getNumberOfColumns()) {
					System.out.println("The number of columns in the input ("+csv.getNumberOfColumns()+") does not match the number of columns in percentageByColumn ("+percentagesByColumn.size()+")");
					System.exit(-1);
				}
				
				sparsifyByColumnMeanClass();
			} else if(command.equalsIgnoreCase("replaceEmptyWith")) {
				replaceEmptyWith();
			} else if(command.equalsIgnoreCase("replaceEmptyMeanByClass")) {
				if (classColumn == null) {
					System.out.println("classColumn missing");
					printUsageAndExit();
				}
				replaceEmptyMeanByClass();
			} else if(command.equalsIgnoreCase("replaceEmptyMedianByClass")) {
				if (classColumn == null) {
					System.out.println("classColumn missing");
					printUsageAndExit();
				}
				replaceEmptyMedianByClass();
			} else if(command.equalsIgnoreCase("replaceEmptyMean")) {
				replaceEmptyMean();
			} else if(command.equalsIgnoreCase("replaceEmptyMedian")) {
				replaceEmptyMedian();
			} else {
				System.out.println("Invalid command "+command);
				printUsageAndExit();
			}
		} else {
			System.out.println("No command");
			printUsageAndExit();
		}
		System.out.println("Commandline: "+ArraytoString(args));
		System.out.println("Command "+command+" executed successfully. Output: "+outputFile.getAbsolutePath());
	}
	public static <T> String ArraytoString(T[] input) {
		if (input==null) return "";
		String out = "[";
		for (int i=0; i<input.length; i++) {
			if (i>0) out+=",";
			out+=input[i].toString();
		}
		out+="]";
		return out;
	}
	private static void sparsifyByColumn() {
		for (int i=0; i<csv.getNumberOfColumns(); i++) {
			csv = SparsityTools.replaceColumn(csv, percentagesByColumn.get(i), i, "?", seed);
		}
		csv.writeToFile(outputFile);
	}
	
	private static void sparsify() {
		csv = SparsityTools.replaceAll(csv, percentage, "?", seed);
		csv.writeToFile(outputFile);
	}

	private static void sparsifyByColumnMedian() {
		
		for (int i=0; i<csv.getNumberOfColumns(); i++) {
			String median = "?";
			if (percentagesByColumn.get(i) > 0) {
				median = DataProcessingTools.calculateMedianForColumn(csv, i);
				csv = SparsityTools.replaceColumn(csv, percentagesByColumn.get(i), i, median, seed);
			}
			
		}
		csv.writeToFile(outputFile);
	}

	private static void sparsifyByColumnMean() {
		for (int i=0; i<csv.getNumberOfColumns(); i++) {
			Double mean = -1.0;
			if (percentagesByColumn.get(i) > 0) {
				mean = DataProcessingTools.calculateMeanForColumn(csv, i);
				csv = SparsityTools.replaceColumn(csv, percentagesByColumn.get(i), i, mean.toString(), seed);
			}
			
		}
		csv.writeToFile(outputFile);
	}
	
	private static void replaceEmptyWith() {
		for (int i=0; i<csv.getNumberOfRows(); i++) {
			ArrayList<String> line = csv.get(i);
			for (int j=0; j<line.size(); j++) {
				if (line.get(j).equals("")) {
					line.set(j, replacement);
				}
			}
			csv.set(i, line);
		}
		csv.writeToFile(outputFile);
	}
	
	private static void replaceEmptyMean() {
		ArrayList<Double> means = DataProcessingTools.calculateAllMeans(csv);
		for (int i=0; i<csv.getNumberOfRows(); i++) {
			ArrayList<String> line = csv.get(i);
			for (int j=0; j<line.size(); j++) {
				if (line.get(j).equals("?")) {
					line.set(j, means.get(j).toString());
				}
			}
			csv.set(i, line);
		}
		csv.writeToFile(outputFile);
	}
	private static void replaceEmptyMedian() {
		ArrayList<String> medians = DataProcessingTools.calculateAllMedians(csv);
		for (int i=0; i<csv.getNumberOfRows(); i++) {
			ArrayList<String> line = csv.get(i);
			for (int j=0; j<line.size(); j++) {
				if (line.get(j).equals("?")) {
					line.set(j, medians.get(j));
				}
			}
			csv.set(i, line);
		}
		csv.writeToFile(outputFile);
	}
	
	private static void replaceEmptyMeanByClass() {
		ArrayList<String> classes = DataProcessingTools.getDistinctData(csv, classColumn);
		ArrayList<HashMap<String, Double>> meansByClass = DataProcessingTools.calculateAllMeansByClassAndColumn(csv, classes, classColumn);
		for (int i=0; i<csv.getNumberOfRows(); i++) {
			ArrayList<String> line = csv.get(i);
			for (int j=0; j<line.size(); j++) {
				if (line.get(j).equals("?")) {
					line.set(j, meansByClass.get(j).get(line.get(classColumn)).toString());
				}
			}
			csv.set(i, line);
		}
		csv.writeToFile(outputFile);
	}
	
	private static void replaceEmptyMedianByClass() {
		ArrayList<String> classes = DataProcessingTools.getDistinctData(csv, classColumn);
		ArrayList<HashMap<String, String>> mediansByClass = DataProcessingTools.calculateAllMediansByClassAndColumn(csv, classes, classColumn);
		for (int i=0; i<csv.getNumberOfRows(); i++) {
			ArrayList<String> line = csv.get(i);
			for (int j=0; j<line.size(); j++) {
				if (line.get(j).equals("?")) {
					line.set(j, mediansByClass.get(j).get(line.get(classColumn)));
				}
			}
			csv.set(i, line);
		}
		csv.writeToFile(outputFile);
	}

	
	
	
	
	
		
	private static void sparsifyByColumnMedianClass() {
		ArrayList<String> classes = DataProcessingTools.getDistinctData(csv, classColumn);
		for (int i=0; i<csv.getNumberOfColumns(); i++) {
			
			if (percentagesByColumn.get(i) > 0) {
				
				HashMap<String, String> mediansByClass = DataProcessingTools.calculateMedianByClassAndColumn(csv, classes, classColumn, i);
				ArrayList<Integer> columnsToReplace = new ArrayList<Integer>();
				columnsToReplace.add(i);
				
				for (String clas : mediansByClass.keySet()) {
					List<String> replacementsByColumn = SparsityTools.createStringList(mediansByClass.get(clas), csv.getNumberOfColumns());
					csv = SparsityTools.replaceColumnsByClass(csv, percentagesByColumn.get(i), clas, classColumn, columnsToReplace, replacementsByColumn, seed);
				}
				
			}
			
		}
		csv.writeToFile(outputFile);
		
	}

	private static void sparsifyByColumnMeanClass() {
		ArrayList<String> classes = DataProcessingTools.getDistinctData(csv, classColumn);
		for (int i=0; i<csv.getNumberOfColumns(); i++) {
			
			if (percentagesByColumn.get(i) > 0) {
				
				HashMap<String, Double> meansByClass = DataProcessingTools.calculateMeanByClassAndColumn(csv, classes, classColumn, i);
				ArrayList<Integer> columnsToReplace = new ArrayList<Integer>();
				columnsToReplace.add(i);
				
				for (String clas : meansByClass.keySet()) {
					List<String> replacementsByColumn = SparsityTools.createStringList(meansByClass.get(clas).toString(), csv.getNumberOfColumns());
					csv = SparsityTools.replaceColumnsByClass(csv, percentagesByColumn.get(i), clas, classColumn, columnsToReplace, replacementsByColumn, seed);
				}
				
			}
			
		}
		csv.writeToFile(outputFile);
		
	}
	
	private static void removeMissing() {
		for (int i=0; i<csv.getNumberOfRows(); i++) {
			ArrayList<String> line = csv.get(i);
			for (String s : line) {
				if (s.equalsIgnoreCase("") || s.equalsIgnoreCase("?")) {
					csv.remove(i);
					i--;
					break;
				}
			}
		}
		csv.writeToFile(outputFile);
	}

}
