# RootwallaBot
A Discord Bot for Magic the Gathering deckbuilding and statistical analysis.

## Commands

## Dependencies

-Discord4J
-JFreeChart
-XStream

## pom.xml 

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

		<dependency>
			<groupId>com.thoughtworks.xstream</groupId>
			<artifactId>xstream</artifactId>
			<version>1.4.11.1</version>
		</dependency>
	</dependencies>
```

## License
Released under the [MIT](https://opensource.org/licenses/MIT) license.
