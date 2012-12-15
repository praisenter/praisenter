package org.praisenter.slide.ui.editor;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.resources.Messages;
import org.praisenter.slide.GenericSlideComponent;

/**
 * Editor panel for {@link GenericSlideComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 * @param <E> the {@link GenericSlideComponent} type
 */
public class GenericSlideComponentEditorPanel<E extends GenericSlideComponent> extends RenderableSlideComponentEditorPanel<E> implements EditorListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 1338378689202204500L;
	
	/** The border paint editor panel */
	protected FillEditorPanel pnlBorderFill;
	
	/** The border stroke editor panel */
	protected LineStyleEditorPanel pnlBorderStyle;

	/** The checkbox for border visibility */
	protected JCheckBox chkBorderVisible;
	
	/**
	 * Default constructor.
	 */
	public GenericSlideComponentEditorPanel() {
		this(true);
	}
	
	/**
	 * Constructor for sub classes only.
	 * @param doLayout true if the layout should be created
	 */
	protected GenericSlideComponentEditorPanel(boolean doLayout) {
		this.pnlBorderFill = new FillEditorPanel(null);
		this.pnlBorderFill.addEditorListener(this);
		this.pnlBorderStyle = new LineStyleEditorPanel(null);
		this.pnlBorderStyle.addEditorListener(this);
		this.chkBorderVisible = new JCheckBox(Messages.getString("panel.slide.editor.component.visible"));
		this.chkBorderVisible.addChangeListener(this);
		
		if (doLayout) {
			this.createLayout();
		}
	}
	
	/**
	 * Creates the layout for a generic slide component.
	 */
	protected void createLayout() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JTabbedPane tabs = new JTabbedPane();
		
		JPanel pnlBackground = new JPanel();
		this.createBackgroundLayout(pnlBackground);
		tabs.addTab(Messages.getString("panel.slide.editor.component.background"), pnlBackground);
		
		JPanel pnlBorder = new JPanel();
		this.createBorderLayout(pnlBorder);
		tabs.addTab(Messages.getString("panel.slide.editor.component.border"), pnlBorder);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.txtName)
				.addComponent(tabs));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(tabs));
	}
	
	/**
	 * Creates a layout for the border fill and style on the given panel.
	 * @param panel the panel
	 */
	protected void createBorderLayout(JPanel panel) {
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.chkBorderVisible)
				.addComponent(this.pnlBorderFill)
				.addComponent(this.pnlBorderStyle));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.chkBorderVisible)
				.addComponent(this.pnlBorderFill)
				.addComponent(this.pnlBorderStyle));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.RenderableSlideComponentEditorPanel#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		super.stateChanged(e);
		
		Object source = e.getSource();
		if (source == this.chkBorderVisible) {
			if (this.slideComponent != null) {
				this.slideComponent.setBorderVisible(this.chkBorderVisible.isSelected());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.RenderableSlideComponentEditorPanel#editPerformed(org.praisenter.slide.ui.editor.EditEvent)
	 */
	@Override
	public void editPerformed(EditEvent event) {
		super.editPerformed(event);
		
		Object source = event.getSource();
		if (source == this.pnlBorderFill) {
			if (this.slideComponent != null) {
				this.slideComponent.setBorderFill(this.pnlBorderFill.getFill());
				this.notifyEditorListeners();
			}
		} else if (source == this.pnlBorderStyle) {
			if (this.slideComponent != null) {
				this.slideComponent.setBorderStyle(this.pnlBorderStyle.getLineStyle());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.RenderableSlideComponentEditorPanel#setSlideComponent(org.praisenter.slide.RenderableSlideComponent)
	 */
	@Override
	public void setSlideComponent(E slideComponent) {
		super.setSlideComponent(slideComponent);
		
		this.pnlBorderFill.setFill(slideComponent.getBorderFill());
		this.pnlBorderStyle.setLineStyle(slideComponent.getBorderStyle());
		this.chkBorderVisible.setSelected(slideComponent.isBorderVisible());
	}
}
