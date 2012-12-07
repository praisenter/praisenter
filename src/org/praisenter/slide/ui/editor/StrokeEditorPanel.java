package org.praisenter.slide.ui.editor;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// FIXME translate

/**
 * Editor panel for BasicStroke objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class StrokeEditorPanel extends JPanel implements ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 7601186524559365213L;

	/** The stroke being configured */
	private BasicStroke stroke;
	
	// controls
	
	/** The line width spinner */
	private JSpinner spnLineWidth;
	
	/** The line cap type combo box */
	private JComboBox<CapType> cmbCap;
	
	/** The line join type combo box */
	private JComboBox<JoinType> cmbJoin;
	
	/** The line dash type combo box */
	private JComboBox<DashPattern> cmbDash;
	
	/** The line preview panel */
	private JPanel pnlPreview;
	
	/**
	 * Minimal constructor.
	 * @param stroke the initial stroke; can be null
	 */
	@SuppressWarnings("serial")
	public StrokeEditorPanel(BasicStroke stroke) {
		if (stroke == null) {
			final float l = 1.0f;
			this.stroke = stroke = new BasicStroke(l, CapType.SQUARE.getStrokeValue(), JoinType.BEVEL.getStrokeValue(), l, DashPattern.SOLID.getDashLengths(l), 0.0f);
		}
		
		JLabel lblLineWidth = new JLabel("Line Width");
		this.spnLineWidth = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
		
		JLabel lblCap = new JLabel("Cap");
		this.cmbCap = new JComboBox<CapType>(CapType.values());
		
		JLabel lblJoin = new JLabel("Join");
		this.cmbJoin = new JComboBox<JoinType>(JoinType.values());
		
		JLabel lblDashPattern = new JLabel("Dash Pattern");
		this.cmbDash = new JComboBox<DashPattern>(DashPattern.values());
		
		this.pnlPreview = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2d.setStroke(StrokeEditorPanel.this.stroke);
				g2d.drawRect(25, 25, 225, 50);
			}
		};
		this.pnlPreview.setMinimumSize(new Dimension(300, 100));
		this.pnlPreview.setPreferredSize(new Dimension(300, 100));
		
		if (stroke instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke)stroke;
			this.spnLineWidth.setValue(bs.getLineWidth());
			this.cmbCap.setSelectedItem(CapType.getCapType(bs.getEndCap()));
			this.cmbJoin.setSelectedItem(JoinType.getJoinType(bs.getLineJoin()));
			this.cmbDash.setSelectedItem(DashPattern.getDashPattern(bs.getDashArray(), bs.getLineWidth()));
		}
		
		// wire up events
		this.spnLineWidth.addChangeListener(this);
		this.cmbCap.addItemListener(this);
		this.cmbJoin.addItemListener(this);
		this.cmbDash.addItemListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(lblLineWidth)
								.addComponent(lblCap)
								.addComponent(lblJoin)
								.addComponent(lblDashPattern))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.spnLineWidth)
								.addComponent(this.cmbCap)
								.addComponent(this.cmbJoin)
								.addComponent(this.cmbDash)))
				.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblLineWidth)
						.addComponent(this.spnLineWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblCap)
						.addComponent(this.cmbCap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblJoin)
						.addComponent(this.cmbJoin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblDashPattern)
						.addComponent(this.cmbDash, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
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
				this.stroke = new BasicStroke(
						this.stroke.getLineWidth(),
						cap.getStrokeValue(),
						this.stroke.getLineJoin(),
						this.stroke.getMiterLimit(),
						this.stroke.getDashArray(),
						this.stroke.getDashPhase());
				this.pnlPreview.repaint();
			} else if (source == this.cmbJoin) {
				JoinType join = (JoinType)e.getItem();
				this.stroke = new BasicStroke(
						this.stroke.getLineWidth(),
						this.stroke.getEndCap(),
						join.getStrokeValue(),
						this.stroke.getMiterLimit(),
						this.stroke.getDashArray(),
						this.stroke.getDashPhase());
				this.pnlPreview.repaint();
			} else if (source == this.cmbDash) {
				DashPattern pattern = (DashPattern)e.getItem();
				this.stroke = new BasicStroke(
						this.stroke.getLineWidth(),
						this.stroke.getEndCap(),
						this.stroke.getLineJoin(),
						this.stroke.getMiterLimit(),
						pattern.getDashLengths(this.stroke.getLineWidth()),
						this.stroke.getDashPhase());
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
				DashPattern pattern = DashPattern.getDashPattern(this.stroke.getDashArray(), this.stroke.getLineWidth());
				this.stroke = new BasicStroke(
						((Number)v).floatValue(),
						this.stroke.getEndCap(),
						this.stroke.getLineJoin(),
						this.stroke.getMiterLimit(),
						pattern.getDashLengths(((Number)v).floatValue()),
						this.stroke.getDashPhase());
				this.pnlPreview.repaint();
			}
		}
	}
}
