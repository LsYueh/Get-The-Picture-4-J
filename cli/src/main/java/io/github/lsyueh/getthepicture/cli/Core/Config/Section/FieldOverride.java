package io.github.lsyueh.getthepicture.cli.Core.Config.Section;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FieldOverride(
    @JsonProperty("type")    String type,
    @JsonProperty("propertyName") String propertyName,
    @JsonProperty("comment") String comment
) {}
