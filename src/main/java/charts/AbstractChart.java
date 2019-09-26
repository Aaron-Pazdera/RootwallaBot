package charts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartTheme;

public abstract class AbstractChart {

	public AbstractChart() {

	}

	public AbstractChart(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected int width = 600;
	protected int height = 400;
	// Subclasses must make their own datasets
	protected ArrayList<ChartData> datalist = new ArrayList<ChartData>();
	protected ChartTheme theme = new DiscordTheme();
	protected JFreeChart chart = null;

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setTheme(ChartTheme theme) {
		this.theme = theme;
	}

	abstract public void addData(ChartData data);

	abstract public void createChart(File file, String chartTitle, String subtitle) throws IOException;

	protected void saveChart(File file) throws IOException {
		if (chart == null) {
			throw new IllegalStateException("Please create the chart first.");
		}
		if (file == null) {
			throw new NullPointerException("file cannot be null.");
		}

		theme.apply(chart);

		ChartUtils.saveChartAsPNG(file, chart, width, height);
	}
}
