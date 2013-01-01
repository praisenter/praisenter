package org.praisenter.slide.ui.editor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LinearGradientDirection;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.RadialGradientDirection;
import org.praisenter.slide.graphics.RadialGradientFill;
import org.praisenter.slide.graphics.Stop;
import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.WindowUtilities;

// TODO we need to support multi-stop gradients (multi-thumb slider would be best)
// cycle method, focal point (radial only) aren't really needed since we are abstracting away
// most of the details of the gradient selection. This could change when we do multi-stop
// gradients

/**
 * Editor panel for MultipleGradientPaint objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class FillEditorPanel extends EditorPanel implements ActionListener, ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -4080895520033411798L;

	// controls
	
	// fill type
	
	/** The fill type */
	private JLabel lblFillType;
	
	/** The color type radio button */
	private JRadioButton rdoColor;
	
	/** The gradient type radio button */
	private JRadioButton rdoGradient;
	
	// color type
	
	/** The color label */
	private JLabel lblColor;
	
	/** The color button */
	private JButton btnColor;
	
	// gradient type
	
	/** The gradient type label */
	private JLabel lblGradientType;
	
	/** The linear gradient type radio button */
	private JRadioButton rdoLinear;
	
	/** The radial gradient type radio button */
	private JRadioButton rdoRadial;
	
	// gradient direction
	
	/** The gradient direction label */
	private JLabel lblDirection;
	
	/** The linear gradient direction combo box */
	private JComboBox<LinearGradientDirection> cmbLinearDirection;
	
	/** The radial gradient direction combo box */
	private JComboBox<RadialGradientDirection> cmbRadialDirection;
	
	// stops (fractions)
	
	/** The stop position label */
	private JLabel lblStopPosition;
	
	/** The midpoint stop position of the gradient */
	private JSlider sldStopPosition;
	
	/** The gradient color 1 button */
	private JButton btnColor1;
	
	/** The gradient color 2 button */
	private JButton btnColor2;
	
	// the fills
	
	/** The color */
	private ColorFill color;
	
	/** The configured linear gradient fill */
	private LinearGradientFill linear;
	
	/** The configured radial gradient fill */
	private RadialGradientFill radial;
	
	/**
	 * Full constructor.
	 * @param fill the initial fill
	 */
	public FillEditorPanel(Fill fill) {
		// check for a null fill, if its null then just create a linear fill
		if (fill == null) {
			fill = new LinearGradientFill();
		}
		
		this.lblFillType = new JLabel(Messages.getString("panel.slide.editor.fill.type"));
		this.rdoColor = new JRadioButton(Messages.getString("panel.slide.editor.fill.color"));
		this.rdoGradient = new JRadioButton(Messages.getString("panel.slide.editor.fill.gradient"));
		
		ButtonGroup bgFillType = new ButtonGroup();
		bgFillType.add(this.rdoColor);
		bgFillType.add(this.rdoGradient);
		
		this.lblColor = new JLabel(Messages.getString("panel.slide.editor.fill.color"));
		this.btnColor = new JButton(Messages.getString("panel.slide.editor.fill.chooseColor"));
		this.btnColor.addActionListener(this);
		this.btnColor.setActionCommand("color");
		
		this.lblGradientType = new JLabel(Messages.getString("panel.slide.editor.fill.gradient.type"));
		this.rdoLinear = new JRadioButton(Messages.getString("panel.slide.editor.fill.gradient.linear"));
		this.rdoRadial = new JRadioButton(Messages.getString("panel.slide.editor.fill.gradient.radial"));
		
		ButtonGroup bgGradientType = new ButtonGroup();
		bgGradientType.add(this.rdoLinear);
		bgGradientType.add(this.rdoRadial);
		
		this.lblDirection = new JLabel(Messages.getString("panel.slide.editor.fill.gradient.direction"));
		this.cmbLinearDirection = new JComboBox<LinearGradientDirection>(LinearGradientDirection.values());
		this.cmbLinearDirection.setRenderer(new LinearGradientDirectionListCellRenderer());
		this.cmbRadialDirection = new JComboBox<RadialGradientDirection>(RadialGradientDirection.values());
		this.cmbRadialDirection.setRenderer(new RadialGradientDirectionListCellRenderer());
		
		this.lblStopPosition = new JLabel(Messages.getString("panel.slide.editor.fill.gradient.stopPosition"));
		this.sldStopPosition = new JSlider(0, 100, 0);
		
		this.btnColor1 = new JButton(MessageFormat.format(Messages.getString("panel.slide.editor.fill.gradient.icolor"), 1));
		this.btnColor1.setActionCommand("color-1");
		this.btnColor1.addActionListener(this);
		
		this.btnColor2 = new JButton(MessageFormat.format(Messages.getString("panel.slide.editor.fill.gradient.icolor"), 2));
		this.btnColor2.setActionCommand("color-2");
		this.btnColor2.addActionListener(this);
		
		// assign defaults
		
		this.setFill(fill);
		
		// wire up events
		
		this.rdoColor.setActionCommand("type-color");
		this.rdoColor.addActionListener(this);
		this.rdoGradient.setActionCommand("type-gradient");
		this.rdoGradient.addActionListener(this);
		
		this.rdoLinear.setActionCommand("type-linear");
		this.rdoLinear.addActionListener(this);
		this.rdoRadial.setActionCommand("type-radial");
		this.rdoRadial.addActionListener(this);
		
		this.cmbLinearDirection.addItemListener(this);
		this.cmbRadialDirection.addItemListener(this);
		
		this.sldStopPosition.addChangeListener(this);
		
		// create the layout
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblFillType)
						.addComponent(this.lblColor)
						.addComponent(this.lblGradientType)
						.addComponent(this.lblDirection)
						.addComponent(this.lblStopPosition))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.rdoColor)
								.addComponent(this.rdoGradient))
						.addComponent(this.btnColor)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.rdoLinear)
								.addComponent(this.rdoRadial))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbLinearDirection)
								.addComponent(this.cmbRadialDirection))
						.addComponent(this.sldStopPosition)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnColor1)
								.addComponent(this.btnColor2))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblFillType)
						.addComponent(this.rdoColor)
						.addComponent(this.rdoGradient))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblColor)
						.addComponent(this.btnColor))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblGradientType)
						.addComponent(this.rdoLinear)
						.addComponent(this.rdoRadial))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblDirection)
						.addComponent(this.cmbLinearDirection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbRadialDirection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblStopPosition)
						.addComponent(this.sldStopPosition))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.btnColor1)
						.addComponent(this.btnColor2)));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("type-linear".equals(command)) {
			this.sldStopPosition.setValue((int)Math.floor(this.linear.getStops()[1].getFraction() * 100));
			this.cmbLinearDirection.setVisible(true);
			this.cmbRadialDirection.setVisible(false);
			this.notifyEditorListeners();
		} else if ("type-radial".equals(command)) {
			this.sldStopPosition.setValue((int)Math.floor(this.radial.getStops()[1].getFraction() * 100));
			this.cmbLinearDirection.setVisible(false);
			this.cmbRadialDirection.setVisible(true);
			this.notifyEditorListeners();
		} else if ("type-color".equals(command)) {
			this.toggleColorEditing();
			this.notifyEditorListeners();
		} else if ("type-gradient".equals(command)) {
			this.toggleGradientEditing();
			this.notifyEditorListeners();
		} else if ("color".equals(command)) {
			Color color = JColorChooser.showDialog(WindowUtilities.getParentWindow(this), "Browse", this.color.getColor());
			if (color != null) {
				this.color = new ColorFill(color);
				this.notifyEditorListeners();
			}
		} else if ("color-1".equals(command)) {
			boolean linear = this.rdoLinear.isSelected();
			// show the color chooser
			Color color = null;
			Stop[] stops = null;
			if (linear) {
				stops = this.linear.getStops();
			} else {
				stops = this.radial.getStops();
			}
			color = JColorChooser.showDialog(WindowUtilities.getParentWindow(this), "Browse", stops[0].getColor());
			if (color != null) {
				// recreate the currently selected fill
				Color other = stops[2].getColor();
				if (linear) {
					this.linear = new LinearGradientFill(
							this.linear.getDirection(), 
							new Stop(stops[0].getFraction(), color),
							new Stop(stops[1].getFraction(), ColorUtilities.getColorAtMidpoint(color, other)), 
							stops[2]);
				} else {
					this.radial = new RadialGradientFill(
							this.radial.getDirection(), 
							new Stop(stops[0].getFraction(), color),
							new Stop(stops[1].getFraction(), ColorUtilities.getColorAtMidpoint(color, other)), 
							stops[2]);
				}
				this.notifyEditorListeners();
			}
		} else if ("color-2".equals(command)) {
			boolean linear = this.rdoLinear.isSelected();
			// show the color chooser
			Color color = null;
			Stop[] stops = null;
			if (linear) {
				stops = this.linear.getStops();
			} else {
				stops = this.radial.getStops();
			}
			color = JColorChooser.showDialog(WindowUtilities.getParentWindow(this), "Browse", stops[2].getColor());
			if (color != null) {
				// recreate the currently selected fill
				Color other = stops[0].getColor();
				if (linear) {
					this.linear = new LinearGradientFill(
							this.linear.getDirection(), 
							stops[0],
							new Stop(stops[1].getFraction(), ColorUtilities.getColorAtMidpoint(other, color)), 
							new Stop(stops[2].getFraction(), color));
				} else {
					this.radial = new RadialGradientFill(
							this.radial.getDirection(), 
							stops[0],
							new Stop(stops[1].getFraction(), ColorUtilities.getColorAtMidpoint(other, color)), 
							new Stop(stops[2].getFraction(), color));
				}
				this.notifyEditorListeners();
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
			if (source == this.cmbLinearDirection) {
				LinearGradientDirection direction = (LinearGradientDirection)e.getItem();
				this.linear = new LinearGradientFill(direction, this.linear.getStops());
				this.notifyEditorListeners();
			} else if (source == this.cmbRadialDirection) {
				RadialGradientDirection direction = (RadialGradientDirection)e.getItem();
				this.radial = new RadialGradientFill(direction, this.radial.getStops());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.sldStopPosition) {
			float value = this.sldStopPosition.getValue();
			value /= 100.0f;
			
			// clamp the value
			if (value <= 0.0f) {
				value = Float.MIN_VALUE;
			} else if (value >= 1.0f) {
				value = Math.nextAfter(1.0f, 0.0f);
			}
			
			boolean linear = this.rdoLinear.isSelected();
			if (linear) {
				this.linear = new LinearGradientFill(
						this.linear.getDirection(),
						this.linear.getStops()[0],
						new Stop(value, this.linear.getStops()[1].getColor()),
						this.linear.getStops()[2]);
				this.notifyEditorListeners();
			} else {
				this.radial = new RadialGradientFill(
						this.radial.getDirection(),
						this.radial.getStops()[0],
						new Stop(value, this.radial.getStops()[1].getColor()),
						this.radial.getStops()[2]);
				this.notifyEditorListeners();
			}
		}
	}
	
	/**
	 * Shows the color editing controls.
	 */
	private void toggleColorEditing() {
		this.lblColor.setVisible(true);
		this.btnColor.setVisible(true);
		
		this.lblGradientType.setVisible(false);
		this.rdoLinear.setVisible(false);
		this.rdoRadial.setVisible(false);
		
		this.lblDirection.setVisible(false);
		this.cmbLinearDirection.setVisible(false);
		this.cmbRadialDirection.setVisible(false);
		
		this.lblStopPosition.setVisible(false);
		this.sldStopPosition.setVisible(false);
		
		this.btnColor1.setVisible(false);
		this.btnColor2.setVisible(false);
	}
	
	/**
	 * Shows the gradient editing controls.
	 */
	private void toggleGradientEditing() {
		this.lblColor.setVisible(false);
		this.btnColor.setVisible(false);
		
		this.lblGradientType.setVisible(true);
		this.rdoLinear.setVisible(true);
		this.rdoRadial.setVisible(true);
		
		this.lblDirection.setVisible(true);
		if (this.rdoLinear.isSelected()) {
			this.cmbRadialDirection.setVisible(false);
			this.cmbLinearDirection.setVisible(true);
		} else {
			this.cmbLinearDirection.setVisible(false);
			this.cmbRadialDirection.setVisible(true);
		}
		
		this.lblStopPosition.setVisible(true);
		this.sldStopPosition.setVisible(true);
		
		this.btnColor1.setVisible(true);
		this.btnColor2.setVisible(true);
	}
	
	/**
	 * Sets the fill to edit.
	 * @param fill the fill
	 */
	public void setFill(Fill fill) {
		// check for a null fill, if its null then just create a linear fill
		if (fill == null) {
			fill = new LinearGradientFill();
		}
		
		LinearGradientDirection lDirection = LinearGradientDirection.TOP;
		RadialGradientDirection rDirection = RadialGradientDirection.CENTER;
		float fraction = 0.5f;
		
		int type = 0;
		if (fill != null) {
			if (fill instanceof LinearGradientFill) {
				this.linear = (LinearGradientFill)fill;
				// determine the direction from the start point
				lDirection = this.linear.getDirection();
				rDirection = RadialGradientDirection.CENTER;
				fraction = this.linear.getStops()[1].getFraction();
				// create an equivalent radial fill
				this.radial = new RadialGradientFill();
				this.color = new ColorFill();
			} else if (fill instanceof RadialGradientFill) {
				type = 1;
				this.radial = (RadialGradientFill)fill;
				// determine the direction from the center point
				rDirection = this.radial.getDirection();
				lDirection = LinearGradientDirection.TOP;
				fraction = this.radial.getStops()[1].getFraction();
				// create an equivalent linear fill
				this.linear = new LinearGradientFill();
				this.color = new ColorFill();
			} else if (fill instanceof ColorFill) {
				type = 2;
				this.color = (ColorFill)fill;
				lDirection = LinearGradientDirection.TOP;
				rDirection = RadialGradientDirection.CENTER;
				this.linear = new LinearGradientFill();
				this.radial = new RadialGradientFill();
			}
		}
		
		if (type == 0) {
			this.rdoColor.setSelected(false);
			this.rdoGradient.setSelected(true);
			this.rdoLinear.setSelected(true);
			this.rdoRadial.setSelected(false);
			
			this.toggleGradientEditing();
		} else if (type == 1) {
			this.rdoColor.setSelected(false);
			this.rdoGradient.setSelected(true);
			this.rdoLinear.setSelected(false);
			this.rdoRadial.setSelected(true);
			
			this.toggleGradientEditing();
		} else {
			this.rdoColor.setSelected(true);
			this.rdoGradient.setSelected(false);
			this.rdoLinear.setSelected(true);
			this.rdoRadial.setSelected(false);
			
			this.toggleColorEditing();
		}
		this.cmbLinearDirection.setSelectedItem(lDirection);
		this.cmbRadialDirection.setSelectedItem(rDirection);
		this.sldStopPosition.setValue((int)Math.floor(fraction * 100));
	}
	
	/**
	 * Returns the fill configured by the use for the given width and height.
	 * @return Fill
	 */
	public Fill getFill() {
		// return the color
		if (this.rdoColor.isSelected()) {
			return this.color;
		}
		// return the right gradient
		if (this.rdoLinear.isSelected()) {
			return this.linear;
		} else {
			return this.radial;
		}
	}
}
