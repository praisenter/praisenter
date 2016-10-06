package org.praisenter.bible;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.xml.bind.JAXBException;

public class PraisenterBibleExporter implements BibleExporter {
	@Override
	public void execute(Path path, List<Bible> bibles) throws IOException, JAXBException {
		// TODO build zip with bibles in it
		// be sure to clear id and import date info
	}
}
