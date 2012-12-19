package org.praisenter.slide.ui.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.praisenter.media.ImageMedia;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaFile;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnail;
import org.praisenter.media.MediaType;
import org.praisenter.media.NoMediaLoaderException;
import org.praisenter.media.ui.MediaLibraryDialog;
import org.praisenter.media.ui.MediaUI;
import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.utilities.WindowUtilities;

/**
 * Editor panel for {@link ImageMediaComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ImageMediaComponentEditorPanel  extends GenericComponentEditorPanel<ImageMediaComponent> implements ListSelectionListener, ItemListener, ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1702500646124912915L;

	/** The scale type label */
	protected JLabel lblScaleType;

	/** The image visibility checkbox */
	protected JCheckBox chkImageVisible;
	
	/** The image media list */
	protected JList<MediaThumbnail> lstImages;
	
	/** The scale type combo box */
	protected JComboBox<ScaleType> cmbScaleType;
	
	/** The button to manage the media library */
	protected JButton btnManageMedia;
	
	/**
	 * Default constructor.
	 */
	public ImageMediaComponentEditorPanel() {
		super(false);
		
		this.chkImageVisible = new JCheckBox(Messages.getString("panel.slide.editor.visible"));
		this.chkImageVisible.addChangeListener(this);
		
		// load up all the thumbnails for the media in the media library
		List<MediaThumbnail> images = MediaLibrary.getThumbnails(MediaType.IMAGE);
		this.lstImages = MediaUI.createJList(images);
		this.lstImages.addListSelectionListener(this);
		
		this.lblScaleType = new JLabel(Messages.getString("panel.slide.editor.image.scaleType"));
		this.cmbScaleType = new JComboBox<ScaleType>(ScaleType.values());
		this.cmbScaleType.setRenderer(new ScaleTypeListCellRenderer());
		this.cmbScaleType.addItemListener(this);
		
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
		
		JTabbedPane tabs = new JTabbedPane();

		JPanel pnlGeneral = new JPanel();
		this.createGeneralLayout(pnlGeneral);
		
		JPanel pnlMedia = new JPanel();
		this.createMediaLayout(pnlMedia);
		tabs.addTab(Messages.getString("panel.slide.editor.image"), pnlMedia);
		
		JPanel pnlBackground = new JPanel();
		this.createBackgroundLayout(pnlBackground);
		tabs.addTab(Messages.getString("panel.slide.editor.component.background"), pnlBackground);
		
		JPanel pnlBorder = new JPanel();
		this.createBorderLayout(pnlBorder);
		tabs.addTab(Messages.getString("panel.slide.editor.component.border"), pnlBorder);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(tabs));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(tabs));
	}
	
	/**
	 * Sets the layout of the media panel on the given panel.
	 * @param panel the panel
	 */
	protected void createMediaLayout(JPanel panel) {
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		JScrollPane pane = new JScrollPane(this.lstImages);
		pane.setPreferredSize(new Dimension(200, 150));
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.chkImageVisible)
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.lblScaleType)
						.addComponent(this.cmbScaleType))
				.addComponent(pane)
				.addComponent(this.btnManageMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.chkImageVisible)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblScaleType)
						.addComponent(this.cmbScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(pane)
				.addComponent(this.btnManageMedia));
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Object source = e.getSource();
			if (source == this.lstImages) {
				MediaThumbnail thumbnail = this.lstImages.getSelectedValue();
				// make sure there is a selected value
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					
					if (this.slideComponent != null) {
						try {
							// FIXME probably will need a progress bar here
							ImageMedia media = (ImageMedia)MediaLibrary.getMedia(file.getPath());
							this.slideComponent.setMedia(media);
							this.notifyEditorListeners();
						} catch (NoMediaLoaderException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (MediaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbScaleType) {
				if (this.slideComponent != null) {
					this.slideComponent.setScaleType((ScaleType)e.getItem());
					this.notifyEditorListeners();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.GenericComponentEditorPanel#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		super.stateChanged(e);
		
		Object source = e.getSource();
		if (source == this.chkImageVisible) {
			boolean flag = this.chkImageVisible.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setImageVisible(flag);
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
			MediaLibraryDialog.show(WindowUtilities.getParentWindow(this));
			
			// when control returns here we need to update the items in the jlist with the current media library items
			List<MediaThumbnail> thumbnails = MediaLibrary.getThumbnails(MediaType.IMAGE);
			// save the selected value
			MediaThumbnail thumb = this.lstImages.getSelectedValue();
			this.lstImages.clearSelection();
			DefaultListModel<MediaThumbnail> model = (DefaultListModel<MediaThumbnail>)this.lstImages.getModel();
			model.removeAllElements();
			for (MediaThumbnail thumbnail : thumbnails) {
				model.addElement(thumbnail);
			}
			this.lstImages.setSelectedValue(thumb, true);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.GenericComponentEditorPanel#setSlideComponent(org.praisenter.slide.GenericComponent)
	 */
	@Override
	public void setSlideComponent(ImageMediaComponent slideComponent) {
		super.setSlideComponent(slideComponent);
		
		if (slideComponent != null) {
			if (slideComponent.getMedia() != null) {
				MediaThumbnail thumbnail = MediaLibrary.getThumbnail(slideComponent.getMedia());
				if (thumbnail != null) {
					this.lstImages.setSelectedValue(thumbnail, true);
				}
			}
			this.cmbScaleType.setSelectedItem(slideComponent.getScaleType());
			this.chkImageVisible.setSelected(slideComponent.isImageVisible());
		}
	}
}
