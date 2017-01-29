@XmlSchema(
	elementFormDefault = XmlNsForm.QUALIFIED, 
	attributeFormDefault = XmlNsForm.UNQUALIFIED,
	xmlns = {
		@XmlNs(namespaceURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, prefix = "xsi")
	})
package org.praisenter.slide;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
