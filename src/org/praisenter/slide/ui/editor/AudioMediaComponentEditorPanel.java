package org.praisenter.slide.ui.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaFile;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnail;
import org.praisenter.media.MediaType;
import org.praisenter.media.NoMediaLoaderException;
import org.praisenter.media.ui.MediaLibraryDialog;
import org.praisenter.media.ui.MediaUI;
import org.praisenter.resources.Messages;
import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.utilities.WindowUtilities;

/**
 * Editor panel for {@link AudioMediaComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class AudioMediaComponentEditorPanel  extends SlideComponentEditorPanel<AudioMediaComponent> implements ListSelectionListener, ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 1742777929088652998L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(AudioMediaComponentEditorPanel.class);
	
	/** The audio label */
	protected JLabel lblAudio;
	
	/** The audio media list */
	protected JList<MediaThumbnail> lstAudio;
	
	/** The checkbox for muting the audio */
	protected JCheckBox chkAudioMuted;
	
	/** The checkbox for looping the media */
	protected JCheckBox chkLooped;
	
	/** The button to manage the media library */
	protected JButton btnManageMedia;
	
	/**
	 * Default constructor.
	 */
	public AudioMediaComponentEditorPanel() {
		this.lblAudio = new JLabel(Messages.getString("panel.slide.editor.audio"));
		
		// load up all the thumbnails for the media in the media library
		List<MediaThumbnail> audio = MediaLibrary.getThumbnails(MediaType.AUDIO);
		this.lstAudio = MediaUI.createJList(audio);
		this.lstAudio.addListSelectionListener(this);

		this.chkAudioMuted = new JCheckBox(Messages.getString("panel.slide.editor.media.muteAudio"));
		this.chkAudioMuted.setToolTipText(Messages.getString("panel.slide.editor.media.muteAudio.tooltip"));
		this.chkAudioMuted.addChangeListener(this);
		
		this.chkLooped = new JCheckBox(Messages.getString("panel.slide.editor.media.loop"));
		this.chkLooped.setToolTipText(Messages.getString("panel.slide.editor.media.loop.tooltip"));
		this.chkLooped.addChangeListener(this);
		
		this.btnManageMedia = new JButton(Messages.getString("panel.slide.editor.mediaLibrary"));
		this.btnManageMedia.addActionListener(this);
		this.btnManageMedia.setActionCommand("media-library");
		
		this.createLayout();
	}

	/**
	 * Creates the layout for a generic slide component.
	 */
	protected void createLayout() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JScrollPane pane = new JScrollPane(this.lstAudio);
		pane.setPreferredSize(new Dimension(200, 150));
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblName)
								.addComponent(this.lblAudio))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtName)
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.chkLooped)
										.addComponent(this.chkAudioMuted))))
				.addComponent(pane)
				.addComponent(this.btnManageMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAudio)
						.addComponent(this.chkLooped)
						.addComponent(this.chkAudioMuted))
				.addComponent(pane)
				.addComponent(this.btnManageMedia));
	}
	
	/**
	 * Sets the layout of the media panel on the given panel.
	 * @param panel the panel
	 */
	protected void createMediaLayout(JPanel panel) {
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		JScrollPane pane = new JScrollPane(this.lstAudio);
		pane.setPreferredSize(new Dimension(200, 150));
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pane)
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.chkLooped)
						.addComponent(this.chkAudioMuted))
				.addComponent(this.btnManageMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pane)
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkLooped)
						.addComponent(this.chkAudioMuted))
				.addComponent(this.btnManageMedia));
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Object source = e.getSource();
			if (source == this.lstAudio) {
				MediaThumbnail thumbnail = this.lstAudio.getSelectedValue();
				// make sure there is a selected value
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					
					if (this.slideComponent != null) {
						try {
							AbstractAudioMedia media = (AbstractAudioMedia)MediaLibrary.getMedia(file.getPath());
							this.slideComponent.setMedia(media);
							this.notifyEditorListeners();
						} catch (NoMediaLoaderException ex) {
							LOGGER.error(ex);
						} catch (MediaException ex) {
							LOGGER.error(ex);
						}
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.chkAudioMuted) {
			boolean flag = this.chkAudioMuted.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setAudioMuted(flag);
				this.notifyEditorListeners();
			}
		} else if (source == this.chkLooped) {
			boolean flag = this.chkLooped.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setLoopEnabled(flag);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("media-library".equals(command)) {
			boolean mediaLibraryUpdated = MediaLibraryDialog.show(WindowUtilities.getParentWindow(this));
			
			// only update the list if media was added or removed
			if (mediaLibraryUpdated) {
				// when control returns here we need to update the items in the jlist with the current media library items
				List<MediaThumbnail> thumbnails = MediaLibrary.getThumbnails(MediaType.AUDIO);
				// save the selected value
				MediaThumbnail thumb = this.lstAudio.getSelectedValue();
				this.lstAudio.clearSelection();
				DefaultListModel<MediaThumbnail> model = (DefaultListModel<MediaThumbnail>)this.lstAudio.getModel();
				model.removeAllElements();
				for (MediaThumbnail thumbnail : thumbnails) {
					model.addElement(thumbnail);
				}
				this.lstAudio.setSelectedValue(thumb, true);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#setSlideComponent(org.praisenter.slide.SlideComponent, boolean)
	 */
	@Override
	public void setSlideComponent(AudioMediaComponent slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		if (slideComponent != null) {
			if (slideComponent.getMedia() != null) {
				MediaThumbnail thumbnail = MediaLibrary.getThumbnail(slideComponent.getMedia());
				if (thumbnail != null) {
					this.lstAudio.setSelectedValue(thumbnail, true);
				}
			}
			this.chkAudioMuted.setSelected(slideComponent.isAudioMuted());
			this.chkLooped.setSelected(slideComponent.isLoopEnabled());
		} else {
			this.lstAudio.clearSelection();
			this.chkAudioMuted.setSelected(false);
			this.chkLooped.setSelected(false);
		}
	}
}
