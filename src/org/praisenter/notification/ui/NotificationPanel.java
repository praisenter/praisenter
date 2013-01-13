package org.praisenter.notification.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

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
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.ui.SlideLibraryListener;
import org.praisenter.slide.ui.SlideThumbnailComboBoxRenderer;
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
 * @version 2.0.0
 * @since 1.0.0
 */
public class NotificationPanel extends JPanel implements ActionListener, ItemListener, PreferencesListener, SlideLibraryListener {
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

	/** The template combo box */
	private JComboBox<SlideThumbnail> cmbTemplates;
	
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
	
	// preferences 
	
	/** A local reference to the preferences */
	protected Preferences preferences = Preferences.getInstance();
	
	/** A local references to the notification preferences */
	protected NotificationPreferences nPreferences = this.preferences.getNotificationPreferences();
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public NotificationPanel() {
		this.waitTimerLock = new Object();
		
		// get the primary device and size
		GraphicsDevice device = this.preferences.getPrimaryOrDefaultDevice();
		
		// get the bible slide template
		NotificationSlideTemplate template = this.getTemplate();
		
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
		
		SlideThumbnail[] thumbnails = this.getThumbnails();
		SlideThumbnail selected = this.getSelectedThumbnail(thumbnails);
		this.cmbTemplates = new JComboBox<SlideThumbnail>(thumbnails);
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		}
		this.cmbTemplates.setToolTipText(Messages.getString("panel.preferences.template.tooltip"));
		this.cmbTemplates.setRenderer(new SlideThumbnailComboBoxRenderer());
		this.cmbTemplates.addItemListener(this);
		
		this.txtWaitPeriod = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtWaitPeriod.setToolTipText(Messages.getString("panel.notification.wait.tooltip"));
		this.txtWaitPeriod.setValue(this.nPreferences.getWaitPeriod());
		this.txtWaitPeriod.setColumns(5);
		this.txtWaitPeriod.addFocusListener(new SelectTextFocusListener(this.txtWaitPeriod));
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(device);
		
		this.cmbInTransition = new JComboBox<Transition>(Transitions.IN);
		this.cmbInTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbInTransition.setSelectedItem(Transitions.getTransitionForId(this.nPreferences.getSendTransitionId(), Transition.Type.IN));
		this.cmbInTransition.setToolTipText(Messages.getString("panel.notification.send.inTransition"));
		this.txtInTransition = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtInTransition.addFocusListener(new SelectTextFocusListener(this.txtInTransition));
		this.txtInTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtInTransition.setValue(this.nPreferences.getSendTransitionDuration());
		this.txtInTransition.setColumns(3);
		
		this.cmbOutTransition = new JComboBox<Transition>(Transitions.OUT);
		this.cmbOutTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbOutTransition.setSelectedItem(Transitions.getTransitionForId(this.nPreferences.getClearTransitionId(), Transition.Type.OUT));
		this.cmbOutTransition.setToolTipText(Messages.getString("panel.notification.send.outTransition"));
		this.txtOutTransition = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtOutTransition.addFocusListener(new SelectTextFocusListener(this.txtOutTransition));
		this.txtOutTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtOutTransition.setValue(this.nPreferences.getClearTransitionDuration());
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
				.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnSend)
				.addComponent(this.btnClear));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.txtText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnClear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}
	
	/**
	 * Returns the template to use from the preferences.
	 * @return {@link NotificationSlideTemplate}
	 */
	private NotificationSlideTemplate getTemplate() {
		// get the primary device size
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		// get the bible slide template
		NotificationSlideTemplate template = null;
		String templatePath = this.nPreferences.getTemplate();
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
		this.verifyTemplateDimensions(template, displaySize);
		
		return template;
	}
	
	/**
	 * Verifies the template is sized to the given size.
	 * <p>
	 * If not, the template is adjusted to fit.
	 * @param template the template
	 * @param size the size
	 */
	private void verifyTemplateDimensions(NotificationSlideTemplate template, Dimension size) {
		// check the template size against the display size
		if (template.getDeviceWidth() != size.width || template.getDeviceHeight() != size.height) {
			// log a message and modify the template to fit
			LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
			template.adjustSize(size.width, size.height);
		}
	}
	
	/**
	 * Returns an array of {@link SlideThumbnail}s for {@link NotificationSlideTemplate}s.
	 * @return {@link SlideThumbnail}[]
	 */
	private SlideThumbnail[] getThumbnails() {
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		List<SlideThumbnail> thumbs = SlideLibrary.getThumbnails(NotificationSlideTemplate.class);
		// add in the default template
		NotificationSlideTemplate dTemplate = NotificationSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		BufferedImage image = dTemplate.getThumbnail(SlideLibrary.THUMBNAIL_SIZE);
		SlideThumbnail temp = new SlideThumbnail(SlideFile.NOT_STORED, dTemplate.getName(), image);
		thumbs.add(temp);
		// sort them
		Collections.sort(thumbs);
		
		return thumbs.toArray(new SlideThumbnail[0]);
	}
	
	/**
	 * Returns the selected thumbnail for the selected {@link NotificationSlideTemplate}
	 * given in the preferences.
	 * @param thumbnails the list of all slide thumbnails
	 * @return {@link SlideThumbnail}
	 */
	private SlideThumbnail getSelectedThumbnail(SlideThumbnail[] thumbnails) {
		for (SlideThumbnail thumb : thumbnails) {
			if (thumb.getFile() == SlideFile.NOT_STORED) {
				if (this.nPreferences.getTemplate() == null) {
					return thumb;
				}
			} else if (thumb.getFile().getPath().equals(this.nPreferences.getTemplate())) {
				return thumb;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbTemplates) {
				SlideThumbnail thumbnail = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
				if (thumbnail != null) {
					NotificationSlideTemplate template = null;
					Dimension size = this.preferences.getPrimaryOrDefaultDeviceResolution();
					if (thumbnail.getFile() == SlideFile.NOT_STORED) {
						template = NotificationSlideTemplate.getDefaultTemplate(size.width, size.height);
					} else {
						try {
							template = SlideLibrary.getTemplate(thumbnail.getFile().getPath(), NotificationSlideTemplate.class);
						} catch (SlideLibraryException ex) {
							// just log the error
							LOGGER.error("Failed to switch to template: [" + thumbnail.getFile().getPath() + "]", ex);
							return;
						}
					}
					this.verifyTemplateDimensions(template, size);
					this.slide = template.createSlide();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesListener#preferencesChanged()
	 */
	@Override
	public void preferencesChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.SlideLibraryListener#slideLibraryChanged()
	 */
	@Override
	public void slideLibraryChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	/**
	 * Called when the preferences or the slide library changes.
	 * <p>
	 * The preferences can alter the selected template and the slide library
	 * can be changed from the preferences dialog. Because of this, we need
	 * to perform the same action for both events.
	 */
	private void onPreferencesOrSlideLibraryChanged() {
		SlideThumbnail[] thumbnails = this.getThumbnails();
		SlideThumbnail selected = this.getSelectedThumbnail(thumbnails);
		
		// update the list of templates
		this.cmbTemplates.removeAllItems();
		for (SlideThumbnail thumb : thumbnails) {
			this.cmbTemplates.addItem(thumb);
		}
		
		// set the selected one
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		} else {
			this.cmbTemplates.setSelectedIndex(0);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
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
						Easings.getEasingForId(this.nPreferences.getSendTransitionEasingId()));
				TransitionAnimator out = new TransitionAnimator(
						(Transition)this.cmbOutTransition.getSelectedItem(),
						((Number)this.txtOutTransition.getValue()).intValue(),
						Easings.getEasingForId(this.nPreferences.getClearTransitionEasingId()));
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
					Easings.getEasingForId(this.nPreferences.getClearTransitionEasingId()));
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
