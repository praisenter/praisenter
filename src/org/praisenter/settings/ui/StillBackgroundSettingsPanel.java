package org.praisenter.settings.ui;

import java.awt.Color;
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
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.display.CompositeType;
import org.praisenter.display.StillBackgroundComponent;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;
import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.ui.ImageFileFilter;
import org.praisenter.ui.ImageFilePreview;

/**
 * Panel used to setup an image background.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class StillBackgroundSettingsPanel extends JPanel implements ActionListener, ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -8394312426259802361L;

	/** The class logger */
	private static final Logger LOGGER = Logger.getLogger(StillBackgroundSettingsPanel.class);
	
	/** The {@link StillBackgroundComponent} to setup */
	protected StillBackgroundComponent component;
	
	/** The button to select the image from the file system */
	protected JButton btnImageSelect;
	
	/** The combo box to select the image scale type */
	protected JComboBox<ScaleType> cmbImageScaleType;
	
	/** The combo box to select the image scale quality */
	protected JComboBox<ScaleQuality> cmbImageScaleQuality;
	
	/** The checkbox for image visibility */
	protected JCheckBox chkImageVisible;

	/** The button to show the color chooser */
	protected JButton btnColorSelect;
	
	/** The checkbox for color visibility */
	protected JCheckBox chkColorVisible;
	
	/** The combo box for color compositing */
	protected JComboBox<CompositeType> cmbColorCompositeType;
	
	/** The checkbox for overall visibility */
	protected JCheckBox chkVisible;

	/**
	 * Minimal constructor.
	 * @param component the component to setup
	 */
	public StillBackgroundSettingsPanel(StillBackgroundComponent component) {
		this.component = component;
		
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.still.setup.title")));
		
		// color
		JLabel lblColor = new JLabel(Messages.getString("panel.color.setup.title"));
		this.btnColorSelect = new JButton(Icons.COLOR);
		this.btnColorSelect.setToolTipText(Messages.getString("panel.color.setup.browse"));
		this.btnColorSelect.addActionListener(this);
		this.btnColorSelect.setActionCommand("color-select");
		
		// color composite type
		this.cmbColorCompositeType = new JComboBox<CompositeType>(CompositeType.values());
		this.cmbColorCompositeType.setToolTipText(Messages.getString("panel.color.setup.compositeType"));
		this.cmbColorCompositeType.setRenderer(new CompositeTypeRenderer());
		this.cmbColorCompositeType.setSelectedItem(this.component.getColorCompositeType());
		this.cmbColorCompositeType.addItemListener(this);
		
		// color visibility
		this.chkColorVisible = new JCheckBox(Messages.getString("panel.color.setup.visible"), this.component.isColorVisible());
		this.chkColorVisible.addChangeListener(this);
		
		// image
		JLabel lblImage = new JLabel(Messages.getString("panel.image.setup.title"));
		this.btnImageSelect = new JButton(Messages.getString("panel.image.setup.browse"));
		this.btnImageSelect.setToolTipText(Messages.getString("panel.image.setup.browse.tooltip"));
		this.btnImageSelect.addActionListener(this);
		this.btnImageSelect.setActionCommand("image-select");
		
		// image scale type
		this.cmbImageScaleType = new JComboBox<ScaleType>(ScaleType.values());
		this.cmbImageScaleType.setToolTipText(Messages.getString("panel.image.setup.scaleType"));
		this.cmbImageScaleType.setRenderer(new ScaleTypeRenderer());
		this.cmbImageScaleType.setSelectedItem(this.component.getImageScaleType());
		this.cmbImageScaleType.addItemListener(this);
		
		// image scale quality
		this.cmbImageScaleQuality = new JComboBox<ScaleQuality>(ScaleQuality.values());
		this.cmbImageScaleQuality.setToolTipText(Messages.getString("panel.image.setup.scaleQuality"));
		this.cmbImageScaleQuality.setRenderer(new ScaleQualityRenderer());
		this.cmbImageScaleQuality.setSelectedItem(this.component.getImageScaleQuality());
		this.cmbImageScaleQuality.addItemListener(this);
		
		// image visible
		this.chkImageVisible = new JCheckBox(Messages.getString("panel.image.setup.visible"), this.component.isImageVisible());
		this.chkImageVisible.addChangeListener(this);
		
		// overall visibility
		this.chkVisible = new JCheckBox(Messages.getString("panel.still.setup.visible"), this.component.isVisible());
		this.chkVisible.addChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblColor)
						.addComponent(lblImage))
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addComponent(this.btnColorSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(this.cmbColorCompositeType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(this.chkColorVisible))
					.addGroup(layout.createSequentialGroup()
							.addComponent(this.btnImageSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(this.cmbImageScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(this.cmbImageScaleQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(this.chkImageVisible))
					.addComponent(this.chkVisible)));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblColor)
						.addComponent(this.btnColorSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbColorCompositeType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkColorVisible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblImage)
						.addComponent(this.btnImageSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbImageScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbImageScaleQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkImageVisible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.chkVisible));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("image-select".equals(command)) {
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
					this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, image);
				} catch (IOException ex) {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.image.setup.exception.read.title"), 
							MessageFormat.format(Messages.getString("panel.image.setup.exception.read.text"), file.getAbsolutePath()), 
							ex);
					LOGGER.error("An error occurred while reading the selected file [" + file.getAbsolutePath() + "]: ", ex);
				}
			}
		} else if ("color-select".equals(command)) {
			Color old = this.component.getColor();
			// show the color chooser
			Color color = JColorChooser.showDialog(this, Messages.getString("panel.color.setup.browse"), old);
			if (color != null) {
				this.component.setColor(color);
				this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, color);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == this.cmbImageScaleType) {
			// the scale type was changed
			ScaleType old = this.component.getImageScaleType();
			ScaleType type = (ScaleType)e.getItem();
			this.component.setImageScaleType(type);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, type);
		} else if (e.getSource() == this.cmbImageScaleQuality) {
			ScaleQuality old = this.component.getImageScaleQuality();
			ScaleQuality quality = (ScaleQuality)e.getItem();
			this.component.setImageScaleQuality(quality);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, quality);
		} else if (e.getSource() == this.cmbColorCompositeType) {
			CompositeType old = this.component.getColorCompositeType();
			CompositeType type = (CompositeType)e.getItem();
			this.component.setColorCompositeType(type);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, type);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.chkImageVisible) {
			boolean old = this.component.isImageVisible();
			boolean flag = this.chkImageVisible.isSelected();
			this.component.setImageVisible(flag);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, flag);
		} else if (source == this.chkColorVisible) {
			boolean old = this.component.isColorVisible();
			boolean flag = this.chkColorVisible.isSelected();
			this.component.setColorVisible(flag);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, flag);
		} else if (source == this.chkVisible) {
			boolean old = this.component.isVisible();
			boolean flag = this.chkVisible.isSelected();
			this.component.setVisible(flag);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, flag);
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
	
	/**
	 * ListCellRenderer for the composite type.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class CompositeTypeRenderer extends DefaultListCellRenderer {
		/** The version id */
		private static final long serialVersionUID = -7830562519385465036L;

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if (value instanceof CompositeType) {
				CompositeType type = (CompositeType)value;
				if (type == CompositeType.OVERLAY) {
					this.setText(Messages.getString("panel.color.setup.compositeType.overlay"));
					this.setToolTipText(Messages.getString("panel.color.setup.compositeType.overlay.tooltip"));
				} else if (type == CompositeType.UNDERLAY) {
					this.setText(Messages.getString("panel.color.setup.compositeType.underlay"));
					this.setToolTipText(Messages.getString("panel.color.setup.compositeType.underlay.tooltip"));
				}
			}
			
			return this;
		}
	}
}
