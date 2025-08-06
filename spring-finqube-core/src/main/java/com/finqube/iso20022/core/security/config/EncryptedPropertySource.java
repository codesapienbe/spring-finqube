package com.finqube.iso20022.core.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Property source that automatically decrypts encrypted configuration values.
 *
 * <p>This property source wraps another property source and automatically
 * decrypts any values that appear to be encrypted using the configuration
 * encryption service.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class EncryptedPropertySource extends EnumerablePropertySource<PropertySource<?>> {

    private static final Logger log = LoggerFactory.getLogger(EncryptedPropertySource.class);

    private final ConfigurationEncryptionService encryptionService;
    private final Map<String, String> decryptedCache = new HashMap<>();

    /**
     * Constructs a new EncryptedPropertySource.
     *
     * @param name the property source name
     * @param source the wrapped property source
     * @param encryptionService the configuration encryption service
     */
    public EncryptedPropertySource(String name, PropertySource<?> source, ConfigurationEncryptionService encryptionService) {
        super(name, source);
        this.encryptionService = encryptionService;
    }

    @Override
    public Object getProperty(String name) {
        Object value = source.getProperty(name);

        if (value instanceof String) {
            String stringValue = (String) value;

            // Check if the value is encrypted
            if (encryptionService.isEncrypted(stringValue)) {
                // Check cache first
                if (decryptedCache.containsKey(name)) {
                    log.debug("Returning cached decrypted value for property: {}", name);
                    return decryptedCache.get(name);
                }

                try {
                    // Decrypt the value
                    String decryptedValue = encryptionService.decrypt(stringValue);
                    decryptedCache.put(name, decryptedValue);

                    log.debug("Successfully decrypted property: {}", name);
                    return decryptedValue;

                } catch (ConfigurationEncryptionException e) {
                    log.error("Failed to decrypt property '{}': {}", name, e.getMessage());
                    // Return the encrypted value as-is to avoid breaking the application
                    return stringValue;
                }
            }
        }

        return value;
    }

    @Override
    public String[] getPropertyNames() {
        if (source instanceof EnumerablePropertySource) {
            return ((EnumerablePropertySource<?>) source).getPropertyNames();
        }
        return new String[0];
    }

    /**
     * Clears the decrypted value cache.
     */
    public void clearCache() {
        decryptedCache.clear();
        log.debug("Cleared decrypted property cache");
    }

    /**
     * Gets the number of cached decrypted values.
     *
     * @return the cache size
     */
    public int getCacheSize() {
        return decryptedCache.size();
    }
}
