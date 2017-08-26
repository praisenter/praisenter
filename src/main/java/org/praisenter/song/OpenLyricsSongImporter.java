/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.song;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.InvalidFormatException;
import org.praisenter.Tag;
import org.praisenter.utility.Streams;
import org.praisenter.utility.StringManipulator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX XML reader for the Praisenter's song format v1.0.0.
 * @author William Bittle
 * @version 3.0.0
 */
public final class OpenLyricsSongImporter implements SongImporter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/* (non-Javadoc)
	 * @see org.praisenter.song.SongImporter#execute(java.lang.String, java.io.InputStream)
	 */
	@Override
	public List<Song> execute(String fileName, InputStream stream) throws IOException, InvalidFormatException {
		try {
			return this.parse(stream);
		} catch (ParserConfigurationException | SAXException ex) {
			throw new InvalidFormatException("Failed reading file '" + fileName + "' as a ChurchView song file.", ex);
		}
	}
	
	/**
	 * Attempts to parse the given input stream.
	 * @param stream the stream
	 * @return List&lt;{@link Song}&gt;
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException if an IO error occurs
	 */
	private List<Song> parse(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		byte[] content = Streams.read(stream);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// prevent XXE attacks 
		// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		
		SAXParser parser = factory.newSAXParser();
		Praisenter1Handler handler = new Praisenter1Handler();
		parser.parse(new ByteArrayInputStream(content), handler);
		
		List<Song> songs = new ArrayList<Song>();
		songs.add(handler.song);
		return songs;
	}
	
	// SAX parser implementation
	
	/**
	 * SAX parse for the Praisenter 1.0.0 format.
	 * @author William Bittle
	 * @version 3.0.0
	 */
	private final class Praisenter1Handler extends DefaultHandler {
		/** The song currently being processed */
		private Song song;
		
		private Lyrics lyrics;
		
		private Author author;
		
		/** The verse currently being processed */
		private Verse verse;
		
		private boolean inVerse = false;
		
		private List<Songbook> songbooks;
		
		/** Buffer for tag contents */
		private StringBuilder dataBuilder;
		
		public Praisenter1Handler() {
			this.songbooks = new ArrayList<Songbook>();
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// inspect the tag name
			if (qName.equalsIgnoreCase("song")) {
				// when we see the <Songs> tag we create a new song
				this.song = new Song();
				
				String createdIn = attributes.getValue("createdIn");
				String modifiedIn = attributes.getValue("modifiedIn");
				String modifiedDate = attributes.getValue("modifiedDate");
				
				this.song.setSource(!StringManipulator.isNullOrEmpty(createdIn) ? createdIn : modifiedIn);
				if (!StringManipulator.isNullOrEmpty(modifiedDate)) {
					try {
						Calendar cal = DatatypeConverter.parseDateTime(modifiedDate);
						this.song.setModifiedDate(cal.getTime().toInstant());
					} catch (Exception ex) {
						LOGGER.warn("Failed to parse modifiedDate '" + modifiedDate + "' as a datetime.");
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
					this.song.lyrics.add(lyrics);
				}
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.setTransliteration(transliteration);
				if (!StringManipulator.isNullOrEmpty(original)) {
					try {
						lyrics.setOriginal(DatatypeConverter.parseBoolean(original));
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
					this.song.lyrics.add(lyrics);
				}
				
				this.author = new Author(type, null);
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.getAuthors().add(this.author);
			} else if (qName.equalsIgnoreCase("songbook")) {
				String name = attributes.getValue("name");
				String entry = attributes.getValue("entry");
				
				Songbook sb = new Songbook(name, entry);
				this.songbooks.add(sb);
			} else if (qName.equalsIgnoreCase("verse")) {
				String name = attributes.getValue("name");
				this.verse = new Verse();
				this.verse.setName(name);
				
				String language = attributes.getValue("lang");
				String transliteration = attributes.getValue("translit");
				
				Lyrics lyrics = this.song.getLyrics(language, transliteration);
				
				if (lyrics == null) {
					lyrics = new Lyrics();
					this.song.lyrics.add(lyrics);
				}
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.setTransliteration(transliteration);
				lyrics.getVerses().add(this.verse);
				
				this.inVerse = true;
			} else if (qName.equalsIgnoreCase("br")) {
				if (this.dataBuilder == null) {
					this.dataBuilder = new StringBuilder();
				}
				this.dataBuilder.append(Constants.NEW_LINE);
			} else if (qName.equalsIgnoreCase("comment") && this.inVerse) {
				// this begins a verse level comment
				if (this.dataBuilder != null) {
					String verse = this.verse.getText();
					String text = this.dataBuilder.toString();
					if (text != null) {
						if (verse == null) {
							verse = text;
						} else {
							verse += text;
						}
						this.verse.setText(verse);
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
					for (Songbook songbook : this.songbooks) {
						lyrics.getSongbooks().add(songbook.copy());
					}
				}
				// set primary lyrics
				Lyrics lyrics = this.song.getDefaultLyrics();
				this.song.setPrimaryLyrics(lyrics.id);
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
						try {
							this.song.setCcli(Integer.parseInt(text));
						} catch (Exception ex) {
							LOGGER.warn("Failed to parse ccliNo '" + text + "' as an integer.");
						}
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
						try {
							this.song.setTransposition(Integer.parseInt(text));
						} catch (Exception ex) {
							LOGGER.warn("Failed to parse transposition '" + text + "' as an integer.");
						}
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
			} else if (qName.equalsIgnoreCase("verseOrder")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						String[] order = text.split("\\s+");
						for (String item : order) {
							this.song.getSequence().add(item);
						}
					}
				}
			} else if (qName.equalsIgnoreCase("theme")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						this.song.getTags().add(new Tag(text));
					}
				}
			} else if (qName.equalsIgnoreCase("comment")) {
				if (this.dataBuilder != null) {
					String comments = this.song.getComments();
					String comment = this.dataBuilder.toString().trim();
					if (comments == null) {
						comments = comment;
					} else {
						comments = comments + Constants.NEW_LINE + comment;
					}
					this.song.setComments(comments);
					if (this.inVerse) {
						this.dataBuilder = null;
					}
				}
			} else if (qName.equalsIgnoreCase("verse")) {
				this.inVerse = false;
				this.verse = null;
			} else if (qName.equalsIgnoreCase("lines")) {
				if (this.dataBuilder != null) {
					String verse = this.verse.getText();
					String text = this.dataBuilder.toString();
					if (text != null) {
						if (verse == null) {
							verse = text;
						} else {
							verse += text;
						}
						this.verse.setText(verse);
					}
				}
			}
			
			if (this.verse == null) {
				this.dataBuilder = null;
			}
		}
	}
}
