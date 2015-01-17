public class MissingValuesSample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if (args.length < 5) {
			System.out
					.printf("Parameter missing!\n\nUsage: <inputfile> <outputfile> <repl. strategy> <class attribute> <percentage> <<OR>> <inputfile> <outputfile> <class attribute> <replStrategy> <att1> <percentage1> <att2> <percentage2> ...\n"
							+ "E.g. C:\\BI\\infile.csv C:\\BI\\outfile.csv ignore 21 10 <<OR>> C:\\BI\\file.csv C:\\BI\\outfile.csv ignore 21 0 10 2 50 \n");
			System.exit(-1);
		}
		new Parser(args);
	}



}
