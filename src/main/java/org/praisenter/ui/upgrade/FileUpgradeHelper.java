package org.praisenter.ui.upgrade;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class FileUpgradeHelper {
	private final Path archiveFolder;
	
	public FileUpgradeHelper(Path archiveFolder) {
		this.archiveFolder = archiveFolder;
	}
	
	public void archiveFile(Path path) throws IOException {
		this.archiveFile(path, null);
	}
	
	public void archiveFile(Path path, String targetFolder) throws IOException {
		Path target = this.archiveFolder;
		if (targetFolder != null) {
			target = this.archiveFolder.resolve(targetFolder);
		}
		Files.createDirectories(target);
		Path to = target.resolve(path.getFileName().toString());
		Files.copy(path, to, StandardCopyOption.REPLACE_EXISTING);
	}

	public boolean isFileContentIdentical(Path path, String classpath) throws NoSuchAlgorithmException, IOException {
		try (FileInputStream fis = new FileInputStream(path.toFile());
			 InputStream is = InstallUpgradeHandler.class.getResourceAsStream(classpath)) {
			return this.isFileContentIdentical(fis, is);
		}
	}
	
	public boolean isFileContentIdentical(InputStream is1, InputStream is2) throws IOException, NoSuchAlgorithmException {
		// get a hash of the file
		String hash1 = hash(is1, "SHA-256");
		String hash2 = hash(is2, "SHA-256");
		return hash1.equals(hash2);
	}
	
	private static final String hash(InputStream stream, String algorithm) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		DigestInputStream dis = new DigestInputStream(stream, md);

		byte[] data = new byte[4096];
		while (dis.read(data, 0, data.length) != -1) {}
		
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
		    sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return sb.toString();
	}
}
