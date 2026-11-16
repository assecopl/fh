package pl.fhframework.dp.commons.ds.repository.exception;

/**
 * Sygnalizuje, że dokument w repozytorium ma inną wersję niż oczekiwana przez zapis - typowo dlatego,
 * że równolegle zapisała go inna operacja. Wołający powinien odczytać dokument ponownie i powtórzyć zmianę.
 */
public class VersionMismatchRuntimeException extends RuntimeException {

	private final String id;
	private final int expectedVersion;

	public VersionMismatchRuntimeException(String id, int expectedVersion) {
		super("Document version is not " + expectedVersion);
		this.id = id;
		this.expectedVersion = expectedVersion;
	}

	public String getId() {
		return id;
	}

	public int getExpectedVersion() {
		return expectedVersion;
	}
}
