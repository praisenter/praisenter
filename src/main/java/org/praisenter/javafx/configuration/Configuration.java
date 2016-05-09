package org.praisenter.javafx.configuration;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	
	@XmlElement(name = "language", required = false)
	@XmlJavaTypeAdapter(value = LocaleXmlAdapter.class)
	private Locale language;
	
	@XmlElement(name = "theme", required = false)
	private String theme;
	
	@XmlElementWrapper(name = "screenMappings", required = false)
	@XmlElement(name = "screenMapping", required = false)
	private final List<ScreenMapping> screenMappings;
	
	public Configuration() {
		this.screenMappings = new ArrayList<ScreenMapping>();
	}
	
	public static final Configuration load() {
		try {
			Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
			if (Files.exists(path)) {
				return XmlIO.read(path, Configuration.class);
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
		
		// default screens
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();
		for (int i = 0; i < devices.length; i++) {
			GraphicsDevice device = devices[i];
			if (i == 0) {
				// the first is by default the controller screen (no presentation)
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.NONE));
			} else if (i == 1) {
				// the second is by default the primary presentation screen
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.PRESENTATION));
			} else if (i == 2) {
				// the third is by default the musician screen
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.MUSICIAN));
			} else {
				// all others are by default nothing
				conf.screenMappings.add(new ScreenMapping(device.getIDstring(), ScreenRole.NONE));
			}
		}
		
		return conf;
	}

	public Locale getLanguage() {
		return this.language;
	}
	
	public void setLanguage(Locale language) {
		this.language = language;
	}

	public String getTheme() {
		return this.theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public List<ScreenMapping> getScreenMappings() {
		return this.screenMappings;
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
}

