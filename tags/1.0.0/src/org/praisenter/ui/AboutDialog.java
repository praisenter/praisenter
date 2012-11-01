package org.praisenter.ui;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.praisenter.Version;
import org.praisenter.resources.Messages;

/**
 * Dialog showing the about information.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class AboutDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = 1871724293655898430L;

	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private AboutDialog(Window owner) {
		super(owner, Messages.getString("dialog.about.title"), ModalityType.APPLICATION_MODAL);
		// set the size
		this.setPreferredSize(new Dimension(450, 500));
		
		Container container = this.getContentPane();
		
		// set the layout
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// add the logo to the top
		JLabel icon = new JLabel();
		icon.setText(MessageFormat.format(Messages.getString("dialog.about.text"), Version.getVersion()));
		
		// add the about text section with clickable links
		JTextPane text = new JTextPane();
		text.setEditable(false);
		try {
			text.setPage(this.getClass().getResource(Messages.getString("dialog.about.html")));
		} catch (IOException e) {
			// if the file is not found then just set the text to empty
			text.setText(Messages.getString("dialog.about.html.error"));
		}
		// add a hyperlink listener to open links in the default browser
		text.addHyperlinkListener(new HyperlinkListener() {
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
								System.err.println(MessageFormat.format(Messages.getString("dialog.about.uri.error"), e.getURL()));
							} catch (IOException ex) {
								// this shouldn't happen either since
								// most desktops have a default program to
								// open urls
								System.err.println(Messages.getString("dialog.about.navigate.error"));
							}
						}
					}
				}
			}
		});
		// wrap the text pane in a scroll pane just in case
		JScrollPane scroller = new JScrollPane(text);
		
		container.add(icon);
		container.add(scroller);
		
		this.pack();
	}
	
	/**
	 * Shows the about dialog.
	 * @param owner the dialog owner
	 */
	public static final void show(Window owner) {
		// create the dialog
		AboutDialog dialog = new AboutDialog(owner);
		dialog.setLocationRelativeTo(owner);
		// show the dialog
		dialog.setVisible(true);
	}
}
