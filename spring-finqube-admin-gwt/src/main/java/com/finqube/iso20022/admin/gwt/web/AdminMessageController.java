package com.finqube.iso20022.admin.gwt.web;

import com.finqube.iso20022.admin.gwt.model.AdminMessage;
import com.finqube.iso20022.admin.gwt.model.Direction;
import com.finqube.iso20022.admin.gwt.model.MessageSummary;
import com.finqube.iso20022.admin.gwt.model.PageResponse;
import com.finqube.iso20022.admin.gwt.service.AdminMessageService;
import java.util.List;
import java.util.Locale;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * REST controller that provides read-only access to incoming and outgoing messages
 * for the admin dashboard UI.
 */
@RestController
@RequestMapping(path = "/api/admin/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminMessageController {

    private static final int MAX_PAGE_SIZE = 100;

    private final AdminMessageService adminMessageService;

    /**
     * Creates a controller with the given message service.
     *
     * @param adminMessageService service providing message data
     */
    public AdminMessageController(AdminMessageService adminMessageService) {
        this.adminMessageService = adminMessageService;
    }

    /**
     * Lists messages filtered by optional direction. If not provided, all messages are returned.
     *
     * @param direction optional direction filter; accepts INCOMING or OUTGOING (case-insensitive)
     * @return list of messages
     */
    @GetMapping
    public List<AdminMessage> list(@RequestParam(name = "direction", required = false) String direction) {
        final Direction dir = parseDirection(direction);
        return adminMessageService.findMessages(dir);
    }

    /**
     * Paginated and filterable search endpoint.
     */
    @GetMapping(path = "/page")
    public PageResponse<AdminMessage> page(
            @RequestParam(name = "direction", required = false) String direction,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String messageType,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(MAX_PAGE_SIZE, size));
        final Direction dir = parseDirection(direction);
        return adminMessageService.search(dir, status, messageType, q, safePage, safeSize);
    }

    /**
     * Summary aggregates endpoint.
     */
    @GetMapping(path = "/summary")
    public MessageSummary summary() {
        return adminMessageService.summarize();
    }

    private static Direction parseDirection(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        final String normalized = raw.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "INCOMING" -> Direction.INCOMING;
            case "OUTGOING" -> Direction.OUTGOING;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid direction. Allowed values: INCOMING, OUTGOING");
        };
    }
}
