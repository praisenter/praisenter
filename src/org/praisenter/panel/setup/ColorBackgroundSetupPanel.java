package org.praisenter.panel.setup;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.display.ColorBackgroundComponent;
import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;

/**
 * Panel used to setup a color background.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ColorBackgroundSetupPanel extends JPanel implements ActionListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 2396781350562073058L;

	/** The {@link ColorBackgroundComponent} to setup */
	protected ColorBackgroundComponent component;
	
	/** The button to show the color chooser */
	protected JButton btnSelect;
	
	/** The checkbox for visibility */
	protected JCheckBox chkVisible;
	
	/**
	 * Minimal constructor.
	 * @param component the component to setup
	 */
	public ColorBackgroundSetupPanel(ColorBackgroundComponent component) {
		this.component = component;
		
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("display.basic.background.color")));
		
		// color
		this.btnSelect = new JButton(Icons.COLOR);
		this.btnSelect.setToolTipText(Messages.getString("panel.color.setup.browse"));
		this.btnSelect.addActionListener(this);
		this.btnSelect.setActionCommand("select");
		
		// visible
		this.chkVisible = new JCheckBox(Messages.getString("panel.color.setup.visible"), this.component.isVisible());
		this.chkVisible.addChangeListener(this);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.btnSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkVisible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.btnSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkVisible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("select".equals(e.getActionCommand())) {
			Color old = this.component.getColor();
			// show the color chooser
			Color color = JColorChooser.showDialog(this, Messages.getString("panel.color.setup.browse"), old);
			if (color != null) {
				this.component.setColor(color);
				this.firePropertyChange(DisplaySetupPanel.DISPLAY_COMPONENT_PROPERTY, old, color);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.chkVisible) {
			boolean old = this.component.isVisible();
			boolean flag = this.chkVisible.isSelected();
			this.component.setVisible(flag);
			this.firePropertyChange(DisplaySetupPanel.DISPLAY_COMPONENT_PROPERTY, old, flag);
		}
	}
}
