package org.praisenter.ui.upgrade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.data.workspace.WorkspaceManager;
import org.praisenter.utility.ClasspathLoader;

/**
 * This class handles install/upgrade for both the application files
 * and workspace files.
 * <p>
 * A praisenter install has one working location for the application itself
 * in the user's home directory under the .praisenter3 folder.
 * <p>
 * Each workspace tracks a configuration file with the current version of
 * the workspace.
 * @author WBittle
 *
 */
public final class InstallUpgradeHandler {
	private final InstallUpgradeLogger logger;
	private final FileUpgradeHelper fileUpgradeHelper;
	private final List<VersionUpgradeHandler> upgradeHandlers;
	
	public InstallUpgradeHandler() {
		this.logger = new InstallUpgradeLogger();
		
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		Path archivePath = Paths.get(Constants.UPGRADE_ARCHIVE_ABSOLUTE_PATH, now.toString().replaceAll("-|:", ""));
		this.fileUpgradeHelper = new FileUpgradeHelper(archivePath); 
		
		this.upgradeHandlers = new ArrayList<>();
		this.upgradeHandlers.add(new Upgrade300OrEarlier());
		this.upgradeHandlers.add(new Upgrade316OrEarlier());
	}
	
	public void performApplicationInstallOrUpgradeSteps() {
		// ensure folders exist
		logger.debug("Making sure all application folders exist");
		this.buildFolders();
		
		// make sure these files exist
		logger.debug("Making sure all application files exist");
		this.installOrUpgradeLog4jConfiguration(false);
		this.installOrUpgradeDefaultLocale(false);
		this.installOrUpgradeDefaultStyles(false);
		
		// check for existing version file
		logger.debug("Checking if version file exits");
		Path path = Paths.get(Constants.UPGRADE_ABSOLUTE_PATH, Constants.UPGRADE_VERSION_FILENAME);
		if (!Files.exists(path)) {
			logger.info("Initial install detected, setting up log4j, the default locale, and default theme.");
			// it's a new install, so initialize the file data
			this.writeVersionFile();
			return;
		}
		
		// if it's exists, then try to read it
		logger.debug("Reading the version file");
		Version currentVersion = this.readVersionFile(path);
		if (currentVersion == null) {
			// the file wasn't found, was empty or in the wrong format
			// or we encountered an IO error
			throw new RuntimeException("Failed to read the '" + path.toAbsolutePath() + "' file which determines whether Praisenter should perform install or upgrade steps. Try to run the application again to confirm it's not an intermittent issue. Make sure the file is in the correct format MAJOR.MINOR.REVISION where each component is an integer.");
		}
		
		logger.debug("Current installed version is: '" + currentVersion + "' runtime version is: '" + Version.VERSION + "'");
		Version runtimeVersion = Version.VERSION;
		
		// does the runtime version match the last runtime version?
		if (currentVersion.equals(runtimeVersion)) {
			logger.info("Versions match, so no upgrade/install steps required");
			// the versions are the same, so no upgrade/install steps needed
			return;
		}
		
		// is the runtime version lower than the last runtime version?
		if (currentVersion.isGreaterThan(runtimeVersion)) {
			logger.warn("The current version is greater than the runtime version. USE AT YOUR OWN RISK.");
			// this should never happen, but could if someone upgrades
			// then tries to down grade.  The behavior would be unknown
			// but we'll allow it for now
			return;
		}
		
		logger.info("Performing upgrade steps for runtime version: '" + runtimeVersion + "' from current version: '" + currentVersion + "'");

		// otherwise, perform any upgrade steps
		this.installOrUpgradeLog4jConfiguration(true);
		this.installOrUpgradeDefaultLocale(true);
		this.installOrUpgradeDefaultStyles(true);
		this.archiveAndRemoveThemesFolder();
		this.writeVersionFile();

		logger.info("All install/upgrade initialization steps have completed successfully.");
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
	
	private void archiveAndRemoveThemesFolder() {
		final String folder = "themes";
		// from 3.0.0 and before, the app was styled using css only in the themes folder
		Path path = Paths.get(Constants.ROOT_PATH, folder);
		if (Files.exists(path)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				for (Path file : stream) {
					try {
						// attempt to move them to the archive
						this.fileUpgradeHelper.archiveFile(file, folder);
						// remove the file
						Files.deleteIfExists(file);
					} catch (IOException ex) {
						logger.warn("Failed to archive or delete: '" + file.toAbsolutePath() + "'");
					}
				}
			} catch (IOException ex) {
				logger.warn("Failed to archive or delete files in folder: '" + path.toAbsolutePath() + "'");
			}
			
			// then delete the folder
			try {
				Files.deleteIfExists(path);
			} catch (IOException ex) {
				logger.warn("Failed to delete folder: '" + path.toAbsolutePath() + "'");
			}
		}
	}
	
	private void installOrUpgradeClasspathFile(String filePath, String fileName, String classpathPath, boolean isUpgrade) {
		Path path = Paths.get(filePath, fileName);
		if (Files.exists(path)) {
			logger.debug("The file '" + fileName + "' exits.");
			try {
				if (isUpgrade && !this.fileUpgradeHelper.isFileContentIdentical(path, classpathPath)) {
					logger.debug("The file '" + fileName + "' exits, we're doing an upgrade, and the file content is different.");
					logger.debug("Archiving file '" + fileName + "'.");
					// archive off the file
					this.fileUpgradeHelper.archiveFile(path);
					// delete the current file
					logger.debug("Replacing file '" + fileName + "' with version from the jar.");
					Files.delete(path);
					// then replace the version
					ClasspathLoader.copy(classpathPath, path);
				}
			} catch (NoSuchAlgorithmException e) {
				// shouldn't happen, we should only use
				// algos that exist within the deployed
				// JVM
				logger.fatal("Failed to check the '" + fileName + "' file hashes because SHA-256 wasn't a support digest.");
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
				logger.debug("The file '" + fileName + "' does not exist. Copying file from jar.");
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
			logger.debug("Version file read with content '" + text + "'. Parsing...");
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
			logger.debug("Removing version file if it exits...");
			Files.deleteIfExists(path);
			logger.debug("Writing version file...");
			try (FileWriter fw = new FileWriter(path.toFile(), Charset.forName("UTF-8"));
				 BufferedWriter bw = new BufferedWriter(fw)) {
				bw.write(Version.STRING);
				bw.newLine();
			}
			logger.debug("Version file created successfully.");
		} catch (IOException e) {
			logger.fatal("Failed to write version file '" + path.toAbsolutePath() + "'");
			throw new RuntimeException(e);
		}
	}

	public boolean isUpgradeRequired(Logger workspaceLogger, Version workspaceVersion) {
		// now compare with the runtime version
		if (workspaceVersion == null) {
			// we couldn't parse the runtime version
			workspaceLogger.warn("We couldn't determine the current workspace version. No upgrade steps will be performed.");
		} else if (workspaceVersion.equals(Version.VERSION)) {
			// then there's nothing to do
			workspaceLogger.debug("Workspace version and runtime version match: '{}'. No upgrade steps necessary.", workspaceVersion);
		} else if (workspaceVersion.isLessThan(Version.VERSION)) {
			// then the workspace is older than the current runtime version
			// perform any upgrade steps necessary to the workspace
			workspaceLogger.debug("Workspace version '{}' is older than runtime version '{}'. Performing workspace upgrade steps.", workspaceVersion, Version.VERSION);
			return true;
		} else if (workspaceVersion.isGreaterThan(Version.VERSION)) {
			// the workspace is newer than the runtime version, this could be a problem
			// but we're just going to warn about it
			workspaceLogger.warn("Workspace version '{}' is newer than runtime version '{}'. The behavior in this scenario is not defined.", workspaceVersion, Version.VERSION);
		}
		
		return false;
	}
	
	public CompletableFuture<Void> performWorkspacePreLoadUpgradeSteps(Logger workspaceLogger, WorkspaceManager workspaceManager, Version workspaceVersion) {
		// we'll need to be very careful here if we want to prevent issues where we leave the 
		// application files in a bad state. That might mean we need to manually archive the 
		// library so we can revert or something like that
		
		// do we need to upgrade?
		if (!this.isUpgradeRequired(workspaceLogger, workspaceVersion)) {
			return CompletableFuture.completedFuture(null);
		}
		
		// run through all the upgrade handlers sequentially
		CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
		for (VersionUpgradeHandler handler : this.upgradeHandlers) {
			if (handler.isUpgradeRequired(workspaceVersion)) {
				future = future.thenComposeAsync((v) -> {
					workspaceLogger.debug("Executing upgrade handler: '{}'", handler.getClass().getName());
					return handler.beforeLoadUpgrade(workspaceLogger, workspaceManager);
				});
			}
		}

		return future.thenRun(() -> {
			workspaceLogger.debug("Pre-load upgrade handlers completed successfully");
		});
	}
	
	public CompletableFuture<Void> performWorkspacePostLoadUpgradeSteps(Logger workspaceLogger, WorkspaceManager workspaceManager, Version workspaceVersion) {
		// we'll need to be very careful here if we want to prevent issues where we leave the 
		// application files in a bad state. That might mean we need to manually archive the 
		// library so we can revert or something like that

		// do we need to upgrade?
		if (!this.isUpgradeRequired(workspaceLogger, workspaceVersion)) {
			return CompletableFuture.completedFuture(null);
		}
		
		// run through all the upgrade handlers sequentially
		CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
		for (VersionUpgradeHandler handler : this.upgradeHandlers) {
			if (handler.isUpgradeRequired(workspaceVersion)) {
				future = future.thenComposeAsync((v) -> {
					workspaceLogger.debug("Executing upgrade handler: '{}'", handler.getClass().getName());
					return handler.afterLoadUpgrade(workspaceLogger, workspaceManager);
				});
			}
		}

		return future.thenRun(() -> {
			workspaceLogger.debug("Post-load upgrade handlers completed successfully");
		});
	}
}
