package org.praisenter.slide;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.xml.XmlIO;

/**
 * Represents a list of resolutions.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "Resolutions")
@XmlAccessorType(XmlAccessType.NONE)
public class Resolutions {
	/** The resolutions */
	@XmlElement(name = "Resolution", required = true, nillable = false)
	protected List<Resolution> resolutions;
	
	/** Default constructor */
	protected Resolutions() {}
	
	/**
	 * Minimal constructor.
	 * @param resolutions the resolutions
	 */
	protected Resolutions(List<Resolution> resolutions) {
		this.resolutions = resolutions;
	}
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Resolutions.class);
	
	/** The list of default resolutions */
	private static final Resolution[] DEFAULT_RESOLUTIONS = new Resolution[] {
		new Resolution(800, 600),
		new Resolution(1024, 768),
		new Resolution(1280, 720),
		new Resolution(1280, 800),
		new Resolution(1280, 1024),
		new Resolution(1400, 1050),
		new Resolution(1600, 1200),
		new Resolution(1920, 1080),
		new Resolution(1920, 1200)
	};
	
	/** The configuration file name */
	private static final String RESOLUTIONS_FILE = Constants.CONFIGURATION_FILE_LOCATION + Constants.SEPARATOR + "resolutions.xml";
	
	/** The list of resolutions */
	private static final List<Resolution> RESOLUTIONS = getSavedResolutions();
	
	/**
	 * Returns the saved resolutions from the resolution configuration file.
	 * @return List&lt;{@link Resolution}&gt;
	 */
	private static final List<Resolution> getSavedResolutions() {
		List<Resolution> resolutions = new ArrayList<Resolution>();
		Collections.addAll(resolutions, DEFAULT_RESOLUTIONS);
		try {
			// overwrite the default resolutions with whats in the file
			resolutions = XmlIO.read(RESOLUTIONS_FILE, Resolutions.class).resolutions;
			// check the size
			if (resolutions.size() == 0) {
				// if there aren't any, then use the default resolutions
				Collections.addAll(resolutions, DEFAULT_RESOLUTIONS);
			}
			// make sure they are sorted
			Collections.sort(resolutions);
			return resolutions;
		} catch (FileNotFoundException e) {
			LOGGER.warn("Resolution configuration file not found. Generating default configuration file.");
			// save the file
			try {
				XmlIO.save(RESOLUTIONS_FILE, new Resolutions(resolutions));
			} catch (JAXBException e1) {
				// just log these execptions
				LOGGER.error("A JAXB error occurred while saving the resolution configuration file: ", e1);
			} catch (IOException e1) {
				// just log these execptions
				LOGGER.error("An IO error occurred while saving the resolution configuration file: ", e1);
			}
		} catch (JAXBException e) {
			LOGGER.error("Unable to read resolution configuration file. Using default configuration: ", e);
		} catch (IOException e) {
			LOGGER.error("An IO error occurred while reading the resolution configuration file. Using default configuration: ", e);
		}
		return resolutions;
	}
	
	/**
	 * Returns the list of svaed resolutions.
	 * @return List&lt;{@link Resolution}&gt;
	 */
	public static synchronized final List<Resolution> getResolutions() {
		return new ArrayList<Resolution>(RESOLUTIONS);
	}
	
	/**
	 * Adds a new resolution to the list of resolutions.
	 * <p>
	 * This will also save the resolutions to the resolutions config file.
	 * @param resolution the resolution to add
	 * @throws JAXBException thrown if a JAXB error occurs while saving the resolution config file
	 * @throws IOException thrown if an IO error occurs while saving the resolution config file
	 */
	public static synchronized final void addResolution(Resolution resolution) throws JAXBException, IOException {
		if (resolution == null) return;
		// check if it exists
		for (Resolution r : RESOLUTIONS) {
			if (r.equals(resolution)) {
				return;
			}
		}
		// add the resolution
		RESOLUTIONS.add(resolution);
		// make sure they are sorted
		Collections.sort(RESOLUTIONS);
		// save the resolutions
		XmlIO.save(RESOLUTIONS_FILE, new Resolutions(RESOLUTIONS));
	}
	
	/**
	 * Removes the given resolution from the list of resolutions.
	 * <p>
	 * This will also save the resolutions to the resolutions config file.
	 * @param resolution the resolution to remove
	 * @throws JAXBException thrown if a JAXB error occurs while saving the resolution config file
	 * @throws IOException thrown if an IO error occurs while saving the resolution config file
	 */
	public static synchronized final void removeResolution(Resolution resolution) throws JAXBException, IOException {
		if (resolution == null) return;
		// add the resolution
		RESOLUTIONS.remove(resolution);
		// make sure they are sorted
		Collections.sort(RESOLUTIONS);
		// save the resolutions
		XmlIO.save(RESOLUTIONS_FILE, new Resolutions(RESOLUTIONS));
	}
}
