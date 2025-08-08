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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GWT EntryPoint for the Admin Dashboard.
 *
 * <p>Renders two tabs (Incoming and Outgoing) and an Overview tab with filtering,
 * pagination, and summary counters.</p>
 */
public final class AdminDashboard implements EntryPoint {

    private static final String API_BASE = GWT.getHostPageBaseURL() + "api/admin/messages";

    private FlexTable incomingTable;
    private FlexTable outgoingTable;
    private Label incomingStatus;
    private Label outgoingStatus;

    // Overview tab widgets
    private FlexTable overviewTable;
    private Label overviewStatus;
    private ListBox directionList;
    private TextBox statusBox;
    private TextBox typeBox;
    private TextBox queryBox;
    private ListBox sizeList;
    private int currentPage = 0;
    private int totalPages = 0;

    private Grid summaryGrid;

    // Real message sending widgets
    private Label sendProgressLabel;
    private Label sendStatusLabel;
    private Button sendRealMessagesButton;

    @Override
    public void onModuleLoad() {
        RootPanel rootPanel = RootPanel.get();
        TabPanel tabs = new TabPanel();
        tabs.setAnimationEnabled(true);

        // Overview Tab
        VerticalPanel overviewPanel = new VerticalPanel();
        overviewPanel.setSpacing(8);
        HorizontalPanel filters = new HorizontalPanel();
        filters.setSpacing(8);
        directionList = new ListBox();
        directionList.addItem("All", "");
        directionList.addItem("Incoming", "INCOMING");
        directionList.addItem("Outgoing", "OUTGOING");
        statusBox = new TextBox();
        statusBox.getElement().setPropertyString("placeholder", "Status contains...");
        typeBox = new TextBox();
        typeBox.getElement().setPropertyString("placeholder", "Type contains...");
        queryBox = new TextBox();
        queryBox.getElement().setPropertyString("placeholder", "Search (id/summary)...");
        sizeList = new ListBox();
        sizeList.addItem("10", "10");
        sizeList.addItem("20", "20");
        sizeList.addItem("50", "50");
        sizeList.setSelectedIndex(1);
        Button applyFilters = new Button("Apply", (ClickHandler) c -> {
            currentPage = 0;
            fetchOverviewPage();
            fetchSummary();
        });
        Button generateMessages = new Button("Generate 100 Messages", (ClickHandler) c -> {
            generateMessages();
        });
        filters.add(new Label("Direction:"));
        filters.add(directionList);
        filters.add(new Label("Status:"));
        filters.add(statusBox);
        filters.add(new Label("Type:"));
        filters.add(typeBox);
        filters.add(new Label("Query:"));
        filters.add(queryBox);
        filters.add(new Label("Size:"));
        filters.add(sizeList);
        filters.add(applyFilters);
        filters.add(generateMessages);

        // Real message sending section
        HorizontalPanel sendPanel = new HorizontalPanel();
        sendPanel.setSpacing(8);
        sendRealMessagesButton = new Button("Send Real Messages (100 incoming, 50 outgoing)", (ClickHandler) c -> {
            sendRealMessages();
        });
        sendProgressLabel = new Label("Ready");
        sendStatusLabel = new Label("Ready to send real messages");
        sendPanel.add(sendRealMessagesButton);
        sendPanel.add(sendProgressLabel);
        sendPanel.add(sendStatusLabel);

        overviewStatus = new Label();
        overviewTable = createTable();
        HorizontalPanel pager = new HorizontalPanel();
        pager.setSpacing(8);
        Button prev = new Button("Prev", (ClickHandler) c -> {
            if (currentPage > 0) {
                currentPage--;
                fetchOverviewPage();
            }
        });
        Button next = new Button("Next", (ClickHandler) c -> {
            if (currentPage + 1 < totalPages) {
                currentPage++;
                fetchOverviewPage();
            }
        });
        pager.add(prev);
        pager.add(next);

        summaryGrid = new Grid(2, 4);
        summaryGrid.setText(0, 0, "Incoming");
        summaryGrid.setText(0, 1, "Outgoing");
        summaryGrid.setText(1, 0, "0");
        summaryGrid.setText(1, 1, "0");
        // status maps will be shown as simple counts appended

        overviewPanel.add(filters);
        overviewPanel.add(sendPanel);
        overviewPanel.add(overviewStatus);
        overviewPanel.add(overviewTable);
        overviewPanel.add(pager);
        overviewPanel.add(new Label("Summary"));
        overviewPanel.add(summaryGrid);

        // Incoming Tab
        VerticalPanel incomingPanel = new VerticalPanel();
        incomingPanel.setSpacing(8);
        Button refreshIncoming = new Button("Refresh Incoming");
        refreshIncoming.addClickHandler((ClickHandler) click -> fetchIncoming());
        incomingStatus = new Label();
        incomingTable = createTable();
        incomingPanel.add(refreshIncoming);
        incomingPanel.add(incomingStatus);
        incomingPanel.add(incomingTable);

        // Outgoing Tab
        VerticalPanel outgoingPanel = new VerticalPanel();
        outgoingPanel.setSpacing(8);
        Button refreshOutgoing = new Button("Refresh Outgoing");
        refreshOutgoing.addClickHandler((ClickHandler) click -> fetchOutgoing());
        outgoingStatus = new Label();
        outgoingTable = createTable();
        outgoingPanel.add(refreshOutgoing);
        outgoingPanel.add(outgoingStatus);
        outgoingPanel.add(outgoingTable);

        tabs.add(overviewPanel, "Overview");
        tabs.add(incomingPanel, "Incoming");
        tabs.add(outgoingPanel, "Outgoing");
        tabs.selectTab(0);

        rootPanel.add(tabs);

        // Initial load
        fetchOverviewPage();
        fetchSummary();
        fetchIncoming();
        fetchOutgoing();
    }

    private FlexTable createTable() {
        FlexTable table = new FlexTable();
        table.setText(0, 0, "Timestamp");
        table.setText(0, 1, "Type");
        table.setText(0, 2, "Status");
        table.setText(0, 3, "Summary");
        return table;
    }

    private void sendRealMessages() {
        sendRealMessagesButton.setEnabled(false);
        sendProgressLabel.setText("Sending...");
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
                                sendStatusLabel.setText(message != null ? message.stringValue() :
                                    "Sent " + (successful != null ? (int) successful.doubleValue() : 0) +
                                    " messages successfully");

                                // Refresh data to show new messages
                                currentPage = 0;
                                fetchOverviewPage();
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
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    sendStatusLabel.setText("Error sending messages: " + exception.getMessage());
                    sendRealMessagesButton.setEnabled(true);
                    sendProgressLabel.setText("Ready");
                }
            });
        } catch (Exception e) {
            sendStatusLabel.setText("Request error: " + e.getMessage());
            sendRealMessagesButton.setEnabled(true);
            sendProgressLabel.setText("Ready");
        }
    }

    private void generateMessages() {
        overviewStatus.setText("Generating 100 mock messages...");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, API_BASE + "/generate?count=100");
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        overviewStatus.setText("Generated 100 messages successfully");
                        // Refresh data
                        currentPage = 0;
                        fetchOverviewPage();
                        fetchSummary();
                    } else {
                        overviewStatus.setText("Error generating messages: " + response.getStatusCode());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    overviewStatus.setText("Error generating messages: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            overviewStatus.setText("Request error: " + e.getMessage());
        }
    }

    private void fetchOverviewPage() {
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
        overviewStatus.setText("Loading page " + (currentPage + 1) + "...");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        renderPage(overviewTable, response.getText());
                        overviewStatus.setText("Loaded page " + (currentPage + 1) + " of " + totalPages);
                    } else {
                        overviewStatus.setText("Error: " + response.getStatusCode());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    overviewStatus.setText("Network error: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            overviewStatus.setText("Request error: " + e.getMessage());
        }
    }

    private void fetchIncoming() {
        fetchList("INCOMING", incomingTable, incomingStatus);
    }

    private void fetchOutgoing() {
        fetchList("OUTGOING", outgoingTable, outgoingStatus);
    }

    private void fetchList(String direction, FlexTable table, Label statusLabel) {
        String url = API_BASE + "?direction=" + direction;
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        statusLabel.setText("Loading " + direction.toLowerCase() + "...");
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        renderItems(table, response.getText());
                        statusLabel.setText("Loaded " + direction.toLowerCase());
                    } else {
                        statusLabel.setText("Error: " + response.getStatusCode());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    statusLabel.setText("Network error: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            statusLabel.setText("Request error: " + e.getMessage());
        }
    }

    private void fetchSummary() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, API_BASE + "/summary");
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
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

    private void renderPage(FlexTable table, String jsonText) {
        JSONObject obj = JSONParser.parseStrict(jsonText).isObject();
        if (obj == null) return;
        JSONNumber totalPagesNum = obj.get("totalPages").isNumber();
        totalPages = totalPagesNum != null ? (int) totalPagesNum.doubleValue() : 0;
        JSONArray items = obj.get("items").isArray();
        renderItemsArray(table, items);
    }

    private void renderItems(FlexTable table, String jsonText) {
        JSONArray arr = JSONParser.parseStrict(jsonText).isArray();
        renderItemsArray(table, arr);
    }

    private void renderItemsArray(FlexTable table, JSONArray arr) {
        // Clear old rows except header
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
        if (arr == null) return;
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.get(i).isObject();
            if (obj == null) continue;
            setRow(table, i + 1, obj);
        }
    }

    private void setRow(FlexTable table, int row, JSONObject obj) {
        String timestamp = getString(obj, "timestamp");
        String type = getString(obj, "messageType");
        String status = getString(obj, "status");
        String summary = getString(obj, "summary");
        table.setText(row, 0, safe(timestamp));
        table.setText(row, 1, safe(type));
        table.setText(row, 2, safe(status));
        table.setText(row, 3, safe(summary));
    }

    private void renderSummary(String jsonText) {
        JSONObject obj = JSONParser.parseStrict(jsonText).isObject();
        if (obj == null) return;
        JSONNumber inTotal = obj.get("totalIncoming").isNumber();
        JSONNumber outTotal = obj.get("totalOutgoing").isNumber();
        summaryGrid.setText(1, 0, inTotal != null ? String.valueOf((long) inTotal.doubleValue()) : "0");
        summaryGrid.setText(1, 1, outTotal != null ? String.valueOf((long) outTotal.doubleValue()) : "0");
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
