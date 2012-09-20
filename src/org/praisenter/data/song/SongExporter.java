package org.praisenter.data.song;

import java.util.List;

import org.praisenter.data.DataException;
import org.praisenter.utilities.XmlFormatter;

/**
 * Class used to export the entire song database to an XML document.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongExporter {
	
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
		  .append("<Title>").append(song.title).append("</Title>")
		  .append("<Notes>").append(song.notes).append("</Notes>")
		  .append("<DateAdded>").append(song.dateAdded).append("</DateAdded>")
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
		  .append("<Index>").append(part.partIndex).append("</Index>")
		  .append("<Name>").append(part.partName).append("</Name>")
		  .append("<Text>").append(part.text).append("</Text>")
		  .append("<FontSize>").append(part.fontSize).append("</FontSize>")
		  .append("</SongPart>");
		
		return sb.toString();
	}
}
