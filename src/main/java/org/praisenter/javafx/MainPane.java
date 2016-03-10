package org.praisenter.javafx;

import java.awt.MenuItem;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public final class MainPane extends BorderPane {
	
	public MainPane() {
		this.setTop(createMenus());
	}
	
	// TODO menu options
	private MenuBar createMenus() {
		MenuBar menu = new MenuBar();
		
		Menu file = new Menu("File");
		Menu media = new Menu("Media");
		Menu songs = new Menu("Songs");
		Menu bibles = new Menu("Bibles");
		Menu slides = new Menu("Slides");
		Menu help = new Menu("Help");
		
		menu.getMenus().addAll(file, media, songs, bibles, slides, help);
		
		MenuItem fSetup = new MenuItem();
		
		MenuItem mImport = new MenuItem();
		MenuItem mManage = new MenuItem();
		// maybe...
		MenuItem mTranscode = new MenuItem();
		
		// add/edit
		MenuItem soNew = new MenuItem();
		MenuItem soImport = new MenuItem();

		MenuItem slNew = new MenuItem();
		
		MenuItem hAbout = new MenuItem();
		
		return menu;
	}
	
	// TODO slide show pane
	// TODO song pane
	// TODO bible pane
	// TODO slide pane
	
}
