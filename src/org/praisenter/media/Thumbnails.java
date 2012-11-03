package org.praisenter.media;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Container class for the thumbnail XML documents.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "Thumbnails")
public class Thumbnails {
	/** Hidden constructor. */
	private Thumbnails() {}
	
	/** The list of thumbnails */
	@XmlElement(name = "Thumbnail")
	protected List<Thumbnail> thumbnails;
	
	/**
	 * Reads the thumbnails file at the given file path.
	 * @param filePath the file name and path
	 * @return List&lt;{@link Thumbnail}&gt;
	 * @throws JAXBException thrown if an exception occurs while reading the XML file
	 */
	public static final List<Thumbnail> read(String filePath) throws JAXBException {
		// see if the file exists
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		// otherwise attempt to read the file
		JAXBContext context = JAXBContext.newInstance(Thumbnails.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Thumbnails thumbnails = (Thumbnails)unmarshaller.unmarshal(file);
		return thumbnails.thumbnails;
	}
	
	/**
	 * Saves the given thumbnails to a thumbnails file.
	 * @param filePath the file name and path to save the file
	 * @param thumbnails the thumbnails to save in the file
	 * @throws PropertyException thrown if a property on the marshaller cannot be set
	 * @throws JAXBException thrown if an exception occurs while writing the XML file
	 * @throws IOException thrown if an IO error occurs
	 */
	public static final void save(String filePath, List<Thumbnail> thumbnails) throws PropertyException, JAXBException, IOException {
		Thumbnails thumbs = new Thumbnails();
		thumbs.thumbnails = thumbnails;
		
		JAXBContext context = JAXBContext.newInstance(Thumbnails.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		marshaller.marshal(thumbs, file);
	}
}
