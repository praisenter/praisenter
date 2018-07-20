/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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

/**
 * Custom control class to look in a specialized folder for user supplied
 * translations.
 * @author William Bittle
 * @version 3.0.0
 */
final class FileSystemControl extends ResourceBundle.Control {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see java.util.ResourceBundle.Control#newBundle(java.lang.String, java.util.Locale, java.lang.String, java.lang.ClassLoader, boolean)
	 */
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
