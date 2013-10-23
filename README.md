#twitter-sentiment#

A small project to get the current "sentiment" on some companies based on recent tweets.

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

[Twitter Developers]: https://dev.twitter.com/apps
