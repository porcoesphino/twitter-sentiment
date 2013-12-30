package com.porcoesphino.twitterSentiment;

import static org.junit.Assert.*;

import org.junit.Test;

import com.porcoesphino.twitterSentiment.TweetWindow.StatusAndMeta;

public class CompanyTweetParserTest {

	@Test
	public void splitsSpaces() {
		String[] splitWords = CompanyTweetParser.splitTweetIntoWords("one two");
		assertTrue("Wrong number of words split", splitWords.length == 2);
		assertTrue("First word split is wrong", splitWords[0].contentEquals("one"));
		assertTrue("Second word split is wrong", splitWords[1].contentEquals("two"));
	}
	
	@Test
	public void ignoredHash() {
		String[] splitWords = CompanyTweetParser.splitTweetIntoWords("#one #two");
		assertTrue("Wrong number of words with hash", splitWords.length == 2);
		assertTrue("First word split is wrong", splitWords[0].contentEquals("one"));
		assertTrue("Second word split is wrong", splitWords[1].contentEquals("two"));
	}
	
	@Test
	public void ignoredHashAndNames() {
		String[] splitWords = CompanyTweetParser.splitTweetIntoWords("@one @two #three");
		assertTrue("Wrong number of words with hash", splitWords.length == 3);
		assertTrue("First word split is wrong", splitWords[0].contentEquals("one"));
		assertTrue("Second word split is wrong", splitWords[1].contentEquals("two"));
		assertTrue("Third word split is wrong", splitWords[2].contentEquals("three"));
	}
	
	private void AssertStringArraysEqual(String msg, String[] actual, String[] target) {
		assertEquals(msg + ": lengths didn't match", actual.length, target.length);
		for (int i=0; i<actual.length; i++) {
			assertTrue(msg, actual[i].contentEquals(target[i]));
		}
	}
	
	@Test
	public void removesExpectedFromAscii() {
		String[] splitWords = CompanyTweetParser.splitTweetIntoWords(" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~");
		String[] expected = new String[] {"'", "0123456789", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz" };
		AssertStringArraysEqual("", splitWords, expected);
	}
	
	@Test
	public void keepsAccents() {
		String[] splitWords = CompanyTweetParser
				.splitTweetIntoWords(
				"Les courbes rêvées");
		String[] result = new String[] {
			"Les", "courbes", "rêvées"
		};
		AssertStringArraysEqual("", splitWords, result);
	}
	
	@Test
	public void keepsApostrophes() {
		String[] splitWords = CompanyTweetParser
				.splitTweetIntoWords(
				"d'une TV Apple");
		String[] result = new String[] {
			"d'une", "TV", "Apple"
		};
		AssertStringArraysEqual("", splitWords, result);
	}
	
	@Test
	public void removesUrls1() {
		String[] splitWords = CompanyTweetParser
				.splitTweetIntoWords(
				"TV Apple http://t.co/XvhnpGY4Ua");
		String[] result = new String[] {
			"TV", "Apple"
		};
		AssertStringArraysEqual("", splitWords, result);
	}
	
	@Test
	public void removesUrls2() {
		String[] splitWords = CompanyTweetParser
				.splitTweetIntoWords(
				"appletechspot http://t.co/tNLm8fDOU0 Tongbu");
		String[] result = new String[] {
			"appletechspot", "Tongbu"
		};
		AssertStringArraysEqual("", splitWords, result);
	}
	
	@Test
	public void ignoresPartCompanyString() {
		String text = "Google  Is Inc. Dictating SERP (Search Engine Result Page) For Dollars ? http://t.co/BNbTLiXTbx #Startups";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		CompanyTweetParser parser = new CompanyTweetParser("GOOG", "Google Inc.");
		assertFalse(parser.addIfForThisCompany(new StatusAndMeta(null, words)));
	}
	
	@Test
	public void noNullIfWebsiteStarts() {
		String text = "http://t.co/gkqYXLyG50 Apple MacBook Pro";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		String[] wanted = new String[] {"Apple", "MacBook", "Pro"};
		AssertStringArraysEqual("", words, wanted);	
	}
	
	@Test
	public void nothingIfOnlyWebsite() {
		String text = " http://t.co/gkqYXLyG50 ";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		String[] wanted = new String[] {""};
		AssertStringArraysEqual("", words, wanted);	
	}
	
	@Test
	public void doesntMatchAmazonDotCom() {
		String text = "http://t.co/gkqYXLyG50 Apple MacBook Pro MD101LL/A 13.3-Inch Laptop $980 Apple MacBook Pro MD101LL/A 13.3-Inch Laptop $980 , Dec 29";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		CompanyTweetParser parser = new CompanyTweetParser("AMZN", "Amazon.com");
		assertFalse(parser.addIfForThisCompany(new StatusAndMeta(null, words)));
	}
	
	@Test
	public void doesntMatchAmazonDotCom2() {
		String text = "http://t.co/nIwjKiPYwM";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		CompanyTweetParser parser = new CompanyTweetParser("AMZN", "Amazon.com");
		assertFalse(parser.addIfForThisCompany(new StatusAndMeta(null, words)));
	} 
	
	@Test
	public void removesHearts() {
		String text = "looking for your opinion... ♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		String[] wanted = new String[] {"looking", "for", "your", "opinion"};
		AssertStringArraysEqual("", words, wanted);
	} 
	
	@Test
	public void doesntMatchEmoticons() {
		String text = "like and share 👊✌ Yeah!️";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		String[] wanted = new String[] {"like", "and", "share", "Yeah"};
		AssertStringArraysEqual("", words, wanted);
	}
	
	@Test
	public void noTrailingNull() {
		String text = "Yeah!️";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		String[] wanted = new String[] {"Yeah"};
		AssertStringArraysEqual("", words, wanted);
	}
	
	@Test
	public void splitsAsianWhitespace() {
		String text = "一枚くらい、自分の彼女の写真撮っときたいな～って。　（椿明）　「謎の彼女X」　️";
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		String[] wanted = new String[] {"一枚くらい","自分の彼女の写真撮っときたいな","って", "椿明", "謎の彼女X"};
		AssertStringArraysEqual("", words, wanted);
	}
	
	public void companyMatched(String ticker, String company, String text) {
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		CompanyTweetParser parser = new CompanyTweetParser(ticker, company);
		assertTrue(company + " (" + ticker + ") didn't match: " + text,
				parser.addIfForThisCompany(new StatusAndMeta(null, words)));
	}
	
	@Test
	public void findsCompanyString() {
		companyMatched("GOOG", "Google Inc.", 
		    "#Bangalore  Is Google Inc. Dictating SERP (Search Engine Result Page) For Dollars ? http://t.co/BNbTLiXTbx #Startups");
	}
	
	@Test
	public void findsAfterPartCompanyString() {
		companyMatched("GOOG", "Google Inc.",
		    "Google  Is Google Inc. Dictating SERP (Search Engine Result Page) For Dollars ? http://t.co/BNbTLiXTbx #Startups");
	}
	
	@Test
	public void findsTickerString() {
		companyMatched("GOOG", "Google Inc.",
		    "Is $GOOG Dictating SERP (Search Engine Result Page) For Dollars ? http://t.co/BNbTLiXTbx #Startups");
	}
	
	@Test
	public void findsIfBrackets() {
		companyMatched("MSFT", "Microsoft Corp.",
		    "Microsoft Corporation Announces Global Xbox One Sales (MSFT)");
	}
	
	@Test
	public void findsIfExclamationMark() {
		companyMatched("MSFT", "Microsoft Corp.",
		    "I've been looking forward to this day for 3 months... Thanks, $MSFT!");
	}
	
	@Test
	public void findsIfQuestionMark() {
		companyMatched("AAPL", "Apple Inc.",
		    "кстати, все уже сбросили акции AAPL?");
	}
	
	@Test
	public void findsWithColon() {
		companyMatched("GOOG", "Google Inc.",
		    "(NASDAQ:GOOG) Increased Spending; EU Court Rejects Cisco ...");
	}
	
	@Test
	public void findsWithBangComma() {
		companyMatched("GOOG", "Google Inc.",
		    "#DAX,#DOW,#FB,#GOOG,#TSLA,#YHOO,#Nokia Nokia und Google mit neuen Perspektiven: Eine Weihnachtsrally sieht irg... http://t.co/ytFLJlmV7h");
	}
	
	@Test
	public void findsWithComma() {
		companyMatched("AAPL", "Apple Inc.",
		    "What i see  all Market segments all red ! AAPL,IBM Short EA short IBM is here  my Favoriten");
	}
	
	@Test
	public void findsWithPeriod() {
		companyMatched("GOOG", "Google Inc.", 
		    "Nuevo Gmail 4.7. Ahora podemos adjuntar cualquier tipo de archivo, respuesta automática, Goog... http://t.co/5pQyGiqvnO");
	}	
	
	// TODO: Add a test for the likes of this:
	// After napping, it is goog time for ice chocolate :)
}
