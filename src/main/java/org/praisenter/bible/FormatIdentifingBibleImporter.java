package org.praisenter.bible;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.praisenter.InvalidFormatException;
import org.praisenter.UnknownFormatException;

public final class FormatIdentifingBibleImporter implements BibleImporter {
	/** The {@link BibleLibrary} */
	private final BibleLibrary library;
	
	/**
	 * Minimal constructor.
	 * @param library the bible library to import into
	 */
	public FormatIdentifingBibleImporter(BibleLibrary library) {
		this.library = library;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.bible.BibleImporter#execute(java.nio.file.Path)
	 */
	@Override
	public Bible execute(Path path) throws IOException, SQLException, FileNotFoundException, BibleAlreadyExistsException, InvalidFormatException, UnknownFormatException {
		// make sure the file exists
		if (Files.exists(path)) {
			BibleImporter importer = this.getImporter(path);
			if (importer == null) {
				throw new UnknownFormatException(path.toAbsolutePath().toString());
			}
			return importer.execute(path);
		} else {
			throw new FileNotFoundException(path.toAbsolutePath().toString());
		}
	}
	
	/**
	 * Attempts to read the file to determine what format the file is.
	 * @param path the path
	 * @return {@link BibleImporter}
	 * @throws IOException if an IO error occurs
	 * @throws FileNotFoundException if the file is not found
	 */
	private BibleImporter getImporter(Path path) throws IOException, FileNotFoundException {
		// we'll try by the file extension first
		if (path.getFileName().toString().toLowerCase().endsWith(".zip")) {
			// read the contents to see what format we are looking at
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().equalsIgnoreCase("book_names.txt")) {
						return new UnboundBibleImporter(this.library);
					}
					if (entry.getName().toLowerCase().endsWith(".xmm")) {
						return new OpenSongBibleImporter(this.library);
					}
				}
			}
		} else if (path.getFileName().toString().toLowerCase().endsWith(".xmm")) {
			return new OpenSongBibleImporter(this.library);
		}
		
		return null;
	}
}
