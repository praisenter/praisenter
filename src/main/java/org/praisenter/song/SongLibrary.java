package org.praisenter.song;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.praisenter.DisplayType;
import org.praisenter.SearchType;
import org.praisenter.song.openlyrics.OpenLyricsSong;
import org.praisenter.song.openlyrics.OpenLyricsTitle;
import org.praisenter.song.openlyrics.OpenLyricsVerse;
import org.praisenter.xml.XmlIO;

public final class SongLibrary {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_VERSE = "verse";
	private static final String FIELD_KEYWORD = "keyword";
	private static final String FIELD_PATH = "path";
	
	private static final String INDEX_DIR = "_index";
	private static final String METADATA_DIR = "_metadata";

	/** The suffix added to a song file for metadata */
	private static final String METADATA_EXT = "_metadata.xml";
	
	
	private final Path path;
	private final Path indexPath;
	private final Path metadataPath;
	
	private Directory directory;
	private Analyzer analyzer;
	
	private final Map<Path, OpenLyricsSong> songs;
	
	public static final SongLibrary open(Path path) throws IOException {
		SongLibrary sl = new SongLibrary(path);
		sl.initialize();
		return sl;
	}
	
	private SongLibrary(Path path) {
		this.path = path;
		
		this.indexPath = this.path.resolve(INDEX_DIR);
		this.metadataPath = this.path.resolve(METADATA_DIR);
		
		this.songs = new HashMap<Path, OpenLyricsSong>();
	}
	
	// TODO import methods
	// TODO update methods
	// TODO delete methods
	
	private void initialize() throws IOException {
		Files.createDirectories(this.indexPath);
		Files.createDirectories(this.metadataPath);
		
		// load existing meta data into a temporary map
		Map<Path, SongMetadata> metadata = new HashMap<Path, SongMetadata>();
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		try (DirectoryStream<Path> dir = Files.newDirectoryStream(this.metadataPath)) {
			for (Path path : dir) {
				if (Files.isRegularFile(path)) {
					String mimeType = map.getContentType(path.toString());
					if (mimeType.equals("application/xml")) {
						try {
							SongMetadata meta = XmlIO.read(path, SongMetadata.class);
							metadata.put(meta.path, meta);
						} catch (Exception e) {
							// just continue loading
							LOGGER.warn("Failed to read file '" + path.toAbsolutePath().toString() + "'.", e);
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed loading existing song metadata.", ex);
		}
		
		// load and update the index
		this.directory = FSDirectory.open(this.indexPath);
		
		// don't exclude stop words!
		this.analyzer = new StandardAnalyzer(new CharArraySet(1, false));
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		IndexWriter writer = new IndexWriter(this.directory, config);
		
		// index existing documents
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.path)) {
			for (Path file : stream) {
				if (Files.isRegularFile(file)) {
					String mimeType = map.getContentType(file.toString());
					if (mimeType.equals("application/xml")) {
						try (InputStream is = Files.newInputStream(file)) {
							Document document = new Document();
							
							// store the path so we know where to get the song
							Field pathField = new StringField(FIELD_PATH, file.toAbsolutePath().toString(), Field.Store.YES);
							document.add(pathField);
							
							try {
								// read in the xml
								OpenLyricsSong song = XmlIO.read(is, OpenLyricsSong.class);
								
								// clean it up for use in Praisenter
								song.prepare();
								
								// check for metadata
								if (metadata.containsKey(file)) {
									song.metadata = metadata.get(file);
								} else {
									// save a new metadata
									SongMetadata sm = new SongMetadata();
									sm.path = file;
									song.metadata = sm;
									
									try {
										Path mp = this.metadataPath.resolve(file.getFileName().toString() + METADATA_EXT);
										XmlIO.save(mp, sm);
									} catch (Exception ex) {
										LOGGER.warn("Failed to save metadata for '" + file.toAbsolutePath().toString() + "'", ex);
									}
								}
								
								// add it to the song map
								this.songs.put(file.toAbsolutePath(), song);
								
								// keywords
								if (song.properties.keywords != null) {
									Field keyWordsField = new TextField(FIELD_KEYWORD, song.properties.keywords, Field.Store.YES);
									document.add(keyWordsField);
								}
								
								// title fields
								for (OpenLyricsTitle title : song.properties.titles) {
									Field titleField = new TextField(FIELD_TITLE, title.text, Field.Store.YES);
									document.add(titleField);
								}
								
								// verse fields
								for (OpenLyricsVerse verse : song.verses) {
									Field verseField = new TextField(FIELD_VERSE, verse.getDisplayText(DisplayType.MAIN), Field.Store.YES);
									document.add(verseField);
								}
								
								writer.updateDocument(new Term(FIELD_PATH, file.toAbsolutePath().toString()), document);
							} catch (Exception e) {
								LOGGER.warn("Failed to load song '" + file.toAbsolutePath().toString() + "'", e);
								writer.deleteDocuments(new Term(FIELD_PATH, file.toAbsolutePath().toString()));
							}
						}
					}
				}
			}
		}
		
		writer.close();
	}
	
	public void search(String text, SearchType type) throws IOException, InvalidTokenOffsetsException {
		if (text == null || text.length() == 0) return;
		
		List<String> tTokens = this.getTokens(text, FIELD_TITLE);
		List<String> vTokens = this.getTokens(text, FIELD_VERSE);
		List<String> kTokens = this.getTokens(text, FIELD_KEYWORD);
		
		BooleanQuery query = new BooleanQuery.Builder()
				.add(getQueryForTokens(FIELD_TITLE, tTokens, type), Occur.SHOULD)
				.add(getQueryForTokens(FIELD_VERSE, vTokens, type), Occur.SHOULD)
				.add(getQueryForTokens(FIELD_KEYWORD, kTokens, type), Occur.SHOULD)
				.build();
		
		this.search(query);
	}
	
	private Query getQueryForTokens(String field, List<String> tokens, SearchType type) {
		final String[] temp = new String[0];
		if (tokens.size() == 0) return null;
		if (tokens.size() == 1) {
			return new TermQuery(new Term(field, tokens.get(0)));
		} else if (type == SearchType.PHRASE) {
			return new PhraseQuery(2, field, tokens.toArray(temp));
		} else {
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (String token : tokens) {
				builder.add(new TermQuery(new Term(field, token)), type == SearchType.ALL_WORDS ? Occur.MUST : Occur.SHOULD);
			}
			return builder.build();
		}
	}
	
	private List<String> getTokens(String text, String field) throws IOException {
		List<String> tokens = new ArrayList<String>();
		
		TokenStream stream = this.analyzer.tokenStream(field, text);
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();

		while (stream.incrementToken()) {
			tokens.add(attr.toString());
		}
		
		stream.end();
		stream.close();
		
		return tokens;
	}
	
	private void search(Query query) throws IOException, InvalidTokenOffsetsException {
		IndexReader reader = DirectoryReader.open(this.directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TopDocs result = searcher.search(query, 25);
		ScoreDoc[] docs = result.scoreDocs;
		
		Scorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(scorer);
		
		for (ScoreDoc doc : docs) {
			Document document = searcher.doc(doc.doc);
			System.out.println(document.get(FIELD_PATH) + " " + doc.score);
			
			// http://stackoverflow.com/questions/25814445/accessing-words-around-a-positional-match-in-lucene
			System.out.println("+++++++++title");
			String[] titles = document.getValues(FIELD_TITLE);
			for (String title : titles) {
				String text = highlighter.getBestFragment(analyzer, FIELD_TITLE, title);
				if (text != null) {
					System.out.println(text);
					System.out.println("----------");
				}
			}
			
			System.out.println("+++++++++verse");
			String[] verses = document.getValues(FIELD_VERSE);
			for (String verse : verses) {
				String text = highlighter.getBestFragment(analyzer, FIELD_VERSE, verse);
				if (text != null) {
					System.out.println(text);
					System.out.println("----------");
				}
			}
			
			System.out.println("+++++++++keywords");
			String[] keywords = document.getValues(FIELD_KEYWORD);
			if (keywords != null && keywords.length != 0) {
				for (String keys : keywords) {
					String text = highlighter.getBestFragment(analyzer, FIELD_KEYWORD, keys);
					if (text != null) {
						System.out.println(text);
						System.out.println("----------");
					}
				}
			}
		}
		
		reader.close();
	}
	
	private void search(String term) throws IOException, ParseException, InvalidTokenOffsetsException {
		IndexReader reader = DirectoryReader.open(this.directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		QueryParser parser1 = new QueryParser("keywords", analyzer);
		QueryParser parser2 = new QueryParser("titles", analyzer);
		QueryParser parser3 = new QueryParser("verses", analyzer);
		
		parser1.setAllowLeadingWildcard(true);
		parser2.setAllowLeadingWildcard(true);
		parser3.setAllowLeadingWildcard(true);
		
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
		
		Scorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(scorer);
		
		for (ScoreDoc doc : docs) {
			Document document = searcher.doc(doc.doc);
			System.out.println(document.get("path"));
			
			// http://stackoverflow.com/questions/25814445/accessing-words-around-a-positional-match-in-lucene
			String[] titles = document.getValues("titles");
			for (String title : titles) {
				String text = highlighter.getBestFragment(analyzer, "titles", title);
				if (text != null) {
					System.out.println(title + " - " + text);
					System.out.println("----------");
				}
			}
			String[] verses = document.getValues("verses");
			for (String verse : verses) {
				System.out.println(verse + " - " + highlighter.getBestFragment(analyzer, "verses", verse));
				System.out.println("----------");
			}
			String[] keywords = document.getValues("keywords");
			if (keywords != null && keywords.length != 0) {
				for (String keys : keywords) {
					System.out.println(keywords + " - " + highlighter.getBestFragment(analyzer, "keywords", keys));
					System.out.println("----------");
				}
			}
		}
		
		reader.close();
	}
}
