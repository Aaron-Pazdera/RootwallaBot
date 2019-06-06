# RootwallaBot
A Discord Bot for Magic the Gathering deckbuilding and statistical analysis.

## Overveiw


## Commands

## Dependencies
RootwallaBot is a Java [Maven](https://maven.apache.org/) project with the following dependencies.

• [Discord4J](https://discord4j.com/)

• [JFreeChart](http://www.jfree.org/jfreechart/)

To automatically incorporate these into your Maven project, copy/paste the following into your pom.xml.

```
<repositories>
		<repository>
			<id>jcenter</id>
			<url>http://jcenter.bintray.com</url>
		</repository>
	</repositories>

	<dependencies>
		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter-engine</artifactId>
			<version>5.3.1</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>com.discord4j</groupId>
			<artifactId>Discord4J</artifactId>
			<version>2.10.1</version>
		</dependency>

		<dependency>
			<groupId>org.jfree</groupId>
			<artifactId>jfreechart</artifactId>
			<version>1.5.0</version>
		</dependency>

	</dependencies>
```

## License
Released under the [MIT](https://opensource.org/licenses/MIT) license.
