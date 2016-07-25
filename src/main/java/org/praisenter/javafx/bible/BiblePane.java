package org.praisenter.javafx.bible;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.Book;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.AutoCompleteComparator;
import org.praisenter.javafx.PraisenterContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public final class BiblePane extends BorderPane {
	private AutoCompleteComboBox<Book> cmbBook;
	
	public BiblePane(PraisenterContext context) throws SQLException {
		BibleLibrary bl = context.getBibleLibrary();
		
		int backupBible = -1;
		List<Bible> bibles = bl.getBibles();
		if (bibles != null && bibles.size() > 0) {
			backupBible = bibles.get(0).getId();
		}
		
		int id = context.getConfiguration().getPrimaryBibleId();
		if (id < 0) {
			id = backupBible;
		}
		Bible bible = bl.getBible(id);
		
		if (bible == null) {
			bible = bl.getBible(backupBible);
			if (bible == null) {
				throw new RuntimeException();
			}
		}
		
		ObservableList<Book> books = FXCollections.observableArrayList();
		List<Book> bb = bl.getBooks(bible);
		books.addAll(bb);
		
		cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
			public boolean matches(String typedText, Book objectToCompare) {
				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(objectToCompare.getName()).matches()) {
					return true;
				}
				return false;
			}
		});
		
		Button btn = new Button("show value");
		btn.setOnAction((e) -> {
			Book book = cmbBook.getSelectionModel().getSelectedItem();
			if (book != null) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText(book.getName());
				alert.show();
			}
		});
		
		setCenter(cmbBook);
		setBottom(btn);
	}
}
