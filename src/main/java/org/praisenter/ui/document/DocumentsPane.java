package org.praisenter.ui.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.praisenter.data.Persistable;
import org.praisenter.ui.Action;
import org.praisenter.ui.ActionPane;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;

// JAVABUG (L) 09/28/18 [workaround] Focus tab content when tab is selected https://stackoverflow.com/questions/19025268/javafx-tabpane-switch-tabs-only-when-focused/19046535#19046535, https://stackoverflow.com/questions/15646724/javafx-setfocus-after-tabpaine-change/15648609#15648609
// JAVABUG (L) 10/06/18 Tab down button (when lots of tabs) doesn't fully hide when clicked twice https://bugs.openjdk.java.net/browse/JDK-8186176

public final class DocumentsPane extends HBox implements ActionPane {
	private final GlobalContext context;
	
	private final MappedList<DocumentTab, DocumentContext<? extends Persistable>> docToTabMapping;
	
	private final TabPane tabs;
	private final DocumentPropertiesPane properties;
	
	private final ObjectProperty<DocumentEditor<?>> currentEditor;
	private final ObservableList<Object> selectedItems;
	
	public DocumentsPane(GlobalContext context) {
		
		this.context = context;
		
		this.docToTabMapping = new MappedList<DocumentTab, DocumentContext<? extends Persistable>>(
				context.getOpenDocumentsUnmodifiable(), 
				(item) -> {
					return new DocumentTab(context, item);
				});
		
		this.tabs = new TabPane();
		this.tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		this.properties = new DocumentPropertiesPane(context);
		
		this.currentEditor = new SimpleObjectProperty<>();
		this.selectedItems = FXCollections.observableArrayList();
		
		context.currentDocumentProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				for (Tab tab : this.tabs.getTabs()) {
					if (Objects.equals(((DocumentTab)tab).getDocumentContext(), nv)) {
						// since tabs are single select, a simple select does the trick
						this.tabs.getSelectionModel().select(tab);
						break;
					}
				}
			} else {
				this.tabs.getSelectionModel().clearSelection();
			}
		});
		
		// when the tab pane gets focused, move focus to the selected tab's content
		this.tabs.focusedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				// focus the content of the selected tab
				Tab tab = this.tabs.getSelectionModel().getSelectedItem();
				if (tab != null) {
					Node node = tab.getContent();
					if (node != null) {
						if (node instanceof DocumentEditor) {
							((DocumentEditor<?>)node).setDefaultFocus();
						} else {
							node.requestFocus();
						}
					}
				}
			}
		});
		
		Bindings.bindContent(this.tabs.getTabs(), this.docToTabMapping);
		
		this.tabs.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Node node = nv.getContent();
				if (node != null) {
					if (node instanceof DocumentEditor) {
						this.currentEditor.set((DocumentEditor<?>)node);
					}
				}
			} else {
				this.currentEditor.set(null);
			}
		});
		
		this.currentEditor.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.selectedItems, ov.getDocumentContext().getSelectedItems());
			}
			if (nv != null) {
				Bindings.bindContent(this.selectedItems, nv.getDocumentContext().getSelectedItems());
			}
		});
		
		this.getChildren().addAll(this.tabs, this.properties);
	}
	
	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ObservableList<Object> getSelectedItems() {
		return this.selectedItems;
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		// delegate to current document
		DocumentEditor<?> editor = this.currentEditor.get();
		if (editor != null) {
			return editor.isActionEnabled(action);
		}
		return false;
	}
	@Override
	public boolean isActionVisible(Action action) {
		// delegate to current document
		DocumentEditor<?> editor = this.currentEditor.get();
		if (editor != null) {
			return editor.isActionVisible(action);
		}
		return false;
	}
	
	@Override
	public CompletableFuture<Void> executeAction(Action action) {
		if (action == Action.SAVE_ALL) {
			// then pass the save action to all editors
			List<CompletableFuture<Void>> futures = new ArrayList<>();
			for (Tab tab : this.tabs.getTabs()) {
				if (tab instanceof DocumentTab) {
					DocumentTab dt = (DocumentTab)tab;
					DocumentEditor<?> editor = dt.getDocumentEditor();
					DocumentContext<?> context = dt.getDocumentContext();
					if (context.hasUnsavedChanges()) {
						futures.add(editor.executeAction(Action.SAVE));
					}
				}
			}
			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		}
		// delegate to current document
		DocumentEditor<?> editor = this.currentEditor.get();
		if (editor != null) {
			return editor.executeAction(action);
		}
		return null;
	}
	
	@Override
	public void setDefaultFocus() {
		// delegate to current document
		DocumentEditor<?> editor = this.currentEditor.get();
		if (editor != null) {
			editor.setDefaultFocus();
		}
	}
}