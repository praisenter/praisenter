package org.praisenter.settings.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.display.FontScaleType;
import org.praisenter.display.TextAlignment;
import org.praisenter.display.TextComponent;
import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.settings.TextComponentSettings;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.utilities.FontManager;

/**
 * Represents a panel to setup a {@link TextComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TextComponentSettingsPanel extends JPanel implements ItemListener, ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 3293259447883381709L;

	/** The text component to setup */
	protected TextComponent component;
	
	// controls
	
	/** The font family combo box */
	protected JComboBox<String> cmbFontFamilies;
	
	/** The toggle button for bold */
	protected JToggleButton tglBold;
	
	/** The toggle button for italic */
	protected JToggleButton tglItalic;
	
	/** The font size spinner */
	protected JSpinner spnFontSize;
	
	/** The font size scale type combo box */
	protected JComboBox<FontScaleType> cmbFontScaleType;
	
	/** The text alignment left toggle button */
	protected JToggleButton tglTextAlignmentLeft;
	
	/** The text alignment center toggle button */
	protected JToggleButton tglTextAlignmentCenter;
	
	/** The text alignment right toggle button */
	protected JToggleButton tglTextAlignmentRight;
	
	/** The wrap text check box */
	protected JCheckBox chkWrapText;
	
	/** The padding text box */
	protected JSpinner spnPadding;
	
	/** The visible check box */
	protected JCheckBox chkVisible;
	
	/**
	 * Full constructor.
	 * @param component the text component to setup
	 */
	public TextComponentSettingsPanel(TextComponent component) {
		// TODO for non-placeholder components the text should come from the settings
		this.component = component;
		
		// labels
		
		JLabel lblFontFamily = new JLabel(Messages.getString("panel.text.setup.fontFamily"));
		JLabel lblFontSize = new JLabel(Messages.getString("panel.text.setup.fontSize"));
		JLabel lblLayout = new JLabel(Messages.getString("panel.text.setup.layout"));
		
		// the font family selection
		Font font = this.component.getTextFont();
		if (font == null) {
			font = TextComponentSettings.DEFALUT_FONT;
		}
		String[] fonts = FontManager.getFontFamilyNames();
		this.cmbFontFamilies = new JComboBox<String>(fonts);
		this.cmbFontFamilies.setRenderer(new FontFamilyRenderer());
		if (this.isFontAvailable(font.getFamily(), fonts)) {
			this.cmbFontFamilies.setSelectedItem(font.getFamily());
		}
		this.cmbFontFamilies.addItemListener(this);
		
		// the font style(s)
		this.tglBold = new JToggleButton(Icons.BOLD);
		this.tglBold.setSelected(font.isBold());
		this.tglBold.setToolTipText(Messages.getString("panel.text.setup.bold"));
		this.tglBold.setActionCommand("bold");
		this.tglBold.addActionListener(this);
		this.tglItalic = new JToggleButton(Icons.ITALIC);
		this.tglItalic.setSelected(font.isItalic());
		this.tglItalic.setToolTipText(Messages.getString("panel.text.setup.italic"));
		this.tglItalic.setActionCommand("italic");
		this.tglItalic.addActionListener(this);
		
		JPanel pnlBoldItalic = new JPanel();
		pnlBoldItalic.setLayout(new BorderLayout());
		pnlBoldItalic.add(this.tglBold, BorderLayout.LINE_START);
		pnlBoldItalic.add(this.tglItalic, BorderLayout.LINE_END);
		
		// the font size
		int fontSize = font.getSize();
		this.spnFontSize = new JSpinner(new SpinnerNumberModel(fontSize, 1, Integer.MAX_VALUE, 1));
		this.spnFontSize.addChangeListener(this);
		this.spnFontSize.setToolTipText(Messages.getString("panel.text.setup.fontSize.tooltip"));
		JTextField txtFontSize = ((DefaultEditor)this.spnFontSize.getEditor()).getTextField();
		txtFontSize.setColumns(3);
		txtFontSize.addFocusListener(new SelectTextFocusListener(txtFontSize));
		
		// font scale type
		this.cmbFontScaleType = new JComboBox<FontScaleType>(FontScaleType.values());
		this.cmbFontScaleType.setToolTipText(Messages.getString("panel.text.setup.fontScaleType.tooltip"));
		this.cmbFontScaleType.setRenderer(new FontScaleTypeRenderer());
		this.cmbFontScaleType.setSelectedItem(this.component.getTextFontScaleType());
		this.cmbFontScaleType.addItemListener(this);
		
		// alignments
		TextAlignment alignment = component.getTextAlignment();
		this.tglTextAlignmentLeft = new JToggleButton(Icons.ALIGN_LEFT);
		this.tglTextAlignmentLeft.setActionCommand("align-left");
		this.tglTextAlignmentLeft.setToolTipText(Messages.getString("panel.text.setup.align.left"));
		this.tglTextAlignmentLeft.setSelected(alignment == TextAlignment.LEFT);
		this.tglTextAlignmentCenter = new JToggleButton(Icons.ALIGN_CENTER);
		this.tglTextAlignmentCenter.setActionCommand("align-center");
		this.tglTextAlignmentCenter.setToolTipText(Messages.getString("panel.text.setup.align.center"));
		this.tglTextAlignmentCenter.setSelected(alignment == TextAlignment.CENTER);
		this.tglTextAlignmentRight = new JToggleButton(Icons.ALIGN_RIGHT);
		this.tglTextAlignmentRight.setActionCommand("align-right");
		this.tglTextAlignmentRight.setToolTipText(Messages.getString("panel.text.setup.align.right"));
		this.tglTextAlignmentRight.setSelected(alignment == TextAlignment.RIGHT);
		
		ButtonGroup bgAlignment = new ButtonGroup();
		bgAlignment.add(this.tglTextAlignmentLeft);
		bgAlignment.add(this.tglTextAlignmentCenter);
		bgAlignment.add(this.tglTextAlignmentRight);
		
		this.tglTextAlignmentLeft.addActionListener(this);
		this.tglTextAlignmentCenter.addActionListener(this);
		this.tglTextAlignmentRight.addActionListener(this);
		
		JPanel pnlAlignment = new JPanel();
		pnlAlignment.setLayout(new BorderLayout());
		pnlAlignment.add(this.tglTextAlignmentLeft, BorderLayout.LINE_START);
		pnlAlignment.add(this.tglTextAlignmentCenter, BorderLayout.CENTER);
		pnlAlignment.add(this.tglTextAlignmentRight, BorderLayout.LINE_END);
		
		// text wrap
		this.chkWrapText = new JCheckBox(Messages.getString("panel.text.setup.wrapping"), this.component.isTextWrapped());
		this.chkWrapText.setToolTipText(Messages.getString("panel.text.setup.wrapping.tooltip"));
		this.chkWrapText.addChangeListener(this);
		
		// text color
		JButton btnTextColor = new JButton(Icons.COLOR);
		btnTextColor.setToolTipText(Messages.getString("panel.text.setup.color"));
		btnTextColor.addActionListener(this);
		btnTextColor.setActionCommand("textColor");
		
		this.spnPadding = new JSpinner(new SpinnerNumberModel(this.component.getPadding(), 0, Integer.MAX_VALUE, 1));
		this.spnPadding.addChangeListener(this);
		this.spnPadding.setToolTipText(Messages.getString("panel.text.setup.padding.tooltip"));
		JTextField txtPadding = ((DefaultEditor)this.spnPadding.getEditor()).getTextField();
		txtPadding.setColumns(2);
		txtPadding.addFocusListener(new SelectTextFocusListener(txtPadding));
		
		// visible
		this.chkVisible = new JCheckBox(Messages.getString("panel.text.setup.visible"), this.component.isVisible());
		this.chkVisible.setToolTipText(Messages.getString("panel.text.setup.visible.tooltip"));
		this.chkVisible.addChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				// column 1
				.addGroup(layout.createParallelGroup()
						.addComponent(lblFontFamily)
						.addComponent(lblFontSize)
						.addComponent(lblLayout))
				// column 2
				.addGroup(layout.createParallelGroup()
						// font row
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbFontFamilies)
								.addComponent(pnlBoldItalic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnTextColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						// size row
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.cmbFontScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						// layout row
						.addGroup(layout.createSequentialGroup()
								.addComponent(pnlAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.spnPadding, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.chkWrapText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.chkVisible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				// font row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFontFamily)
						.addComponent(this.cmbFontFamilies, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlBoldItalic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnTextColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// size row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFontSize)
						.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbFontScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// layout row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblLayout)
						.addComponent(pnlAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.spnPadding, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkWrapText)
						.addComponent(this.chkVisible)));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getSource();
		if (source == this.cmbFontFamilies) {
			String family = (String)this.cmbFontFamilies.getSelectedItem();
			Font old = this.component.getTextFont();
			if (old == null) {
				old = TextComponentSettings.DEFALUT_FONT;
			}
			Font font = new Font(family, old.getStyle(), old.getSize());
			this.component.setTextFont(font);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, font);
		} else if (source == this.cmbFontScaleType) {
			FontScaleType old = this.component.getTextFontScaleType();
			FontScaleType type = (FontScaleType)this.cmbFontScaleType.getSelectedItem();
			this.component.setTextFontScaleType(type);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, type);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		if (source == this.chkWrapText) {
			boolean old = this.component.isTextWrapped();
			boolean flag = this.chkWrapText.isSelected();
			this.component.setTextWrapped(flag);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, flag);
		} else if (source == this.chkVisible) {
			boolean old = this.component.isVisible();
			boolean flag = this.chkVisible.isSelected();
			this.component.setVisible(flag);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, flag);
		} else if (source == this.spnFontSize) {
			Object nv = this.spnFontSize.getModel().getValue();
			if (nv instanceof Number) {
				Number nnv = (Number)nv;
				float size = nnv.floatValue();
				Font old = this.component.getTextFont();
				if (old == null) {
					old = TextComponentSettings.DEFALUT_FONT;
				}
				Font font = old.deriveFont(size);
				this.component.setTextFont(font);
				this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, font);
			}
		} else if (source == this.spnPadding) {
			Object nv = this.spnPadding.getModel().getValue();
			if (nv instanceof Number) {
				Number nnv = (Number)nv;
				int old = this.component.getPadding();
				int padding = nnv.intValue();
				this.component.setPadding(padding);
				this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, padding);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if ("textColor".equals(command)) {
			Color old = this.component.getTextColor();
			// show the color chooser
			Color color = JColorChooser.showDialog(this, Messages.getString("panel.text.setup.color"), old);
			if (color != null) {
				this.component.setTextColor(color);
				this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, color);
			}
		} else if ("bold".equals(command) || "italic".equals(command)) {
			int style = 0;
			if (this.tglBold.isSelected()) {
				style += Font.BOLD;
			}
			if (this.tglItalic.isSelected()) {
				style += Font.ITALIC;
			}
			if (style == 0) {
				style = Font.PLAIN;
			}
			Font old = this.component.getTextFont();
			if (old == null) {
				old = TextComponentSettings.DEFALUT_FONT;
			}
			Font font = old.deriveFont(style);
			this.component.setTextFont(font);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, font);
		} else if ("align-left".equals(command)) {
			TextAlignment old = this.component.getTextAlignment();
			this.component.setTextAlignment(TextAlignment.LEFT);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, TextAlignment.LEFT);
		} else if ("align-center".equals(command)) {
			TextAlignment old = this.component.getTextAlignment();
			this.component.setTextAlignment(TextAlignment.CENTER);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, TextAlignment.CENTER);
		} else if ("align-right".equals(command)) {
			TextAlignment old = this.component.getTextAlignment();
			this.component.setTextAlignment(TextAlignment.RIGHT);
			this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, TextAlignment.RIGHT);
		}
	}
	
	/**
	 * Returns true if the given font is in the given array of fonts.
	 * @param font the font to find
	 * @param fonts the array of available fonts
	 * @return boolean
	 */
	private boolean isFontAvailable(String font, String[] fonts) {
		if (fonts != null) {
			for (String f : fonts) {
				if (f.equals(font)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Renderer for showing fonts.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class FontFamilyRenderer extends DefaultListCellRenderer {
		/** The version id */
		private static final long serialVersionUID = -3747597107187786695L;

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof String) {
				String family = (String)value;
				// create the font using the family name
				Font font = FontManager.getFont(family, Font.PLAIN, this.getFont().getSize());
				// fix some fonts showing all boxes
				if (font.canDisplayUpTo(family) < 0) {
					// hack to fix some fonts taking up way too much height-wise space
					Rectangle2D bounds = font.getMaxCharBounds(new FontRenderContext(new AffineTransform(), true, true));
					if (bounds.getHeight() <= 50) {
						this.setFont(font);
					}
				}
			}
			
			return this;
		}
	}
	
	/**
	 * Renderer for showing font scale types.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class FontScaleTypeRenderer extends DefaultListCellRenderer {
		/** The version id */
		private static final long serialVersionUID = -3747597107187786695L;

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof FontScaleType) {
				FontScaleType type = (FontScaleType)value;
				if (type == FontScaleType.NONE) {
					this.setText(Messages.getString("panel.text.setup.fontScaleType.none"));
					this.setToolTipText(Messages.getString("panel.text.setup.fontScaleType.none.tooltip"));
					this.setIcon(Icons.FONT_SIZE_NONE);
				} else if (type == FontScaleType.REDUCE_SIZE_ONLY) {
					this.setText(Messages.getString("panel.text.setup.fontScaleType.reduceSizeOnly"));
					this.setToolTipText(Messages.getString("panel.text.setup.fontScaleType.reduceSizeOnly.tooltip"));
					this.setIcon(Icons.FONT_SIZE_REDUCE_ONLY);
				} else if (type == FontScaleType.BEST_FIT) {
					this.setText(Messages.getString("panel.text.setup.fontScaleType.bestFit"));
					this.setToolTipText(Messages.getString("panel.text.setup.fontScaleType.bestFit.tooltip"));
					this.setIcon(Icons.FONT_SIZE_BEST_FIT);
				}
			}
			
			return this;
		}
	}
}
