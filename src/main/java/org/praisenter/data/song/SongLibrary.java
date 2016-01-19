package org.praisenter.data.song;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.praisenter.DisplayType;
import org.praisenter.xml.XmlIO;

public final class SongLibrary {
	public static final SongLibrary open(Path path) throws IOException {
		SongLibrary sl = new SongLibrary(path);
		sl.initialize();
		return sl;
	}
	
	private final Path path;
	
	private Directory directory;
	
	private SongLibrary(Path path) {
		this.path = path;

	}
	
	private void initialize() throws IOException {
		
		Path index = this.path.resolve("_index");
		Files.createDirectories(index);
		
		this.directory = FSDirectory.open(index);
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		IndexWriter writer = new IndexWriter(this.directory, config);
		
		// index existing documents
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.path)) {
			for (Path file : stream) {
				if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".xml")) {
					try (InputStream is = Files.newInputStream(file)) {
						Document document = new Document();
						
						// store the path so we know where to get the song
						Field pathField = new StringField("path", file.toAbsolutePath().toString(), Field.Store.YES);
						document.add(pathField);
						
						// store the path so we know where to get the song
						Field lmField = new LongField("lastmodified", Files.getLastModifiedTime(file).toMillis(), Field.Store.YES);
						document.add(lmField);
						
						// read in the xml
						try {
							Song song = XmlIO.read(is, Song.class);
							
							// keywords
							if (song.properties.keywords != null) {
								Field keyWordsField = new TextField("keywords", song.properties.keywords, Field.Store.NO);
								document.add(keyWordsField);
							}
							
							// title fields
							for (Title title : song.properties.titles) {
								Field titleField = new TextField("titles", title.text, Field.Store.NO);
								document.add(titleField);
							}
							
							// verse fields
							for (Verse verse : song.verses) {
								Field verseField = new TextField("verses", verse.getDisplayText(DisplayType.MAIN), Field.Store.NO);
								document.add(verseField);
							}
						} catch (Exception e) {
							// TODO fix
							e.printStackTrace(System.err);
						}
						
						writer.updateDocument(new Term("path", file.toAbsolutePath().toString()), document);
					}
				}
			}
		}
		
		writer.close();
	}
	
	public void search(String term) throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(this.directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		
		QueryParser parser1 = new QueryParser("keywords", analyzer);
		QueryParser parser2 = new QueryParser("titles", analyzer);
		QueryParser parser3 = new QueryParser("verses", analyzer);
		
		Query query1 = parser1.parse(term);
		Query query2 = parser2.parse(term);
		Query query3 = parser3.parse(term);
		
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(query1, Occur.SHOULD);
		builder.add(query2, Occur.SHOULD);
		builder.add(query3, Occur.SHOULD);
		
		BooleanQuery query = builder.build();
		
		TopDocs result = searcher.search(query, 25);
		ScoreDoc[] docs = result.scoreDocs;
		
		for (ScoreDoc doc : docs) {
			Document document = searcher.doc(doc.doc);
			System.out.println(document.get("path"));
		}
	}
	
	public void save(Song song) {
		
	}
}
