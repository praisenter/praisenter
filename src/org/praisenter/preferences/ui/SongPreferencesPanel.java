package org.praisenter.preferences.ui;

import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.praisenter.easings.Easing;
import org.praisenter.easings.Easings;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.SongPreferences;
import org.praisenter.resources.Messages;
import org.praisenter.slide.ui.EasingListCellRenderer;
import org.praisenter.slide.ui.TransitionListCellRenderer;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transitions;
import org.praisenter.ui.SelectTextFocusListener;

/**
 * Panel used to set the {@link SongPreferences}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongPreferencesPanel extends JPanel implements PreferencesEditor {
	/** The verison id */
	private static final long serialVersionUID = -3575533232722870706L;
	
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
	public SongPreferencesPanel() {
		Preferences preferences = Preferences.getInstance();
		SongPreferences sp = preferences.getSongPreferences();
		
		// transitions
		
		JLabel lblSendTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultSend"));
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(sp.getSendTransitionId(), Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(sp.getSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		this.cmbSendEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbSendEasings.setRenderer(new EasingListCellRenderer());
		this.cmbSendEasings.setSelectedItem(Easings.getEasingForId(sp.getSendTransitionEasingId()));
		this.cmbSendEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
		JLabel lblClearTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultClear"));
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(sp.getClearTransitionId(), Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(sp.getClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		this.cmbClearEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbClearEasings.setRenderer(new EasingListCellRenderer());
		this.cmbClearEasings.setSelectedItem(Easings.getEasingForId(sp.getClearTransitionEasingId()));
		this.cmbClearEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
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

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("panel.general.setup.transition.title"), pnlTransitions);
		
		JLabel lblMessage = new JLabel(Messages.getString("panel.song.setup.message"));
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesEditor#applyPreferences()
	 */
	@Override
	public void applyPreferences() {
		Preferences preferences = Preferences.getInstance();
		SongPreferences sPreferences = preferences.getSongPreferences();
		
		// transitions
		sPreferences.setSendTransitionId(((Transition)this.cmbSendTransitions.getSelectedItem()).getTransitionId());
		sPreferences.setSendTransitionDuration(((Number)this.txtSendTransitions.getValue()).intValue());
		sPreferences.setSendTransitionEasingId(((Easing)this.cmbSendEasings.getSelectedItem()).getEasingId());
		sPreferences.setClearTransitionId(((Transition)this.cmbClearTransitions.getSelectedItem()).getTransitionId());
		sPreferences.setClearTransitionDuration(((Number)this.txtClearTransitions.getValue()).intValue());
		sPreferences.setClearTransitionEasingId(((Easing)this.cmbClearEasings.getSelectedItem()).getEasingId());
	}
}
