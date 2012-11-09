package org.praisenter.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

/**
 * Container class for the thumbnail XML documents.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XmlIO {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XmlIO.class);
	
	/** The cached JAXB contexts */
	private static final Map<Class<?>, XmlContext> CONTEXTS = new HashMap<Class<?>, XmlContext>();
	
	/**
	 * Returns the {@link XmlContext} for the given class.
	 * @param clazz the class
	 * @return {@link XmlContext}
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 */
	private static synchronized final XmlContext getXmlContext(Class<?> clazz) throws JAXBException {
		XmlContext context = CONTEXTS.get(clazz);
		if (context == null) {
			try {
				context = XmlContext.create(clazz);
				CONTEXTS.put(clazz, context);
			} catch (PropertyException e) {
				LOGGER.error("Couldn't assign context property for class [" + clazz.getName() + "]: ", e);
				throw e;
			} catch (JAXBException e) {
				LOGGER.error("Couldn't create context for class [" + clazz.getName() + "]: ", e);
				throw e;
			}
		}
		return context;
	}
	
	/**
	 * Reads the file at the given file path.
	 * @param filePath the file name and path
	 * @param clazz the type to read in
	 * @return E
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while reading the XML file
	 */
	public static final <E> E read(String filePath, Class<E> clazz) throws FileNotFoundException, JAXBException, IOException {
		// see if the file exists
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("The file [" + file.getAbsolutePath() + "] was not found.");
		}
		// otherwise attempt to read the file
		XmlContext context = getXmlContext(clazz);
		Unmarshaller unmarshaller = context.getUnmarshaller();
		return clazz.cast(unmarshaller.unmarshal(file));
	}
	
	/**
	 * Saves the given object to the given file file.
	 * @param filePath the file name and path to save the file
	 * @param object the object to save in the file
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while writing the XML file
	 */
	public static final <E> void save(String filePath, E object) throws JAXBException, IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		XmlContext context = getXmlContext(object.getClass());
		Marshaller marshaller = context.getMarshaller();
		marshaller.marshal(object, file);
	}
}
