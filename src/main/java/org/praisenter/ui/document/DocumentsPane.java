package org.praisenter.ui.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.praisenter.data.Persistable;
import org.praisenter.ui.Action;
import org.praisenter.ui.ActionPane;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.controls.FastScrollPane;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;

// JAVABUG (L) 09/28/18 [workaround] Focus tab content when tab is selected https://stackoverflow.com/questions/19025268/javafx-tabpane-switch-tabs-only-when-focused/19046535#19046535, https://stackoverflow.com/questions/15646724/javafx-setfocus-after-tabpaine-change/15648609#15648609

public final class DocumentsPane extends BorderPane implements ActionPane {
	private static final String DOCUMENTS_PANE_CLASS = "p-documents-pane";
	private static final String DOCUMENTS_PANE_TABS_CLASS = "p-documents-pane-tabs";
	private static final String DOCUMENTS_PANE_SELECTION_EDITOR_CLASS = "p-documents-pane-selection-editor";
	
	private final MappedList<DocumentTab, DocumentContext<? extends Persistable>> documentToTabMapping;
	
	private final TabPane documentTabs;
	private final CurrentDocumentSelectionEditor documentSelectionEditor;
	
	private final ObjectProperty<DocumentEditor<?>> currentDocumentEditor;
	private final ObservableList<Object> documentSelectedItems;
	
	public DocumentsPane(GlobalContext context) {
		this.getStyleClass().add(DOCUMENTS_PANE_CLASS);
		
		this.documentToTabMapping = new MappedList<DocumentTab, DocumentContext<? extends Persistable>>(
				context.getOpenDocumentsUnmodifiable(), 
				(item) -> {
					return new DocumentTab(context, item);
				});

		this.documentTabs = new TabPane();
		this.documentTabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		this.documentTabs.getStyleClass().addAll(DOCUMENTS_PANE_TABS_CLASS);
		
		this.documentSelectionEditor = new CurrentDocumentSelectionEditor(context);
		
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
		
		FastScrollPane scroller = new FastScrollPane(this.documentSelectionEditor, 2.0);
		scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroller.setFitToWidth(true);
		scroller.getStyleClass().addAll(DOCUMENTS_PANE_SELECTION_EDITOR_CLASS);
		
		SplitPane split = new SplitPane(this.documentTabs, scroller);
		split.setDividerPosition(0, 0.7);
		split.visibleProperty().bind(Bindings.size(this.documentTabs.getTabs()).greaterThan(0));
		SplitPane.setResizableWithParent(scroller, false);
		
		this.setCenter(split);
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