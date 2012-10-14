package org.praisenter.settings.ui;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JTabbedPane;

import org.praisenter.display.DisplayFactory;
import org.praisenter.display.GraphicsComponent;
import org.praisenter.display.SongDisplay;
import org.praisenter.display.TextComponent;
import org.praisenter.resources.Messages;
import org.praisenter.settings.GraphicsComponentSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.settings.SongSettings;
import org.praisenter.settings.TextComponentSettings;

/**
 * Panel for song settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongDisplaySettingsPanel extends FullScreenDisplaySettingsPanel<SongSettings, SongDisplay> {
	/** The version id */
	private static final long serialVersionUID = 2104445426415028234L;
	
	/**
	 * Minimal constructor.
	 * @param settings the song display settings
	 * @param displaySize the target display size
	 */
	public SongDisplaySettingsPanel(SongSettings settings, Dimension displaySize) {
		super(settings, displaySize);
		
		this.pnlDisplayPreview.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));
		
		TextComponentSettingsPanel pnlText = new TextComponentSettingsPanel(this.display.getTextComponent());
		pnlText.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		pnlText.addPropertyChangeListener(this);
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("panel.setup.background.name"), this.pnlBackground);
		tabs.addTab(Messages.getString("panel.song.setup.text.name"), pnlText);
		
		JTabbedPane pTabs = new JTabbedPane();
		pTabs.addTab(Messages.getString("panel.display.setup.preview"), this.pnlDisplayPreview);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				// don't allow the group to resize
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(tabs))
						// only resize the preview
				.addComponent(pTabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		
		layout.setVerticalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(tabs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(pTabs));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ui.DisplaySettingsPanel#getDisplay(org.praisenter.settings.RootSettings, java.awt.Dimension)
	 */
	@Override
	protected SongDisplay getDisplay(SongSettings settings, Dimension displaySize) {
		return DisplayFactory.getDisplay(settings, displaySize);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ui.DisplaySettingsPanel#setSettingsFromComponents()
	 */
	@Override
	protected void setSettingsFromComponents() throws SettingsException {
		// copy the still background settings
		GraphicsComponentSettings<GraphicsComponent> bSet = this.settings.getBackgroundSettings();
		GraphicsComponent bCom = this.display.getBackgroundComponent();
		bSet.setSettings(bCom);
		
		// save the title settings
		TextComponentSettings tSet = this.settings.getTextSettings();
		TextComponent tCom = this.display.getTextComponent();
		tSet.setSettings(tCom);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ui.DisplaySettingsPanel#isInsideAny(java.awt.Point)
	 */
	@Override
	protected boolean isInsideAny(Point displayPoint) {
		// make sure the display is not null
		if (this.display != null) {
			if (this.isInside(displayPoint, this.display.getTextComponent())) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ui.DisplaySettingsPanel#getGraphicsComponent(java.awt.Point)
	 */
	@Override
	protected GraphicsComponent getGraphicsComponent(Point displayPoint) {
		if (this.display != null) {
			if (this.isInside(displayPoint, this.display.getTextComponent())) {
				return display.getTextComponent();
			}
		}
		return null;
	}
}
