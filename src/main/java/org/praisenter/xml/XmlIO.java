/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
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
import org.praisenter.Reference;

/**
 * Helper class for reading and saving XML using JAXB.
 * @author William Bittle
 * @version 3.0.0
 */
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
		final Reference<JAXBException> ref = new Reference<JAXBException>();
		XmlContext context = CONTEXTS.computeIfAbsent(clazz, new Function<Class<?>, XmlContext>() {
			public XmlContext apply(Class<?> clazz) {
				try {
					return XmlContext.create(clazz);
				} catch (PropertyException e) {
					LOGGER.error("Couldn't assign context property for class [" + clazz.getName() + "]: ", e);
					ref.set(e);
				} catch (JAXBException e) {
					LOGGER.error("Couldn't create context for class [" + clazz.getName() + "]: ", e);
					ref.set(e);
				}
				return null;
			}
		});
		if (context == null) {
			if (ref.get() != null) {
				throw ref.get();
			} else {
				throw new JAXBException("");
			}
		}
		return context;
	}

	/**
	 * Reads the given string as XML.
	 * @param string the xml in a string
	 * @param clazz the type to read in
	 * @return E
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while reading the XML
	 */
	public static final <E> E read(String string, Class<E> clazz) throws JAXBException, IOException {
		// otherwise attempt to read the file
		XmlContext context = getXmlContext(clazz);
		Unmarshaller unmarshaller = context.unmarshaller;
		return clazz.cast(unmarshaller.unmarshal(new StringReader(string)));
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
	
	/**
	 * Writes the XML representation of the given object to string and returns it.
	 * @param object the object to write to a string
	 * @return String
	 * @throws JAXBException thrown if a JAXB context could not be created for the given type
	 * @throws IOException thrown if an exception occurs while writing the XML
	 */
	public static final <E> String save(E object) throws JAXBException, IOException {
		XmlContext context = getXmlContext(object.getClass());
		Marshaller marshaller = context.marshaller;
		StringWriter sw = new StringWriter();
		marshaller.marshal(object, sw);
		return sw.toString();
	}
}
