package org.praisenter.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.SingleFileManager;
import org.praisenter.data.workspace.WorkspaceManager;
import org.praisenter.data.workspace.WorkspaceReference;
import org.praisenter.data.workspace.Workspaces;
import org.praisenter.utility.RuntimeProperties;
import org.praisenter.utility.StringManipulator;

import com.plexteq.ssb.nativeimpl.SecurityScopedBookmarks;

final class WorkspaceInitializer {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final SingleFileManager<Workspaces> workspacesManager;
	
	public WorkspaceInitializer(SingleFileManager<Workspaces> workspacesManager) {
		this.workspacesManager = workspacesManager;
	}
	
	public WorkspaceManager initializeWorkspace(WorkspaceReference wr) throws WorkspaceInitializationException {
		// make sure the reference is not null
		if (wr == null)
			throw new WorkspaceInitializationException();
		
		// make sure the location is not null
		Path path = wr.getPath();
		if (path == null)
			throw new WorkspaceInitializationException();
		
		// once someone tries to launch, check if the bookmark exists
		// if it doesn't, then assume that they selected the folder
		// and we need to create the security scoped bookmark
		if (RuntimeProperties.IS_MAC_OS) {
			String token = wr.getSecurityToken();
			if (StringManipulator.isNullOrEmpty(token)) {
				LOGGER.info("Creating security scoped bookmark for workspace: '" + path.toAbsolutePath() + "'");
				try {
					token = SecurityScopedBookmarks.createBookmarkImpl(path.toUri().toString());
					wr.setSecurityToken(token);
					LOGGER.info("Security scoped bookmark created successfully for workspace: '" + path.toAbsolutePath() + "'");
				} catch (Exception ex) {
					LOGGER.error("Failed to create bookmark for workspace folder '" + path.toAbsolutePath() + "'", ex);
					throw new WorkspaceInitializationException(ex);
				} catch (UnsatisfiedLinkError ex) {
					LOGGER.error("Failed to create bookmark for workspace folder '" + path.toAbsolutePath() + "'", ex);
					throw new WorkspaceInitializationException(ex);
				}
			} else {
				try {
	    			LOGGER.info("Acquiring security scoped bookmark for workspace '" + path.toAbsolutePath() + "'");
	    			SecurityScopedBookmarks.startResourceAccessingImpl(token);
	    			LOGGER.info("Security scoped bookmark for workspace '" + path.toAbsolutePath() + "' acquired successfully");
	    		} catch (Exception ex) {
	    			LOGGER.error("Failed to acquire the security scoped bookmark for workspace: '" + path.toAbsolutePath() + "'", ex);
					throw new WorkspaceInitializationException(ex);
	    		} catch (UnsatisfiedLinkError ex) {
	    			LOGGER.error("Failed to acquire the security scoped bookmark for workspace: '" + path.toAbsolutePath() + "'", ex);
					throw new WorkspaceInitializationException(ex);
				}
			}
		}
		
		try {
			// get all the other workspaces
			Set<WorkspaceReference> otherWorkspaces = workspacesManager.getData().getWorkspaces()
					.stream()
					.filter(s -> !s.equals(wr))
					.collect(Collectors.toSet());
			
			// build the workspace manager and initialize the workspace
			WorkspaceManager wsm = WorkspaceManager.open(wr, otherWorkspaces);
			LOGGER.info("Workspace '" + path.toAbsolutePath() + "' was opened successfully.");
			
			return wsm;
		} catch (IOException ex) {
			throw new WorkspaceInitializationException(ex);	
		} catch (Exception ex) {
			throw new WorkspaceInitializationException(ex);
		}
	}
}
