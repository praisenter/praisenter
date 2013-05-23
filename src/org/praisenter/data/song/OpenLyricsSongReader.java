/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.data.song;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX XML reader for the OpenLyrics (http://openlyrics.info/) format.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public class OpenLyricsSongReader extends DefaultHandler {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(OpenLyricsSongReader.class);
	
	/** The verse name pattern */
	private static final Pattern VERSE_NAME_PATTERN = Pattern.compile("([vVcCpPbBeE])(\\d+)([a-zA-Z]+)?");
	
	/**
	 * Returns a new list of songs from the given file.
	 * @param file the file to read from
	 * @return List&lt;Song&gt; the list of songs
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static List<Song> fromXml(File file) throws ParserConfigurationException, SAXException, IOException {
		return OpenLyricsSongReader.fromXml(new InputSource(new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))));
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
		return OpenLyricsSongReader.fromXml(new InputSource(new StringReader(xml)));
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
		return OpenLyricsSongReader.fromXml(new InputSource(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
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
		
		OpenLyricsSongReader reader = new OpenLyricsSongReader();
		
		parser.parse(source, reader);
		
		List<Song> songs = new ArrayList<Song>();
		songs.add(reader.song);
		
		return songs;
	}
	
	/** The song currently being processed */
	private Song song = new Song();
	
	/** Buffer for tag contents */
	private StringBuilder dataBuilder;
	
	/** The attributes from the start element */
	private Attributes attributes;
	
	// temp
	
	/** 
	 * Indicates the current match of title; the higher 
	 * the value the better the match we have found 
	 */
	private int titleMatchIndicator = -1;
	
	/**
	 * The song variant.  Typically this will be a string that tells
	 * the user if this song is a variant of the original.
	 */
	private String variant = null;
	
	/** The current song part. */
	private SongPart part = null;
	
	/** True if the next append of text to a song part requires a new line */
	private boolean newPartLines = false;
	
	/**
	 * Hidden constructor.
	 */
	private OpenLyricsSongReader() {}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.attributes = attributes;
		if ("verse".equals(qName)) {
			// get the name attribute
			String name = attributes.getValue("name");
			// get the type and index of the part
			SongPartType type = SongPartType.OTHER;
			if (name.startsWith("v")) {
				type = SongPartType.VERSE;
			} else if (name.startsWith("c")) {
				type = SongPartType.CHORUS;
			} else if (name.startsWith("p")) {
				type = SongPartType.PRECHORUS;
			} else if (name.startsWith("b")) {
				type = SongPartType.BRIDGE;
			} else if (name.startsWith("e")) {
				type = SongPartType.END;
			}
			
			Matcher matcher = VERSE_NAME_PATTERN.matcher(name);
			
			SongPart part = null;
			// check for parts that have just the type
			if (name.length() == 1) {
				// check if the index 1 verse already exists
				// this can happen with transliteration and 
				// other languages (in this case the text will
				// just be appended to the text of the verse)
				part = this.song.getSongPart(type, 1);
				if (part == null) {
					part = this.song.addSongPart(new SongPart(type, 1, ""));
				}
				// set the current song part
				this.part = part;
			// check for parts that have a type and sub part
			// or just type and index
			} else if (matcher.matches()) {
				// parts of the format [type][index][subpart]
				// will be merged into one part of the shared index
				try {
					// get the index
					int index = Integer.parseInt(matcher.group(2));
					// get the part
					part = this.song.getSongPart(type, index);
					// check for null
					if (part == null) {
						// create it
						part = this.song.addSongPart(new SongPart(type, index, ""));
					}
					// set the current song part
					this.part = part;
				} catch (NumberFormatException ex) {
					throw new SAXException("Unable to parse song part index: " + name, ex);
				}
			}
		} else if ("lines".equals(qName)) {
			// flag that its a new verse requiring a new line if there is existing text
			this.newPartLines = true;
		} else if ("comment".equals(qName) && this.part != null) {
			// write the current text in the databuilder to the text of the part
			if (this.dataBuilder != null) {
				String text = this.part.getText();
				String lines = this.dataBuilder.toString().trim();
				// append the text to the part
				this.part.setText(text + (!text.isEmpty() && this.newPartLines ? "\n" : "") + lines);
				// clear the data builder
				this.dataBuilder = null;
				this.newPartLines = false;
			}
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
		this.dataBuilder.append(s.trim());
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("title".equalsIgnoreCase(qName)) {
			String lv = this.attributes.getValue("lang");
			String ov = this.attributes.getValue("original");
			boolean isOriginal = false;
			if (ov != null) {
				isOriginal = Boolean.parseBoolean(ov);
			}
			// if the title is the original use that
			String title = this.dataBuilder.toString().trim();
			if (isOriginal && this.titleMatchIndicator < 0) {
				this.song.setTitle(title);
				this.titleMatchIndicator = 0;
			} else {
				int result = this.compareLocaleLanguage(lv);
				// favor the closest match
				if (result > this.titleMatchIndicator) {
					this.song.setTitle(title);
					this.titleMatchIndicator = result;
				} else if (song.getTitle().isEmpty()) {
					// then assign it anyway
					this.song.setTitle(title);
				}
			}
		} else if ("variant".equalsIgnoreCase(qName)) {
			// store the variant for later use since we can't guarentee when the
			// variant tag will come
			this.variant = this.dataBuilder.toString().trim();
		} else if ("comment".equalsIgnoreCase(qName)) {
			// only store the song comments
			if (this.part == null) {
				// append the comments to the notes
				String notes = this.song.getNotes();
				this.song.setNotes(notes + (notes.length() > 0 ? " " : "") + this.dataBuilder.toString().trim());
			} else {
				// clear the databuilder (this effectively strips out
				// the <comment> self tags and adds a space character
				// to the text)
				this.dataBuilder = new StringBuilder();
				this.dataBuilder.append(" ");
				return;
			}
		} else if ("chord".equalsIgnoreCase(qName)) {
			// don't clear the databuilder (this effectively strips out
			// the <chord/> self terminating tags and adds a space character
			// to the text)
			this.dataBuilder.append(" ");
			return;
		} else if ("br".equalsIgnoreCase(qName)) {
			// don't clear the databuilder (this effectively strips out
			// the <br/> self terminating tags and adds a new line character
			// to the text string
			this.dataBuilder.append("\n");
			return;
		} else if ("song".equalsIgnoreCase(qName)) {
			// when the song ends append the variant
			if (this.variant != null && this.variant.length() > 0) {
				this.song.setTitle(this.song.getTitle() + " (" + this.variant + ")");
			}
		} else if ("lines".equals(qName)) {
			if (this.part != null) {
				if (this.dataBuilder != null) {
					String text = this.part.getText();
					String lines = this.dataBuilder.toString();
					// append the text to the part
					this.part.setText(text + (!text.isEmpty() && this.newPartLines ? "\n" : "") + lines);
				}
				this.newPartLines = false;
			} else {
				// log that some lines were lost
				LOGGER.warn("Lines were dropped due to null part: " + this.dataBuilder.toString());
			}
		} else if ("verse".equals(qName)) {
			this.part = null;
		}
		
		this.dataBuilder = null;
	}
	
	/**
	 * Returns -1 if the given language does not match the locale. 
	 * Returns 1 if the given language matches only the language of the locale.
	 * Returns 2 if the given language matches both the language and country of the locale.
	 * <p>
	 * Zero represents the original song title.
	 * @param lang the language from the xml
	 * @return int
	 */
	private int compareLocaleLanguage(String lang) {
		if (lang == null) return -1;
		lang = lang.trim();
		Locale locale = Locale.getDefault();
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String combo = language + "-" + country;
		if (lang.equalsIgnoreCase(language)) {
			return 0;
		}
		if (lang.equalsIgnoreCase(combo)) {
			return 1;
		}
		return -1;
	}
}
