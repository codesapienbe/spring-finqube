package com.finqube.iso20022.admin.gwt.model;

import java.util.List;

/**
 * Generic pagination envelope for REST responses.
 *
 * @param <T> payload item type
 */
public record PageResponse<T>(
        List<T> items,
        long total,
        int page,
        int size,
        int totalPages) {
}
