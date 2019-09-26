package charts.barchart;

import charts.ChartData;

public class BarData implements ChartData {
	protected String series;
	protected String category;
	protected double value;

	public BarData(String series, String category, double value) {
		this.setSeries(series);
		this.setCategory(category);
		this.setValue(value);
	}

	public BarData(String series, String category, int value) {
		this.setSeries(series);
		this.setCategory(category);
		this.setValue((double) value);
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
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
