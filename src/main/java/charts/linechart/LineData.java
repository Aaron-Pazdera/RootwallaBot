package charts.linechart;

import charts.barchart.BarData;

public class LineData extends BarData {

	public LineData(String series, String category, int value) {
		super(series, category, value);
	}

	public LineData(String series, String category, double value) {
		super(series, category, value);
	}
}
