package com.finqube.iso20022.core.transport;

import java.util.List;
import java.util.Optional;

/**
 * Interface for TransportFactory operations.
 */
public interface TransportFactoryOperations {
    Transport getTransport(String transportId) throws TransportException;
    List<String> getAvailableTransportIds();
    List<TransportFactory.TransportInfo> getAvailableTransports();
    boolean isTransportAvailable(String transportId);
    Optional<TransportFactory.TransportInfo> getTransportInfo(String transportId);
    TransportStatistics getStatistics();
    void closeAll();
}
