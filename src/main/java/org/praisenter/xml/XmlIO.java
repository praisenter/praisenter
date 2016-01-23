package org.praisenter.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class XmlIO {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger(XmlIO.class);
	
	/** The cached JAXB contexts */
	private static final ConcurrentHashMap<Class<?>, XmlContext> CONTEXTS = new ConcurrentHashMap<Class<?>, XmlContext>();
	
	/** Hidden default constructor */
	private XmlIO() {}
	
	/**
	 * Returns the {@link XmlContext} for the given class.
	 * @param clazz the class
	 * @return {@link XmlContext}
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 */
	private static final XmlContext getXmlContext(final Class<?> clazz) throws JAXBException {
		XmlContext context = CONTEXTS.computeIfAbsent(clazz, new Function<Class<?>, XmlContext>() {
			public XmlContext apply(Class<?> clazz) {
				try {
					return XmlContext.create(clazz);
				} catch (PropertyException e) {
					LOGGER.error("Couldn't assign context property for class [" + clazz.getName() + "]: ", e);
				} catch (JAXBException e) {
					LOGGER.error("Couldn't create context for class [" + clazz.getName() + "]: ", e);
				}
				return null;
			}
		});
		if (context == null) {
			throw new JAXBException("");
		}
		return context;
	}
	
	/**
	 * Reads the file at the given file path.
	 * @param path the path
	 * @param clazz the type to read in
	 * @return E
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while reading the XML file
	 */
	public static final <E> E read(Path path, Class<E> clazz) throws FileNotFoundException, JAXBException, IOException {
		// see if the file exists
		if (!Files.exists(path)) {
			throw new FileNotFoundException("The file [" + path.toAbsolutePath().toString() + "] was not found.");
		}
		// otherwise attempt to read the file
		XmlContext context = getXmlContext(clazz);
		Unmarshaller unmarshaller = context.unmarshaller;
		return clazz.cast(unmarshaller.unmarshal(path.toFile()));
	}
	
	/**
	 * Reads the given input stream.
	 * @param stream the input stream to read
	 * @param clazz the type to read in
	 * @return E
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while reading the XML file
	 */
	public static final <E> E read(InputStream stream, Class<E> clazz) throws JAXBException, IOException {
		// otherwise attempt to read the file
		XmlContext context = getXmlContext(clazz);
		Unmarshaller unmarshaller = context.unmarshaller;
		return clazz.cast(unmarshaller.unmarshal(stream));
	}
	
	/**
	 * Saves the given object to the given file file.
	 * @param path the path to save the file to
	 * @param object the object to save in the file
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while writing the XML file
	 */
	public static final <E> void save(Path path, E object) throws JAXBException, IOException {
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (FileAlreadyExistsException ex) {
				// ignore this
			}
		}
		XmlContext context = getXmlContext(object.getClass());
		Marshaller marshaller = context.marshaller;
		marshaller.marshal(object, path.toFile());
	}
	
	/**
	 * Saves the given object to the given stream.
	 * @param stream the stream to write to
	 * @param object the object to save in the file
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while writing the XML file
	 */
	public static final <E> void save(OutputStream stream, E object) throws JAXBException, IOException {
		XmlContext context = getXmlContext(object.getClass());
		Marshaller marshaller = context.marshaller;
		marshaller.marshal(object, stream);
	}
}