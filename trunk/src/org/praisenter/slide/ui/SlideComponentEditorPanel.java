package org.praisenter.slide.ui;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.SlideComponent;

/**
 * Abstact editor panel for slide components.
 * @param <E> the {@link SlideComponent} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class SlideComponentEditorPanel<E extends RenderableSlideComponent> extends JPanel implements ActionListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -8456563715108565220L;

	/** The component being edited */
	protected E component;
	
	/** The color settings label */
	protected JLabel lblBackgroundColor;
	
	/** The button to show the color chooser */
	protected JButton btnBackgroundColor;
	
	/** The checkbox for color visibility */
	protected JCheckBox chkBackgroundVisible;
	
	// TODO translate
	// TODO gradients
	/**
	 * Default constructor.
	 */
	public SlideComponentEditorPanel() {
		// color
		this.lblBackgroundColor = new JLabel("background color");
		this.btnBackgroundColor = new JButton(Icons.COLOR);
		this.btnBackgroundColor.addActionListener(this);
		this.btnBackgroundColor.setActionCommand("bg-color");
		
		this.chkBackgroundVisible = new JCheckBox("visible");
		this.chkBackgroundVisible.addChangeListener(this);
		this.chkBackgroundVisible.setSelected(this.component.isBackgroundPaintVisible());
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("bg-color".equals(command)) {
			Paint old = this.component.getBackgroundPaint();
			Color color = Color.WHITE;
			if (old instanceof Color) {
				color = (Color)old;
			}
			// show the color chooser
			Color nColor = JColorChooser.showDialog(this, Messages.getString("panel.color.setup.browse"), color);
			if (nColor != null) {
				this.component.setBackgroundPaint(nColor);
				this.firePropertyChange(SlideEditorPanel.PROPERTY_SLIDE_CHANGED, color, nColor);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.chkBackgroundVisible) {
			boolean old = this.component.isBackgroundPaintVisible();
			boolean flag = this.chkBackgroundVisible.isSelected();
			this.component.setBackgroundPaintVisible(flag);
			this.firePropertyChange(SlideEditorPanel.PROPERTY_SLIDE_CHANGED, old, flag);
		}
	}
}
