package org.praisenter.slide.ui.editor;

import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.praisenter.resources.Messages;
import org.praisenter.slide.SlideComponent;
import org.praisenter.ui.WaterMark;

/**
 * Editor panel base class for concrete editors.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 * @param <E> the {@link SlideComponent} type
 */
public abstract class SlideComponentEditorPanel<E extends SlideComponent> extends EditorPanel implements DocumentListener {
	/** The version id */
	private static final long serialVersionUID = 7582366573845511409L;

	/** The component being edited */
	protected E slideComponent;
	
	// controls
	
	/** The component name label */
	protected JLabel lblName;
	
	/** The component name text box */
	protected JTextField txtName;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	protected SlideComponentEditorPanel() {
		this.lblName = new JLabel(Messages.getString("panel.slide.editor.name"));
		this.txtName = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.slide.editor.component.name"));
			}
		};
		this.txtName.getDocument().addDocumentListener(this);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		if (this.txtName.getDocument() == e.getDocument()) {
			if (this.slideComponent != null) {
				this.slideComponent.setName(this.txtName.getText());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		if (this.txtName.getDocument() == e.getDocument()) {
			if (this.slideComponent != null) {
				this.slideComponent.setName(this.txtName.getText());
				this.notifyEditorListeners();
			}
		}
	}
	
	/**
	 * Returns the {@link SlideComponent} being configured.
	 * @return E
	 */
	public E getSlideComponent() {
		return this.slideComponent;
	}
	
	/**
	 * Sets the {@link SlideComponent} to configure.
	 * @param slideComponent the slide component
	 * @param isStatic true if the slide component is a static component
	 */
	public void setSlideComponent(E slideComponent, boolean isStatic) {
		this.slideComponent = slideComponent;
		
		if (slideComponent != null) {
			this.txtName.setText(slideComponent.getName());
			this.txtName.setCaretPosition(0);
			this.txtName.setEnabled(!isStatic);
		} else {
			this.txtName.setText("");
			this.txtName.setEnabled(false);
		}
	}
}
