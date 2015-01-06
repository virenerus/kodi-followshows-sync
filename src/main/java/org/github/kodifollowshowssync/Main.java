package org.github.kodifollowshowssync;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	public static void main(String[] args) throws ParseException, MalformedURLException, IOException {
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error");
		CommandLineParser parser = new BasicParser();
		Options options=new Options();
		options.addOption("mail", true, "account mail address");
		options.addOption("pw", true, "account password");
		CommandLine cmd=parser.parse(options, args);
		
		if(cmd.hasOption("mail") && cmd.hasOption("pw")) {
			new Syncer(cmd.getOptionValue("mail"),cmd.getOptionValue("pw")).sync();
		}
		else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "kodi-followshows-sync", options );
		}
	}
}
