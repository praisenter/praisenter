package org.praisenter.ui.document;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.Persistable;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.BibleEditor;
import org.praisenter.ui.bible.SelectedBibleItemEditor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;

public class EditorRegistrations {
	static final Map<Class<?>, BiFunction<GlobalContext, DocumentContext<?>, DocumentEditor<?>>> EDITORS;
	static final Map<Class<?>, Function<GlobalContext, DocumentSelectionEditor<?>>> SELECTION_EDITORS;
	
	static {
		EDITORS = new HashMap<>();
		SELECTION_EDITORS = new HashMap<>();
		
		register(Bible.class, (context, documentContext) -> {
			return new BibleEditor(context, documentContext);
		});
		
		register(Bible.class, (context) -> {
			return new SelectedBibleItemEditor(context);
		});
	}
	
	public static <T extends Persistable> void register(Class<T> clazz, BiFunction<GlobalContext, DocumentContext<T>, DocumentEditor<T>> fn) { 
		EDITORS.put(clazz, (context, documentContext) -> {
			return fn.apply(context, (DocumentContext<T>)documentContext);
		});
	}
	
	public static <T extends Persistable> void register(Class<T> clazz, Function<GlobalContext, DocumentSelectionEditor<T>> fn) { 
		SELECTION_EDITORS.put(clazz, (context) -> {
			return fn.apply(context);
		});
	}
	
	public static <T extends Persistable> DocumentEditor<T> createEditor(GlobalContext gc, DocumentContext<T> dc) {
		Class<T> clazz = dc.getDocumentClass();
		BiFunction<GlobalContext, DocumentContext<?>, DocumentEditor<?>> fn = EDITORS.get(clazz);
		return (DocumentEditor<T>) fn.apply(gc, dc);
	}
	
	public static <T extends Persistable> DocumentSelectionEditor<T> createSelectionEditor(GlobalContext gc, ObjectProperty<DocumentContext<?>> binder) {
		final DocumentContext<?> ctx = binder.get();
		final Class<?> clazz = ctx.getDocumentClass();
		//Class<T> clazz = dc.getDocumentClass();
		Function<GlobalContext, DocumentSelectionEditor<?>> fn = SELECTION_EDITORS.get(clazz);
		DocumentSelectionEditor<T> ed = (DocumentSelectionEditor<T>) fn.apply(gc);
		ed.documentContextProperty().bind(Bindings.createObjectBinding(() -> {
			DocumentContext<?> ctx2 = binder.get();
			Class<?> clazz2 = ctx.getDocumentClass();
			if (clazz2 == clazz) {
				return (DocumentContext<T>)ctx2;
			}
			return null;
		}, binder));
		return ed;
	}
}
