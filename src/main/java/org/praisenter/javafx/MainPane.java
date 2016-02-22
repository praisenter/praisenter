package org.praisenter.javafx;

import java.awt.MenuItem;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public final class MainPane extends BorderPane {
	
	public MainPane() {
		
	}
	
	// TODO menu options
	private MenuBar createMenus() {
		MenuBar menu = new MenuBar();
		
		Menu file = new Menu();
		Menu media = new Menu();
		Menu songs = new Menu();
		Menu bibles = new Menu();
		Menu slides = new Menu();
		Menu help = new Menu();
		
		MenuItem fSetup = new MenuItem();
		
		MenuItem mImport = new MenuItem();
		MenuItem mManage = new MenuItem();
		
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
