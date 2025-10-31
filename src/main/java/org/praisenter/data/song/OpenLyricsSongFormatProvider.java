package org.praisenter.data.song;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.ImportExportProvider;
import org.praisenter.data.InvalidImportExportFormatException;
import org.praisenter.data.PathResolver;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.Tag;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;
import org.praisenter.utility.StringManipulator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class OpenLyricsSongFormatProvider implements ImportExportProvider<Song> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public boolean isSupported(Path path) {
		return this.isSupported(MimeType.get(path));
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.XML.is(mimeType) || mimeType.toLowerCase().startsWith("text");
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) {
		if (!stream.markSupported()) {
			LOGGER.warn("Mark is not supported on the given input stream.");
		}
		
		return this.isSupported(MimeType.get(stream, name));
	}
	
	@Override
	public void exp(PersistAdapter<Song> adapter, OutputStream stream, Song data) throws IOException {
		OpenSongWriter writer = new OpenSongWriter(data, stream);
		try {
			writer.write();
		} catch (TransformerException e) {
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
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
		Path path = pathResolver.getExportBasePath().resolve(pathResolver.getFriendlyFileName(data, "xml"));
		ZipArchiveEntry entry = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putArchiveEntry(entry);
		this.exp(adapter, (OutputStream)stream, data);
		stream.closeArchiveEntry();
	}

	@Override
	public DataImportResult<Song> imp(PersistAdapter<Song> adapter, Path path) throws IOException {
		DataImportResult<Song> result = new DataImportResult<>();
		
		if (!this.isOpenLyricsSong(path)) {
			return result;
		}
		
		String name = path.getFileName().toString();
		int i = name.lastIndexOf('.');
		if (i >= 0) {
			name = name.substring(0, i);
		}
		
		List<DataReadResult<Song>> results = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(path.toFile());
			BufferedInputStream bis = new BufferedInputStream(fis)) {
			results.add(this.parse(bis, name));
		} catch (SAXException | ParserConfigurationException ex) {
			throw new InvalidImportExportFormatException(ex);
		}
		
		for (DataReadResult<Song> drr : results) {
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
	
	private boolean isOpenLyricsSong(Path path) {
		try (FileInputStream stream = new FileInputStream(path.toFile())) {
			XMLInputFactory f = XMLInputFactory.newInstance();
			// prevent XXE attacks
			// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLInputFactory_.28a_StAX_parser.29
			f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			XMLStreamReader r = f.createXMLStreamReader(stream);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().equalsIgnoreCase("song")) {
			    		String ns = r.getNamespaceURI();
			    		if (ns != null && ns.toLowerCase().startsWith("http://openlyrics.info/")) {
			    			return true;
			    		}
			    	}
			    }
			}
		} catch (Exception ex) {
			LOGGER.trace("Failed to read the path as an XML document.", ex);
		}
		return false;
	}
	
	/**
	 * Attempts to parse the given input stream into the internal song format.
	 * @param stream the input stream
	 * @param name the name
	 * @throws IOException if an IO error occurs
	 * @throws InvalidImportExportFormatException if the stream was not in the expected format
	 * @return {@link Song}
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private DataReadResult<Song> parse(InputStream stream, String name) throws ParserConfigurationException, SAXException, IOException {
		byte[] content = Streams.read(stream);
		// read the bytes
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// prevent XXE attacks 
		// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser parser = factory.newSAXParser();
		OpenSongReader handler = new OpenSongReader();
		parser.parse(new ByteArrayInputStream(content), handler);
		
		// clean up
		for (Lyrics lyrics : handler.song.getLyrics()) {
			for (Section section : lyrics.getSections()) {
				String cleansed = section.getText()
				.replaceAll("\\s*\\n+\\s*", "<br>")
				.replaceAll("\\s+", " ")
				.replaceAll("<br>", Constants.NEW_LINE);
				section.setText(cleansed.trim());
			}
		}
		
		return new DataReadResult<Song>(handler.song);
	}
	
	private final class OpenSongWriter {
		private final Song song;
		private final OutputStream stream;
		
		public OpenSongWriter(Song song, OutputStream stream) {
			this.song = song;
			this.stream = stream;
		}
		
		public void write() throws TransformerException, ParserConfigurationException, IOException {
			// 1. Create a DocumentBuilderFactory and DocumentBuilder
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setIgnoringElementContentWhitespace(false);
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // 2. Create a new Document
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("song");
            doc.appendChild(rootElement);
            rootElement.setAttribute("xmlns", "http://openlyrics.info/namespace/2009/song");
            //rootElement.setAttribute("xml:lang", );
            //rootElement.setAttribute("chordNotation", "");
            rootElement.setAttribute("version", "0.9");
            rootElement.setAttribute("createdIn", song.getSource());
            //rootElement.setAttribute("modifiedIn",);
            rootElement.setAttribute("modifiedDate", song.getModifiedDate().toString());

            Element properties = doc.createElement("properties");
            rootElement.appendChild(properties);
            if (!StringManipulator.isNullOrEmpty(song.getName())) {
	            Element titles = doc.createElement("titles");
	            properties.appendChild(titles);
	            for (Lyrics lyrics : song.getLyrics()) {
	            	String t = lyrics.getTitle();
	            	if (!StringManipulator.isNullOrEmpty(t)) {
			            Element title = doc.createElement("title");
			            if (!StringManipulator.isNullOrEmpty(lyrics.getLanguage())) title.setAttribute("lang", lyrics.getLanguage());
			            if (!StringManipulator.isNullOrEmpty(lyrics.getTransliteration())) title.setAttribute("translit", lyrics.getTransliteration());
			            if (lyrics.isOriginal()) title.setAttribute("original", "true");
			            title.setTextContent(t);
			            titles.appendChild(title);
	            	}
	            }
            }
            
            if (song.getLyrics().stream().anyMatch(l -> l.getAuthors().size() > 0)) {
	            Element authors = doc.createElement("authors");
	            properties.appendChild(authors);
	            for (Lyrics lyrics : song.getLyrics()) {
	            	for (Author au : lyrics.getAuthors()) {
	            		if (!StringManipulator.isNullOrEmpty(au.getName())) {
				            Element author = doc.createElement("author");
				            if (!StringManipulator.isNullOrEmpty(au.getType())) author.setAttribute("type", au.getType());
				            if (!StringManipulator.isNullOrEmpty(lyrics.getLanguage())) author.setAttribute("lang", lyrics.getLanguage());
				            author.setTextContent(au.getName());
				            authors.appendChild(author);
	            		}
	            	}
	            }
            }
            
            if (!StringManipulator.isNullOrEmpty(song.getCopyright())) {
	            Element copyright = doc.createElement("copyright");
	            copyright.setTextContent(song.getCopyright());
	            properties.appendChild(copyright);
            }
            
            if (!StringManipulator.isNullOrEmpty(song.getCCLINumber())) {
	            Element ccliNo = doc.createElement("ccliNo");
	            ccliNo.setTextContent(song.getCCLINumber());
	            properties.appendChild(ccliNo);
            }
            
            if (!StringManipulator.isNullOrEmpty(song.getReleased())) {
	            Element released = doc.createElement("released");
	            released.setTextContent(song.getReleased());
	            properties.appendChild(released);
            }

            if (!StringManipulator.isNullOrEmpty(song.getTransposition())) {
	            Element transposition = doc.createElement("transposition");
	            transposition.setTextContent(song.getTransposition());
	            properties.appendChild(transposition);
            }
            
            if (!StringManipulator.isNullOrEmpty(song.getKey())) {
	            Element key = doc.createElement("key");
	            key.setTextContent(song.getKey());
	            properties.appendChild(key);
            }
            
//            Element timeSignature = doc.createElement("timeSignature");
//            timeSignature.setTextContent(null);
//            properties.appendChild(timeSignature);

            if (!StringManipulator.isNullOrEmpty(song.getVariant())) {
	            Element variant = doc.createElement("variant");
	            variant.setTextContent(song.getVariant());
	            properties.appendChild(variant);
            }
            
            if (!StringManipulator.isNullOrEmpty(song.getPublisher())) {
	            Element publisher = doc.createElement("publisher");
	            publisher.setTextContent(song.getPublisher());
	            properties.appendChild(publisher);
            }
            
//            Element version = doc.createElement("version");
//            version.setTextContent(song.getVersion());
//            properties.appendChild(version);
            
            if (!StringManipulator.isNullOrEmpty(song.getKeywords())) {
	            Element keywords = doc.createElement("keywords");
	            keywords.setTextContent(song.getKeywords());
	            properties.appendChild(keywords);
            }

//            Element verseOrder = doc.createElement("verseOrder");
//            verseOrder.setTextContent(null);
//            properties.appendChild(verseOrder);
            
            if (!StringManipulator.isNullOrEmpty(song.getTempo())) {
	            Element tempo = doc.createElement("tempo");
	            tempo.setAttribute("type", "bpm");
	            tempo.setTextContent(song.getTempo());
	            properties.appendChild(tempo);
            }
            
            if (song.getLyrics().stream().anyMatch(l -> l.getSongBooks().size() > 0)) {
	            Element songbooks = doc.createElement("songbooks");
	            properties.appendChild(songbooks);
	            for (Lyrics lyrics : song.getLyrics()) {
	            	for (SongBook sb : lyrics.getSongBooks()) {
	            		if (!StringManipulator.isNullOrEmpty(sb.getName()) ||
	            			!StringManipulator.isNullOrEmpty(sb.getEntry())) {
				            Element songbook = doc.createElement("songbook");
				            if (!StringManipulator.isNullOrEmpty(sb.getName())) songbook.setAttribute("name", sb.getName());
				            if (!StringManipulator.isNullOrEmpty(sb.getEntry())) songbook.setAttribute("entry", sb.getEntry());
				            songbooks.appendChild(songbook);
	            		}
	            	}
	            }
            }

            if (song.getTags().size() > 0) {
	            Element themes = doc.createElement("themes");
	            properties.appendChild(themes);
	            for (Tag tag : song.getTags()) {
	            	if (!StringManipulator.isNullOrEmpty(tag.getName())) {
			            Element theme = doc.createElement("theme");
						// theme.setAttribute("lang", null);
						// theme.setAttribute("translit", null);
			            theme.setTextContent(tag.getName());
			            themes.appendChild(theme);
	            	}
	            }
            }
            
            if (song.getNotes() != null) {
                Element comments = doc.createElement("comments");
                properties.appendChild(comments);
	            String[] cs = song.getNotes().split("\n");
	            for (String c : cs) {
		            Element comment = doc.createElement("comment");
		            comment.setTextContent(c);
		            comments.appendChild(comment);
	            }
            }
            
            // lyrics
            
            if (song.getLyrics().size() > 0) {
	            Element lyrics = doc.createElement("lyrics");
	            rootElement.appendChild(lyrics);
	            for (Lyrics lys : song.getLyrics()) {
	            	for (Section sec : lys.getSections()) {
		                Element verse = doc.createElement("verse");
		                lyrics.appendChild(verse);
		                verse.setAttribute("name", sec.getName());
		                
		                if (!StringManipulator.isNullOrEmpty(lys.getLanguage())) verse.setAttribute("lang", lys.getLanguage());
		                if (!StringManipulator.isNullOrEmpty(lys.getTransliteration())) verse.setAttribute("translit", lys.getTransliteration());
		                
		                Element lines = doc.createElement("lines");
		                verse.appendChild(lines);
		                
		                String text = sec.getText();
		                if (text == null) {
		                	text = "";
		                }
		                
		                String[] lns = text.split("\\r?\\n");
		                int lineNumber = 0;
		                for (String line : lns) {
		                	if (lineNumber != 0) {
		                		Element br = doc.createElement("br");
		                		lines.appendChild(br);
		                	}
			                Text txt = doc.createTextNode(line.trim());
			                lines.appendChild(txt);
			                lineNumber++;
		                }
	            	}
	            }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(this.stream);

            transformer.transform(source, result);
            
            this.stream.flush();
		}
	}
	
	// SAX parser implementation
	
	/**
	 * SAX parse for the OpenSong format.
	 * @author William Bittle
	 * @version 3.0.0
	 */
	private final class OpenSongReader extends DefaultHandler {
		private Song song;
		private Lyrics lyrics;
		private Author author;
		private Section section;
		
		private boolean inSection = false;
		
		private List<SongBook> songbooks;
		
		/** Buffer for tag contents */
		private StringBuilder dataBuilder;
		
		public OpenSongReader() {
			this.songbooks = new ArrayList<SongBook>();
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// inspect the tag name
			if (qName.equalsIgnoreCase("song")) {
				// when we see the <song> tag we create a new song
				this.song = new Song();
				
				String createdIn = attributes.getValue("createdIn");
				String modifiedIn = attributes.getValue("modifiedIn");
				String modifiedDate = attributes.getValue("modifiedDate");
				
				this.song.setSource(!StringManipulator.isNullOrEmpty(createdIn) ? createdIn : modifiedIn);
				if (!StringManipulator.isNullOrEmpty(modifiedDate)) {
					try {
						this.song.setModifiedDate(Instant.parse(modifiedDate));
					} catch (Exception ex) {
						LOGGER.warn("Failed to parse modifiedDate '" + modifiedDate + "' as an Instant.");
						try {
							this.song.setModifiedDate(LocalDateTime.parse(modifiedDate).toInstant(ZoneOffset.of(ZoneOffset.systemDefault().getId())));
						} catch (Exception ex1) {
							LOGGER.warn("Failed to parse modifiedDate '" + modifiedDate + "' as a LocalDateTime.");
						}
					}
				}
			} else if (qName.equalsIgnoreCase("title")) {
				// get the language and transliteration attributes
				String language = attributes.getValue("lang");
				String transliteration = attributes.getValue("translit");
				String original = attributes.getValue("original");
				
				Lyrics lyrics = this.song.getLyrics(language, transliteration);
				
				if (lyrics == null) {
					lyrics = new Lyrics();
					this.song.getLyrics().add(lyrics);
				}
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.setTransliteration(transliteration);
				if (!StringManipulator.isNullOrEmpty(original)) {
					try {
						lyrics.setOriginal(Boolean.parseBoolean(original));
					} catch (Exception ex) {
						LOGGER.warn("Failed to parse original flag '" + original + "' as a boolean.");
					}
				}
			} else if (qName.equalsIgnoreCase("author")) {
				// get the type language
				String language = attributes.getValue("lang");
				String type = attributes.getValue("type");
				
				Lyrics lyrics = this.song.getLyrics(language, null);
				
				if (lyrics == null) {
					lyrics = new Lyrics();
					this.song.getLyrics().add(lyrics);
				}
				
				String at = type;
				if (!StringManipulator.isNullOrEmpty(at)) {
					if (at.toLowerCase().equals("words")) {
						at = Author.TYPE_LYRICS;
					} else if (at.toLowerCase().equals("music")) {
						at = Author.TYPE_MUSIC;
					} else if (at.toLowerCase().equals("translation")) {
						at = Author.TYPE_TRANSLATION;
					}
				}
				
				this.author = new Author(null, at);
				
				this.lyrics = lyrics;
				
				lyrics.setLanguage(language);
				lyrics.getAuthors().add(this.author);
			} else if (qName.equalsIgnoreCase("songbook")) {
				String name = attributes.getValue("name");
				String entry = attributes.getValue("entry");
				
				SongBook sb = new SongBook(name, entry);
				this.songbooks.add(sb);
			} else if (qName.equalsIgnoreCase("verse")) {
				String name = attributes.getValue("name");
				this.section = new Section();
				this.section.setName(name);
				
				String language = attributes.getValue("lang");
				String transliteration = attributes.getValue("translit");
				
				Lyrics lyrics = this.song.getLyrics(language, transliteration);
				
				if (lyrics == null) {
					lyrics = new Lyrics();
					lyrics.setLanguage(language);
					lyrics.setTransliteration(transliteration);
					this.song.getLyrics().add(lyrics);
				}
				
				this.lyrics = lyrics;
				
				lyrics.getSections().add(this.section);
				
				this.inSection = true;
			} else if (qName.equalsIgnoreCase("br")) {
				if (this.dataBuilder == null) {
					this.dataBuilder = new StringBuilder();
				}
				this.dataBuilder.append(Constants.NEW_LINE);
			} else if (qName.equalsIgnoreCase("comment") && this.inSection) {
				// this begins a verse level comment
				if (this.dataBuilder != null) {
					String verse = this.section.getText();
					String text = this.dataBuilder.toString();
					if (text != null) {
						if (verse == null) {
							verse = text;
						} else {
							verse += text;
						}
						this.section.setText(verse);
					}
					this.dataBuilder = null;
				}
			} else if (qName.equalsIgnoreCase("lines")) {
				if (this.inSection && this.section != null && !StringManipulator.isNullOrEmpty(this.section.getText())) {
					this.section.setText(this.section.getText() + "\n");
				}
				this.dataBuilder = null;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			// this method can be called a number of times for the contents of a tag
			// this is done to improve performance so we need to append the text before
			// using it
			String s = new String(ch, start, length);
			if (this.dataBuilder == null) {
				this.dataBuilder = new StringBuilder();
			}
			this.dataBuilder.append(s);
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("song".equalsIgnoreCase(qName)) {
				// apply songbooks
				Lyrics lyrics = this.song.getDefaultLyrics();
				for (SongBook songbook : this.songbooks) {
					lyrics.getSongBooks().add(songbook.copy());
				}
				// set primary lyrics
				this.song.setPrimaryLyrics(lyrics.getId());
			} else if ("title".equalsIgnoreCase(qName)) {
				if (this.dataBuilder != null) {
					String title = this.dataBuilder.toString().trim().replaceAll("\r?\n", " ");
					this.lyrics.setTitle(title);
					if (!StringManipulator.isNullOrEmpty(title) && (this.song.getName() == null || this.lyrics.isOriginal())) {
						this.song.setName(title);
					}
				}
			} else if ("author".equalsIgnoreCase(qName)) {
				if (this.dataBuilder != null) {
					this.author.setName(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("copyright")) {
				if (this.dataBuilder != null) {
					this.song.setCopyright(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("ccliNo")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						this.song.setCCLINumber(text);
					}
				}
			} else if (qName.equalsIgnoreCase("released")) {
				if (this.dataBuilder != null) {
					this.song.setReleased(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("transposition")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						this.song.setTransposition(text);
					}
				}
			} else if (qName.equalsIgnoreCase("tempo")) {
				if (this.dataBuilder != null) {
					this.song.setTempo(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("key")) {
				if (this.dataBuilder != null) {
					this.song.setKey(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("variant")) {
				if (this.dataBuilder != null) {
					this.song.setVariant(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("publisher")) {
				if (this.dataBuilder != null) {
					this.song.setPublisher(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("keywords")) {
				if (this.dataBuilder != null) {
					this.song.setKeywords(this.dataBuilder.toString().trim());
				}
			} else if (qName.equalsIgnoreCase("theme")) {
				if (this.dataBuilder != null) {
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						this.song.getTags().add(new Tag(text));
					}
				}
			} else if (qName.equalsIgnoreCase("comment") || qName.equalsIgnoreCase("verseOrder")) {
				if (this.dataBuilder != null) {
					String comments = this.song.getNotes();
					String text = this.dataBuilder.toString().trim();
					if (!StringManipulator.isNullOrEmpty(text)) {
						if (comments == null) {
							comments = text;
						} else {
							comments = comments + Constants.NEW_LINE + text;
						}
						this.song.setNotes(comments);
					}
					if (this.inSection) {
						this.dataBuilder = null;
					}
				}
			} else if (qName.equalsIgnoreCase("verse")) {
				this.inSection = false;
				this.section = null;
			} else if (qName.equalsIgnoreCase("lines")) {
				// section could be null here in the situation that we
				// encountered a <instrument> tag (since they have <lines> too)
				if (this.dataBuilder != null && this.section != null) {
					String verse = this.section.getText();
					String text = this.dataBuilder.toString().trim();
					if (text != null) {
						if (verse == null) {
							verse = text;
						} else {
							verse += text;
						}
						this.section.setText(verse);
					}
				}
			}
			
			if (this.section == null) {
				this.dataBuilder = null;
			}
		}
	}
}
