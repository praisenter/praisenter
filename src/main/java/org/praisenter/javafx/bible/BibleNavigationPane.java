package org.praisenter.javafx.bible;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleReference;
import org.praisenter.bible.Book;
import org.praisenter.bible.LocatedVerse;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.AutoCompleteComparator;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Setting;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public final class BibleNavigationPane extends BorderPane {
	private ComboBox<BibleListItem> cmbBible;
	private AutoCompleteComboBox<Book> cmbBook;
	private Spinner<Integer> spnChapter;
	private Spinner<Integer> spnVerse;
	
	private Label text;
	
	private ListProperty<BibleReference> selected = new SimpleListProperty<BibleReference>();
	
	// JAVABUG Fixed in Java 9; Editable ComboBox and Spinner auto commit - https://bugs.openjdk.java.net/browse/JDK-8150946
	
	// TODO features: 
	// max chapter/verse number 
	// validation 
	// selection (shift? and ctrl keys)
	// next
	// previous
	// search box (brings up full search window-non-modal)
	// toggle to use secondary translation (or just allow a blank option)
	// template selection
	// add (create slide?)
	// send (not sure here...)
	
	public BibleNavigationPane(PraisenterContext context) {

		this.spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		this.spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		
		this.spnChapter.setEditable(true);
		this.spnVerse.setEditable(true);
		
		this.text = new Label();
		
		// filter the list of selectable bibles by whether they are loaded or not
		ObservableBibleLibrary bl = context.getBibleLibrary();		
		FilteredList<BibleListItem> bibles = new FilteredList<BibleListItem>(context.getBibleLibrary().getItems());
		bibles.setPredicate(b -> b.isLoaded());
		
		UUID backupBible = null;
		if (bibles != null && bibles.size() > 0) {
			backupBible = bibles.get(0).getBible().getId();
		}
		
		UUID id = context.getConfiguration().getUUID(Setting.BIBLE_PRIMARY, null);
		if (id == null) {
			id = backupBible;
		}
		
		Bible bible = null;
		if (id != null) {
			bible = bl.get(id);
			if (bible == null) {
				id = backupBible;
				bible = bl.get(backupBible);
			}
		}
		
		ObservableList<Book> books = FXCollections.observableArrayList();
		List<Book> bb = bible != null ? bible.getBooks() : null;
		if (bb != null) {
			books.addAll(bb);
		}
		
		cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
			public boolean matches(String typedText, Book objectToCompare) {
				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(objectToCompare.getName()).matches()) {
					return true;
				}
				return false;
			}
		});
		
		// TODO should be filtered to only loaded ones
		cmbBible = new ComboBox<BibleListItem>(context.getBibleLibrary().getItems());
		cmbBible.getSelectionModel().select(new BibleListItem(bible));
		cmbBible.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					context.getConfiguration().setUUID(Setting.BIBLE_PRIMARY, nv.getBible().getId());
					books.setAll(nv.getBible().getBooks());
					cmbBook.setValue(null);
				}
				return;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			books.clear();
		});
		
		Button btn = new Button("show value");
		btn.setOnAction((e) -> {
			Book book = cmbBook.valueProperty().get();
			short ch = spnChapter.getValue().shortValue();
			short v = spnVerse.getValue().shortValue();
			if (book != null) {
				try {
					
					LocatedVerse lv = book.getVerse(ch, v);
					if (lv != null) {
						text.setText(lv.getVerse().getText());
					} else {
						text.setText("Verse not found");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		Button next = new Button("next");
		next.setOnAction((e) -> {
			Bible bbl = cmbBible.valueProperty().get().getBible();
			Book book = cmbBook.valueProperty().get();
			short ch = spnChapter.getValue().shortValue();
			short v = spnVerse.getValue().shortValue();
			if (book != null) {
				try {
					LocatedVerse lv = bbl.getNextVerse(book.getNumber(), ch, v);
					if (lv != null) {
						cmbBook.setValue(lv.getBook());
						spnChapter.getValueFactory().setValue(Integer.valueOf(lv.getChapter().getNumber()));
						spnVerse.getValueFactory().setValue(Integer.valueOf(lv.getVerse().getNumber()));
						text.setText(lv.getVerse().getText());
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		HBox row = new HBox(cmbBible, cmbBook, spnChapter, spnVerse, btn, next);
		
		setTop(row);
		setCenter(text);
	}
}
