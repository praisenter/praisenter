package org.praisenter.data.workspace;

import java.nio.file.Path;

public final class WorkspacePathResolver {
	private static final String SEARCH_INDEX_RELATIVE_PATH = "_index";
	private static final String MEDIA_RELATIVE_PATH = "media";
	private static final String SONGS_RELATIVE_PATH = "songs";
	private static final String SLIDES_RELATIVE_PATH = "slides";
	private static final String BIBLES_RELATIVE_PATH = "bibles";
	private static final String LOGS_RELATIVE_PATH = "logs";
	private static final String CONFIGURATION_FILE = "workspace.json";
	
	private final Path workspacePath;
	
	public WorkspacePathResolver(Path workspacePath) {
		this.workspacePath = workspacePath;
	}
	
	public Path getBasePath() {
		return this.workspacePath;
	}
	
	public Path getSearchIndexPath() {
		return this.workspacePath.resolve(WorkspacePathResolver.SEARCH_INDEX_RELATIVE_PATH);
	}
	
	public Path getConfigurationPath() {
		return this.workspacePath;
	}
	
	public Path getLogsPath() {
		return this.workspacePath.resolve(WorkspacePathResolver.LOGS_RELATIVE_PATH);
	}
	
	public Path getConfigurationFilePath() {
		return this.workspacePath.resolve(WorkspacePathResolver.CONFIGURATION_FILE);
	}
	
	public Path getMediaPath() {
		return this.workspacePath.resolve(WorkspacePathResolver.MEDIA_RELATIVE_PATH);
	}
	
	public Path getSongsPath() {
		return this.workspacePath.resolve(WorkspacePathResolver.SONGS_RELATIVE_PATH);
	}
	
	public Path getSlidesPath() {
		return this.workspacePath.resolve(WorkspacePathResolver.SLIDES_RELATIVE_PATH);
	}
	
	public Path getBiblesPath() {
		return this.workspacePath.resolve(WorkspacePathResolver.BIBLES_RELATIVE_PATH);
	}
}
