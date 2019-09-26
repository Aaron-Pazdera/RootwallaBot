package charts.linechart;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import charts.AbstractChart;
import charts.ChartData;

import org.jfree.chart.renderer.AbstractRenderer;
public class LineChart extends AbstractChart {

	public LineChart() {
		super();
	}

	public LineChart(int width, int height) {
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
		if (!(data instanceof LineData)) {
			throw new IllegalArgumentException("Data must be of type BarChartData.");
		}
		datalist.add((LineData) data);
	}

	private DefaultCategoryDataset buildDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < datalist.size(); i++) {
			LineData tuple = (LineData) datalist.get(i);
			dataset.addValue(tuple.getValue(), tuple.getSeries(), tuple.getCategory());
		}
		return dataset;
	}

	public void createChart(File file, String chartTitle, String subtitle) throws IOException {
		if (file == null || chartTitle == null || subtitle == null) {
			throw new IllegalArgumentException("No arguments may be null.");
		}

		chart = ChartFactory.createLineChart(chartTitle, xAxisLabel, yAxisLabel, buildDataset());
		chart.addSubtitle(new TextTitle(subtitle));

		AbstractRenderer rend = (AbstractRenderer) chart.getCategoryPlot().getRenderer(); 
		rend.setAutoPopulateSeriesStroke(true);
		
		saveChart(file);
	}

}
