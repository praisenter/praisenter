package org.praisenter.slide.ui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.slide.GenericComponent;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LineStyle;
import org.praisenter.utilities.WindowUtilities;

/**
 * Editor panel for {@link GenericComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 * @param <E> the {@link GenericComponent} type
 */
public class GenericComponentEditorPanel<E extends GenericComponent> extends RenderableComponentEditorPanel<E> implements ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 1338378689202204500L;
	
	/** The checkbox for border visibility */
	protected JCheckBox chkBorderVisible;
	
	/** The border fill label */
	protected JLabel lblBorder;
	
	/** The border fill button */
	protected JButton btnBorderFill;
	
	/** The border style button */
	protected JButton btnBorderStyle;

	/**
	 * Default constructor.
	 */
	public GenericComponentEditorPanel() {
		this(true);
	}
	
	/**
	 * Constructor for sub classes only.
	 * @param doLayout true if the layout should be created
	 */
	protected GenericComponentEditorPanel(boolean doLayout) {
		this.lblBorder = new JLabel(Messages.getString("panel.slide.editor.border"));
		this.btnBorderFill = new JButton(Icons.FILL);
		this.btnBorderFill.addActionListener(this);
		this.btnBorderFill.setActionCommand("border-fill");
		
		this.btnBorderStyle = new JButton(Icons.BORDER);
		this.btnBorderStyle.addActionListener(this);
		this.btnBorderStyle.setActionCommand("border-style");
		this.btnBorderStyle.setToolTipText(Messages.getString("panel.slide.editor.line"));
		
		this.chkBorderVisible = new JCheckBox(Messages.getString("panel.slide.editor.visible"));
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
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblBackground)
						.addComponent(this.lblBorder))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnBackgroundFill)
								.addComponent(this.chkBackgroundVisible))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnBorderFill)
								.addComponent(this.btnBorderStyle)
								.addComponent(this.chkBorderVisible))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBackground)
						.addComponent(this.btnBackgroundFill)
						.addComponent(this.chkBackgroundVisible))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBorder)
						.addComponent(this.btnBorderFill)
						.addComponent(this.btnBorderStyle)
						.addComponent(this.chkBorderVisible)));
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
	 * @see org.praisenter.slide.ui.editor.RenderableComponentEditorPanel#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		
		String command = e.getActionCommand();
		if ("border-fill".equals(command)) {
			Fill fill = new ColorFill();
			if (this.slideComponent != null) {
				fill = this.slideComponent.getBorderFill();
			}
			fill = FillEditorDialog.show(WindowUtilities.getParentWindow(this), fill);
			if (fill != null) {
				if (this.slideComponent != null) {
					this.slideComponent.setBorderFill(fill);
					this.notifyEditorListeners();
				}
			}
		} else if ("border-style".equals(command)) {
			LineStyle style = new LineStyle();
			if (this.slideComponent != null) {
				style = this.slideComponent.getBorderStyle();
			}
			style = LineStyleEditorDialog.show(WindowUtilities.getParentWindow(this), style);
			if (style != null) {
				if (this.slideComponent != null) {
					this.slideComponent.setBorderStyle(style);
					this.notifyEditorListeners();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.RenderableComponentEditorPanel#setSlideComponent(org.praisenter.slide.RenderableComponent, boolean)
	 */
	@Override
	public void setSlideComponent(E slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		if (slideComponent != null) {
			this.chkBorderVisible.setSelected(slideComponent.isBorderVisible());
		} else {
			this.chkBorderVisible.setSelected(false);
		}
	}
}
