package org.praisenter.media.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.praisenter.media.AudioMediaFile;
import org.praisenter.media.ImageMediaFile;
import org.praisenter.media.MediaFile;
import org.praisenter.media.VideoMediaFile;
import org.praisenter.resources.Messages;
import org.praisenter.utilities.ComponentUtilities;

/**
 * Panel used to display the properties of a {@link MediaFile}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class MediaPropertiesPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 4043952099167166737L;
	
	/** The time format */
	private static final NumberFormat TIME_FORMAT = new DecimalFormat("00");
	
	/** The media file information */
	private MediaFile file;
	
	// controls
	
	/** The properties main label */
	private JLabel lblProperties;
	
	/** The main label to properties separator */
	private JSeparator sepProperties;
	
	/** The properties to specific properties separator */
	private JSeparator sepSpecifics;
	
	// labels
	
	/** The name label */
	private JLabel lblName;
	
	/** The path label */
	private JLabel lblPath;
	
	/** The size label */
	private JLabel lblSize;
	
	/** The format label */
	private JLabel lblFormat;
	
	/** The image/video width label */
	private JLabel lblWidth;
	
	/** The image/video height label */
	private JLabel lblHeight;
	
	/** The video/audio length label */
	private JLabel lblLength;
	
	/** The video has audio label */
	private JLabel lblHasAudio;
	
	// fields
	
	/** The name text box */
	private JTextField txtName;
	
	/** The path text box */
	private JTextField txtPath;
	
	/** The size text box */
	private JTextField txtSize;
	
	/** The format text box */
	private JTextField txtFormat;
	
	/** The image/video width text box */
	private JTextField txtWidth;
	
	/** The image/video height text box */
	private JTextField txtHeight;
	
	/** The video/audio length text box */
	private JTextField txtLength;
	
	/** The video has audio check box */
	private JCheckBox chkHasAudio;
	
	/**
	 * Default constructor.
	 */
	public MediaPropertiesPanel() {
		this.lblProperties = new JLabel(Messages.getString("panel.media.properties"));
		this.sepProperties = new JSeparator();
		this.sepSpecifics = new JSeparator();
		
		this.lblName = new JLabel(Messages.getString("panel.media.properties.name"));
		this.txtName = new JTextField();
		this.txtName.setEditable(false);
		this.txtName.setToolTipText(Messages.getString("panel.media.properties.name.tooltip"));
		
		this.lblPath = new JLabel(Messages.getString("panel.media.properties.path"));
		this.txtPath = new JTextField();
		this.txtPath.setEditable(false);
		this.txtPath.setToolTipText(Messages.getString("panel.media.properties.path.tooltip"));
		
		this.lblSize = new JLabel(Messages.getString("panel.media.properties.size"));
		this.txtSize = new JTextField();
		this.txtSize.setEditable(false);
		this.txtSize.setToolTipText(Messages.getString("panel.media.properties.size.tooltip"));
		
		this.lblFormat = new JLabel(Messages.getString("panel.media.properties.format"));
		this.txtFormat = new JTextField();
		this.txtFormat.setEditable(false);
		this.txtFormat.setToolTipText(Messages.getString("panel.media.properties.format.tooltip"));
		
		this.lblWidth = new JLabel(Messages.getString("panel.media.properties.width"));
		this.txtWidth = new JTextField();
		this.txtWidth.setEditable(false);
		this.txtWidth.setToolTipText(Messages.getString("panel.media.properties.width.tooltip"));
		
		this.lblHeight = new JLabel(Messages.getString("panel.media.properties.height"));
		this.txtHeight = new JTextField();
		this.txtHeight.setEditable(false);
		this.txtHeight.setToolTipText(Messages.getString("panel.media.properties.height.tooltip"));
		
		this.lblLength = new JLabel(Messages.getString("panel.media.properties.length"));
		this.txtLength = new JTextField();
		this.txtLength.setEditable(false);
		this.txtLength.setToolTipText(Messages.getString("panel.media.properties.length.tooltip"));
		
		this.lblHasAudio = new JLabel(Messages.getString("panel.media.properties.audio"));
		this.chkHasAudio = new JCheckBox();
		this.chkHasAudio.setSelected(false);
		this.chkHasAudio.setEnabled(false);
		this.chkHasAudio.setToolTipText(Messages.getString("panel.media.properties.audio.tooltip"));
		
		// size the labels
		ComponentUtilities.setMinimumSize(
				this.lblName,
				this.lblPath,
				this.lblSize,
				this.lblFormat,
				this.lblWidth,
				this.lblHeight,
				this.lblLength,
				this.lblHasAudio);
		
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
								.addComponent(this.lblSize)
								.addComponent(this.lblFormat))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtName)
								.addComponent(this.txtPath)
								.addComponent(this.txtSize)
								.addComponent(this.txtFormat)))
				.addComponent(this.sepSpecifics)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblWidth)
								.addComponent(this.lblHeight)
								.addComponent(this.lblLength)
								.addComponent(this.lblHasAudio))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtWidth)
								.addComponent(this.txtHeight)
								.addComponent(this.txtLength)
								.addComponent(this.chkHasAudio))));
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
						.addComponent(this.txtSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblFormat)
						.addComponent(this.txtFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.sepSpecifics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblWidth)
						.addComponent(this.txtWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblHeight)
						.addComponent(this.txtHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLength)
						.addComponent(this.txtLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblHasAudio)
						.addComponent(this.chkHasAudio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// initially hide any specific properties
		this.toggleVisibility(-1);
	}
	
	/**
	 * Sets the media file.
	 * @param file the media file
	 */
	public void setMediaFile(MediaFile file) {
		this.file = file;
		
		if (file != null) {
			this.txtName.setText(file.getName());
			this.txtName.setCaretPosition(0);
			this.txtPath.setText(file.getPath());
			this.txtPath.setCaretPosition(0);
			this.txtSize.setText(String.valueOf(file.getSize()));
			this.txtSize.setCaretPosition(0);
			this.txtFormat.setText(file.getFormat());
			this.txtFormat.setCaretPosition(0);
			
			if (file instanceof ImageMediaFile) {
				ImageMediaFile iFile = (ImageMediaFile)file;
				this.txtWidth.setText(String.valueOf(iFile.getWidth()));
				this.txtWidth.setCaretPosition(0);
				this.txtHeight.setText(String.valueOf(iFile.getHeight()));
				this.txtHeight.setCaretPosition(0);
				this.toggleVisibility(0);
			}
			
			if (file instanceof VideoMediaFile) {
				VideoMediaFile vFile = (VideoMediaFile)file;
				this.txtWidth.setText(String.valueOf(vFile.getWidth()));
				this.txtWidth.setCaretPosition(0);
				this.txtHeight.setText(String.valueOf(vFile.getHeight()));
				this.txtHeight.setCaretPosition(0);
				this.txtLength.setText(this.getLengthFormattedString(vFile.getLength()));
				this.txtLength.setCaretPosition(0);
				this.chkHasAudio.setSelected(vFile.isAudioPresent());
				this.toggleVisibility(1);
			}
			
			if (file instanceof AudioMediaFile) {
				AudioMediaFile aFile = (AudioMediaFile)file;
				this.txtLength.setText(this.getLengthFormattedString(aFile.getLength()));
				this.txtLength.setCaretPosition(0);
				this.toggleVisibility(2);
			}
		} else {
			this.txtName.setText("");
			this.txtPath.setText("");
			this.txtSize.setText("");
			this.txtFormat.setText("");
			this.toggleVisibility(-1);
		}
	}
	
	/**
	 * Toggles the visibility of the specific media file controls
	 * give the media type.
	 * <p>
	 * Use -1 to indicate null or initial state, 0 for image media,
	 * 1 for video media, and 2 for audio media.
	 * @param type the media type
	 */
	private void toggleVisibility(int type) {
		if (type == -1) { // initial/null
			this.lblHasAudio.setVisible(false);
			this.chkHasAudio.setVisible(false);
			this.lblHeight.setVisible(false);
			this.txtHeight.setVisible(false);
			this.lblWidth.setVisible(false);
			this.txtWidth.setVisible(false);
			this.lblLength.setVisible(false);
			this.txtLength.setVisible(false);
		} else if (type == 0) { // image
			this.txtWidth.setVisible(true);
			this.lblWidth.setVisible(true);
			this.txtHeight.setVisible(true);
			this.lblHeight.setVisible(true);
			this.txtLength.setVisible(false);
			this.lblLength.setVisible(false);
			this.lblHasAudio.setVisible(false);
			this.chkHasAudio.setVisible(false);
		} else if (type == 1) { // video
			this.txtWidth.setVisible(true);
			this.lblWidth.setVisible(true);
			this.txtHeight.setVisible(true);
			this.lblHeight.setVisible(true);
			this.txtLength.setVisible(true);
			this.lblLength.setVisible(true);
			this.lblHasAudio.setVisible(true);
			this.chkHasAudio.setVisible(true);
		} else if (type == 2) { // audio
			this.txtWidth.setVisible(false);
			this.lblWidth.setVisible(false);
			this.txtHeight.setVisible(false);
			this.lblHeight.setVisible(false);
			this.txtLength.setVisible(true);
			this.lblLength.setVisible(true);
			this.lblHasAudio.setVisible(false);
			this.chkHasAudio.setVisible(false);
		}
	}
	
	/**
	 * Returns a formated time string for the given length.
	 * @param length the length in seconds
	 * @return String
	 */
	private String getLengthFormattedString(long length) {
		long hours = length / 3600;
		long minutes = (length % 3600) / 60;
		long seconds = (length % 3600) % 60;
		return TIME_FORMAT.format(hours) + ":" + TIME_FORMAT.format(minutes) + ":" + TIME_FORMAT.format(seconds);
	}
	
	/**
	 * Returns the current media file.
	 * @return {@link MediaFile}
	 */
	public MediaFile getMediaFile() {
		return this.file;
	}
}
