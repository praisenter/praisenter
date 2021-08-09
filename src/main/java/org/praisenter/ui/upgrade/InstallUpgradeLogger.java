package org.praisenter.ui.upgrade;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.praisenter.Constants;

final class InstallUpgradeLogger {
	private final Path upgradeLogFile = this.getUpgradeLogFile();

	public final void fatal(String message) {
		this.log("FATAL", message);
	}
	
	public final void error(String message) {
		this.log("ERROR", message);
	}
	
	public final void warn(String message) {
		this.log("WARN ", message);
	}
	
	public final void info(String message) {
		this.log("INFO ", message);
	}
	
	public final void debug(String message) {
		this.log("DEBUG", message);
	}

	public final void trace(String message) {
		this.log("TRACE", message);
	}
	
	private final void log(String level, String message) {
		try (FileWriter fw = new FileWriter(upgradeLogFile.toFile(), true);
			 BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(level);
			bw.write(" ");
			bw.write(message);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final Path getUpgradeLogFile() {
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		String name = now.toString().replaceAll("-|:", "");
		return Paths.get(Constants.UPGRADE_ABSOLUTE_PATH, "upgrade" + name + ".log");
	}
	
}
