import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MeanCalculator {

	ArrayList<Row> map = new ArrayList<Row>();
	HashMap<Integer, Double> mean = new HashMap<Integer, Double>();
	HashMap<String, Double> meanClass = new HashMap<String, Double>();
	HashMap<String, Long> classCount = new HashMap<String, Long>();
	int classAttribute;

	public MeanCalculator(ArrayList<Row> map, int classAttribute) {
		super();
		this.map = map;
		this.classAttribute = classAttribute;
	}

	public void calculateMean() {
		double curValue = 0.00;
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#.###", dfs);
		
		for (Row r : map) {
			for (int i = 0; i < r.data.length - 1; i++) {
				if (mean.containsKey(i)) {
					curValue = mean.get(i);
				}
				curValue += Double.parseDouble(r.data[i]);
				mean.put(i, curValue);
				curValue = 0.00;
			}
		}

		String s = "";
		for (int i = 0; i < mean.size(); i++) {
			mean.put(i, mean.get(i) / map.size());
			s += i + ": " + df.format(mean.get(i)) + " | ";
		}

		System.out.println("Missing values will be replaced by the following mean values (attribute No: value):\n" + s + "\n----------------------\n");
	}

	public void calculateClassMean() {
		double curValue = 0.00;
		long curCount = 0;
		String classOfInstance = "";
		String key = "";
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#.###", dfs);
		
		for (Row r : map) {
			classOfInstance = r.data[this.classAttribute];
			if (classCount.containsKey(classOfInstance)) {
				curCount = classCount.get(classOfInstance);
			}
			curCount++;
			classCount.put(classOfInstance, curCount);

			for (int i = 0; i < r.data.length - 1; i++) {
				key = classOfInstance + "|" + i;
				if (meanClass.containsKey(key)) {
					curValue = meanClass.get(key);
				}
				curValue += Double.parseDouble(r.data[i]);
				meanClass.put(key, curValue);
				curValue = 0.00;
				curCount = 0;
			}
		}
		String s = "";
		long count = 0;
		for (Map.Entry<String, Double> e : meanClass.entrySet()) {
			count = classCount.get(e.getKey().substring(0, e.getKey().indexOf("|")));
			meanClass.put(e.getKey(), meanClass.get(e.getKey()) / count);
			s += e.getKey() + ": " + df.format(meanClass.get(e.getKey())) + " | ";
		}
		System.out.println("Missing values will be replaced by the following class mean values (class|attributeNo: value):\n" + s + "\n----------------------\n");
	}

	public double getMeanValue(int attribute) {
		return mean.get(attribute);
	}

	public double getClassMeanValue(String key) {
		return meanClass.get(key);
	}
}
