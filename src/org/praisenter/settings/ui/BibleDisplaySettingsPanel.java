package org.praisenter.settings.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;

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
		
		// put the preview panel in a flow layout
		JPanel pnlPreview = new JPanel();
		pnlPreview.setLayout(new BorderLayout());
		pnlPreview.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()),
						BorderFactory.createEmptyBorder(5, 0, 0, 0)),
				Messages.getString("panel.display.setup.preview")));
		pnlPreview.add(this.pnlDisplayPreview);
		
		TextComponentSettingsPanel pnlTitle = new TextComponentSettingsPanel(this.display.getScriptureTitleComponent());
		pnlTitle.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.bible.setup.title.name")),
				BorderFactory.createEmptyBorder(5, 0, 0, 0)));
		pnlTitle.addPropertyChangeListener(this);
		
		TextComponentSettingsPanel pnlText = new TextComponentSettingsPanel(this.display.getScriptureTextComponent());
		pnlText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.bible.setup.text.name")),
				BorderFactory.createEmptyBorder(5, 0, 0, 0)));
		pnlText.addPropertyChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.pnlBackground, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						// only resize the preview
				.addComponent(pnlPreview, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		
		layout.setVerticalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.pnlBackground, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(pnlPreview));
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
