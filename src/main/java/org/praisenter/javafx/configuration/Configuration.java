package org.praisenter.javafx.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.javafx.styles.Theme;
import org.praisenter.xml.XmlIO;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.stage.Screen;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
public final class Configuration {
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The data storage */
	@XmlElement(name = "settings")
	private final Map<Setting, String> settings = new HashMap<Setting, String>();
	
	// restart required
	
	/** The currently in use language */
	private Locale language;
	
	/** The currently in use theme */
	private Theme theme;
	
	// for listening
	
	private Configuration() {
		this.language = Locale.getDefault();
		this.theme = Theme.DEFAULT;

		// set default language/theme
		this.set(Setting.GENERAL_LANGUAGE, this.language.toLanguageTag());
		this.set(Setting.GENERAL_THEME, this.theme.toString());
		
		// set other defaults
		this.set(Setting.BIBLE_PRIMARY, null);
		this.set(Setting.BIBLE_SECONDARY, null);
		this.set(Setting.BIBLE_SHOW_RENUMBER_WARNING, "true");
	}
	
	public static final Configuration load() {
		Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
		if (Files.exists(path)) {
			try {
				Configuration conf = XmlIO.read(path, Configuration.class);
				conf.language = Locale.forLanguageTag(conf.get(Setting.GENERAL_LANGUAGE));
				conf.theme = Theme.valueOf(conf.get(Setting.GENERAL_THEME));
				if (conf.theme == null) {
					conf.theme = Theme.DEFAULT;
				}
				return conf;
			} catch (Exception ex) {
				LOGGER.info("Failed to load configuration. Using default configuration.", ex);
			}
		} else {
			LOGGER.info("No configuration found. Using default configuration.");
		}
		
		Configuration conf = new Configuration();
		
		// try to save it
		try {
			XmlIO.save(path, conf);
		} catch (Exception ex) {
			LOGGER.warn("Failed to save default configuration.", ex);
		}
		
		return conf;
	}
	
	public static final void save(Configuration configuration) throws JAXBException, IOException {
		Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
		XmlIO.save(path, configuration);
	}
	
	// public interface
	
	public void set(Setting setting, String value) {
		this.settings.put(setting, value);
	}

	public boolean isSet(Setting setting) {
		return this.settings.containsKey(setting) && this.settings.get(setting) != null;
	}
	
	public String get(Setting setting) {
		return this.settings.get(setting);
	}

	public void remove(Setting setting) {
		this.settings.remove(setting);
	}
	
	// helpers
	
	public Locale getLanguage() {
		return this.language;
	}

	public void setLanguage(Locale locale) {
		this.set(Setting.GENERAL_LANGUAGE, locale != null ? locale.toLanguageTag() : Locale.getDefault().toLanguageTag());
	}

	public Theme getTheme() {
		return this.theme;
	}
	
	public void setTheme(Theme theme) {
		this.set(Setting.GENERAL_THEME, theme != null ? theme.toString() : Theme.DEFAULT.toString());
	}
}

