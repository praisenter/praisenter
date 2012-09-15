package org.praisenter.panel.setup;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;

import org.praisenter.display.BibleDisplay;
import org.praisenter.display.FloatingDisplayComponent;
import org.praisenter.display.TextComponent;
import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleDisplaySettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.settings.TextSettings;

/**
 * Panel for bible display settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleDisplaySetupPanel extends DisplaySetupPanel<BibleDisplaySettings, BibleDisplay> {
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
	public BibleDisplaySetupPanel(BibleDisplaySettings settings, Dimension displaySize) {
		super(settings, displaySize);
		
		// put the preview panel in a flow layout
		JPanel pnlPreview = new JPanel();
		pnlPreview.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlPreview.add(this.pnlDisplayPreview);
		
		TextComponentSetupPanel pnlTitle = new TextComponentSetupPanel(this.display.getScriptureTitleComponent());
		pnlTitle.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("display.bible.title.name")));
		pnlTitle.addPropertyChangeListener(this);
		
		TextComponentSetupPanel pnlBody = new TextComponentSetupPanel(this.display.getScriptureTextComponent());
		pnlBody.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("display.bible.body.name")));
		pnlBody.addPropertyChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(pnlPreview)
				.addComponent(this.pnlColorBackground, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlImageBackground, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlTitle)
				.addComponent(pnlBody));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlPreview)
				.addComponent(this.pnlColorBackground)
				.addComponent(this.pnlImageBackground)
				.addComponent(pnlTitle)
				.addComponent(pnlBody));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.DisplaySetupPanel#setSettingsFromComponents()
	 */
	@Override
	protected void setSettingsFromComponents() throws SettingsException {
		// save the background settings
		super.setSettingsFromComponents();
		
		// save the title settings
		TextSettings tSet = this.settings.getScriptureTitleSettings();
		TextComponent tCom = this.display.getScriptureTitleComponent();
		tSet.setSettings(tCom);
		
		TextSettings bSet = this.settings.getScriptureTextSettings();
		TextComponent bCom = this.display.getScriptureTextComponent();
		bSet.setSettings(bCom);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.DisplaySetupPanel#isInsideAny(java.awt.Point)
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
	 * @see org.praisenter.panel.setup.DisplaySetupPanel#getFloatingDisplayComponent(java.awt.Point)
	 */
	@Override
	protected FloatingDisplayComponent getFloatingDisplayComponent(Point displayPoint) {
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
