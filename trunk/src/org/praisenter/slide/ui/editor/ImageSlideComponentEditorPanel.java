package org.praisenter.slide.ui.editor;

import org.praisenter.media.ui.MediaLibraryPanel;
import org.praisenter.slide.media.ImageMediaComponent;

public class ImageSlideComponentEditorPanel extends SlideComponentEditorPanel<ImageMediaComponent> {
	// select from media library
	
	protected MediaLibraryPanel pnlMediaLibrary;
	
	public ImageSlideComponentEditorPanel() {
		pnlMediaLibrary = new MediaLibraryPanel();
		
		
	}
}
