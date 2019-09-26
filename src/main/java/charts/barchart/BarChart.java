package charts.barchart;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import charts.AbstractChart;
import charts.ChartData;


public class BarChart extends AbstractChart {

	public BarChart() {
		super();
	}

	public BarChart(int width, int height) {
		super(width, height);
	}

	private String xAxisLabel = "x-Axis Label";
	private String yAxisLabel = "y-Axis Label";

	public void setXLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public void setYLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	public void addData(ChartData data) {
		if (!(data instanceof BarData)) {
			throw new IllegalArgumentException("Data must be of type BarChartData.");
		}
		datalist.add((BarData) data);
	}

	private DefaultCategoryDataset buildDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < datalist.size(); i++) {
			BarData tuple = (BarData) datalist.get(i);
			dataset.addValue(tuple.getValue(), tuple.getSeries(), tuple.getCategory());
		}
		return dataset;
	}
	
	public void createChart(File file, String chartTitle, String subtitle) throws IOException {
		if (file == null || chartTitle == null || subtitle == null) {
			throw new IllegalArgumentException("No arguments may be null.");
		}

		DefaultCategoryDataset dataset = buildDataset();

		chart = ChartFactory.createBarChart(chartTitle, xAxisLabel, yAxisLabel, (DefaultCategoryDataset) dataset);
		chart.addSubtitle(new TextTitle(subtitle));

		saveChart(file);
	}

}
