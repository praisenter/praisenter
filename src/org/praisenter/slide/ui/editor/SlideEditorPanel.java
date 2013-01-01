package org.praisenter.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.bind.JAXBException;

import org.praisenter.resources.Messages;
import org.praisenter.slide.GenericComponent;
import org.praisenter.slide.NotificationSlide;
import org.praisenter.slide.PositionedComponent;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.Resolution;
import org.praisenter.slide.Resolutions;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to edit a slide.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// FIXME check for media support
// FIXME add special logic for notification templates
// FIXME we may want to allow them to choose the target display during the slide edit process (or display size)
// TODO snap to grid
public class SlideEditorPanel extends JPanel implements MouseMotionListener, MouseListener, ListSelectionListener, EditorListener, ActionListener, ItemListener {
	/** The version id */
	private static final long serialVersionUID = -927595042247907332L;

	/** The resize gap (the additional gap area to allow resizing) */
	private static final int RESIZE_GAP = 50;
	
	/** The generic editor panel card */
	private static final String GENERIC_CARD = "Generic";
	
	/** The text editor panel card */
	private static final String TEXT_CARD = "Text";
	
	/** The image editor panel card */
	private static final String IMAGE_CARD = "Image";
	
	/** The video editor panel card */
	private static final String VIDEO_CARD = "Video";
	
	/** The audio editor panel card */
	private static final String AUDIO_CARD = "Audio";
	
	/** A blank card */
	private static final String BLANK_CARD = "Blank";
	
	// input
	
	/** The size of the display */
	protected Dimension displaySize;

	/** The slide being edited */
	protected Slide slide;

	// components
	
	/** The slide/template name */
	protected JTextField txtSlideName;
	
	/** The list resolution targets */
	protected JComboBox<Resolution> cmbResolutionTargets;
	
	/** A button to add custom resolutions */
	protected JButton btnAddCustomResolution;
	
	/** The list of components */
	protected JList<SlideComponent> lstComponents;
	
	/** The panel used to preview the changes */
	protected SlideEditorPreviewPanel pnlSlidePreview;
	
	/** Button to move the component back */
	protected JButton btnMoveBack;
	
	/** Button to move the component forward */
	protected JButton btnMoveForward;

	/** Button to remove the component */
	protected JButton btnRemoveComponent;
	
	/** Button to add a new component */
	protected JButton btnAddComponent;
	
	// editor panels
	
	/** Generic editor panel for {@link GenericComponent}s */
	protected GenericComponentEditorPanel<GenericComponent> pnlGeneric;
	
	/** Text editor panel for {@link TextComponent}s */
	protected TextComponentEditorPanel pnlText;
	
	/** Image editor panel for {@link ImageMediaComponent}s */
	protected ImageMediaComponentEditorPanel pnlImage;
	
	/** Video editor panel for {@link VideoMediaComponent}s */
	protected VideoMediaComponentEditorPanel pnlVideo;
	
	/** Panel for the editor panels */
	protected JPanel pnlEditorCards;
	
	/** The layout for the editor panel */
	protected CardLayout layEditorCards;
	
	// mouse commands
	
	/** Mouse command for moving components */
	private MoveCommand moveCommand = new MoveCommand();
	
	/** Mouse command for resizing components */
	private ResizeCommand resizeCommand = new ResizeCommand();
	
	/** Mouse command for resizing the width of components */
	private ResizeWidthCommand resizeWidthCommand = new ResizeWidthCommand();
	
	/** Mouse command for resizing the height of components */
	private ResizeHeightCommand resizeHeightCommand = new ResizeHeightCommand();
	
	/** Mouse command mutually exclusive group */
	private MutexCommandGroup<BoundsCommand> mouseCommandGroup = new MutexCommandGroup<BoundsCommand>(new BoundsCommand[] {
			this.moveCommand,
			this.resizeCommand,
			this.resizeWidthCommand,
			this.resizeHeightCommand
	});
	
	/**
	 * Minimal constructor.
	 * @param slide the slide to edit
	 * @param displaySize the display target size
	 */
	@SuppressWarnings("serial")
	public SlideEditorPanel(Slide slide, Dimension displaySize) {
		this.slide = slide;
		this.displaySize = displaySize;
		
		int dsw = this.displaySize.width / 2;
		int dsh = this.displaySize.height / 2;
		
		// setup the preview panel
		Dimension previewSize = new Dimension(dsw, dsh);
		this.pnlSlidePreview = new SlideEditorPreviewPanel();
		this.pnlSlidePreview.setSlide(this.slide);
		this.pnlSlidePreview.setMinimumSize(previewSize);
		this.pnlSlidePreview.setPreferredSize(previewSize);
		this.pnlSlidePreview.addMouseListener(this);
		this.pnlSlidePreview.addMouseMotionListener(this);
		
		int width = slide.getWidth();
		int height = slide.getHeight();
		if (slide instanceof NotificationSlide) {
			NotificationSlide nSlide = (NotificationSlide)slide;
			width = nSlide.getDeviceWidth();
			height = nSlide.getDeviceHeight();
		}
		
		this.txtSlideName = new JTextField(slide.getName()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.slide.editor.name"));
			}
		};
		
		// get the resolutions
		List<Resolution> resolutions = Resolutions.getResolutions();
		// add any window sizes to the targets
		GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
		for (int i = 0; i < devices.length; i++) {
			GraphicsDevice device = devices[i];
			DisplayMode mode = device.getDisplayMode();
			Resolution resolution = new Resolution(mode.getWidth(), mode.getHeight());
			// see if it already exists in the list
			boolean found = false;
			for (Resolution r : resolutions) {
				if (r.equals(resolution)) {
					found = true;
					break;
				}
			}
			if (!found) {
				resolutions.add(resolution);
			}
		}
		// make sure they are sorted
		Collections.sort(resolutions);
		// create the targets drop down
		this.cmbResolutionTargets = new JComboBox<Resolution>(resolutions.toArray(new Resolution[0]));
		this.cmbResolutionTargets.setSelectedItem(new Resolution(width, height));
		this.cmbResolutionTargets.setToolTipText(Messages.getString("panel.slide.editor.resolution"));
		this.cmbResolutionTargets.addItemListener(this);
		
		this.btnAddCustomResolution = new JButton(Messages.getString("panel.slide.editor.resolution.custom.add"));
		this.btnAddCustomResolution.setToolTipText(Messages.getString("panel.slide.editor.resolution.custom.add.tooltip"));
		this.btnAddCustomResolution.addActionListener(this);
		this.btnAddCustomResolution.setActionCommand("add-resolution");
		
		JLabel lblComponents = new JLabel(Messages.getString("panel.slide.editor.components"));
		
		// get all the components on the slide/template
		List<SlideComponent> components = slide.getComponents(SlideComponent.class);
		// add in the background component
		SlideComponent background = slide.getBackground();
		if (background != null) {
			components.add(0, slide.getBackground());
		}
		this.lstComponents = new JList<SlideComponent>(components.toArray(new SlideComponent[0]));
		this.lstComponents.setCellRenderer(new SlideComponentListCellRenderer());
		this.lstComponents.addListSelectionListener(this);
		JScrollPane scrComponents = new JScrollPane(this.lstComponents);
		scrComponents.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrComponents.setMinimumSize(new Dimension(0, 100));
		
		this.btnRemoveComponent = new JButton(Messages.getString("panel.slide.editor.remove"));
		this.btnRemoveComponent.addActionListener(this);
		this.btnRemoveComponent.setActionCommand("remove");
		this.btnRemoveComponent.setEnabled(false);
		
		this.btnMoveBack = new JButton(Messages.getString("panel.slide.editor.moveBack"));
		this.btnMoveBack.addActionListener(this);
		this.btnMoveBack.setActionCommand("moveBack");
		this.btnMoveBack.setEnabled(false);
		
		this.btnMoveForward = new JButton(Messages.getString("panel.slide.editor.moveForward"));
		this.btnMoveForward.addActionListener(this);
		this.btnMoveForward.setActionCommand("moveForward");
		this.btnMoveForward.setEnabled(false);
		
		this.btnAddComponent = new JButton(Messages.getString("panel.slide.editor.add"));
		this.btnAddComponent.addActionListener(this);
		this.btnAddComponent.setActionCommand("add");
		
		JPanel pnlButtonGrid = new JPanel();
		pnlButtonGrid.setLayout(new GridLayout(2, 2));
		pnlButtonGrid.add(this.btnMoveBack);
		pnlButtonGrid.add(this.btnMoveForward);
		pnlButtonGrid.add(this.btnRemoveComponent);
		pnlButtonGrid.add(this.btnAddComponent);
		
		JPanel pnlComponentLists = new JPanel();
		GroupLayout clLayout = new GroupLayout(pnlComponentLists);
		pnlComponentLists.setLayout(clLayout);
		
		clLayout.setAutoCreateGaps(true);
		clLayout.setAutoCreateContainerGaps(true);
		clLayout.setHorizontalGroup(clLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.txtSlideName)
				.addGroup(clLayout.createSequentialGroup()
						.addComponent(this.cmbResolutionTargets)
						.addComponent(this.btnAddCustomResolution))
				.addComponent(lblComponents)
				.addComponent(scrComponents, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlButtonGrid));
		clLayout.setVerticalGroup(clLayout.createSequentialGroup()
				.addComponent(this.txtSlideName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(clLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.cmbResolutionTargets, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnAddCustomResolution, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(lblComponents)
				.addComponent(scrComponents)
				.addComponent(pnlButtonGrid, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		this.pnlGeneric = new GenericComponentEditorPanel<>();
		this.pnlGeneric.addEditorListener(this);
		
		this.pnlText = new TextComponentEditorPanel();
		this.pnlText.addEditorListener(this);
		
		this.pnlImage = new ImageMediaComponentEditorPanel();
		this.pnlImage.addEditorListener(this);
		
		this.pnlVideo = new VideoMediaComponentEditorPanel();
		this.pnlVideo.addEditorListener(this);
		
		JPanel pnlBlank = new JPanel();
		pnlBlank.setLayout(new BorderLayout());
		JLabel lblSelect = new JLabel(Messages.getString("panel.slide.editor.selectComponent"));
		lblSelect.setAlignmentX(CENTER_ALIGNMENT);
		lblSelect.setHorizontalAlignment(SwingConstants.CENTER);
		Font font = lblSelect.getFont();
		lblSelect.setFont(font.deriveFont(Font.BOLD, 1.5f * font.getSize()));
		pnlBlank.add(lblSelect);
		
		this.pnlEditorCards = new JPanel();
		this.layEditorCards = new CardLayout();
		this.pnlEditorCards.setLayout(this.layEditorCards);
		
		this.pnlEditorCards.add(pnlBlank, BLANK_CARD);
		this.pnlEditorCards.add(this.pnlGeneric, GENERIC_CARD);
		this.pnlEditorCards.add(this.pnlText, TEXT_CARD);
		this.pnlEditorCards.add(this.pnlImage, IMAGE_CARD);
		this.pnlEditorCards.add(this.pnlVideo, VIDEO_CARD);
		
		JSplitPane splEditorControls = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splEditorControls.setTopComponent(pnlComponentLists);
		splEditorControls.setBottomComponent(this.pnlEditorCards);
		
		JSplitPane splPreviewEditor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splPreviewEditor.setLeftComponent(splEditorControls);
		splPreviewEditor.setRightComponent(this.pnlSlidePreview);
		
		this.setLayout(new BorderLayout());
		this.add(splPreviewEditor);
	}
	
	/**
	 * Returns the top level component that is at the given point.
	 * @param point the Slide space point
	 * @return {@link PositionedComponent}
	 */
	protected PositionedComponent getComponentAtPoint(Point point) {
		if (this.slide != null) {
			List<PositionedComponent> components = this.slide.getComponents(PositionedComponent.class);
			// reverse the list so that we find the highest component first
			Collections.reverse(components);
			for (PositionedComponent component : components) {
				if (this.isInside(point, component)) {
					return component;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns true if the given point is inside any component on the slide.
	 * @param point the Slide space point
	 * @return boolean
	 */
	protected boolean isInside(Point point) {
		if (this.slide != null) {
			List<PositionedComponent> components = this.slide.getComponents(PositionedComponent.class);
			for (PositionedComponent component : components) {
				if (this.isInside(point, component)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the given point is inside the given component.
	 * @param point the Slide space point
	 * @param component the component
	 * @return boolean
	 */
	protected boolean isInside(Point point, PositionedComponent component) {
		// get the bounds
		Shape bounds = component.getBounds();
		// see if the bounds contains the point
		return bounds.contains(point);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		if (!this.mouseCommandGroup.isCommandActive()) {
			this.setCursor(Cursor.getDefaultCursor());
			this.pnlSlidePreview.setMouseOverComponent(null);
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// check for mouse button 1
		if (e.getButton() == MouseEvent.BUTTON1) {
			// make sure no other command is active
			if (!this.mouseCommandGroup.isCommandActive()) {
				// convert the point to display space
				Point point = this.pnlSlidePreview.getSlideSpacePoint(e.getPoint());
				PositionedComponent cMouseOver = this.pnlSlidePreview.getMouseOverComponent();
				PositionedComponent cSelected = this.pnlSlidePreview.getSelectedComponent();
				PositionedComponent component = null;
				// see if we are still over the same component
				if (cSelected != null && this.isInside(point, cSelected)) {
					// then set the component as the one we are still over
					component = cSelected;
				} else if (cMouseOver != null && this.isInside(point, cMouseOver)) {
					component = cMouseOver;
				} else {
					// otherwise see if we are over a different one
					component = this.getComponentAtPoint(point);
				}
				// make sure we found one
				if (component != null) {
					// get the bounds
					Rectangle bounds = component.getRectangleBounds();
					int rx = bounds.x + bounds.width - RESIZE_GAP;
					int ry = bounds.y + bounds.height - RESIZE_GAP;
					if (point.x >= rx && point.y >= ry) {
						// resize both dimensions
						this.resizeCommand.begin(point, component);
					} else if (point.x >= rx) {
						// resize width
						this.resizeWidthCommand.begin(point, component);
					} else if (point.y >= ry) {
						// resize height
						this.resizeHeightCommand.begin(point, component);
					} else {
						// move
						this.moveCommand.begin(point, component);
					}
					this.pnlSlidePreview.setSelectedComponent(component);
					this.lstComponents.setSelectedValue(component, true);
					this.pnlSlidePreview.repaint();
				} else {
					this.pnlSlidePreview.setSelectedComponent(null);
					this.lstComponents.clearSelection();
					this.pnlSlidePreview.repaint();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// check for mouse button 1
		if (e.getButton() == MouseEvent.BUTTON1) {
			// end all the mouse button 1 commands
			this.moveCommand.end();
			this.resizeCommand.end();
			this.resizeWidthCommand.end();
			this.resizeHeightCommand.end();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// see if any mouse command is active
		if (this.mouseCommandGroup.isCommandActive()) {
			// convert the point to display space
			Point end = this.pnlSlidePreview.getSlideSpacePoint(e.getPoint());
			// check if we are still over a component
			if (this.moveCommand.isActive()) {
				// move the component
				this.moveCommand.update(end);
			} else if (this.resizeCommand.isActive()) {
				this.resizeCommand.update(end);
			} else if (this.resizeWidthCommand.isActive()) {
				this.resizeWidthCommand.update(end);
			} else if (this.resizeHeightCommand.isActive()) {
				this.resizeHeightCommand.update(end);
			}
			// if we modify a component then we need to redraw it
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// make sure no other command is active in this group
		if (!this.mouseCommandGroup.isCommandActive()) {
			// convert the point to display space
			Point point = this.pnlSlidePreview.getSlideSpacePoint(e.getPoint());
			PositionedComponent cMouseOver = this.pnlSlidePreview.getMouseOverComponent();
			PositionedComponent cSelected = this.pnlSlidePreview.getSelectedComponent();
			PositionedComponent component = null;
			// see if we are still over the same component
			if (cSelected != null && this.isInside(point, cSelected)) {
				// then set the component as the one we are still over
				component = cSelected;
			} else {
				// otherwise see if we are over a different one
				component = this.getComponentAtPoint(point);
			}
			// make sure we found one
			if (component != null) {
				// set the hover component
				if (component != cMouseOver) {
					this.pnlSlidePreview.setMouseOverComponent(component);
					this.repaint();
				}
				// get the bounds
				Rectangle bounds = component.getRectangleBounds();
				int rx = bounds.x + bounds.width - RESIZE_GAP;
				int ry = bounds.y + bounds.height - RESIZE_GAP;
				if (point.x >= rx && point.y >= ry) {
					// resize both dimensions
					this.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				} else if (point.x >= rx) {
					// resize width
					this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				} else if (point.y >= ry) {
					// resize height
					this.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				} else {
					// then just show the move command
					this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			} else {
				this.repaint();
				this.pnlSlidePreview.setMouseOverComponent(null);
				this.setCursor(Cursor.getDefaultCursor());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("remove".equals(command)) {
			// FIXME implement
		} else if ("add".equals(command)) {
			// FIXME implement
		} else if ("moveBack".equals(command)) {
			SlideComponent component = this.lstComponents.getSelectedValue();
			if (component != this.slide.getBackground() && component instanceof RenderableComponent) {
				RenderableComponent rc = (RenderableComponent)component;
				this.slide.moveComponentDown(rc);
				this.pnlSlidePreview.repaint();
			}
		} else if ("moveForward".equals(command)) {
			SlideComponent component = this.lstComponents.getSelectedValue();
			if (component != this.slide.getBackground() && component instanceof RenderableComponent) {
				RenderableComponent rc = (RenderableComponent)component;
				this.slide.moveComponentUp(rc);
				this.pnlSlidePreview.repaint();
			}
		} else if ("add-resolution".equals(command)) {
			// show the custom resolution dialog
			Resolution resolution = CustomResolutionDialog.show(WindowUtilities.getParentWindow(this));
			if (resolution != null) {
				try {
					// attempt to add it to the list
					Resolutions.addResolution(resolution);
					// see where it needs to go in the list of resolutions
					int n = this.cmbResolutionTargets.getItemCount();
					for (int i = 0; i < n; i++) {
						Resolution r = this.cmbResolutionTargets.getItemAt(i);
						if (r.equals(resolution)) {
							return;
						}
						if (resolution.compareTo(r) < 0) {
							this.cmbResolutionTargets.insertItemAt(resolution, i);
							return;
						}
					}
					this.cmbResolutionTargets.addItem(resolution);
				} catch (JAXBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Object source = e.getSource();
			if (source == this.lstComponents) {
				SlideComponent component = this.lstComponents.getSelectedValue();
				if (component != null && component != this.slide.getBackground()) {
					if (component instanceof TextComponent) {
						this.pnlText.setSlideComponent((TextComponent)component);
						this.layEditorCards.show(this.pnlEditorCards, TEXT_CARD);
					} else if (component instanceof ImageMediaComponent) {
						this.pnlImage.setSlideComponent((ImageMediaComponent)component);
						this.layEditorCards.show(this.pnlEditorCards, IMAGE_CARD);
					} else if (component instanceof VideoMediaComponent) {
						this.pnlVideo.setSlideComponent((VideoMediaComponent)component);
						this.layEditorCards.show(this.pnlEditorCards, VIDEO_CARD);
					} else if (component instanceof AudioMediaComponent) {
						// FIXME implement
					} else if (component instanceof GenericComponent) {
						this.pnlGeneric.setSlideComponent((GenericComponent)component);
						this.layEditorCards.show(this.pnlEditorCards, GENERIC_CARD);
					} else {
						// show the blank card with a "select a control label"
						this.layEditorCards.show(this.pnlEditorCards, BLANK_CARD);
					}
					
					if (component instanceof PositionedComponent) {
						this.pnlSlidePreview.setSelectedComponent((PositionedComponent)component);
					} else {
						this.pnlSlidePreview.setSelectedComponent(null);
					}
					
					this.btnMoveBack.setEnabled(true);
					this.btnMoveForward.setEnabled(true);
					this.btnRemoveComponent.setEnabled(true);
				} else {
					this.pnlSlidePreview.setSelectedComponent(null);
					// FIXME show the background editor panel
					this.layEditorCards.show(this.pnlEditorCards, BLANK_CARD);
					
					this.btnMoveBack.setEnabled(false);
					this.btnMoveForward.setEnabled(false);
					this.btnRemoveComponent.setEnabled(false);
				}
				this.pnlSlidePreview.repaint();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.EditorListener#editPerformed(org.praisenter.slide.ui.editor.EditEvent)
	 */
	@Override
	public void editPerformed(EditEvent event) {
		this.pnlSlidePreview.repaint();
		this.lstComponents.repaint();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbResolutionTargets) {
				Resolution resolution = (Resolution)e.getItem();
				if (this.slide != null) {
					// adjust the current slide
					this.slide.adjustSize(resolution.getWidth(), resolution.getHeight());
					this.pnlSlidePreview.repaint();
				}
			}
		}
	}
}
