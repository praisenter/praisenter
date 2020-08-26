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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.data.DataFormatProvider;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.InvalidFormatException;
import org.praisenter.data.Tag;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;
import org.praisenter.utility.StringManipulator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class OpenLyricsSongFormatProvider implements DataFormatProvider<Song> {
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
			    	if (r.getLocalName().equalsIgnoreCase("song")) {
			    		String ns = r.getNamespaceURI();
			    		if (ns != null && ns.toLowerCase().startsWith("http://openlyrics.info/")) {
			    			return true;
			    		}
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
			    	if (r.getLocalName().equalsIgnoreCase("song")) {
			    		String ns = r.getNamespaceURI();
			    		if (ns != null && ns.toLowerCase().startsWith("http://openlyrics.info/")) {
			    			return true;
			    		}
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
			results.add(this.parse(stream, name));
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
	 * @return {@link Song}
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private DataReadResult<Song> parse(InputStream stream, String name) throws ParserConfigurationException, SAXException, IOException {
		byte[] content = Streams.read(stream);
		// read the bytes
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// prevent XXE attacks 
		// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser parser = factory.newSAXParser();
		OpenSongHandler handler = new OpenSongHandler();
		parser.parse(new ByteArrayInputStream(content), handler);
		
		// set song name
		handler.song.setName(handler.song.getDefaultTitle());
		
		// clean up
		for (Lyrics lyrics : handler.song.getLyrics()) {
			for (Section section : lyrics.getSections()) {
				String cleansed = section.getText()
				.replaceAll("\\s*\\n+\\s*", "<br>")
				.replaceAll("\\s+", " ")
				.replaceAll("<br>", Constants.NEW_LINE);
				section.setText(cleansed.trim());
			}
		}
		
		return new DataReadResult<Song>(handler.song);
	}
	
	// SAX parser implementation
	
	/**
	 * SAX parse for the OpenSong format.
	 * @author William Bittle
	 * @version 3.0.0
	 */
	private final class OpenSongHandler extends DefaultHandler {
		private Song song;
		private Lyrics lyrics;
		private Author author;
		private Section section;
		
		private boolean inSection = false;
		
		private List<SongBook> songbooks;
		
		/** Buffer for tag contents */
		private StringBuilder dataBuilder;
		
		public OpenSongHandler() {
			this.songbooks = new ArrayList<SongBook>();
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// inspect the tag name
			if (qName.equalsIgnoreCase("song")) {
				// when we see the <song> tag we create a new song
				this.song = new Song();
				
				String createdIn = attributes.getValue("createdIn");
				String modifiedIn = attributes.getValue("modifiedIn");
				String modifiedDate = attributes.getValue("modifiedDate");
				
				this.song.setSource(!StringManipulator.isNullOrEmpty(createdIn) ? createdIn : modifiedIn);
				if (!StringManipulator.isNullOrEmpty(modifiedDate)) {
					try {
						this.song.setModifiedDate(Instant.parse(modifiedDate));
					} catch (Exception ex) {
						LOGGER.warn("Failed to parse modifiedDate '" + modifiedDate + "' as an Instant.");
						try {
							this.song.setModifiedDate(LocalDateTime.parse(modifiedDate).toInstant(ZoneOffset.of(ZoneOffset.systemDefault().getId())));
						} catch (Exception ex1) {
							LOGGER.warn("Failed to parse modifiedDate '" + modifiedDate + "' as a LocalDateTime.");
						}
					}
				}
			} else if (qName.equalsIgnoreCase("title")) {
				// get the language and transliteration attributes
				String language = attributes.getValue("lang");
				String transliteration = attributes.getValue("translit");
				String original = attributes.getValue("original");
				
				Lyrics lyrics = this.song.getLyrics(language, transliteration);
				
				if (lyrics == null) {
					lyrics = new Lyrics();
					this.song.getLyrics().add(lyrics);
				}
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.setTransliteration(transliteration);
				if (!StringManipulator.isNullOrEmpty(original)) {
					try {
						lyrics.setOriginal(Boolean.parseBoolean(original));
					} catch (Exception ex) {
						LOGGER.warn("Failed to parse original flag '" + original + "' as a boolean.");
					}
				}
			} else if (qName.equalsIgnoreCase("author")) {
				// get the type language
				String language = attributes.getValue("lang");
				String type = attributes.getValue("type");
				
				Lyrics lyrics = this.song.getLyrics(language, null);
				
				if (lyrics == null) {
					lyrics = new Lyrics();
					this.song.getLyrics().add(lyrics);
				}
				
				this.author = new Author(type, null);
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.getAuthors().add(this.author);
			} else if (qName.equalsIgnoreCase("songbook")) {
				String name = attributes.getValue("name");
				String entry = attributes.getValue("entry");
				
				SongBook sb = new SongBook(name, entry);
				this.songbooks.add(sb);
			} else if (qName.equalsIgnoreCase("verse")) {
				String name = attributes.getValue("name");
				this.section = new Section();
				this.section.setName(name);
				
				String language = attributes.getValue("lang");
				String transliteration = attributes.getValue("translit");
				
				Lyrics lyrics = this.song.getLyrics(language, transliteration);
				
				if (lyrics == null) {
					lyrics = new Lyrics();
					this.song.getLyrics().add(lyrics);
				}
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.setTransliteration(transliteration);
				lyrics.getSections().add(this.section);
				
				this.inSection = true;
			} else if (qName.equalsIgnoreCase("br")) {
				if (this.dataBuilder == null) {
					this.dataBuilder = new StringBuilder();
				}
				this.dataBuilder.append(Constants.NEW_LINE);
			} else if (qName.equalsIgnoreCase("comment") && this.inSection) {
				// this begins a verse level comment
				if (this.dataBuilder != null) {
					String verse = this.section.getText();
					String text = this.dataBuilder.toString();
					if (text != null) {
						if (verse == null) {
							verse = text;
						} else {
							verse += text;
						}
						this.section.setText(verse);
					}
					this.dataBuilder = null;
				}
			} else if (qName.equalsIgnoreCase("lines")) {
				this.dataBuilder = null;
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
			if ("song".equalsIgnoreCase(qName)) {
				// apply songbooks
				for (Lyrics lyrics : this.song.getLyrics()) {
					for (SongBook songbook : this.songbooks) {
						lyrics.getSongBooks().add(songbook.copy());
					}
				}
				// set primary lyrics
				Lyrics lyrics = this.song.getDefaultLyrics();
				this.song.setPrimaryLyrics(lyrics.getId());
			} else if ("title".equalsIgnoreCase(qName)) {
				if (this.dataBuilder != null) {
					this.lyrics.setTitle(this.dataBuilder.toString().trim());
				}
			} else if ("author".equalsIgnoreCase(qName)) {
				if (this.dataBuilder != null) {
					this.author.setName(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("copyright")) {
				if (this.dataBuilder != null) {
					this.song.setCopyright(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("ccliNo")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						this.song.setCCLINumber(text);
					}
				}
			} else if (qName.equalsIgnoreCase("released")) {
				if (this.dataBuilder != null) {
					this.song.setReleased(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("transposition")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						this.song.setTransposition(text);
					}
				}
			} else if (qName.equalsIgnoreCase("tempo")) {
				if (this.dataBuilder != null) {
					this.song.setTempo(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("key")) {
				if (this.dataBuilder != null) {
					this.song.setKey(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("variant")) {
				if (this.dataBuilder != null) {
					this.song.setVariant(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("publisher")) {
				if (this.dataBuilder != null) {
					this.song.setPublisher(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("keywords")) {
				if (this.dataBuilder != null) {
					this.song.setKeywords(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("theme")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						this.song.getTags().add(new Tag(text));
					}
				}
			} else if (qName.equalsIgnoreCase("comment") || qName.equalsIgnoreCase("verseOrder")) {
				if (this.dataBuilder != null) {
					String comments = this.song.getNotes();
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						if (comments == null) {
							comments = text;
						} else {
							comments = comments + Constants.NEW_LINE + text;
						}
						this.song.setNotes(comments);
					}
					if (this.inSection) {
						this.dataBuilder = null;
					}
				}
			} else if (qName.equalsIgnoreCase("verse")) {
				this.inSection = false;
				this.section = null;
			} else if (qName.equalsIgnoreCase("lines")) {
				if (this.dataBuilder != null) {
					String verse = this.section.getText();
					String text = this.dataBuilder.toString();
					if (text != null) {
						if (verse == null) {
							verse = text;
						} else {
							verse += text;
						}
						this.section.setText(verse);
					}
				}
			}
			
			if (this.section == null) {
				this.dataBuilder = null;
			}
		}
	}
}
