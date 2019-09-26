package bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

	static final String BOT_PREFIX = "/";

	public static String formatFileName(String name) throws CommandFailureException {
		if (name.length() > 255) {
			throw new CommandFailureException("The deck's name is too long.");
		}
		if (name.matches("^.*[\\/:*?\".<>|].*$")) {
			throw new CommandFailureException("The deck's name cannot contain /\\:*?\".<>|");
		}
		return BotUtils.toTitleCase(name);
	}

	// A list of words for toTitleCase not to capitalize.
	private static ArrayList<String> TitleCaseLowerCaseWords = new ArrayList<String>();
	static {
		System.out.println("Loading title case excluded words.");
		TitleCaseLowerCaseWords.add("a");
		TitleCaseLowerCaseWords.add("an");
		TitleCaseLowerCaseWords.add("the");
		TitleCaseLowerCaseWords.add("for");
		TitleCaseLowerCaseWords.add("and");
		TitleCaseLowerCaseWords.add("nor");
		TitleCaseLowerCaseWords.add("but");
		TitleCaseLowerCaseWords.add("or");
		TitleCaseLowerCaseWords.add("yet");
		TitleCaseLowerCaseWords.add("so");
		TitleCaseLowerCaseWords.add("and");
		TitleCaseLowerCaseWords.add("as");
		TitleCaseLowerCaseWords.add("at");
		TitleCaseLowerCaseWords.add("but");
		TitleCaseLowerCaseWords.add("by");
		TitleCaseLowerCaseWords.add("for");
		TitleCaseLowerCaseWords.add("from");
		TitleCaseLowerCaseWords.add("in");
		TitleCaseLowerCaseWords.add("minus");
		TitleCaseLowerCaseWords.add("near");
		TitleCaseLowerCaseWords.add("of");
		TitleCaseLowerCaseWords.add("off");
		TitleCaseLowerCaseWords.add("on");
		TitleCaseLowerCaseWords.add("onto");
		TitleCaseLowerCaseWords.add("per");
		TitleCaseLowerCaseWords.add("plus");
		TitleCaseLowerCaseWords.add("than");
		TitleCaseLowerCaseWords.add("to");
		TitleCaseLowerCaseWords.add("under");
		TitleCaseLowerCaseWords.add("unlike");
		TitleCaseLowerCaseWords.add("until");
		TitleCaseLowerCaseWords.add("up");
		TitleCaseLowerCaseWords.add("upon");
		TitleCaseLowerCaseWords.add("versus");
		TitleCaseLowerCaseWords.add("via");
		TitleCaseLowerCaseWords.add("with");
		TitleCaseLowerCaseWords.add("within");
		TitleCaseLowerCaseWords.add("without");
		System.out.println("Done.");
	}

	public static void verifyFile(File file) throws CommandFailureException {
		if (!file.exists()) {
			throw new CommandFailureException("Something strange happened, and the chart could not be found.");
		}
		if (!file.canRead()) {
			throw new CommandFailureException(
					"Something strange happened, and the chart could be saved, but not read.");
		}
	}

	public static String codeBlock(String message) {
		return "```" + message + "```";
	}
	
	public static String formatPercent(Double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return (char) 8776 + " " + df.format(d * 100).toString() + "%";
	}

	public static String toTitleCase(String title) {

		// Split on spaces and underscore
		String[] words = title.split("\\s+|_");

		String newtitle = "";
		for (int i = 0; i < words.length; i++) {
			newtitle += capitalizeWord(words[i]);
		}
		return newtitle;
	}

	private static String capitalizeWord(String str) {

		if (TitleCaseLowerCaseWords.contains(str)) {
			return str.toLowerCase();
		}

		// Create a char array of given String
		char ch[] = str.toCharArray();

		for (int i = 0; i < str.length(); i++) {

			// If first character of a word is found
			if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') {

				// If it is in lower-case
				if (ch[i] >= 'a' && ch[i] <= 'z') {

					// Convert into Upper-case
					ch[i] = (char) (ch[i] - 'a' + 'A');
				}
			}

			// If apart from first character
			// Any one is in uppercase
			else if (ch[i] >= 'A' && ch[i] <= 'Z')

				// Convert into lowercase
				ch[i] = (char) (ch[i] + 'a' - 'A');

		}

		// Convert the char array to equivalent String
		String st = new String(ch);
		return st;
	}

	static void sendMessage(IChannel channel, String message) {

		// RequestBuffer is a utility class intended to deal with RateLimitExceptions by
		// queuing rate-limited operations until they can be completed.
		RequestBuffer.request(() -> {
			try {
				channel.sendMessage(message);
			} catch (DiscordException e) {
				System.out.println("Message could not be sent. Returned the error: ");
				e.printStackTrace();
			}
		});
	}

	static void sendFile(IChannel channel, File file) {
		RequestBuffer.request(() -> {
			try {
				channel.sendFile(file);
			} catch (DiscordException | FileNotFoundException e) {
				System.out.println("Message could not be sent. Returned the error: ");
				e.printStackTrace();
			}
		});

	}

	static void sendFileMessage(IChannel channel, String message, File file) {
		RequestBuffer.request(() -> {
			try {
				channel.sendFile(message, file);
			} catch (DiscordException | FileNotFoundException e) {
				System.out.println("Message could not be sent. Returned the error: ");
				e.printStackTrace();
			}
		});

	}
}
