package org.praisenter.ui.pages;

import java.net.URL;

import org.praisenter.ui.GlobalContext;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public final class ManualPage extends BorderPane implements Page {
	private static final String MANUAL_PAGE_CLASS = "p-manual-page";
	
	// create a jekyll or other project (maybe just add onto the praisenter one)
	// have it generate HTML in a /help subfolder that can be copied into the resources area
	
	// paths seem to work and are relative
	// would we want to build the site as part of the build too? ...probably
	// should we have an auto-update mechanism for the help?  probably too hard
	
	// basic usage
	// import / export
	// presenting
	// NDI
	// hot keys
	
	public ManualPage(GlobalContext context) {
		URL url = ManualPage.class.getResource("/org/praisenter/manual/index.html");
		System.out.println(url);
		String location = "about:blank";
		if (url != null) {
			location = url.toExternalForm();
		}
		
		WebView webview = new WebView();
		var engine = webview.getEngine();
		engine.load(location);
		
		this.setCenter(webview);
		this.getStyleClass().addAll(MANUAL_PAGE_CLASS);
	}
	
	@Override
	public void setDefaultFocus() {
		// no-op
	}
}
