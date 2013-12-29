package com.porcoesphino.twitterSentiment;

import static org.junit.Assert.*;

import org.junit.Test;

import com.porcoesphino.twitterSentiment.TweetWindow.StatusAndMeta;

public class CompanyTweetParserTest {

	@Test
	public void splitsSpaces() {
		String[] splitWords = CompanyTweetParser.splitIntoWords("one two");
		assertTrue("Wrong number of words split", splitWords.length == 2);
		assertTrue("First word split is wrong", splitWords[0].contentEquals("one"));
		assertTrue("Second word split is wrong", splitWords[1].contentEquals("two"));
	}
	
	@Test
	public void ignoredHash() {
		String[] splitWords = CompanyTweetParser.splitIntoWords("#one #two");
		assertTrue("Wrong number of words with hash", splitWords.length == 2);
		assertTrue("First word split is wrong", splitWords[0].contentEquals("one"));
		assertTrue("Second word split is wrong", splitWords[1].contentEquals("two"));
	}
	
	@Test
	public void ignoredHashAndNames() {
		String[] splitWords = CompanyTweetParser.splitIntoWords("@one @two #three");
		assertTrue("Wrong number of words with hash", splitWords.length == 3);
		assertTrue("First word split is wrong", splitWords[0].contentEquals("one"));
		assertTrue("Second word split is wrong", splitWords[1].contentEquals("two"));
		assertTrue("Third word split is wrong", splitWords[2].contentEquals("three"));
	}
	
	@Test
	public void ignoresPartCompanyString() {
		String text = "Google  Is Inc. Dictating SERP (Search Engine Result Page) For Dollars ? http://t.co/BNbTLiXTbx #Startups";
		String[] words = CompanyTweetParser.splitIntoWords(text);
		CompanyTweetParser parser = new CompanyTweetParser("GOOG", "Google Inc.");
		assertFalse(parser.addIfForThisCompany(new StatusAndMeta(null, words)));
	}
	
	public void companyMatched(String ticker, String company, String text) {
		String[] words = CompanyTweetParser.splitIntoWords(text);
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
