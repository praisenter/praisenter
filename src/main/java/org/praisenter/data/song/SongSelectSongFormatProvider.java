package org.praisenter.data.song;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.ImportExportProvider;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.Tag;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;

final class SongSelectSongFormatProvider implements ImportExportProvider<Song> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String[] EXTENSIONS = new String[] {
		".txt", ".usr", ".bin"
	};
	
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
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void exp(PersistAdapter<Song> adapter, Path path, Song data) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void exp(PersistAdapter<Song> adapter, ZipArchiveOutputStream stream, Song data) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataImportResult<Song> imp(PersistAdapter<Song> adapter, Path path) throws IOException {
		DataImportResult<Song> result = new DataImportResult<>();
		
		SongSelectReader parser = new SongSelectReader(path);
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
	
	private class SongSelectReader {
		private static final Pattern USR_CCLI_PATTERN = Pattern.compile("\\s*\\[S\\s+A?(.+)\\]\\s*");
		private static final Pattern TXT_CCLI_PATTERN = Pattern.compile(".*?(\\d+).*?");
		
		private final Path path;
		private final List<Song> songs;
		
		private Song song;
		private Lyrics lyrics;
		
		public SongSelectReader(Path path) {
			this.path = path;
			this.songs = new ArrayList<>();
		}
		
		public List<Song> read() throws FileNotFoundException, IOException {
			// get the file extension
			String filename = this.path.getFileName().toString();
			
			try (FileInputStream fis = new FileInputStream(path.toFile());
				BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedReader reader = new BufferedReader(new InputStreamReader(bis))) {
				
				reader.mark(4096);;
				String line = reader.readLine();
				if (line == null) {
					return this.songs;
				}
				
				this.song = new Song();
				this.lyrics = new Lyrics();
				this.song.getLyrics().add(this.lyrics);
				this.songs.add(this.song);
				
				boolean isUsrFile = false;
				if (line.startsWith("[File]") || filename.startsWith(".usr") || filename.startsWith(".bin")) {
					// parse as .usr file
					isUsrFile = true;
				}
				
				reader.reset();
				
				if (isUsrFile) {
					this.readLineFromUsr(reader);
				} else {
					this.readLineFromTxt(reader);
				}
			}
			
			return this.songs;
		}
		
		public void readLineFromUsr(BufferedReader reader) throws IOException {
			String line = null;
			
			// read line-by-line
			while ((line = reader.readLine()) != null) {
		        // CCLI number
		        Matcher matcher = USR_CCLI_PATTERN.matcher(line);
		        if (matcher.matches()) {
		        	// has the following patterns
		        	// [S xxxxxx]
		        	// [S Axxxxxx]
		        	this.song.setCCLINumber(matcher.group(1));
		        } else if (line.startsWith("Title=")) {
		        	// no special format for title
	                this.song.setName(line.substring(6).trim());
	                this.lyrics.setTitle(this.song.getName());
	            } else if (line.startsWith("Author=")) {
	            	// Author is "|" delimited
	                String[] authors = line.substring(7).trim().split("\\|");
	                for (String author : authors) {
	                	this.lyrics.getAuthors().add(new Author(author.trim(), null));
	                }
	            } else if (line.startsWith("Copyright=")) {
	            	// Copyright is "|" delimited
	            	this.song.setCopyright(line.substring(10).trim());
	            } else if (line.startsWith("Themes=")) {
	            	// Themes is "/t" delimited
	                String[] themes = line.substring(7).trim().split("/t");
	                for (String theme : themes) {
	                	this.song.getTags().add(new Tag(theme.trim()));
	                }
	            } else if (line.startsWith("Keys=")) {
	            	this.song.setKey(line.substring(5).trim());
	            } else if (line.startsWith("Fields=")) {
	            	// Fields is the list of parts of the song (verse, chorus, etc) delimited by /t
	            	String parts[] = line.substring(7).split("/t");
	            	int sc = this.lyrics.getSections().size();
	            	
	                for (int i = 0; i < parts.length; i++) {
	                	String part = parts[i];
	                	Section section = null;
	                	if (i >= sc) {
	                		section = new Section();
	                		this.lyrics.getSections().add(section);
	                	} else {
	                		section = this.lyrics.getSections().get(i);
	                	}
	                	
	                	section.setName(part);
	                }
	            } else if (line.startsWith("Words=")) {
	            	// Words is the list of parts of the song (the verse content) delimited by /t
	                String parts[] = line.substring(6).split("/t");
	            	int sc = this.lyrics.getSections().size();
	            	
	                for (int i = 0; i < parts.length; i++) {
	                	String part = parts[i];
	                	Section section = null;
	                	if (i >= sc) {
	                		section = new Section();
	                		this.lyrics.getSections().add(section);
	                	} else {
	                		section = this.lyrics.getSections().get(i);
	                	}
	                	
	                	// Some words start with a (PRECHORUS)-like line, so skip this
	                	String[] textLines = part.split("/n");
	                	int skip = 0;
	                	if (textLines[0].startsWith("(")) {
	                		// then ignore this line for the text
	                		// but use it as the name
	                		String name = section.getName();
	                		if (StringManipulator.isNullOrEmpty(name)) {
	                			section.setName(textLines[0].trim());
	                		} else {
	                			section.setName(name + " " + textLines[0].trim());
	                		}
	                		skip = 1;
	                	}
	                	List<String> lines = Stream.of(textLines).skip(skip).collect(Collectors.toList());
	                	section.setText(String.join("\n", lines));
	                }
	            } else if (line.startsWith("Type=")) {
	                this.song.setSource(line.substring(5).trim());
	            }
			}
		}

		public void readLineFromTxt(BufferedReader reader) throws IOException {
			String line = null;
			
			int lineNumber = 0;
			
			Section section = null;
			int emptyLines = 0;
			boolean inSection = false;
			boolean inMetadata = false;
			
			// read line-by-line
			while ((line = reader.readLine()) != null) {
				boolean isEmpty = StringManipulator.isNullOrEmpty(line);
				
				// first line is title
				if (lineNumber == 0) {
					this.song.setName(line);
					this.lyrics.setTitle(line);
				} else if (isEmpty) {
					inSection = false;
					emptyLines++;
				} else if (inMetadata && line.startsWith("©")) {
					// it's the copyright line
					this.song.setCopyright(line.trim());
				} else if (inMetadata) {
					String[] authors = line.split("/");
					for (String author : authors) {
						this.lyrics.getAuthors().add(new Author(author.trim(), null));
					}
					// we're done now
					break;
				} else if (inSection) {
					if (line.startsWith("(")) {
						// a more specific section name
						String name = section.getName();
						if (StringManipulator.isNullOrEmpty(name)) {
							section.setName(line.trim());
						} else {
							section.setName(name + " " + line.trim());							
						}
					} else {
						// otherwise, just normal section text
						String text = section.getText();
						if (StringManipulator.isNullOrEmpty(text)) {
							section.setText(line.trim());
						} else {
							section.setText(text + "\n" + line.trim());
						}
					}
				} else if (!inMetadata && line.startsWith("CCLI")) {
					// there are two lines in the file that start with CCLI
					// the first one is the song's CCLI number
					// the second one is the license number of the subscriber
					Matcher matcher = TXT_CCLI_PATTERN.matcher(line);
					if (matcher.matches()) {
						this.song.setCCLINumber(matcher.group(1));
					}
					inSection = false;
					inMetadata = true;
				} else if (emptyLines >= 2) {
					// we're moving into a verse now
					section = new Section();
					section.setName(line.trim());
					inSection = true;
					this.lyrics.getSections().add(section);
				}
				
				if (!isEmpty) {
					emptyLines = 0;
				}
				
				lineNumber++;
			}
		}
	}
}
