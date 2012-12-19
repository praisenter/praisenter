package org.praisenter.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.utilities.FontManager;

/**
 * Editor panel for {@link TextComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class TextComponentEditorPanel extends GenericComponentEditorPanel<TextComponent> implements ActionListener, ItemListener, EditorListener, DocumentListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 6502908345432550911L;

	// labels
	
	/** The font family label */
	protected JLabel lblFontFamily;
	
	/** The font size label */
	protected JLabel lblFontSize;
	
	/** The layout label */
	protected JLabel lblLayout;
	
	// controls
	
	/** The checkbox for text visibility */
	protected JCheckBox chkTextVisible;
	
	/** The font family combo box */
	protected JComboBox<String> cmbFontFamilies;
	
	/** The toggle button for bold */
	protected JToggleButton tglBold;
	
	/** The toggle button for italic */
	protected JToggleButton tglItalic;
	
	/** The bold/italic panel */
	protected JPanel pnlBoldItalic;
	
	/** The font size spinner */
	protected JSpinner spnFontSize;
	
	/** The font size scale type combo box */
	protected JComboBox<FontScaleType> cmbFontScaleType;
	
	/** The horizontal text alignment left toggle button */
	protected JToggleButton tglHorizontalTextAlignmentLeft;
	
	/** The horizontal text alignment center toggle button */
	protected JToggleButton tglHorizontalTextAlignmentCenter;
	
	/** The horizontal text alignment right toggle button */
	protected JToggleButton tglHorizontalTextAlignmentRight;
	
	/** The vertical text alignment top toggle button */
	protected JToggleButton tglVerticalTextAlignmentTop;
	
	/** The vertical text alignment center toggle button */
	protected JToggleButton tglVerticalTextAlignmentCenter;
	
	/** The vertical text alignment bottom toggle button */
	protected JToggleButton tglVerticalTextAlignmentBottom;
	
	/** The horizontal text alignment panel */
	protected JPanel pnlHorizontalTextAlignment;

	/** The vertical text alignment panel */
	protected JPanel pnlVerticalTextAlignment;
	
	/** The wrap text check box */
	protected JCheckBox chkWrapText;
	
	/** The padding text box */
	protected JSpinner spnPadding;

	/** The font fill */
	protected FillEditorPanel pnlTextFill;
	
	/** The text box for the text */
	protected JTextArea txtText;

	/**
	 * Default constructor.
	 */
	public TextComponentEditorPanel() {
		// pass false down so that GenericSlideComponentEditorPanel doesn't build
		// the layout, we need this class to build its own layout
		super(false);
		
		this.chkTextVisible = new JCheckBox(Messages.getString("panel.slide.editor.visible"));
		this.chkTextVisible.addChangeListener(this);
		
		this.txtText = new JTextArea();
		this.txtText.setRows(8);
		this.txtText.setLineWrap(true);
		this.txtText.setWrapStyleWord(true);
		this.txtText.getDocument().addDocumentListener(this);
		
		// build the text component controls
		this.lblFontFamily = new JLabel(Messages.getString("panel.slide.editor.text.font.family"));
		this.lblFontSize = new JLabel(Messages.getString("panel.slide.editor.text.font.size"));
		this.lblLayout = new JLabel(Messages.getString("panel.slide.editor.text.layout"));
		
		// the font family selection
		Font font = FontManager.getDefaultFont();
		String[] fonts = FontManager.getFontFamilyNames();
		this.cmbFontFamilies = new JComboBox<String>(fonts);
		this.cmbFontFamilies.setRenderer(new FontFamilyListCellRenderer());
		if (this.isFontAvailable(font.getFamily(), fonts)) {
			this.cmbFontFamilies.setSelectedItem(font.getFamily());
		}
		this.cmbFontFamilies.addItemListener(this);
		
		// the font style(s)
		this.tglBold = new JToggleButton(Icons.BOLD);
		this.tglBold.setSelected(font.isBold());
		this.tglBold.setToolTipText(Messages.getString("panel.slide.editor.text.font.bold"));
		this.tglBold.setActionCommand("bold");
		this.tglBold.addActionListener(this);
		this.tglItalic = new JToggleButton(Icons.ITALIC);
		this.tglItalic.setSelected(font.isItalic());
		this.tglItalic.setToolTipText(Messages.getString("panel.slide.editor.text.font.italic"));
		this.tglItalic.setActionCommand("italic");
		this.tglItalic.addActionListener(this);
		
		this.pnlBoldItalic = new JPanel();
		this.pnlBoldItalic.setLayout(new BorderLayout());
		this.pnlBoldItalic.add(this.tglBold, BorderLayout.LINE_START);
		this.pnlBoldItalic.add(this.tglItalic, BorderLayout.LINE_END);
		
		// the font size
		int fontSize = font.getSize();
		this.spnFontSize = new JSpinner(new SpinnerNumberModel(fontSize, 1, Integer.MAX_VALUE, 1));
		this.spnFontSize.addChangeListener(this);
		this.spnFontSize.setToolTipText(Messages.getString("panel.slide.editor.text.font.size.tooltip"));
		JTextField txtFontSize = ((DefaultEditor)this.spnFontSize.getEditor()).getTextField();
		txtFontSize.setColumns(3);
		txtFontSize.addFocusListener(new SelectTextFocusListener(txtFontSize));
		
		// font scale type
		this.cmbFontScaleType = new JComboBox<FontScaleType>(FontScaleType.values());
		this.cmbFontScaleType.setToolTipText(Messages.getString("panel.slide.editor.text.font.scale"));
		this.cmbFontScaleType.setRenderer(new FontScaleTypeListCellRenderer());
		this.cmbFontScaleType.setSelectedItem(FontScaleType.REDUCE_SIZE_ONLY);
		this.cmbFontScaleType.addItemListener(this);
		
		// alignments
		HorizontalTextAlignment halignment = HorizontalTextAlignment.LEFT;
		this.tglHorizontalTextAlignmentLeft = new JToggleButton(Icons.HORIZONTAL_ALIGN_LEFT);
		this.tglHorizontalTextAlignmentLeft.setActionCommand("align-h-left");
		this.tglHorizontalTextAlignmentLeft.setToolTipText(Messages.getString("panel.slide.editor.text.align.horizontal.left"));
		this.tglHorizontalTextAlignmentLeft.setSelected(halignment == HorizontalTextAlignment.LEFT);
		this.tglHorizontalTextAlignmentCenter = new JToggleButton(Icons.HORIZONTAL_ALIGN_CENTER);
		this.tglHorizontalTextAlignmentCenter.setActionCommand("align-h-center");
		this.tglHorizontalTextAlignmentCenter.setToolTipText(Messages.getString("panel.slide.editor.text.align.horizontal.center"));
		this.tglHorizontalTextAlignmentCenter.setSelected(halignment == HorizontalTextAlignment.CENTER);
		this.tglHorizontalTextAlignmentRight = new JToggleButton(Icons.HORIZONTAL_ALIGN_RIGHT);
		this.tglHorizontalTextAlignmentRight.setActionCommand("align-h-right");
		this.tglHorizontalTextAlignmentRight.setToolTipText(Messages.getString("panel.slide.editor.text.align.horizontal.right"));
		this.tglHorizontalTextAlignmentRight.setSelected(halignment == HorizontalTextAlignment.RIGHT);
		
		ButtonGroup bgHorizontalAlignment = new ButtonGroup();
		bgHorizontalAlignment.add(this.tglHorizontalTextAlignmentLeft);
		bgHorizontalAlignment.add(this.tglHorizontalTextAlignmentCenter);
		bgHorizontalAlignment.add(this.tglHorizontalTextAlignmentRight);
		
		this.tglHorizontalTextAlignmentLeft.addActionListener(this);
		this.tglHorizontalTextAlignmentCenter.addActionListener(this);
		this.tglHorizontalTextAlignmentRight.addActionListener(this);
		
		this.pnlHorizontalTextAlignment = new JPanel();
		this.pnlHorizontalTextAlignment.setLayout(new BorderLayout());
		this.pnlHorizontalTextAlignment.add(this.tglHorizontalTextAlignmentLeft, BorderLayout.LINE_START);
		this.pnlHorizontalTextAlignment.add(this.tglHorizontalTextAlignmentCenter, BorderLayout.CENTER);
		this.pnlHorizontalTextAlignment.add(this.tglHorizontalTextAlignmentRight, BorderLayout.LINE_END);
		
		VerticalTextAlignment valignment = VerticalTextAlignment.TOP;
		this.tglVerticalTextAlignmentTop = new JToggleButton(Icons.VERTICAL_ALIGN_TOP);
		this.tglVerticalTextAlignmentTop.setActionCommand("align-v-top");
		this.tglVerticalTextAlignmentTop.setToolTipText(Messages.getString("panel.slide.editor.text.align.vertical.top"));
		this.tglVerticalTextAlignmentTop.setSelected(valignment == VerticalTextAlignment.TOP);
		this.tglVerticalTextAlignmentCenter = new JToggleButton(Icons.VERTICAL_ALIGN_CENTER);
		this.tglVerticalTextAlignmentCenter.setActionCommand("align-v-center");
		this.tglVerticalTextAlignmentCenter.setToolTipText(Messages.getString("panel.slide.editor.text.align.vertical.center"));
		this.tglVerticalTextAlignmentCenter.setSelected(valignment == VerticalTextAlignment.CENTER);
		this.tglVerticalTextAlignmentBottom = new JToggleButton(Icons.VERTICAL_ALIGN_BOTTOM);
		this.tglVerticalTextAlignmentBottom.setActionCommand("align-v-bottom");
		this.tglVerticalTextAlignmentBottom.setToolTipText(Messages.getString("panel.slide.editor.text.align.vertical.bottom"));
		this.tglVerticalTextAlignmentBottom.setSelected(valignment == VerticalTextAlignment.BOTTOM);
		
		ButtonGroup bgVerticalAlignment = new ButtonGroup();
		bgVerticalAlignment.add(this.tglVerticalTextAlignmentTop);
		bgVerticalAlignment.add(this.tglVerticalTextAlignmentCenter);
		bgVerticalAlignment.add(this.tglVerticalTextAlignmentBottom);
		
		this.tglVerticalTextAlignmentTop.addActionListener(this);
		this.tglVerticalTextAlignmentCenter.addActionListener(this);
		this.tglVerticalTextAlignmentBottom.addActionListener(this);
		
		this.pnlVerticalTextAlignment = new JPanel();
		this.pnlVerticalTextAlignment.setLayout(new BorderLayout());
		this.pnlVerticalTextAlignment.add(this.tglVerticalTextAlignmentTop, BorderLayout.LINE_START);
		this.pnlVerticalTextAlignment.add(this.tglVerticalTextAlignmentCenter, BorderLayout.CENTER);
		this.pnlVerticalTextAlignment.add(this.tglVerticalTextAlignmentBottom, BorderLayout.LINE_END);
		
		// text wrap
		this.chkWrapText = new JCheckBox(Messages.getString("panel.slide.editor.text.wrap"), true);
		this.chkWrapText.addChangeListener(this);
		
		// text fill
		this.pnlTextFill = new FillEditorPanel(null);
		this.pnlTextFill.addEditorListener(this);
		
		this.spnPadding = new JSpinner(new SpinnerNumberModel(30, 0, Integer.MAX_VALUE, 1));
		this.spnPadding.addChangeListener(this);
		this.spnPadding.setToolTipText(Messages.getString("panel.slide.editor.text.padding"));
		JTextField txtPadding = ((DefaultEditor)this.spnPadding.getEditor()).getTextField();
		txtPadding.setColumns(2);
		txtPadding.addFocusListener(new SelectTextFocusListener(txtPadding));
		
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
		
		JPanel pnlText = new JPanel();
		this.createTextLayout(pnlText);
		tabs.addTab(Messages.getString("panel.slide.editor.text"), pnlText);
		
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
	 * Creates a layout for the text component on the given panel.
	 * @param panel the panel
	 */
	protected void createTextLayout(JPanel panel) {
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		this.txtText.setMinimumSize(new Dimension(50, 200));
		JScrollPane scrText = new JScrollPane(this.txtText);
		scrText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.chkTextVisible)
				.addComponent(scrText)
				.addGroup(layout.createSequentialGroup()
						// column 1
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblFontFamily)
								.addComponent(this.lblFontSize)
								.addComponent(this.lblLayout))
						// column 2
						.addGroup(layout.createParallelGroup()
								// font row
								.addComponent(this.cmbFontFamilies)
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.pnlBoldItalic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.spnPadding, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.chkWrapText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								// size row
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.cmbFontScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								// layout row
								.addComponent(this.pnlHorizontalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							    // layout row 2
								.addComponent(this.pnlVerticalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addComponent(this.pnlTextFill));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.chkTextVisible)
				.addComponent(scrText)
				// font row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblFontFamily)
						.addComponent(this.cmbFontFamilies, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.pnlBoldItalic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.spnPadding, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkWrapText))
				// size row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblFontSize)
						.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbFontScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// layout row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLayout)
						.addComponent(this.pnlHorizontalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// layout row 2
				.addComponent(this.pnlVerticalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlTextFill));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getSource();
		if (source == this.cmbFontFamilies) {
			String family = (String)this.cmbFontFamilies.getSelectedItem();
			if (this.slideComponent != null) {
				Font old = this.slideComponent.getTextFont();
				if (old == null) {
					old = FontManager.getDefaultFont();
				}
				Font font = new Font(family, old.getStyle(), old.getSize());
				this.slideComponent.setTextFont(font);
				this.notifyEditorListeners();
			}
		} else if (source == this.cmbFontScaleType) {
			if (this.slideComponent != null) {
				FontScaleType scaleType = (FontScaleType)this.cmbFontScaleType.getSelectedItem();
				this.slideComponent.setTextFontScaleType(scaleType);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		// send to the super class first
		super.stateChanged(event);
		
		Object source = event.getSource();
		if (source == this.chkWrapText) {
			boolean flag = this.chkWrapText.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setTextWrapped(flag);
				this.notifyEditorListeners();
			}
		} else if (source == this.spnFontSize) {
			Object nv = this.spnFontSize.getModel().getValue();
			if (nv instanceof Number && this.slideComponent != null) {
				Number nnv = (Number)nv;
				float size = nnv.floatValue();
				Font old = this.slideComponent.getTextFont();
				if (old == null) {
					old = FontManager.getDefaultFont();
				}
				Font font = old.deriveFont(size);
				this.slideComponent.setTextFont(font);
				this.notifyEditorListeners();
			}
		} else if (source == this.spnPadding) {
			Object nv = this.spnPadding.getModel().getValue();
			if (nv instanceof Number && this.slideComponent != null) {
				Number nnv = (Number)nv;
				int padding = nnv.intValue();
				this.slideComponent.setTextPadding(padding);
				this.notifyEditorListeners();
			}
		} else if (source == this.chkTextVisible) {
			boolean flag = this.chkTextVisible.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setTextVisible(flag);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if ("bold".equals(command) || "italic".equals(command)) {
			if (this.slideComponent != null) {
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
				Font old = this.slideComponent.getTextFont();
				if (old == null) {
					old = FontManager.getDefaultFont();
				}
				Font font = old.deriveFont(style);
				this.slideComponent.setTextFont(font);
				this.notifyEditorListeners();
			}
		} else if ("align-h-left".equals(command)) {
			if (this.slideComponent != null) {
				this.slideComponent.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
				this.notifyEditorListeners();
			}
		} else if ("align-h-center".equals(command)) {
			if (this.slideComponent != null) {
				this.slideComponent.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				this.notifyEditorListeners();
			}
		} else if ("align-h-right".equals(command)) {
			if (this.slideComponent != null) {
				this.slideComponent.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
				this.notifyEditorListeners();
			}
		} else if ("align-v-top".equals(command)) {
			if (this.slideComponent != null) {
				this.slideComponent.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				this.notifyEditorListeners();
			}
		} else if ("align-v-center".equals(command)) {
			if (this.slideComponent != null) {
				this.slideComponent.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
				this.notifyEditorListeners();
			}
		} else if ("align-v-bottom".equals(command)) {
			if (this.slideComponent != null) {
				this.slideComponent.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		super.insertUpdate(e);
		if (this.txtText.getDocument() == e.getDocument()) {
			if (this.slideComponent != null) {
				this.slideComponent.setText(this.txtText.getText());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		super.removeUpdate(e);
		if (this.txtText.getDocument() == e.getDocument()) {
			if (this.slideComponent != null) {
				this.slideComponent.setText(this.txtText.getText());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.GenericSlideComponentEditorPanel#editPerformed(org.praisenter.slide.ui.editor.EditEvent)
	 */
	@Override
	public void editPerformed(EditEvent event) {
		super.editPerformed(event);
		
		if (event.getSource() == this.pnlTextFill) {
			if (this.slideComponent != null) {
				this.slideComponent.setTextFill(this.pnlTextFill.getFill());
				this.notifyEditorListeners();
			}
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.GenericSlideComponentEditorPanel#setSlideComponent(org.praisenter.slide.GenericSlideComponent)
	 */
	@Override
	public void setSlideComponent(TextComponent slideComponent) {
		super.setSlideComponent(slideComponent);
		
		if (slideComponent != null) {
			this.chkWrapText.setSelected(slideComponent.isTextWrapped());
			Font font = slideComponent.getTextFont();
			if (font == null) {
				font = FontManager.getDefaultFont();
			}
			this.cmbFontFamilies.setSelectedItem(font.getFamily());
			this.cmbFontScaleType.setSelectedItem(slideComponent.getTextFontScaleType());
			this.tglBold.setSelected(font.isBold());
			this.tglItalic.setSelected(font.isItalic());
			this.tglHorizontalTextAlignmentLeft.setSelected(slideComponent.getHorizontalTextAlignment() == HorizontalTextAlignment.LEFT);
			this.tglHorizontalTextAlignmentCenter.setSelected(slideComponent.getHorizontalTextAlignment() == HorizontalTextAlignment.CENTER);
			this.tglHorizontalTextAlignmentRight.setSelected(slideComponent.getHorizontalTextAlignment() == HorizontalTextAlignment.RIGHT);
			this.tglVerticalTextAlignmentTop.setSelected(slideComponent.getVerticalTextAlignment() == VerticalTextAlignment.TOP);
			this.tglVerticalTextAlignmentCenter.setSelected(slideComponent.getVerticalTextAlignment() == VerticalTextAlignment.CENTER);
			this.tglVerticalTextAlignmentBottom.setSelected(slideComponent.getVerticalTextAlignment() == VerticalTextAlignment.BOTTOM);
			this.spnFontSize.setValue(font.getSize());
			this.spnPadding.setValue(slideComponent.getTextPadding());
			this.pnlTextFill.setFill(slideComponent.getTextFill());
			this.txtText.setText(slideComponent.getText());
			this.txtText.setCaretPosition(0);
			this.chkTextVisible.setSelected(slideComponent.isTextVisible());
		}
	}
}
