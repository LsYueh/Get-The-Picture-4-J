package io.github.lsyueh.getthepicture.cli.Core.Config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.lsyueh.getthepicture.cli.Core.Config.Section.FieldOverride;

import java.util.Collections;
import java.util.Map;

public class ForgeConfig {

    private final Map<String, FieldOverride> fields;

    public ForgeConfig(ObjectMapper objectMapper, Map<String, Object> rawConfig) {
        Object section = rawConfig.get("fields");
        if (section == null) {
            this.fields = Collections.emptyMap();
        } else {
            Map<String, FieldOverride> result = objectMapper.convertValue(
                section,
                new TypeReference<Map<String, FieldOverride>>() {}
            );
            this.fields = Collections.unmodifiableMap(result);
        }
    }

    public Map<String, FieldOverride> fields() {
        return fields;
    }
}
