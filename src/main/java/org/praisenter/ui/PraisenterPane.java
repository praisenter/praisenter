package org.praisenter.ui;

import java.nio.file.Paths;
import java.util.Locale;

import org.praisenter.Reference;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.bible.BibleEditorPane;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class PraisenterPane extends BorderPane {
	private final ReadOnlyPraisenterContext context;
	
	public PraisenterPane(ReadOnlyPraisenterContext context) {
		this.context = context;
		
		this.context.getApplicationState().focusOwnerProperty().addListener((obs, ov, nv) -> {
			System.out.println("Focus changed from " + ov + " to " + nv);
		});
		
		BibleEditorPane bibleEditorPane1 = new BibleEditorPane(context);
		BibleEditorPane bibleEditorPane2 = new BibleEditorPane(context);
		
		TabPane tabs = new TabPane();
		tabs.getTabs().add(new Tab("bible1", bibleEditorPane1));
		tabs.getTabs().add(new Tab("bible2", bibleEditorPane2));
		
		Button btn = new Button("import bible");
		btn.setOnAction(e -> {
			context.getDataManager().importData(Paths.get("D:\\Personal\\Praisenter\\data\\bibles\\kjv_apocrypha.zip"), Bible.class);
		});
		Button btn2 = new Button("set bible1");
		btn2.setOnAction(e -> {
			bibleEditorPane1.setBible(context.getDataManager().getItems(Bible.class).get(0).copy());
		});
		Button btn3 = new Button("set bible2");
		btn3.setOnAction(e -> {
			bibleEditorPane2.setBible(context.getDataManager().getItems(Bible.class).get(0).copy());
		});
		Button btn4 = new Button("clear bible1");
		btn4.setOnAction(e -> {
			bibleEditorPane1.setBible(null);
		});
		Button btn5 = new Button("clear bible2");
		btn5.setOnAction(e -> {
			bibleEditorPane2.setBible(null);
		});
		Button btn6 = new Button("set locale");
		btn6.setOnAction(e -> {
			String lang = context.getConfiguration().getLanguageTag();
			String target = "en";
			if ("en-US".equals(lang) || "en".equals(lang)) {
				target = "es";
			}
    		context.getConfiguration().setLanguageTag(target);
		});
		HBox buttons = new HBox(btn, btn2, btn3, btn4, btn5, btn6);
		
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
		
		Button btn8 = new Button("copy");
		btn8.setOnAction(e -> {
//			Book book = new Book();
//			book.setName("Test");
//			book.setNumber(bibleEditorPane1.getBible().getBooks().size());
//			
//			Chapter chapter = new Chapter();
//			chapter.setNumber(1);
//			book.getChapters().add(chapter);
//			
//			Verse verse = new Verse();
//			verse.setNumber(1);
//			verse.setText("The first verse");
//			chapter.getVerses().add(verse);
//			
//			bibleEditorPane1.getBible().getBooks().add(book);
			
			bibleEditorPane1.performAction(Action.COPY);
		});
		
		Button btn10 = new Button("paste");
		btn10.setOnAction(e -> {
			bibleEditorPane1.performAction(Action.PASTE);
		});
		
		Button btn9 = new Button("undo");
		btn9.setOnAction(e -> {
			bibleEditorPane1.undo();
		}); 
		
		buttons.getChildren().addAll(btn8, btn9, btn10);
		
		this.setCenter(tabs);
		this.setBottom(buttons);
		
		EventHandler<Event> eh = e -> {
			System.out.println("Recieved state change from " + this.context.getApplicationState().getApplicationPane());
		};
		this.context.getApplicationState().applicationPaneProperty().addListener((obs, ov, nv) -> {
			eh.handle(null);
			if (ov != null) ov.setOnActionStateChanged(null);
			if (nv != null) nv.setOnActionStateChanged(eh);
		});
	}
	
	
}
