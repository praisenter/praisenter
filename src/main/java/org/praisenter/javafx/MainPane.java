package org.praisenter.javafx;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
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

public final class MainPane extends BorderPane implements NavigationManager {
	private final PraisenterContext context;
	
	private final MainMenu menu;
	
	private final TreeItem<NavigationItem<Node>> root;
	private final BreadCrumbBar<NavigationItem<Node>> breadcrumb;
	
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
		
		VBox top = new VBox(menu, breadcrumb);
		this.setTop(top);
		
		this.setupPane = new SetupPane(context.getConfiguration());
		this.bibleLibraryPane = new BibleLibraryPane(context);
		this.mediaLibraryPane = new MediaLibraryPane(context, Orientation.HORIZONTAL);
		this.slideLibraryPane = new SlideLibraryPane(context);
		
		this.addEventHandler(ApplicationEvent.ALL, e -> {
			handleApplicationEvent(e.getAction());
		});
	}
	
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
			default:
				// do nothing
				break;
		}
	}
	
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
