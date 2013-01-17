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

import java.text.DateFormat;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.praisenter.data.DataException;
import org.praisenter.utilities.XmlFormatter;

/**
 * Class used to export the entire song database to an XML document.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated Replaced by JAXB annotations in v2.0.0
 */
@Deprecated
public final class SongExporter {
	/** The output date format */
	protected static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	/**
	 * Exports the songs to an XML string.
	 * @return String the xml
	 * @throws DataException if an exception occurs getting the song data
	 */
	public static final String exportSongs() throws DataException {
		StringBuffer sb = new StringBuffer();
		List<Song> songs = Songs.getSongs();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
		  .append("<Songs>");
		for (Song song : songs) {
			sb.append(toXml(song));
		}
		sb.append("</Songs>");
		
		return XmlFormatter.format(sb.toString(), 4);
	}
	
	/**
	 * Converts the given song to XML.
	 * @param song the song
	 * @return String the xml
	 */
	private static final String toXml(Song song) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Song>")
		  .append("<Id>").append(song.id).append("</Id>")
		  .append("<Title>").append(StringEscapeUtils.escapeXml(song.title)).append("</Title>")
		  .append("<Notes>").append(StringEscapeUtils.escapeXml(song.notes)).append("</Notes>")
		  .append("<DateAdded>").append(DATE_FORMAT.format(song.dateAdded)).append("</DateAdded>")
		  .append("<Parts>");
		for (SongPart part : song.parts) {
			sb.append(toXml(part));
		}
		sb.append("</Parts>")
		  .append("</Song>");
		
		return sb.toString();
	}
	
	/**
	 * Converts the given song part to XML.
	 * @param part the song part
	 * @return String the xml
	 */
	private static final String toXml(SongPart part) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<SongPart>")
		  .append("<Id>").append(part.id).append("</Id>")
		  .append("<Type>").append(part.type).append("</Type>")
		  .append("<Index>").append(part.index).append("</Index>")
		  .append("<Order>").append(part.order).append("</Order>")
		  .append("<Text>").append(StringEscapeUtils.escapeXml(part.text)).append("</Text>")
		  .append("<FontSize>").append(part.fontSize).append("</FontSize>")
		  .append("</SongPart>");
		
		return sb.toString();
	}
}
