package com.finqube.iso20022.admin.gwt.service;

import com.finqube.iso20022.admin.gwt.model.AdminMessage;
import com.finqube.iso20022.admin.gwt.model.Direction;
import com.finqube.iso20022.admin.gwt.model.MessageSummary;
import com.finqube.iso20022.admin.gwt.model.PageResponse;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.pacs.Pacs008Message;
import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.transport.Transport;
import com.finqube.iso20022.core.transport.TransportFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final TransportFactory transportFactory;

    public AdminMessageService(TransportFactory transportFactory) {
        this.transportFactory = transportFactory;
    }

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

    @PreDestroy
    void shutdown() {
        executor.shutdown();
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

    /**
     * Generates additional mock messages for testing the dashboard.
     *
     * @param count number of messages to generate
     * @return number of messages actually generated
     */
    public int generateMockMessages(int count) {
        final String[] messageTypes = {"pacs.008", "camt.054", "pacs.002", "pain.001", "camt.052", "pacs.004"};
        final String[] statuses = {"RECEIVED", "VALIDATED", "QUEUED", "SENT", "PROCESSING", "COMPLETED"};

        int generated = 0;
        for (int i = 0; i < count; i++) {
            Direction direction = i % 2 == 0 ? Direction.INCOMING : Direction.OUTGOING;
            String messageType = messageTypes[i % messageTypes.length];
            String status = statuses[i % statuses.length];
            String summary = "Mock " + direction.toString().toLowerCase() + " message " + (messages.size() + i + 1);

            messages.add(new AdminMessage(
                    UUID.randomUUID().toString(),
                    Instant.now().minusSeconds(i * 2L), // Spread timestamps
                    direction,
                    messageType,
                    status,
                    summary));
            generated++;
        }

        // Re-sort by timestamp (newest first)
        messages.sort((a, b) -> b.timestamp().compareTo(a.timestamp()));

        LOGGER.info("{}", toLog("admin_messages_generated", null, generated));
        return generated;
    }

    /**
     * Sends real messages through the transport layer and tracks progress.
     *
     * @param incomingCount number of incoming messages to simulate
     * @param outgoingCount number of outgoing messages to send
     * @param progressCallback callback to report progress (0-100)
     * @return CompletableFuture that completes when all messages are processed
     */
    public CompletableFuture<SendResult> sendRealMessages(int incomingCount, int outgoingCount,
                                                         ProgressCallback progressCallback) {
        return CompletableFuture.supplyAsync(() -> {
            int totalMessages = incomingCount + outgoingCount;
            int processed = 0;
            int successful = 0;
            int failed = 0;

            try {
                // Simulate incoming messages (these would normally come from external systems)
                for (int i = 0; i < incomingCount; i++) {
                    try {
                        simulateIncomingMessage(i + 1);
                        successful++;
                    } catch (Exception e) {
                        LOGGER.warn("Failed to simulate incoming message {}: {}", i + 1, e.getMessage());
                        failed++;
                    }
                    processed++;
                    progressCallback.onProgress((processed * 100) / totalMessages);
                    Thread.sleep(50); // Small delay to show progress
                }

                // Send real outgoing messages
                for (int i = 0; i < outgoingCount; i++) {
                    try {
                        sendOutgoingMessage(i + 1);
                        successful++;
                    } catch (Exception e) {
                        LOGGER.warn("Failed to send outgoing message {}: {}", i + 1, e.getMessage());
                        failed++;
                    }
                    processed++;
                    progressCallback.onProgress((processed * 100) / totalMessages);
                    Thread.sleep(100); // Longer delay for real sends
                }

                LOGGER.info("{}", toLog("real_messages_sent", null, successful));
                return new SendResult(successful, failed, totalMessages);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Message sending interrupted", e);
            }
        }, executor);
    }

    private void simulateIncomingMessage(int index) {
        String messageId = UUID.randomUUID().toString();
        String messageType = index % 2 == 0 ? "pacs.008" : "camt.054";
        String status = index % 3 == 0 ? "RECEIVED" : "VALIDATED";

        messages.add(new AdminMessage(
                messageId,
                Instant.now(),
                Direction.INCOMING,
                messageType,
                status,
                "Real incoming message " + index));

        messages.sort((a, b) -> b.timestamp().compareTo(a.timestamp()));
    }

    private void sendOutgoingMessage(int index) throws Exception {
        String messageId = UUID.randomUUID().toString();
        String messageType = index % 2 == 0 ? "pacs.002" : "pain.001";

        // Create a real message using the core API
        BaseMessage message = createOutgoingMessage(messageType, messageId);

        // Send through transport layer
        Transport transport = transportFactory.createTransport("default");
        transport.send(message);

        // Add to our tracking list
        messages.add(new AdminMessage(
                messageId,
                Instant.now(),
                Direction.OUTGOING,
                messageType,
                "SENT",
                "Real outgoing message " + index));

        messages.sort((a, b) -> b.timestamp().compareTo(a.timestamp()));
    }

    private BaseMessage createOutgoingMessage(String messageType, String messageId) {
        // Create appropriate message type based on the type string
        if ("pacs.002".equals(messageType)) {
            return new Pacs008Message(messageId); // Using Pacs008 as example
        } else if ("pain.001".equals(messageType)) {
            return new Pain001Message(messageId);
        } else {
            // Default to Pacs008
            return new Pacs008Message(messageId);
        }
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

    /**
     * Callback interface for progress reporting.
     */
    public interface ProgressCallback {
        void onProgress(int percentage);
    }

    /**
     * Result of sending real messages.
     */
    public static class SendResult {
        private final int successful;
        private final int failed;
        private final int total;

        public SendResult(int successful, int failed, int total) {
            this.successful = successful;
            this.failed = failed;
            this.total = total;
        }

        public int getSuccessful() { return successful; }
        public int getFailed() { return failed; }
        public int getTotal() { return total; }
    }
}
