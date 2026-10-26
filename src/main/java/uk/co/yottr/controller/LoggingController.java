package uk.co.yottr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Lets administrators see and change log levels while the application runs. It works through the actuator's
 * loggers endpoint, which is also available to administrators at /actuator/loggers.
 * Changes last until the application restarts.
 */
@Controller
public class LoggingController {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingController.class);

    static final int MAX_LOGGERS_SHOWN = 200;

    private final LoggersEndpoint loggersEndpoint;

    public LoggingController(LoggersEndpoint loggersEndpoint) {
        this.loggersEndpoint = loggersEndpoint;
    }

    public record LoggerRow(String name, String configuredLevel, String effectiveLevel) {
    }

    /**
     * With no filter, shows the loggers that have a level of their own; with one, every logger whose name contains it.
     */
    @RequestMapping(value = "/admin/logging", method = RequestMethod.GET)
    public ModelAndView loggers(@RequestParam(defaultValue = "") String filter) {
        final String filterText = filter.trim().toLowerCase(Locale.ROOT);

        final List<LoggerRow> loggers = loggersEndpoint.loggers().getLoggers().entrySet().stream()
                .filter(logger -> filterText.isEmpty()
                        ? logger.getValue().getConfiguredLevel() != null
                        : logger.getKey().toLowerCase(Locale.ROOT).contains(filterText))
                .limit(MAX_LOGGERS_SHOWN + 1)
                .map(LoggingController::toRow)
                .toList();

        final ModelAndView modelAndView = new ModelAndView("adminLogging");
        modelAndView.addObject("filter", filter.trim());
        modelAndView.addObject("loggers", loggers.stream().limit(MAX_LOGGERS_SHOWN).toList());
        modelAndView.addObject("moreLoggers", loggers.size() > MAX_LOGGERS_SHOWN);
        modelAndView.addObject("levels", LogLevel.values());
        return modelAndView;
    }

    /**
     * Sets a logger's level, or with no level, removes its own level so that it inherits one again.
     */
    @RequestMapping(value = "/admin/logging", method = RequestMethod.POST)
    public String setLevel(@RequestParam String name, @RequestParam(required = false) LogLevel level,
                           Principal principal, RedirectAttributes redirectAttributes) {
        final String loggerName = name.trim();

        if (!loggerName.isEmpty()) {
            LOG.info("{} set the log level of {} to {}", principal.getName(), loggerName, level == null ? "inherited" : level);
            loggersEndpoint.configureLogLevel(loggerName, level);
        }

        redirectAttributes.addAttribute("filter", loggerName);
        return "redirect:/admin/logging";
    }

    private static LoggerRow toRow(Map.Entry<String, LoggersEndpoint.LoggerLevelsDescriptor> logger) {
        final String effectiveLevel = logger.getValue() instanceof LoggersEndpoint.SingleLoggerLevelsDescriptor single
                ? single.getEffectiveLevel() : null;
        return new LoggerRow(logger.getKey(), logger.getValue().getConfiguredLevel(), effectiveLevel);
    }
}
