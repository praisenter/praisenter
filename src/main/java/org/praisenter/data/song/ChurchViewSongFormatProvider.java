package org.praisenter.data.song;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataFormatProvider;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.InvalidFormatException;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class ChurchViewSongFormatProvider implements DataFormatProvider<Song> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** Regular expression pattern used to parse the song part name */
	private static final Pattern SONG_PART_PATTERN = Pattern.compile("(.*)(\\d+)", Pattern.CASE_INSENSITIVE);
	
	/** Regular expression pattern used to parse the song part font size */
	private static final Pattern SONG_PART_SIZE_PATTERN = Pattern.compile("([CVBTE])(\\d+)?Size", Pattern.CASE_INSENSITIVE);
//	
//	/** Scale factor for the font size */
//	private static final double FONT_SIZE_SCALE = 1.5;
//	
	@Override
	public boolean isSupported(Path path) {
		try (Reader reader = new BufferedReader(new FileReader(path.toFile()))) {
			XMLInputFactory f = XMLInputFactory.newInstance();
			// prevent XXE attacks
			// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLInputFactory_.28a_StAX_parser.29
			f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			XMLStreamReader r = f.createXMLStreamReader(reader);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().matches("_(CV).+(_SongsDataSet)")) {
			    		return true;
			    	}
			    }
			}
		} catch (Exception ex) {
			LOGGER.trace("Failed to read the path as an XML document.", ex);
		}
		return false;
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.XML.is(mimeType) || mimeType.toLowerCase().startsWith("text");
	}
	
	@Override
	public boolean isSupported(String resourceName, InputStream stream) {
		try {
			XMLInputFactory f = XMLInputFactory.newInstance();
			// prevent XXE attacks
			// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLInputFactory_.28a_StAX_parser.29
			f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			XMLStreamReader r = f.createXMLStreamReader(stream);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().matches("(CV).+(_SongsDataSet)")) {
			    		return true;
			    	}
			    }
			}
		} catch (Exception ex) {
			LOGGER.trace("Failed to read the input stream as an XML document.", ex);
		}
		return false;
	}
	
	@Override
	public List<DataReadResult<Song>> read(Path path) throws IOException {
		try (FileInputStream stream = new FileInputStream(path.toFile())) {
			return this.read(path.getFileName().toString(), stream);
		}
	}
	
	@Override
	public List<DataReadResult<Song>> read(String resourceName, InputStream stream) throws IOException {
		String name = resourceName;
		int i = resourceName.lastIndexOf('.');
		if (i >= 0) {
			name = resourceName.substring(0, i);
		}
		
		List<DataReadResult<Song>> results = new ArrayList<>();
		try {
			results.addAll(this.parse(stream, name));
		} catch (SAXException | ParserConfigurationException ex) {
			throw new InvalidFormatException(ex);
		}
		return results;
	}
	
	@Override
	public void write(OutputStream stream, Song song) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void write(Path path, Song song) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Attempts to parse the given input stream into the internal song format.
	 * @param stream the input stream
	 * @param name the name
	 * @throws IOException if an IO error occurs
	 * @throws InvalidFormatException if the stream was not in the expected format
	 * @return List
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private List<DataReadResult<Song>> parse(InputStream stream, String name) throws ParserConfigurationException, SAXException, IOException {
		byte[] content = Streams.read(stream);
		// read the bytes
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// prevent XXE attacks 
		// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser parser = factory.newSAXParser();
		ChurchViewHandler handler = new ChurchViewHandler();
		parser.parse(new ByteArrayInputStream(content), handler);
		return handler.songs.stream().map(s -> new DataReadResult<Song>(s)).collect(Collectors.toList());
	}
	
	// SAX parser implementation
	
	/**
	 * SAX parse for the ChurchView format.
	 * @author William Bittle
	 * @version 3.0.0
	 */
	private final class ChurchViewHandler extends DefaultHandler {
		/** The songs */
		private List<Song> songs;
		
		/** The song currently being processed */
		private Song song;
		
		/** The lyrics currently being processed */
		private Lyrics lyrics;
		
		/** Buffer for tag contents */
		private StringBuilder dataBuilder;
		
		/**
		 * Default constructor.
		 */
		public ChurchViewHandler() {
			this.songs = new ArrayList<Song>();
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// inspect the tag name
			if (qName.equalsIgnoreCase("Songs")) {
				// when we see the <Songs> tag we create a new song
				this.song = new Song();
				this.lyrics = new Lyrics();
				this.song.setPrimaryLyrics(this.lyrics.getId());
				this.song.getLyrics().add(this.lyrics);
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
			if ("Songs".equalsIgnoreCase(qName)) {
				// we are done with the song so add it to the list
				this.songs.add(this.song);
				this.song.setName(this.song.getDefaultTitle());
				this.song = null;
				this.lyrics = null;
			} else if ("SongTitle".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					// set the song title
					this.lyrics.setTitle(StringEscapeUtils.unescapeXml(this.dataBuilder.toString().trim()));
				}
			} else if ("Song".equalsIgnoreCase(qName) ||
					"Bridge".equalsIgnoreCase(qName) ||
					qName.startsWith("Verse") ||
					qName.startsWith("Chorus") ||
					"Tag".equalsIgnoreCase(qName) ||
					"Ending".equalsIgnoreCase(qName) ||
					"Vamp".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					// get the text
					String text = this.dataBuilder.toString().trim();
					// only add the part if its not empty
					if (!text.isEmpty()) {
						Section section = new Section();
						
						// set the type
						String type = this.getType(qName);
						int number = 1;
						
						// set the number
						if (qName.startsWith("Verse") || qName.startsWith("Chorus")) {
							Matcher matcher = SONG_PART_PATTERN.matcher(qName);
							if (matcher.matches()) {
								String n = matcher.group(2);
								try {
									number = Integer.parseInt(n);
								} catch (NumberFormatException e) {
									LOGGER.warn("Failed to read verse part number: {}", n);
								}
							} else {
								LOGGER.warn("Failed to read verse part number from: {}", qName);
							}
						}
						
						section.setName(type, number, null);
						
						// set the text
						section.setText(text);
						
						this.lyrics.getSections().add(section);
					}
				}
			} else if ("cDate".equalsIgnoreCase(qName)) {
				// ignore, use today's date
			} else if ("NOTES".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					String data = this.dataBuilder.toString().trim();
					this.song.setNotes(data);
				}
			} 
//			else if (qName.contains("Size")) {
//				// make sure the tag was not self terminating
//				if (this.dataBuilder != null) {
//					Section section = this.getVerseForSize(qName);
//					if (section != null) {
//						// interpret the size
//						String s = this.dataBuilder.toString().trim();
//						try {
//							int size = (int)Math.floor(Integer.parseInt(s) * FONT_SIZE_SCALE);
//							section.setFontSize(size);
//						} catch (NumberFormatException e) {
//							LOGGER.warn("Failed to read verse font size: {}", s);
//						}
//					}
//				}
//			}
			
			this.dataBuilder = null;
		}
		
		/**
		 * Returns the type of verse based on the tag name.
		 * @param name the tag name
		 * @return String
		 */
		private final String getType(String name) {
			if (name.startsWith("Verse")) {
				return "v";
			} else if (name.startsWith("Chorus") || name.equalsIgnoreCase("Song")) {
				return "c";
			} else if (name.equalsIgnoreCase("Bridge")) {
				return "b";
			} else if (name.equalsIgnoreCase("Ending")) {
				return "e";
			} else if (name.equalsIgnoreCase("Tag")) {
				return "t";
			} else if (name.equalsIgnoreCase("Vamp")) {
				return "e";
			} else {
				return "o";
			}
		}
		
		/**
		 * Returns the verse for the given font size tag name.
		 * @param name the font size tag name
		 * @return List&lt;{@link Verse}&gt;
		 */
		private final Section getVerseForSize(String name) {
			if ("FontSize".equalsIgnoreCase(name)) {
				return null;
			}
			Matcher matcher = SONG_PART_SIZE_PATTERN.matcher(name);
			if (matcher.matches()) {
				String t = matcher.group(1);
				String i = matcher.group(2);
				if (i == null) {
					// then its a vamp, bridge, tag or end size
					if ("V".equalsIgnoreCase(t)) {
						return this.lyrics.getSectionByName("v1");
					} else if ("B".equalsIgnoreCase(t)) {
						return this.lyrics.getSectionByName("b1");
					} else if ("T".equalsIgnoreCase(t)) {
						return this.lyrics.getSectionByName("t1");
					} else if ("E".equalsIgnoreCase(t)) {
						return this.lyrics.getSectionByName("e1");
					} else {
						return null;
					}
				} else {
					int index = 0;
					try {
						index = Integer.parseInt(i);
					} catch (NumberFormatException e) {
						LOGGER.warn("Failed to read verse part number: {}", i);
						return null;
					}
					// then its a chorus or verse tag
					if ("C".equalsIgnoreCase(t)) {
						// chorus
						return this.lyrics.getSectionByName("c" + index);
					} else if ("V".equalsIgnoreCase(t)) {
						// verse
						return this.lyrics.getSectionByName("v" + index);
					} else {
						return null;
					}
				}
			}
			
			return null;
		}
	}
}
