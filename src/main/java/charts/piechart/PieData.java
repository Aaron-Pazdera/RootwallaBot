package charts.piechart;

import charts.ChartData;

public class PieData implements ChartData {

	private String category;
	private double value;

	public PieData(String category, int value) {
		this.category = category;
		this.value = (double) value;
	}

	public PieData(String category, double value) {
		this.category = category;
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
