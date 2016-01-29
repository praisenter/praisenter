package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fill")
public interface Fill {

	public abstract Paint getPaint(int x, int y, int w, int h);
}
