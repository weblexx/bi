package at.tuwien.busiws14.g30.lab2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class wont modify the input CSVTools
 * @author CLF
 *
 */
public class DataProcessingTools {
	public static class DataException extends RuntimeException {
		private static final long serialVersionUID = -881764000276728775L;
		DataException(String msg, Exception e) {
			super(msg, e);
		}
		DataException(String msg) {
			super(msg);
		}
	}
	/**
	 * list of distinct classes
	 */
//	private ArrayList<String> classes = new ArrayList<String>();
	
	//private CSVTools data = null;
	/**
	 * Starts with 0
	 */
//	private int classColumn = -1;
//	/**
//	 * 
//	 * @param data
//	 * @param classColumn - column with classes (start with 0)
//	 */
//	public DataProcessingTools(CSVTools data, int classColumn) {
//		if (data == null) throw new NullPointerException("Input data is null");
//		this.data = data;
//		if (classColumn < 0 || classColumn >= this.data.getNumberOfColumns()) {
//			throw new DataException("classColumn index ("+classColumn+") out of bounds. min:0, max:"+this.data.getNumberOfColumns());
//		}
//		this.classColumn = classColumn;
//		this.classes = getDistinctData(data, classColumn);
//	}
	/**
	 * Returns a list containing the distincs data of the input column
	 * @param data
	 * @param columnIndex
	 * @return
	 */
	public static ArrayList<String> getDistinctData(CSVTools data, int columnIndex) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (columnIndex < 0 || columnIndex >= data.getNumberOfColumns()) {
			throw new DataException("columnIndex ("+columnIndex+") out of bounds. min:0, max:"+data.getNumberOfColumns());
		}
		ArrayList<String> column = data.getColumnData(columnIndex);
		ArrayList<String> classes = new ArrayList<String>();
		for (String s : column) {
			if (!classes.contains(s)) {
				classes.add(s);
			}
		}
		return classes;
	}
	/**
	 * Splits the csvdata by class and returns the result as hashmap
	 * @return
	 */
	public static HashMap<String, ArrayList<ArrayList<String>>> splitDataByClasses(CSVTools data, ArrayList<String> classes, int classColumn) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (classes == null) throw new NullPointerException("Input classes is null");
		if (classColumn < 0 || classColumn >= data.getNumberOfColumns()) {
			throw new DataException("columnIndex ("+classColumn+") out of bounds. min:0, max:"+data.getNumberOfColumns());
		}
		HashMap<String, ArrayList<ArrayList<String>>> out = new HashMap<String, ArrayList<ArrayList<String>>>();
		for (String cl : classes) {
			ArrayList<ArrayList<String>> dataPerClass = new ArrayList<ArrayList<String>>();
			for (ArrayList<String> line : data) {
				if (cl.equals(line.get(classColumn))) {
					dataPerClass.add(line);
				}
			}
			out.put(cl, dataPerClass);
		}
		
		return out;
	}
	/**
	 * Returns a hashmap with all medians for each class in the input column
	 * @param data
	 * @param classes
	 * @param classColumn
	 * @param columToCalculate
	 * @return
	 */
	public static HashMap<String, String> calculateMedianByClassAndColumn(CSVTools data, ArrayList<String> classes,  int classColumn, int columToCalculate) {
		HashMap<String, String> out = new HashMap<String, String>();
		HashMap<String, ArrayList<ArrayList<String>>> dataByClass = splitDataByClasses(data, classes, classColumn);
		
		for (String clas : dataByClass.keySet()) {
			out.put(clas, calculateMedian(getColumn(dataByClass.get(clas), columToCalculate)));
		}
		
		return out;
	}
	/**
	 * Returns a hashmap with all means for each class in the input column
	 * @param data
	 * @param classes
	 * @param classColumn
	 * @param columToCalculate
	 * @return
	 */
	public static HashMap<String, Double> calculateMeanByClassAndColumn(CSVTools data, ArrayList<String> classes,  int classColumn, int columToCalculate) {
		HashMap<String, Double> out = new HashMap<String, Double>();
		HashMap<String, ArrayList<ArrayList<String>>> dataByClass = splitDataByClasses(data, classes, classColumn);
		
		for (String clas : dataByClass.keySet()) {
			out.put(clas, calculateMean(getColumn(dataByClass.get(clas), columToCalculate)));
		}
		
		return out;
	}
	/**
	 * Calculates all medians per class for each column. (in order)
	 * @param data
	 * @param classes
	 * @param classColumn
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> calculateAllMediansByClassAndColumn(CSVTools data, ArrayList<String> classes,  int classColumn) {
		ArrayList<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
		for (int i=0; i<data.getNumberOfColumns(); i++) {
			out.add(calculateMedianByClassAndColumn(data, classes,  classColumn, i));
		}
		
		return out;
	}
	/**
	 * Calculates all means per class for each column. (in order)
	 * @param data
	 * @param classes
	 * @param classColumn
	 * @return
	 */
	public static ArrayList<HashMap<String, Double>> calculateAllMeansByClassAndColumn(CSVTools data, ArrayList<String> classes,  int classColumn) {
		ArrayList<HashMap<String, Double>> out = new ArrayList<HashMap<String, Double>>();
		for (int i=0; i<data.getNumberOfColumns(); i++) {
			out.add(calculateMeanByClassAndColumn(data, classes,  classColumn, i));
		}
		
		return out;
	}
	/**
	 * Returns a list of all medians of the column of the input data (in order)
	 * @param data
	 * @return
	 */
	public static ArrayList<String> calculateAllMedians(CSVTools data) {
		ArrayList<String> out = new ArrayList<String>();
		for (int i=0; i<data.getNumberOfColumns(); i++) {
			ArrayList<String> column = data.getColumnData(i);
			out.add(calculateMedian(column));
			
		}
		return out;
	}
	
	/**
	 * Returns a list of all means of the column of the input data (in order)
	 * @param data
	 * @return
	 */
	public static ArrayList<Double> calculateAllMeans(CSVTools data) {
		ArrayList<Double> out = new ArrayList<Double>();
		for (int i=0; i<data.getNumberOfColumns(); i++) {
			ArrayList<String> column = data.getColumnData(i);
			out.add(calculateMean(column));
			
		}
		return out;
	}
	
	/**
	 * Returns the mean of the input column of the input data
	 * @param data
	 * @return
	 */
	public static Double calculateMeanForColumn(CSVTools data, int columnIndex) {
		Double out = null;
		ArrayList<String> column = data.getColumnData(columnIndex);
		out = calculateMean(column);
		return out;
	}
	
	/**
	 * Returns the median of the input column of the input data
	 * @param data
	 * @return
	 */
	public static String calculateMedianForColumn(CSVTools data, int columnIndex) {
		String out = null;
		ArrayList<String> column = data.getColumnData(columnIndex);
		out = calculateMedian(column);
		return out;
	}
	
	private static ArrayList<String> getColumn(ArrayList<ArrayList<String>> input, int columnIndex) {
		if (columnIndex < 0 || columnIndex >= input.get(0).size()) throw new DataException("input columnIndex out of range");
		ArrayList<String> out = new ArrayList<String>();
		
		for (ArrayList<String> line : input) {
			if (line.size() <= columnIndex) throw new DataException("a line is too small "+line);
			out.add(line.get(columnIndex));
		}
		
		return out;
	}
	/**
	 * Calculates the mean of the input data. will throw numberformatexceptions if data is not a number.
	 * "?" will be seen as empty and ignored, so will empty cells
	 * @param data
	 * @return
	 */
	public static double calculateMean(ArrayList<String> data) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (data.size() < 1) throw new DataException("Input data must have at least one entry");
		double sum = 0;
		for (String s : data) {
			if (s.equalsIgnoreCase("?") || s.equalsIgnoreCase("")) {
				sum+=0;
			} else {
				double add = 0;
				try {
					add = Double.valueOf(s);
				} catch(NumberFormatException e) {
					
				}
				sum+=add;
			}
			
		}
		return (sum/data.size());
	}
	/**
	 * Returns the median of the input data. ignores empty cells or cells with "?"
	 * @param data
	 * @return
	 */
	public static String calculateMedian(ArrayList<String> data) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (data.size() < 1) throw new DataException("Input data must have at least one entry");
		data = sort(data);
		for (int i=0; i<data.size(); i++) {
			if (data.get(i).equalsIgnoreCase("?") || data.get(i).equalsIgnoreCase("")) {
				data.remove(i);
				i--;
			}
		}
		double half = Math.ceil(data.size())-1;
		if (half < 0) return "?";
		int half_i = (int) half;
		return data.get(half_i);
	}
	
	private static <T> ArrayList<T> linearizeMatrix(ArrayList<ArrayList<T>> input) {
		ArrayList<T> out = new ArrayList<T>();
		for (ArrayList<T> line : input) {
			for (T t : line) {
				out.add(t);
			}
		}
		return out;
	}
	/**
	 * Sorts the input data. Will throw numberformatexceptions if data is not a number.
	 * Data must not be empty. data will be modified!!
	 * @param data
	 * @return
	 */
	public static ArrayList<String> sort(ArrayList<String> data) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (data.size() < 1) throw new DataException("Input data must have at least one entry");
		boolean numbers = true;
		for (String s : data) {
			try {
				Double.valueOf(s);
			} catch (NumberFormatException e) {
				numbers = false;
				break;
			}
		}
		if (numbers) {
			Collections.sort(data, new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					Double d1 = Double.valueOf(arg0);
					Double d2 = Double.valueOf(arg1);
					
					return d1.compareTo(d2);
				}
				
			});
		} else {
			Collections.sort(data, new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					return arg0.compareTo(arg1);
				}
				
			});
		}
		
		return data;
	}
}
