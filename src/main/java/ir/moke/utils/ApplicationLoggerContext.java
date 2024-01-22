package ir.moke.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

import java.nio.charset.StandardCharsets;

public class ApplicationLoggerContext extends ContextAwareBase implements Configurator {
    private static final String PATTERN = "[%-5level] %date [%thread] %logger{10} [%file:%line] %msg%n";
    @Override
    public ExecutionStatus configure(LoggerContext loggerContext) {

        PatternLayout pattern = new PatternLayout();
        pattern.setContext(loggerContext);
        pattern.setPattern(PATTERN);
        pattern.start();

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setLayout(pattern);

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        loggerContext.reset();

        Logger log = loggerContext.getLogger("ir.moke");
        log.setAdditive(false);
        log.setLevel(Level.ALL);
        log.addAppender(consoleAppender);

        return ExecutionStatus.NEUTRAL;
    }
}
