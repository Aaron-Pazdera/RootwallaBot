package charts.normaldistributionchart;

import java.awt.BasicStroke;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import charts.AbstractChart;
import charts.ChartData;

public class NormalDistributionChart extends AbstractChart {

	public NormalDistributionChart() {

	}

	public NormalDistributionChart(int width, int height) {
		this.width = width;
		this.height = height;
	}

	private static boolean allowRangeBelowZero = true;

	public void setAllowRangeBelowZero(boolean allow) {
		allowRangeBelowZero = allow;
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
		if (!(data instanceof NormalDistributionData)) {
			throw new IllegalArgumentException("Data must be of type NormalDistributionChartData.");
		}
		datalist.add((NormalDistributionData) data);
	}

	private XYSeriesCollection buildDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		for (int i = 0; i < datalist.size(); i++) {
			NormalDistributionData tuple = (NormalDistributionData) datalist.get(i);
			Function2D ndf2d = new NormalDistributionFunction2D(tuple.getMean(), tuple.getStandardDeviation());
			double negrange = tuple.getMean() - (tuple.getStandardDeviation() * 3);
			double posrange = tuple.getMean() + (tuple.getStandardDeviation() * 3);
			if ((!allowRangeBelowZero) && negrange < 0) {
				negrange = 0;
			}
			XYSeries xys = DatasetUtils.sampleFunction2DToSeries(ndf2d, negrange, posrange, 10000, tuple.getSeries());
			dataset.addSeries(xys);
		}

		return dataset;
	}

	public void createChart(File file, String chartTitle, String subtitle) throws IOException {
		if (file == null || chartTitle == null || subtitle == null) {
			throw new IllegalArgumentException("No arguments may be null.");
		}

		XYSeriesCollection dataset = buildDataset();

		chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
				true, // Legend
				true, // Tooltips
				false // URL
		);
		chart.addSubtitle(new TextTitle(subtitle));

		// Set series strokes
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) ((XYPlot) chart.getPlot()).getRenderer();
		for (int i = 0; i < datalist.size(); i++) {
			renderer.setSeriesStroke(i, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		}

		// Place annotations
		XYPlot plot = (XYPlot) chart.getPlot();
		for (int i = 0; i < datalist.size(); i++) {
			NormalDistributionData tuple = (NormalDistributionData) datalist.get(i);

			double xpos = tuple.getMean();
			double ypos = tuple.getHeightAt(tuple.getMean()) * .7;

			// Round to two decimal places
			XYTextAnnotation annotation = new XYTextAnnotation(
					"\u03BC = " + (Math.round(tuple.getMean() * 100.0) / 100.0) + " " + "\u03C3 = "
							+ (Math.round(tuple.getStandardDeviation() * 100.0) / 100.0),
					xpos, ypos);
			plot.addAnnotation(annotation);
		}

		// Add integral dataset

		saveChart(file);
	}

}
