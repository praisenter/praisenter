package org.praisenter.preferences.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;
import org.praisenter.easings.Easing;
import org.praisenter.easings.Easings;
import org.praisenter.preferences.NotificationPreferences;
import org.praisenter.preferences.Preferences;
import org.praisenter.resources.Messages;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.ui.EasingListCellRenderer;
import org.praisenter.slide.ui.SlideThumbnailComboBoxRenderer;
import org.praisenter.slide.ui.TransitionListCellRenderer;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transitions;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.utilities.ComponentUtilities;

/**
 * Panel used to set the {@link NotificationPreferences}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationPreferencesPanel extends JPanel implements PreferencesEditor, ActionListener {
	/** The verison id */
	private static final long serialVersionUID = -2274087793090854194L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(NotificationPreferencesPanel.class);
	
	/** The default wait period text box */
	protected JFormattedTextField txtDefaultWaitPeriod;

	// template
	
	/** The template combo box */
	private JComboBox<SlideThumbnail> cmbTemplates;
	
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
	public NotificationPreferencesPanel() {
		Preferences preferences = Preferences.getInstance();
		NotificationPreferences nPreferences = preferences.getNotificationPreferences();
		
		// general notification settings
		JLabel lblDefaultWaitPeriod = new JLabel(Messages.getString("panel.notification.preferences.defaultWaitPeriod"));
		this.txtDefaultWaitPeriod = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtDefaultWaitPeriod.setToolTipText(Messages.getString("panel.notification.preferences.defaultWaitPeriod.tooltip"));
		this.txtDefaultWaitPeriod.setValue(nPreferences.getWaitPeriod());
		this.txtDefaultWaitPeriod.setColumns(6);

		// template
		
		JLabel lblTemplate = new JLabel(Messages.getString("panel.preferences.template"));
		List<SlideThumbnail> thumbs = SlideLibrary.getThumbnails(NotificationSlideTemplate.class);
		// add in the default template
		Dimension displaySize = preferences.getPrimaryOrDefaultDeviceResolution();
		NotificationSlideTemplate template = NotificationSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		BufferedImage image = template.getThumbnail(SlideLibrary.THUMBNAIL_SIZE);
		SlideThumbnail temp = new SlideThumbnail(SlideFile.NOT_STORED, template.getName(), image);
		thumbs.add(temp);
		// find the selected template
		SlideThumbnail selected = null;
		for (SlideThumbnail thumb : thumbs) {
			if (thumb.getFile() == SlideFile.NOT_STORED) {
				if (nPreferences.getTemplate() == null) {
					selected = thumb;
					break;
				}
			} else if (thumb.getFile().getPath().equals(nPreferences.getTemplate())) {
				selected = thumb;
				break;
			}
		}
		Collections.sort(thumbs);
		this.cmbTemplates = new JComboBox<SlideThumbnail>(thumbs.toArray(new SlideThumbnail[0]));
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		}
		this.cmbTemplates.setToolTipText(Messages.getString("panel.preferences.template.tooltip"));
		this.cmbTemplates.setRenderer(new SlideThumbnailComboBoxRenderer());
		JButton btnAddTemplate = new JButton(Messages.getString("panel.preferences.template.add"));
		btnAddTemplate.setActionCommand("addTemplate");
		btnAddTemplate.setToolTipText(Messages.getString("panel.preferences.template.add.tooltip"));
		btnAddTemplate.addActionListener(this);
		
		// transitions
		
		JLabel lblSendTransition = new JLabel(Messages.getString("panel.preferences.transition.defaultSend"));
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(nPreferences.getSendTransitionId(), Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(nPreferences.getSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		this.cmbSendEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbSendEasings.setRenderer(new EasingListCellRenderer());
		this.cmbSendEasings.setSelectedItem(Easings.getEasingForId(nPreferences.getSendTransitionEasingId()));
		this.cmbSendEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
		JLabel lblClearTransition = new JLabel(Messages.getString("panel.preferences.transition.defaultClear"));
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(nPreferences.getClearTransitionId(), Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(nPreferences.getClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		this.cmbClearEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbClearEasings.setRenderer(new EasingListCellRenderer());
		this.cmbClearEasings.setSelectedItem(Easings.getEasingForId(nPreferences.getClearTransitionEasingId()));
		this.cmbClearEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
		JPanel pnlTransitions = new JPanel();
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
		
		// setup the template panel
		JPanel pnlTemplate = new JPanel();
		layout = new GroupLayout(pnlTemplate);
		pnlTemplate.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(lblTemplate)
				.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(btnAddTemplate));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblTemplate)
				.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(btnAddTemplate));
		
		// setup the layout
		JPanel pnlGeneral = new JPanel();
		layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(lblDefaultWaitPeriod)
				.addComponent(this.txtDefaultWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblDefaultWaitPeriod)
				.addComponent(this.txtDefaultWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		ComponentUtilities.setMinimumSize(lblDefaultWaitPeriod, lblTemplate, lblClearTransition, lblSendTransition);
		
		JSeparator sep1 = new JSeparator(JSeparator.HORIZONTAL);
		JSeparator sep2 = new JSeparator(JSeparator.HORIZONTAL);
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep1)
				.addComponent(pnlTemplate)
				.addComponent(sep2)
				.addComponent(pnlTransitions));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlTemplate)
				.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlTransitions));
	}

	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesEditor#applyPreferences()
	 */
	@Override
	public void applyPreferences() {
		Preferences preferences = Preferences.getInstance();
		NotificationPreferences nPreferences = preferences.getNotificationPreferences();
		
		// save this panel's settings
		nPreferences.setWaitPeriod(((Number)this.txtDefaultWaitPeriod.getValue()).intValue());
		
		// template
		SlideThumbnail thumbnail = ((SlideThumbnail)this.cmbTemplates.getSelectedItem());
		// check for the default template
		if (thumbnail.getFile() != SlideFile.NOT_STORED) {
			nPreferences.setTemplate(thumbnail.getFile().getPath());
		} else {
			nPreferences.setTemplate(null);
		}
		
		// transitions
		nPreferences.setSendTransitionId(((Transition)this.cmbSendTransitions.getSelectedItem()).getTransitionId());
		nPreferences.setSendTransitionDuration(((Number)this.txtSendTransitions.getValue()).intValue());
		nPreferences.setSendTransitionEasingId(((Easing)this.cmbSendEasings.getSelectedItem()).getEasingId());
		nPreferences.setClearTransitionId(((Transition)this.cmbClearTransitions.getSelectedItem()).getTransitionId());
		nPreferences.setClearTransitionDuration(((Number)this.txtClearTransitions.getValue()).intValue());
		nPreferences.setClearTransitionEasingId(((Easing)this.cmbClearEasings.getSelectedItem()).getEasingId());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("addTemplate".equals(command)) {
			// FIXME add code to create template
			LOGGER.error("Not implemented yet!!!");
		}
	}
}
