package org.praisenter.settings.ui;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JTabbedPane;

import org.praisenter.display.BibleDisplay;
import org.praisenter.display.DisplayFactory;
import org.praisenter.display.GraphicsComponent;
import org.praisenter.display.TextComponent;
import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.GraphicsComponentSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.settings.TextComponentSettings;

/**
 * Panel for bible settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleDisplaySettingsPanel extends FullScreenDisplaySettingsPanel<BibleSettings, BibleDisplay> {
	/** The version id */
	private static final long serialVersionUID = -5766205823834580532L;

	/** The scripture title property name */
	protected static final String PROPERTY_SCRIPTURE_TITLE = "ScriptureTitle";
	
	/** The scripture text property name */
	protected static final String PROPERTY_SCRIPTURE_TEXT = "ScriptureText";
	
	/**
	 * Minimal constructor.
	 * @param settings the bible display settings
	 * @param displaySize the target display size
	 */
	public BibleDisplaySettingsPanel(BibleSettings settings, Dimension displaySize) {
		super(settings, displaySize);
		
		this.pnlDisplayPreview.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));
		
		TextComponentSettingsPanel pnlTitle = new TextComponentSettingsPanel(this.display.getScriptureTitleComponent());
		pnlTitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		pnlTitle.addPropertyChangeListener(this);
		
		TextComponentSettingsPanel pnlText = new TextComponentSettingsPanel(this.display.getScriptureTextComponent());
		pnlText.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		pnlText.addPropertyChangeListener(this);
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("panel.setup.background.name"), this.pnlBackground);
		tabs.addTab(Messages.getString("panel.bible.setup.title.name"), pnlTitle);
		tabs.addTab(Messages.getString("panel.bible.setup.text.name"), pnlText);
		
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
	protected BibleDisplay getDisplay(BibleSettings settings, Dimension displaySize) {
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
		TextComponentSettings tSet = this.settings.getScriptureTitleSettings();
		TextComponent tCom = this.display.getScriptureTitleComponent();
		tSet.setSettings(tCom);
		
		TextComponentSettings xSet = this.settings.getScriptureTextSettings();
		TextComponent xCom = this.display.getScriptureTextComponent();
		xSet.setSettings(xCom);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ui.DisplaySettingsPanel#isInsideAny(java.awt.Point)
	 */
	@Override
	protected boolean isInsideAny(Point displayPoint) {
		// make sure the display is not null
		if (this.display != null) {
			if (this.isInside(displayPoint, this.display.getScriptureTextComponent())) {
				return true;
			}
			if (this.isInside(displayPoint, this.display.getScriptureTitleComponent())) {
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
			if (this.isInside(displayPoint, this.display.getScriptureTextComponent())) {
				return display.getScriptureTextComponent();
			}
			if (this.isInside(displayPoint, this.display.getScriptureTitleComponent())) {
				return display.getScriptureTitleComponent();
			}
		}
		return null;
	}
}
