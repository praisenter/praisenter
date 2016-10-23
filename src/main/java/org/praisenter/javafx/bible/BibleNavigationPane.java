package org.praisenter.javafx.bible;

import java.sql.SQLException;

import org.praisenter.bible.Book;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.PraisenterContext;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;

public final class BibleNavigationPane extends BorderPane {
	private ComboBox<BibleListItem> cmbBible;
	private AutoCompleteComboBox<Book> cmbBook;
	private Spinner<Integer> spnChapter;
	private Spinner<Integer> spnVerse;
	
	public BibleNavigationPane(PraisenterContext context) throws SQLException {

		spnChapter = new Spinner<>(1, 1000, 1, 1);
		spnVerse = new Spinner<>(1, 1000, 1, 1);
		
		
		ObservableBibleLibrary bl = context.getBibleLibrary();
		
//		int backupBible = -1;
//		List<Bible> bibles = bl.getBibles();
//		if (bibles != null && bibles.size() > 0) {
//			backupBible = bibles.get(0).getId();
//		}
//		
//		int id = context.getConfiguration().getPrimaryBibleId();
//		if (id < 0) {
//			id = backupBible;
//		}
//		Bible bible = bl.getBible(id);
//		
//		if (bible == null) {
//			bible = bl.getBible(backupBible);
//			if (bible == null) {
//				throw new RuntimeException();
//			}
//		}
//		
//		ObservableList<Book> books = FXCollections.observableArrayList();
//		List<Book> bb = bl.getBooks(bible);
//		books.addAll(bb);
//		
//		cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
//			public boolean matches(String typedText, Book objectToCompare) {
//				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
//				if (pattern.matcher(objectToCompare.getName()).matches()) {
//					return true;
//				}
//				return false;
//			}
//		});
//		
//		Button btn = new Button("show value");
//		btn.setOnAction((e) -> {
//			Bible bbl = cmbBible.getValue().bible;
//			Book book = cmbBook.valueProperty().get();
//			int ch = spnChapter.getValue();
//			int v = spnVerse.getValue();
//			if (book != null) {
//				try {
//					Verse verse = bl.getVerse(bbl, book.getCode(), ch, v);
//					Alert alert = new Alert(AlertType.INFORMATION);
//					alert.setContentText(verse.getText());
//					alert.show();
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		});
//		
//		cmbBible = new ComboBox<BibleListItem>(context.getBibleLibrary().getItems());
//		cmbBible.getSelectionModel().select(new BibleListItem(bible));
//		cmbBible.valueProperty().addListener((obs, ov, nv) -> {
//			books.clear();
//			try {
//				books.addAll(bl.getBooks(nv.bible));
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		});
//		
//		HBox row = new HBox(cmbBible, cmbBook, spnChapter, spnVerse);
//		
//		setCenter(row);
//		setBottom(btn);
	}
}
