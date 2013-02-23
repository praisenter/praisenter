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
package org.praisenter.application.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;
import org.praisenter.application.resources.Messages;

/**
 * Implementation of a hyperlink listener used to open a link in the
 * user's default browser.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class OpenUrlHyperlinkListener implements HyperlinkListener {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(OpenUrlHyperlinkListener.class);
	
	/* (non-Javadoc)
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		// make sure the hyperlink event is a "onclick"
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			// make sure accessing the desktop is supported
			if (Desktop.isDesktopSupported()) {
				// get the current desktop
				Desktop desktop = Desktop.getDesktop();
				// make sure that browsing is supported
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					// if so then attempt to load the page
					// in the default browser
					try {
						URI uri = e.getURL().toURI();
						desktop.browse(uri);
					} catch (URISyntaxException ex) {
						// this shouldn't happen
						LOGGER.error(MessageFormat.format(Messages.getString("dialog.about.uri.error"), e.getURL()));
					} catch (IOException ex) {
						// this shouldn't happen either since
						// most desktops have a default program to
						// open urls
						LOGGER.error(Messages.getString("dialog.about.navigate.error"));
					}
				}
			}
		}
	}
}
