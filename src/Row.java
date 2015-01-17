import java.util.ArrayList;

public class Row {
	String data[] = null;

	public Row(String data[]) {
		super();
		this.data = data;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public String printRow(String delimiter) {
		String rowString = "";
		for (String s : data) {
			rowString += s + delimiter;
		}
		return rowString.substring(0, rowString.length() - 1);
	}

	public String printRow(ArrayList<Integer> ignoreAttr, String delimiter) {
		String rowString = "";
		int i = 0;
		for (String s : data) {
			if (!ignoreAttr.contains(i)) {
				rowString += s + delimiter;
			}
			i++;
		}
		return rowString.substring(0, rowString.length() - 1);
	}

}
