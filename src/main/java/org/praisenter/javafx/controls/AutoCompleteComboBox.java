package org.praisenter.javafx.controls;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class AutoCompleteComboBox<T> extends ComboBox<T> {
	private final AutoCompleteComparator comparator;
	private boolean mutating = false;
	public AutoCompleteComboBox(ObservableList<T> items, AutoCompleteComparator<T> comparator) {
		super(items);
		
		this.getStyleClass().add("auto-complete-combobox");
		
		this.comparator = comparator;
		
		setEditable(true);
//		getEditor().focusedProperty().addListener(obs -> {
//			if (getSelectionModel().getSelectedIndex() < 0) {
//				getEditor().setText(null);
//			}
//		});
		
		addEventHandler(KeyEvent.KEY_RELEASED, (e) -> {
			TextField editor = getEditor();
			String text = editor.getText();
			int start = editor.getCaretPosition();
			if (text == null || text.length() == 0) {
				return;
			}
			if (e.getCode() == KeyCode.BACK_SPACE ||
				e.getCode() == KeyCode.DELETE ||
				e.getCode() == KeyCode.SHIFT ||
				e.getCode() == KeyCode.ALT ||
				e.getCode() == KeyCode.CONTROL ||
				e.getCode() == KeyCode.CAPS) {
				return;
			}
			for (T item : items) {
				if (comparator.matches(text, item)) {
					String newText = item.toString();
					editor.setText(newText);
					editor.commitValue();
					editor.selectRange(start, newText.length());
					break;
				}
			}
		});
		
		setConverter(new StringConverter<T>() {
			@Override
			public T fromString(String string) {
				for (T item : items) {
					if (item.toString().equals(string)) {
						return item;
					}
				}
				return null;
			}
			@Override
			public String toString(T object) {
				if (object != null) {
					return object.toString();
				}
				return null;
			}
		});
		
//		addEventHandler(KeyEvent.KEY_PRESSED, t -> hide());
//        addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
//
//            private boolean moveCaretToPos = false;
//            private int caretPos;
//
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode() == KeyCode.UP) {
//                    caretPos = -1;
//                    moveCaret(getEditor().getText().length());
//                    return;
//                } else if (event.getCode() == KeyCode.DOWN) {
//                    if (!isShowing()) {
//                        show();
//                    }
//                    caretPos = -1;
//                    moveCaret(getEditor().getText().length());
//                    return;
//                } else if (event.getCode() == KeyCode.BACK_SPACE) {
//                    moveCaretToPos = true;
//                    caretPos = getEditor().getCaretPosition();
//                } else if (event.getCode() == KeyCode.DELETE) {
//                    moveCaretToPos = true;
//                    caretPos = getEditor().getCaretPosition();
//                } else if (event.getCode() == KeyCode.ENTER) {
//                    return;
//                }
//
//                if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.getCode().equals(KeyCode.SHIFT) || event.getCode().equals(KeyCode.CONTROL)
//                        || event.isControlDown() || event.getCode() == KeyCode.HOME
//                        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
//                    return;
//                }
//
//                ObservableList<T> list = FXCollections.observableArrayList();
//                for (T aData : items) {
//                    if (aData != null && getEditor().getText() != null && comparator.matches(getEditor().getText(), aData)) {
//                        list.add(aData);
//                    }
//                }
//                String t = getEditor().getText();
//
//                setItems(list);
//                getEditor().setText(t);
//                if (!moveCaretToPos) {
//                    caretPos = -1;
//                }
//                moveCaret(t.length());
//                if (!list.isEmpty()) {
//                    show();
//                }
//            }
//
//            private void moveCaret(int textLength) {
//                if (caretPos == -1) {
//                    getEditor().positionCaret(textLength);
//                } else {
//                    getEditor().positionCaret(caretPos);
//                }
//                moveCaretToPos = false;
//            }
//        });
	}
	
	public T getComboBoxValue(ComboBox<T> comboBox){
        if (comboBox.getSelectionModel().getSelectedIndex() < 0) {
            return null;
        } else {
            return comboBox.getItems().get(comboBox.getSelectionModel().getSelectedIndex());
        }
    }
}