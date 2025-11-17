package org.praisenter.ui.pages;

import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.workspace.WorkspaceConfiguration;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Icons;
import org.praisenter.utility.DesktopLauncher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import atlantafx.base.controls.CustomTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
// NOTE: if this gives the "The package netscape.javascript is accessible from more than one module" error
// go to Eclipse build path, expand the jdk, double click "is modular" and remove jdk.jsobject
import netscape.javascript.JSObject;

public final class ManualPage extends BorderPane implements Page {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String MANUAL_PAGE_CLASS = "p-manual-page";
	
	private final StringProperty searchText;
	
	public ManualPage(GlobalContext context) {
		this.searchText = new SimpleStringProperty();
		
		URL url = ManualPage.class.getResource("/org/praisenter/manual/manual.html");
		System.out.println(url);
		String location = "about:blank";
		if (url != null) {
			location = url.toExternalForm();
		}
		
		final String home = location;
		
		WebView webview = new WebView();
		webview.setContextMenuEnabled(false);
		
		// handle app zoom
		context.getWorkspaceConfiguration().applicationFontSizeProperty().addListener((obs, ov, nv) -> {
			double zoom = nv.doubleValue() / WorkspaceConfiguration.DEFAULT_FONT_SIZE;
			webview.setZoom(zoom);
		});
		
		
		var engine = webview.getEngine();
		// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/620.1 (KHTML, like Gecko) JavaFX/25 Version/18.4 Safari/620.1
//		System.out.println(engine.getUserAgent());
		
		// handle external links
		engine.getLoadWorker().stateProperty().addListener((obs, ov, nv) -> {
			if (nv == Worker.State.SUCCEEDED) {
				Document doc = engine.getDocument();
				launchBrowserOnExternalLink(doc);
			}
		});
		
		engine.load(location);
		
		Button btnHome = new Button();
		btnHome.setGraphic(Icons.getIcon(Icons.WEB_HOME));
		btnHome.setOnAction(e -> {
			engine.load(home);
		});
		
		Button btnBack = new Button();
		btnBack.setGraphic(Icons.getIcon(Icons.WEB_BACK));
		btnBack.setOnAction(e -> {
			int index = engine.getHistory().getCurrentIndex();
			if (index > 0) {
				engine.getHistory().go(-1);
			}
		});
		// TODO
//		btnBack.disableProperty().bind(engine.getHistory().currentIndexProperty().greaterThan(0));
		
		Button btnForward = new Button();
		btnForward.setGraphic(Icons.getIcon(Icons.WEB_FORWARD));
		btnForward.setOnAction(e -> {
			int index = engine.getHistory().getCurrentIndex();
			if (index < engine.getHistory().getEntries().size() - 1) {
				engine.getHistory().go(1);
			}
		});
		// TODO
//		btnBack.disableProperty().bind(engine.getHistory().currentIndexProperty().lessThan(engine.getHistory().getEntries().));
		
		Button btnRefresh = new Button();
		btnRefresh.setGraphic(Icons.getIcon(Icons.WEB_REFRESH));
		btnRefresh.setOnAction(e -> {
			engine.reload();
		});
		
		CustomTextField txtLocation = new CustomTextField();
		txtLocation.setEditable(false);
		txtLocation.setLeft(Icons.getIcon(Icons.WEB));
		txtLocation.textProperty().bind(Bindings.createStringBinding(() -> {
			String title = engine.getTitle();
			String loc = engine.getLocation();
			return title + " - " + loc;
		}, engine.titleProperty(), engine.locationProperty()));
		
		CustomTextField txtSearch = new CustomTextField();
		txtSearch.textProperty().bindBidirectional(this.searchText);
		txtSearch.setLeft(Icons.getIcon(Icons.SEARCH));
		txtSearch.setPromptText("search");
		txtSearch.setOnAction(e -> {
			try {
				JSObject window = (JSObject)engine.executeScript("window");
				window.call("find", this.searchText.get(), false, false, false, false, false, false);
			} catch (Exception ex) {
				LOGGER.error("Failed to use the window.find function to find the text '" + this.searchText.get() + "'", ex);
			}
		});
		
		HBox controls = new HBox(5);
		controls.getChildren().addAll(
				btnHome,
				btnBack,
				btnForward,
				btnRefresh,
				txtLocation,
				txtSearch);
		HBox.setHgrow(txtLocation, Priority.ALWAYS);
		controls.setPadding(new Insets(5));
		
		
		this.setCenter(webview);
		this.setTop(controls);
		this.getStyleClass().addAll(MANUAL_PAGE_CLASS);
	}
	
	@Override
	public void setDefaultFocus() {
		// no-op
	}
	
	private void launchBrowserOnExternalLink(Document document) {
		NodeList nodeList = document.getElementsByTagName("a");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node= nodeList.item(i);
            EventTarget eventTarget = (EventTarget) node;
            eventTarget.addEventListener("click", new EventListener()
            {
                @Override
                public void handleEvent(Event evt)
                {
                    EventTarget target = evt.getCurrentTarget();
                    HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                    String href = anchorElement.getHref();
                    System.out.println(href);
                    if (href.startsWith("https://") || href.startsWith("http://")) {
                    	evt.preventDefault();
                    	DesktopLauncher.browse(href);
                    } else {
                    	
                    }
                }
            }, false);
        }
	}
}
