package com.porcoesphino.twitterSentiment;

import com.porcoesphino.ts.SentimentServer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SentimentServerTest extends TestCase {
	
	public SentimentServerTest(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		return new TestSuite(SentimentServerTest.class);
	}

	public void testSAndPFileExists() {
		assertTrue(SentimentServer.companiesFilePath.toFile().exists());
	}
}
