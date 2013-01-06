package org.praisenter.slide.ui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.utilities.WindowUtilities;

/**
 * Abstact editor panel for {@link RenderableComponent}s.
 * @param <E> the {@link RenderableComponent} type
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class RenderableComponentEditorPanel<E extends RenderableComponent> extends SlideComponentEditorPanel<E> implements ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = -8456563715108565220L;
	
	/** The checkbox for background paint visibility */
	protected JCheckBox chkBackgroundVisible;
	
	/** The background fill label */
	protected JLabel lblBackground;
	
	/** The background fill button */
	protected JButton btnBackgroundFill;
	
	/**
	 * Default constructor.
	 */
	protected RenderableComponentEditorPanel() {
		// background
		this.lblBackground = new JLabel(Messages.getString("panel.slide.editor.background"));
		
		this.btnBackgroundFill = new JButton(Icons.FILL);
		this.btnBackgroundFill.addActionListener(this);
		this.btnBackgroundFill.setActionCommand("bg-fill");
		
		this.chkBackgroundVisible = new JCheckBox(Messages.getString("panel.slide.editor.visible"));
		this.chkBackgroundVisible.addChangeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.chkBackgroundVisible) {
			if (this.slideComponent != null) {
				boolean flag = this.chkBackgroundVisible.isSelected();
				this.slideComponent.setBackgroundVisible(flag);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("bg-fill".equals(command)) {
			Fill fill = new ColorFill();
			if (this.slideComponent != null) {
				fill = this.slideComponent.getBackgroundFill();
			}
			fill = FillEditorDialog.show(WindowUtilities.getParentWindow(this), fill);
			if (fill != null) {
				if (this.slideComponent != null) {
					this.slideComponent.setBackgroundFill(fill);
					this.notifyEditorListeners();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#setSlideComponent(org.praisenter.slide.SlideComponent, boolean)
	 */
	public void setSlideComponent(E slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		if (slideComponent != null) {
			this.chkBackgroundVisible.setSelected(slideComponent.isBackgroundVisible());
		} else {
			this.chkBackgroundVisible.setSelected(false);
		}
	}
}