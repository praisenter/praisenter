package org.praisenter.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

/**
 * Class used to cache JAXB resources.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XmlContext {
	/** The JAXB context */
	protected JAXBContext context;
	
	/** The marshaller */
	protected Marshaller marshaller;
	
	/** The unmarshaller */
	protected Unmarshaller unmarshaller;
	
	/** Hidden constructor */
	private XmlContext() {}

	/**
	 * Creates a new {@link XmlContext} object for the given class.
	 * @param clazz the class
	 * @return {@link XmlContext}
	 * @throws PropertyException thrown if an exception occurs while assigning a marshaller property
	 * @throws JAXBException thrown if an exception occurs while trying to build the {@link XmlContext}
	 */
	public static XmlContext create(Class<?> clazz) throws PropertyException, JAXBException {
		XmlContext context = new XmlContext();
		// create the JAXB context
		context.context = JAXBContext.newInstance(clazz);
		// create the unmarshaller
		context.unmarshaller = context.context.createUnmarshaller();
		// create the marshaller
		context.marshaller = context.context.createMarshaller();
		context.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		context.marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		
		return context;
	}
	
	/**
	 * Returns the JAXBContext.
	 * @return JAXBContext
	 */
	public JAXBContext getContext() {
		return this.context;
	}

	/**
	 * Returns the marshaller.
	 * @return Marshaller
	 */
	public Marshaller getMarshaller() {
		return this.marshaller;
	}

	/**
	 * Returns the unmarshaller.
	 * @return Unmarshaller
	 */
	public Unmarshaller getUnmarshaller() {
		return this.unmarshaller;
	}
}
