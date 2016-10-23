package org.praisenter.javafx;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.javafx.bible.BibleLibraryPane;
import org.praisenter.javafx.media.MediaLibraryPane;
import org.praisenter.javafx.slide.SlideLibraryPane;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public final class MainPane extends BorderPane implements NavigationManager, ApplicationPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	
	private final MainMenu menu;
	
	private final TreeItem<NavigationItem<Node>> root;
	private final BreadCrumbBar<NavigationItem<Node>> breadcrumb;
	private final MainStatusBar status;
	
	private final SetupPane setupPane;
	private final BibleLibraryPane bibleLibraryPane;
	private final MediaLibraryPane mediaLibraryPane;
	private final SlideLibraryPane slideLibraryPane;
	
	private final ObjectProperty<ApplicationPane> focusedPane = new SimpleObjectProperty<ApplicationPane>();
	
	public MainPane(PraisenterContext context) {
		this.context = context;
		
		this.menu = new MainMenu(this);
		this.root = new TreeItem<NavigationItem<Node>>(new NavigationItem<Node>("Home", this));
		this.breadcrumb = new BreadCrumbBar<NavigationItem<Node>>();
		this.breadcrumb.setSelectedCrumb(this.root);
		
		VBox top = new VBox(menu);
		this.setTop(top);
		
		this.status = new MainStatusBar(context);
		this.setBottom(this.status);
		
		this.setupPane = new SetupPane(context.getConfiguration());
		this.bibleLibraryPane = new BibleLibraryPane(context);
		this.mediaLibraryPane = new MediaLibraryPane(context, Orientation.HORIZONTAL);
		this.slideLibraryPane = new SlideLibraryPane(context);
		
		this.addEventHandler(ApplicationEvent.ALL, e -> {
			handleApplicationEvent(e.getAction());
		});
	}
	
	// APPLICATION PANE
	
	public void handleApplicationEvent(ApplicationAction action) {
		switch (action) {
			case PREFERENCES:
				this.navigate("Setup", this.setupPane);
				break;
			case MANAGE_BIBLES:
				this.navigate("Bible Library", this.bibleLibraryPane);
				break;
			case MANAGE_MEDIA:
				this.navigate("Media Library", this.mediaLibraryPane);
				break;
			case MANAGE_SLIDES:
				this.navigate("Slide Library", this.slideLibraryPane);
				break;
			case EXIT:
				this.context.getJavaFXContext().getStage().close();
				break;
			case LOGS:
				// open the log directory
				if (Desktop.isDesktopSupported()) {
				    try {
						Desktop.getDesktop().open(Paths.get(Constants.LOGS_ABSOLUTE_PATH).toFile());
					} catch (IOException ex) {
						LOGGER.error("Unable to open logs directory due to: " + ex.getMessage(), ex);
					}
				}
			default:
				// do nothing
				break;
		}
	}
	
	public boolean isApplicationActionEnabled(ApplicationAction action) {
    	switch (action) {
			case ABOUT:
			case EXIT:
			case IMPORT_BIBLES:
			case IMPORT_SLIDES:
			case IMPORT_SONGS:
			case MANAGE_BIBLES:
			case MANAGE_MEDIA:
			case MANAGE_SLIDES:
			case MANAGE_SONGS:
			case PREFERENCES:
			case LOGS:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isApplicationActionVisible(ApplicationAction action) {
		return true;
	}
	
	// NAVIGATION MANAGER
	
	public void push(String name, Node node) {
		this.push(new NavigationItem<Node>(name, node));
	}
	
	public void push(NavigationItem<Node> item) {
		TreeItem<NavigationItem<Node>> ti = new TreeItem<NavigationItem<Node>>(item);
		this.breadcrumb.getSelectedCrumb().getChildren().add(ti);
		this.breadcrumb.setSelectedCrumb(ti);
		this.setCenter(item.getData());
	}
	
	public NavigationItem<Node> pop() {
		NavigationItem<Node> item = this.breadcrumb.getSelectedCrumb().getValue();
		TreeItem<NavigationItem<Node>> ti = this.breadcrumb.getSelectedCrumb().getParent();
		this.breadcrumb.setSelectedCrumb(ti);
		this.setCenter(ti.getValue().getData());
		return item;
	}
	
	@Override
	public NavigationItem<Node> peek() {
		return this.breadcrumb.getSelectedCrumb().getValue();
	}
	
	@Override
	public void root() {
		this.breadcrumb.setSelectedCrumb(this.root);
		this.setCenter(this.root.getValue().getData());
	}
	
	private void navigate(String name, Node node) {
		this.navigate(new NavigationItem<Node>(name, node));
	}
	
	private void navigate(NavigationItem<Node> item) {
		TreeItem<NavigationItem<Node>> ti = new TreeItem<NavigationItem<Node>>(item);
		this.breadcrumb.setSelectedCrumb(this.root);
		this.breadcrumb.getSelectedCrumb().getChildren().add(ti);
		this.breadcrumb.setSelectedCrumb(ti);
		this.setCenter(item.getData());
	}
}
