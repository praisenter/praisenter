package org.praisenter.preferences.ui;

import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.bible.ui.BibleListCellRenderer;
import org.praisenter.easings.Easing;
import org.praisenter.easings.Easings;
import org.praisenter.preferences.BiblePreferences;
import org.praisenter.preferences.Preferences;
import org.praisenter.resources.Messages;
import org.praisenter.slide.ui.EasingListCellRenderer;
import org.praisenter.slide.ui.TransitionListCellRenderer;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transitions;
import org.praisenter.ui.SelectTextFocusListener;

/**
 * Panel used to set the {@link BiblePreferences}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BiblePreferencesPanel extends JPanel /* implements SettingsPanel */ {
	/** The verison id */
	private static final long serialVersionUID = 460972285830298448L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(BiblePreferencesPanel.class);
	
	/** The available bibles */
	private JComboBox<Bible> cmbBiblesPrimary;
	
	/** The secondary bible listing */
	private JComboBox<Bible> cmbBiblesSecondary;
	
	/** The checkbox to use the secondary bible */
	private JCheckBox chkUseSecondaryBible;
	
	/** The checkbox to include/exclude the apocrypha */
	private JCheckBox chkIncludeApocrypha;
	
	// transitions
	
	/** The combo box of send transitions */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box for the send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of send easings */
	private JComboBox<Easing> cmbSendEasings;
	
	/** The combo box of clear transitions */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box for the clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	/** The combo box of clear easings */
	private JComboBox<Easing> cmbClearEasings;
	
	/**
	 * Default constructor.
	 */
	public BiblePreferencesPanel() {
		Preferences preferences = Preferences.getInstance();
		BiblePreferences bPreferences = preferences.getBiblePreferences();
		
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
			bible = Bibles.getBible(bPreferences.getPrimaryTranslationId());
		} catch (DataException e) {
			LOGGER.error("Default bible could not be retrieved:", e);
		}
		if (bible != null) {
			// set the default value
			this.cmbBiblesPrimary.setSelectedItem(bible);
		}
		JLabel lblIncludeApocrypha = new JLabel(Messages.getString("panel.bible.setup.includeApocrypha"));
		this.chkIncludeApocrypha = new JCheckBox();
		this.chkIncludeApocrypha.setToolTipText(Messages.getString("panel.bible.setup.includeApocrypha.tooltip"));
		this.chkIncludeApocrypha.setSelected(bPreferences.isApochryphaIncluded());
		
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
			bible = Bibles.getBible(bPreferences.getSecondaryTranslationId());
		} catch (DataException e) {
			LOGGER.error("Default bible could not be retrieved:", e);
		}
		if (bible != null) {
			// set the default value
			this.cmbBiblesSecondary.setSelectedItem(bible);
		}
		
		this.chkUseSecondaryBible = new JCheckBox(Messages.getString("panel.bible.setup.useSecondaryBible"));
		this.chkUseSecondaryBible.setToolTipText(Messages.getString("panel.bible.setup.useSecondaryBible.tooltip"));
		this.chkUseSecondaryBible.setSelected(bPreferences.isSecondaryTranslationEnabled());
		
		// transitions
		
		JLabel lblSendTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultSend"));
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(bPreferences.getSendTransitionId(), Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(bPreferences.getSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		this.cmbSendEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbSendEasings.setRenderer(new EasingListCellRenderer());
		this.cmbSendEasings.setSelectedItem(Easings.getEasingForId(bPreferences.getSendTransitionEasingId()));
		this.cmbSendEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
		JLabel lblClearTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultClear"));
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(bPreferences.getClearTransitionId(), Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(bPreferences.getClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		this.cmbClearEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbClearEasings.setRenderer(new EasingListCellRenderer());
		this.cmbClearEasings.setSelectedItem(Easings.getEasingForId(bPreferences.getClearTransitionEasingId()));
		this.cmbClearEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
//		if (!transitionsSupported) {
//			this.cmbSendTransitions.setEnabled(false);
//			this.txtSendTransitions.setEnabled(false);
//			this.cmbSendEasings.setEnabled(false);
//			this.cmbClearTransitions.setEnabled(false);
//			this.txtClearTransitions.setEnabled(false);
//			this.cmbClearEasings.setEnabled(false);
//		}
		
		JPanel pnlTransitions = new JPanel();
		pnlTransitions.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
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
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbSendEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSendTransition)
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbSendEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblClearTransition)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the layout
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		
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
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("panel.bible.setup.general"), pnlGeneral);
		tabs.addTab(Messages.getString("panel.general.setup.transition.title"), pnlTransitions);
		
		JLabel lblMessage = new JLabel(Messages.getString("panel.bible.setup.message"));
		lblMessage.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblMessage)
				.addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblMessage)
				.addComponent(tabs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}

//	/* (non-Javadoc)
//	 * @see org.praisenter.panel.setup.SetupPanel#saveSettings()
//	 */
//	@Override
//	public void saveSettings() throws SettingsException {
//		// save this panel's settings
//		Bible primary = (Bible)this.cmbBiblesPrimary.getSelectedItem();
//		if (primary == null) {
//			primary = (Bible)this.cmbBiblesPrimary.getItemAt(0);
//		}
//		Bible secondary = (Bible)this.cmbBiblesSecondary.getSelectedItem();
//		if (secondary == null) {
//			secondary = (Bible)this.cmbBiblesSecondary.getItemAt(0);
//		}
//		
//		if (primary != null) this.settings.setDefaultPrimaryBibleId(primary.getId());
//		if (secondary != null) this.settings.setDefaultSecondaryBibleId(secondary.getId());
//		this.settings.setSecondaryBibleInUse(this.chkUseSecondaryBible.isSelected());
//		this.settings.setApocryphaIncluded(this.chkIncludeApocrypha.isSelected());
//		// transitions
//		this.settings.setDefaultSendTransition(((Transition)this.cmbSendTransitions.getSelectedItem()).getTransitionId());
//		this.settings.setDefaultSendTransitionDuration(((Number)this.txtSendTransitions.getValue()).intValue());
//		this.settings.setSendEasing(((Easing)this.cmbSendEasings.getSelectedItem()).getEasingId());
//		this.settings.setDefaultClearTransition(((Transition)this.cmbClearTransitions.getSelectedItem()).getTransitionId());
//		this.settings.setDefaultClearTransitionDuration(((Number)this.txtClearTransitions.getValue()).intValue());
//		this.settings.setClearEasing(((Easing)this.cmbClearEasings.getSelectedItem()).getEasingId());
//		// save the display panel's settings
//		this.pnlDisplay.saveSettings();
//		// save the settings to persistent store
//		this.settings.save();
//	}
	
//	/* (non-Javadoc)
//	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
//	 */
//	@Override
//	public void propertyChange(PropertyChangeEvent event) {
//		// the display panel cares about these events
//		this.pnlDisplay.propertyChange(event);
//	}
}
