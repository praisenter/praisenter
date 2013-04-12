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
package org.praisenter.application.slide.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.application.resources.Messages;
import org.praisenter.common.utilities.ColorUtilities;
import org.praisenter.common.utilities.ImageUtilities;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LinearGradientDirection;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.RadialGradientDirection;
import org.praisenter.slide.graphics.RadialGradientFill;
import org.praisenter.slide.graphics.Stop;

/**
 * Editor panel for MultipleGradientPaint objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class FillEditorPanel extends JPanel implements ActionListener, ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -4080895520033411798L;

	/** The transparent background */
	private static final BufferedImage TRANSPARENT_BACKGROUND = ImageUtilities.getImageFromClassPathSuppressExceptions("/org/praisenter/application/resources/transparent.png");
	
	// controls
	
	/** The fill type tabs */
	private JTabbedPane tabs;
	
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
	
	// preview
	
	/** The preview panel */
	private JPanel pnlPreview;
	
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
	@SuppressWarnings("serial")
	public FillEditorPanel(Fill fill) {
		// check for a null fill, if its null then just create a linear fill
		if (fill == null) {
			fill = new LinearGradientFill();
		}
		
		// color
		
		this.lblColor = new JLabel(Messages.getString("panel.slide.editor.fill.color"));
		this.btnColor = new JButton(Messages.getString("panel.slide.editor.fill.chooseColor"));
		this.btnColor.addActionListener(this);
		this.btnColor.setActionCommand("color");
		
		JPanel pnlColor = new JPanel();
		GroupLayout cLayout = new GroupLayout(pnlColor);
		pnlColor.setLayout(cLayout);
		
		cLayout.setAutoCreateContainerGaps(true);
		cLayout.setAutoCreateGaps(true);
		cLayout.setHorizontalGroup(cLayout.createSequentialGroup()
				.addComponent(this.lblColor)
				.addComponent(this.btnColor));
		cLayout.setVerticalGroup(cLayout.createParallelGroup()
				.addComponent(this.lblColor)
				.addComponent(this.btnColor));
		
		// gradient
		
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
		
		JPanel pnlGradient = new JPanel();
		GroupLayout gLayout = new GroupLayout(pnlGradient);
		pnlGradient.setLayout(gLayout);
		
		gLayout.setAutoCreateContainerGaps(true);
		gLayout.setAutoCreateGaps(true);
		gLayout.setHorizontalGroup(gLayout.createSequentialGroup()
				.addGroup(gLayout.createParallelGroup()
						.addComponent(this.lblGradientType)
						.addComponent(this.lblDirection)
						.addComponent(this.lblStopPosition))
				.addGroup(gLayout.createParallelGroup()
						.addGroup(gLayout.createSequentialGroup()
								.addComponent(this.rdoLinear)
								.addComponent(this.rdoRadial))
						.addGroup(gLayout.createSequentialGroup()
								.addComponent(this.cmbLinearDirection)
								.addComponent(this.cmbRadialDirection))
						.addComponent(this.sldStopPosition)
						.addGroup(gLayout.createSequentialGroup()
								.addComponent(this.btnColor1)
								.addComponent(this.btnColor2))));
		gLayout.setVerticalGroup(gLayout.createSequentialGroup()
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblGradientType)
						.addComponent(this.rdoLinear)
						.addComponent(this.rdoRadial))
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblDirection)
						.addComponent(this.cmbLinearDirection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbRadialDirection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblStopPosition)
						.addComponent(this.sldStopPosition))
				.addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.btnColor1)
						.addComponent(this.btnColor2)));
		
		// preview
		final int p = 5;
		
		this.pnlPreview = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				int w = this.getWidth() - 2 * p;
				int h = this.getHeight() - 2 * p;
				// paint a transparent background
				ImageUtilities.renderTiledImage(TRANSPARENT_BACKGROUND, g, p, p, w, h);
				// paint the background using the current fill
				Fill fill = getFill();
				if (fill != null) {
					Graphics2D g2d = (Graphics2D)g;
					Paint oPaint = g2d.getPaint();
					
					Paint paint = fill.getPaint(p, p, w, h);
					g2d.setPaint(paint);
					g2d.fillRect(p, p, w, h);
					
					g2d.setPaint(oPaint);
				}
			}
		};
		this.pnlPreview.setMinimumSize(new Dimension(0, 25));
		this.pnlPreview.setPreferredSize(new Dimension(100, 150));
		this.pnlPreview.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(p, p, p, p), 
				BorderFactory.createLineBorder(Color.DARK_GRAY)));
		
		// create the layout
		this.tabs = new JTabbedPane();
		this.tabs.addTab(Messages.getString("panel.slide.editor.fill.color"), pnlColor);
		this.tabs.addTab(Messages.getString("panel.slide.editor.fill.gradient"), pnlGradient);
		this.tabs.addChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.tabs)
				.addComponent(this.pnlPreview));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.tabs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlPreview));
		
		// assign defaults
		
		this.setFill(fill);
		
		// wire up events
		
		this.rdoLinear.setActionCommand("type-linear");
		this.rdoLinear.addActionListener(this);
		this.rdoRadial.setActionCommand("type-radial");
		this.rdoRadial.addActionListener(this);
		
		this.cmbLinearDirection.addItemListener(this);
		this.cmbRadialDirection.addItemListener(this);
		
		this.sldStopPosition.addChangeListener(this);
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
			this.pnlPreview.repaint();
		} else if ("type-radial".equals(command)) {
			this.sldStopPosition.setValue((int)Math.floor(this.radial.getStops()[1].getFraction() * 100));
			this.cmbLinearDirection.setVisible(false);
			this.cmbRadialDirection.setVisible(true);
			this.pnlPreview.repaint();
		} else if ("color".equals(command)) {
			Color color = JColorChooser.showDialog(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.editor.fill.selectColor"), this.color.getColor());
			if (color != null) {
				this.color = new ColorFill(color);
				this.pnlPreview.repaint();
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
			color = JColorChooser.showDialog(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.editor.fill.selectColor"), stops[0].getColor());
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
				this.pnlPreview.repaint();
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
			color = JColorChooser.showDialog(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.editor.fill.selectColor"), stops[2].getColor());
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
				this.linear = new LinearGradientFill(direction, this.linear.getStops());
				this.pnlPreview.repaint();
			} else if (source == this.cmbRadialDirection) {
				RadialGradientDirection direction = (RadialGradientDirection)e.getItem();
				this.radial = new RadialGradientFill(direction, this.radial.getStops());
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
				this.pnlPreview.repaint();
			} else {
				this.radial = new RadialGradientFill(
						this.radial.getDirection(),
						this.radial.getStops()[0],
						new Stop(value, this.radial.getStops()[1].getColor()),
						this.radial.getStops()[2]);
				this.pnlPreview.repaint();
			}
		} else if (source == this.tabs) {
			// when you change tabs update the preview panel
			this.pnlPreview.repaint();
		}
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
			this.tabs.setSelectedIndex(1);
			this.rdoLinear.setSelected(true);
			this.rdoRadial.setSelected(false);
			this.cmbLinearDirection.setVisible(true);
			this.cmbRadialDirection.setVisible(false);
		} else if (type == 1) {
			this.tabs.setSelectedIndex(1);
			this.rdoLinear.setSelected(false);
			this.rdoRadial.setSelected(true);
			this.cmbLinearDirection.setVisible(false);
			this.cmbRadialDirection.setVisible(true);
		} else {
			this.tabs.setSelectedIndex(0);
			this.rdoLinear.setSelected(true);
			this.rdoRadial.setSelected(false);
			this.cmbLinearDirection.setVisible(true);
			this.cmbRadialDirection.setVisible(false);
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
		if (this.tabs.getSelectedIndex() == 0) {
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
