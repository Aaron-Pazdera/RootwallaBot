package bot;

import statistics.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;

@SuppressWarnings("unused")
public class CommandHandler {

	// Contains every command that can be called
	private static Map<String, Command> commandMap = new HashMap<>();

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event) {
		// Don't process messages received by bots
		if (event.getAuthor().isBot() == true) {
			return;
		}
		// Don't process messages that aren't commands.
		String message = event.getMessage().getContent().trim();
		if (!message.startsWith(BotUtils.BOT_PREFIX) || message == null) {
			return;
		}

		// Process the command.

		// Splits up the line after every space.
		String[] messageSplit = message.split(" ");

		// Strip the bot prefix off the 0th entry, leaving
		// only the command name in the 0th element of the array.
		String commandStr = messageSplit[0].substring(BotUtils.BOT_PREFIX.length()).toLowerCase().trim();

		// Combine the rest back together. Note that this does trim some extra spaces.
		String argStr = "";
		for (int i = 1; i < messageSplit.length; i++) {
			argStr += " " + messageSplit[i].trim();
		}
		argStr.trim();

		// Run the command specified, and alert the user if there's an error.
		if (commandMap.containsKey(commandStr)) {
			try {
				System.out.println("Command entered: " + commandStr);
				commandMap.get(commandStr).runCommand(event, argStr);
			} catch (CommandFailureException e) {
				// Prints message to user in discord if they caused the issue.
				BotUtils.sendMessage(event.getChannel(), e.getMessage());
			} catch (Exception e) {
				// If there's a legitimate bug or something,
				// print it to command line instead. No need to tell the
				// user if there's nothing they can do about it.
				System.out.println("\n\n");
				e.printStackTrace(System.out);
			}

		}

	}

	public static boolean isHelpCommand(String args) {
		// args is immutable, so the original is not affected.
		args = args.trim();
		return args.equalsIgnoreCase("h") || args.equalsIgnoreCase("help") || args.equalsIgnoreCase("-h");
	}

	// Registers all the commands.
	static {

		// NOTE: Argument strings are what they user typed in, but split on
		// spaces, trimmed, and concatenated back together with spaces between.
		// The end result of this is that every token is separated by
		// exactly one space.

		System.out.println("Loading Command map.");

		// Initialize the command map with all commands
		
		Command listCommands = (event, args) -> {
			BotUtils.sendMessage(event.getChannel(), ""
					+ "```"
					+ "//***********************//\n" 
					+ "//  Statistics Commands  //\n" 
					+ "//***********************//\n"
					+ "```"
					+ "**" + BotUtils.BOT_PREFIX + "prob** *Calculates the probability that you'll draw the combination of cards that you want.*\n"
					+ "**" + BotUtils.BOT_PREFIX + "probonmulltox** *Given that you're willing to mulligan down to x cards in hand, this command calculates the cumulative probability that you'll draw the combination of cards that you want.*\n"
					+ ""
					+ ""
					+ "```"
					+ "//***********************//\n" 
					+ "//    Graph Commands     //\n" 
					+ "//***********************//\n"
					+ "```"
					+ "**" + BotUtils.BOT_PREFIX + "probchart** *Creates a bell curve of expected frequency in the sample.*\n"
					+ "**" + BotUtils.BOT_PREFIX + "probonmulltox** *Creates a pie chart of the size of each group.*\n"
					+ ""
					+ ""
					+ "```"
					+ "//***********************//\n" 
					+ "//     Misc Commands     //\n" 
					+ "//***********************//\n"
					+ "```"
					+ "**" + BotUtils.BOT_PREFIX + "<commandname>help** *Displays a message describing how to use the command. This message may be very long.*\n"
					+ "**" + BotUtils.BOT_PREFIX + "commandlist** *Displays this list of commands.*\n"
					+ ""
					+ "**" + BotUtils.BOT_PREFIX + "snowflake** *Posts the unique snowflake ID of your discord profile.*\n"
					+ "**" + BotUtils.BOT_PREFIX + "profile** *Posts a link to your profile picture.*\n"
					+ ""
					);
		};
		
			commandMap.put("commandlist", listCommands);
			commandMap.put("listcommands", listCommands);
			
		

		//***********************//
		//  Statistics Commands  //
		//***********************//
		
		// Group Help
		commandMap.put("gh", HyperGeoCommands.groupHelp);
		commandMap.put("grouphelp", HyperGeoCommands.groupHelp);
		
		// Prob
		commandMap.put("p", HyperGeoCommands.prob);
		commandMap.put("phelp", HyperGeoCommands.probHelp);
		
		commandMap.put("prob", HyperGeoCommands.prob);
		commandMap.put("probhelp", HyperGeoCommands.probHelp);

		commandMap.put("probability", HyperGeoCommands.prob);
		commandMap.put("probabilityhelp", HyperGeoCommands.probHelp);
		
		// Prob on mull to X
		commandMap.put("pom", HyperGeoCommands.probOnMullToX);
		commandMap.put("pomhelp", HyperGeoCommands.probOnMullToXHelp);
		
		commandMap.put("probmull", HyperGeoCommands.probOnMullToX);
		commandMap.put("probmullhelp", HyperGeoCommands.probOnMullToXHelp);
		
		commandMap.put("probonmull", HyperGeoCommands.probOnMullToX);
		commandMap.put("probonmullhelp", HyperGeoCommands.probOnMullToXHelp);
		
		commandMap.put("probonmulltox", HyperGeoCommands.probOnMullToX);
		commandMap.put("probonmulltoxhelp", HyperGeoCommands.probOnMullToXHelp);
		
		commandMap.put("probabilityonmull", HyperGeoCommands.probOnMullToX);
		commandMap.put("probabilityonmullhelp", HyperGeoCommands.probOnMullToXHelp);
		
		commandMap.put("probabilityonmulltox", HyperGeoCommands.probOnMullToX);
		commandMap.put("probabilityonmulltoxhelp", HyperGeoCommands.probOnMullToXHelp);
		
		// ***********************//
		// Graph Commands         //
		// ***********************//

		// Prob Chart (Normal Distribution Graph)
		commandMap.put("pc", GraphCommands.probChart);
		commandMap.put("pchelp", GraphCommands.probChartHelp);
		
		commandMap.put("probchart", GraphCommands.probChart);
		commandMap.put("probcharthelp", GraphCommands.probChartHelp);
		
		// Group Pie Chart
		commandMap.put("gpc", GraphCommands.groupPieChart);
		commandMap.put("gpchelp", GraphCommands.groupPieChartHelp);
		
		commandMap.put("grouppiechart", GraphCommands.groupPieChart);
		commandMap.put("grouppiecharthelp", GraphCommands.groupPieChartHelp);
		
		// ***********************//
		// Misc. Commands         //
		// ***********************//

		commandMap.put("snowflake", (event, args) -> {
			if (args == "") {
				BotUtils.sendMessage(event.getChannel(), BotUtils.codeBlock(event.getAuthor().getStringID()));
			}
			args = args.trim();
			IGuild g = event.getGuild();
			List<IUser> userList = g.getUsersByName(args.trim(), true);
			for (IUser u : userList) {
				BotUtils.sendMessage(event.getChannel(), "Snowflake ID for " + u.getDisplayName(g) + ": " + BotUtils.codeBlock(u.getStringID()));
			}
			
		});

		commandMap.put("profile", (event, args) -> {
			if (args == "") {
				BotUtils.sendMessage(event.getChannel(), "User avatar for: " + event.getAuthor().getDisplayName(event.getGuild()) + ":\n" + event.getAuthor().getAvatarURL());
				return;
			}
			IGuild g = event.getGuild();
			List<IUser> userList = g.getUsersByName(args.trim(), true);
			for (IUser u : userList) {
				BotUtils.sendMessage(event.getChannel(), "User avatar for: " + u.getDisplayName(g) + "\n" + u.getAvatarURL());
			}
		});

		System.out.println("Done loading commands.");
	}

}