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
package org.praisenter.slide.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.CapType;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.JoinType;
import org.praisenter.slide.graphics.LineStyle;

/**
 * Editor panel for BasicStroke objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class LineStyleEditorPanel extends JPanel implements ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 7601186524559365213L;

	/** The line style being configured */
	private LineStyle style;
	
	// controls
	
	/** The line width label */
	private JLabel lblLineWidth;
	
	/** The line width spinner */
	private JSpinner spnLineWidth;
	
	/** The line cap label */
	private JLabel lblCap;
	
	/** The line cap type combo box */
	private JComboBox<CapType> cmbCap;
	
	/** The line join label */
	private JLabel lblJoin;
	
	/** The line join type combo box */
	private JComboBox<JoinType> cmbJoin;
	
	/** The line dash pattern label */
	private JLabel lblDashPattern;
	
	/** The line dash type combo box */
	private JComboBox<DashPattern> cmbDash;
	
	/** The line style preview panel */
	private JPanel pnlPreview;
	
	/**
	 * Minimal constructor.
	 * @param style the initial line style; can be null
	 */
	@SuppressWarnings("serial")
	public LineStyleEditorPanel(LineStyle style) {
		this.lblLineWidth = new JLabel(Messages.getString("panel.slide.editor.line.width"));
		this.spnLineWidth = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		
		this.lblCap = new JLabel(Messages.getString("panel.slide.editor.line.cap"));
		this.cmbCap = new JComboBox<CapType>(CapType.values());
		this.cmbCap.setRenderer(new CapTypeListCellRenderer());
		
		this.lblJoin = new JLabel(Messages.getString("panel.slide.editor.line.join"));
		this.cmbJoin = new JComboBox<JoinType>(JoinType.values());
		this.cmbJoin.setRenderer(new JoinTypeListCellRenderer());
		
		this.lblDashPattern = new JLabel(Messages.getString("panel.slide.editor.line.pattern"));
		this.cmbDash = new JComboBox<DashPattern>(DashPattern.values());
		this.cmbDash.setRenderer(new DashPatternListCellRenderer());
		
		final Color bgColor = Color.WHITE;
		final Color fgColor = Color.BLACK;
		final int p = 5;
		this.pnlPreview = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				int w = this.getWidth() - 2 * p;
				int h = this.getHeight() - 2 * p;
				
				// paint the background
				g.setColor(bgColor);
				g.fillRect(p, p, w, h);
				
				LineStyle style = LineStyleEditorPanel.this.style;
				if (style != null) {
					int lw = (int)Math.ceil(style.getWidth());
					Graphics2D g2d = (Graphics2D)g;
					Stroke oStroke = g2d.getStroke();
					RenderingHints oHints = g2d.getRenderingHints();
					
					Stroke stroke = style.getStroke();
					g2d.setStroke(stroke);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					g2d.setColor(fgColor);
					g2d.drawRect(p + lw + 20, p + lw + 20, w - 2 * lw - 40, h - 2 * lw - 40);
					
					g2d.setRenderingHints(oHints);
					g2d.setStroke(oStroke);
				}
			}
		};
		this.pnlPreview.setMinimumSize(new Dimension(0, 150));
		this.pnlPreview.setPreferredSize(new Dimension(200, 150));
		this.pnlPreview.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(p, p, p, p), 
				BorderFactory.createLineBorder(Color.DARK_GRAY)));
		
		// set values
		this.setLineStyle(style);
		
		// wire up events
		this.spnLineWidth.addChangeListener(this);
		this.cmbCap.addItemListener(this);
		this.cmbJoin.addItemListener(this);
		this.cmbDash.addItemListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblLineWidth)
								.addComponent(this.lblCap)
								.addComponent(this.lblJoin)
								.addComponent(this.lblDashPattern))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.spnLineWidth)
								.addComponent(this.cmbCap)
								.addComponent(this.cmbJoin)
								.addComponent(this.cmbDash)))
				.addComponent(this.pnlPreview));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLineWidth)
						.addComponent(this.spnLineWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblCap)
						.addComponent(this.cmbCap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblJoin)
						.addComponent(this.cmbJoin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblDashPattern)
						.addComponent(this.cmbDash, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.pnlPreview));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbCap) {
				CapType cap = (CapType)e.getItem();
				this.style = new LineStyle(this.style.getWidth(), cap, this.style.getJoin(), this.style.getPattern());
				this.pnlPreview.repaint();
			} else if (source == this.cmbJoin) {
				JoinType join = (JoinType)e.getItem();
				this.style = new LineStyle(this.style.getWidth(), this.style.getCap(), join, this.style.getPattern());
				this.pnlPreview.repaint();
			} else if (source == this.cmbDash) {
				DashPattern pattern = (DashPattern)e.getItem();
				this.style = new LineStyle(this.style.getWidth(), this.style.getCap(), this.style.getJoin(), pattern);
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
		if (source == this.spnLineWidth) {
			Object v = this.spnLineWidth.getValue();
			if (v != null && v instanceof Number) {
				float width = ((Number)v).floatValue();
				this.style = new LineStyle(width, this.style.getCap(), this.style.getJoin(), this.style.getPattern());
				this.pnlPreview.repaint();
			}
		}
	}
	
	/**
	 * Sets the line style to configure.
	 * @param style the line style
	 */
	public void setLineStyle(LineStyle style) {
		this.style = style;
		
		if (style == null) {
			this.style = style = new LineStyle();
		}
		
		this.spnLineWidth.setValue(style.getWidth());
		this.cmbCap.setSelectedItem(style.getCap());
		this.cmbJoin.setSelectedItem(style.getJoin());
		this.cmbDash.setSelectedItem(style.getPattern());
	}
	
	/**
	 * Returns the configured line style.
	 * @return {@link LineStyle}
	 */
	public LineStyle getLineStyle() {
		return this.style;
	}
}
