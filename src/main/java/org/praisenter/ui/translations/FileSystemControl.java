package org.praisenter.ui.translations;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;

final class FileSystemControl extends ResourceBundle.Control {
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
		
		// the format can be java.class or java.properties
		// only continue if its java.properties
		if (!format.equals("java.properties")) {
	        return super.newBundle(baseName, locale, format, loader, reload);
	    }

		// create the bundle name using the base name and locale
	    String bundleName = toBundleName(baseName, locale);

	    // remove the package from the bundle name so that all locale
	    // files will be in the format of "messages.properties"
	    int lastPeriod = bundleName.lastIndexOf('.');
	    if (lastPeriod >= 0) {
	        bundleName = bundleName.substring(lastPeriod + 1);
	    }

	    Path path = Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH, bundleName + ".properties");
	    try {
		    // attempt to find the property file in the locales folder
		    if (Files.exists(path) && Files.isRegularFile(path)) {
		    	// if it exists, attempt to load it
		    	// NOTE: selecting UTF-8 here allows the property files to be in the native UTF-8 encoding
			    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(path.toFile()), Charset.forName("UTF-8"))) {
		            return new PropertyResourceBundle(reader);
			    }
		    }
	    } catch (Exception ex) {
	    	LOGGER.warn("Failed to load translation '" + path + "' due to: " + ex.getMessage(), ex);
	    }
	    
	    // perform the default process
		return super.newBundle(baseName, locale, format, loader, reload);
	}
}
