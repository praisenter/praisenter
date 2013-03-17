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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX XML reader for the ChurchView program's song export.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChurchViewSongReader extends DefaultHandler {
	/** Regular expression pattern used to clean the date for reading */
	private static final Pattern DATE_PATTERN = Pattern.compile("(.*)-(\\d{2}):(\\d{2})$", Pattern.CASE_INSENSITIVE);
	
	/** Regular expression pattern used to parse the song part name */
	private static final Pattern SONG_PART_PATTERN = Pattern.compile("(.*)(\\d+)", Pattern.CASE_INSENSITIVE);
	
	/** Regular expression pattern used to parse the song part font size */
	private static final Pattern SONG_PART_SIZE_PATTERN = Pattern.compile("([CVBTE])(\\d+)?Size", Pattern.CASE_INSENSITIVE);
	
	/** Date format to parse the dates */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	/** Scale factor for the font size */
	private static final double FONT_SIZE_SCALE = 1.5;
	
	/**
	 * Returns a new list of songs from the given file.
	 * @param file the file to read from
	 * @return List&lt;Song&gt; the list of songs
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static List<Song> fromXml(File file) throws ParserConfigurationException, SAXException, IOException {
		return ChurchViewSongReader.fromXml(new InputSource(new FileReader(file)));
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
		return ChurchViewSongReader.fromXml(new InputSource(new StringReader(xml)));
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
		return ChurchViewSongReader.fromXml(new InputSource(stream));
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
		
		ChurchViewSongReader reader = new ChurchViewSongReader();
		
		parser.parse(source, reader);
		
		return reader.songs;
	}
	
	/** The songs */
	private List<Song> songs;
	
	/** The song currently being processed */
	private Song song;
	
	/** Buffer for tag contents */
	private StringBuilder dataBuilder;
	
	/**
	 * Hidden constructor.
	 */
	private ChurchViewSongReader() {
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
			this.song = null;
		} else if ("SongTitle".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				// set the song title
				this.song.setTitle(this.dataBuilder.toString().trim());
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
					SongPartType type = this.getPartType(qName);
					SongPart sp = new SongPart();
					sp.setType(type);
					sp.setText(text);
					sp.setOrder(1);
					if (qName.startsWith("Verse") || qName.startsWith("Chorus")) {
						Matcher matcher = SONG_PART_PATTERN.matcher(qName);
						if (matcher.matches()) {
							String n = matcher.group(2);
							try {
								int index = Integer.parseInt(n);
								sp.setIndex(index);
							} catch (NumberFormatException e) {
								throw new SAXException("Unable to parse the song part index: '" + n + "' as an integer.");
							}
						} else {
							throw new SAXException("Failed to match the song part: '" + qName + "'.");
						}
					}
					this.song.getParts().add(sp);
				}
			}
		} else if ("cDate".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				String data = this.dataBuilder.toString().trim();
				// attempt to fix the time zone
				Matcher matcher = DATE_PATTERN.matcher(data);
				if (matcher.matches()) {
					data = matcher.group(1) + "-" + matcher.group(2) + matcher.group(3);
				}
				// attempt to parse the string
				try {
					Date date = DATE_FORMAT.parse(data);
					this.song.setDateAdded(date);
				} catch (ParseException e) {
					throw new SAXException("Unable to parse the added date: '" + data + "' as a date.", e);
				}
			}
		} else if ("NOTES".equalsIgnoreCase(qName)) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				String data = this.dataBuilder.toString().trim();
				this.song.setNotes(data);
			}
		} else if (qName.contains("Size")) {
			// make sure the tag was not self terminating
			if (this.dataBuilder != null) {
				SongPart part = this.getSongPartForSize(qName);
				if (part != null) {
					// interpret the size
					String s = this.dataBuilder.toString().trim();
					try {
						int size = Integer.parseInt(s);
						part.setFontSize((int)Math.floor(size * FONT_SIZE_SCALE));
					} catch (NumberFormatException e) {
						throw new SAXException("Unable to parse the font size: '" + s + "' as an integer.", e);
					}
				}
			}
		}
		
		this.dataBuilder = null;
	}
	
	/**
	 * Returns the song part type for the given tag name.
	 * @param name the tag name
	 * @return {@link SongPartType}
	 * @throws SAXException thrown if the part name is not recognized
	 */
	private SongPartType getPartType(String name) throws SAXException {
		if (name.startsWith("Verse")) {
			return SongPartType.VERSE;
		} else if (name.startsWith("Chorus") || name.equalsIgnoreCase("Song")) {
			return SongPartType.CHORUS;
		} else if (name.equalsIgnoreCase("Bridge")) {
			return SongPartType.BRIDGE;
		} else if (name.equalsIgnoreCase("Ending")) {
			return SongPartType.END;
		} else if (name.equalsIgnoreCase("Tag")) {
			return SongPartType.TAG;
		} else if (name.equalsIgnoreCase("Vamp")) {
			return SongPartType.VAMP;
		} else {
			throw new SAXException("Song part: '" + name + "' not recognized.");
		}
	}
	
	/**
	 * Returns the {@link SongPart} for the given font size tag name.
	 * @param name the font size tag name
	 * @return {@link SongPart}
	 * @throws SAXException if the part name is not recognized or does not match the expected part pattern
	 */
	private SongPart getSongPartForSize(String name) throws SAXException {
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
					return this.song.getSongPart(SongPartType.VAMP, 1);
				} else if ("B".equalsIgnoreCase(t)) {
					return this.song.getSongPart(SongPartType.BRIDGE, 1);
				} else if ("T".equalsIgnoreCase(t)) {
					return this.song.getSongPart(SongPartType.TAG, 1);
				} else if ("E".equalsIgnoreCase(t)) {
					return this.song.getSongPart(SongPartType.END, 1);
				} else {
					throw new SAXException("Song part: '" + name + "' not recognized.");
				}
			} else {
				int index = 0;
				try {
					index = Integer.parseInt(i);
				} catch (NumberFormatException e) {
					throw new SAXException("Unable to parse the part index: '" + matcher.group(2) + "' as an integer.", e);
				}
				// then its a chorus or verse tag
				if ("C".equalsIgnoreCase(t)) {
					// chorus
					return this.song.getSongPart(SongPartType.CHORUS, index);
				} else if ("V".equalsIgnoreCase(t)) {
					// verse
					return this.song.getSongPart(SongPartType.VERSE, index);
				} else {
					throw new SAXException("Song part: '" + name + "' not recognized.");
				}
			}
		} else {
			throw new SAXException("The song part: '" + name + "' did not match the expected pattern.");
		}
	}
}
