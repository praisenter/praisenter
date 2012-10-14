package org.praisenter.data.song;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Xml reader for the Praisenter's song format.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PraisenterSongReader extends DefaultHandler {
	/**
	 * Returns a new list of songs from the given file.
	 * @param file the file to read from
	 * @return List&lt;Song&gt; the list of songs
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static List<Song> fromXml(File file) throws ParserConfigurationException, SAXException, IOException {
		return PraisenterSongReader.fromXml(new InputSource(new FileReader(file)));
	}
	
	/**
	 * Returns a new list of songs from the given string.
	 * @param xml the string containing the XML to read from
	 * @return List&lt;Song&gt; the list of songs
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static List<Song> fromXml(String xml) throws ParserConfigurationException, SAXException, IOException {
		return PraisenterSongReader.fromXml(new InputSource(new StringReader(xml)));
	}
	
	/**
	 * Returns a new list of songs from the given stream.
	 * @param stream the input stream containing the xml
	 * @return List&lt;Song&gt; the list of songs
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static List<Song> fromXml(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		return PraisenterSongReader.fromXml(new InputSource(stream));
	}
	
	/**
	 * Returns a new list of songs from the given input source.
	 * @param source the source containing the XML
	 * @return List&lt;Song&gt; the list of songs
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	private static List<Song> fromXml(InputSource source) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		PraisenterSongReader reader = new PraisenterSongReader();
		
		parser.parse(source, reader);
		
		return reader.songs;
	}
	
	/** The songs */
	private List<Song> songs;
	
	/** The song currently being processed */
	private Song song;
	
	/** The song part currently being processed */
	private SongPart part;
	
	/** Buffer for tag contents */
	private StringBuilder dataBuilder;
	
	/**
	 * Hidden constructor.
	 */
	private PraisenterSongReader() {
		this.songs = new ArrayList<Song>();
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// inspect the tag name
		if (qName.equalsIgnoreCase("Song")) {
			// when we see the <Songs> tag we create a new song
			this.song = new Song();
		} else if (qName.equalsIgnoreCase("SongPart")) {
			this.part = new SongPart();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// this method can be called a number of times for the contents of a tag
		// this is done to improve performance so we need to append the text before
		// using it
		String s = new String(ch, start, length);
		if (this.dataBuilder == null) {
			this.dataBuilder = new StringBuilder();
		}
		this.dataBuilder.append(s);
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("Song".equalsIgnoreCase(qName)) {
			// we are done with the song so add it to the list
			this.songs.add(this.song);
			this.song = null;
		} else if ("SongPart".equalsIgnoreCase(qName)) {
			this.song.parts.add(this.part);
			this.part = null;
		} else if ("Title".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				// set the song title
				this.song.setTitle(StringEscapeUtils.unescapeXml(this.dataBuilder.toString().trim()));
			}
		} else if ("Notes".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				// set the song title
				this.song.setNotes(StringEscapeUtils.unescapeXml(this.dataBuilder.toString().trim()));
			}
		} else if ("DateAdded".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				// set the song title
				try {
					this.song.setDateAdded(SongExporter.DATE_FORMAT.parse(this.dataBuilder.toString().trim()));
				} catch (ParseException e) {
					throw new SAXException("Unable to parse date format: ", e);
				}
			}
		} else if ("Type".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				// get the type
				String type = this.dataBuilder.toString().trim();
				this.part.setType(SongPartType.valueOf(type));
			}
		} else if ("Index".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				String data = this.dataBuilder.toString().trim();
				try {
					this.part.setIndex(Integer.parseInt(data));
				} catch (NumberFormatException e) {
					throw new SAXException("Unable to parse part index: ", e);
				}
			}
		} else if ("Order".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				String data = this.dataBuilder.toString().trim();
				try {
					this.part.setOrder(Integer.parseInt(data));
				} catch (NumberFormatException e) {
					throw new SAXException("Unable to parse part order: ", e);
				}
			}
		} else if ("Text".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				String data = this.dataBuilder.toString().trim();
				this.part.setText(StringEscapeUtils.unescapeXml(data));
			}
		} else if ("FontSize".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				String data = this.dataBuilder.toString().trim();
				try {
					this.part.setFontSize(Integer.parseInt(data));
				} catch (NumberFormatException e) {
					throw new SAXException("Unable to parse font size: ", e);
				}
			}
		}
		
		this.dataBuilder = null;
	}
}
