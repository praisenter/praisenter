package org.praisenter.data.song;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.ImportExportProvider;
import org.praisenter.data.PathResolver;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.Tag;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;

final class ChordProSongFormatProvider implements ImportExportProvider<Song> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	// https://www.chordpro.org/chordpro/chordpro-introduction/
	// plus a few extras
	private static final String[] EXTENSIONS = new String[] {
		".cho", ".crd", ".chopro", ".chord", ".pro", ".chordpro", ".cpm"
	};
	
	private static final String DIRECTIVE_TITLE = "title";
	private static final String DIRECTIVE_SUBTITLE = "subtitle";
	private static final String DIRECTIVE_AUTHOR = "author";
	private static final String DIRECTIVE_ARTIST = "artist";
	private static final String DIRECTIVE_COMPOSER = "composer";
	private static final String DIRECTIVE_LYRICIST = "lyricist";
	private static final String DIRECTIVE_TRANSLATOR = "translator";
	private static final String DIRECTIVE_COPYRIGHT = "copyright";
	private static final String DIRECTIVE_ALBUM = "album";
	private static final String DIRECTIVE_YEAR = "year";
	private static final String DIRECTIVE_TRANSPOSE = "transpose";
	private static final String DIRECTIVE_KEY = "key";
	private static final String DIRECTIVE_TEMPO = "tempo";
	private static final String DIRECTIVE_TAG = "tag";
	private static final String DIRECTIVE_ARRANGER = "arranger";
	private static final String DIRECTIVE_COMMENT = "comment";
	private static final String DIRECTIVE_CCLI = "ccli";
	private static final String DIRECTIVE_BOOK = "book";
	private static final String DIRECTIVE_KEYWORDS = "keywords";
	private static final String DIRECTIVE_START_OF_VERSE = "start_of_verse";
	private static final String DIRECTIVE_START_OF_CHORUS = "start_of_chorus";
	private static final String DIRECTIVE_START_OF_BRIDGE = "start_of_bridge";
	private static final String DIRECTIVE_END_OF_VERSE = "end_of_verse";
	private static final String DIRECTIVE_END_OF_CHORUS = "end_of_chorus";
	private static final String DIRECTIVE_END_OF_BRIDGE = "end_of_bridge";
	private static final String DIRECTIVE_START_OF = "start_of_";
	private static final String DIRECTIVE_LANGUAGE = "language";
	private static final String DIRECTIVE_TRANSLITERATION = "transliteration";
	private static final String ATTRIBUTE_LABEL = "label";
	
	private boolean isSupportedFileExtension(String name) {
		return Stream.of(EXTENSIONS).anyMatch(ext -> name.endsWith(ext));
	}
	
	@Override
	public boolean isSupported(Path path) {
		return this.isSupportedFileExtension(path.getFileName().toString()) || this.isSupported(MimeType.get(path));
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return mimeType.toLowerCase().equals("text/plain");
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) {
		if (!stream.markSupported()) {
			LOGGER.warn("Mark is not supported on the given input stream.");
		}
		
		return this.isSupportedFileExtension(name) || this.isSupported(MimeType.get(stream, name));
	}
	
	@Override
	public void exp(PersistAdapter<Song> adapter, OutputStream stream, Song data) throws IOException {
		ChordProWriter writer = new ChordProWriter(data, stream);
		writer.write();
	}
	
	@Override
	public void exp(PersistAdapter<Song> adapter, Path path, Song data) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(path.toFile());
			BufferedOutputStream bos = new BufferedOutputStream(fos)) {
			this.exp(adapter, bos, data);
		}
	}
	
	@Override
	public void exp(PersistAdapter<Song> adapter, ZipArchiveOutputStream stream, Song data) throws IOException {
		PathResolver<Song> pathResolver = adapter.getPathResolver();
		Path path = pathResolver.getExportBasePath().resolve(pathResolver.getFriendlyFileName(data, "cho"));
		ZipArchiveEntry entry = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putArchiveEntry(entry);
		this.exp(adapter, (OutputStream)stream, data);
		stream.closeArchiveEntry();
	}

	@Override
	public DataImportResult<Song> imp(PersistAdapter<Song> adapter, Path path) throws IOException {
		DataImportResult<Song> result = new DataImportResult<>();
		
		ChordProReader parser = new ChordProReader(path);
		List<Song> songs = parser.read();
		
		List<DataReadResult<Song>> rrs = (songs.stream().map(s -> new DataReadResult<Song>(s)).collect(Collectors.toList()));
		
		for (DataReadResult<Song> drr : rrs) {
			if (drr == null) continue;
			Song song = drr.getData();
			if (song == null) continue;
			try {
				boolean isUpdate = adapter.upsert(song);
				if (isUpdate) {
					result.getUpdated().add(song);
				} else {
					result.getCreated().add(song);
				}
				result.getWarnings().addAll(drr.getWarnings());
			} catch (Exception ex) {
				result.getErrors().add(ex);
			}
		}
		
		return result;
	}
	
	private class ChordProWriter {
		private final Song song;
		private final OutputStream stream;
		
		public ChordProWriter(Song song, OutputStream stream) {
			this.song = song;
			this.stream = stream;
		}
		
		public void write() throws IOException {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
			writeMetadata(writer, DIRECTIVE_CCLI, this.song.getCCLINumber());
			writeMetadata(writer, DIRECTIVE_COPYRIGHT, this.song.getCopyright());
			writeMetadata(writer, DIRECTIVE_KEY, this.song.getKey());
			writeMetadata(writer, DIRECTIVE_KEYWORDS, this.song.getKeywords());
			writeMetadata(writer, DIRECTIVE_TITLE, this.song.getName());
			writeMetadata(writer, DIRECTIVE_COMMENT, this.song.getNotes());
			writeMetadata(writer, DIRECTIVE_AUTHOR, this.song.getPublisher());
			writeMetadata(writer, DIRECTIVE_YEAR, this.song.getReleased());
			writeMetadata(writer, DIRECTIVE_TEMPO, this.song.getTempo());
			writeMetadata(writer, DIRECTIVE_TRANSPOSE, this.song.getTransposition());
			writeMetadata(writer, DIRECTIVE_SUBTITLE, this.song.getVariant());
			
			writeEmptyLine(writer);
			
			for (Lyrics lyrics : this.song.getLyrics()) {
				writeMetadata(writer, DIRECTIVE_TRANSLITERATION, lyrics.getTransliteration());
				writeMetadata(writer, DIRECTIVE_LANGUAGE, lyrics.getLanguage());
				writeMetadata(writer, DIRECTIVE_SUBTITLE, lyrics.getTitle());
				
				for (Author author : lyrics.getAuthors()) {
					writeAuthor(writer, author);
				}
				
				for (SongBook book : lyrics.getSongBooks()) {
					writeBook(writer, book);
				}
				
				for (Section section : lyrics.getSections()) {
					writeEmptyLine(writer);
					writeSection(writer, section);
					writeEmptyLine(writer);
				}
				
				writeEmptyLine(writer);
			}
			
			// seems to be required for ZipArchiveOutputStream
			writer.flush();
		}
		
		private void writeMetadata(BufferedWriter writer, String name, String value) throws IOException {
			if (!StringManipulator.isNullOrEmpty(value)) {
				String[] lines = value.split("\n");
				for (String line : lines) {
					writer.write("{" + name + ": " + line + "}");
					writer.newLine();
				}
			}
		}
		
		private void writeAuthor(BufferedWriter writer, Author author) throws IOException {
			if (author.getType() == Author.TYPE_LYRICS) {
				writeMetadata(writer, DIRECTIVE_LYRICIST, author.getName());
			} else if (author.getType() == Author.TYPE_MUSIC) {
				writeMetadata(writer, DIRECTIVE_COMPOSER, author.getName());
			} else if (author.getType() == Author.TYPE_TRANSLATION) {
				writeMetadata(writer, DIRECTIVE_TRANSLATOR, author.getName());
			}
		}
		
		private void writeBook(BufferedWriter writer, SongBook book) throws IOException {
			writeMetadata(writer, DIRECTIVE_BOOK, book.getName());
		}
		
		private void writeEmptyLine(BufferedWriter writer) throws IOException {
			writer.newLine();
		}
		
		private void writeSection(BufferedWriter writer, Section section) throws IOException {
			String name = section.getName();
			if (StringManipulator.isNullOrEmpty(name))
				name = "";
			name = name.toLowerCase();
			
			String type = 
				name.contains("verse") ? "verse" : 
				name.contains("chorus") ? "chorus" : 
				name.contains("bridge") ? "bridge" :
				"verse";
			
			String label = section.getName().replaceAll("\"", "");
			
			writer.write("{start_of_" + type + ": label=\"" + label + "\"}");
			writer.newLine();
			writer.write(section.getText());
			writer.newLine();
			writer.write("{end_of_" + type + "}");
		}
	}
	
	private class ChordProReader {
	    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("\\{\\s*([^:\\s}-]+)-?([^:\\s}]+)?:?(?:\\s*(.+))?\\s*}");
	    private static final Pattern META_PATTERN = Pattern.compile("\\{\\s*meta:\\s*([^\\s=\"']+)\\s+(.+)\\s*}");
	    // this should handle attributes with or without single/double quotes
	    private static final Pattern ATTR_PATTERN = Pattern.compile("([^=\\s]+)=(?:([\"]([^\"]+)[\"])|(?:[']([^']+)['])|([^\\s}]+))");
	    private static final Pattern XML_PATTERN = Pattern.compile("<[^>]+>");
	    private static final Pattern ANNOTATION_PATTERN = Pattern.compile("\\[\\*.+\\]");
	    private static final Pattern CHORD_PATTERN = Pattern.compile("\\[[^\\]*]+\\]");

	    private abstract class Line {
			public final String raw;
	        public Line(String raw) { this.raw = raw; }
	    }

	    private class Directive extends Line {
	        public final String name;
	        public final String selector;
	        public final String value;
	        public final Map<String, String> attributes;

	        public Directive(String raw, String name, String selector, String value, Map<String, String> attributes) {
	            super(raw);
	            this.name = name;
	            this.selector = selector;
	            this.value = value;
	            this.attributes = attributes;
	        }

	        @Override
	        public String toString() {
	            return "Directive{name='" + name + "', selector='" + selector + "', value='" + value + "', attributes=" + attributes + "}";
	        }
	        
	        public boolean isStartOf() {
	        	return this.name.startsWith("start_of_");
	        }
	        
	        public boolean isEndOf() {
	        	return this.name.startsWith("end_of_");
	        }
	    }

	    private class Content extends Line {
	        public final String text;

	        public Content(String raw, String text) {
	            super(raw);
	            this.text = text;
	        }

	        @Override
	        public String toString() {
	            return "Content{text='" + text + "'}";
	        }
	    }
	    
	    private class EmptyLine extends Line {
	    	public EmptyLine(String raw) {
	            super(raw);
	        }
	    	
	    	@Override
	    	public String toString() {
	    		return "EmptyLine{}";
	    	}
	    }
	    
	    private class Annotation extends Line {
	        public Annotation(String raw) {
	            super(raw);
	        }

	    	@Override
	    	public String toString() {
	    		return "Annotation{raw='" + raw + "'}";
	    	}
	    }
		
	    private class Remark extends Line {
	    	public Remark(String raw) {
	            super(raw);
	        }
	    	
	    	@Override
	    	public String toString() {
	    		return "Remark{}";
	    	}
	    }
	    
		private Path path;
		
		private final List<Song> songs;
		private Song currentSong = null;
		private Lyrics currentLyrics = null;
		private Section currentSection = null;
		private boolean ignoreContent = false;
		private Map<String, Integer> sectionNumbers;
		
		public ChordProReader(Path path) {
			this.path = path;
			this.songs = new ArrayList<Song>();
			this.sectionNumbers = new HashMap<>();
		}

		public List<Song> read() throws FileNotFoundException, IOException {
			try (FileInputStream fis = new FileInputStream(path.toFile());
				BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedReader reader = new BufferedReader(new InputStreamReader(bis))) {
				
				// read line-by-line
				String line = null;
				
				// store all the lines in-order, we'll process them after parsing
				List<Line> lines = new ArrayList<>();
				while ((line = reader.readLine()) != null) {
					Line parsed = parseLine(line);
					if (parsed != null) {
						lines.add(parsed);
						// for testing
//						System.out.println(parsed.toString());
					}
				}
				
				// if there's no directives in the file, then we aren't sure if this
				// is a chordpro file or not
				if (!lines.stream().anyMatch(l -> l.getClass() == Directive.class)) {
					// return zero songs to indicate to try a different importer
					return this.songs;
				}
				
				this.processLines(lines);
				
				return this.songs;
			}
		}
		
	    private Line parseLine(String rawLine) {
	        String trimmed = rawLine.trim();
	        
	        if (trimmed.isEmpty()) 
	        	return new EmptyLine(rawLine);
	        
	        if (trimmed.startsWith("#"))
	        	return new Remark(trimmed);

	        Matcher directiveMatcher = DIRECTIVE_PATTERN.matcher(trimmed);
	        Matcher annotationMatcher = ANNOTATION_PATTERN.matcher(trimmed);
	        if (directiveMatcher.matches()) {
	            // Parse directive
	            String name = useLongDirectiveName(directiveMatcher.group(1).toLowerCase());
	            String selector = directiveMatcher.group(2);
	            String arg = directiveMatcher.group(3);
	            
	            Map<String, String> attrs = new HashMap<>();
	            if (arg != null) {
	            	// check for {meta: _name_ _value_} pattern
	            	Matcher metaMatcher = META_PATTERN.matcher(trimmed);
	            	if (metaMatcher.matches()) {
	            		attrs.put(metaMatcher.group(1).toLowerCase(), metaMatcher.group(2));
	            	} else {
		                // check for attribute pattern
		                Matcher attrMatcher = ATTR_PATTERN.matcher(arg);
		                while (attrMatcher.find()) {
		                	String attrName = attrMatcher.group(1).toLowerCase();
		                	String attrValue3 = attrMatcher.group(3); // double quoted value
		                	String attrValue4 = attrMatcher.group(4); // no quotes at all
		                	String attrValue5 = attrMatcher.group(5); // single quoted value
		                	String attrValue = "";
		                	if (!StringManipulator.isNullOrEmpty(attrValue3)) {
		                		attrValue = attrValue3;
		                	} else if (!StringManipulator.isNullOrEmpty(attrValue4)) {
		                		attrValue = attrValue4;
		                	} else if (!StringManipulator.isNullOrEmpty(attrValue5)) {
		                		attrValue = attrValue5;
		                	}
		                	// now strip all XML
		                	attrValue = XML_PATTERN.matcher(attrValue).replaceAll("");
		                    attrs.put(attrName, attrValue);
		                }
	            	}
	            }

	            return new Directive(rawLine, name, selector, arg, attrs);
	        } else if (annotationMatcher.matches()) {
	        	return new Annotation(rawLine);
	        } else {
	            // Treat as song content
	            String noChords = CHORD_PATTERN.matcher(rawLine).replaceAll("");
	            String noXml = XML_PATTERN.matcher(noChords).replaceAll("");
	            return new Content(rawLine, noXml);
	        }
	    }
		
	    private void processLines(List<Line> lines) {
	    	if (lines.size() == 0)
	    		return;
	    	
	    	// check for new_song/ns at the beginning
	    	// and skip it
	    	int startIndex = 0;
	    	Line first = lines.get(startIndex);
	    	if (first.getClass() == Directive.class) {
	    		Directive directive = (Directive)first;
	    		if ("ns".equals(directive.name) || "new_song".equals(directive.name)) {
	    			startIndex++;
	    		}
	    	}
	    	
	    	if (lines.size() == 1)
	    		return;
	    	
	    	// initialize (we should have at least one song/lyrics now)
	    	this.currentSong = new Song();
	    	this.currentLyrics = new Lyrics();
	    	this.currentSong.getLyrics().add(this.currentLyrics);
	    	this.songs.add(this.currentSong);
	    	
	    	// now process each line
	    	for (int i = startIndex; i < lines.size(); i++ ) {
	    		Line line = lines.get(i);
	    		if (line.getClass() == Directive.class) {
	    			Directive directive = (Directive)line;
	    			processDirective(directive);
	    		} else if (line.getClass() == Content.class && !this.ignoreContent) {
	    			Content content = (Content)line;
	    			processContent(content);
	    		} else if (line.getClass() == EmptyLine.class) {
	    			processEmptyLine();
	    		} else if (line.getClass() == Annotation.class) {
	    			// we ignore annotation lines
	    		} else {
	    			LOGGER.warn("The line type '{}' is not recognized, ignoring data.", line.getClass());
	    		}
	    	}
	    }
	    
	    private void processDirective(Directive line) {
	    	this.currentSection = null;
	    	if (line.isStartOf()) {
				processStartDirective(line);
			} else if (line.isEndOf()) {
				processEndDirective(line);
			} else {
				processStandardDirective(line);
			}
	    }
	    
	    private void processContent(Content line) {
	    	if (this.currentSection == null) {
	    		this.currentSection = new Section(this.getAutoGeneratedSectionName("Verse"), "");
	    		this.currentLyrics.getSections().add(this.currentSection);
	    	}
	    	
	    	String cv = this.currentSection.getText();
	    	if (!StringManipulator.isNullOrEmpty(cv)) {
	    		cv += "\n";
	    	} else {
	    		cv = "";
	    	}
	    	
	    	this.currentSection.setText(cv + line.text);
	    }
	    
	    private String getAutoGeneratedSectionName(String type) {
	    	String key = type.toLowerCase();
	    	Integer num = this.sectionNumbers.get(key);
	    	if (num == null) {
	    		num = 1;
	    	} else {
	    		num++;
	    	}
	    	
	    	this.sectionNumbers.put(key, num);
	    	
	    	return type + " " + num;
	    }
	    
	    private void processEmptyLine() {
	    	// an empty line usually means new content
	    	this.currentSection = null;
	    }
	    
		private void processStandardDirective(Directive directive) {
			if (directive.attributes.size() > 0) {
				for (String key : directive.attributes.keySet()) {
		    		String value = directive.attributes.get(key);
		    		processMetadata(key, value);
		    	}
			} else {
				processMetadata(directive.name, directive.value);
			}
		}
		
		
		private String useLongDirectiveName(String name) {
			switch (name) {
	            case "soc":
	            	return DIRECTIVE_START_OF_CHORUS;
	            case "eoc":
	            	return DIRECTIVE_END_OF_CHORUS;
	            case "sov":
	            	return DIRECTIVE_START_OF_VERSE;
	            case "eov":
	            	return DIRECTIVE_END_OF_VERSE;
	            case "sob":
	            	return DIRECTIVE_START_OF_BRIDGE;
	            case "eob":
	            	return DIRECTIVE_END_OF_BRIDGE;
	            case "sot":
	            	return "start_of_tab";
	            case "eot":
	            	return "end_of_tab";
	            case "sog":
	            	return "start_of_grid";
	            case "eog":
	            	return "end_of_grid";
	            default:
	            	return name;
			}
		}
		
		private void processMetadata(String name, String value) {
			switch(name) {
				// {ns / new_song toc=no}
				case "ns":
				case "new_song":
					this.currentSong = new Song();
					this.currentLyrics = new Lyrics();
					this.currentSong.getLyrics().add(this.currentLyrics);
					this.songs.add(this.currentSong);
					this.sectionNumbers.clear();
					break;
				// {t: / title: }
				case "t":
				case DIRECTIVE_TITLE:
					currentSong.setName(value);
					currentLyrics.setTitle(value);
					break;
				// {st: / su: / subtitle: }
				case "st":
				case "su":
				case DIRECTIVE_SUBTITLE:
					// override the title
					currentLyrics.setTitle(value);
					break;
				// {artist: / a: Leonard Cohen}
				// {author: Leonard Cohen}
				case "a":
				case DIRECTIVE_AUTHOR:
				case DIRECTIVE_ARTIST:
					currentSong.setPublisher(value);
					break;
				// {composer: Leonard Cohen}
				case DIRECTIVE_COMPOSER:
					currentLyrics.getAuthors().add(new Author(value, Author.TYPE_MUSIC));
					break;
				// {lyricist: Leonard Nijgh}
				case DIRECTIVE_LYRICIST:
					currentLyrics.getAuthors().add(new Author(value, Author.TYPE_LYRICS));
					break;
				// {translator: Hello World}
				case DIRECTIVE_TRANSLATOR:
					currentLyrics.getAuthors().add(new Author(value, Author.TYPE_TRANSLATION));
					break;
				// {copyright: 2014 Music Inc.}
				case DIRECTIVE_COPYRIGHT:
					currentSong.setCopyright(value);
					break;
				// {album: Songs Of Love And Hate}
				case DIRECTIVE_ALBUM:
					currentSong.setVariant(value);
					break;
				// {year: 2016}
				case DIRECTIVE_YEAR:
					currentSong.setReleased(value);
					break;
				// {transpose +2}
				// {transpose: +2}
				case DIRECTIVE_TRANSPOSE:
					currentSong.setTransposition(value);
					break;
				// {key: / k: C}
				case DIRECTIVE_KEY:
				case "k":
					currentSong.setKey(value);
					break;
				// {time: 4/4}
				case "time":
					LOGGER.warn("Ignoring unsupported directive: 'time'");
					break;
				// {tempo: / bpm: / metronome: 120}
				case DIRECTIVE_TEMPO:
				case "bpm":
				case "metronome":
					currentSong.setTempo(value);
					break;
				// {duration: 268}
				case "duration":
					LOGGER.warn("Ignoring unsupported directive: 'duration'");
					break;
				// {capo: / ca: 2}
				case "capo":
				case "ca":
					LOGGER.warn("Ignoring unsupported directive: 'capo'");
					break;
				// {tag: Needs study}
				case DIRECTIVE_TAG:
					currentSong.getTags().add(new Tag(value));
					break;
				// {arranger: Rogier van Otterloo}
				case DIRECTIVE_ARRANGER:
					currentLyrics.getAuthors().add(new Author(value, Author.TYPE_MUSIC));
					break;
				// {c: / comment: this is a comment}
				// {ci: / comment_italic: this is a comment}
				// {cb: / comment_box: this is a comment}
				// {highlight; this is a comment}
				case "c":
				case DIRECTIVE_COMMENT:
				case "ci":
				case "comment_italic":
				case "cb":
				case "comment_box":
				case "highlight":
					String notes = currentSong.getNotes();
					if (!StringManipulator.isNullOrEmpty(notes)) {
						notes += "\n";
					} else {
						notes = "";
					}
					currentSong.setNotes(notes + value);
					break;
					
				// non-standard directives
					
				// {ccli: 12345}
				case DIRECTIVE_CCLI:
					currentSong.setCCLINumber(value);
					break;
				// {book: adfasfd}
				case DIRECTIVE_BOOK:
					currentLyrics.getSongBooks().add(new SongBook(value, ""));
					break;
				// {keywords: }
				case DIRECTIVE_KEYWORDS:
					currentSong.setKeywords(value);
					break;
				// {transliteration: }
				case DIRECTIVE_TRANSLITERATION:
					currentLyrics.setTransliteration(value);
					break;
				// {language: EN-us}
				case DIRECTIVE_LANGUAGE:
					currentLyrics.setLanguage(value);
					break;
				default:
					LOGGER.warn("Ignoring unsupported directive: '{}'", name);
					break;
			}
		}
		
		private void processStartDirective(Directive directive) {
			// ignored content
			switch (directive.name) {
				case "start_of_tab":
				case "start_of_grid":
				case "start_of_abc":
					this.ignoreContent = true;
					return;
				default:
					break;
			}
			
			
			this.currentSection = new Section("", "");
			
			// try to use the label attribute if it exists
			if (directive.attributes.containsKey("label")) {
				this.currentSection.setName(directive.attributes.get(ATTRIBUTE_LABEL));
			}
			
			// if the name is still null/empty, then generate a name
			if (StringManipulator.isNullOrEmpty(this.currentSection.getName())) {
				String name = directive.name.replaceAll(DIRECTIVE_START_OF, "");
				this.currentSection.setName(this.getAutoGeneratedSectionName(name));
			}
			
			this.currentLyrics.getSections().add(this.currentSection);
		}
		
		private void processEndDirective(Directive directive) {
			this.currentSection = null;
			this.ignoreContent = false;
		}
	}
}
