package org.praisenter.ui;

import java.util.Objects;

import org.praisenter.data.bible.Bible;
import org.praisenter.ui.bible.BibleEditorPane;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;

// JAVABUG (L) 09/28/18 [workaround] Focus tab content when tab is selected https://stackoverflow.com/questions/19025268/javafx-tabpane-switch-tabs-only-when-focused/19046535#19046535, https://stackoverflow.com/questions/15646724/javafx-setfocus-after-tabpaine-change/15648609#15648609
// JAVABUG (L) 10/06/18 Tab down button (when lots of tabs) doesn't fully hide when clicked twice https://bugs.openjdk.java.net/browse/JDK-8186176

public final class DocumentEditorPane extends VBox {
	private final ReadOnlyPraisenterContext context;
	
	private final MappedList<DocumentTab, DocumentContext<?>> docToTabMapping;
	
	private final TabPane tabs;
	
	public DocumentEditorPane(ReadOnlyPraisenterContext context) {
		
		this.context = context;
		
		this.docToTabMapping = new MappedList<DocumentTab, DocumentContext<?>>(context.getApplicationState().getOpenDocumentsUnmodifiable(), (item) -> {
			
			if (item == null) {
				throw new NullPointerException("You cannot edit a null object");
			}
			
			final Node node = this.getEditorForDocument(item);
			final DocumentPane<?> editor = node != null && node instanceof DocumentPane ? (DocumentPane<?>)node : null;
			
			if (editor == null) {
				throw new IllegalArgumentException("An object of type " + item.getClass().getName() + " cannot be edited. No editor found.");
			}
			
			// TODO handle * for modified document
			DocumentTab tab = new DocumentTab(item);
			tab.setContent(node);
			
			// check for unsaved changes on close of a tab
			tab.setOnCloseRequest(e -> {
				// TODO check if editor has unsaved changes
				if (editor.hasUnsavedChanges()) {
					// prompt to save first, then remove regardless of what the user chooses
				}
				
				context.getApplicationState().closeDocument(item.getDocument());
				e.consume();
			});
			
			// set the default focus when the tab is selected
			tab.selectedProperty().addListener((obs, ov, nv) -> {
				if (nv) {
					context.getApplicationState().setCurrentDocument(item);
					// https://stackoverflow.com/questions/19025268/javafx-tabpane-switch-tabs-only-when-focused/19046535#19046535
					// https://stackoverflow.com/questions/15646724/javafx-setfocus-after-tabpaine-change/15648609#15648609
//					CompletableFuture.runAsync(() -> {
//						try {
//							Thread.sleep(100);
//						} catch (Exception ex) {}
//					}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
//						editor.setDefaultFocus();
//					}));
				}
			});
			
//			tab.setOnSelectionChanged(e -> {
//				e.consume();
//				context.getApplicationState().setCurrentDocument(item);
//			});
			
			// set the tab name based on the document and whether it's been changed
			tab.textProperty().bind(Bindings.createStringBinding(() -> {
				// TODO check if edit has unsaved changes
				if (editor.hasUnsavedChanges()) {
					return "*" + editor.getDocumentName();
				}
				return editor.getDocumentName();
			}, editor.documentNameProperty(), editor.unsavedChangesProperty()));
			
			return tab;
		});
		
		this.tabs = new TabPane();
		this.tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
//		this.tabs.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
//			if (nv != null) {
//				context.getApplicationState().setCurrentDocument(((DocumentTab)nv).document);
//			} else {
//				context.getApplicationState().setCurrentDocument(null);
//			}
//		});
		
		context.getApplicationState().currentDocumentProperty().addListener((obs, ov, nv) -> {
			System.out.println("Current Document: " + nv);
			for (Tab tab : this.tabs.getTabs()) {
				if (Objects.equals(((DocumentTab)tab).document, nv)) {
					//this.tabs.getSelectionModel().clearSelection();
					this.tabs.getSelectionModel().select(tab);
					Platform.runLater(() -> {
						Node node = tab.getContent();
						((DocumentPane<?>)node).setDefaultFocus();
					});
					break;
				}
			}
		});
				
//		this.tabs.setOnMousePressed(e -> {
//			Node node = this.tabs.getSelectionModel().getSelectedItem().getContent();//.requestFocus()
//			((DocumentPane<?>)node).setDefaultFocus();
//		});

//		this.tabs.focusedProperty().addListener((obs, ov, nv) -> {
//			// focus the selected tab's content
//			Tab t = this.tabs.getSelectionModel().getSelectedItem();
//			// this could be null if there's no open documents
//			if (t != null) {
//				((DocumentPane<?>)t.getContent()).setDefaultFocus();
//			}
//		});
		
		Bindings.bindContent(this.tabs.getTabs(), this.docToTabMapping);
		
		this.getChildren().add(this.tabs);
	}
	
	private Node getEditorForDocument(DocumentContext<?> ctx) {
		if (ctx != null) {
			Object document = ctx.getDocument();
			if (document != null) {
				if (document.getClass() == Bible.class) {
					BibleEditorPane bep = new BibleEditorPane(this.context);
					bep.setDocumentContext((DocumentContext<Bible>)ctx);
					return bep;
				}
			}
		}
		return null;
//		if (ctx != null && ctx.getDocument() != null && ctx.getDocument().getClass() == Bible.class) {
//			BibleEditorPane bep = new BibleEditorPane(this.context);
//			// TODO the problem is that the document context is readonly on the pane, but we want it in the application state for tracking the open documents
//			bep.getDocumentContext().setDocument((Bible)object);
//			return bep;
//		}
//		return null;
	}
	
//	public void addOrFocusDocument(Object document) {
//		// check if the document is already being edited
//		DocumentTab editorTab = null;
//		for (DocumentTab tab : this.docToTabMapping) {
//			if (document.equals(tab.document)) {
//				editorTab = tab;
//			}
//		}
//		if (editorTab != null) {
//			final Tab t = editorTab;
//			this.tabs.getSelectionModel().clearSelection();
//			this.tabs.getSelectionModel().select(editorTab);
//			Platform.runLater(() -> {
//				((DocumentPane<?>)t.getContent()).setDefaultFocus();
//			});
//		} else {
//			this.documents.add(document);
//		}
//	}
	
	private class DocumentTab extends Tab {
		private final DocumentContext<?> document;
		public DocumentTab(DocumentContext<?> document) {
			this.document = document;
		}
	}
}

// - Root
// -- Toolbar
// -- Document editor
// -- listings
// -- global search