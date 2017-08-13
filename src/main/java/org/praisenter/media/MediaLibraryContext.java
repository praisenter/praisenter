package org.praisenter.media;

import java.nio.file.Path;

import org.praisenter.ThumbnailSettings;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.tools.Tools;

public final class MediaLibraryContext {
	private final ObservableConfiguration configuration;
	private final ThumbnailSettings thumbnailSettings;
	private final Tools tools;
	
	public MediaLibraryContext(ObservableConfiguration configuration, ThumbnailSettings thumbnailSettings, Tools tools) {
		this.configuration = configuration;
		this.thumbnailSettings = thumbnailSettings;
		this.tools = tools;
	}

	public ObservableConfiguration getConfiguration() {
		return this.configuration;
	}

	public ThumbnailSettings getThumbnailSettings() {
		return thumbnailSettings;
	}
	
	public Tools getTools() {
		return tools;
	}
}
