package io.github.lsyueh.getthepicture.cli.Core.Config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ForgeConfigBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> merged = new HashMap<>();

    public ForgeConfigBuilder addJsonFile(String path, boolean optional) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            if (!optional) {
                throw new IOException("Configuration file not found: " + path);
            }
            System.out.println("[config] skipped (not found): " + path);
            return this;
        }

        System.out.println("[config] loaded: " + file.getAbsolutePath());

        Map<String, Object> data = objectMapper.readValue(
            file,
            new TypeReference<Map<String, Object>>() {}
        );

        merged.putAll(data);  // 後加的覆蓋先加的
        
        return this;
    }

    public ForgeConfig build() {
        return new ForgeConfig(objectMapper, Map.copyOf(merged));
    }
}
