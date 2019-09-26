package charts.piechart;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import charts.AbstractChart;
import charts.ChartData;

public class PieChart extends AbstractChart {

	private boolean showlegend = false;

	public PieChart() {
		super();
	}

	public PieChart(int width, int height) {
		super(width, height);
	}

	public void showLegend(boolean show) {
		this.showlegend = show;
	}
	
	public void addData(ChartData data) {
		if (!(data instanceof PieData)) {
			throw new IllegalArgumentException("Data must be of type PieChartData.");
		}
		datalist.add((PieData) data);
	}
	
	private DefaultPieDataset buildDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(int i = 0; i < datalist.size(); i++) {
			PieData tuple = (PieData) datalist.get(i);
			dataset.setValue(tuple.getCategory(), tuple.getValue());
		}
		return dataset;
	}

	public void createChart(File file, String chartTitle, String subtitle) throws IOException {
		if (file == null || chartTitle == null || subtitle == null) {
			throw new IllegalArgumentException("No arguments may be null.");
		}
		
		DefaultPieDataset dataset = buildDataset();

		chart = ChartFactory.createPieChart(chartTitle, // chart title
				(DefaultPieDataset) dataset, // data
				showlegend, // legend
				false, // tooltips
				false // URL generation
		);
		chart.addSubtitle(new TextTitle(subtitle)); // subtitle

		saveChart(file);
	}

}

