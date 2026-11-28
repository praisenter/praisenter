package org.praisenter.data.media;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.PraisenterFormatProvider;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.json.PraisenterFormat;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;
import org.praisenter.utility.FileHelper;

final class PraisenterMediaFormatProvider extends PraisenterFormatProvider<Media> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	public PraisenterMediaFormatProvider() {
		super(Media.class);
	}

	@Override
	public boolean isSupported(Path path) {
		return this.isSupported(MimeType.get(path));
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.ZIP.is(mimeType);
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) throws IOException {
		if (!stream.markSupported()) {
			LOGGER.warn("Mark is not supported on the given input stream.");
		}
		
		return this.isSupported(MimeType.get(stream, name));
	}
	
	@Override
	public void exp(PersistAdapter<Media> adapter, OutputStream stream, Media data) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void exp(PersistAdapter<Media> adapter, Path path, Media data) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void exp(PersistAdapter<Media> adapter, ZipArchiveOutputStream stream, Media data) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		
		// the metadata
		LOGGER.trace("Exporting media metadata '{}'", data.getName());
		Path path = mpr.getExportPath(data);
		ZipArchiveEntry e = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putArchiveEntry(e);
		JsonIO.write(stream, data);
		stream.closeArchiveEntry();
		
		// the media
		LOGGER.trace("Exporting media '{}'", data.getName());
		path = mpr.getExportMediaPath(data);
		e = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putArchiveEntry(e);
		Files.copy(mpr.getMediaPath(data), stream);
		stream.closeArchiveEntry();
		
		// the image (video only)
		if (data.getMediaType() == MediaType.VIDEO) {
			LOGGER.trace("Exporting media image '{}'", data.getName());
			path = mpr.getExportImagePath(data);
			e = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(path.toString()));
			stream.putArchiveEntry(e);
			Files.copy(mpr.getImagePath(data), stream);
			stream.closeArchiveEntry();
		}
		
		// the thumb
		LOGGER.trace("Exporting media thumbnail '{}'", data.getName());
		path = mpr.getExportThumbPath(data);
		e = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putArchiveEntry(e);
		Files.copy(mpr.getThumbPath(data), stream);
		stream.closeArchiveEntry();
	}

	@Override
	public DataImportResult<Media> imp(PersistAdapter<Media> adapter, Path path) throws IOException {
		DataImportResult<Media> result = new DataImportResult<>();
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		
		String name = path.getFileName().toString();
		
		// check for praisenter format package format by reading any metadata files first
		LOGGER.trace("Searching contents of '{}' for media metadata", name);
		List<Media> metadata = new ArrayList<>();
		List<String> files = new ArrayList<>();
		
		// NOTE: Native java.util.zip package can't support zips 4GB or bigger or elements 2GB or bigger
        try (ZipFile zipFile = ZipFile.builder().setPath(path).get()) {
        	Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        	while (entries.hasMoreElements()) {
        		ZipArchiveEntry entry = entries.nextElement();
        		
        		if (entry.isDirectory()) 
        			continue;
        		
        		if (!zipFile.canReadEntryData(entry)) {
        			LOGGER.warn("Unable to read entry '{}'. This is usually caused by encryption or an unsupported compression algorithm.", entry.getName());
        			continue;
        		}
        		
				String entryName = entry.getName();
				// read all entries
				files.add(entryName);

				// wrap the stream in a stream that can reset
				try (InputStream entryStream = zipFile.getInputStream(entry);
					BufferedInputStream zbis = new BufferedInputStream(entryStream)) {
					zbis.mark(Integer.MAX_VALUE);

					// check if the entry is a media json file
					LOGGER.trace("Checking if '{}' is media metadata", entryName);
					if (MimeType.JSON.check(zbis, entryName)) {
						zbis.reset();
						try {
							// read the stream to the end
							byte[] data = Streams.read(zbis);
							ByteArrayInputStream bais = new ByteArrayInputStream(data);
							PraisenterFormat format = JsonIO.getPraisenterFormat(bais);
							
							// check the format
							if (format != null && format.is(Media.class)) {
								LOGGER.trace("Entry '{}' is media metadata", entryName);
								bais.reset();
								Media media = JsonIO.read(bais, Media.class);
								if (media.getMediaType() == MediaType.IMAGE) {
									media.setMediaImagePath(mpr.getMediaPath(media));	
								} else if (media.getMediaType() == MediaType.AUDIO) {
									media.setMediaImagePath(mpr.getThumbPath(media));
								} else {
									media.setMediaImagePath(mpr.getImagePath(media));
								}
								media.setMediaPath(mpr.getMediaPath(media));
								media.setMediaThumbnailPath(mpr.getThumbPath(media));
								metadata.add(media);
							}
						} catch (Exception ex) {
							LOGGER.warn("Failed to read resource '" + entryName + "' as a Media object.", ex);
						}
					}
				}
			}
		}
		
		if (!metadata.isEmpty()) {
			LOGGER.trace("Media metadata found in '{}'", name);
			for (Media media : metadata) {
				LOGGER.trace("Importing '{}'", media.getName());
				DataImportResult<Media> res = this.load(adapter, path, media, files);
				result.add(res);
			}
		}
		
		return result;
	}
	
	private DataImportResult<Media> load(PersistAdapter<Media> adapter, Path path, Media media, List<String> files) throws IOException {
		DataImportResult<Media> result = new DataImportResult<>();
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		
		// get the export paths for the media
		String jep = FilenameUtils.separatorsToUnix(mpr.getExportPath(media).toString()); 	 // /{exportPath}/media/{id}.json
		String mep = FilenameUtils.separatorsToUnix(mpr.getExportMediaPath(media).toString()); // /{exportPath}/media/media/{id}.{ext}
		String iep = FilenameUtils.separatorsToUnix(mpr.getExportImagePath(media).toString()); // /{exportPath}/media/images/{id}.jpg
		String tep = FilenameUtils.separatorsToUnix(mpr.getExportThumbPath(media).toString()); // /{exportPath}/media/thumbs/{id}.png

		// get the destination file paths for the media
		Path jp = mpr.getPath(media);
		Path mp = mpr.getMediaPath(media);
		Path ip = mpr.getImagePath(media);
		Path tp = mpr.getThumbPath(media);

		// backup paths
		// Path bdp = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "dpback"));
		// Path bmp = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "mpback"));
		// Path bip = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "ipback"));
		// Path btp = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "tpback"));
		
		// verify all components exist
		LOGGER.trace("Verifying all files exist for '{}' (media, image, thumb, etc)", media.getName());
		boolean mpFound = false;
		boolean ipFound = media.getMediaType() != MediaType.VIDEO; // image is only for video media
		boolean tpFound = false;
		for (String entry : files) {
			if (entry.equals(mep)) mpFound = true;
			if (entry.equals(iep)) ipFound = true;
			if (entry.equals(tep)) tpFound = true;
		}
		
		if (mpFound && ipFound && tpFound) {
			LOGGER.trace("All files exist for '{}', attempting import", media.getName());
			// lock the file path since it may exist
			LOGGER.trace("Getting lock for '{}'", media.getId());
			synchronized(adapter.getLock(media.getId())) {
				LOGGER.trace("Lock for '{}' obtained", media.getId());

				// The item will only show up in Praisenter if the metadata file
				// exists in the library location
				boolean jExists = Files.exists(jp);
				boolean mExists = Files.exists(mp);
				boolean iExists = Files.exists(ip);
				boolean tExists = Files.exists(tp);
				boolean isUpdate = jExists;
				boolean retainBackup = false;
				boolean jOverwritten = false;
				boolean mOverwritten = false;
				boolean iOverwritten = false;
				boolean tOverwritten = false;
				Path tempPath = null;
				Path backPath = null;
				try {
					// create a temp location to unzip the files to
					tempPath = Files.createTempDirectory(mpr.getTempPath(), "IMPORT");
					Path jtp = tempPath.resolve(mpr.getFileName(media));
					Path mtp = tempPath.resolve(Path.of("media"), mpr.getMediaFileName(media));			
					Path itp = tempPath.resolve(Path.of("image"), mpr.getImageFileName(media));
					Path ttp = tempPath.resolve(Path.of("thumb"), mpr.getThumbFileName(media));
					Files.createDirectories(mtp.getParent());
					Files.createDirectories(itp.getParent());
					Files.createDirectories(ttp.getParent());
					
					// create a temp location to backup existing files
					backPath = Files.createTempDirectory(mpr.getTempPath(), "BACK");
					Path jbp = backPath.resolve(mpr.getFileName(media));
					Path mbp = backPath.resolve(Path.of("media"), mpr.getMediaFileName(media));
					Path ibp = backPath.resolve(Path.of("image"), mpr.getImageFileName(media));
					Path tbp = backPath.resolve(Path.of("thumb"), mpr.getThumbFileName(media));
					Files.createDirectories(mbp.getParent());
					Files.createDirectories(ibp.getParent());
					Files.createDirectories(tbp.getParent());

					// unzip the contents to a temp location first
					FileHelper.unzip(path, tempPath, (entry, defaultPath) -> {
						String name = entry.getName();
						if (name.equals(jep)) 
							return jtp;
						if (name.equals(mep))
							return mtp;
						if (media.getMediaType() == MediaType.VIDEO && name.equals(iep))
							return itp; // image is only for video
						if (name.equals(tep))
							return ttp;

						// skip the file
						return null;
					});

					// backup existing files before moving new ones into place
					if (Files.exists(jp)) Files.copy(jp, jbp, StandardCopyOption.REPLACE_EXISTING);
					if (Files.exists(mp)) Files.copy(mp, mbp, StandardCopyOption.REPLACE_EXISTING);
					if (Files.exists(ip)) Files.copy(ip, ibp, StandardCopyOption.REPLACE_EXISTING);
					if (Files.exists(tp)) Files.copy(tp, tbp, StandardCopyOption.REPLACE_EXISTING);

					try {
						// move the extracted files from the temp location to their final destination
						jOverwritten = true;
						FileHelper.moveFile(jtp, jp, FileHelper.FALLBACK_COPY); 
						
						mOverwritten = true;
						FileHelper.moveFile(mtp, mp, FileHelper.FALLBACK_COPY); 

						if (media.getMediaType() == MediaType.VIDEO) {
							iOverwritten = true;
							FileHelper.moveFile(itp, ip, FileHelper.FALLBACK_COPY); 
						}

						tOverwritten = true;
						FileHelper.moveFile(ttp, tp, FileHelper.FALLBACK_COPY); 

						if (isUpdate) {
							result.getUpdated().add(media);
						} else {
							result.getCreated().add(media);
						}
					} catch (IOException ex) {
						// if the files didn't exist in the first place
						// then delete them, since we can't trust if they
						// were successfully moved or not
						if (!jExists) try { Files.deleteIfExists(jp); } catch (IOException e) { LOGGER.warn("Failed to delete '{}' during recovery", jp.toAbsolutePath(), e); }
						if (!mExists) try { Files.deleteIfExists(mp); } catch (IOException e) { LOGGER.warn("Failed to delete '{}' during recovery", mp.toAbsolutePath(), e); }
						if (!iExists) try { Files.deleteIfExists(ip); } catch (IOException e) { LOGGER.warn("Failed to delete '{}' during recovery", ip.toAbsolutePath(), e); }
						if (!tExists) try { Files.deleteIfExists(tp); } catch (IOException e) { LOGGER.warn("Failed to delete '{}' during recovery", tp.toAbsolutePath(), e); }
						
						// restore backup files
						if (jOverwritten && Files.exists(jbp)) {
							try { 
								FileHelper.moveFile(jbp, jp, FileHelper.FALLBACK_COPY); 
							} catch (IOException e) { 
								retainBackup = true; 
								LOGGER.error("Failed to restore '{}' during recovery, you can find the backup file at '{}'", jp.toAbsolutePath(), jbp.toAbsolutePath(), e); 
							}
						}

						if (mOverwritten && Files.exists(mbp)) {
							try { 
								FileHelper.moveFile(mbp, mp, FileHelper.FALLBACK_COPY); 
							} catch (IOException e) { 
								retainBackup = true; 
								LOGGER.error("Failed to restore '{}' during recovery, you can find the backup file at '{}'", mp.toAbsolutePath(), mbp.toAbsolutePath(), e); 
							}
						}

						if (iOverwritten && Files.exists(ibp)) {
							try { 
								FileHelper.moveFile(ibp, ip, FileHelper.FALLBACK_COPY); 
							} catch (IOException e) { 
								retainBackup = true; 
								LOGGER.error("Failed to restore '{}' during recovery, you can find the backup file at '{}'", ip.toAbsolutePath(), ibp.toAbsolutePath(), e); 
							}
						}

						if (tOverwritten && Files.exists(tbp)) {
							try { 
								FileHelper.moveFile(tbp, tp, FileHelper.FALLBACK_COPY); 
							} catch (IOException e) { 
								retainBackup = true; 
								LOGGER.error("Failed to restore '{}' during recovery, you can find the backup file at '{}'", tp.toAbsolutePath(), tbp.toAbsolutePath(), e); 
							}
						}

						throw ex;
					}
				} catch (IOException ex) {
					result.getErrors().add(ex);
					throw ex;
				} finally {
					try {
						if (tempPath != null) FileHelper.deleteDirectory(tempPath, false);
					} catch (IOException e) {
						LOGGER.warn("Failed to clean up temp directory '{}'", tempPath.toAbsolutePath(), e);
					}
					try {
						if (backPath != null && !retainBackup) FileHelper.deleteDirectory(backPath, false);
					} catch (IOException e) {
						LOGGER.warn("Failed to clean up backup directory '{}'", backPath.toAbsolutePath(), e);
					}
				}
			}
		} else {
			result.getErrors().add(new Exception("The given archive file doesn't include the proper data to import '" + media.getName() + "'."));
		}
		
		return result;
	}
}
