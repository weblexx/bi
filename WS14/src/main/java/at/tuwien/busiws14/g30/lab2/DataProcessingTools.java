package at.tuwien.busiws14.g30.lab2;

import java.util.ArrayList;
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
	 * Calculates the mean of the input data. will throw numberformatexceptions if data is not a number.
	 * @param data
	 * @return
	 */
	public static double calculateMean(ArrayList<String> data) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (data.size() < 1) throw new DataException("Input data must have at least one entry");
		double sum = 0;
		for (String s : data) {
			sum+=Double.valueOf(s);
		}
		return (sum/data.size());
	}
	/**
	 * Returns the median of the input data. Will throw numberformatexceptions if data is not a number.
	 * @param data
	 * @return
	 */
	public static String calculateMedian(ArrayList<String> data) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (data.size() < 1) throw new DataException("Input data must have at least one entry");
		data = sort(data);
		double half = Math.ceil(data.size())-1;
		int half_i = (int) half;
		return data.get(half_i);
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
		data.sort(new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				Double d1 = Double.valueOf(arg0);
				Double d2 = Double.valueOf(arg1);
				
				return d1.compareTo(d2);
			}
			
		});
		return data;
	}
}
