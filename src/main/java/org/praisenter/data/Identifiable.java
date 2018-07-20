package org.praisenter.data;

import java.util.UUID;

public interface Identifiable {
	public UUID getId();
	public boolean identityEquals(Object other);
}
