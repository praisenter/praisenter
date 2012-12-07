package org.praisenter.slide.ui.editor;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.praisenter.slide.RenderableSlideComponent;

/**
 * The panel used to modify the background of a slide.
 * <p>
 * This panel allows the configuration of a paint/image/video background.
 * @author USWIBIT
 *
 */
public class BackgroundEditorPanel extends JPanel {
	private static final String PAINT_CARD = "Paint";
	private static final String IMAGE_CARD = "Image";
	private static final String VIDEO_CARD = "Video";
	
	private RenderableSlideComponent component;
	
	// controls
	
	private JRadioButton rdoPaint;
	private JRadioButton rdoImage;
	private JRadioButton rdoVideo;
	
	private JPanel pnlCards;
	
	public BackgroundEditorPanel(RenderableSlideComponent component) {
		
	}

	public RenderableSlideComponent getComponent() {
		return this.component;
	}

	public void setComponent(RenderableSlideComponent component) {
		this.component = component;
	}
}
