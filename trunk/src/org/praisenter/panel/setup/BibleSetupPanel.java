package org.praisenter.panel.setup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Bibles;
import org.praisenter.display.BibleDisplay;
import org.praisenter.panel.bible.BibleListCellRenderer;
import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.SettingsException;

/**
 * Panel used to set the {@link BibleSettings}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleSetupPanel extends JPanel implements SetupPanel {
	/** The verison id */
	private static final long serialVersionUID = 460972285830298448L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(BibleSetupPanel.class);
	
	/** The settings being configured */
	protected BibleSettings settings;

	/** The available bibles */
	private JComboBox<Bible> cmbBiblesPrimary;
	
	/** The secondary bible listing */
	private JComboBox<Bible> cmbBiblesSecondary;
	
	/** The checkbox to use the secondary bible */
	private JCheckBox chkUseSecondaryBible;
	
	/** The checkbox to include/exclude the apocrypha */
	private JCheckBox chkIncludeApocrypha;
	
	/** The panel used to setup the {@link BibleDisplay} */
	protected BibleDisplaySetupPanel pnlDisplay;
	
	/**
	 * Minimal constructor.
	 * @param settings the {@link BibleSettings}
	 * @param displaySize the target display size
	 */
	public BibleSetupPanel(BibleSettings settings, Dimension displaySize) {
		this.settings = settings;
		// general bible settings
		JLabel lblDefaultBible = new JLabel(Messages.getString("panel.bible.setup.defaultBible"));
		Bible[] bibles = null;
		try {
			bibles = Bibles.getBibles().toArray(new Bible[0]);
		} catch (DataException e) {
			LOGGER.error("Bibles could not be retrieved:", e);
		}
		// check for null
		if (bibles == null) {
			this.cmbBiblesPrimary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesPrimary = new JComboBox<Bible>(bibles);
		}
		this.cmbBiblesPrimary.setRenderer(new BibleListCellRenderer());
		// get the default value
		Bible bible = null;
		try {
			bible = Bibles.getBible(settings.getDefaultPrimaryBibleId());
		} catch (DataException e) {
			LOGGER.error("Default bible could not be retrieved:", e);
		}
		if (bible != null) {
			// set the default value
			this.cmbBiblesPrimary.setSelectedItem(bible);
		}
		JLabel lblIncludeApocrypha = new JLabel(Messages.getString("panel.bible.setup.includeApocrypha"));
		lblIncludeApocrypha.setToolTipText(Messages.getString("panel.bible.setup.includeApocrypha.tooltip"));
		this.chkIncludeApocrypha = new JCheckBox();
		this.chkIncludeApocrypha.setSelected(settings.isApocryphaIncluded());
		
		// the secondary bible
		JLabel lblDefaultSecondaryBible = new JLabel(Messages.getString("panel.bible.setup.defaultSecondaryBible"));
		if (bibles == null) {
			this.cmbBiblesSecondary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesSecondary = new JComboBox<Bible>(bibles);
		}
		this.cmbBiblesSecondary.setRenderer(new BibleListCellRenderer());
		// get the default value
		bible = null;
		try {
			bible = Bibles.getBible(settings.getDefaultSecondaryBibleId());
		} catch (DataException e) {
			LOGGER.error("Default bible could not be retrieved:", e);
		}
		if (bible != null) {
			// set the default value
			this.cmbBiblesSecondary.setSelectedItem(bible);
		}
		
		JLabel lblUseSecondaryBible = new JLabel(Messages.getString("panel.bible.setup.useSecondaryBible"));
		lblUseSecondaryBible.setToolTipText(Messages.getString("panel.bible.setup.useSecondaryBible.tooltip"));
		this.chkUseSecondaryBible = new JCheckBox();
		this.chkUseSecondaryBible.setSelected(settings.isSecondaryBibleInUse());
		
		// create the bible display panel
		this.pnlDisplay = new BibleDisplaySetupPanel(settings, displaySize);
		
		// setup the layout
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createTitledBorder(Messages.getString("panel.bible.setup.general")));
		pnlGeneral.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		GroupLayout layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblDefaultBible)
						.addComponent(lblDefaultSecondaryBible)
						.addComponent(lblUseSecondaryBible)
						.addComponent(lblIncludeApocrypha))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkUseSecondaryBible)
						.addComponent(this.chkIncludeApocrypha)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblDefaultBible)
						.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblDefaultSecondaryBible)
						.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblUseSecondaryBible)
						.addComponent(this.chkUseSecondaryBible))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblIncludeApocrypha)
						.addComponent(this.chkIncludeApocrypha)));
		
		this.setLayout(new BorderLayout());
		this.add(pnlGeneral, BorderLayout.PAGE_START);
		this.add(this.pnlDisplay, BorderLayout.CENTER);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.SetupPanel#saveSettings()
	 */
	@Override
	public void saveSettings() throws SettingsException {
		// save this panel's settings
		Bible primary = (Bible)this.cmbBiblesPrimary.getSelectedItem();
		if (primary == null) {
			primary = (Bible)this.cmbBiblesPrimary.getItemAt(0);
		}
		Bible secondary = (Bible)this.cmbBiblesSecondary.getSelectedItem();
		if (secondary == null) {
			secondary = (Bible)this.cmbBiblesSecondary.getItemAt(0);
		}
		
		if (primary != null) this.settings.setDefaultPrimaryBibleId(primary.getId());
		if (secondary != null) this.settings.setDefaultSecondaryBibleId(secondary.getId());
		this.settings.setSecondaryBibleInUse(this.chkUseSecondaryBible.isSelected());
		this.settings.setApocryphaIncluded(this.chkIncludeApocrypha.isSelected());
		// save the display panel's settings
		this.pnlDisplay.saveSettings();
		// save the settings to persistent store
		this.settings.save();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// the display panel cares about these events
		this.pnlDisplay.propertyChange(event);
	}
}
