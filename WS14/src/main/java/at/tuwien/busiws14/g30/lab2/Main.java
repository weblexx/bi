package at.tuwien.busiws14.g30.lab2;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) throws ParseException {
		// create Options object
		Options options = new Options();

		// add options
		options.addOption("h", "help", false, "Show help.");
		options.addOption("m", "myoption", true, "Help for this option.");
		
		CommandLineParser cp = new BasicParser();

		CommandLine cli = cp.parse(options, args);
		
		// print help if help or no options available
		if (cli.hasOption("help") || cli.getOptions().length==0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("These are the available options.", options);
			System.exit(0);
		}
		// just to test our option
		if (cli.hasOption("myoption")) {
			System.out.println(cli.getOptionValue("myoption"));
		}
	}

}
