package org.praisenter.javafx.actions;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.bible.Bible;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;

import javafx.stage.Window;

public final class Actions {
	private Actions() {}
	
	// bible
	
	public static final void bibleImport(PraisenterContext context, Window owner, List<Path> paths, Consumer<List<Bible>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		(new BibleImportAction(context, owner, paths, onSuccess, onError)).call();
	}

	public static final void biblePromptImport(PraisenterContext context, Window owner, Consumer<List<Bible>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		(new BiblePromptImportAction(context, owner, onSuccess, onError)).call();
	}
	
	public static final void bibleCopy(PraisenterContext context, Window owner, Bible bible, Consumer<Bible> onSuccess, BiConsumer<Bible, Throwable> onError) {
		(new BibleCopyAction(context, owner, bible, onSuccess, onError)).call();
	}
	
	public static final void biblePromptExport(PraisenterContext context, Window owner, List<Bible> bibles, Consumer<Path> onSuccess, BiConsumer<Path, Throwable> onError) {
		(new BiblePromptExportAction(context, owner, bibles, onSuccess, onError)).call();
	}
	
	public static final void biblePromptDelete(PraisenterContext context, Window owner, List<Bible> bibles, Runnable onSuccess, Consumer<List<FailedOperation<Bible>>> onError) {
		(new BiblePromptDeleteAction(context, owner, bibles, onSuccess, onError)).call();
	}
	
	public static final void biblePromptRename(PraisenterContext context, Window owner, Bible bible, Consumer<Bible> onSuccess, BiConsumer<Bible, Throwable> onError) {
		(new BiblePromptRenameAction(context, owner, bible, onSuccess, onError)).call();
	}
	
	public static final void bibleSave(PraisenterContext context, Window owner, Bible bible, Consumer<Bible> onSuccess, BiConsumer<Bible, Throwable> onError) {
		(new BibleSaveAction(context, owner, bible, onSuccess, onError)).call();
	}
	
	public static final void biblePromptSaveAs(PraisenterContext context, Window owner, Bible bible, Consumer<Bible> onSuccess, BiConsumer<Bible, Throwable> onError) {
		(new BiblePromptSaveAsAction(context, owner, bible, onSuccess, onError)).call();
	}
	
	// media

	public static final void mediaImport(PraisenterContext context, Window owner, List<Path> paths, Consumer<List<Media>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		(new MediaImportAction(context, owner, paths, onSuccess, onError)).call();
	}
	
	public static final void mediaPromptImport(PraisenterContext context, Window owner, Consumer<List<Media>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		(new MediaPromptImportAction(context, owner, onSuccess, onError)).call();
	}

	public static final void mediaPromptDelete(PraisenterContext context, Window owner, List<Media> media, Runnable onSuccess, Consumer<List<FailedOperation<Media>>> onError) {
		(new MediaPromptDeleteAction(context, owner, media, onSuccess, onError)).call();
	}

	public static final void mediaPromptRename(PraisenterContext context, Window owner, Media media, Consumer<Media> onSuccess, BiConsumer<Media, Throwable> onError) {
		(new MediaPromptRenameAction(context, owner, media, onSuccess, onError)).call();
	}
	
	public static final void mediaAddTag(PraisenterContext context, Window owner, Media media, Tag tag, Consumer<Tag> onSuccess, BiConsumer<Tag, Throwable> onError) {
		(new MediaAddTagAction(context, owner, media, tag, onSuccess, onError)).call();
	}
	
	public static final void mediaDeleteTag(PraisenterContext context, Window owner, Media media, Tag tag, Consumer<Tag> onSuccess, BiConsumer<Tag, Throwable> onError) {
		(new MediaAddTagAction(context, owner, media, tag, onSuccess, onError)).call();
	}
}
