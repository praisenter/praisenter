package org.praisenter.xml.adapters;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class PathXmlAdapter extends XmlAdapter<String, Path> {

	@Override
	public String marshal(Path path) throws Exception {
		return path != null ? path.toAbsolutePath().toString() : "";
	}

	@Override
	public Path unmarshal(String path) throws Exception {
		return Paths.get(path);
	}
}
