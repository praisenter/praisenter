package org.praisenter.ui.upgrade;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.praisenter.Constants;
import org.praisenter.Version;

public final class UpgradeChecker {
	public CompletableFuture<Version> getLatestReleaseVersion() {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(new URI(Constants.UPGRADE_VERSION_CHECK_URL))
			    .build();
			
			return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply((response) -> {
				if (response.statusCode() >= 200 &&
					response.statusCode() < 300) {
					Version version = Version.parse(response.body().trim());
					return version;
				} else {
					throw new CompletionException(new Exception(response.body()));
				}
			});
		} catch (Exception e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}
