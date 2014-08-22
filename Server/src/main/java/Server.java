import com.google.gson.JsonObject;
import org.apache.commons.cli.*;

import java.text.SimpleDateFormat;
import java.util.*;

// Created by Khashayar Abdouli on 8/22/14.
//

public class Server {

    /**
     * The main entry point.
     */
    public static void main(String[] args) throws Exception{
        Options options = buildCommandLineOptions();

        CommandLineParser parser = commandLineParser();
        CommandLine cmdLine = parseCommandLine(parser, options, args);

        String user = cmdLine.getOptionValue("user");
        String pass = cmdLine.getOptionValue("pass");

        SimpleDateFormat initialDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String speedString = cmdLine.getOptionValue("speed", "1");

        double speed = Double.parseDouble(speedString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDateFormat.parse(cmdLine.getOptionValue("startdate", initialDateFormat.format(calendar.getTime()))));

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(initialDateFormat.parse(cmdLine.getOptionValue("enddate", "2020-12-31")));

        TimePlayer timePlayer = new TimePlayer(calendar , speed);


        long sleepTime;
        boolean running = true;
        while(running){
            calendar = timePlayer.getCurrentDate();
            System.out.println("\t\t\tDate for :" + calendar.getTime());
            Calendar before = Calendar.getInstance();
            before.setTimeInMillis(calendar.getTimeInMillis() + 3300000);
            Calendar after = Calendar.getInstance();
            after.setTimeInMillis(calendar.getTimeInMillis() + 3900000);

            String minDate = dateFormat.format(before.getTime());
            String maxDate = dateFormat.format(after.getTime());
            String date = dateFormat.format(calendar.getTime());

            HashMap<String, JsonObject> alerts = Couch.fetchAlert(minDate, maxDate);

            Iterator<Map.Entry<String, JsonObject>> iterator = alerts.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, JsonObject> entry = iterator.next();

                String user_id = entry.getKey();
                JsonObject message = entry.getValue();


                String recipientsMail = Couch.fetchMail(user_id);
                if(recipientsMail.isEmpty()){
                    System.out.println("No mail has been set");
                }
                else{
                    Mail.sendMail(user, pass, recipientsMail, message);
                }
                iterator.remove();
            }

            String nextDate = Couch.getNextDate(date);
            Calendar next = Calendar.getInstance();
            next.setTimeInMillis(dateFormat.parse(nextDate).getTime());
            sleepTime = Math.max(timePlayer.sleepTime(next) , 1);


            Thread.sleep(sleepTime);

            if(calendar.after(endDate))
                running = false;
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

        Option		speed = new Option("s", "speed", true, "playback speed");
        options.addOption(speed);

        Option		startDate = new Option("sd", "startdate", true, "initial date");
        options.addOption(startDate);

        Option		endDate = new Option("ed", "enddate", true, "end date");
        options.addOption(endDate);

        Option		user = new Option("u", "user", true, "mail username that notification will be send from");
        options.addOption(user);

        Option		pass = new Option("p", "pass", true, "password of the specified mail");
        options.addOption(pass);
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
