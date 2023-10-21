package org.praisenter.ui.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Persistable;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.song.Song;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.BibleSelectionEditor;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.slide.SlideSelectionEditor;
import org.praisenter.ui.song.SongSelectionEditor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public final class CurrentDocumentSelectionEditor extends VBox {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	
	private final ObservableList<DocumentSelectionEditor<?>> selectionEditors;
	private final Map<Class<?>, DocumentSelectionEditor<?>> selectionEditorMapping;
	private final MappedList<Node, DocumentSelectionEditor<?>> mapping;
	
	public CurrentDocumentSelectionEditor(GlobalContext context) {
		this.context = context;
		
		this.selectionEditors = FXCollections.observableArrayList();
		this.selectionEditorMapping = new HashMap<>();
		
		context.currentDocumentProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				// see if its a new type we haven't created a selection editor for
				Class<?> dc = nv.getDocumentClass();
				DocumentSelectionEditor<?> dse = this.selectionEditorMapping.get(dc);
				if (dse == null) {
					DocumentSelectionEditor<?> dse2 = this.createSelectionEditor(this.context, this.context.currentDocumentProperty());
					
					Node node = (Node)dse2;
					node.visibleProperty().bind(dse2.documentContextProperty().isNotNull());
					node.managedProperty().bind(node.visibleProperty());
					
					this.selectionEditors.add(dse2);
					this.selectionEditorMapping.put(dc, dse2);
				}
			}
		});
		
		this.mapping = new MappedList<Node, DocumentSelectionEditor<?>>(this.selectionEditors, (dse) -> {
			Node node = (Node)dse;
			return node;
		});
		
		Bindings.bindContent(this.getChildren(), this.mapping);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Persistable> DocumentSelectionEditor<T> createSelectionEditor(GlobalContext gc, ObjectProperty<DocumentContext<?>> binder) {
		final DocumentContext<?> ctx = binder.get();
		final Class<?> clazz = ctx.getDocumentClass();
		
		DocumentSelectionEditor<T> dse = null;
		if (clazz == Bible.class) {
			dse = (DocumentSelectionEditor<T>)(new BibleSelectionEditor(gc));
		} else if (clazz == Slide.class) {
			dse = (DocumentSelectionEditor<T>)(new SlideSelectionEditor(gc));
		} else if (clazz == Song.class) {
			dse = (DocumentSelectionEditor<T>)(new SongSelectionEditor(gc));
		} else {
			LOGGER.warn("No selection editor for class '" + clazz.getName() + "'.");
			dse = (DocumentSelectionEditor<T>)(new UnknownDocumentSelectionEditor(gc));
		}
		
		dse.documentContextProperty().bind(Bindings.createObjectBinding(() -> {
			DocumentContext<? extends Persistable> current = gc.getCurrentDocument();
			if (current != null && current.getDocumentClass() == clazz) {
				return (DocumentContext<T>)current;
			}
			return null;
		}, gc.currentDocumentProperty()));
		return dse;
	}
}
