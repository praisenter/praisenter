package org.praisenter.ui.undo;

import java.util.List;

interface Registration {
	public Object getTarget();
	public List<?> getDependents();
	public void unbind();
}
