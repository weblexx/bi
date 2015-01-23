package at.tuwien.busiws14.g30.lab2;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	private static File inputFile = null;
	private static File outputFile = null;
	public static void main(String[] args) throws ParseException {
		// create Options object
		Options options = new Options();
		// add options
		options.addOption("h", "help", false, "Show help.");
		options.addOption("i", "input", true, "the input CSV file");
		options.addOption("o", "output", true, "the output CSV file");
		
		CommandLineParser cp = new BasicParser();

		CommandLine cli = cp.parse(options, args);
		
		// print help if help or no options available
		if (cli.hasOption("help") || cli.getOptions().length==0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("These are the available options.", options);
			System.exit(0);
		}
		// just to test our option
		if (cli.hasOption("input")) {
			inputFile = new File(cli.getOptionValue("input"));
		}
		// just to test our option
		if (cli.hasOption("output")) {
			outputFile = new File(cli.getOptionValue("output"));
		}
		
		
		
		CSVTools csv = new CSVTools(inputFile, ",",true,"#############");
		
		csv.writeToFile(outputFile);
	}
	
	

}
