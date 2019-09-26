package bot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import charts.normaldistributionchart.NormalDistributionChart;
import charts.normaldistributionchart.NormalDistributionData;
import charts.piechart.PieChart;
import charts.piechart.PieData;
import statistics.hypergeo.Group;
import statistics.hypergeo.MultiHyperGeo;
import statistics.hypergeo.UniHyperGeo;
import statistics.hypergeo.data.HyperGeoData;
import statistics.hypergeo.data.MultivariateData;
import statistics.hypergeo.data.UnivariateData;

public class GraphCommands {
	
	private static String chartSavePath = System.getProperty("user.dir") + File.separator;

	public static Command probChartHelp = (event, args) -> {
		BotUtils.sendMessage(event.getChannel(), ""
				+ "This command creates a pie chart, with each group having a size equal to "
				+ "the number of expected successes from a sample size of n, or " 
				+ HyperGeoCommands.DEFAULT_HAND_SIZE + " if not specified."
				+ "```"
				+ "For the syntax of this command, use one of the following:\n"
				+ "Univariate:\n"
				+ BotUtils.BOT_PREFIX + "probChart N, K, k, n\n"
				+ BotUtils.BOT_PREFIX + "probChart N, K, k1, k2, n\n"
				+ "\n"
				+ "**OR**\n"
				+ "\n"
				+ "Multivariate:\n"
				+ BotUtils.BOT_PREFIX + "prob Group, ..., Group, n\n"
				+ BotUtils.BOT_PREFIX + "prob Group, ..., Group\n"
				+ "For what to put for a group, type " + BotUtils.BOT_PREFIX + "gh for details." 
				+ "```"
				+ "```"
				+ "Where:\n"
				+ "N is the total population size\n"
				+ "K is the number of successes/cards in the population or group respectively,\n"	
				+ "k1 is the minimum number of cards reuqired in the sample,\n"
				+ "k2 is the maximum number of cards required in the sample,\n"
				+ "k is the exact number of cards required in the sample (This is shorthand for having the same k1 and k2),\n"
				+ "n is the sample size\n"
				+ "```");
	};

	// Creates a pie chart with the expected number of successes from a sample of size n.
	public static Command probChart = (event, args) -> {
		if (CommandHandler.isHelpCommand(args)) {
			probChartHelp.runCommand(event, args);
			return;
		}

		// Collisions could still happen between two asynchronous calls of this method,
		// but become very, very unlikely.
		// We could check to see if the file exists already, but that would take
		// precious I/O time..
		final String chartFileName = ((Integer) Objects.hash(event, args, System.currentTimeMillis())).toString()
				+ ".png";
		NormalDistributionChart chart = new NormalDistributionChart();

		final boolean useMultivariate = args.trim().matches(HyperGeoCommands.MULTIVARIATE_DECISION_REGEX);

		HyperGeoData data = null;
		if (useMultivariate) {
			data = HyperGeoCommands.parseMultivariate(args);
		} else {
			data = HyperGeoCommands.parseUnivariate(args);
		}

		// Fill chart with mean of each group
		if (useMultivariate) {
			MultivariateData mvd = (MultivariateData) data;
			List<Group> glist = mvd.getGlist();
			int n = mvd.getN();
			for (Group g : glist) {
				chart.addData(new NormalDistributionData(g.getName(), MultiHyperGeo.mean(glist, g, n),
						MultiHyperGeo.standardDeviation(glist, g, n)));
			}
		} else {
			UnivariateData uvd = (UnivariateData) data;
			int N = uvd.getN(), K = uvd.getK(), n = uvd.getn();
			chart.addData(new NormalDistributionData("Specified Cards", UniHyperGeo.mean(N, K, n),
					UniHyperGeo.standardDeviation(N, K, n)));
			chart.addData(new NormalDistributionData("Other Cards", UniHyperGeo.mean(N, N - K, n),
					UniHyperGeo.standardDeviation(N, N - K, n)));
		}

		File chartFile = new File(chartSavePath + chartFileName);
		try {
			chart.setAllowRangeBelowZero(false);
			chart.setXLabel("Relative Frequency");
			chart.setYLabel("Number of cards");
			chart.createChart(chartFile, "Group Graph", "(Graph of Expected Frequency in Sample)");
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandFailureException(
					"Something went wrong, and the chart requested could not be created/saved.");
		}

		BotUtils.verifyFile(chartFile);
		BotUtils.sendFile(event.getChannel(), chartFile);
		chartFile.delete();
	};

	public static Command groupPieChartHelp = (event, args) -> {
		BotUtils.sendMessage(event.getChannel(), ""
				+ "This command creates a pie chart, with each group having a size equal to "
				+ "the size of that group." 
				+ HyperGeoCommands.DEFAULT_HAND_SIZE + " if not specified."
				+ "```"
				+ "For the syntax of this command, use one of the following:\n"
				+ "Univariate:\n"
				+ BotUtils.BOT_PREFIX + "probChart N, K, k, n\n"
				+ BotUtils.BOT_PREFIX + "probChart N, K, k1, k2, n\n"
				+ "\n"
				+ "**OR**\n"
				+ "\n"
				+ "Multivariate:\n"
				+ BotUtils.BOT_PREFIX + "prob Group, ..., Group, n\n"
				+ BotUtils.BOT_PREFIX + "prob Group, ..., Group\n"
				+ "For what to put for a group, type " + BotUtils.BOT_PREFIX + "gh for details." 
				+ "```"
				+ "Where:\n"
				+ "N is the total population size\n"
				+ "K is the number of successes/cards in the population or group respectively,\n"	
				+ "k1 is the minimum number of cards reuqired in the sample,\n"
				+ "k2 is the maximum number of cards required in the sample,\n"
				+ "k is the exact number of cards required in the sample (This is shorthand for having the same k1 and k2),\n"
				+ "n is the sample size\n"
				+ "```");
	};

	public static Command groupPieChart = (event, args) -> {
		if (CommandHandler.isHelpCommand(args)) {
			probChartHelp.runCommand(event, args);
			return;
		}

		// Collisions could still technically happen between two asynchronous calls of
		// this method, but become very, very unlikely. So unlikely in fact that it's
		// better to think of it as impossible.
		// We could check to see if the file exists already to make sure, but that would
		// take precious I/O time.
		final String chartFileName = ((Integer) Objects.hash(event, args, System.currentTimeMillis())).toString()
				+ ".png";
		PieChart chart = new PieChart();

		final boolean useMultivariate = args.trim().matches(HyperGeoCommands.MULTIVARIATE_DECISION_REGEX);

		HyperGeoData data = null;
		if (useMultivariate) {
			data = HyperGeoCommands.parseMultivariate(args);
		} else {
			data = HyperGeoCommands.parseUnivariate(args);
		}

		// Fill chart with data
		if (useMultivariate) {
			MultivariateData mvd = (MultivariateData) data;
			List<Group> glist = mvd.getGlist();
			for (int i = 0; i < glist.size(); i++) {
				Group g = glist.get(i);
				String name = g.getName();
				chart.addData(new PieData(name, g.getK()));
			}
		} else {
			UnivariateData uvd = (UnivariateData) data;
			int N = uvd.getN(), K = uvd.getK();
			chart.addData(new PieData("Specified Cards", K));
			chart.addData(new PieData("Other Cards", N - K));
		}

		File chartFile = new File(chartSavePath + chartFileName);
		try {
			chart.createChart(chartFile, "Group Pie Chart", "(Graph of Expected Frequency in Sample)");
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandFailureException(
					"Something went wrong, and the chart requested could not be created/saved.");
		}

		BotUtils.verifyFile(chartFile);
		BotUtils.sendFile(event.getChannel(), chartFile);
		chartFile.delete();
	};
}
