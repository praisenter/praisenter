package org.praisenter.settings.ui;

import java.awt.Dimension;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.bible.ui.BibleListCellRenderer;
import org.praisenter.display.BibleDisplay;
import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transitions;
import org.praisenter.transitions.ui.TransitionListCellRenderer;
import org.praisenter.ui.SelectTextFocusListener;

/**
 * Panel used to set the {@link BibleSettings}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// FIXME fix the layouts of the bible and notification settings panels to be less big
public class BibleSettingsPanel extends JPanel implements SettingsPanel {
	/** The verison id */
	private static final long serialVersionUID = 460972285830298448L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(BibleSettingsPanel.class);
	
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
	protected BibleDisplaySettingsPanel pnlDisplay;

	// transitions
	
	/** The combo box of send transitions */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box for the send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of clear transitions */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box for the clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	/**
	 * Minimal constructor.
	 * @param settings the {@link BibleSettings}
	 * @param displaySize the target display size
	 */
	public BibleSettingsPanel(BibleSettings settings, Dimension displaySize) {
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
		this.chkUseSecondaryBible = new JCheckBox(Messages.getString("panel.bible.setup.useSecondaryBible"));
		this.chkUseSecondaryBible.setToolTipText(Messages.getString("panel.bible.setup.useSecondaryBible.tooltip"));
		this.chkUseSecondaryBible.setSelected(settings.isSecondaryBibleInUse());
		
		// transitions
		boolean transitionsSupported = GeneralSettings.getInstance().getPrimaryOrDefaultDisplay().isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
		
		JLabel lblSendTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultSend"));
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(settings.getDefaultSendTransition(), Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(settings.getDefaultSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		
		JLabel lblClearTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultClear"));
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(settings.getDefaultClearTransition(), Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(settings.getDefaultClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbSendTransitions.setEnabled(false);
			this.txtSendTransitions.setEnabled(false);
			this.cmbClearTransitions.setEnabled(false);
			this.txtClearTransitions.setEnabled(false);
		}
		
		JPanel pnlTransitions = new JPanel();
		pnlTransitions.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.general.setup.transition.title")));
		GroupLayout layout = new GroupLayout(pnlTransitions);
		pnlTransitions.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblSendTransition)
						.addComponent(lblClearTransition))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSendTransition)
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblClearTransition)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

		// create the bible display panel
		this.pnlDisplay = new BibleDisplaySettingsPanel(settings, displaySize);
		
		// setup the layout
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.bible.setup.general")));
		
		layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblDefaultBible)
						.addComponent(lblDefaultSecondaryBible)
						.addComponent(lblIncludeApocrypha))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkIncludeApocrypha))
				.addComponent(this.chkUseSecondaryBible));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblDefaultBible)
						.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkUseSecondaryBible))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblDefaultSecondaryBible)
						.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblIncludeApocrypha)
						.addComponent(this.chkIncludeApocrypha)));
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlTransitions, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlDisplay, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlDisplay));
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
		// transitions
		this.settings.setDefaultSendTransition(((Transition)this.cmbSendTransitions.getSelectedItem()).getTransitionId());
		this.settings.setDefaultSendTransitionDuration(((Number)this.txtSendTransitions.getValue()).intValue());
		this.settings.setDefaultClearTransition(((Transition)this.cmbClearTransitions.getSelectedItem()).getTransitionId());
		this.settings.setDefaultClearTransitionDuration(((Number)this.txtClearTransitions.getValue()).intValue());
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