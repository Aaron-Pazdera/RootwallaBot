# RootwallaBot
A Discord Bot for Magic the Gathering deckbuilding and statistical analysis.

## Overveiw
RootwallaBot allows you to very quickly calculate the probability of a hypergeometric event occurring during a game of magic. It also provides helpful charts for deckbuilding.

## Examples
This is a quick way to calculate the chances that, assuming you have 24 lands in your 60 card deck, you draw 2 to 4 of them in your 7-card opening hand.

![Image description](https://raw.githubusercontent.com/Aaron-Pazdera/RootwallaBot/master/Examples/RootwallaBot%20Prob%20Example.png)


![Image description](https://raw.githubusercontent.com/Aaron-Pazdera/RootwallaBot/master/Examples/RootwallaBot%20ProbChart%20Example.png)

## Commands
Type /commandlist to find the list of commands. Type **/\<commandname\>help** to learn how to use the command.

## Dependencies
RootwallaBot is a Java [Maven](https://maven.apache.org/) project with the following dependencies.

• [Discord4J](https://discord4j.com/)

• [JFreeChart](http://www.jfree.org/jfreechart/)

To automatically incorporate these into your Maven project, copy/paste the <repositories> and <dependencies> from my pom.xml into yours.


## TODO (In order of Priority)
• More statistics, expose univariate and multivariate variance and standard deviation methods

• More charts

• Make bot and docs easier to understand

• Reformat charts, add interval when a k range is specified

• Refactor chart library, remove unnecessary inheritance



## License
Released under the [MIT](https://opensource.org/licenses/MIT) license.
