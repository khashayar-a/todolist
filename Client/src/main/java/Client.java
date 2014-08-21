import org.apache.commons.cli.*;

// Created by Khashayar Abdouli on 8/21/14.
//
public class Client {

    /**
     * enum for different modes
     */
    private enum Mode {
        help, add, view, signup
    }

    /**
     * The main entry point.
     */
    public static void main(String[] args) {


        Options options = buildCommandLineOptions();

        CommandLineParser parser = commandLineParser();
        CommandLine cmdLine = parseCommandLine(parser, options, args);

        String mode = cmdLine.getOptionValue("mode", "add");

        String user = cmdLine.getOptionValue("user");
        String pass = cmdLine.getOptionValue("pass");

        String userID = "";
        switch (Mode.valueOf(mode)){
            case help:
                System.out.println("Specify mode");
                break;
            case signup:
                Actions.signup();
                break;
            case add:
                userID = Couch.signIn(user.toLowerCase(), pass);
                if(userID.isEmpty())
                    System.err.println("Wrong Username or Password");
                else
                    Actions.add(userID);
                break;
            case view:
                userID = Couch.signIn(user.toLowerCase(), pass);
                if(userID.isEmpty())
                    System.err.println("Wrong Username or Password");
                else
                    Actions.view(userID);
                break;
        }

    }

    /**
     * Create the options used to parse the command line arguments.
     */
    private static Options buildCommandLineOptions()
    {
        Options		options = new Options();

        Options		derivedOptions = commandLineOptions();
        if(derivedOptions != null)
        {
            for(Object option : derivedOptions.getOptions())
            {
                options.addOption( (Option)option );
            }
        }

        return options;
   }

    /**
     * Return the command line options that should be used to parse the application's
     * command line arguments.
     */
    private static Options commandLineOptions()
    {
        Options		options = new Options();

        Option		mode = new Option("m", "mode", true, "Mode specified");
        options.addOption(mode);

        Option		user = new Option("u", "user", true, "Mode specified");
        options.addOption(user);
/**
 *
 * @author root
 */
        Option		pass = new Option("p", "pass", true, "Mode specified");
        options.addOption(pass);

        Option		directory = new Option("d", "dir", true, "Directory ");
        options.addOption(directory);

        return options;
    }


    /**
     * Return the command line parser to use.
     */
    private static CommandLineParser commandLineParser()
    {
        return new PosixParser();
    }

    /**
     * Parse the command line arguments.
     * @param parser The command line parser to use.
     * @param args The command line arguments that was supplied to the application.
     * @return The parsed command line instance.
     */
    public static CommandLine parseCommandLine(CommandLineParser parser, Options options, String[] args)
    {
        try
        {
            return parser.parse(options, args, false);
        }
        catch(ParseException ex)
        {
            System.err.println("Command line parsing failed. Reason: " + ex.getMessage());
        }

        return null;
    }
}
