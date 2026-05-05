package io.github.lsyueh.getthepicture.cli;

import java.nio.file.Path;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "forge",
    mixinStandardHelpOptions = true,
    description = "Process COBOL copybook files."
)
public class Forge implements Runnable {

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
    public void run() {
        // Main logic is here....
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Forge()).execute(args));
    }
}
