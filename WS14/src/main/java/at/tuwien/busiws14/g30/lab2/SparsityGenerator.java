package at.tuwien.busiws14.g30.lab2;

import java.util.HashMap;

/**
 * Will modify the input CSVTools
 * @author CLF
 *
 */
public class SparsityGenerator {
	public static class SparsityException extends RuntimeException {
		private static final long serialVersionUID = -881764000276728775L;
		SparsityException(String msg, Exception e) {
			super(msg, e);
		}
		SparsityException(String msg) {
			super(msg);
		}
	}
	
	private static final long serialVersionUID = -3760978622748022342L;
	private String replacement = "?";
	private CSVTools data = null;
	/**
	 * 
	 * @param data
	 * @param replacement - may be empty
	 */
	public SparsityGenerator(CSVTools data, String replacement) {
		if (data == null) throw new NullPointerException("Input data is null");
		if (replacement == null) throw new NullPointerException("Input data is null");
		
		this.data = data;
		this.replacement = replacement;
		
	}
	
	public void replaceAll(float percentage) {
		
	}
	public void replaceColumn(float percentage, int columnIndex) {
		
	}
	/**
	 * The class-map does not have to be complete
	 * @param percentage
	 */
	public void replaceAllByClass(HashMap<String,Float> percentagesByClass) {
		
	}
	public void replaceColumnByClass(HashMap<String,Float> percentagesByClass, int columnIndex) {
		
	}

}
