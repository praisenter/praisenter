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

final class Praisenter1SongFormatProvider implements DataFormatProvider<Song> {
	private static final Logger LOGGER = LogManager.getLogger();
	
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
			    	if (r.getLocalName().equalsIgnoreCase("songs")) {
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
			    	if (r.getLocalName().equalsIgnoreCase("songs")) {
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
		Praisenter1Handler handler = new Praisenter1Handler();
		parser.parse(new ByteArrayInputStream(content), handler);
		return handler.songs.stream().map(s -> new DataReadResult<Song>(s)).collect(Collectors.toList());
	}
	
	// SAX parser implementation
	
	/**
	 * SAX parse for the Praisenter 1.0.0 format.
	 * @author William Bittle
	 * @version 3.0.0
	 */
	private final class Praisenter1Handler extends DefaultHandler {
		/** The songs */
		private List<Song> songs;
		
		/** The song currently being processed */
		private Song song;
	
		/** The lyrics currently being processed */
		private Lyrics lyrics;
		
		/** The verse currently being processed */
		private Section verse;
		
		/** Buffer for tag contents */
		private StringBuilder dataBuilder;
		
		/**
		 * Default constructor.
		 */
		public Praisenter1Handler() {
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
				this.lyrics = new Lyrics();
				this.song.setPrimaryLyrics(this.lyrics.getId());
				this.song.getLyrics().add(lyrics);
			} else if (qName.equalsIgnoreCase("SongPart")) {
				this.verse = new Section();
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
				this.song.setName(this.song.getDefaultTitle());
				this.song = null;
				this.lyrics = null;
			} else if ("SongPart".equalsIgnoreCase(qName)) {
				this.lyrics.getSections().add(this.verse);
				this.verse = null;
			} else if ("Title".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					// set the song title
					this.lyrics.setTitle(StringEscapeUtils.unescapeXml(this.dataBuilder.toString().trim()));
				}
			} else if ("Notes".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					this.song.setNotes(StringEscapeUtils.unescapeXml(this.dataBuilder.toString().trim()));
				}
			} else if ("DateAdded".equalsIgnoreCase(qName)) {
				// ignore
			} else if ("Type".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					// get the type
					String type = this.dataBuilder.toString().trim();
					this.verse.setName(getType(type));
				}
			} else if ("Index".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					String data = this.dataBuilder.toString().trim();
					try {
						this.verse.setName(this.verse.getName() + Integer.parseInt(data));
					} catch (NumberFormatException e) {
						LOGGER.warn("Failed to read verse number: {}", data);
					}
				}
			} else if ("Text".equalsIgnoreCase(qName)) {
				// make sure the tag was not self terminating
				if (this.dataBuilder != null) {
					String data = StringEscapeUtils.unescapeXml(this.dataBuilder.toString().trim());
					
					// set the text
					verse.setText(data);
				}
			} 
//			else if ("FontSize".equalsIgnoreCase(qName)) {
//				// make sure the tag was not self terminating
//				if (this.dataBuilder != null) {
//					String data = this.dataBuilder.toString().trim();
//					try {
//						int size = Integer.parseInt(data);
//						this.verse.setFontSize(size);
//					} catch (NumberFormatException e) {
//						LOGGER.warn("Failed to read verse font size: {}", data);
//					}
//				}
//			}
			
			this.dataBuilder = null;
		}
	
		/**
		 * Returns the type string for the given type.
		 * @param type the type
		 * @return String
		 */
		private final String getType(String type) {
			if ("VERSE".equals(type)) {
				return "v";
			} else if ("PRECHORUS".equals(type)) {
				return "p";
			} else if ("CHORUS".equals(type)) {
				return "c";
			} else if ("BRIDGE".equals(type)) {
				return "b";
			} else if ("TAG".equals(type)) {
				return "t";
			} else if ("VAMP".equals(type)) {
				return "e";
			} else if ("END".equals(type)) {
				return "e";
			} else {
				return "o";
			}
		}
	}
}
