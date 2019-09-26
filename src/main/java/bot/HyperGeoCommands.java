package bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import statistics.hypergeo.Group;
import statistics.hypergeo.MultiHyperGeo;
import statistics.hypergeo.UniHyperGeo;
import statistics.hypergeo.data.HyperGeoData;
import statistics.hypergeo.data.MultivariateData;
import statistics.hypergeo.data.UnivariateData;

public class HyperGeoCommands {
	
	static final int DEFAULT_HAND_SIZE = 7;
	
	static final String MULTIVARIATE_DECISION_REGEX = "\\(.*\\)\\w*,.*";
	
	static List<Group> parseGroupList(List<String> groups) throws CommandFailureException {
		List<Group> glist = new ArrayList<>();
		int groupIndex = 0;
		for (String g : groups) {
			String[] arguments = g.split(",");
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = arguments[i].replace(")", "").trim();
			}
			
			// These are the default values for when the argument is omitted. However, K should always be set.
			String name = "Group #" + (groupIndex + 1);
			int K = 0, k1 = 0, k2 = DEFAULT_HAND_SIZE;
			
			if (arguments.length == 1) {// (K),
				try {
					K = Integer.parseInt(arguments[0]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[0] + "\".");
				}
			} else if (arguments.length == 2) {// (name, K), 
				name = arguments[0];
				try {
					K = Integer.parseInt(arguments[1]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[1] + "\".");
				}
			} else if (arguments.length == 3) {// (K, k1, k2),
				try {
					K = Integer.parseInt(arguments[0]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[0] + "\".");
				}
				try {
					k1 = Integer.parseInt(arguments[1]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[1] + "\".");
				}
				try {
					k2 = Integer.parseInt(arguments[2]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[2] + "\".");
				}
			} else if (arguments.length == 4) {// (name, K, k1, k2),
				name = arguments[0];
				try {
					K = Integer.parseInt(arguments[1]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[1] + "\".");
				}
				try {
					k1 = Integer.parseInt(arguments[2]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[2] + "\".");
				}
				try {
					k2 = Integer.parseInt(arguments[3]);
				} catch (NumberFormatException e) {
					throw new CommandFailureException("Expected an integer, but got \"" + arguments[3] + "\".");
				}
			} else {
				throw new CommandFailureException(BotUtils.codeBlock(""
						+ "The group (" + g + ") needs to follow one of the specifications for defining a group.\n"
						+ "For information on defining groups, please type " + BotUtils.BOT_PREFIX + "grouphelp or " + BotUtils.BOT_PREFIX + "gh."
						+ "For additional information on how to use the command you just entered, please retype it, but with only |help| for arguments. Ex: /p help\n"
						));
			}
			glist.add(new Group(name, K, k1, k2));
		}
		return glist;
	}
	
	static UnivariateData parseUnivariate(String args) throws CommandFailureException {
		String[] arguments = args.split(",");
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = arguments[i].trim();
		}
					
		if (arguments.length == 4) {
			int N = 0, K = 0, k = 0, n = DEFAULT_HAND_SIZE;
			try {
				N = Integer.parseInt(arguments[0]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[0] + "\".");
			}
			try {
				K = Integer.parseInt(arguments[1]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[1] + "\".");
			}
			try {
				k = Integer.parseInt(arguments[2]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[2] + "\".");
			}
			try {
				n = Integer.parseInt(arguments[3]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[3] + "\".");
			}
			return new UnivariateData(N, K, k, n);
		} else if (arguments.length == 5) {
			int N = 0, K = 0, k1 = 0, k2 = 0, n = DEFAULT_HAND_SIZE;
			try {
				N = Integer.parseInt(arguments[0]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[0] + "\".");
			}
			try {
				K = Integer.parseInt(arguments[1]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[1] + "\".");
			}
			try {
				k1 = Integer.parseInt(arguments[2]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[2] + "\".");
						}
			try {
				k2 = Integer.parseInt(arguments[3]);
			} catch (NumberFormatException e) {
				throw new CommandFailureException("Expected an integer, but got \"" + arguments[3] + "\".");
			}
			try {
				n = Integer.parseInt(arguments[4]);
			} catch (NumberFormatException e) {
			throw new CommandFailureException("Expected an integer, but got \"" + arguments[4] + "\".");
			}
			return new UnivariateData(N, K, k1, k2, n);
		} else {
			throw new CommandFailureException(""
					+ "```"
					+ "For the syntax of the univariate form of command, use one of the following:\n"
					+ "/prob N, K, k, n\n"
					+ "/prob N, K, k1, k2, n"
					+ "```");
		}
	}
	
	static MultivariateData parseMultivariate(String args) throws CommandFailureException {
		args = args.replaceAll("\\(", "");
		
		// The last index will be n
		String[] groupsAndn = args.split("\\)\\w*,");
		
		int n = HyperGeoCommands.DEFAULT_HAND_SIZE;
		
		// See if the last index in the array is a group or not. If it isn't, then we assume that 
		// they tried to define an n, and we should attempt to parse it.
		boolean nIsPresent = !groupsAndn[groupsAndn.length - 1].contains(",");
		if (nIsPresent) {
			try {
				// Since we matched (.*),.* there must be at least one split, so the array is at least of length two, 
				// and this should never access out of bounds. If they did not specify a value for n, or the index
				// contains some garbage values, this should throw a NumberFormatException. It does so when it sees
				// a parenthesis or non-number character, and it does so when it sees the empty string too.
				n = Integer.parseInt(groupsAndn[groupsAndn.length - 1].trim());
			} catch (NumberFormatException e) {
				throw new CommandFailureException(""
						+ "Expected some integer value n, but got \"" + groupsAndn[groupsAndn.length - 1].trim() + "\" instead.\n"
						+ "```"
						+ "For the syntax of the multivariate form of this command, use one of the following:\n"
						+ BotUtils.BOT_PREFIX + "*commandName* (name, K, k1, k2), ..., (name, K, k1, k2), n\n"
						+ BotUtils.BOT_PREFIX + "*commandName* (name, K, k1, k2), ..., (name, K, k1, k2)\n"
						+ BotUtils.BOT_PREFIX + "*commandName* (K, k1, k2), ..., (K, k1, k2), n\n"
						+ BotUtils.BOT_PREFIX + "*commandName* (K, k1, k2), ..., (K, k1, k2)\n"
						+ "```");
			}
		}
		
		// For ease of access
		List<String> groups = new ArrayList<>(Arrays.asList(groupsAndn));
		// If we've got n, so take off the n so that only the real groups remain
		if (nIsPresent) {
			groups.remove(groups.size() - 1);	
		}
		
		List<Group> glist = HyperGeoCommands.parseGroupList(groups);
		return new MultivariateData(glist, n);
	}
	
	public static Command groupHelp = (event, args) -> {
		BotUtils.sendMessage(event.getChannel(), ""
				+ "```"
				+ "To define a group, choose one of the following. Arguments not specified will be given default values. Also, remember to separate groups with commas.\n"
				+ "\n"
				+ "(K)\n"
				+ "(name, K)\n"
				+ "(K, k1, k2)\n"
				+ "(name, K, k1, k2)\n"
				+ "\n"
				+ "Defaults:\n"
				+ "name: Group #i\n"
				+ "K: No default value. Must be specified every time.\n"
				+ "k1: 0\n"
				+ "k2: n\n"
				+ "```");
	};
	
	public static Command probHelp = (event, args) -> {
		BotUtils.sendMessage(event.getChannel(), ""
				+ "This command calculates the probability of drawing the specified amounts of some specified groups of cards.\n"
				+ "```"
				+ "For the syntax of this command, use one of the following:\n"
				+ "Univariate:\n"
				+ BotUtils.BOT_PREFIX + "prob N, K, k, n\n"
				+ BotUtils.BOT_PREFIX + "prob N, K, k1, k2, n\n"
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
		// Unfortunately I had to split it into two messages, since Discord has a 2000 character limit per message.
		// Sleeping for half a second fixes the issue of the messages sending in the wrong order.
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BotUtils.sendMessage(event.getChannel(), ""
				+ "```"
				+ "Examples:\n"
				+ "Let's say that you want to know the probability that, in your 60 card deck with 24 lands, you'll draw between two and four of them in your seven card opening hand. The probability of a success can be calculated by:\n"
				+ BotUtils.BOT_PREFIX + "prob 60, 24, 2, 4, 7\n\n"
				+ "The same calculation can be acheived also with the more expressive multivariate form of the command, like so:\n"
				+ BotUtils.BOT_PREFIX + "prob (Lands, 24, 2, 4), (Other Cards, 36, 0, 7), 7\n\n"
				+ "The second group named \"Other Cards\" is necessary to bring the count of the deck up to 60. You'll notice that the range starts at zero and ends at seven, the sample size. This is, in effect, a way to say \"I don't care about how many cards out of this group I draw.\"\n\n"
				+ "Now for a more relevant, in-game example. How likely am I to hit at least one counterspell my Azcanta the Sunken Ruin activation? Azcanta digs four cards deep, and let's say we have 43 cards remaining in the deck, six of which are counterspells. The probability of a success can be calculated by:\n"
				+ BotUtils.BOT_PREFIX + "prob (Counterspells, 6, 1, 4), (Other Cards, 37, 0, 4), 4\n\n"
				+ "Similarly, you can calculate the odds that your opponent has something. For example, \"What are the chances that my opponent has a counterspell? I assume that they play four, and sideboarded in three more...\" The answer to this type of problem could easily influence and inform your decision making process during a game of Magic the Gathering, or other similar card game.\n"
				+ "```"
				+ "```"
				+ "Wikipedia article: https://en.wikipedia.org/wiki/Hypergeometric_distribution#Multivariate_hypergeometric_distribution"
				+ "```");
	};
	
	public static Command prob = (event, args) -> {
		if (CommandHandler.isHelpCommand(args)) {
			probHelp.runCommand(event, args);
			return;
		}
		
		HyperGeoData data = null;
		
		boolean useMultivariate;
		useMultivariate = args.trim().matches(MULTIVARIATE_DECISION_REGEX);
		if (useMultivariate) {
			data = parseMultivariate(args);
		}
		else {
			data = parseUnivariate(args);
		}		
		
		Double ans = null;
		try {
			// If there's less than 7 groups, the singlethreaded version will probably be faster.
			if (useMultivariate) {
				MultivariateData mvd = (MultivariateData)data;
				ans = MultiHyperGeo.probability(mvd.getGlist(), mvd.getN(), mvd.getGlist().size() >= 7);
			}
			else {
				UnivariateData uvd = (UnivariateData)data;
				ans = UniHyperGeo.probability(uvd.getN(), uvd.getK(), uvd.getn(), uvd.getk1(), uvd.getk2()); 
			}
		} catch (IllegalArgumentException e) {
			throw new CommandFailureException(e.getMessage());
		} catch (org.apache.commons.math3.exception.MathArithmeticException e) {
			throw new CommandFailureException("Something went wrong. Please use smaller arguments.");
		}
		
		BotUtils.sendMessage(event.getChannel(), BotUtils.formatPercent(ans));
	};

	public static Command probOnMullToXHelp = (event, args) -> {
		BotUtils.sendMessage(event.getChannel(), ""
				+ "This command calculates the probability that, in the course of mulliganing down to X cards in hand, you'll draw the specified amounts of some specified groups of cards.\n"
				+ "```"
				+ "For the syntax of this command, use one of the following:\n"
				+ BotUtils.BOT_PREFIX + "probonmull (name, K, k1, k2), ... (name, K, k1, k2), X\n"
				+ BotUtils.BOT_PREFIX + "probonmull (K, k1, k2), ... (K, k1, k2), X\n"
				+ "```"
				+ "```"
				+ "Where:\n"
				+ "K is the number of cards in the group (Ex: 24 lands)\n"	
				+ "k1 is the minimum number of cards reuqired in the sample (Ex: minimum 2 lands)\n"
				+ "k2 is the maximum number of cards required in the sample (Ex: maximim 4 lands)\n"
				+ "X is the number of cards to mulligan down to (Ex: 5)\n"
				+ "```"
				+ "```"
				+ "Example:\n"
				+ "Let's say that we have a 60 card Magic deck, and we want to know the likelihood that we'll draw a hand containing at least one creature, with between two and four lands. "
				+ "Let's also say that the deck has 15 creatures in it, 24 lands, and we're willing to London mulligan down to 5 cards to find this combination of cards. The probability of a succes can be calculated by:\n"
				+ "\n"
				+ BotUtils.BOT_PREFIX + "probonmull (Lands, 24, 2, 4), (Creature, 15, 1, 7), (Other Cards, 21, 0, 7), 5\n"
				+ "\n"
				+ "The size of the deck becomes equal to the sum of the cards in each group. It may be necessary to add a group for cards not already accounted for. In this case, since we need a 60 card deck, 24 + 15 + 21 will get us there."
				+ "```"
				);
	};
	
	public static Command probOnMullToX = (event, args) -> {

		if (CommandHandler.isHelpCommand(args)) {
			probOnMullToXHelp.runCommand(event, args);
			return;
		}
		
		// Unlike with /prob, if the group structure doesn't exist we should just throw an
		// exception right away. It exists for both Univariate and Multivariate, but it's 
		// difficult to adapt both for Discord. The reason is the variable number of arguments, 
		// and our inability to tell them apart.
		if (!args.trim().matches("\\(.*\\)\\w*,.*")) {
			throw new IllegalArgumentException(""
					+ "```"
					+ "For the syntax of this command, use one of the following:\n"
					+ BotUtils.BOT_PREFIX + "probonmull (name, K, k1, k2), ... (name, K, k1, k2), X\n"
					+ BotUtils.BOT_PREFIX + "probonmull (K, k1, k2), ... (K, k1, k2), X\n"
					+ "```");
		}
		
		args = args.replaceAll("\\(", "");
		
		// The last index will be X
		String[] groupsAndX = args.split("\\)\\w*,");
		int X;
		
		// Unlike parsing for n in /prob, we need to try to find an X. No need to keep track of if we 
		// tried to parse it or not. This is also the reason why we're not using parseMultivariate().
		// We need a value for X, and we can't just parse like for N, because N is optional.
		try {
			// Since we matched (.*),.* there must be at least one split, so the array is at least of length two, 
			// and this should never access out of bounds. If they did not specify a value for n, or the index
			// contains some garbage values, this should throw a NumberFormatException. It does so when it sees
			// a parenthesis or non-number character, and it does so when it sees the empty string too.
			X = Integer.parseInt(groupsAndX[groupsAndX.length - 1].trim());
		} catch (NumberFormatException e) {
			throw new CommandFailureException(""
					+ "Expected some integer value X, but got \"" + groupsAndX[groupsAndX.length - 1].trim() + "\" instead.\n"
					+ "```"
					+ "For the syntax of the multivariate form of this command, use one of the following:\n"
					+ BotUtils.BOT_PREFIX + "probonmull (name, K, k1, k2), ..., (name, K, k1, k2), n\n"
					+ BotUtils.BOT_PREFIX + "probonmull (name, K, k1, k2), ..., (name, K, k1, k2)\n"
					+ BotUtils.BOT_PREFIX + "probonmull (K, k1, k2), ..., (K, k1, k2), n\n"
					+ BotUtils.BOT_PREFIX + "probonmull (K, k1, k2), ..., (K, k1, k2)\n"
					+ "```");
		}
		
		List<String> groups = new ArrayList<>(Arrays.asList(groupsAndX));
		
		// Remove X because it's been parsed and isn't a group.
		groups.remove(groups.size() - 1);
		
		List<Group> glist = parseGroupList(groups);
		
		Double ans = 0d;
		try {
			ans = MultiHyperGeo.probabilityOnLondonMullToX(glist, X);
		} catch (IllegalArgumentException e) {
			throw new CommandFailureException(e.getMessage());
		} catch (org.apache.commons.math3.exception.MathArithmeticException e) {
			throw new CommandFailureException("Something went wrong. Please use smaller arguments.");
		}
		
		BotUtils.sendMessage(event.getChannel(), BotUtils.formatPercent(ans));
	};
	
	
	
}
