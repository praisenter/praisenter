/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
 * @version 2.0.0
 * @since 2.0.0
 */
public final class XmlIO {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XmlIO.class);
	
	/** The cached JAXB contexts */
	private static final Map<Class<?>, XmlContext> CONTEXTS = new HashMap<Class<?>, XmlContext>();
	
	/** Hidden default constructor */
	private XmlIO() {}
	
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
