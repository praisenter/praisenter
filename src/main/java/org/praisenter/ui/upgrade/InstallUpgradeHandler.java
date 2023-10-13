package org.praisenter.ui.upgrade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.utility.ClasspathLoader;

public final class InstallUpgradeHandler {
	private final InstallUpgradeLogger logger;
	private final String archiveFolder;
	
	public InstallUpgradeHandler() {
		this.logger = new InstallUpgradeLogger();
		
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		this.archiveFolder = now.toString().replaceAll("-|:", "");
	}
	
	public void initialize() {
		// ensure folders exist
		this.buildFolders();
		
		// make sure these files exist
		this.installOrUpgradeLog4jConfiguration(false);
		this.installOrUpgradeDefaultLocale(false);
		this.installOrUpgradeDefaultStyles(false);
		
		// check for existing version file
		Path path = Paths.get(Constants.UPGRADE_ABSOLUTE_PATH, Constants.UPGRADE_VERSION_FILENAME);
		if (!Files.exists(path)) {
			logger.info("Initial install detected, setting up log4j, the default locale, and default theme.");
			// it's a new install, so initialize the file data
			this.writeVersionFile();
			return;
		}
		
		// if it's exists, then try to read it
		Version currentVersion = this.readVersionFile(path);
		if (currentVersion == null) {
			// the file wasn't found, was empty or in the wrong format
			// or we encountered an IO error
			throw new RuntimeException("Failed to read the '" + path.toAbsolutePath() + "' file which determines whether Praisenter should perform install or upgrade steps. Try to run the application again to confirm it's not an intermittent issue. Make sure the file is in the correct format MAJOR.MINOR.REVISION where each component is an integer.");
		}
		
		Version runtimeVersion = Version.VERSION;
		
		// does the runtime version match the last runtime version?
		if (currentVersion.equals(runtimeVersion)) {
			// the versions are the same, so no upgrade/install steps needed
			return;
		}
		
		// is the runtime version lower than the last runtime version?
		if (currentVersion.isGreaterThan(runtimeVersion)) {
			// this should never happen, but could if someone upgrades
			// then tries to down grade.  The behavior would be unknown
			// but we'll allow it for now
			return;
		}
		
		logger.info("Performing upgrade steps for runtime version '" + runtimeVersion + "' from last runtime version '" + currentVersion + "'");

		// otherwise, perform any upgrade steps
		this.installOrUpgradeLog4jConfiguration(true);
		this.installOrUpgradeDefaultLocale(true);
		this.installOrUpgradeDefaultStyles(true);
		this.writeVersionFile();

		logger.info("All install/upgrade initialization steps have completed successfully.");
	}
	
	public CompletableFuture<Void> performUpgradeSteps() {
		// we'll need to be very careful here if we want to prevent issues where we leave the 
		// application files in a bad state. That might mean we need to manually archive the 
		// library so we can revert or something like that
		return CompletableFuture.completedFuture(null);
	}
	
	private void buildFolders() {
		final Path[] paths = new Path[] {
				Paths.get(Constants.ROOT_PATH),
				Paths.get(Constants.LOGS_ABSOLUTE_PATH),
				Paths.get(Constants.UPGRADE_ABSOLUTE_PATH),
				Paths.get(Constants.UPGRADE_ARCHIVE_ABSOLUTE_PATH),
				Paths.get(Constants.LOCALES_ABSOLUTE_PATH),
				Paths.get(Constants.STYLES_ABSOLUTE_PATH)
		};
		
		for (Path path : paths) {
			try {
				Files.createDirectories(path);
			} catch (IOException ex) {
				logger.fatal("Failed to create directory '" + path.toAbsolutePath() + "'.");
				throw new RuntimeException(ex);
			}
		}
	}
	
	private void installOrUpgradeLog4jConfiguration(boolean isUpgrade) {
		this.installOrUpgradeClasspathFile(
				Constants.ROOT_PATH, 
				Constants.LOGS_CONFIGURATION_FILENAME, 
				Constants.LOGS_CONFIGURATION_ON_CLASSPATH,
				isUpgrade);
	}
	
	private void installOrUpgradeDefaultLocale(boolean isUpgrade) {
		this.installOrUpgradeClasspathFile(
				Constants.LOCALES_ABSOLUTE_PATH, 
				Constants.LOCALES_DEFAULT_LOCALE_FILENAME, 
				Constants.LOCALES_DEFAULT_LOCALE_ON_CLASSPATH,
				isUpgrade);
	}
	
	private void installOrUpgradeDefaultStyles(boolean isUpgrade) {
		this.installOrUpgradeClasspathFile(
				Constants.STYLES_ABSOLUTE_PATH, 
				Constants.STYLES_BASE_FILENAME, 
				Constants.STYLES_BASE_ON_CLASSPATH,
				isUpgrade);
		this.installOrUpgradeClasspathFile(
				Constants.STYLES_ABSOLUTE_PATH, 
				Constants.STYLES_ICONS_FILENAME, 
				Constants.STYLES_ICONS_ON_CLASSPATH,
				isUpgrade);
		this.installOrUpgradeClasspathFile(
				Constants.STYLES_ABSOLUTE_PATH, 
				Constants.STYLES_ACCENT_FILENAME, 
				Constants.STYLES_ACCENT_ON_CLASSPATH,
				isUpgrade);
	}
	
	private void installOrUpgradeClasspathFile(String filePath, String fileName, String classpathPath, boolean isUpgrade) {
		Path path = Paths.get(filePath, fileName);
		if (Files.exists(path)) {
			try {
				if (isUpgrade && !this.isFileContentIdentical(path, classpathPath)) {
					// archive off the file
					this.archiveFile(path);
					// delete the current file
					Files.delete(path);
					// then replace the version
					ClasspathLoader.copy(classpathPath, path);
				}
			} catch (NoSuchAlgorithmException e) {
				// shouldn't happen, we should only use
				// algos that exist within the deployed
				// JVM
				logger.fatal("Failed to check the '" + fileName + "' file hashes because MD5 wasn't a support digest.");
				throw new RuntimeException(e);
			} catch (FileNotFoundException e) {
				// shouldn't happen since the existing
				// file we're checking for and all directories
				// should be created at this point
				logger.fatal("The file '" + fileName + "' was not found: " + e.getMessage());
				throw new RuntimeException(e);
			} catch (IOException e) {
				// this could happen if there's intermittent IO issues
				// or the file is locked and can't be deleted or other
				// things like that
				logger.fatal("Failed to install/upgrade '" + fileName + "' because: " + e.getMessage());
				throw new RuntimeException(e);
			}
		} else {
			try {
				ClasspathLoader.copy(classpathPath, path);
			} catch (FileAlreadyExistsException e) {
				// shouldn't happen because we already
				// checked that it didn't exist
				logger.fatal("Failed to install/upgrade '" + fileName + "' because the file already existed: " + e.getMessage());
				throw new RuntimeException(e);
			} catch (FileNotFoundException e) {
				// shouldn't happen since the existing
				// file we're checking for and all directories
				// should be created at this point
				logger.fatal("The file '" + fileName + "' was not found: " + e.getMessage());
				throw new RuntimeException(e);
			} catch (IOException e) {
				// this could happen if there's intermittent IO issues
				// or the file is locked and can't be deleted or other
				// things like that
				logger.fatal("Failed to install/upgrade '" + fileName + "' because: " + e.getMessage());
				throw new RuntimeException(e);
			}
		}
	}
	
	private Version readVersionFile(Path path) {
		// then read it
		try (FileReader reader = new FileReader(path.toFile(), Charset.forName("UTF-8"));
			 BufferedReader br = new BufferedReader(reader)) {
			String text = br.readLine();
			return Version.parse(text);
		} catch (FileNotFoundException e) {
			logger.fatal("Version file '" + path.toAbsolutePath() + "' was not found.");
		} catch (IllegalArgumentException e) {
			logger.fatal("Version file '" + path.toAbsolutePath() + "' was not in the correct format MAJOR.MINOR.REVISION where each segment is an integer.");
		} catch (IOException e) {
			logger.fatal("Failed to read the file '" + path.toAbsolutePath() + "': " + e.getMessage());
		} catch (Exception e) {
			logger.fatal("Failed to read the file '" + path.toAbsolutePath() + "': " + e.getMessage());
		}
		
		return null;
	}
	
	private void writeVersionFile() {
		Path path = Paths.get(Constants.UPGRADE_ABSOLUTE_PATH, Constants.UPGRADE_VERSION_FILENAME);
		try {
			Files.deleteIfExists(path);
			try (FileWriter fw = new FileWriter(path.toFile(), Charset.forName("UTF-8"));
				 BufferedWriter bw = new BufferedWriter(fw)) {
				bw.write(Version.STRING);
				bw.newLine();
			}
		} catch (IOException e) {
			logger.fatal("Failed to write version file '" + path.toAbsolutePath() + "'");
			throw new RuntimeException(e);
		}
	}
	
	private void archiveFile(Path path) throws IOException {
		Path folder = Paths.get(Constants.UPGRADE_ARCHIVE_ABSOLUTE_PATH, this.archiveFolder);
		Files.createDirectories(folder);
		Path to = folder.resolve(path.getFileName().toString());
		Files.copy(path, to, StandardCopyOption.REPLACE_EXISTING);
	}
	
	private boolean isFileContentIdentical(Path path, String classpath) throws NoSuchAlgorithmException, IOException {
		try (FileInputStream fis = new FileInputStream(path.toFile());
			 InputStream is = InstallUpgradeHandler.class.getResourceAsStream(classpath)) {
			return this.isFileContentIdentical(fis, is);
		}
	}
	
	private boolean isFileContentIdentical(InputStream is1, InputStream is2) throws IOException, NoSuchAlgorithmException {
		// get a hash of the file
		String hash1 = hash(is1, "MD5");
		String hash2 = hash(is2, "MD5");
		return hash1.equals(hash2);
	}
	
	public static final String hash(InputStream stream, String algorithm) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		DigestInputStream dis = new DigestInputStream(stream, md);

		byte[] data = new byte[4096];
		while (dis.read(data, 0, data.length) != -1) {}
		
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
		    sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return sb.toString();
	}
}
