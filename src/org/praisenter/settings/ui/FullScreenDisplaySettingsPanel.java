package org.praisenter.settings.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;

import org.praisenter.display.FullScreenDisplay;
import org.praisenter.display.GraphicsComponent;
import org.praisenter.resources.Messages;
import org.praisenter.settings.RootSettings;

/**
 * Panel used to manage a full screen display.
 * @author William Bittle
 * @param <E> the {@link RootSettings} type
 * @param <T> the {@link FullScreenDisplay} type
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class FullScreenDisplaySettingsPanel<E extends RootSettings<E>, T extends FullScreenDisplay> extends DisplaySettingsPanel<E, T> {
	/** The version id */
	private static final long serialVersionUID = -1337083972725098754L;
	
	/** The panel to setup the background */
	protected GraphicsComponentSettingsPanel<GraphicsComponent> pnlBackground;
	
	/**
	 * Minimal constructor.
	 * @param settings the display settings to setup
	 * @param displaySize the display target size
	 */
	public FullScreenDisplaySettingsPanel(E settings, Dimension displaySize) {
		super(settings, displaySize);
		
		// add the image background panel
		this.pnlBackground = new GraphicsComponentSettingsPanel<GraphicsComponent>(((FullScreenDisplay)this.display).getBackgroundComponent());
		this.pnlBackground.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.setup.background.name")),
				BorderFactory.createEmptyBorder(5, 0, 0, 0)));
		this.pnlBackground.addPropertyChangeListener(this);
	}
}
