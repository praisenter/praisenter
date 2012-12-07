package org.praisenter.notification.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.praisenter.easings.Easings;
import org.praisenter.preferences.NotificationPreferences;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.ui.PreferencesListener;
import org.praisenter.resources.Messages;
import org.praisenter.slide.NotificationSlide;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.ui.TransitionListCellRenderer;
import org.praisenter.slide.ui.present.SlideWindow;
import org.praisenter.slide.ui.present.SlideWindows;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.transitions.Transitions;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to send notifications.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationPanel extends JPanel implements ActionListener, PreferencesListener {
	/** The version id */
	private static final long serialVersionUID = 20837022721408081L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(NotificationPanel.class);
	
	// slide
	
	/** The notification slide */
	protected NotificationSlide slide;
	
	// controls
	
	/** The notification text */
	protected JTextField txtText;
	
	/** The notification wait period */
	protected JFormattedTextField txtWaitPeriod;
	
	/** The in transition */
	protected JComboBox<Transition> cmbInTransition;
	
	/** The in transition duration */
	protected JFormattedTextField txtInTransition;
	
	/** The out transition */
	protected JComboBox<Transition> cmbOutTransition;
	
	/** The out transition duration */
	protected JFormattedTextField txtOutTransition;
	
	/** The send button */
	protected JButton btnSend;
	
	/** The manual clear button */
	protected JButton btnClear;
	
	// state
	
	/** The wait duration timer */
	protected Timer waitTimer;
	
	/** The wait timer lock */
	protected Object waitTimerLock;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public NotificationPanel() {
		this.waitTimerLock = new Object();
		
		// get the preferences
		Preferences preferences = Preferences.getInstance();
		NotificationPreferences nPreferences = preferences.getNotificationPreferences();
		
		// get the primary device and size
		GraphicsDevice device = preferences.getPrimaryOrDefaultDevice();
		Dimension displaySize = preferences.getPrimaryOrDefaultDeviceResolution();
		
		// get the bible slide template
		NotificationSlideTemplate template = null;
		String templatePath = nPreferences.getTemplate();
		if (templatePath != null && templatePath.trim().length() > 0) {
			try {
				template = SlideLibrary.getTemplate(templatePath, NotificationSlideTemplate.class);
			} catch (SlideLibraryException e) {
				LOGGER.error("Unable to load default notification template [" + templatePath + "]: ", e);
			}
		}
		if (template == null) {
			// if its still null, then use the default template
			template = NotificationSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		}
		
		// check the template size against the display size
		if (template.getDeviceWidth() != displaySize.width || template.getDeviceHeight() != displaySize.height) {
			// log a message and modify the template to fit
			LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
			template.adjustSize(displaySize.width, displaySize.height);
		}
		
		// create the slide
		this.slide = template.createSlide();
		
		this.txtText = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.notification.text.watermark"));
			}
		};
		this.txtText.setColumns(20);
		this.txtText.addFocusListener(new SelectTextFocusListener(this.txtText));
		
		this.txtWaitPeriod = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtWaitPeriod.setToolTipText(Messages.getString("panel.notification.wait.tooltip"));
		this.txtWaitPeriod.setValue(nPreferences.getWaitPeriod());
		this.txtWaitPeriod.setColumns(5);
		this.txtWaitPeriod.addFocusListener(new SelectTextFocusListener(this.txtWaitPeriod));
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(device);
		
		this.cmbInTransition = new JComboBox<Transition>(Transitions.IN);
		this.cmbInTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbInTransition.setSelectedItem(Transitions.getTransitionForId(nPreferences.getSendTransitionId(), Transition.Type.IN));
		this.cmbInTransition.setToolTipText(Messages.getString("panel.notification.send.inTransition"));
		this.txtInTransition = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtInTransition.addFocusListener(new SelectTextFocusListener(this.txtInTransition));
		this.txtInTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtInTransition.setValue(nPreferences.getSendTransitionDuration());
		this.txtInTransition.setColumns(3);
		
		this.cmbOutTransition = new JComboBox<Transition>(Transitions.OUT);
		this.cmbOutTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbOutTransition.setSelectedItem(Transitions.getTransitionForId(nPreferences.getClearTransitionId(), Transition.Type.OUT));
		this.cmbOutTransition.setToolTipText(Messages.getString("panel.notification.send.outTransition"));
		this.txtOutTransition = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtOutTransition.addFocusListener(new SelectTextFocusListener(this.txtOutTransition));
		this.txtOutTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtOutTransition.setValue(nPreferences.getClearTransitionDuration());
		this.txtOutTransition.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbInTransition.setEnabled(false);
			this.txtInTransition.setEnabled(false);
			this.cmbOutTransition.setEnabled(false);
			this.txtOutTransition.setEnabled(false);
		}
		
		this.btnSend = new JButton(Messages.getString("panel.notification.send"));
		this.btnSend.setToolTipText(Messages.getString("panel.notification.send.tooltip"));
		this.btnSend.addActionListener(this);
		this.btnSend.setActionCommand("send");
		
		this.btnClear = new JButton(Messages.getString("panel.notification.clear"));
		this.btnClear.setToolTipText(Messages.getString("panel.notification.clear.tooltip"));
		this.btnClear.setActionCommand("clear");
		this.btnClear.addActionListener(this);
		
		// set the layout
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(this.txtText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnSend)
				.addComponent(this.btnClear));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.txtText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnClear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesListener#preferencesChanged()
	 */
	@Override
	public void preferencesChanged() {
		// when the preferences change we need to check if the display
		// size was changed and update the slides if necessary

		// get the preferences
		Preferences preferences = Preferences.getInstance();
		
		// get the primary device
		Dimension displaySize = preferences.getPrimaryOrDefaultDeviceResolution();
		
		if (this.slide.getDeviceWidth() != displaySize.width || this.slide.getDeviceHeight() != displaySize.height) {
			// adjust the slide size
			this.slide.adjustSize(displaySize.width, displaySize.height);
			LOGGER.info("Adjusting slides due to display size change.");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		NotificationPreferences preferences = Preferences.getInstance().getNotificationPreferences();
		if ("send".equals(event.getActionCommand())) {
			// get the text
			String text = this.txtText.getText();
			// check the text length
			if (text != null && !text.trim().isEmpty()) {
				// set the text on the slide
				this.slide.getTextComponent().setText(text);
				// create the transition animators
				TransitionAnimator in = new TransitionAnimator(
						(Transition)this.cmbInTransition.getSelectedItem(),
						((Number)this.txtInTransition.getValue()).intValue(),
						Easings.getEasingForId(preferences.getSendTransitionEasingId()));
				TransitionAnimator out = new TransitionAnimator(
						(Transition)this.cmbOutTransition.getSelectedItem(),
						((Number)this.txtOutTransition.getValue()).intValue(),
						Easings.getEasingForId(preferences.getClearTransitionEasingId()));
				// get the wait duration
				int wait = ((Number)this.txtWaitPeriod.getValue()).intValue();
				// send the notification
				this.sendWaitClear(this.slide, in, out, wait);
			}
		} else if ("clear".equals(event.getActionCommand())) {
			synchronized (this.waitTimerLock) {
				// check if we are currently waiting
				if (this.waitTimer != null && this.waitTimer.isRunning()) {
					// if so, then just stop it
					this.waitTimer.stop();
				}
			}
			// create the out transition animator
			TransitionAnimator animator = new TransitionAnimator(
					(Transition)this.cmbOutTransition.getSelectedItem(),
					((Number)this.txtOutTransition.getValue()).intValue(),
					Easings.getEasingForId(preferences.getClearTransitionEasingId()));
			// get the notification slide window
			SlideWindow window = SlideWindows.getPrimaryNotificationWindow();
			if (window != null) {
				// clear it
				window.clear(animator);
			}
		}
	}
	
	/**
	 * Sends the slide to this slide window using the given in animator, waits the given period, then clears the
	 * slide using the given out animator.
	 * @param slide the slide to show
	 * @param inAnimator the in transition animator
	 * @param outAnimator the out transition animator
	 * @param waitPeriod the wait period in milliseconds
	 */
	protected void sendWaitClear(Slide slide, TransitionAnimator inAnimator, final TransitionAnimator outAnimator, int waitPeriod) {
		final SlideWindow window = SlideWindows.getPrimaryNotificationWindow();
		if (window != null) {
			synchronized (this.waitTimerLock) {
				// check if we are currently waiting
				if (this.waitTimer != null && this.waitTimer.isRunning()) {
					// if so, then just stop it
					this.waitTimer.stop();
				}
				// add the duration of the in transition to the total wait time
				boolean ts = Transitions.isTransitionSupportAvailable(window.getDevice());
				if (ts && inAnimator != null) {
					waitPeriod += inAnimator.getDuration();
				}
				window.send(slide, inAnimator);
				this.waitTimer = new Timer(waitPeriod, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						// once the wait period is up, then execute the clear operation
						window.clear(outAnimator);
					}
				});
				this.waitTimer.setRepeats(false);
				this.waitTimer.start();
			}
		} else {
			// the device is no longer available
			JOptionPane.showMessageDialog(
					WindowUtilities.getParentWindow(this), 
					Messages.getString("dialog.device.primary.missing.text"), 
					Messages.getString("dialog.device.primary.missing.title"), 
					JOptionPane.WARNING_MESSAGE);
		}
	}
	
}
