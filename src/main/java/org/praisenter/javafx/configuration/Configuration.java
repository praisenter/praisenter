package org.praisenter.javafx.configuration;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.xml.XmlIO;
import org.praisenter.xml.adapters.LocaleXmlAdapter;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
public final class Configuration {
	private static final Logger LOGGER = LogManager.getLogger();
	
	// restart required
	
	/** The currently in use language */
	private Locale language;
	
	/** The currently in use theme */
	private String theme;
	
	/** The currently saved language */
	private final ObjectProperty<Locale> savedLanguage = new SimpleObjectProperty<Locale>();
	
	/** The currently saved theme */
	private final StringProperty savedTheme = new SimpleStringProperty();
	
	// general
	
	// TODO changes to the values of the screen mappings wont work (might need to be a list property)
	/** The screen to role mapping */
	private final ObservableList<ScreenMapping> screenMappings = FXCollections.observableArrayList();
	
	// bible settings
	
	/** The primary bible id */
	private final IntegerProperty primaryBibleId = new SimpleIntegerProperty(-1);
	
	/** The secondary bible id */
	private final IntegerProperty secondaryBibleId = new SimpleIntegerProperty(-1);
	
	/** True to include the Apocryphal books (if available) of the bible */
	private final BooleanProperty apocryphaIncluded = new SimpleBooleanProperty(false);
	
	// slide
	
	private final ObservableList<Resolution> resolutions = FXCollections.observableArrayList();
	
	private final ObjectProperty<Resolution> resolution = new SimpleObjectProperty<Resolution>();
	
	public Configuration() {
		apocryphaIncluded.addListener((e) -> {
			try {
				Configuration.save(this);
			} catch (Exception ex) {
				// just log the error
				LOGGER.error("Failed to save configuration for apocrypha setting", ex);
			}
		});
	}
	
	public static final Configuration load() {
		try {
			Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
			if (Files.exists(path)) {
				Configuration conf = XmlIO.read(path, Configuration.class);
				conf.language = conf.savedLanguage.get();
				conf.theme = conf.savedTheme.get();
				return conf;
			}
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return null;
	}
	
	public static final void save(Configuration configuration) throws JAXBException, IOException {
		Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
		XmlIO.save(path, configuration);
	}
	
	public static final Configuration createDefaultConfiguration() {
		Configuration conf = new Configuration();
		conf.language = Locale.ENGLISH;
		conf.theme = "default";
		conf.savedLanguage.set(conf.language);
		conf.savedTheme.set(conf.theme);
		conf.resolutions.addAll(Resolution.DEFAULT_RESOLUTIONS);
		
		Resolution resolution = null;
		// default screens
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();
		for (int i = 0; i < devices.length; i++) {
			GraphicsDevice device = devices[i];
			GraphicsConfiguration gc = device.getDefaultConfiguration();
			if (i == 0) {
				// the first is by default the controller screen (no presentation)
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.NONE));
				// backup
				resolution = new Resolution(gc.getBounds().width, gc.getBounds().height);
			} else if (i == 1) {
				// the second is by default the primary presentation screen
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.PRESENTATION));
				// the default presentation resolution
				resolution = new Resolution(gc.getBounds().width, gc.getBounds().height);
			} else if (i == 2) {
				// the third is by default the musician screen
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.MUSICIAN));
			} else {
				// all others are by default nothing
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.NONE));
			}
		}
		
		conf.resolution.set(resolution);
		conf.resolutions.add(resolution);
		
		return conf;
	}

	public Locale getLanguage() {
		return this.language;
	}
	
	public void setLanguage(Locale language) {
		this.savedLanguage.set(language);
	}
	
	@XmlElement(name = "language", required = false)
	@XmlJavaTypeAdapter(value = LocaleXmlAdapter.class)
	private Locale getSavedLanguage() {
		return this.savedLanguage.get();
	}
	
	private void setSavedLanguage(Locale locale) {
		this.savedLanguage.set(locale);
	}

	public String getTheme() {
		return this.theme;
	}
	
	public void setTheme(String theme) {
		this.savedTheme.set(theme);
	}

	@XmlElement(name = "theme", required = false)
	private String getSavedTheme() {
		return this.savedTheme.get();
	}
	
	private void setSavedTheme(String theme) {
		this.savedTheme.set(theme);
	}
	
	public String getThemeCss() {
		String url = Configuration.class.getResource("/org/praisenter/javafx/styles/default.css").toExternalForm();
		// check if a theme is present
		if (this.theme != null && this.theme.length() > 0) {
			URL tUrl = Configuration.class.getResource("/org/praisenter/javafx/styles/" + this.theme + ".css");
			// make sure the theme is found on the source path
			if (tUrl != null) {
				String sUrl = tUrl.toExternalForm();
				if (sUrl != null) {
					return sUrl;
				}
			}
		}
		return url;
	}

	@XmlElementWrapper(name = "screenMappings", required = false)
	@XmlElement(name = "screenMapping", required = false)
	public List<ScreenMapping> getScreenMappings() {
		return this.screenMappings;
	}
	
	public ObservableList<ScreenMapping> screenMappingsProperty() {
		return this.screenMappings;
	}

	@XmlElement(name = "primaryBibleId", required = false)
	public int getPrimaryBibleId() {
		return this.primaryBibleId.get();
	}
	
	public void setPrimaryBibleId(int id) {
		this.primaryBibleId.set(id);
	}
	
	public IntegerProperty primaryBibleIdProperty() {
		return this.primaryBibleId;
	}

	@XmlElement(name = "secondaryBibleId", required = false)
	public int getSecondaryBibleId() {
		return this.secondaryBibleId.get();
	}
	
	public void setSecondaryBibleId(int id) {
		this.secondaryBibleId.set(id);
	}
	
	public IntegerProperty secondaryBibleIdProperty() {
		return this.secondaryBibleId;
	}

	@XmlElement(name = "apocryphaIncluded", required = false)
	public boolean isApocryphaIncluded() {
		return this.apocryphaIncluded.get();
	}
	
	public void setApocryphaIncluded(boolean flag) {
		this.apocryphaIncluded.set(flag);
	}
	
	public BooleanProperty apocryphaIncludedProperty() {
		return this.apocryphaIncluded;
	}

	@XmlElementWrapper(name = "resolutions", required = false)
	@XmlElement(name = "resolution", required = false)
	public List<Resolution> getResolutions() {
		return this.resolutions;
	}
	
	public ObservableList<Resolution> resolutionsProperty() {
		return this.resolutions;
	}

	@XmlElement(name = "resolution", required = false)
	public Resolution getResolution() {
		return this.resolution.get();
	}
	
	public void setResolution(Resolution resolution) {
		this.resolution.set(resolution);
	}
	
	public ObjectProperty<Resolution> resolutionProperty() {
		return this.resolution;
	}
}

