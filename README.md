#twitter-sentiment#

A small project to get the current "sentiment" on some companies based on recent tweets.

With maven installed then after you set up authentication (below) you should be able to execute it with:

> mvn exec:java

This will execute the main method from `com.porcoesphino.ts.SentimentServer`. This is intended to demonstrate the intended API. There is a gui avaliable for visual debugging / investigation of trends. This is run from `com.porcoesphino.ts.gui.SentimentViewer`

*NOTE*: If there is an issue finding java version 1.7 you may need to install it or ensure `JAVA_HOME` is set correctly.

## Authentication ##

Authentication is required for use of the 1.1 Twitter API.
This application uses application-only auth.
Please go to the [Twitter Developers][] site to get the following:

 - Consumer key
 - Consumer secret
 - Access token
 - Access token secret

Add them to the `twitter.properties` file.

You may need to execute `git update-index --assume-unchanged` to ensure this file isn't commited after doing this.

To disable logging, this line can be added to the above file `twitter4j.loggerFactory=twitter4j.internal.logging.NullLoggerFactory`

## Issues ##

 - Doesn't always reconnect if the connections goes down.
 - If there is an internal expception, twitter4J can get brought down
 - OAuth could be used for authentication

## TODO ##

 - Think about using [Gradle][]

[Twitter Developers]: https://dev.twitter.com/apps
[Gradle]: www.gradle.org
