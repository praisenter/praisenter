package org.praisenter.slide.ui.editor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.images.Images;
import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.ComponentUtilities;
import org.praisenter.utilities.ImageUtilities;

// TODO we need to support multi-stop gradients (multi-thumb slider would be best)
// cycle method, focal point (radial only) aren't really needed since we are abstracting away
// most of the details of the gradient selection. This could change when we do multi-stop
// gradients

// FIXME translate
/**
 * Editor panel for MultipleGradientPaint objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class PaintEditorPanel extends JPanel implements ActionListener, ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -4080895520033411798L;

	/** The linear gradient direction card name */
	private static final String LINEAR_DIRECTION_CARD = "Linear";
	
	/** The radial gradient direction card name */
	private static final String RADIAL_DIRECTION_CARD = "Radial";

	/** The color card name */
	private static final String COLOR_CARD = "Color";
	
	/** The gradient card name */
	private static final String GRADIENT_CARD = "Gradient";
	
	// controls
	
	// paint type
	
	/** The color type radio button */
	private JRadioButton rdoColor;
	
	/** The gradient type radio button */
	private JRadioButton rdoGradient;
	
	/** The paint type cards */
	private JPanel pnlPaintCards;
	
	// gradient type
	
	/** The linear gradient type radio button */
	private JRadioButton rdoLinear;
	
	/** The radial gradient type radio button */
	private JRadioButton rdoRadial;
	
	// gradient direction
	
	/** The linear gradient direction combo box */
	private JComboBox<LinearGradientDirection> cmbLinearDirection;
	
	/** The radial gradient direction combo box */
	private JComboBox<RadialGradientDirection> cmbRadialDirection;
	
	/** The linear/radial card layout panel */
	private JPanel pnlDirectionCards;
	
	// stops (fractions)
	
	/** The midpoint stop position of the gradient */
	private JSlider sldStopPosition;
	
	// preview
	
	/** The gradient preview panel */
	private JPanel pnlPreview;
	
	/** The gradient preview panel width */
	private static final int PREVIEW_WIDTH = 300;
	
	/** The gradient preview panel height */
	private static final int PREVIEW_HEIGHT = 100;
	
	/** Tiled transparent background for the preview panel */
	private BufferedImage background = null;
	
	// the paints
	
	/** The color */
	private Color color;
	
	/** The configured linear gradient paint */
	private LinearGradientPaint linearPaint;
	
	/** The configured radial gradient paint */
	private RadialGradientPaint radialPaint;
	
	/**
	 * Full constructor.
	 * @param paint the initial paint
	 */
	@SuppressWarnings("serial")
	public PaintEditorPanel(Paint paint) {
		// check for a null paint, if its null then just create a linear paint
		if (paint == null) {
			paint = new LinearGradientPaint(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { Color.WHITE, getColorAtMidpoint(Color.WHITE, Color.BLUE), Color.BLUE });
		}
		
		Dimension size = new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		this.pnlPreview = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D)g;
				Dimension size = this.getSize();
				
				if (background == null) {
					background = ImageUtilities.getTiledImage(Images.TRANSPARENT_BACKGROUND, g2d.getDeviceConfiguration(), size.width, size.height);
				}
				
				g2d.drawImage(background, 0, 0, null);
				
				if (rdoColor.isSelected()) {
					g2d.setPaint(color);
				} else {
					if (rdoLinear.isSelected()) {
						g2d.setPaint(linearPaint);
					} else {
						g2d.setPaint(radialPaint);
					}
				}
				g2d.fillRect(0, 0, size.width, size.height);
			}
		};
		this.pnlPreview.setMinimumSize(size);
		this.pnlPreview.setPreferredSize(size);
		
		LinearGradientDirection lDirection = LinearGradientDirection.TOP_LEFT;
		RadialGradientDirection rDirection = RadialGradientDirection.CENTER;
		
		boolean color = true;
		boolean linear = false;
		if (paint != null) {
			if (paint instanceof LinearGradientPaint) {
				color = false;
				linear = true;
				this.linearPaint = (LinearGradientPaint)paint;
				// determine the direction from the start point
				Point2D s = this.linearPaint.getStartPoint();
				Point2D e = this.linearPaint.getEndPoint();
				int sx = (int)s.getX();
				int sy = (int)s.getY();
				int ex = (int)e.getX();
				int ey = (int)e.getY();
				rDirection = RadialGradientDirection.CENTER;
				if (sx == ex && sy != ey) {
					if (sy < ey) {
						lDirection = LinearGradientDirection.TOP;
					} else {
						lDirection = LinearGradientDirection.BOTTOM;
					}
				} else if (sx != ex && sy != ey) {
					if (sx < ex && sy < ey) {
						lDirection = LinearGradientDirection.TOP_LEFT;
						rDirection = RadialGradientDirection.TOP_LEFT;
					} else if (sx > ex && sy > ey) {
						lDirection = LinearGradientDirection.BOTTOM_RIGHT;
						rDirection = RadialGradientDirection.BOTTOM_RIGHT;
					} else if (sx < ex && sy > ey) {
						lDirection = LinearGradientDirection.BOTTOM_LEFT;
						rDirection = RadialGradientDirection.BOTTOM_LEFT;
					} else {
						lDirection = LinearGradientDirection.TOP_RIGHT;
						rDirection = RadialGradientDirection.TOP_RIGHT;
					}
				} else { //if (sx != ex && sy == ey) {
					if (sx < ex) {
						lDirection = LinearGradientDirection.LEFT;
					} else {
						lDirection = LinearGradientDirection.RIGHT;
					}
				}
				// create an equivalent radial paint
				float radius = (float)Math.hypot(PREVIEW_WIDTH, PREVIEW_HEIGHT);
				this.radialPaint = new RadialGradientPaint(s, radius, this.linearPaint.getFractions(), this.linearPaint.getColors());
				this.color = this.linearPaint.getColors()[0];
			} else if (paint instanceof RadialGradientPaint) {
				this.radialPaint = (RadialGradientPaint)paint;
				color = false;
				linear = false;
				// determine the direction from the center point
				Point2D point = this.radialPaint.getCenterPoint();
				float radius = this.radialPaint.getRadius();
				int x = (int)point.getX();
				int y = (int)point.getY();
				float ex = x;
				float ey = y;
				if (x == 0 && y == 0) {
					rDirection = RadialGradientDirection.TOP_LEFT;
					lDirection = LinearGradientDirection.TOP_LEFT;
					ex += radius;
					ey += radius;
				} else if (x == 0 && y >= radius) {
					rDirection = RadialGradientDirection.BOTTOM_LEFT;
					lDirection = LinearGradientDirection.BOTTOM_LEFT;
					ex += radius;
					ey -= radius;
				} else if (x >= radius && y == 0) {
					rDirection = RadialGradientDirection.TOP_RIGHT;
					lDirection = LinearGradientDirection.TOP_RIGHT;
					ex -= radius;
					ey += radius;
				} else if (x >= radius && y >= radius) {
					rDirection = RadialGradientDirection.BOTTOM_RIGHT;
					lDirection = LinearGradientDirection.BOTTOM_RIGHT;
					ex -= radius;
					ey -= radius;
				} else {
					rDirection = RadialGradientDirection.CENTER;
					lDirection = LinearGradientDirection.LEFT;
					x = 0;
					y = 0;
					ex = radius;
					ey = 0;
				}
				// create an equivalent linear paint
				this.linearPaint = new LinearGradientPaint(x, y, ex, ey, this.radialPaint.getFractions(), this.radialPaint.getColors());
				this.color = this.radialPaint.getColors()[0];
			} else if (paint instanceof Color) {
				linear = false;
				color = true;
				this.color = (Color)paint;
				Color alternate = ColorUtilities.getForegroundColorFromBackgroundColor(this.color);
				float[] fractions = new float[] { 0.0f, 0.5f, 1.0f };
				Color[] colors = new Color[] { this.color, this.getColorAtMidpoint(this.color, alternate), alternate };
				this.linearPaint = new LinearGradientPaint(0, 0, PREVIEW_WIDTH, 0, fractions, colors);
				this.radialPaint = new RadialGradientPaint(0, 0, (float)Math.hypot(PREVIEW_WIDTH, PREVIEW_HEIGHT), fractions, colors);
			}
		}
		
		JLabel lblPaintType = new JLabel("Type");
		this.rdoColor = new JRadioButton("Color");
		this.rdoGradient = new JRadioButton("Gradient");
		
		this.rdoColor.setSelected(color);
		this.rdoGradient.setSelected(!color);
		
		ButtonGroup bgPaintType = new ButtonGroup();
		bgPaintType.add(this.rdoColor);
		bgPaintType.add(this.rdoGradient);
		
		JLabel lblColor = new JLabel("Color");
		JButton btnColor = new JButton("Color");
		btnColor.addActionListener(this);
		btnColor.setActionCommand("color");
		
		JPanel pnlColor = new JPanel();
		GroupLayout cLayout = new GroupLayout(pnlColor);
		pnlColor.setLayout(cLayout);
		
		cLayout.setAutoCreateGaps(true);
		cLayout.setHorizontalGroup(cLayout.createSequentialGroup()
				.addComponent(lblColor)
				.addComponent(btnColor));
		cLayout.setVerticalGroup(cLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblColor)
				.addComponent(btnColor));
		
		JLabel lblGradientType = new JLabel("Gradient Type");
		this.rdoLinear = new JRadioButton("Linear");
		this.rdoRadial = new JRadioButton("Radial");
		
		ButtonGroup bgType = new ButtonGroup();
		bgType.add(this.rdoLinear);
		bgType.add(this.rdoRadial);
		
		this.rdoLinear.setSelected(linear);
		this.rdoRadial.setSelected(!linear);
		
		JLabel lblDirection = new JLabel("Direction");
		this.cmbLinearDirection = new JComboBox<LinearGradientDirection>(LinearGradientDirection.values());
		this.cmbLinearDirection.setSelectedItem(lDirection);
		
		this.cmbRadialDirection = new JComboBox<RadialGradientDirection>(RadialGradientDirection.values());
		this.cmbRadialDirection.setSelectedItem(rDirection);
		
		this.pnlDirectionCards = new JPanel();
		this.pnlDirectionCards.setLayout(new CardLayout());
		this.pnlDirectionCards.add(this.cmbLinearDirection, LINEAR_DIRECTION_CARD);
		this.pnlDirectionCards.add(this.cmbRadialDirection, RADIAL_DIRECTION_CARD);
		((CardLayout)this.pnlDirectionCards.getLayout()).show(this.pnlDirectionCards, LINEAR_DIRECTION_CARD);
		
		JLabel lblStopPosition = new JLabel("Position");
		this.sldStopPosition = new JSlider(0, 100, (int)Math.floor((linear ? this.linearPaint.getFractions()[1] : this.radialPaint.getFractions()[1]) * 100));
		
		JButton btnColor1 = new JButton("Color 1");
		btnColor1.setActionCommand("color-1");
		btnColor1.addActionListener(this);
		
		JButton btnColor2 = new JButton("Color 2");
		btnColor2.setActionCommand("color-2");
		btnColor2.addActionListener(this);
		
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
		JPanel pnlGradient = new JPanel();
		GroupLayout gLayout = new GroupLayout(pnlGradient);
		pnlGradient.setLayout(gLayout);
		
		gLayout.setAutoCreateGaps(true);
		gLayout.setHorizontalGroup(gLayout.createSequentialGroup()
				.addGroup(gLayout.createParallelGroup()
						.addComponent(lblGradientType)
						.addComponent(lblDirection)
						.addComponent(lblStopPosition))
				.addGroup(gLayout.createParallelGroup()
						.addGroup(gLayout.createSequentialGroup()
								.addComponent(this.rdoLinear)
								.addComponent(this.rdoRadial))
						.addComponent(this.pnlDirectionCards)
						.addComponent(this.sldStopPosition)
						.addGroup(gLayout.createSequentialGroup()
								.addComponent(btnColor1)
								.addComponent(btnColor2))));
		gLayout.setVerticalGroup(gLayout.createSequentialGroup()
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblGradientType)
						.addComponent(this.rdoLinear)
						.addComponent(this.rdoRadial))
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblDirection)
						.addComponent(this.pnlDirectionCards, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblStopPosition)
						.addComponent(this.sldStopPosition))
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnColor1)
						.addComponent(btnColor2)));
		
		CardLayout cpLayout = new CardLayout();
		this.pnlPaintCards = new JPanel();
		this.pnlPaintCards.setLayout(cpLayout);
		this.pnlPaintCards.add(pnlColor, COLOR_CARD);
		this.pnlPaintCards.add(pnlGradient, GRADIENT_CARD);
		if (color) {
			cpLayout.show(this.pnlPaintCards, COLOR_CARD);
		} else {
			cpLayout.show(this.pnlPaintCards, GRADIENT_CARD);
		}
		
		ComponentUtilities.setMinimumSize(lblColor, lblDirection, lblGradientType, lblPaintType, lblStopPosition);
		
		JSeparator sep = new JSeparator();
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(lblPaintType)
						.addComponent(this.rdoColor)
						.addComponent(this.rdoGradient))
				.addComponent(sep)
				.addComponent(this.pnlPaintCards)
				.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPaintType)
						.addComponent(this.rdoColor)
						.addComponent(this.rdoGradient))
				.addComponent(sep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlPaintCards, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("type-linear".equals(command)) {
			((CardLayout)this.pnlDirectionCards.getLayout()).show(this.pnlDirectionCards, LINEAR_DIRECTION_CARD);
			this.sldStopPosition.setValue((int)Math.floor(this.linearPaint.getFractions()[1] * 100));
			this.pnlPreview.repaint();
		} else if ("type-radial".equals(command)) {
			((CardLayout)this.pnlDirectionCards.getLayout()).show(this.pnlDirectionCards, RADIAL_DIRECTION_CARD);
			this.sldStopPosition.setValue((int)Math.floor(this.radialPaint.getFractions()[1] * 100));
			this.pnlPreview.repaint();
		} else if ("type-color".equals(command)) {
			((CardLayout)this.pnlPaintCards.getLayout()).show(this.pnlPaintCards, COLOR_CARD);
			this.pnlPreview.repaint();
		} else if ("type-gradient".equals(command)) {
			((CardLayout)this.pnlPaintCards.getLayout()).show(this.pnlPaintCards, GRADIENT_CARD);
			this.pnlPreview.repaint();
		} else if ("color".equals(command)) {
			Color color = JColorChooser.showDialog(this, "Browse", this.color);
			if (color != null) {
				this.color = color;
				this.pnlPreview.repaint();
			}
		} else if ("color-1".equals(command)) {
			boolean linear = this.rdoLinear.isSelected();
			// show the color chooser
			Color color = JColorChooser.showDialog(this, "Browse", linear ? this.linearPaint.getColors()[0] : this.radialPaint.getColors()[0]);
			if (color != null) {
				// recreate the currently selected paint
				if (linear) {
					Color other = this.linearPaint.getColors()[2];
					this.linearPaint = new LinearGradientPaint(
							this.linearPaint.getStartPoint(),
							this.linearPaint.getEndPoint(),
							this.linearPaint.getFractions(),
							new Color[] { color, this.getColorAtMidpoint(color, other), other });
				} else {
					Color other = this.radialPaint.getColors()[2];
					this.radialPaint = new RadialGradientPaint(
							this.radialPaint.getCenterPoint(),
							this.radialPaint.getRadius(),
							this.radialPaint.getFractions(),
							new Color[] { color, this.getColorAtMidpoint(color, other), other });
				}
				this.pnlPreview.repaint();
			}
		} else if ("color-2".equals(command)) {
			boolean linear = this.rdoLinear.isSelected();
			// show the color chooser
			Color color = JColorChooser.showDialog(this, "Browse", linear ? this.linearPaint.getColors()[1] : this.radialPaint.getColors()[1]);
			if (color != null) {
				// recreate the currently selected paint
				if (linear) {
					Color other = this.linearPaint.getColors()[0];
					this.linearPaint = new LinearGradientPaint(
							this.linearPaint.getStartPoint(),
							this.linearPaint.getEndPoint(),
							this.linearPaint.getFractions(),
							new Color[] { other, this.getColorAtMidpoint(other, color), color });
				} else {
					Color other = this.radialPaint.getColors()[0];
					this.radialPaint = new RadialGradientPaint(
							this.radialPaint.getCenterPoint(),
							this.radialPaint.getRadius(),
							this.radialPaint.getFractions(),
							new Color[] { other, this.getColorAtMidpoint(other, color), color });
				}
				this.pnlPreview.repaint();	
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
				float sx, sy, ex, ey;
				if (direction == LinearGradientDirection.BOTTOM) {
					sx = 0; sy = PREVIEW_HEIGHT;
					ex = 0; ey = 0;
				} else if (direction == LinearGradientDirection.BOTTOM_LEFT) {
					sx = 0; sy = PREVIEW_HEIGHT;
					ex = PREVIEW_WIDTH; ey = 0;
				} else if (direction == LinearGradientDirection.BOTTOM_RIGHT) {
					sx = PREVIEW_WIDTH; sy = PREVIEW_HEIGHT;
					ex = 0; ey = 0;
				} else if (direction == LinearGradientDirection.LEFT) {
					sx = 0; sy = 0;
					ex = PREVIEW_WIDTH; ey = 0;
				} else if (direction == LinearGradientDirection.RIGHT) {
					sx = PREVIEW_WIDTH; sy = 0;
					ex = 0; ey = 0;
				} else if (direction == LinearGradientDirection.TOP) {
					sx = 0; sy = 0;
					ex = 0; ey = PREVIEW_HEIGHT;
				} else if (direction == LinearGradientDirection.TOP_LEFT) {
					sx = 0; sy = 0;
					ex = PREVIEW_WIDTH; ey = PREVIEW_HEIGHT;
				} else { //if (direction == LinearGradientDirection.TOP_RIGHT) {
					sx = PREVIEW_WIDTH; sy = 0;
					ex = 0; ey = PREVIEW_HEIGHT;
				}
				this.linearPaint = new LinearGradientPaint(
						sx, sy, ex, ey,
						this.linearPaint.getFractions(),
						this.linearPaint.getColors());
				this.pnlPreview.repaint();
			} else if (source == this.cmbRadialDirection) {
				RadialGradientDirection direction = (RadialGradientDirection)e.getItem();
				float x, y, r = (float)Math.hypot(PREVIEW_WIDTH, PREVIEW_HEIGHT);
				if (direction == RadialGradientDirection.BOTTOM_LEFT) {
					x = 0; y = PREVIEW_HEIGHT;
				} else if (direction == RadialGradientDirection.BOTTOM_RIGHT) {
					x = PREVIEW_WIDTH; y = PREVIEW_HEIGHT;
				} else if (direction == RadialGradientDirection.CENTER) {
					x = PREVIEW_WIDTH * 0.5f;
					y = PREVIEW_HEIGHT * 0.5f;
					r = r * 0.5f;
				} else if (direction == RadialGradientDirection.TOP_LEFT) {
					x = 0; y = 0;
				} else { //if (direction == RadialGradientDirection.TOP_RIGHT) {
					x = PREVIEW_WIDTH; y = 0;
				}
				this.radialPaint = new RadialGradientPaint(
						x, y,
						r,
						this.radialPaint.getFractions(),
						this.radialPaint.getColors());
				this.pnlPreview.repaint();
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
				value = 0.0000001f;
			} else if (value >= 1.0f) {
				value = 0.9999999f;
			}
			
			boolean linear = this.rdoLinear.isSelected();
			if (linear) {
				this.linearPaint = new LinearGradientPaint(
						this.linearPaint.getStartPoint(),
						this.linearPaint.getEndPoint(),
						new float[] { 0.0f, value, 1.0f },
						this.linearPaint.getColors());
				this.pnlPreview.repaint();
			} else {
				this.radialPaint = new RadialGradientPaint(
						this.radialPaint.getCenterPoint(),
						this.radialPaint.getRadius(),
						new float[] { 0.0f, value, 1.0f },
						this.radialPaint.getColors());
				this.pnlPreview.repaint();
			}
		}
	}
	
	/**
	 * Returns the color at the mid point of the given colors.
	 * @param c1 the first color
	 * @param c2 the second color
	 * @return Color
	 */
	protected Color getColorAtMidpoint(Color c1, Color c2) {
		int r = (c2.getRed() - c1.getRed()) / 2 + c1.getRed();
		int g = (c2.getGreen() - c1.getGreen()) / 2 + c1.getGreen();
		int b = (c2.getBlue() - c1.getBlue()) / 2 + c1.getBlue();
		int a = (c2.getAlpha() - c1.getAlpha()) / 2 + c1.getAlpha();
		return new Color(r, g, b, a);
	}
	
	/**
	 * Returns the gradient paint configured by the user for the given width and height.
	 * @param width the width
	 * @param height the height
	 * @return Paint
	 */
	public Paint getPaint(int width, int height) {
		// return the color
		if (this.rdoColor.isSelected()) {
			return this.color;
		}
		// return the right gradient
		if (this.rdoLinear.isSelected()) {
			// we need to update the start/end points so that they match the
			// target width and height
			LinearGradientDirection direction = (LinearGradientDirection)this.cmbLinearDirection.getSelectedItem();
			float sx, sy, ex, ey;
			if (direction == LinearGradientDirection.BOTTOM) {
				sx = 0; sy = height;
				ex = 0; ey = 0;
			} else if (direction == LinearGradientDirection.BOTTOM_LEFT) {
				sx = 0; sy = height;
				ex = width; ey = 0;
			} else if (direction == LinearGradientDirection.BOTTOM_RIGHT) {
				sx = width; sy = height;
				ex = 0; ey = 0;
			} else if (direction == LinearGradientDirection.LEFT) {
				sx = 0; sy = 0;
				ex = width; ey = 0;
			} else if (direction == LinearGradientDirection.RIGHT) {
				sx = width; sy = 0;
				ex = 0; ey = 0;
			} else if (direction == LinearGradientDirection.TOP) {
				sx = 0; sy = 0;
				ex = 0; ey = height;
			} else if (direction == LinearGradientDirection.TOP_LEFT) {
				sx = 0; sy = 0;
				ex = width; ey = height;
			} else { //if (direction == LinearGradientDirection.TOP_RIGHT) {
				sx = width; sy = 0;
				ex = 0; ey = height;
			}
			return new LinearGradientPaint(
					sx, sy,
					ex, ey,
					this.linearPaint.getFractions(),
					this.linearPaint.getColors());
		} else {
			// we need to update the center point and radius so that they match the
			// target width and height
			RadialGradientDirection direction = (RadialGradientDirection)this.cmbRadialDirection.getSelectedItem();
			float x, y, r = (float)Math.hypot(PREVIEW_WIDTH, PREVIEW_HEIGHT);
			if (direction == RadialGradientDirection.BOTTOM_LEFT) {
				x = 0; y = PREVIEW_HEIGHT;
			} else if (direction == RadialGradientDirection.BOTTOM_RIGHT) {
				x = PREVIEW_WIDTH; y = PREVIEW_HEIGHT;
			} else if (direction == RadialGradientDirection.CENTER) {
				x = PREVIEW_WIDTH * 0.5f;
				y = PREVIEW_HEIGHT * 0.5f;
				r = r * 0.5f;
			} else if (direction == RadialGradientDirection.TOP_LEFT) {
				x = 0; y = 0;
			} else { //if (direction == RadialGradientDirection.TOP_RIGHT) {
				x = PREVIEW_WIDTH; y = 0;
			}
			return new RadialGradientPaint(
					x, y,
					r,
					this.radialPaint.getFractions(),
					this.radialPaint.getColors());
		}
	}
}
