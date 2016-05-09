package org.praisenter.javafx;

import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import org.praisenter.javafx.bible.BibleLibraryPane;
import org.praisenter.javafx.media.MediaLibraryPane;

public final class MainPane extends BorderPane {
	
	final PraisenterContext context;
	
	public MainPane(PraisenterContext context) {
		this.context = context;
		
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
		
		MenuItem mManage = new MenuItem("Manage media");
		MenuItem mImport = new MenuItem("Import media");
		
		// maybe...
		MenuItem mTranscode = new MenuItem("Transcode");
		media.getItems().addAll(mManage, mImport);
		
		// add/edit
		MenuItem soManage = new MenuItem("Manage songs");
		MenuItem soImport = new MenuItem("Import songs");
		MenuItem soNew = new MenuItem("Create a new song");
		// manage
		songs.getItems().addAll(soManage, soImport, soNew);

		MenuItem slManage = new MenuItem("Manage slides");
		MenuItem slNew = new MenuItem("Create a new slide");
		slides.getItems().addAll(slManage, slNew);
		
		MenuItem blManage = new MenuItem("Manage Bibles");
		bibles.getItems().addAll(blManage);
		
		MenuItem hAbout = new MenuItem("About");
		help.getItems().addAll(hAbout);
		
		// menu actions

		fSetup.setOnAction((e) -> {
			SetupPane sp = new SetupPane(context.getConfiguration());
			setCenter(sp);
		});
		
		blManage.setOnAction((e) -> {
			BibleLibraryPane blp = new BibleLibraryPane(context.getBibleLibrary());
			setCenter(blp);
		});
		
		mManage.setOnAction((e) -> {
			MediaLibraryPane mlp = new MediaLibraryPane(context.getMediaLibrary(), Orientation.HORIZONTAL, context.getTags());
			setCenter(mlp);
		});
		
		return menu;
	}
	
	// TODO slide show pane
	// TODO song pane
	// TODO bible pane
	// TODO slide pane
	
}
