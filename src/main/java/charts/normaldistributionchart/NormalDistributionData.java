package charts.normaldistributionchart;

import java.util.ArrayList;

import charts.ChartData;

public class NormalDistributionData implements ChartData {

	private String series;
	private Double mean;
	private Double standardDeviation;
	
	private ArrayList<FillRange> fills = new ArrayList<FillRange>();

	public NormalDistributionData(String series, int mean, int standardDeviation) {
		this.series = series;
		this.mean = (double) mean;
		this.standardDeviation = (double) standardDeviation;
	}

	public NormalDistributionData(String series, double mean, int standardDeviation) {
		this.series = series;
		this.mean = mean;
		this.standardDeviation = (double) standardDeviation;
	}

	public NormalDistributionData(String series, int mean, double standardDeviation) {
		this.series = series;
		this.mean = (double) mean;
		this.standardDeviation = standardDeviation;
	}

	public NormalDistributionData(String series, double mean, double standardDeviation) {
		this.series = series;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Double getMean() {
		return mean;
	}

	public void setMean(Double mean) {
		this.mean = mean;
	}

	public Double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(Double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	
	public void addFill(FillRange fill) {
		fills.add(fill);
	}
	
	public void addFill(Double begin, Double end) {
		fills.add(new FillRange(begin, end));
	}
	
	public FillRange getFill(int index) {
		FillRange ret = null; 
		try {
			ret = fills.get(index);
		} catch(Exception e) {
			throw new IndexOutOfBoundsException();
		}
		return ret;
	}
	
	public ArrayList<FillRange> getFills() {
		return fills;
	}

	// http://www.stat.yale.edu/Courses/1997-98/101/normal.htm
	// http://davidmlane.com/hyperstat/A25726.html
	public double getHeightAt(Double x) {
		Double coefficient = 1.0 / Math.sqrt(2.0 * Math.PI * getStandardDeviation() * getStandardDeviation());
		Double exponentnumerator = -1.0 * (x - getMean()) * (x - getMean());
		Double exponentdenominator = 2 * getStandardDeviation() * getStandardDeviation();
		return coefficient * (Math.pow(Math.E, exponentnumerator/exponentdenominator));
	}

}
