package bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class MainRunner {

	// Change this if the token is regenerated, or overwrite it with argument from
	// command line.
	private static String token = "NTMxNjY4ODQ3MzU1MzYzMzM4.XNNZng.rW_PubdWN77ILz1vQYzokFbmIu4";

	public static void main(String[] args) {

		if (args.length == 1) {
			token = args[0];
		}

		IDiscordClient cli = getBuiltDiscordClient(token);

		// Register a listener via the EventSubscriber annotation which allows for
		// organization and delegation of events
		cli.getDispatcher().registerListener(new CommandHandler());

		// Only login after all events are registered otherwise some may be missed.
		cli.login();

	}

	// Handles the creation and getting of a IDiscordClient object for a token
	static IDiscordClient getBuiltDiscordClient(String token) {

		// The ClientBuilder object is where you attach parameters for configuring this
		// instance of this Discord4J bot, such as withToken, setDaemon etc
		ClientBuilder rootwalla = new ClientBuilder();
		rootwalla.withToken(token);
		rootwalla.setMaxReconnectAttempts(Integer.MAX_VALUE);
		rootwalla.set5xxRetryCount(Integer.MAX_VALUE);
		return rootwalla.build();
	}

}
