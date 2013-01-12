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
import org.praisenter.slide.graphics.Fill;
import org.praisenter.ui.BottomButtonPanel;

/**
 * Dialog for choosing a fill color.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class FillEditorDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 7361262866678826911L;
	
	/** True if the action was cancelled */
	private boolean isCancel;
	
	/** The fill editor panel */
	private FillEditorPanel pnlFillEditor;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param fill the initial fill
	 */
	private FillEditorDialog(Window owner, Fill fill) {
		super(owner, Messages.getString("panel.slide.editor.fill"), ModalityType.APPLICATION_MODAL);
		
		this.isCancel = false;
		this.pnlFillEditor = new FillEditorPanel(fill);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlFillEditor, BorderLayout.CENTER);
		
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
		
		container.add(this.pnlFillEditor, BorderLayout.CENTER);
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
	 * Shows a new {@link FillEditorDialog} using the given initial fill.
	 * <p>
	 * Returns the user configured fill when the user clicks the accept button
	 * and returns null if the user clicks the cancel button.
	 * @param owner the dialog owner
	 * @param fill the initial fill
	 * @return {@link Fill}
	 */
	public static final Fill show(Window owner, Fill fill) {
		FillEditorDialog dialog = new FillEditorDialog(owner, fill);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (dialog.isCancel) {
			fill = null;
		} else {
			fill = dialog.pnlFillEditor.getFill();
		}
		
		dialog.dispose();
		
		return fill;
	}
}
