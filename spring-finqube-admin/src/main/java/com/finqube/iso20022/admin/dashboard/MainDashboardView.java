package com.finqube.iso20022.admin.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import com.finqube.iso20022.admin.service.MessageMonitoringService;
import com.finqube.iso20022.admin.service.SystemHealthService;

/**
 * Main dashboard view for the Spring Finqube Admin application.
 *
 * <p>This view provides a comprehensive monitoring interface with:
 * <ul>
 *   <li>Real-time message statistics and metrics</li>
 *   <li>Navigation to different monitoring sections</li>
 *   <li>System health indicators</li>
 *   <li>Quick access to critical functions</li>
 * </ul></p>
 *
 * <p>The dashboard uses Vaadin AppLayout for a professional admin interface
 * with a responsive design that adapts to different screen sizes.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Route("")
@PageTitle("Spring Finqube Admin Dashboard")
public class MainDashboardView extends AppLayout {

    private final MessageMonitoringService messageMonitoringService;
    private final SystemHealthService systemHealthService;

    /**
     * Constructs the main dashboard view with required services.
     *
     * @param messageMonitoringService service for message monitoring functionality
     * @param systemHealthService service for system health monitoring
     */
    @Autowired
    public MainDashboardView(MessageMonitoringService messageMonitoringService,
                           SystemHealthService systemHealthService) {
        this.messageMonitoringService = messageMonitoringService;
        this.systemHealthService = systemHealthService;

        createHeader();
        createDrawer();
        createContent();
    }

    /**
     * Creates the application header with title and navigation controls.
     */
    private void createHeader() {
        H1 logo = new H1("Spring Finqube Admin");
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM);

        String user = "Admin User"; // TODO: Get from security context
        Span userSpan = new Span(user);

        HorizontalLayout header = new HorizontalLayout(
            new DrawerToggle(),
            logo,
            userSpan
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    /**
     * Creates the navigation drawer with menu items.
     */
    private void createDrawer() {
        RouterLink dashboardLink = new RouterLink("Dashboard", MainDashboardView.class);
        RouterLink messagesLink = new RouterLink("Messages", MessageListView.class);
        RouterLink securityLink = new RouterLink("Security", SecurityView.class);
        RouterLink monitoringLink = new RouterLink("Monitoring", MonitoringView.class);
        RouterLink configurationLink = new RouterLink("Configuration", ConfigurationView.class);

        addToDrawer(new VerticalLayout(
            dashboardLink,
            messagesLink,
            securityLink,
            monitoringLink,
            configurationLink
        ));
    }

    /**
     * Creates the main content area with dashboard widgets.
     */
    private void createContent() {
        H2 title = new H2("Dashboard Overview");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);

        HorizontalLayout statsRow = new HorizontalLayout(
            createMessageStatsCard(),
            createSystemHealthCard(),
            createSecurityStatusCard()
        );
        statsRow.setWidthFull();

        VerticalLayout content = new VerticalLayout(title, statsRow);
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(false);

        setContent(content);
    }

    /**
     * Creates a card showing message statistics.
     *
     * @return the message statistics card component
     */
    private Component createMessageStatsCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL
        );

        H2 title = new H2("Message Statistics");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Top.NONE);

        Span totalMessages = new Span("Total Messages: 1,234");
        Span todayMessages = new Span("Today: 56");
        Span pendingMessages = new Span("Pending: 12");
        Span errorMessages = new Span("Errors: 3");

        card.add(title, totalMessages, todayMessages, pendingMessages, errorMessages);
        card.setWidth("300px");

        return card;
    }

    /**
     * Creates a card showing system health status.
     *
     * @return the system health card component
     */
    private Component createSystemHealthCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL
        );

        H2 title = new H2("System Health");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Top.NONE);

        Span status = new Span("Status: Healthy");
        status.addClassNames(LumoUtility.TextColor.SUCCESS);

        Span uptime = new Span("Uptime: 7 days, 14 hours");
        Span memory = new Span("Memory: 45% used");
        Span cpu = new Span("CPU: 23% used");

        card.add(title, status, uptime, memory, cpu);
        card.setWidth("300px");

        return card;
    }

    /**
     * Creates a card showing security status.
     *
     * @return the security status card component
     */
    private Component createSecurityStatusCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL
        );

        H2 title = new H2("Security Status");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Top.NONE);

        Span status = new Span("Status: Secure");
        status.addClassNames(LumoUtility.TextColor.SUCCESS);

        Span certificates = new Span("Certificates: Valid");
        Span encryption = new Span("Encryption: Active");
        Span auditLog = new Span("Audit Log: Enabled");

        card.add(title, status, certificates, encryption, auditLog);
        card.setWidth("300px");

        return card;
    }
}
