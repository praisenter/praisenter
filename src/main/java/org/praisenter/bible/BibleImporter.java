package org.praisenter.bible;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public interface BibleImporter {
	public abstract void execute(Path path) throws IOException, SQLException, FileNotFoundException, BibleAlreadyExistsException, BibleFormatException, BibleImportException;
}
