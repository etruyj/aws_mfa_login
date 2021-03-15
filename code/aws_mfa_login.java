//======================================================================
// aws_mfa_login.java
//
// DESCRIPTION:
// 	This script is designed to handle mfa login for AWS. It'll use 
// 	profile credentials in the .aws/configure file along with a token
// 	to query AWS for MFA credentials and token. Global variables
// 	are then used in the shell to allow MFA access to the account 
// 	with standard AWS CLI commands.
//======================================================================

import java.io.*;

public class aws_mfa_login
{
	public static void main(String[] args) 
	{
		FlagParser cmds = new FlagParser();
		BashProcessor bp = new BashProcessor();

		cmds.parseFlags(args);
		
		if(cmds.getExitScript())
		{
			cmds.printMessage();
		}
		else
		{
			if(System.getProperty("os.name").substring(0, 7).equals("Windows"))
			{
				// Execute Windows Commands
				bp.mfaLoginWindows(cmds.getAwsProfile(), cmds.getAwsToken());
			}
			else
			{
				// Execute Linux/MacOs Commands
				bp.mfaLoginLinux(cmds.getAwsProfile(), cmds.getAwsToken());
			}
		}
	}

}
