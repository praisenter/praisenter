package org.praisenter.bible;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.xml.bind.JAXBException;

// TODO add export tool
public interface BibleExporter {
	
	public abstract void execute(Path path, List<Bible> bibles) throws IOException, JAXBException;
}
