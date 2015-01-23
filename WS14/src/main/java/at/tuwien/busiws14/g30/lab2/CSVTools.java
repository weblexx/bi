package at.tuwien.busiws14.g30.lab2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
/**
 * Utility class for managing CSV files
 * @author CLF
 *
 */
public class CSVTools extends ArrayList<ArrayList<String>>{

	private static final long serialVersionUID = -6258609021639175452L;

	private static Logger log = Logger.getLogger(CSVTools.class);

	//private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	private ArrayList<String> header = new ArrayList<String>();
	private ArrayList<String> dataComments = new ArrayList<String>(); // eventual comments in each line INCLUDING THE HEADER!!!
	
	private int numColumns = -1;

	private static final String DEFAULT_LINECOMMENTSYMBOL = "|";
	private static final String DEFAULT_DELIMITER = ",";
	private String delimiter = null; //default
	private String lineCommentSymbol = null; //default
	
	
	private boolean hasHeader = false;
	
	public static class CSVException extends RuntimeException {
		private static final long serialVersionUID = -881764000276728775L;
		CSVException(String msg, Exception e) {
			super(msg, e);
		}
		CSVException(String msg) {
			super(msg);
		}
	}
	/**
	 * Creates a new CSVTools and reads in the input CSV file.<br>
	 * Note: The row-delimiter is ALWAYS newline!!!<br> 
	 * If the number of cells in each row is not consistent with the header,<br>
	 * an exception will be thrown. The header determines the number of columns.
	 * 
	 * @param inputFile
	 * @param delimiter - delimiters between cells, SINGLE CHARACTERS ONLY, not null, not empty
	 */
	public CSVTools(File inputFile, String delimiter, boolean hasHeader, String lineCommentSymbol) {
		this.readFile(inputFile, delimiter, hasHeader, lineCommentSymbol);
		this.delimiter = delimiter;
		this.hasHeader = hasHeader;
		this.lineCommentSymbol = lineCommentSymbol;
	}
	public CSVTools(File inputFile, boolean hasHeader) {
		this(inputFile, DEFAULT_DELIMITER, hasHeader, DEFAULT_LINECOMMENTSYMBOL);
	}
	
	/**
	 * Read in CSV File
	 * @param inputFile
	 */
	private void readFile(File inputFile, String delimiter, boolean hasHeader, String lineCommentSymbol) {
		if (delimiter == null) throw new NullPointerException("Input delimiter is null");
		if (delimiter.equals("")) throw new CSVException("Input delimiter is empty");
		if (lineCommentSymbol == null) throw new NullPointerException("Input lineCommentSymbol is null");
		if (lineCommentSymbol.equals("")) throw new CSVException("Input lineCommentSymbol is empty");
		if (inputFile==null) throw new NullPointerException("Input File is null");
		
		
		int counter = 0;
		BufferedReader br = null;
		String l;
		try {
			br = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			throw new CSVException("Input file does not exist: "+inputFile.getAbsolutePath(), e);
		}
		try {
			boolean firstLine = true;
			
			while ((l = br.readLine()) != null) {
				counter++;
				if (l.equalsIgnoreCase("")) {
					log.warn("Skipping empty line at "+counter);
					continue;
				}
				String[] split = l.split(Pattern.quote(lineCommentSymbol));
				String comment = "";
				if (split.length > 1) {
					l = split[0];
					comment = split[1];
					
				}
				String check = split[0].replaceAll("\\s", "");
				if (check.length() == 0 || check.equalsIgnoreCase("")) {
					log.debug("Skipping empty line "+counter);
					counter--;
					continue;
				}
				
				String[] splittedLine = StringUtils.splitPreserveAllTokens(l, delimiter);
				if (numColumns == -1) {
					numColumns = splittedLine.length;
				} else {
					if (splittedLine.length != numColumns) {
						if (br != null)br.close();
						throw new CSVException("Line "+counter+": invalid number of columns. "+splittedLine.length+" line: "+l);
					}
				}
				
				ArrayList<String> line = new ArrayList<String>();
				for (int i=0; i<splittedLine.length;i++) {
					line.add(splittedLine[i]);
				}
				if (firstLine && hasHeader) {
					header = line;
					firstLine=false;
				} else {
					super.add(line);
				}
				
				dataComments.add(comment);
			}
		} catch (IOException e) {
			throw new CSVException("IOException occured.", e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				throw new CSVException("Could not close BufferedRreader after error.", e);
			}
		}
		
	}
	/**
	 * Write to CSV-File
	 * @param outputFile
	 */
	public void writeToFile(File outputFile) {
		if (outputFile==null) throw new NullPointerException("Output File File is null");
		BufferedWriter bw = null;
		String line;
		try {
			bw = new BufferedWriter(new FileWriter(outputFile, false));
		} catch (IOException e) {
			throw new CSVException("Could not open outputFile "+outputFile.getAbsolutePath()+" for writing.", e);
		}
		try {
			if (this.hasHeader) {
				line = "";
				for (int i=0; i<header.size(); i++) {
					if (i>0) {
						line+=this.delimiter;
					}
					line+=header.get(i);
				}
				if (!dataComments.get(0).equals("")) {
					line+=this.lineCommentSymbol+dataComments.get(0);
				}
				bw.append(line+"\n");
			}
			
			// write data
			for (int i=0; i<this.size(); i++) {
				line = "";
				for (int j=0; j<this.get(i).size();j++) {
					if (j>0) {
						line+=delimiter;
					}
					line+=this.get(i).get(j);
				}
				if (!dataComments.get(i).equals("")) {
					line+=this.lineCommentSymbol+dataComments.get(i);
				}
				bw.append(line+"\n");
			}
			if (bw != null)bw.close();
		} catch (IOException e) {
			throw new CSVException("IOException occured.", e);
		}
		
	}
	public int getNumberOfColumns() {
		return this.numColumns;
	}
	public int getNumberOfRows() {
		return this.numColumns;
	}
	@Override
	public void add(int index, ArrayList<String> element) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean add(ArrayList<String> element) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(Collection<? extends ArrayList<String>> element) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(int index, Collection<? extends ArrayList<String>> element) {
		throw new UnsupportedOperationException();
	}
	/**
	 * Returns data of a column in order without header.
	 * @param columnIndex
	 * @return
	 */
	public ArrayList<String> getColumnData(int columnIndex){
//		if (columnIndex < 0 || columnIndex >= this.numColumns) throw new CSVException("Invalid number of columns. There are "+this.numColumns+" but input tried to access column "+columnIndex);
//		ArrayList<String> out = new ArrayList<String>();
//		for (ArrayList<String> l : this) {
//			out.add(l.get(columnIndex));
//		}
//		return out;
		return getColumn(this, columnIndex);
	}
	/**
	 * Assumes the input data has the same amount of columns every line and there is at least one line.
	 * @param data
	 * @param columnIndex
	 * @return
	 */
	public static ArrayList<String> getColumn(ArrayList<ArrayList<String>> data, int columnIndex){
		if (data == null) throw new NullPointerException("Input data is null");
		if (data.size()<1) throw new CSVException("Input data must have at least one entry");
		if (columnIndex < 0 || columnIndex >= data.get(0).size()) throw new CSVException("Invalid number of columns. There are "+data.get(0).size()+" but input tried to access column "+columnIndex);
		ArrayList<String> out = new ArrayList<String>();
		for (ArrayList<String> l : data) {
			out.add(l.get(columnIndex));
		}
		return out;
	}
	/**
	 * Replaces the input column with the input data (without header)<br>
	 * Throws an exception if the number of rows dont match.
	 * @param columnIndex
	 * @param columnData
	 */
	public void setColumnData(int columnIndex, ArrayList<String> columnData) {
		if (columnIndex < 0 || columnIndex >= this.numColumns) throw new CSVException("Invalid number of columns. There are "+this.numColumns+" but input tried to access column "+columnIndex);
		if (columnData.size() != this.size()) throw new CSVException("Input columnData size("+columnData.size()+") does not match CSVTools"+this.size()+" size.");
		
		for (int i=0; i<this.size(); i++) {
			ArrayList<String> current = this.get(i);
			current.set(columnIndex, columnData.get(i));
			this.set(i, current);
		}
	}
	@Override
	public ArrayList<String> set(int index, ArrayList<String> element) {
		if (element.size() == this.numColumns) {
			return super.set(index, element);
		} else {
			throw new CSVException("Input element size ("+element.size()+") does not match the number of columns ("+this.numColumns+").");
		}
	}
}
