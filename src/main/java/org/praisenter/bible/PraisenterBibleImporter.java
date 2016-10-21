package org.praisenter.bible;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.InvalidFormatException;
import org.praisenter.UnknownFormatException;
import org.praisenter.xml.XmlIO;

public final class PraisenterBibleImporter extends AbstractBibleImporter implements BibleImporter {
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public List<Bible> execute(Path path) throws IOException, JAXBException, FileNotFoundException, InvalidFormatException, UnknownFormatException {
		List<Bible> bibles = new ArrayList<Bible>();
		
		// make sure the file exists
		if (Files.exists(path)) {
			LOGGER.debug("Reading Praisenter Bible file: " + path.toAbsolutePath().toString());

			boolean read = false;
			Throwable throwable = null;
			// first try to open it as a zip
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				LOGGER.debug("Reading as zip file: " + path.toAbsolutePath().toString());
				// read the entries (each should be a .xml file)
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					read = true;
					if (!entry.isDirectory()) {
						byte[] data = read(zis);
						try {
							Bible bible = XmlIO.read(new ByteArrayInputStream(data), Bible.class);
							bible.importDate = new Date();
							bibles.add(bible);
						} catch (Exception ex) {
							throwable = ex;
							LOGGER.warn("Failed to parse zip entry: " + entry.getName());
						}
					}
				}
			}
			
			// check if we read an entry
			// if not, that may mean the file was not a zip so try it as a normal file
			if (!read) {
				LOGGER.debug("Reading as XML file: " + path.toAbsolutePath().toString());
				// hopefully its an .xml
				// just read it
				try (FileInputStream stream = new FileInputStream(path.toFile())) {
					Bible bible = XmlIO.read(stream, Bible.class);
					bible.importDate = new Date();
					bibles.add(bible);
				}
			}

			// throw the exception stored during the unzip process
			// only if we didn't find any bibles (if we successfully read in
			// a bible from the zip then we don't want to throw)
			if (bibles.size() == 0 && throwable != null) {
				throw new InvalidFormatException(throwable.getMessage(), throwable);
			}

			return bibles;
		} else {
			// throw an exception
			throw new FileNotFoundException(path.toAbsolutePath().toString());
		}
	}
}
