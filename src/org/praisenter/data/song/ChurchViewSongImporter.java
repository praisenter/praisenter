package org.praisenter.data.song;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.DataImportException;
import org.praisenter.resources.Messages;
import org.xml.sax.SAXException;

/**
 * Song list importer from the ChurchView file format (xml).
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChurchViewSongImporter {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(ChurchViewSongImporter.class);
	
	/**
	 * Imports the songs contained in the given file.
	 * @param file the file to import
	 * @throws DataImportException if an error occurs during import
	 */
	public static final void importSongs(File file) throws DataImportException {
		try {
			LOGGER.info("Reading ChurchView song file: " + file.getName());
			// load the songs xml file into objects
			List<Song> songs = ChurchViewSongReader.fromXml(file);
			LOGGER.info("ChurchView song file read successfully: " + file.getName());
			
			// insert the songs into the database
			for (Song song : songs) {
				try {
					// save the song
					Songs.saveSong(song);
				} catch (DataException e) {
					// just throw this error
					throw new DataImportException(e);
				}
			}
			
			LOGGER.info("ChurchView song file imported successfully: " + file.getName());
		} catch (ParserConfigurationException e) {
			LOGGER.error(e);
			// throw a translated error
			throw new DataImportException(Messages.getString("songs.import.unrecognizedFormat"), e);
		} catch (SAXException e) {
			LOGGER.error(e);
			// throw a translated error
			throw new DataImportException(Messages.getString("songs.import.unrecognizedFormat"), e);
		} catch (IOException e) {
			LOGGER.error(e);
			// throw a translated error
			throw new DataImportException(Messages.getString("songs.import.io"), e);
		}
	}
}
