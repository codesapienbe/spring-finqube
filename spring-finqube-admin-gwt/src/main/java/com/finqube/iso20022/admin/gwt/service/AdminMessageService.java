package com.finqube.iso20022.admin.gwt.service;

import com.finqube.iso20022.admin.gwt.model.AdminMessage;
import com.finqube.iso20022.admin.gwt.model.Direction;
import com.finqube.iso20022.admin.gwt.model.MessageSummary;
import com.finqube.iso20022.admin.gwt.model.PageResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Read-only message feed for the admin dashboard.
 *
 * <p>Currently provides an in-memory list of sample messages to enable the
 * dashboard UI. This service is intentionally isolated from core business
 * processing and can be replaced with a persistence-backed implementation later
 * without impacting the REST API surface.</p>
 */
@Service
public class AdminMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminMessageService.class);

    private final List<AdminMessage> messages = new ArrayList<>();

    /**
     * Seeds a small set of demo messages at startup.
     */
    @PostConstruct
    void seedDemoMessages() {
        // Seed 10 incoming and 10 outgoing messages with sanitized summaries
        for (int i = 0; i < 10; i++) {
            messages.add(new AdminMessage(
                    UUID.randomUUID().toString(),
                    Instant.now().minusSeconds(300 - i * 15L),
                    Direction.INCOMING,
                    i % 2 == 0 ? "pacs.008" : "camt.054",
                    i % 3 == 0 ? "RECEIVED" : "VALIDATED",
                    "Incoming message " + (i + 1)));
        }
        for (int i = 0; i < 10; i++) {
            messages.add(new AdminMessage(
                    UUID.randomUUID().toString(),
                    Instant.now().minusSeconds(280 - i * 12L),
                    Direction.OUTGOING,
                    i % 2 == 0 ? "pacs.002" : "pain.001",
                    i % 4 == 0 ? "QUEUED" : "SENT",
                    "Outgoing message " + (i + 1)));
        }
        messages.sort((a, b) -> b.timestamp().compareTo(a.timestamp()));
        LOGGER.info("{}", toLog("admin_messages_seeded", null, messages.size()));
    }

    /**
     * Returns messages filtered by direction. If direction is null, all messages are returned.
     *
     * @param direction optional direction to filter by
     * @return immutable list of messages
     */
    public List<AdminMessage> findMessages(Direction direction) {
        final List<AdminMessage> result;
        if (direction == null) {
            result = List.copyOf(messages);
        } else {
            result = messages.stream()
                    .filter(m -> m.direction() == direction)
                    .collect(Collectors.toList());
        }
        LOGGER.info("{}", toLog("admin_messages_fetched", direction, result.size()));
        return Collections.unmodifiableList(result);
    }

    /**
     * Filterable, paginated search.
     */
    public PageResponse<AdminMessage> search(Direction direction,
                                             String status,
                                             String messageType,
                                             String query,
                                             int page,
                                             int size) {
        Predicate<AdminMessage> predicate = m -> true;
        if (direction != null) {
            predicate = predicate.and(m -> m.direction() == direction);
        }
        if (status != null && !status.isBlank()) {
            final String s = status.trim().toUpperCase();
            predicate = predicate.and(m -> m.status() != null && m.status().toUpperCase().contains(s));
        }
        if (messageType != null && !messageType.isBlank()) {
            final String t = messageType.trim().toLowerCase();
            predicate = predicate.and(m -> m.messageType() != null && m.messageType().toLowerCase().contains(t));
        }
        if (query != null && !query.isBlank()) {
            final String q = query.trim().toLowerCase();
            predicate = predicate.and(m ->
                    (m.summary() != null && m.summary().toLowerCase().contains(q))
                            || (m.id() != null && m.id().toLowerCase().contains(q))
            );
        }

        List<AdminMessage> filtered = messages.stream()
                .filter(predicate)
                .sorted((a, b) -> b.timestamp().compareTo(a.timestamp()))
                .collect(Collectors.toList());

        int total = filtered.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<AdminMessage> items = filtered.subList(from, to);
        int totalPages = (int) Math.ceil(total / (double) size);

        LOGGER.info("{}", toLog("admin_messages_search", direction, items.size()));
        return new PageResponse<>(items, total, page, size, totalPages);
    }

    /**
     * Summary aggregates by direction and status.
     */
    public MessageSummary summarize() {
        long totalIncoming = messages.stream().filter(m -> m.direction() == Direction.INCOMING).count();
        long totalOutgoing = messages.stream().filter(m -> m.direction() == Direction.OUTGOING).count();

        Map<String, Long> byStatusIncoming = messages.stream()
                .filter(m -> m.direction() == Direction.INCOMING)
                .collect(Collectors.groupingBy(AdminMessage::status, Collectors.counting()));
        Map<String, Long> byStatusOutgoing = messages.stream()
                .filter(m -> m.direction() == Direction.OUTGOING)
                .collect(Collectors.groupingBy(AdminMessage::status, Collectors.counting()));

        LOGGER.info("{}", toLog("admin_messages_summary", null, null));
        return new MessageSummary(totalIncoming, totalOutgoing, byStatusIncoming, byStatusOutgoing);
    }

    private static String toLog(String event, Direction direction, Integer size) {
        // Minimal JSON-structured log line; avoids leaking message content
        return "{" +
                "\"timestamp\":\"" + Instant.now() + "\"," +
                "\"level\":\"INFO\"," +
                "\"component\":\"AdminMessageService\"," +
                "\"message\":\"" + event + "\"," +
                (direction != null ? "\"direction\":\"" + direction + "\"," : "") +
                (size != null ? "\"count\":" + size + "," : "") +
                "\"correlation_id\":\"-\",\"user_id\":\"-\",\"request_id\":\"-\"}";
    }
}
