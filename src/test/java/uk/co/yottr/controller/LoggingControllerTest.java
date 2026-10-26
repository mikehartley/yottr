package uk.co.yottr.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoggingControllerTest extends AbstractControllerTest {

    private final Principal admin = () -> "ada";

    private void givenLoggers(LoggerConfiguration... configurations) {
        final Map<String, LoggersEndpoint.LoggerLevelsDescriptor> loggers = new LinkedHashMap<>();
        for (LoggerConfiguration configuration : configurations) {
            loggers.put(configuration.getName(), new LoggersEndpoint.SingleLoggerLevelsDescriptor(configuration));
        }
        when(mockLoggersEndpoint.loggers()).thenReturn(new LoggersEndpoint.LoggersDescriptor(new TreeSet<>(List.of(LogLevel.values())), loggers, Map.of()));
    }

    @Test
    public void withNoFilterShowsOnlyLoggersWithTheirOwnLevel() throws Exception {
        givenLoggers(
                new LoggerConfiguration("ROOT", LogLevel.INFO, LogLevel.INFO),
                new LoggerConfiguration("uk.co.yottr", null, LogLevel.INFO),
                new LoggerConfiguration("uk.co.yottr.controller", LogLevel.DEBUG, LogLevel.DEBUG));

        mockMvc.perform(get("/admin/logging").principal(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("adminLogging"))
                .andExpect(model().attribute("loggers", contains(
                        new LoggingController.LoggerRow("ROOT", "INFO", "INFO"),
                        new LoggingController.LoggerRow("uk.co.yottr.controller", "DEBUG", "DEBUG"))));
    }

    @Test
    public void withAFilterShowsEveryMatchingLogger() throws Exception {
        givenLoggers(
                new LoggerConfiguration("ROOT", LogLevel.INFO, LogLevel.INFO),
                new LoggerConfiguration("uk.co.yottr", null, LogLevel.INFO),
                new LoggerConfiguration("uk.co.yottr.controller", LogLevel.DEBUG, LogLevel.DEBUG));

        mockMvc.perform(get("/admin/logging").param("filter", "Yottr").principal(admin))
                .andExpect(status().isOk())
                .andExpect(model().attribute("filter", "Yottr"))
                .andExpect(model().attribute("loggers", contains(
                        new LoggingController.LoggerRow("uk.co.yottr", null, "INFO"),
                        new LoggingController.LoggerRow("uk.co.yottr.controller", "DEBUG", "DEBUG"))));
    }

    @Test
    public void setsALevelAndShowsThatLogger() throws Exception {
        mockMvc.perform(post("/admin/logging").param("name", " uk.co.yottr ").param("level", "DEBUG").principal(admin))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/logging?filter=uk.co.yottr"));

        verify(mockLoggersEndpoint).configureLogLevel("uk.co.yottr", LogLevel.DEBUG);
    }

    @Test
    public void anEmptyLevelRemovesTheLoggersOwnLevel() throws Exception {
        mockMvc.perform(post("/admin/logging").param("name", "uk.co.yottr").param("level", "").principal(admin))
                .andExpect(status().is3xxRedirection());

        verify(mockLoggersEndpoint).configureLogLevel(eq("uk.co.yottr"), isNull());
    }

    @Test
    public void anEmptyNameChangesNothing() throws Exception {
        mockMvc.perform(post("/admin/logging").param("name", " ").param("level", "DEBUG").principal(admin))
                .andExpect(status().is3xxRedirection());

        verify(mockLoggersEndpoint, never()).configureLogLevel(anyString(), any());
    }
}
