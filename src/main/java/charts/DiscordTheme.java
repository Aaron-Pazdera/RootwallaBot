package charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

import java.util.List;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.TextAnchor;

public class DiscordTheme implements ChartTheme {

	public DiscordTheme() {

	}

	static Paint[] paintProgression = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;

	public void apply(JFreeChart chart) {
		// Set across all
		StandardChartTheme theme = new StandardChartTheme("theme");
		theme.apply(chart);
		// Discord chat color
		chart.setBackgroundPaint(new Color(54, 57, 62));

		// Style title
		TextTitle t = chart.getTitle();
		if (t != null) {
			t.setHorizontalAlignment(HorizontalAlignment.CENTER);
			t.setPaint(Color.WHITE);
			t.setFont(new Font("Arial", Font.BOLD, 26));
		}

		// Style subtitle(s)
		for (int i = 0; i < chart.getSubtitleCount(); i++) {
			Title subtitle = chart.getSubtitle(i);
			if (subtitle == null) {
				continue;
			}
			if (subtitle instanceof TextTitle) {
				TextTitle st = (TextTitle) subtitle;
				st.setTextAlignment(HorizontalAlignment.CENTER);
				st.setFont(new Font("TimesRoman", Font.BOLD | Font.ITALIC, 18));
			}
		}

		// Style legend
		LegendTitle legend = chart.getLegend();
		if (legend != null) {
			legend.setBackgroundPaint(null);
			legend.setItemPaint(Color.LIGHT_GRAY);
		}

		// Style plot
		Plot plot = chart.getPlot();
		plot.setBackgroundPaint(null);
		plot.setOutlineVisible(false);

		// PieChart
		if (plot instanceof PiePlot) {
			PiePlot pp = (PiePlot) plot;
			pp.setDefaultSectionOutlinePaint(Color.WHITE);
			pp.setSectionOutlinesVisible(true);
			pp.setDefaultSectionOutlineStroke(new BasicStroke(4.0f));
			pp.setShadowPaint(null);
			pp.setLabelBackgroundPaint(null);
			pp.setLabelPaint(Color.WHITE);
			pp.setLabelFont(new Font(pp.getLabelFont().getFontName(), pp.getLabelFont().getStyle(), 18));
			pp.setLabelLinkStroke(new BasicStroke(1.5f));
			pp.setLabelLinkPaint(Color.WHITE);
		}
		// BarChart and LineChart
		else if (plot instanceof CategoryPlot) {
			CategoryPlot cp = (CategoryPlot) plot;
			cp.setDomainGridlinePaint(Color.WHITE);
			cp.setRangeGridlinePaint(Color.WHITE);

			
			CategoryItemRenderer renderer = cp.getRenderer();
			
			System.out.println("Hello");
			
			int i = 0;
			boolean cont = true;
			while (cont) {
				if (renderer.getSeriesStroke(i) == null) {
					cont = false;
					continue;
				}
				renderer.setSeriesStroke(i, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				System.out.println("Styled #" + i);
				i++;
			}
			
			
			// Style axes
			NumberAxis rangeAxis = (NumberAxis) cp.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			rangeAxis.setLabelPaint(Color.WHITE);
			rangeAxis.setAxisLinePaint(Color.LIGHT_GRAY);
			rangeAxis.setTickLabelPaint(Color.LIGHT_GRAY);

			CategoryAxis domainAxis = cp.getDomainAxis();
			domainAxis.setLabelPaint(Color.WHITE);
			domainAxis.setAxisLinePaint(Color.LIGHT_GRAY);
			domainAxis.setTickLabelPaint(Color.LIGHT_GRAY);
		}
		// NormalDistributionChart
		else if (plot instanceof XYPlot) {
			XYPlot xyp = (XYPlot) plot;
			xyp.setDomainPannable(true);
			xyp.setRangePannable(true);
			
			// Style axes
			NumberAxis rangeAxis = (NumberAxis) xyp.getRangeAxis();
			rangeAxis.setLabelPaint(Color.WHITE);
			rangeAxis.setAxisLinePaint(Color.LIGHT_GRAY);
			rangeAxis.setTickLabelPaint(Color.LIGHT_GRAY);
			rangeAxis.setTickLabelsVisible(true);

			ValueAxis domainAxis = xyp.getDomainAxis();
			domainAxis.setLabelPaint(Color.WHITE);
			domainAxis.setAxisLinePaint(Color.LIGHT_GRAY);
			domainAxis.setTickLabelPaint(Color.LIGHT_GRAY);
			domainAxis.setTickLabelsVisible(true);
			domainAxis.setLowerMargin(0.0);
			domainAxis.setUpperMargin(0.0);
			
			// TODO style integral

			XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) xyp.getRenderer();
			r.setDrawSeriesLineAsPath(true);

			// Style annotations
			@SuppressWarnings("unchecked")
			List<XYTextAnnotation> annotations = xyp.getAnnotations();
			for (int i = 0; i < annotations.size(); i++) {
				XYTextAnnotation a = (XYTextAnnotation) annotations.get(i);
				a.setTextAnchor(TextAnchor.HALF_ASCENT_CENTER);
				a.setBackgroundPaint(paintProgression[i % paintProgression.length]);
				a.setOutlinePaint(Color.LIGHT_GRAY);
			}
		}
	}
}
