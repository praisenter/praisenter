/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.notification.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
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
import org.praisenter.slide.AbstractPositionedSlide;
import org.praisenter.slide.NotificationSlide;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.ui.SlideLibraryListener;
import org.praisenter.slide.ui.SlideThumbnailComboBoxRenderer;
import org.praisenter.slide.ui.TransitionListCellRenderer;
import org.praisenter.slide.ui.present.ClearEvent;
import org.praisenter.slide.ui.present.PresentationEvent;
import org.praisenter.slide.ui.present.PresentationListener;
import org.praisenter.slide.ui.present.SendEvent;
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
public class NotificationPanel extends JPanel implements ActionListener, ItemListener, PreferencesListener, SlideLibraryListener, PresentationListener {
	/** The version id */
	private static final long serialVersionUID = 20837022721408081L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(NotificationPanel.class);
	
	// slide
	
	/** The notification slide */
	private NotificationSlide slide;
	
	// controls
	
	/** The notification text */
	private JTextField txtText;

	/** The template combo box */
	private JComboBox<SlideThumbnail> cmbTemplates;
	
	/** The notification wait period */
	private JFormattedTextField txtWaitPeriod;
	
	/** The in transition */
	private JComboBox<Transition> cmbInTransition;
	
	/** The in transition duration */
	private JFormattedTextField txtInTransition;
	
	/** The out transition */
	private JComboBox<Transition> cmbOutTransition;
	
	/** The out transition duration */
	private JFormattedTextField txtOutTransition;
	
	/** The send button */
	private JButton btnSend;
	
	/** The manual clear button */
	private JButton btnClear;
	
	// state
	
	/** The wait duration timer */
	private Timer waitTimer;
	
	/** The wait timer lock */
	private Object waitTimerLock;
	
	/** The current present event */
	private PresentationEvent event;
	
	/** The bounds of the last sent slide */
	private Rectangle lastSlideBounds;
	
	/** The current state of the notification */
	private NotificationState state = NotificationState.CLEAR;
	
	/** The last send/wait/clear event */
	private SendWaitClearEvent queuedEvent = null;
	
	// preferences 
	
	/** A local reference to the preferences */
	private Preferences preferences = Preferences.getInstance();
	
	/** A local references to the notification preferences */
	private NotificationPreferences nPreferences = this.preferences.getNotificationPreferences();
	
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
		this.cmbTemplates.setToolTipText(Messages.getString("panel.template"));
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
		
		// update the list of templates
		SlideThumbnail selected = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
		if (selected == null) {
			selected = this.getSelectedThumbnail(thumbnails);
		}
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
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("send".equals(command)) {
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
		} else if ("clear".equals(command)) {
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
				// make sure we are listening to events on this window
				window.addPresentListener(this);
				// create a new event
				ClearEvent event = new ClearEvent(animator);
				this.event = event;
				// clear it
				window.execute(event);
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
	protected void sendWaitClear(AbstractPositionedSlide slide, TransitionAnimator inAnimator, final TransitionAnimator outAnimator, int waitPeriod) {
		final SlideWindow window = SlideWindows.getPrimaryNotificationWindow();
		if (window != null) {
			// make sure we are listening to events on this window
			window.addPresentListener(this);
			synchronized (this.waitTimerLock) {
				// check if we are currently waiting
				if (this.waitTimer != null && this.waitTimer.isRunning()) {
					// if so, then just stop it
					this.waitTimer.stop();
				}
				// make sure there is no queued event
				this.queuedEvent = null;
				
				// add the duration of the in transition to the total wait time
				boolean ts = Transitions.isTransitionSupportAvailable(window.getDevice());
				if (ts && inAnimator != null) {
					waitPeriod += inAnimator.getDuration();
				}
				
				SendEvent sendEvent = new SendEvent(slide, inAnimator);
				final ClearEvent clearEvent = new ClearEvent(outAnimator);
				
				// only do this if we have wait for transitions enabled
				if (this.preferences.isWaitForTransitionEnabled()) {
					// we need to check the position and size of the slide against the previous since they could
					// be different. If they are different the transitions will not work (what are we transitioning
					// at that point?). So instead, we need to end the current transition normally (just quickly)
					// and begin the new send
					boolean isSizePositionEqual = this.isSizePositionEqual(slide);
					
					if (!isSizePositionEqual) {
						LOGGER.trace("Size/Position not equal.");
						if ((this.state == NotificationState.IN || this.state == NotificationState.WAIT)) {
							// in either case, stop the current wait timer, set its initial delay to zero,
							// and execute it. In the case of the in transition, the timer will execute a 
							// clear event which will be queued. In the case of the wait event the clear event
							// will execute immediately. In both cases, when the clear event completes we
							// being the queued event.
							if (this.state == NotificationState.IN) {
								LOGGER.trace("In transition executing. Setting the wait timer initial delay: 0.");
							} else {
								LOGGER.trace("Wait period in progress. Executing clear event.");
							}
							this.waitTimer.stop();
							this.waitTimer.setInitialDelay(0);
							this.waitTimer.start();
							
							this.queuedEvent = new SendWaitClearEvent(slide, inAnimator, outAnimator, waitPeriod);
							return;
						}
						// if the current state is CLEAR or OUT just queue the next send normally
					} else {
						LOGGER.trace("Size/Position equal. Queueing send normally.");
					}
				}
				
				this.event = sendEvent;
				this.waitTimer = new Timer(waitPeriod, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// make sure the timer is ended
						waitTimer.stop();
						// when the timer executes this method the wait period
						// has been reached, so create a clear event and 
						// execute it
						event = clearEvent;
						// once the wait period is up, then execute the clear operation
						window.execute(clearEvent);
					}
				});
				this.waitTimer.setRepeats(false);
				// we have to wait to start the wait timer until the send event
				// actually begins to ensure the timing is somewhat accurate
				
				// execute the event
				window.execute(sendEvent);
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
	
	/**
	 * Returns true if the given slide's size and position is equal to the current slide's size and position.
	 * @param slide the new slide
	 * @return boolean
	 */
	protected boolean isSizePositionEqual(AbstractPositionedSlide slide) {
		int sx = slide.getX();
		int sy = slide.getY();
		int sw = slide.getWidth();
		int sh = slide.getHeight();
		
		if (this.lastSlideBounds != null) {
			int ox = this.lastSlideBounds.x;
			int oy = this.lastSlideBounds.y;
			int ow = this.lastSlideBounds.width;
			int oh = this.lastSlideBounds.height;
			
			LOGGER.trace("Slide[" + sx + "," + sy + "," + sw + "," + sh + "] - Last[" + ox + "," + oy + "," + ow + "," + oh + "]");
			return !(sx != ox || sy != oy || sw != ow || sh != oh);
		}
		
		// if the last bounds are not set, assume its a normal send
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.present.PresentListener#inTransitionBegin(org.praisenter.slide.ui.present.SendEvent)
	 */
	@Override
	public void inTransitionBegin(SendEvent event) {
		this.state = NotificationState.IN;
		
		// set the last slide bounds
		this.lastSlideBounds = new Rectangle(
				slide.getX(), slide.getY(),
				slide.getWidth(), slide.getHeight());
		
		// if the event that was started was the one we executed
		// then start the wait timer
		if (this.event == event) {
			if (this.waitTimer != null) {
				this.waitTimer.start();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.present.PresentListener#outTransitionBegin(org.praisenter.slide.ui.present.ClearEvent)
	 */
	@Override
	public void outTransitionBegin(ClearEvent event) {
		this.state = NotificationState.OUT;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.present.PresentListener#eventDropped(org.praisenter.slide.ui.present.PresentEvent)
	 */
	@Override
	public void eventDropped(PresentationEvent event) {}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.present.PresentListener#inTransitionComplete(org.praisenter.slide.ui.present.SendEvent)
	 */
	@Override
	public void inTransitionComplete(SendEvent event) {
		this.state = NotificationState.WAIT;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.present.PresentListener#outTransitionComplete(org.praisenter.slide.ui.present.ClearEvent)
	 */
	@Override
	public void outTransitionComplete(ClearEvent event) {
		this.state = NotificationState.CLEAR;
		
		// when an out transition ends we need to check if a queued event was stored
		if (this.queuedEvent != null) {
			// if so, we need to start this event
			final SlideWindow window = SlideWindows.getPrimaryNotificationWindow();
			if (window != null) {
				window.addPresentListener(this);
				
				final SendWaitClearEvent qevent = this.queuedEvent;
				this.queuedEvent = null;
				
				// set the send event
				this.event = qevent;
				// get the wait timer ready
				this.waitTimer = new Timer(qevent.getWaitPeriod(), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// make sure the timer is ended
						waitTimer.stop();
						// when the timer executes this method the wait period
						// has been reached, so create a clear event and 
						// execute it
						ClearEvent clearEvent = new ClearEvent(qevent.getOutAnimator());
						NotificationPanel.this.event = clearEvent;
						// once the wait period is up, then execute the clear operation
						window.execute(clearEvent);
					}
				});
				this.waitTimer.setRepeats(false);
				
				// execute the send event
				window.execute(qevent);
			} else {
				// in this case don't show a message just log the error
				LOGGER.warn("Display device no longer exists.");
			}
		}
	}
}
