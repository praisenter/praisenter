/**
 * This package represents the JAXB implementation of the OpenLyrics song format
 * allowing both reading and writing.
 * @see <a href="http://openlyrics.org/namespace/2009/song">OpenLyrics</a>
 */
@javax.xml.bind.annotation.XmlSchema(
		namespace = "http://openlyrics.org/namespace/2009/song", 
		elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.praisenter.song.openlyrics;