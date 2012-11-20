package org.praisenter.preferences;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.slide.RenderQuality;
import org.praisenter.xml.XmlIO;

/**
 * Class used to store application preferences.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// FIXME translate
@XmlRootElement(name = "Preferences")
@XmlAccessorType(XmlAccessType.NONE)
public class Preferences {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Preferences.class);
	
	/** The configuration file name */
	private static final String CONFIGURATION_FILE = Constants.CONFIGURATION_FILE_LOCATION + Constants.SEPARATOR + "config.xml";
	
	// general settings
	
	/** The string id of the primary device */
	@XmlElement(name = "PrimaryDeviceId", required = false, nillable = true)
	protected String primaryDeviceId;
	
	/** The resolution of the primary device */
	@XmlElement(name = "PrimaryDeviceResolution", required = false, nillable = true)
	protected Dimension primaryDeviceResolution;
	
	/** The desired render quality of the application */
	@XmlElement(name = "RenderQuality", required = true, nillable = false)
	protected RenderQuality renderQuality;
	
	/** True if smart transitions should be enabled */
	@XmlElement(name = "SmartTransitionsEnabled", required = true, nillable = false)
	protected boolean smartTransitionsEnabled;
	
	// other settings
	
	/** The bible preferences */
	@XmlElement(name = "BiblePreferences", required = true, nillable = false)
	protected BiblePreferences biblePreferences;
	
	/** The song preferences */
	@XmlElement(name = "SongPreferences", required = true, nillable = false)
	protected SongPreferences songPreferences;
	
	/** The notification preferences */
	@XmlElement(name = "NotificationPreferences", required = true, nillable = false)
	protected NotificationPreferences notificationPreferences;
	
	/** The error reportin preferences */
	@XmlElement(name = "ErrorReportingPreferences", required = true, nillable = false)
	protected ErrorReportingPreferences errorReportingPreferences;
	
	/** The singleton instance */
	private static Preferences instance;
	
	/** Hidden constructor */
	private Preferences() {
		this.renderQuality = RenderQuality.MEDIUM;
		this.smartTransitionsEnabled = true;
		
		this.biblePreferences = new BiblePreferences();
		this.songPreferences = new SongPreferences();
		this.notificationPreferences = new NotificationPreferences();
		this.errorReportingPreferences = new ErrorReportingPreferences();
	}
	
	/**
	 * Returns the preferences singleton object.
	 * @return {@link Preferences}
	 */
	public static synchronized final Preferences getInstance() {
		// see if we have loaded the preferences or not
		if (instance == null) {
			try {
				// load the preferences from file
				instance = XmlIO.read(CONFIGURATION_FILE, Preferences.class);
				return instance;
			} catch (FileNotFoundException e) {
				LOGGER.warn("Unable to find the configuration file [" + CONFIGURATION_FILE + "]: ", e);
				// just create a new instance and save that
				instance = new Preferences();
				// save the new instance
				try {
					instance.save();
				} catch (PreferencesException ex) {
					LOGGER.error("Unable to save the default configuration file due to: ", ex);
				}
				return instance;
			} catch (JAXBException e) {
				LOGGER.error("Unable to load the configuration file due to: ", e);
			} catch (IOException e) {
				LOGGER.error("Unable to load the configuration file due to: ", e);
			}
			// just create a new instance (but dont save it)
			instance = new Preferences();
		}
		// return the loaded instance
		return instance;
	}
	
	/**
	 * Saves the preferences instance to disc.
	 * @throws PreferencesException thrown if the save fails
	 */
	public void save() throws PreferencesException {
		try {
			XmlIO.save(CONFIGURATION_FILE, this);
		} catch (JAXBException e) {
			LOGGER.error("Unable to save the configuration due to: ", e);
			throw new PreferencesException("", e);
		} catch (IOException e) {
			LOGGER.error("Unable to save the configuration due to: ", e);
			throw new PreferencesException("", e);
		}
	}

	/**
	 * Returns the primary device id.
	 * <p>
	 * This is the id of the primary display in which the presentation of
	 * slides and notifications will be shown.
	 * @return String
	 */
	public String getPrimaryDeviceId() {
		return this.primaryDeviceId;
	}

	/**
	 * Sets the primary device id.
	 * @param primaryDeviceId the id of the primary device
	 */
	public void setPrimaryDeviceId(String primaryDeviceId) {
		this.primaryDeviceId = primaryDeviceId;
	}

	/**
	 * Returns the primary device's resolution.
	 * @return Dimension
	 */
	public Dimension getPrimaryDeviceResolution() {
		return this.primaryDeviceResolution;
	}

	/**
	 * Sets the primary device's resolution.
	 * @param primaryDeviceResolution the primary device's resolution
	 */
	public void setPrimaryDeviceResolution(Dimension primaryDeviceResolution) {
		this.primaryDeviceResolution = primaryDeviceResolution;
	}

	/**
	 * Returns the render quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getRenderQuality() {
		return this.renderQuality;
	}

	/**
	 * Sets the render quality.
	 * @param renderQuality the render quality
	 */
	public void setRenderQuality(RenderQuality renderQuality) {
		this.renderQuality = renderQuality;
	}

	/**
	 * Returns true if smart transitions should be used.
	 * <p>
	 * Smart transitions attempt to retain the playback of background videos
	 * across slides that have the same background video.
	 * @return boolean
	 */
	public boolean isSmartTransitionsEnabled() {
		return this.smartTransitionsEnabled;
	}

	/**
	 * Sets the use of smart transitions.
	 * @param smartTransitionsEnabled true if smart transitions should be used
	 */
	public void setSmartTransitionsEnabled(boolean smartTransitionsEnabled) {
		this.smartTransitionsEnabled = smartTransitionsEnabled;
	}

	/**
	 * Returns the bible preferences.
	 * @return {@link BiblePreferences}
	 */
	public BiblePreferences getBiblePreferences() {
		return this.biblePreferences;
	}

	/**
	 * Returns the song preferences.
	 * @return {@link SongPreferences}
	 */
	public SongPreferences getSongPreferences() {
		return this.songPreferences;
	}

	/**
	 * Returns the notification preferences.
	 * @return {@link NotificationPreferences}
	 */
	public NotificationPreferences getNotificationPreferences() {
		return this.notificationPreferences;
	}

	/**
	 * Returns the error reporting preferences.
	 * @return {@link ErrorReportingPreferences}
	 */
	public ErrorReportingPreferences getErrorReportingPreferences() {
		return this.errorReportingPreferences;
	}
}
