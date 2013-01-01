package org.praisenter.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.praisenter.resources.Messages;
import org.praisenter.slide.Resolution;
import org.praisenter.slide.Resolutions;
import org.praisenter.ui.BottomButtonPanel;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.WindowUtilities;

public class CustomResolutionDialog extends JDialog implements ActionListener {
	
	private JFormattedTextField txtWidth;
	private JFormattedTextField txtHeight;
	
	private Resolution resolution;
	
	private CustomResolutionDialog(Window owner) {
		super(owner, Messages.getString("panel.slide.editor.resolution.custom.title"), ModalityType.APPLICATION_MODAL);
		
		this.txtWidth = new JFormattedTextField(new DecimalFormat("0")) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.slide.editor.width"));
			}
		};
		this.txtWidth.setColumns(8);
		
		this.txtHeight = new JFormattedTextField(new DecimalFormat("0")) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.slide.editor.height"));
			}
		};
		this.txtHeight.setColumns(8);
		
		JLabel lblX = new JLabel(Messages.getString("panel.slide.editor.resolution.by"));
		
		JButton btnOk = new JButton("ok");
		btnOk.addActionListener(this);
		btnOk.setActionCommand("ok");
		
		JButton btnCancel = new JButton("cancel");
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("cancel");
		
		JPanel pnlResolution = new JPanel();
		GroupLayout layout = new GroupLayout(pnlResolution);
		pnlResolution.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(this.txtWidth)
				.addComponent(lblX)
				.addComponent(this.txtHeight));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(this.txtWidth)
				.addComponent(lblX)
				.addComponent(this.txtHeight));

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		
		container.add(pnlResolution, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("ok".equals(command)) {
			// validate the resolution
			int w = 0;
			int h = 0;
			
			Object ow = this.txtWidth.getValue();
			Object oh = this.txtHeight.getValue();
			
			if (ow != null && ow instanceof Number) {
				w = ((Number)ow).intValue();
			}
			if (oh != null && oh instanceof Number) {
				h = ((Number)oh).intValue();
			}
			
			// make sure the width and height are greater than zero
			if (w > 0 && h > 0) {
				// make sure the resolution doesn't already exist
				List<Resolution> resolutions = Resolutions.getResolutions();
				for (Resolution resolution : resolutions) {
					if (resolution.getWidth() == w && resolution.getHeight() == h) {
						JOptionPane.showMessageDialog(
								WindowUtilities.getParentWindow(this), 
								Messages.getString("panel.slide.editor.resolution.exists.message"), 
								Messages.getString("panel.slide.editor.resolution.invalid.title"), 
								JOptionPane.ERROR_MESSAGE);
						this.resolution = null;
						return;
					}
				}
				// if we make it here the resolution is valid
				this.resolution = new Resolution(w, h);
				this.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(
						WindowUtilities.getParentWindow(this), 
						Messages.getString("panel.slide.editor.resolution.invalid.message"), 
						Messages.getString("panel.slide.editor.resolution.invalid.title"), 
						JOptionPane.ERROR_MESSAGE);
				this.resolution = null;
				return;
			}
		} else if ("cancel".equals(command)) {
			this.resolution = null;
			this.setVisible(false);
		}
	}
	
	public static final Resolution show(Window owner) {
		CustomResolutionDialog dialog = new CustomResolutionDialog(owner);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		Resolution resolution = dialog.resolution;
		dialog.dispose();
		
		return resolution;
	}
}
