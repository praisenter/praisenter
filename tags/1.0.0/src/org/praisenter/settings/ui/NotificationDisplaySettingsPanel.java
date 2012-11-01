package org.praisenter.settings.ui;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JTabbedPane;

import org.praisenter.display.DisplayFactory;
import org.praisenter.display.GraphicsComponent;
import org.praisenter.display.NotificationDisplay;
import org.praisenter.display.TextComponent;
import org.praisenter.resources.Messages;
import org.praisenter.settings.NotificationSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.settings.TextComponentSettings;

/**
 * Panel for notification settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationDisplaySettingsPanel extends DisplaySettingsPanel<NotificationSettings, NotificationDisplay> {
	/** The version id */
	private static final long serialVersionUID = -5766205823834580532L;

	/** The scripture title property name */
	protected static final String PROPERTY_TEXT = "Text";
	
	/**
	 * Minimal constructor.
	 * @param settings the bible display settings
	 * @param displaySize the target display size
	 */
	public NotificationDisplaySettingsPanel(NotificationSettings settings, Dimension displaySize) {
		super(settings, displaySize);
		
		this.pnlDisplayPreview.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));
		
		TextComponentSettingsPanel pnlTitle = new TextComponentSettingsPanel(this.display.getTextComponent());
		pnlTitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		pnlTitle.addPropertyChangeListener(this);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("panel.notification.setup.title.name"), pnlTitle);
		
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
	protected NotificationDisplay getDisplay(NotificationSettings settings, Dimension displaySize) {
		return DisplayFactory.getDisplay(settings, displaySize, Messages.getString("display.notification.defaultText"));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ui.DisplaySettingsPanel#setSettingsFromComponents()
	 */
	@Override
	protected void setSettingsFromComponents() throws SettingsException {
		TextComponentSettings xSet = this.settings.getTextSettings();
		TextComponent xCom = this.display.getTextComponent();
		xSet.setSettings(xCom);
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
