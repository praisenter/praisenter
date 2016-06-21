package org.praisenter.bible;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

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
	public List<Bible> execute(Path path) throws IOException, SQLException, FileNotFoundException, BibleAlreadyExistsException, InvalidFormatException, UnknownFormatException {
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
		String fileName = path.getFileName().toString().toLowerCase();
		// we'll try by the file extension first
		if (fileName.endsWith(".zip")) {
			// read the contents to see what format we are looking at
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					// check for unbound bible book file
					if (entry.getName().equalsIgnoreCase("book_names.txt")) {
						return new UnboundBibleImporter(this.library);
					// check for .xmm (OpenSong) extension
					} else if (entry.getName().toLowerCase().endsWith(".xmm")) {
						return new OpenSongBibleImporter(this.library);
					// check for .xml (Zefania) extension but could be any kind of XML really
					} else if (entry.getName().toLowerCase().endsWith(".xml")) {
						byte[] content = AbstractBibleImporter.read(zis);
						return this.getImporterForXml(new ByteArrayInputStream(content));
					// otherwise read the first line of the file to see
					// if we can determine the file type that way
					} else {
						byte[] content = AbstractBibleImporter.read(zis);
						return this.getImporterForFile(new ByteArrayInputStream(content));
					}
				}
			}
		// check for .xmm (OpenSong) extension
		} else if (fileName.endsWith(".xmm")) {
			return new OpenSongBibleImporter(this.library);
		// check for .xml (Zefania) extension but could be any kind of XML really
		} else if (fileName.endsWith(".xml")) {
			byte[] content = AbstractBibleImporter.read(new FileInputStream(path.toFile()));
			return this.getImporterForXml(new ByteArrayInputStream(content));
		// otherwise read the first line of the file to see
		// if we can determine the file type that way
		} else {
			byte[] content = AbstractBibleImporter.read(new FileInputStream(path.toFile()));
			return this.getImporterForFile(new ByteArrayInputStream(content));
		}
		
		return null;
	}
	
	private BibleImporter getImporterForXml(ByteArrayInputStream stream) {
		try {
			XMLInputFactory f = XMLInputFactory.newInstance();
			XMLStreamReader r = f.createXMLStreamReader(stream);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().equalsIgnoreCase("xmlbible") ||
			    		r.getLocalName().equalsIgnoreCase("x")) {
			    		return new ZefaniaBibleImporter(this.library);
			    	} else if (r.getLocalName().equalsIgnoreCase("bible")) {
			    		return new OpenSongBibleImporter(this.library);
			    	}
			    }
			}
		} catch (Exception ex) {
			
		}
		return null;
	}
	
	private BibleImporter getImporterForFile(ByteArrayInputStream stream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			if (line.toUpperCase().startsWith("#THE UNBOUND BIBLE")) {
				return new UnboundBibleImporter(this.library);
			} else if (line.toUpperCase().startsWith("<?XML")) {
				// then its an xml document
				stream.reset();
				return getImporterForXml(stream);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
