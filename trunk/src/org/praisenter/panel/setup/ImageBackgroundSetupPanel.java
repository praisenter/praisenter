package org.praisenter.panel.setup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.praisenter.control.ImageFileFilter;
import org.praisenter.control.ImageFilePreview;
import org.praisenter.dialog.ExceptionDialog;
import org.praisenter.display.ImageBackgroundComponent;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;
import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;

/**
 * Panel used to setup an image background.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageBackgroundSetupPanel extends JPanel implements ActionListener, ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -8394312426259802361L;

	/** The class logger */
	private static final Logger LOGGER = Logger.getLogger(ImageBackgroundSetupPanel.class);
	
	/** The {@link ImageBackgroundComponent} to setup */
	protected ImageBackgroundComponent component;
	
	/** The button to select the image from the file system */
	protected JButton btnSelect;
	
	/** The combo box to select the image scale type */
	protected JComboBox<ScaleType> cmbScaleType;
	
	/** The combo box to select the image scale quality */
	protected JComboBox<ScaleQuality> cmbScaleQuality;
	
	/** The checkbox for visibility */
	protected JCheckBox chkVisible;
	
	/**
	 * Minimal constructor.
	 * @param component the component to setup
	 */
	public ImageBackgroundSetupPanel(ImageBackgroundComponent component) {
		this.component = component;
		
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.image.setup.title")));
		
		// image
		this.btnSelect = new JButton(Messages.getString("panel.image.setup.browse"));
		this.btnSelect.setToolTipText(Messages.getString("panel.image.setup.browse.tooltip"));
		this.btnSelect.addActionListener(this);
		this.btnSelect.setActionCommand("select");
		
		// type
		this.cmbScaleType = new JComboBox<ScaleType>(ScaleType.values());
		this.cmbScaleType.setToolTipText(Messages.getString("panel.image.setup.scaleType"));
		this.cmbScaleType.setRenderer(new ScaleTypeRenderer());
		this.cmbScaleType.setSelectedItem(this.component.getScaleType());
		this.cmbScaleType.addItemListener(this);
		
		// quality
		this.cmbScaleQuality = new JComboBox<ScaleQuality>(ScaleQuality.values());
		this.cmbScaleQuality.setToolTipText(Messages.getString("panel.image.setup.scaleQuality"));
		this.cmbScaleQuality.setRenderer(new ScaleQualityRenderer());
		this.cmbScaleQuality.setSelectedItem(this.component.getScaleQuality());
		this.cmbScaleQuality.addItemListener(this);

		// visible
		this.chkVisible = new JCheckBox(Messages.getString("panel.image.setup.visible"), this.component.isVisible());
		this.chkVisible.addChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.btnSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbScaleQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkVisible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.btnSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbScaleQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkVisible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("select".equals(e.getActionCommand())) {
			// show the file explorer
			JFileChooser fc = new JFileChooser();
			// the user can only select files
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			// they can only select one file
			fc.setMultiSelectionEnabled(false);
			// they can only select image files
			fc.setFileFilter(new ImageFileFilter());
			// provide a preview for image files
			fc.setAccessory(new ImageFilePreview(fc));
			// they cannot switch to all files
			fc.setAcceptAllFileFilterUsed(false);
			// show the dialog
			int result = fc.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				// get the file
				File file = fc.getSelectedFile();
				// attempt to load the image
				try {
					BufferedImage old = this.component.getImage();
					BufferedImage image = ImageIO.read(file);
					this.component.setImage(image);
					this.firePropertyChange(DisplaySetupPanel.DISPLAY_COMPONENT_PROPERTY, old, image);
				} catch (IOException ex) {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.image.setup.exception.read.title"), 
							MessageFormat.format(Messages.getString("panel.image.setup.exception.read.text"), file.getAbsolutePath()), 
							ex);
					LOGGER.error("An error occurred while reading the selected file [" + file.getAbsolutePath() + "]: ", ex);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == this.cmbScaleType) {
			// the scale type was changed
			ScaleType old = this.component.getScaleType();
			ScaleType type = (ScaleType)e.getItem();
			this.component.setScaleType(type);
			this.firePropertyChange(DisplaySetupPanel.DISPLAY_COMPONENT_PROPERTY, old, type);
		} else if (e.getSource() == this.cmbScaleQuality) {
			ScaleQuality old = this.component.getScaleQuality();
			ScaleQuality quality = (ScaleQuality)e.getItem();
			this.component.setScaleQuality(quality);
			this.firePropertyChange(DisplaySetupPanel.DISPLAY_COMPONENT_PROPERTY, old, quality);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.chkVisible) {
			boolean old = this.component.isVisible();
			boolean flag = this.chkVisible.isSelected();
			this.component.setVisible(flag);
			this.firePropertyChange(DisplaySetupPanel.DISPLAY_COMPONENT_PROPERTY, old, flag);
		}
	}
	
	/**
	 * ListCellRenderer for the scale type.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class ScaleTypeRenderer extends DefaultListCellRenderer {
		/** The version id */
		private static final long serialVersionUID = -3947778319581643564L;

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if (value instanceof ScaleType) {
				ScaleType type = (ScaleType)value;
				if (type == ScaleType.NONE) {
					this.setText(Messages.getString("panel.image.setup.scaleType.none"));
					this.setToolTipText(Messages.getString("panel.image.setup.scaleType.none.tooltip"));
					this.setIcon(Icons.IMAGE_SCALE_NONE);
				} else if (type == ScaleType.UNIFORM) {
					this.setText(Messages.getString("panel.image.setup.scaleType.uniform"));
					this.setToolTipText(Messages.getString("panel.image.setup.scaleType.uniform.tooltip"));
					this.setIcon(Icons.IMAGE_SCALE_NONUNIFORM);
				} else if (type == ScaleType.NONUNIFORM) {
					this.setText(Messages.getString("panel.image.setup.scaleType.nonuniform"));
					this.setToolTipText(Messages.getString("panel.image.setup.scaleType.nonuniform.tooltip"));
					this.setIcon(Icons.IMAGE_SCALE_UNIFORM);
				}
			}
			
			return this;
		}
	}
	
	/**
	 * ListCellRenderer for the scale quality.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class ScaleQualityRenderer extends DefaultListCellRenderer {
		/** The version id */
		private static final long serialVersionUID = -3747597107187786695L;

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if (value instanceof ScaleQuality) {
				ScaleQuality type = (ScaleQuality)value;
				if (type == ScaleQuality.NEAREST_NEIGHBOR) {
					this.setText(Messages.getString("panel.image.setup.scaleQuality.fastest"));
					this.setToolTipText(Messages.getString("panel.image.setup.scaleQuality.fastest.tooltip"));
				} else if (type == ScaleQuality.BILINEAR) {
					this.setText(Messages.getString("panel.image.setup.scaleQuality.normal"));
					this.setToolTipText(Messages.getString("panel.image.setup.scaleQuality.normal.tooltip"));
				} else if (type == ScaleQuality.BICUBIC) {
					this.setText(Messages.getString("panel.image.setup.scaleQuality.best"));
					this.setToolTipText(Messages.getString("panel.image.setup.scaleQuality.best.tooltip"));
				}
			}
			
			return this;
		}
	}
}
