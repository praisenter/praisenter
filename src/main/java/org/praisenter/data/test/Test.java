package org.praisenter.data.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.praisenter.data.DataManager;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BiblePersistAdapter;
import org.praisenter.data.bible.BibleSearchCriteria;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaConfiguration;
import org.praisenter.data.media.MediaPersistAdapter;
import org.praisenter.data.search.SearchIndex;
import org.praisenter.data.search.SearchResult;
import org.praisenter.data.search.SearchType;

import javafx.application.Application;
import javafx.stage.Stage;

public class Test extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Path base = Paths.get("C:\\Users\\wbittle\\Desktop\\test\\base");
		Path path = Paths.get("C:\\Users\\wbittle\\Desktop\\test\\media\\IMG_2718.JPG");
		
		FSDirectory directory = FSDirectory.open(base.resolve("index"));
		Analyzer analyzer = new StandardAnalyzer(new CharArraySet(1, false));
		
		MediaConfiguration config = new MediaConfiguration() {
			
			@Override
			public boolean isVideoTranscodingEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isAudioTranscodingEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String getVideoTranscodeExtension() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getVideoTranscodeCommand() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getVideoFrameExtractCommand() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getThumbnailWidth() {
				// TODO Auto-generated method stub
				return 100;
			}
			
			@Override
			public int getThumbnailHeight() {
				// TODO Auto-generated method stub
				return 100;
			}
			
			@Override
			public String getAudioTranscodeExtension() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getAudioTranscodeCommand() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		SearchIndex index = new SearchIndex(directory, analyzer);
		
		DataManager dm = new DataManager(index);
		
		CompletableFuture.allOf(
			dm.registerPersistAdapter(Media.class, new MediaPersistAdapter(base.resolve("media"), config)), 
			dm.registerPersistAdapter(Bible.class, new BiblePersistAdapter(base.resolve("bibles"))))
		.thenCompose((ignore) -> {
			//return dm.search(new SearchCriteria("called chosen", SearchType.ALL_WORDS, 10));
			return dm.search(new BibleSearchCriteria("called chosen", SearchType.ALL_WORDS, 10, UUID.fromString("b464cb41-845b-4869-ad48-ec58b28d2df8")));
			//return dm.importData(Bible.class, Paths.get("C:\\Users\\wbittle\\Desktop\\test\\DifferentBibleFormatsTest.zip"));
		}).handle((result, ex) -> {
			if (result != null) {
				for (SearchResult res : result) {
					int bookNumber = res.getDocument().getField(Bible.FIELD_BOOK_NUMBER).numericValue().intValue();
					int chapterNumber = res.getDocument().getField(Bible.FIELD_VERSE_CHAPTER).numericValue().intValue();
					int verseNumber = res.getDocument().getField(Bible.FIELD_VERSE_NUMBER).numericValue().intValue();
					System.out.println(res.getMatchDataType() + ":" + res.getMatchId() + " " + res.getScore() + " " + bookNumber + " " + chapterNumber + ":" + verseNumber + " " + res.getMatchText());
				}
//				for (String warning : result.getWarnings()) {
//					System.out.println("WARNING: " + warning);
//				}
//				for (Exception err : result.getErrors()) {
//					System.out.println("ERROR: " + err.getMessage());
//				}
			}
			
			if (ex != null) {
				System.out.println(ex.getMessage());
			} else {
				System.out.println("Success");
			}
			return null;
		});
		
		
//		PersistentStore<Media> store = new PersistentStore<>(new MediaPersistAdapter(base.resolve("media"), config), index);
//		PersistentStore<Bible> bStore = new PersistentStore<>(new BiblePersistAdapter(base.resolve("bibles")), index);
		
//		store
//		.initialize()
//		.thenRun(() -> {
//			store.importData(Paths.get("C:\\Users\\wbittle\\Desktop\\test\\test-praisenter-import.zip"));
////		.thenComposeAsync((a) -> {
////			return store.importData(path);
////		}).thenAccept((result) -> {
////			try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(Paths.get("C:\\Users\\wbittle\\Desktop\\test\\test2.zip").toFile())))) {
////				store.exportData(Constants.FORMAT_NAME, zos, store.getItems());
////			} catch (Exception ex) {
////				throw new CompletionException(ex);
////			}
//		}).handle((result, ex) -> {
//			System.out.println(ex.getMessage());
//			return null;
//		});
//		
//		dm.
//		.initialize()
//		.thenCompose((result) -> { 
//			return bStore.importData(Paths.get("C:\\Users\\wbittle\\Desktop\\test\\DifferentBibleFormatsTest.zip"));
//		}).handle((result, ex) -> {
//			if (ex != null) {
//				System.out.println(ex.getMessage());
//			} else {
//				System.out.println("Success");
//			}
//			return null;
//		});
	}
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
