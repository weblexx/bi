package at.tuwien.busiws14.g30.lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Will modify the input CSVTools
 * @author CLF
 *
 */
public class SparsityTools {
	public static class SparsityException extends RuntimeException {
		private static final long serialVersionUID = -881764000276728775L;
		SparsityException(String msg, Exception e) {
			super(msg, e);
		}
		SparsityException(String msg) {
			super(msg);
		}
	}

	private SparsityTools() {
	}
	/**
	 * takes the input string and returns a list of size size with only the input string in it.
	 * @param input
	 * @param size
	 * @return
	 */
	public static List<String> createStringList(String input, int size) {
		ArrayList<String> out = new ArrayList<String>();
		for (int i=0; i<size; i++) {
			out.add(input);
		}
		return out;
	}
	public static CSVTools replaceAll(CSVTools data, float percentage, String replacement, long seed) {
		return replaceColumnsByClass(data, percentage, null, 0, 
				null, createStringList(replacement, data.getNumberOfColumns()), seed);
	}
	public static CSVTools replaceColumn(CSVTools data, float percentage, int columnIndex, String replacement, long seed) {
		if (replacement==null) throw new SparsityException("Replacement is null");
		if (data==null) throw new SparsityException("Data is null");
		if (percentage == 0) {
			return data; // nothing to do
		}
		if (percentage < 0) throw new SparsityException("percentage must be larger than 0 but was "+percentage);
		if (percentage > 1) throw new SparsityException("percentage must be 1 maximum but was "+percentage);
		ArrayList<String> columnData = data.getColumnData(columnIndex);
		Random random = new Random(seed);
		ArrayList<Integer> index = new ArrayList<Integer>();
		int numLines = columnData.size();
		int currentNumLines = numLines;
		for (int i=0; i<numLines; i++) {
			index.add(i);
		}
		int counter = 0;
		int maxnum = Math.round(percentage*numLines);
		while (counter < maxnum) {
			int linenumber = Math.round(random.nextFloat()*currentNumLines);
			if (linenumber < 1) {
				linenumber = 1;
			}
			if (linenumber > currentNumLines) {
				linenumber = currentNumLines;
			}
			int actualLineNumber = index.get(linenumber-1);
			index.remove(linenumber-1);
			currentNumLines--;
			
			columnData.set(actualLineNumber, replacement);
			
			counter++;
		}
		data.setColumnData(columnIndex, columnData);
		
		return data;
	}
	/**
	 * 
	 * @param data
	 * @param percentage 0-1
	 * @param clas - if null, all classes will be replaced
	 * @param classColumnIndex - index of the column with classes
	 * @param replaceColumnIndices - if null all column will be replaced
	 * @param replacementsByColumn - replacement for each column
	 * @return
	 */
	public static CSVTools replaceColumnsByClass(CSVTools data, float percentage, String clas, int classColumnIndex, List<Integer> replaceColumnIndices, List<String> replacementsByColumn, long seed) {
		if (data==null) throw new SparsityException("Data is null");
		if (classColumnIndex < 0 || classColumnIndex >= data.getNumberOfColumns()) throw new SparsityException("classColumnIndex out of bounds");
		if (replaceColumnIndices != null && replaceColumnIndices.isEmpty()) {
			return data; //nothing to do;
		}
		
		if (replacementsByColumn == null) throw new SparsityException("Input replacementsByColumn is null");
		if (replacementsByColumn != null && replacementsByColumn.size() != data.getNumberOfColumns()) throw new SparsityException("replacementsByColumn has invalid size. Should be "+data.getNumberOfColumns()+" but was "+replacementsByColumn.size());
		for (String s : replacementsByColumn) {
			if (s==null) throw new SparsityException("Input replacementsByColumn contains a null value "+replacementsByColumn);
		}
		
		//if (clas==null) throw new SparsityException("clas is null");
		if (replaceColumnIndices != null) {
			for (Integer i: replaceColumnIndices) {
				if (i==null) throw new SparsityException("An index of the replaceColumnIndices is null "+replaceColumnIndices);
				if (i < 0 || i >= data.getNumberOfColumns()) throw new SparsityException("An index of the replaceColumnIndices is out of bounds "+replaceColumnIndices);
			}
		}
		
		if (percentage < 0) throw new SparsityException("percentage must be larger than 0 but was "+percentage);
		if (percentage > 1) throw new SparsityException("percentage must be 1 maximum but was "+percentage);
		
		Random random = new Random(seed);
		
		ArrayList<Integer[]> index = new ArrayList<Integer[]>();
		int numLines = data.getNumberOfRows();
		
		for (int i=0; i<numLines; i++) { // add only if class matches
			if (clas==null || data.get(i).get(classColumnIndex).equals(clas)) {
				for (int j=0; j<data.get(i).size(); j++) {
					if (replaceColumnIndices == null || replaceColumnIndices.contains(j)) {
						Integer[] inty = {i,j};
						index.add(inty);
						//System.out.println(i+":"+j);
					}
				}
				
			}
		}
		int currentNumIndexLines = index.size();
		//System.out.println("index size: "+index.size());
		int counter = 0;
		int maxnum = Math.round(percentage*index.size());
		while (counter < maxnum) {
			int indexLineNumber = Math.round(random.nextFloat()*currentNumIndexLines);
			if (indexLineNumber < 1) {
				indexLineNumber = 1;
			}
			if (indexLineNumber > currentNumIndexLines) {
				indexLineNumber = currentNumIndexLines;
			}
			int actualLineNumber = index.get(indexLineNumber-1)[0];
			int actualColumnNumber = index.get(indexLineNumber-1)[1];
			index.remove(indexLineNumber-1);
			
			currentNumIndexLines--;
			ArrayList<String> newLine = data.get(actualLineNumber);
			newLine.set(actualColumnNumber, replacementsByColumn.get(actualColumnNumber));
			data.set(actualLineNumber, newLine);
			//System.out.println(counter+" removing "+actualLineNumber+":"+actualColumnNumber+" indexsize: "+index.size());
			counter++;
		}
		return data;
	}
}
