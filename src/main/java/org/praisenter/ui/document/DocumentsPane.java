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

import javafx.application.Platform;
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
import javafx.scene.layout.Priority;

// JAVABUG (L) 09/28/18 [workaround] Focus tab content when tab is selected https://stackoverflow.com/questions/19025268/javafx-tabpane-switch-tabs-only-when-focused/19046535#19046535, https://stackoverflow.com/questions/15646724/javafx-setfocus-after-tabpaine-change/15648609#15648609
// JAVABUG (L) 10/06/18 Tab down button (when lots of tabs) doesn't fully hide when clicked twice https://bugs.openjdk.java.net/browse/JDK-8186176

// TODO use split pane

public final class DocumentsPane extends HBox implements ActionPane {
	private final MappedList<DocumentTab, DocumentContext<? extends Persistable>> documentToTabMapping;
	
	private final TabPane documentTabs;
	private final CurrentDocumentSelectionEditor documentSelectionEditor;
	
	private final ObjectProperty<DocumentEditor<?>> currentDocumentEditor;
	private final ObservableList<Object> documentSelectedItems;

	public DocumentsPane(GlobalContext context) {
		this.documentToTabMapping = new MappedList<DocumentTab, DocumentContext<? extends Persistable>>(
				context.getOpenDocumentsUnmodifiable(), 
				(item) -> {
					return new DocumentTab(context, item);
				});
		
		this.documentTabs = new TabPane();
		this.documentTabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		this.documentSelectionEditor = new CurrentDocumentSelectionEditor(context);
		this.documentSelectionEditor.setMaxWidth(250);
		this.documentSelectionEditor.setMinWidth(250);
		this.documentSelectionEditor.setPrefWidth(250);
		
		this.currentDocumentEditor = new SimpleObjectProperty<>();
		this.documentSelectedItems = FXCollections.observableArrayList();
		
		context.currentDocumentProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				for (Tab tab : this.documentTabs.getTabs()) {
					DocumentTab dt = ((DocumentTab)tab);
					if (Objects.equals(dt.getDocumentContext(), nv)) {
						// since tabs are single select, a simple select does the trick
						this.documentTabs.getSelectionModel().select(tab);
						// set the focus to the editor (for some reason this required the runLater... probably 
						// because of the tab selection)
						Platform.runLater(() -> {
							dt.getDocumentEditor().setDefaultFocus();
						});
						break;
					}
				}
			} else {
				this.documentTabs.getSelectionModel().clearSelection();
			}
		});
		
		// when the tab pane gets focused, move focus to the selected tab's content
		this.documentTabs.focusedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				// focus the content of the selected tab
				Tab tab = this.documentTabs.getSelectionModel().getSelectedItem();
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
		
		Bindings.bindContent(this.documentTabs.getTabs(), this.documentToTabMapping);
		
		this.documentTabs.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Node node = nv.getContent();
				if (node != null) {
					if (node instanceof DocumentEditor) {
						this.currentDocumentEditor.set((DocumentEditor<?>)node);
					}
				}
			} else {
				this.currentDocumentEditor.set(null);
			}
		});
		
		this.currentDocumentEditor.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.documentSelectedItems, ov.getDocumentContext().getSelectedItems());
			}
			if (nv != null) {
				Bindings.bindContent(this.documentSelectedItems, nv.getDocumentContext().getSelectedItems());
			}
		});
		
		this.getChildren().addAll(this.documentTabs, this.documentSelectionEditor);
		
		HBox.setHgrow(this.documentTabs, Priority.ALWAYS);
	}
	
	@Override
	public void cleanUp() {
		
	}
	
	@Override
	public ObservableList<Object> getSelectedItems() {
		return this.documentSelectedItems;
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		// delegate to current document
		DocumentEditor<?> editor = this.currentDocumentEditor.get();
		if (editor != null) {
			return editor.isActionEnabled(action);
		}
		return false;
	}
	@Override
	public boolean isActionVisible(Action action) {
		// delegate to current document
		DocumentEditor<?> editor = this.currentDocumentEditor.get();
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
			for (Tab tab : this.documentTabs.getTabs()) {
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
		DocumentEditor<?> editor = this.currentDocumentEditor.get();
		if (editor != null) {
			return editor.executeAction(action);
		}
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public void setDefaultFocus() {
		// delegate to current document
		DocumentEditor<?> editor = this.currentDocumentEditor.get();
		if (editor != null) {
			editor.setDefaultFocus();
		}
	}
}