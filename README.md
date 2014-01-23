#twitter-sentiment#

A small project to get the current "sentiment" on some companies based on recent tweets.

With maven installed then after you set up authentication (below) you should be able to execute it with:

> mvn exec:java

If there is an issue finding java version 1.7 you may need to install it or ensure `JAVA_HOME` is set correctly.

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
 - It seems like the swing GUI might be leaking String[] and char[]
 - These methods could maybe be optimised:
    - com.porcoesphino.ts.KeyTally.getNTallySets(int, java.util.Set)	0.06730983	2935 ms (0.1%)	28136
    - com.porcoesphino.ts.CompanyTweetParser.removeWebsite(String)	0.05617881	2449 ms (0.1%)	29248
    - com.porcoesphino.ts.gui.CompaniesSentimentTableModel.updateCounters()	0.053804364	2346 ms (0.1%)	17298
    - com.porcoesphino.ts.KeyTally.incrementKey(Object, int)	0.05278458	2301 ms (0.1%)	350617
    - com.porcoesphino.ts.gui.CompaniesSentimentTableModel$WordUpdaterSwingWorker.doInBackground()	0.04679532	2040 ms (0%)	7034
    - com.porcoesphino.ts.CompanyTweetParser.trim(String)	0.034342755	1497 ms (0%)	29248
    - com.porcoesphino.ts.CompanyTweetParser.splitOnWhiteSpaceAndSymbols(String)	0.030377926	1324 ms (0%)	29248

[Twitter Developers]: https://dev.twitter.com/apps
