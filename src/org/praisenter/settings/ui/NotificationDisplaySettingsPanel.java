package org.praisenter.settings.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;

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
		
		// put the preview panel in a flow layout
		JPanel pnlPreview = new JPanel();
		pnlPreview.setLayout(new BorderLayout());
		pnlPreview.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()),
						BorderFactory.createEmptyBorder(5, 0, 0, 0)),
				Messages.getString("panel.display.setup.preview")));
		pnlPreview.add(this.pnlDisplayPreview);
		
		TextComponentSettingsPanel pnlTitle = new TextComponentSettingsPanel(this.display.getTextComponent());
		pnlTitle.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.notification.setup.title.name")),
				BorderFactory.createEmptyBorder(5, 0, 0, 0)));
		pnlTitle.addPropertyChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(pnlTitle))
				.addComponent(pnlPreview));
		
		layout.setVerticalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(pnlTitle))
				.addComponent(pnlPreview));
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
