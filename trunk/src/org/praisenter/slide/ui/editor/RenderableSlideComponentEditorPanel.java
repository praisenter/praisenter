package org.praisenter.slide.ui.editor;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.resources.Messages;
import org.praisenter.slide.RenderableSlideComponent;

/**
 * Abstact editor panel for {@link RenderableSlideComponent}s.
 * @param <E> the {@link RenderableSlideComponent} type
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class RenderableSlideComponentEditorPanel<E extends RenderableSlideComponent> extends SlideComponentEditorPanel<E> implements ChangeListener, EditorListener {
	/** The version id */
	private static final long serialVersionUID = -8456563715108565220L;
	
	/** The background paint editor panel */
	protected FillEditorPanel pnlBackgroundFill;
	
	/** The checkbox for background paint visibility */
	protected JCheckBox chkBackgroundVisible;
	
	/**
	 * Default constructor.
	 */
	protected RenderableSlideComponentEditorPanel() {
		// background
		this.pnlBackgroundFill = new FillEditorPanel(null);
		this.pnlBackgroundFill.addEditorListener(this);
		
		this.chkBackgroundVisible = new JCheckBox(Messages.getString("panel.slide.editor.component.visible"));
		this.chkBackgroundVisible.addChangeListener(this);
	}
	
	/**
	 * Creates a layout for the background fill on the given panel.
	 * @param panel the panel
	 */
	protected void createBackgroundLayout(JPanel panel) {
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.chkBackgroundVisible)
				.addComponent(this.pnlBackgroundFill));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.chkBackgroundVisible)
				.addComponent(this.pnlBackgroundFill));
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
	 * @see org.praisenter.slide.ui.editor.EditorListener#editPerformed(org.praisenter.slide.ui.editor.EditEvent)
	 */
	@Override
	public void editPerformed(EditEvent event) {
		Object source = event.getSource();
		if (source == this.pnlBackgroundFill) {
			if (this.slideComponent != null) {
				this.slideComponent.setBackgroundFill(this.pnlBackgroundFill.getFill());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#setSlideComponent(org.praisenter.slide.SlideComponent)
	 */
	public void setSlideComponent(E slideComponent) {
		super.setSlideComponent(slideComponent);
		
		this.pnlBackgroundFill.setFill(slideComponent.getBackgroundFill());
		this.chkBackgroundVisible.setSelected(slideComponent.isBackgroundVisible());
	}
}