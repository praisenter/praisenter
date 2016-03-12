package org.praisenter.javafx;

import java.util.Properties;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public final class MainPane extends BorderPane {
	
	final PraisenterContext context;
	final Properties config;
	
	public MainPane(PraisenterContext context, Properties config) {
		this.context = context;
		this.config = config;
		
		this.setTop(createMenus());
	}
	
	// TODO menu options
	// TODO translate
	private MenuBar createMenus() {
		MenuBar menu = new MenuBar();
		
		Menu file = new Menu("File");
		Menu media = new Menu("Media");
		Menu songs = new Menu("Songs");
		Menu bibles = new Menu("Bibles");
		Menu slides = new Menu("Slides");
		Menu help = new Menu("Help");
		
		menu.getMenus().addAll(file, media, songs, bibles, slides, help);
		
		MenuItem fSetup = new MenuItem("Setup");
		file.getItems().add(fSetup);
		
		MenuItem mImport = new MenuItem("Import");
		MenuItem mManage = new MenuItem("Manage");
		// maybe...
		MenuItem mTranscode = new MenuItem("Transcode");
		media.getItems().addAll(mManage, mImport);
		
		// add/edit
		MenuItem soNew = new MenuItem("New song");
		MenuItem soImport = new MenuItem("Import songs");
		// manage
		songs.getItems().addAll(soNew, soImport);

		MenuItem slNew = new MenuItem("New slide");
		slides.getItems().addAll(slNew);
		
		MenuItem hAbout = new MenuItem("About");
		help.getItems().addAll(hAbout);
		
		return menu;
	}
	
	// TODO slide show pane
	// TODO song pane
	// TODO bible pane
	// TODO slide pane
	
}
