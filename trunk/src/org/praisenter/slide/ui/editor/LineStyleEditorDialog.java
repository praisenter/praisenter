package org.praisenter.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.LineStyle;
import org.praisenter.ui.BottomButtonPanel;

/**
 * Dialog for choosing a line style.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class LineStyleEditorDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -4898946623099190356L;

	/** True if the action was cancelled */
	private boolean isCancel;
	
	/** The fill editor panel */
	private LineStyleEditorPanel pnlLineEditor;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param style the line style
	 */
	private LineStyleEditorDialog(Window owner, LineStyle style) {
		super(owner, Messages.getString("panel.slide.editor.line"), ModalityType.APPLICATION_MODAL);
		
		this.isCancel = false;
		this.pnlLineEditor = new LineStyleEditorPanel(style);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlLineEditor, BorderLayout.CENTER);
		
		JButton btnOk = new JButton(Messages.getString("panel.slide.editor.ok"));
		btnOk.addActionListener(this);
		btnOk.setActionCommand("ok");
		
		JButton btnCancel = new JButton(Messages.getString("panel.slide.editor.cancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("cancel");
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.TRAILING));
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		
		container.add(this.pnlLineEditor, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("ok".equals(command)) {
			this.setVisible(false);
		} else if ("cancel".equals(command)) {
			this.isCancel = true;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows a new {@link LineStyleEditorDialog} using the given initial line style.
	 * <p>
	 * Returns the user configured line style when the user clicks the accept button
	 * and returns null if the user clicks the cancel button.
	 * @param owner the dialog owner
	 * @param style the initial line style
	 * @return {@link LineStyle}
	 */
	public static final LineStyle show(Window owner, LineStyle style) {
		LineStyleEditorDialog dialog = new LineStyleEditorDialog(owner, style);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (dialog.isCancel) {
			style = null;
		} else {
			style = dialog.pnlLineEditor.getLineStyle();
		}
		
		dialog.dispose();
		
		return style;
	}
}
