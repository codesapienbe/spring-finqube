package com.finqube.iso20022.admin.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GWT EntryPoint for the Admin Dashboard.
 *
 * <p>Renders a modern banking-style dashboard with cards, stats, and organized content
 * that matches the design reference.</p>
 */
public final class AdminDashboard implements EntryPoint {

    private static final String API_BASE = GWT.getHostPageBaseURL() + "api/admin/messages";

    // Stats widgets
    private HTML totalIncomingCard;
    private HTML totalOutgoingCard;
    private HTML totalMessagesCard;
    private HTML successRateCard;

    // Message table
    private FlexTable messagesTable;
    private Label messagesStatus;

    // Controls
    private ListBox directionList;
    private TextBox statusBox;
    private TextBox typeBox;
    private TextBox queryBox;
    private ListBox sizeList;
    private int currentPage = 0;
    private int totalPages = 0;

    // Real message sending widgets
    private Label sendProgressLabel;
    private Label sendStatusLabel;
    private Button sendRealMessagesButton;

    @Override
    public void onModuleLoad() {
        RootPanel rootPanel = RootPanel.get("admindashboard");

        // Create main dashboard layout
        VerticalPanel dashboard = new VerticalPanel();
        dashboard.setStyleName("dashboard-content");

        // Stats Cards Row
        HorizontalPanel statsRow = new HorizontalPanel();
        statsRow.setStyleName("stats-grid");

        // Total Incoming Card
        totalIncomingCard = createStatCard("📥", "Total Incoming", "0", "#3b82f6");
        statsRow.add(totalIncomingCard);

        // Total Outgoing Card
        totalOutgoingCard = createStatCard("📤", "Total Outgoing", "0", "#10b981");
        statsRow.add(totalOutgoingCard);

        // Total Messages Card
        totalMessagesCard = createStatCard("📊", "Total Messages", "0", "#8b5cf6");
        statsRow.add(totalMessagesCard);

        // Success Rate Card
        successRateCard = createStatCard("✅", "Success Rate", "100%", "#f59e0b");
        statsRow.add(successRateCard);

        dashboard.add(statsRow);

        // Controls and Actions Row
        VerticalPanel controlsPanel = new VerticalPanel();
        controlsPanel.setStyleName("card");

        // Controls Header
        HorizontalPanel controlsHeader = new HorizontalPanel();
        controlsHeader.setStyleName("card-header");
        Label controlsTitle = new Label("Message Controls");
        controlsTitle.setStyleName("card-title");
        controlsHeader.add(controlsTitle);

        HorizontalPanel controlsActions = new HorizontalPanel();
        controlsActions.setStyleName("card-actions");

        // Filter Controls
        HorizontalPanel filters = new HorizontalPanel();
        filters.setSpacing(8);
        directionList = new ListBox();
        directionList.addItem("All Directions", "");
        directionList.addItem("Incoming", "INCOMING");
        directionList.addItem("Outgoing", "OUTGOING");
        directionList.setStyleName("btn btn-secondary");

        statusBox = new TextBox();
        statusBox.getElement().setPropertyString("placeholder", "Status...");
        statusBox.setStyleName("btn btn-secondary");

        typeBox = new TextBox();
        typeBox.getElement().setPropertyString("placeholder", "Type...");
        typeBox.setStyleName("btn btn-secondary");

        queryBox = new TextBox();
        queryBox.getElement().setPropertyString("placeholder", "Search...");
        queryBox.setStyleName("btn btn-secondary");

        sizeList = new ListBox();
        sizeList.addItem("10", "10");
        sizeList.addItem("20", "20");
        sizeList.addItem("50", "50");
        sizeList.setSelectedIndex(1);
        sizeList.setStyleName("btn btn-secondary");

        Button applyFilters = new Button("🔍 Apply Filters");
        applyFilters.setStyleName("btn btn-primary");
        applyFilters.addClickHandler((ClickHandler) c -> {
            currentPage = 0;
            fetchMessages();
        });

        Button generateMessages = new Button("🎲 Generate Mock");
        generateMessages.setStyleName("btn btn-secondary");
        generateMessages.addClickHandler((ClickHandler) c -> generateMessages());

        sendRealMessagesButton = new Button("📡 Send Real Messages");
        sendRealMessagesButton.setStyleName("btn btn-primary");
        sendRealMessagesButton.addClickHandler((ClickHandler) c -> sendRealMessages());

        filters.add(new Label("Direction:"));
        filters.add(directionList);
        filters.add(new Label("Status:"));
        filters.add(statusBox);
        filters.add(new Label("Type:"));
        filters.add(typeBox);
        filters.add(new Label("Search:"));
        filters.add(queryBox);
        filters.add(new Label("Size:"));
        filters.add(sizeList);

        controlsActions.add(applyFilters);
        controlsActions.add(generateMessages);
        controlsActions.add(sendRealMessagesButton);

        controlsHeader.add(controlsActions);
        controlsPanel.add(controlsHeader);
        controlsPanel.add(filters);

        // Progress and Status
        HorizontalPanel progressPanel = new HorizontalPanel();
        progressPanel.setSpacing(8);
        sendProgressLabel = new Label("Ready");
        sendProgressLabel.setStyleName("status-badge status-validated");
        sendStatusLabel = new Label("Ready to send real messages");
        sendStatusLabel.setStyleName("hint");
        progressPanel.add(sendProgressLabel);
        progressPanel.add(sendStatusLabel);
        controlsPanel.add(progressPanel);

        dashboard.add(controlsPanel);

        // Messages Table Card
        VerticalPanel messagesPanel = new VerticalPanel();
        messagesPanel.setStyleName("card");

        HorizontalPanel messagesHeader = new HorizontalPanel();
        messagesHeader.setStyleName("card-header");
        Label messagesTitle = new Label("Recent Messages");
        messagesTitle.setStyleName("card-title");
        messagesHeader.add(messagesTitle);

        HorizontalPanel messagesActions = new HorizontalPanel();
        messagesActions.setStyleName("card-actions");

        Button prev = new Button("◀ Prev");
        prev.setStyleName("btn btn-secondary");
        prev.addClickHandler((ClickHandler) c -> {
            if (currentPage > 0) {
                currentPage--;
                fetchMessages();
            }
        });

        Button next = new Button("Next ▶");
        next.setStyleName("btn btn-secondary");
        next.addClickHandler((ClickHandler) c -> {
            if (currentPage + 1 < totalPages) {
                currentPage++;
                fetchMessages();
            }
        });

        messagesActions.add(prev);
        messagesActions.add(next);
        messagesHeader.add(messagesActions);
        messagesPanel.add(messagesHeader);

        // Messages Table
        messagesTable = new FlexTable();
        messagesTable.setStyleName("table");
        messagesTable.setText(0, 0, "Timestamp");
        messagesTable.setText(0, 1, "Direction");
        messagesTable.setText(0, 2, "Type");
        messagesTable.setText(0, 3, "Status");
        messagesTable.setText(0, 4, "Summary");

        messagesStatus = new Label("Loading messages...");
        messagesStatus.setStyleName("hint");

        messagesPanel.add(messagesTable);
        messagesPanel.add(messagesStatus);

        dashboard.add(messagesPanel);

        rootPanel.add(dashboard);

        // Initial load
        fetchSummary();
        fetchMessages();
    }

    private HTML createStatCard(String icon, String label, String value, String color) {
        String html = "<div class='stat-card' style='background: linear-gradient(135deg, " + color + ", " + color + "dd);'>" +
                     "<div class='stat-value'>" + value + "</div>" +
                     "<div class='stat-label'>" + icon + " " + label + "</div>" +
                     "</div>";
        return new HTML(html);
    }

    private void updateStatCard(HTML card, String value) {
        if (card != null && card.getElement() != null) {
            try {
                // Find the stat-value div and update its content
                var elements = card.getElement().getElementsByTagName("div");
                if (elements.getLength() > 0) {
                    elements.getItem(0).setInnerText(value);
                }
            } catch (Exception e) {
                // Fallback: recreate the card with new value
                String icon = card.getElement().getElementsByTagName("div").getItem(1).getInnerText().split(" ")[0];
                String label = card.getElement().getElementsByTagName("div").getItem(1).getInnerText().substring(icon.length() + 1);
                String color = card.getElement().getAttribute("style").contains("#3b82f6") ? "#3b82f6" :
                             card.getElement().getAttribute("style").contains("#10b981") ? "#10b981" :
                             card.getElement().getAttribute("style").contains("#8b5cf6") ? "#8b5cf6" : "#f59e0b";
                card.setHTML("<div class='stat-card' style='background: linear-gradient(135deg, " + color + ", " + color + "dd);'>" +
                           "<div class='stat-value'>" + value + "</div>" +
                           "<div class='stat-label'>" + icon + " " + label + "</div>" +
                           "</div>");
            }
        }
    }

    private void sendRealMessages() {
        sendRealMessagesButton.setEnabled(false);
        sendProgressLabel.setText("Sending...");
        sendProgressLabel.setStyleName("status-badge status-queued");
        sendStatusLabel.setText("Sending real messages...");

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                API_BASE + "/send?incoming=100&outgoing=50");
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        try {
                            JSONObject result = JSONParser.parseStrict(response.getText()).isObject();
                            if (result != null) {
                                JSONNumber successful = result.get("successful").isNumber();
                                JSONNumber failed = result.get("failed").isNumber();
                                JSONString message = result.get("message").isString();

                                sendProgressLabel.setText("Complete");
                                sendProgressLabel.setStyleName("status-badge status-sent");
                                sendStatusLabel.setText(message != null ? message.stringValue() :
                                    "Sent " + (successful != null ? (int) successful.doubleValue() : 0) +
                                    " messages successfully");

                                // Refresh data to show new messages
                                currentPage = 0;
                                fetchMessages();
                                fetchSummary();
                            }
                        } catch (Exception e) {
                            sendStatusLabel.setText("Error parsing response: " + e.getMessage());
                        }
                    } else {
                        sendStatusLabel.setText("Error sending messages: " + response.getStatusCode());
                    }
                    sendRealMessagesButton.setEnabled(true);
                    sendProgressLabel.setText("Ready");
                    sendProgressLabel.setStyleName("status-badge status-validated");
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    sendStatusLabel.setText("Error sending messages: " + exception.getMessage());
                    sendRealMessagesButton.setEnabled(true);
                    sendProgressLabel.setText("Ready");
                    sendProgressLabel.setStyleName("status-badge status-validated");
                }
            });
        } catch (Exception e) {
            sendStatusLabel.setText("Request error: " + e.getMessage());
            sendRealMessagesButton.setEnabled(true);
            sendProgressLabel.setText("Ready");
            sendProgressLabel.setStyleName("status-badge status-validated");
        }
    }

    private void generateMessages() {
        messagesStatus.setText("Generating 100 mock messages...");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, API_BASE + "/generate?count=100");
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        messagesStatus.setText("Generated 100 messages successfully");
                        // Refresh data
                        currentPage = 0;
                        fetchMessages();
                        fetchSummary();
                    } else {
                        messagesStatus.setText("Error generating messages: " + response.getStatusCode());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    messagesStatus.setText("Error generating messages: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            messagesStatus.setText("Request error: " + e.getMessage());
        }
    }

    private void fetchMessages() {
        int dirIdx = directionList.getSelectedIndex();
        String dir = dirIdx >= 0 ? directionList.getValue(dirIdx) : "";
        String status = statusBox.getText();
        String type = typeBox.getText();
        String q = queryBox.getText();
        int sizeIdx = sizeList.getSelectedIndex();
        String size = sizeIdx >= 0 ? sizeList.getValue(sizeIdx) : "20";
        String url = API_BASE + "/page?page=" + currentPage + "&size=" + size
                + (dir != null && !dir.isEmpty() ? "&direction=" + encode(dir) : "")
                + (status != null && !status.isEmpty() ? "&status=" + encode(status) : "")
                + (type != null && !type.isEmpty() ? "&type=" + encode(type) : "")
                + (q != null && !q.isEmpty() ? "&q=" + encode(q) : "");
        messagesStatus.setText("Loading page " + (currentPage + 1) + "...");

        // Debug logging
        System.out.println("Fetching messages from: " + url);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    System.out.println("Response received: " + response.getStatusCode());
                    System.out.println("Response text: " + response.getText());
                    if (response.getStatusCode() == 200) {
                        renderMessages(response.getText());
                        messagesStatus.setText("Loaded page " + (currentPage + 1) + " of " + totalPages);
                    } else {
                        messagesStatus.setText("Error: " + response.getStatusCode());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    messagesStatus.setText("Network error: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            messagesStatus.setText("Request error: " + e.getMessage());
        }
    }

    private void fetchSummary() {
        System.out.println("Fetching summary from: " + API_BASE + "/summary");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, API_BASE + "/summary");
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    System.out.println("Summary response: " + response.getStatusCode());
                    System.out.println("Summary text: " + response.getText());
                    if (response.getStatusCode() == 200) {
                        renderSummary(response.getText());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    // silent in UI
                }
            });
        } catch (Exception ignored) { }
    }

    private void renderMessages(String jsonText) {
        JSONObject obj = JSONParser.parseStrict(jsonText).isObject();
        if (obj == null) return;
        JSONNumber totalPagesNum = obj.get("totalPages").isNumber();
        totalPages = totalPagesNum != null ? (int) totalPagesNum.doubleValue() : 0;
        JSONArray items = obj.get("items").isArray();
        renderMessagesArray(items);
    }

    private void renderMessagesArray(JSONArray arr) {
        // Clear old rows except header
        while (messagesTable.getRowCount() > 1) {
            messagesTable.removeRow(1);
        }
        if (arr == null) return;
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.get(i).isObject();
            if (obj == null) continue;
            setMessageRow(messagesTable, i + 1, obj);
        }
    }

    private void setMessageRow(FlexTable table, int row, JSONObject obj) {
        String timestamp = getString(obj, "timestamp");
        String direction = getString(obj, "direction");
        String type = getString(obj, "messageType");
        String status = getString(obj, "status");
        String summary = getString(obj, "summary");

        table.setText(row, 0, safe(timestamp));

        // Direction with icon
        String directionIcon = "INCOMING".equals(direction) ? "📥" : "📤";
        table.setText(row, 1, directionIcon + " " + safe(direction));

        table.setText(row, 2, safe(type));

        // Status with badge styling
        String statusClass = getStatusClass(status);
        table.setText(row, 3, safe(status));
        table.getCellFormatter().addStyleName(row, 3, statusClass);

        table.setText(row, 4, safe(summary));
    }

    private void renderSummary(String jsonText) {
        JSONObject obj = JSONParser.parseStrict(jsonText).isObject();
        if (obj == null) return;
        JSONNumber inTotal = obj.get("totalIncoming").isNumber();
        JSONNumber outTotal = obj.get("totalOutgoing").isNumber();

        long incoming = inTotal != null ? (long) inTotal.doubleValue() : 0;
        long outgoing = outTotal != null ? (long) outTotal.doubleValue() : 0;
        long total = incoming + outgoing;
        double successRate = total > 0 ? 100.0 : 0.0;

        updateStatCard(totalIncomingCard, String.valueOf(incoming));
        updateStatCard(totalOutgoingCard, String.valueOf(outgoing));
        updateStatCard(totalMessagesCard, String.valueOf(total));
        updateStatCard(successRateCard, String.valueOf((int) successRate) + "%");
    }

    private String getStatusClass(String status) {
        if (status == null) return "status-badge";
        switch (status.toUpperCase()) {
            case "RECEIVED": return "status-badge status-received";
            case "SENT": return "status-badge status-sent";
            case "QUEUED": return "status-badge status-queued";
            case "VALIDATED": return "status-badge status-validated";
            default: return "status-badge";
        }
    }

    private static String getString(JSONObject obj, String key) {
        JSONString s = obj.get(key) != null ? obj.get(key).isString() : null;
        return s != null ? s.stringValue() : "";
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String encode(String s) {
        // Minimal encoding for query usage
        return s.replace(" ", "%20");
    }
}
