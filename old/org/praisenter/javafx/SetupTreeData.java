package org.praisenter.javafx;

public final class SetupTreeData {
	private final String name;
	private final String label;
	
	public SetupTreeData(String name, String label) {
		this.name = name;
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}

	public String getName() {
		return name;
	}
	
	public String getLabel() {
		return label;
	}
	
}
