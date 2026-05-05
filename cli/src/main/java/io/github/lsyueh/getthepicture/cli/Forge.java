package io.github.lsyueh.getthepicture.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.lsyueh.getthepicture.cli.Core.Config.ForgeConfig;
import io.github.lsyueh.getthepicture.cli.Core.Config.ForgeConfigBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "forge",
    mixinStandardHelpOptions = true,
    description = "Process COBOL copybook files."
)
public class Forge implements Callable<Integer> {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Option(
        names = {"-c", "--copybook"},
        required = true,
        description = "Path to the copybook file."
    )
    private Path copybook;

    @Option(
        names = {"-v", "--verbose"},
        description = "Enable verbose output."
    )
    private boolean verbose;

    @Option(
        names = {"--with-renames"},
        description = "Enable generation of properties with renamed field names (Level 66)."
    )
    private boolean withRenames66;

    @Override
    public Integer call() {
        if (!copybook.toFile().exists()) {
            System.err.println("Copybook not found: " + copybook.toAbsolutePath());
            return 1;
        }

        try {
            ForgeConfig config = buildConfig();

            // debug
            ObjectMapper debugMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(debugMapper.writeValueAsString(config.fields()));

            return 0;
        } catch (Exception e) {
            System.err.println("Forge execution failed.");
            System.err.println(e.getMessage());

            if (Boolean.getBoolean("forge.debug")) {
                e.printStackTrace(System.err);
            }

            return 1;
        }
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Forge()).execute(args));
    }

    private ForgeConfig buildConfig() throws IOException {
        String localConfig = changeExtension(copybook.toString(), ".forge.json");

        ForgeConfig config = new ForgeConfigBuilder()
            .addJsonFile("forge.json", true) // global config
            .addJsonFile(localConfig, true) // per copybook config
            .build();

        if (new File(localConfig).exists()) {
            System.out.println("⚠ Local configuration detected: " + localConfig);
        }

        return config;
    }

    private static String changeExtension(String path, String newExtension) {
        int dot = path.lastIndexOf('.');
        String base = dot >= 0 ? path.substring(0, dot) : path;
        return base + newExtension;
    }
}
