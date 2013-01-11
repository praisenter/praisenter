package org.praisenter.slide.ui;

import java.text.MessageFormat;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.praisenter.resources.Messages;
import org.praisenter.slide.SlideFile;
import org.praisenter.utilities.ComponentUtilities;
import org.praisenter.utilities.Formatter;

/**
 * Panel used to display the properties of a {@link SlideFile}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlidePropertiesPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 8713325204658785060L;

	/** The slide file information */
	private SlideFile file;
	
	// controls
	
	/** The properties main label */
	private JLabel lblProperties;
	
	/** The main label to properties separator */
	private JSeparator sepProperties;
	
	// labels
	
	/** The name label */
	private JLabel lblName;
	
	/** The path label */
	private JLabel lblPath;
	
	/** The size label */
	private JLabel lblSize;
	
	// fields
	
	/** The name text box */
	private JTextField txtName;
	
	/** The path text box */
	private JTextField txtPath;
	
	/** The size text box */
	private JTextField txtSize;

	/**
	 * Default constructor.
	 */
	public SlidePropertiesPanel() {
		this.lblProperties = new JLabel(Messages.getString("panel.slide.properties"));
		this.sepProperties = new JSeparator();
		
		this.lblName = new JLabel(Messages.getString("panel.slide.properties.name"));
		this.txtName = new JTextField();
		this.txtName.setEditable(false);
		
		this.lblPath = new JLabel(Messages.getString("panel.slide.properties.path"));
		this.txtPath = new JTextField();
		this.txtPath.setEditable(false);
		
		this.lblSize = new JLabel(Messages.getString("panel.slide.properties.size"));
		this.txtSize = new JTextField();
		this.txtSize.setEditable(false);
		
		// size the labels
		ComponentUtilities.setMinimumSize(
				this.lblName,
				this.lblPath,
				this.lblSize);
		
		// create the layout
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.lblProperties, GroupLayout.Alignment.CENTER)
				.addComponent(this.sepProperties)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblName)
								.addComponent(this.lblPath)
								.addComponent(this.lblSize))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtName)
								.addComponent(this.txtPath)
								.addComponent(this.txtSize))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.lblProperties)
				.addComponent(this.sepProperties, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblPath)
						.addComponent(this.txtPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblSize)
						.addComponent(this.txtSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Sets the slide file.
	 * @param file the slide file
	 * @param isSlide true if the selected slide is a slide (rather than a template)
	 */
	public void setSlideFile(SlideFile file, boolean isSlide) {
		this.file = file;
		
		String type = Messages.getString("panel.slide");
		if (!isSlide) {
			type = Messages.getString("panel.template");
		}
		type = type.toLowerCase();
		
		if (file != null) {
			this.txtName.setText(file.getName());
			this.txtName.setToolTipText(MessageFormat.format(Messages.getString("panel.slide.properties.name.tooltip"), type));
			this.txtName.setCaretPosition(0);
			this.txtPath.setText(file.getPath());
			this.txtPath.setCaretPosition(0);
			this.txtPath.setToolTipText(MessageFormat.format(Messages.getString("panel.slide.properties.path.tooltip"), type));
			this.txtSize.setText(Formatter.getSizeFormattedString(file.getSize()));
			this.txtSize.setCaretPosition(0);
			this.txtSize.setToolTipText(MessageFormat.format(Messages.getString("panel.slide.properties.size.tooltip"), type));
		} else {
			this.txtName.setText("");
			this.txtName.setToolTipText(null);
			this.txtPath.setText("");
			this.txtPath.setToolTipText(null);
			this.txtSize.setText("");
			this.txtSize.setToolTipText(null);
		}
	}
	
	/**
	 * Returns the current slide file.
	 * @return {@link SlideFile}
	 */
	public SlideFile getSlideFile() {
		return this.file;
	}
}
