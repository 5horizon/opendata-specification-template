package ru.isands.lib.specification.template.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;


@Getter
@Configuration
@ConfigurationProperties(prefix = "pagination.filter")
public class FilterProperties {

    @Setter
    private String fieldSeparator =";";

    @Setter
    private String levelSeparator ="_";


    private String localDateFormat = "yyyy-MM-dd";

    private String offsetDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private String pattern="([\\.\\w]+?)(::|!:|<<|>>|>:|<:|<>|!!)([\\w\\s\\.\\(\\)\\-А-яЁё<>|]+?);";


    private Pattern filterPattern;
    private DateTimeFormatter localDateFormatter;
    private DateTimeFormatter offsetDateTimeFormatter;

    public FilterProperties() {
        filterPattern = Pattern.compile(pattern);
        localDateFormatter = DateTimeFormatter.ofPattern(localDateFormat);
        offsetDateTimeFormatter = DateTimeFormatter.ofPattern(offsetDateTimeFormat);
    }

    public Object clone()
    {
        FilterProperties properties = new FilterProperties();
        properties.setPattern(pattern);
        properties.setFieldSeparator(fieldSeparator);
        properties.setLevelSeparator(levelSeparator);
        properties.setLocalDateFormat(localDateFormat);
        properties.setOffsetDateTimeFormat(offsetDateTimeFormat);
        return properties;
    }

    public void setLocalDateFormat(String localDateFormat){
        this.localDateFormat = localDateFormat;
        localDateFormatter = DateTimeFormatter.ofPattern(localDateFormat);
    }

    public void setOffsetDateTimeFormat(String offsetDateTimeFormat){
        this.offsetDateTimeFormat = offsetDateTimeFormat;
        offsetDateTimeFormatter = DateTimeFormatter.ofPattern(offsetDateTimeFormat);
    }

    public void setPattern(String pattern){
        this.pattern = pattern;
        filterPattern = Pattern.compile(pattern);
    }
}
