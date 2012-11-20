package org.praisenter.slide.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

import org.praisenter.preferences.ui.DisplaySettingsPanel;
import org.praisenter.resources.Messages;

public class GradientEditorPanel extends JPanel implements ActionListener {
	
	// TODO we need to support multi-stop gradients (multi-thumb slider would be best)
	// cycle method, focal point (radial only) aren't really needed since we are abstracting away
	// most of the details of the gradient selection. This could change when we do multi-stop
	// gradients
	
	private static final String LINEAR_DIRECTION_CARD = "Linear";
	private static final String RADIAL_DIRECTION_CARD = "Radial";
	
	private static enum GradientType {
		LINEAR,
		RADIAL
	}
	
	private static final class Stop {
		float fraction;
		Color color;
	}
	
	private static enum LinearGradientDirection {
		TOP,
		RIGHT,
		BOTTOM,
		LEFT,
		// corners
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
	
	private static enum RadialGradientDirection {
		CENTER,
		// corners
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
	
	// gradient type
	
	private JRadioButton rdoLinear;
	private JRadioButton rdoRadial;
	
	// gradient direction
	
	private JComboBox<LinearGradientDirection> cmbLinearDirection;
	private JComboBox<RadialGradientDirection> cmbRadialDirection;
	private JPanel pnlDirectionCards;
	
	// stops (fractions)
	
	private JSlider sldStopPosition;
	
	// preview
	
	private JPanel pnlPreview;
	
	// the paints
	
	private LinearGradientPaint linearPaint;
	private RadialGradientPaint radialPaint;
	
	public GradientEditorPanel(Paint paint, int w, int h) {
		
		this.linearPaint = new LinearGradientPaint(0, 0, w, h, new float[] { 0.5f }, new Color[] { Color.WHITE, Color.BLUE });
		this.radialPaint = new RadialGradientPaint(w / 2, h / 2, w > h ? w : h, new float[] { 0.5f }, new Color[] { Color.WHITE, Color.BLUE });
		
		LinearGradientDirection lDirection = LinearGradientDirection.TOP_LEFT;
		RadialGradientDirection rDirection = RadialGradientDirection.CENTER;
		
		boolean linear = true;
		if (paint != null) {
			if (paint instanceof LinearGradientPaint) {
				this.linearPaint = (LinearGradientPaint)paint;
				// determine the direction from the start point
				Point2D point = this.linearPaint.getStartPoint();
				int x = (int)point.getX();
				int y = (int)point.getY();
				if (x == 0 && y == 0) {
					lDirection = LinearGradientDirection.TOP_LEFT;
				} else if (x == 0 && y == h) {
					lDirection = LinearGradientDirection.BOTTOM_LEFT;
				} else if (x == w && y == 0) {
					lDirection = LinearGradientDirection.TOP_RIGHT;
				} else if (x == w && y == h) {
					lDirection = LinearGradientDirection.BOTTOM_RIGHT;
				} else if (x == 0) {
					lDirection = LinearGradientDirection.LEFT;
				} else if (x == w) {
					lDirection = LinearGradientDirection.RIGHT;
				} else if (y == 0) {
					lDirection = LinearGradientDirection.TOP;
				} else if (y == h) {
					lDirection = LinearGradientDirection.BOTTOM;
				}
			} else if (paint instanceof RadialGradientPaint) {
				this.radialPaint = (RadialGradientPaint)paint;
				linear = false;
				// determine the direction from the center point
				Point2D point = this.radialPaint.getCenterPoint();
				int x = (int)point.getX();
				int y = (int)point.getY();
				if (x == 0 && y == 0) {
					rDirection = RadialGradientDirection.TOP_LEFT;
				} else if (x == 0 && y == h) {
					rDirection = RadialGradientDirection.BOTTOM_LEFT;
				} else if (x == w && y == 0) {
					rDirection = RadialGradientDirection.TOP_RIGHT;
				} else if (x == w && y == h) {
					rDirection = RadialGradientDirection.BOTTOM_RIGHT;
				} else if (x == w) {
					rDirection = RadialGradientDirection.CENTER;
				}
			}
		}
		
		this.rdoLinear = new JRadioButton("Linear");
		this.rdoRadial = new JRadioButton("Radial");
		
		ButtonGroup bgType = new ButtonGroup();
		bgType.add(this.rdoLinear);
		bgType.add(this.rdoRadial);
		
		this.rdoLinear.setSelected(linear);
		this.rdoRadial.setSelected(!linear);
		
		this.cmbLinearDirection = new JComboBox<LinearGradientDirection>(LinearGradientDirection.values());
		this.cmbLinearDirection.setSelectedItem(lDirection);
		
		this.cmbRadialDirection = new JComboBox<RadialGradientDirection>(RadialGradientDirection.values());
		this.cmbRadialDirection.setSelectedItem(rDirection);
		
		this.pnlDirectionCards = new JPanel();
		this.pnlDirectionCards.setLayout(new CardLayout());
		this.pnlDirectionCards.add(this.cmbLinearDirection, LINEAR_DIRECTION_CARD);
		this.pnlDirectionCards.add(this.cmbRadialDirection, RADIAL_DIRECTION_CARD);
		((CardLayout)this.pnlDirectionCards.getLayout()).show(this.pnlDirectionCards, LINEAR_DIRECTION_CARD);
		
		this.sldStopPosition = new JSlider(0, 100, (int)Math.floor(linear ? this.linearPaint.getFractions()[0] : this.radialPaint.getFractions()[0]));
		
		JButton btnColor1 = new JButton("Color 1");
		btnColor1.setActionCommand("color-1");
		btnColor1.addActionListener(this);
		
		JButton btnColor2 = new JButton("Color 2");
		btnColor2.setActionCommand("color-2");
		btnColor2.addActionListener(this);
		
		// wire up events
		
		this.rdoLinear.setActionCommand("type-linear");
		this.rdoLinear.addActionListener(this);
		this.rdoRadial.setActionCommand("type-radial");
		this.rdoRadial.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("type-linear".equals(command)) {
			((CardLayout)this.pnlDirectionCards.getLayout()).show(this.pnlDirectionCards, LINEAR_DIRECTION_CARD);
		} else if ("type-radial".equals(command)) {
			((CardLayout)this.pnlDirectionCards.getLayout()).show(this.pnlDirectionCards, RADIAL_DIRECTION_CARD);
		} else if ("color-1".equals(command)) {
			boolean linear = this.rdoLinear.isSelected();
			// TODO show color selector
//			Color old = this.component.getBackgroundColor();
			// show the color chooser
			Color color = JColorChooser.showDialog(this, "Browse", linear ? this.linearPaint.getColors()[0] : this.radialPaint.getColors()[0]);
			if (color != null) {
				if (linear) {
					this.linearPaint = new LinearGradientPaint(
							this.linearPaint.getStartPoint(),
							this.linearPaint.getEndPoint(),
							this.linearPaint.getFractions(),
							new Color[] { color, this.linearPaint.getColors()[1] });
				} else {
					this.radialPaint = new RadialGradientPaint(
							this.radialPaint.getCenterPoint(),
							this.radialPaint.getRadius(),
							this.radialPaint.getFractions(),
							new Color[] { color, this.radialPaint.getColors()[1] });
				}
				// recreate the currently selected paint
//				this.firePropertyChange(DisplaySettingsPanel.DISPLAY_COMPONENT_PROPERTY, old, color);
			}
		} else if ("color-2".equals(command)) {
			// TODO show color selector			
		}
	}
	
	public Paint getPaint() {
		if (this.rdoLinear.isSelected()) {
			return this.linearPaint;
		} else {
			return this.radialPaint;
		}
	}
}
