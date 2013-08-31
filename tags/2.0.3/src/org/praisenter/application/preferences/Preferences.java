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
package org.praisenter.application.preferences;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.log4j.Logger;
import org.praisenter.application.Constants;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.common.xml.DimensionTypeAdapter;
import org.praisenter.common.xml.XmlIO;
import org.praisenter.presentation.PresentationEventConfiguration;
import org.praisenter.presentation.PresentationWindowType;
import org.praisenter.slide.graphics.RenderQualities;

/**
 * Class used to store application preferences.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
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
	@XmlJavaTypeAdapter(DimensionTypeAdapter.class)
	protected Dimension primaryDeviceResolution;
	
	/** True if smart video transitions should be enabled */
	@XmlElement(name = "SmartVideoTransitionsEnabled", required = true, nillable = false)
	protected boolean smartVideoTransitionsEnabled;

	/** True if smart image transitions should be enabled */
	@XmlElement(name = "SmartImageTransitionsEnabled", required = true, nillable = false)
	protected boolean smartImageTransitionsEnabled;
	
	/** True if a send or clear should wait for the currently executing transition to finish before being executed */
	@XmlElement(name = "WaitForTransitionEnabled", required = true, nillable = false)
	protected boolean waitForTransitionEnabled;
	
	// rendering

	/** The overall render quality */
	@XmlElement(name = "RenderQualities", required = true, nillable = false)
	protected RenderQualities renderQualities;
	
	/** The transition delay in milliseconds */
	@XmlElement(name = "TransitionDelay", required = true, nillable = false)
	protected int transitionDelay;
	
	/** True if read-time video conversion is enabled */
	@XmlElement(name = "ReadTimeVideoConversionEnabled", required = false, nillable = true)
	protected boolean readTimeVideoConversionEnabled;
	
	// other settings
	
	/** The bible preferences */
	@XmlElement(name = "BiblePreferences", required = true, nillable = false)
	protected BiblePreferences biblePreferences;
	
	/** The song preferences */
	@XmlElement(name = "SongPreferences", required = true, nillable = false)
	protected SongPreferences songPreferences;
	
	/** The slide preferences */
	@XmlElement(name = "SlidePreferences", required = true, nillable = false)
	protected SlidePreferences slidePreferences;
	
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
		// default render qualities
		this.renderQualities = new RenderQualities();
		this.transitionDelay = 20;
		this.readTimeVideoConversionEnabled = false;
		
		this.smartVideoTransitionsEnabled = true;
		this.smartImageTransitionsEnabled = true;
		this.waitForTransitionEnabled = true;
		
		this.biblePreferences = new BiblePreferences();
		this.songPreferences = new SongPreferences();
		this.slidePreferences = new SlidePreferences();
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
				LOGGER.warn("Unable to find the configuration file [" + CONFIGURATION_FILE + "]: ");
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
		} catch (JAXBException | IOException e) {
			LOGGER.error("Unable to save the configuration due to: ", e);
			throw new PreferencesException(Messages.getString("preferences.save.exception"), e);
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

	// render quality
	
	/**
	 * Returns the render qualities.
	 * @return {@link RenderQualities}
	 */
	public RenderQualities getRenderQualities() {
		return this.renderQualities;
	}
	
	/**
	 * Returns the transition delay.
	 * <p>
	 * This is the wait period between the transition steps.
	 * @return int
	 */
	public int getTransitionDelay() {
		return this.transitionDelay;
	}
	
	/**
	 * Sets the transition delay.
	 * @param delay the delay in milliseconds
	 */
	public void setTransitionDelay(int delay) {
		this.transitionDelay = delay;
	}
	
	/**
	 * Returns true if read-time video conversion is enabled.
	 * @return boolean
	 * @since 2.0.1
	 */
	public boolean isReadTimeVideoConversionEnabled() {
		return this.readTimeVideoConversionEnabled;
	}
	
	/**
	 * Toggles read-time video conversion.
	 * @param flag true if read-time video conversion should be enabled.
	 * @since 2.0.1
	 */
	public void setReadTimeVideoConversionEnabled(boolean flag) {
		this.readTimeVideoConversionEnabled = flag;
	}
	
	// other preferences
	
	/**
	 * Returns true if smart video transitions should be used.
	 * <p>
	 * Smart video transitions attempt to retain the playback of background videos
	 * across slides that have the same background video.
	 * @return boolean
	 */
	public boolean isSmartVideoTransitionsEnabled() {
		return this.smartVideoTransitionsEnabled;
	}

	/**
	 * Sets the use of smart video transitions.
	 * @param flag true if smart video transitions should be used
	 */
	public void setSmartVideoTransitionsEnabled(boolean flag) {
		this.smartVideoTransitionsEnabled = flag;
	}

	/**
	 * Returns true if smart image transitions should be used.
	 * <p>
	 * Smart image transitions will attempt to keep the background fixed while
	 * the rest of the slide is transitioned if both slides have the same
	 * background.
	 * @return boolean
	 */
	public boolean isSmartImageTransitionsEnabled() {
		return this.smartImageTransitionsEnabled;
	}

	/**
	 * Sets the use of smart image transitions.
	 * @param flag true if smart image transitions should be used
	 */
	public void setSmartImageTransitionsEnabled(boolean flag) {
		this.smartImageTransitionsEnabled = flag;
	}

	/**
	 * Returns true if waiting on transitions is enabled.
	 * <p>
	 * Enabling this setting allows the send/clear functionality to wait on a currently executing
	 * transition to finish before executing the given one.  If multiple send/clear actions
	 * are submitted and this is enabled, only the last send/clear action will be used
	 * after the currently executing transition is completed.
	 * <p>
	 * If this setting is disabled, any currently executing transition is stopped and immediately
	 * completed and the given send/clear action is executed.
	 * @return boolean
	 */
	public boolean isWaitForTransitionEnabled() {
		return this.waitForTransitionEnabled;
	}
	
	/**
	 * Enables waiting for current transitions to complete.
	 * @param waitForTransitionEnabled true if waiting should be performed
	 */
	public void setWaitForTransitionEnabled(boolean waitForTransitionEnabled) {
		this.waitForTransitionEnabled = waitForTransitionEnabled;
	}
	
	// sub preferences
	
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
	 * Returns the slide preferences.
	 * @return {@link SlidePreferences}
	 */
	public SlidePreferences getSlidePreferences() {
		return this.slidePreferences;
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
	
	// helper methods
	
	/**
	 * Returns the primary device.
	 * <p>
	 * This will return the setup device if available. If it's not available
	 * the secondary device is returned. If no secondary device is present the
	 * default device is returned.
	 * @return GraphicsDevice
	 */
	public GraphicsDevice getPrimaryOrDefaultDevice() {
		GraphicsDevice device = WindowUtilities.getDeviceForId(this.primaryDeviceId);
		if (device == null) {
			device = WindowUtilities.getSecondaryDevice();
		}
		return device;
	}
	
	/**
	 * Returns the display size for the primary device.
	 * <p>
	 * This method will follow the same logic as the {@link #getPrimaryOrDefaultDevice()} method
	 * to obtain the primary device.
	 * <p>
	 * This method will check the setup display size against the display size of the primary device
	 * and will return the size for the device if its different than the setup display size.
	 * @return Dimension
	 */
	public Dimension getPrimaryOrDefaultDeviceResolution() {
		Dimension displaySize = this.getPrimaryDeviceResolution();
		GraphicsDevice device = this.getPrimaryOrDefaultDevice();
		Dimension deviceSize = WindowUtilities.getDimension(device.getDisplayMode());
		// check for null
		if (displaySize == null) {
			return deviceSize;
		}
		// perform a check against the display sizes
		if (!displaySize.equals(deviceSize)) {
			// if they are not equal we want to log a message and use the display size
			LOGGER.warn("The primary display's resolution does not match the stored display size. Using device resolution.");
			return deviceSize;
		}
		return displaySize;
	}
	
	/**
	 * Creates a new {@link PresentationEventConfiguration} for the current preferences.
	 * @param windowType the window type
	 * @return {@link PresentationEventConfiguration}
	 */
	public PresentationEventConfiguration getPresentationEventConfiguration(PresentationWindowType windowType) {
		PresentationEventConfiguration configuration = new PresentationEventConfiguration();
		configuration.setPresentationWindowDeviceId(this.primaryDeviceId);
		configuration.setPresentationWindowType(windowType);
		configuration.setRenderQualities(this.renderQualities);
		configuration.setWaitForTransitionEnabled(this.waitForTransitionEnabled);
		configuration.setSmartImageTransitionsEnabled(this.smartImageTransitionsEnabled);
		configuration.setSmartVideoTransitionsEnabled(this.smartVideoTransitionsEnabled);
		configuration.setReadTimeVideoConversionEnabled(this.readTimeVideoConversionEnabled);
		return configuration;
	}
}
