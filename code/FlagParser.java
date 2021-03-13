//======================================================================
// FlagParser.java
//
// DESCRIPTION:
// 	This script is designed to handle information about and parsing
// 	the different flags for the aws_mfa_login.java script.
//======================================================================

import java.io.*;

class FlagParser
{
	private String aws_profile = "default";
	private String aws_token = "none";
	private String errorMessage = "none";
	private Boolean exitScript = false;
	
	public String getAwsProfile() { return aws_profile; }
	public String getAwsToken() { return aws_token; }
	public Boolean getExitScript() { return exitScript; }

	public void parseFlags(String[] args)
	{
		// parseFlags
		// 	The purpose of this function is to parse the flags presented
		// 	in the command line execution of this function. This is done
		// 	via a switch statement in a while loop to allow for both boolean
		// 	and variable flags.

		int i = 0;


		while(i< args.length)
		{
			switch(args[i])
			{
				case "-h":
				case "--help":
					// Print instructions.
					printHelp();
					i=i+1000; // just to break the while loop.
					exitScript = true;
					errorMessage = ""; // Print nothing.
					break;
				case "-p":
				case "--profile":
					// Check to see if the flag was the last value passed and that there is another value after it.
					if(args.length>i+1)
					{
						// Grab value associated with the flag.
						aws_profile = args[i+1];
						// Increment twice to move to next flag.
						i = i + 2;
					}
					else
					{
						// No value is associated with the flag. Increment through and end increment.
						i++;
					}
					break;
				case "-t":
				case "--token":
					// Check to see if the flag was the last value passed and that there is another value after it.
					if(args.length>i+1)
					{
						// Grab value associated with the flag.
						aws_token = args[i+1];
						// Increment twice to move to next flag.
						i = i + 2;
					}
					else
					{
						// No value is associated with the flag. Increment through and end increment.
						i++;
					}
					break;
				default:
					// Not a valid flag.
					// Move onto the next one.
					i++;
					break;
			}
		}

		// Error Handling
		// Checking to see if no flags were entered.
		if(args.length<=1 && !exitScript)
		{
			exitScript = true;
			errorMessage = "No valid inputs selected.";
		}

		// Check to see if no token was entered.
		if(aws_token=="none" && !exitScript)
		{
			exitScript = true;
			errorMessage = "No token was entered. The purpose of this script is to simplify MFA access to your AWS account. If you're not using MFA with your AWS account, AWS can be accessed with the aws CLI command."; 
		}
		
	}	

	public void printHelp()
	{
		// Prints help instructions and the flag definitions.
		System.out.println("\nAWS_MFA_Login");
		System.out.println("===========================================\n");
		System.out.println("-h, --help\tPrint help instructions");
		System.out.println("-p, --profile\tSpecify a named profile listed");
		System.out.println("\t\tin the aws/configure file to use that");
		System.out.println("\t\taccess id and secret key instead of the");
		System.out.println("\t\tthe default. If non value is specified, the");
		System.out.println("\t\tdefault values will be used.");
		System.out.println("-t, --token\tSpecify the MFA token to be used");
		System.out.println("\t\tto verify an identify with AWS. This flag");
		System.out.println("\t\tis required.");
	}

	public void printMessage()
	{
		System.out.println(errorMessage);
	}
}
