package org.praisenter.slide.ui.editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
public class LineStyleEditorPanel extends EditorPanel implements ItemListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 7601186524559365213L;

	/** The line style being configured */
	private LineStyle style;
	
	// controls
	
	/** The line width spinner */
	private JSpinner spnLineWidth;
	
	/** The line cap type combo box */
	private JComboBox<CapType> cmbCap;
	
	/** The line join type combo box */
	private JComboBox<JoinType> cmbJoin;
	
	/** The line dash type combo box */
	private JComboBox<DashPattern> cmbDash;
	
	/**
	 * Minimal constructor.
	 * @param style the initial line style; can be null
	 */
	public LineStyleEditorPanel(LineStyle style) {
		JLabel lblLineWidth = new JLabel(Messages.getString("panel.slide.editor.line.width"));
		this.spnLineWidth = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
		
		JLabel lblCap = new JLabel(Messages.getString("panel.slide.editor.line.cap"));
		this.cmbCap = new JComboBox<CapType>(CapType.values());
		this.cmbCap.setRenderer(new CapTypeListCellRenderer());
		
		JLabel lblJoin = new JLabel(Messages.getString("panel.slide.editor.line.join"));
		this.cmbJoin = new JComboBox<JoinType>(JoinType.values());
		this.cmbJoin.setRenderer(new JoinTypeListCellRenderer());
		
		JLabel lblDashPattern = new JLabel(Messages.getString("panel.slide.editor.line.pattern"));
		this.cmbDash = new JComboBox<DashPattern>(DashPattern.values());
		this.cmbDash.setRenderer(new DashPatternListCellRenderer());
		
		// set values
		this.setLineStyle(style);
		
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
								.addComponent(this.cmbDash))));
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
						.addComponent(this.cmbDash, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
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
				this.notifyEditorListeners();
			} else if (source == this.cmbJoin) {
				JoinType join = (JoinType)e.getItem();
				this.style = new LineStyle(this.style.getWidth(), this.style.getCap(), join, this.style.getPattern());
				this.notifyEditorListeners();
			} else if (source == this.cmbDash) {
				DashPattern pattern = (DashPattern)e.getItem();
				this.style = new LineStyle(this.style.getWidth(), this.style.getCap(), this.style.getJoin(), pattern);
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
		if (source == this.spnLineWidth) {
			Object v = this.spnLineWidth.getValue();
			if (v != null && v instanceof Number) {
				float width = ((Number)v).floatValue();
				this.style = new LineStyle(width, this.style.getCap(), this.style.getJoin(), this.style.getPattern());
				this.notifyEditorListeners();
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
