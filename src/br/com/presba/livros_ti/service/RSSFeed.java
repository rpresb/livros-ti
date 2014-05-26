package br.com.presba.livros_ti.service;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.os.Bundle;
import br.com.presba.livros_ti.R;

/**
 * Tutorial: http://www.javaworld.com/article/2078529/java-android-developer/java-tip--set-up-an-rss-feed-for-your-android-application.html
 *  
 * @author denissys
 */
public class RSSFeed extends Activity {
	
	/** Called when the activity is first created. */
	String rssResult = "";
	boolean item = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//TextView rss = (TextView) findViewById(R.id.rss);
		try {
			URL rssUrl = new URL("http://it-ebooks-api.info/v1/search/php%20mysql/page/1");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			RSSHandler rssHandler = new RSSHandler();
			xmlReader.setContentHandler(rssHandler);
			InputSource inputSource = new InputSource(rssUrl.openStream());
			xmlReader.parse(inputSource);

		} catch (IOException e) {
//			rss.setText(e.getMessage());
		} catch (SAXException e) {
//			rss.setText(e.getMessage());
		} catch (ParserConfigurationException e) {
//			rss.setText(e.getMessage());
		}

//		rss.setText(rssResult);
	}

	/**
	 * public String removeSpaces(String s) { StringTokenizer st = new
	 * StringTokenizer(s," ",false); String t=""; while (st.hasMoreElements()) t
	 * += st.nextElement(); return t; }
	 */
	private class RSSHandler extends DefaultHandler {

		public void startElement(String uri, String localName, String qName,
				Attributes attrs) throws SAXException {
			if (localName.equals("item"))
				item = true;

			if (!localName.equals("item") && item == true)
				rssResult = rssResult + localName + ": ";

		}

		public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			String cdata = new String(ch, start, length);
			if (item == true) {
				rssResult = rssResult + (cdata.trim()).replaceAll("\\s+", " ") + "\t";
			}
		}

	}
}