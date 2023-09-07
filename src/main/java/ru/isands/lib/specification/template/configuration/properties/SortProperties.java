package ru.isands.lib.specification.template.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Getter
@Configuration
@ConfigurationProperties(prefix = "pagination.sort")
public class SortProperties {
    private String pattern="([\\w\\.]+?)(:)(\\w+?);";

    @Setter
    private String fieldSeparator =";";

    private Pattern sortPattern;

    public SortProperties() {
        sortPattern = Pattern.compile(pattern);
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        sortPattern = Pattern.compile(pattern);
    }
}
