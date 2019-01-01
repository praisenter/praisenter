package org.praisenter.ui;

import org.praisenter.data.Persistable;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.configuration.Configuration;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.library.LibraryList;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PraisenterPane extends BorderPane {
	private final GlobalContext context;
	
	private final ObservableList<Persistable> items;
	
	public PraisenterPane(GlobalContext context) {
		this.context = context;
		
//		BibleEditorPane bibleEditorPane1 = new BibleEditorPane(context);
//		BibleEditorPane bibleEditorPane2 = new BibleEditorPane(context);
		
		//NavigationBar bar = new NavigationBar();
		
//		HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(h1);
//		burgerTask.setRate(-1);
//		h1.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
//		    burgerTask.setRate(burgerTask.getRate()*-1);
//		    burgerTask.play();
//		});
		
		Menu mnuFile = new Menu("file", null, new MenuItem("reindex"), new MenuItem("preferences", Glyphs.MENU_PREFERENCES.duplicate()));
		Menu mnuHelp = new Menu("help", null, new MenuItem("logs"), new MenuItem("about", Glyphs.MENU_ABOUT.duplicate()));
		MenuBar mainMenu = new MenuBar(mnuFile, mnuHelp);
		
//		
//		TabPane tabs = new TabPane();
//		tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		// needs to be on the tab itself
//		tabs.addEventHandler(Tab.TAB_CLOSE_REQUEST_EVENT, (e) -> {
//			Alert a = new Alert(AlertType.INFORMATION, "testing");
//			a.showAndWait();
//		});
		// TODO set focus on tab focus
//		tabs.getTabs().add(new Tab("bible1", bibleEditorPane1));
//		tabs.getTabs().add(new Tab("bible2", bibleEditorPane2));
//		
//		Button btn = new Button("import bible");
//		btn.setOnAction(e -> {
//			context.getDataManager().importData(Paths.get("D:\\Personal\\Praisenter\\data\\bibles\\kjv_apocrypha.zip"), Bible.class);
//		});
//		Button btn2 = new Button("set bible1");
//		btn2.setOnAction(e -> {
//			bibleEditorPane1.setBible(context.getDataManager().getItems(Bible.class).get(0).copy());
//		});
//		Button btn3 = new Button("set bible2");
//		btn3.setOnAction(e -> {
//			bibleEditorPane2.setBible(context.getDataManager().getItems(Bible.class).get(0).copy());
//		});
//		Button btn4 = new Button("clear bible1");
//		btn4.setOnAction(e -> {
//			bibleEditorPane1.setBible(null);
//		});
//		Button btn5 = new Button("clear bible2");
//		btn5.setOnAction(e -> {
//			bibleEditorPane2.setBible(null);
//		});
//		Button btn6 = new Button("set locale");
//		btn6.setOnAction(e -> {
//			String lang = context.getConfiguration().getLanguageTag();
//			String target = "en";
//			if ("en-US".equals(lang) || "en".equals(lang)) {
//				target = "es";
//			}
//    		context.getConfiguration().setLanguageTag(target);
//		});
//		HBox buttons = new HBox(btn, btn2, btn3, btn4, btn5, btn6);
		
//		ObservableList<Integer> test = FXCollections.observableArrayList(3, 32, 1, 5, 33, 52);
//		final Reference<Integer> changeNumber = new Reference<>();
//		changeNumber.set(0);
//		test.addListener((ListChangeListener.Change<? extends Integer> change) -> {
//			System.out.println("Change Number: " + changeNumber.get());
//			while (change.next()) {
//				if (change.wasPermutated()) {
//					for (int oldIndex = change.getFrom(); oldIndex < change.getTo(); oldIndex++) {
//						System.out.println("Moved " + oldIndex + " to " + change.getPermutation(oldIndex));
//					}
//				} else if (change.wasUpdated()) {
//					System.out.println("Updated");
//				} else {
//					System.out.println("Removed: " + change.getRemovedSize());
//					for (Integer removed : change.getRemoved()) {
//						System.out.println("Removed " + removed + " => " + change.getFrom() + " " + change.getTo());
//					}
//					for (Integer added: change.getAddedSubList()) {
//						System.out.println("Added " + added);
//					}
//				}
//			}
//			changeNumber.set(changeNumber.get() + 1);
//		});
//		
//		Button btn7 = new Button("test list");
//		final Reference<Integer> i = new Reference<>();
//		i.set(0);
//		btn7.setOnAction(e -> {
//			if (i.get() == 0) {
//				// do an add
//				test.add((int)(10 * Math.random()));
//			} else if (i.get() == 1) { 
//				// do a remove
//				test.removeAll(3, 5);
//				FXCollections.sort(test);
//			} else if (i.get() == 2) {
//				// add multiple
//				test.addAll((int)(10 * Math.random()), (int)(10 * Math.random()));
//			} else if (i.get() == 3) {
//				// move them around
//				FXCollections.sort(test);
//			} else if (i.get() == 4) {
//				// remove consecutive
//				test.removeAll(32, 33, 52);
//			}
//			i.set(i.get() + 1);
//			if (i.get() >= 5) {
//				i.set(0);
//			}
//		});
//		
//		buttons.getChildren().add(btn7);
		
//		Button btn8 = new Button("copy");
//		btn8.setOnAction(e -> {
////			Book book = new Book();
////			book.setName("Test");
////			book.setNumber(bibleEditorPane1.getBible().getBooks().size());
////			
////			Chapter chapter = new Chapter();
////			chapter.setNumber(1);
////			book.getChapters().add(chapter);
////			
////			Verse verse = new Verse();
////			verse.setNumber(1);
////			verse.setText("The first verse");
////			chapter.getVerses().add(verse);
////			
////			bibleEditorPane1.getBible().getBooks().add(book);
//			
//			bibleEditorPane1.performAction(Action.COPY);
//		});
//		
//		Button btn10 = new Button("paste");
//		btn10.setOnAction(e -> {
//			bibleEditorPane1.performAction(Action.PASTE);
//		});
//		
//		buttons.getChildren().addAll(btn8, btn10);

		ActionBar ab = new ActionBar(context);
		DocumentsPane dep = new DocumentsPane(context);
//		ContextPropertiesPane cpp = new ContextPropertiesPane(context);
		
//		FlowListView<Bible> bblListing = new FlowListView<>(Orientation.HORIZONTAL, (bible) -> {
//			return new BibleListCell(bible);
//		});
		
//		LibraryList bblListing = new LibraryList(context);
//		Bindings.bindContent(bblListing.getItems(), context.getDataManager().getItemsUnmodifiable(Bible.class));
		
		this.items = new FilteredList<>(context.getDataManager().getItemsUnmodifiable(), (i) -> {
			if (i instanceof Configuration) {
				return false;
			}
			return true;
		});
		
		LibraryList itemListing = new LibraryList(context);
		Bindings.bindContent(itemListing.getItems(), this.items);
		
		//BorderPane bp = new BorderPane();
		this.setTop(mainMenu);
		this.setCenter(new VBox(5, itemListing, dep));
		this.setLeft(ab);
//		this.setRight(cpp);
		
		VBox.setVgrow(dep, Priority.ALWAYS);

		Spinner<Integer> spnIndex = new Spinner<>(0,5,0);
		Button btnLoadDocument = new Button("Print Undo State");
		btnLoadDocument.setOnAction(e -> {
			DocumentContext<?> ctx = context.getCurrentDocument();
			if (ctx != null) {
				ctx.getUndoManager().print();
			}
		});
		HBox buttons = new HBox(5, spnIndex, btnLoadDocument);
		
		ProgressBar progress = new ProgressBar();
		progress.visibleProperty().bind(context.taskExecutingProperty());
		
		this.setBottom(new HBox(5, buttons, progress));
		
		//this.getChildren().addAll(bp);

//		AnchorPane.setLeftAnchor(ab, 0.0);
//		AnchorPane.setTopAnchor(ab, 0.0);
//		AnchorPane.setBottomAnchor(ab, 0.0);
//		
//		AnchorPane.setLeftAnchor(bp, 150.0);
//		AnchorPane.setTopAnchor(bp, 0.0);
//		AnchorPane.setBottomAnchor(bp, 0.0);
//		AnchorPane.setRightAnchor(bp, 0.0);

//		EventHandler<Event> eh = e -> {
//			System.out.println("Recieved state change from " + this.context.getApplicationState().getApplicationPane());
//		};
//		this.context.getApplicationState().actionPaneProperty().addListener((obs, ov, nv) -> {
//			eh.handle(null);
//			if (ov != null) ov.setOnActionStateChanged(null);
//			if (nv != null) nv.setOnActionStateChanged(eh);
//		});
	}
	
	
}
