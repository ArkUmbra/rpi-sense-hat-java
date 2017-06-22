package rpi.sense.hat.executor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by jcincera on 20/06/2017.
 */
public class SingleCommandExecutor implements CommandExecutor {

    private String lineSeparator = System.getProperty("line.separator");

    @Override
    public CommandResult execute(Command command) {
        try {

            // Create command
            final String completeCommand = createCompleteCommand(command);

            // Call
            ProcessBuilder pb = new ProcessBuilder("python", "-c", completeCommand);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            // Read output
            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = output.readLine()) != null) {
                result.append(line);
                result.append(lineSeparator);
            }
            System.out.println("Command result: " + result.toString());

            // Handle result
            p.waitFor();
            waitForCommand();
            checkCommandException(result);
            return new CommandResult(result.toString());
        }
        catch (Exception e) {
            System.err.println(e);
            throw new CommandException(e);
        }
    }

    private void checkCommandException(StringBuilder result) {
        if (result.toString().contains("Traceback") || result.toString().contains("Error")) {
            throw new CommandException("Command execution failed!\n" + result.toString());
        }
    }

    private String createCompleteCommand(Command command) {
        return Command.IMPORT_SENSE_HAT.getCommand() + ";" +
                Command.SENSE_OBJECT.getCommand() + ";" +
                command.getCommand();
    }

    private void waitForCommand() {
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
