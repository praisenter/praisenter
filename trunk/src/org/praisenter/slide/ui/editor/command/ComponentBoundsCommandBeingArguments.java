package org.praisenter.slide.ui.editor.command;

import java.awt.Point;

import org.praisenter.command.Command;
import org.praisenter.slide.PositionedComponent;

/**
 * Class used as input for the {@link Command#begin(Object)} method for {@link BoundsCommand}s for {@link PositionedComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ComponentBoundsCommandBeingArguments extends AbstractBoundsCommandBeginArguments implements BoundsCommandBeginArguments {
	/** The component */
	protected PositionedComponent component;
	
	/**
	 * Full constructor.
	 * @param start the start point in slide space
	 * @param component the component
	 */
	public ComponentBoundsCommandBeingArguments(Point start, PositionedComponent component) {
		super(start);
		this.component = component;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#translate(int, int)
	 */
	@Override
	public void translate(int dx, int dy) {
		this.component.translate(dx, dy);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#resize(int, int)
	 */
	@Override
	public void resize(int dw, int dh) {
		this.component.resize(dw, dh);
	}
	
	/**
	 * Returns the component.
	 * @return {@link PositionedComponent}
	 */
	public PositionedComponent getComponent() {
		return this.component;
	}
}
