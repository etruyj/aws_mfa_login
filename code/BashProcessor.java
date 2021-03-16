//=================================================================
// BashProcessor.java
//
// DESCRIPTION:
// 	This script handles all calls to the bash.
//=================================================================

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.lang.ProcessBuilder;
import java.lang.Process;
import java.lang.StringBuffer;
import java.lang.StringBuilder;

class BashProcessor
{
	private ProcessBuilder processor = new ProcessBuilder();
	private Gson gson = new Gson();
	private MFADevice mfa_device = new MFADevice();
	private MFACredentials mfa_credentials = new MFACredentials();
	private String errorMessage = "none";

	//============================================================
	// Functions
	//============================================================

	public String executeProcess(ProcessBuilder pb)
	{
		String returnJson = "none";

		try
		{
			Process process = processor.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder outJson = new StringBuilder();


			String output = null;
			while((output = stdInput.readLine()) != null)
			{
				outJson.append(output);
			}
			
			returnJson = outJson.toString();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}

		return returnJson;	
	}

	public void getMFACredentialsLinux(String aws_profile, String aws_token, String mfa_arn)
	{
		String json = "none";
		String cmd = "aws sts get-session-token --serial-number " + mfa_arn + " --token-code " + aws_token + " --profile " + aws_profile;

		processor.command("bash", "-c", cmd);

		json = executeProcess(processor);
		
		// Check to see if a blank value was returned for error handling.
		// Despite AWS CLI providing an error message in the event of a 
		// failure it doesn't feed back into this code, so I have to make
		// do with a blank response.
		if(json.equals(""))
		{
			System.out.println("There was a problem validating the MFA token. Please verify the correct token was entered and proper AWS permissions are assigned to your account.");
		}
		else
		{
			mfa_credentials = gson.fromJson(json, MFACredentials.class);
		}
	}

	public void getMFACredentialsWindows(String aws_profile, String aws_token, String mfa_arn)
	{
		String json = "none";
		String cmd = "aws sts get-session-token --serial-number " + mfa_arn + " --token-code " + aws_token + " --profile " + aws_profile;

		processor.command("cmd.exe", "/c", cmd);

		json = executeProcess(processor);

		// Check to see if a blank value was returned for error handling.
		// Despite AWS CLI providing an error message in the event of a 
		// failure it doesn't feed back into this code, so I have to make
		// do with a blank response.
		if(json.equals(""))
		{
			System.out.println("There was a problem validating the MFA token. Please verify the correct token was entered and proper AWS permissions are assigned to your account.");
		}
		else
		{
			mfa_credentials = gson.fromJson(json, MFACredentials.class);
		}
	}
	
	public void getMFADeviceLinux(String aws_profile)
	{
		String json = "none";
		String cmd = "aws iam list-mfa-devices --profile " + aws_profile;
		processor.command("bash", "-c", cmd);

		json = executeProcess(processor);
	
		// Check to see if a blank value was returned for error handling.
		// Despite AWS CLI providing an error message in the event of a 
		// failure it doesn't feed back into this code, so I have to make
		// do with a blank response.
		if(json.equals(""))
		{
			System.out.println("There was an error retrieving your MFA devices frow AWS. Please ensure one is configured for " + aws_profile + " and adequate permissions are assigned.");
		}
		else
		{
			mfa_device = gson.fromJson(json, MFADevice.class);
		}
	}

	public void getMFADeviceWindows(String aws_profile)
	{
		String json = "none";
		String cmd = "aws iam list-mfa-devices --profile " + aws_profile;
		processor.command("cmd.exe", "/c", cmd);

		json = executeProcess(processor);
		
		// Check to see if a blank value was returned for error handling.
		// Despite AWS CLI providing an error message in the event of a 
		// failure it doesn't feed back into this code, so I have to make
		// do with a blank response.
		if(json.equals(""))
		{
			System.out.println("There was an error retrieving your MFA devices from AWS. Please ensure one is configured for " + aws_profile + " and adequate permissions are assigned.");
		}
		else
		{
			mfa_device = gson.fromJson(json, MFADevice.class);
		}
	}

	public void mfaLoginLinux(String aws_profile, String aws_token)
	{
		// This is the primary function for executing these scripts in a Linux/MacOs environment.

		// In order to authenticate with MFA, we need the Serial Number of the MFA device.
		// Fundementally, this is the Amazon Resource Number (ARN) for the device.
		getMFADeviceLinux(aws_profile);

		// Using this information, we'll authenticate our token with AWS.
		// First verify an mfa serial number was returned in the last call.
		if(!mfa_device.getSerialNumber().equals("none"))
		{
			getMFACredentialsLinux(aws_profile, aws_token, mfa_device.getSerialNumber());
		}

		// Update the aws credentials file
		// First verify a session token was retrieved from AWS in the last call.
		if(!mfa_credentials.getSessionToken().equals("none")&&!mfa_device.getSerialNumber().equals("none"))
		{
			String filePath = System.getProperty("user.home") + "/.aws/credentials";
			writeToFile(aws_profile, mfa_credentials.getAccessKey(), mfa_credentials.getSecretKey(), mfa_credentials.getSessionToken(), filePath);
		}
		// !!!! NOT VALID !!!!
		// Using global vars in BASH doesn't work if a default profile
		// is set. As this code anticipates the conditition, this is not
		// a valid resolution. As all functions are already written.
		// We'll leave this section as is.
		// Clear out the old global vars just in case.
		//unsetGlobalVarsLinux();

		// Set the MFA Credentials as new Global Vars.
		//setGlobalVarsLinux(mfa_credentials.getAccessKey(), mfa_credentials.getSecretKey(), mfa_credentials.getSessionToken());
	}

	public void mfaLoginWindows(String aws_profile, String aws_token)
	{
		// This is the primary function for executing these scripts in a Linux/MacOs environment.

		// In order to authenticate with MFA, we need the Serial Number of the MFA device.
		// Fundementally, this is the Amazon Resource Number (ARN) for the device.
		getMFADeviceWindows(aws_profile);

		// Using this information, we'll authenticate our token with AWS.
		// First test to see if AWS returned an MFA serial from the prior call.
		if(!mfa_device.getSerialNumber().equals("none"))
		{
			getMFACredentialsWindows(aws_profile, aws_token, mfa_device.getSerialNumber());
		}

		// Update the aws credentials file
		// First verify a session token was retrieved from AWS in the last call.
		if(!mfa_credentials.getSessionToken().equals("none"))
		{
			String filePath = System.getProperty("user.home") + "/.aws/credentials";
			writeToFile(aws_profile, mfa_credentials.getAccessKey(), mfa_credentials.getSecretKey(), mfa_credentials.getSessionToken(), filePath);
		}
	}

	private void setAccessKeyLinux(String aws_access_key)
	{
		// This function sets the Access Key global variable.
		String cmd = "export AWS_ACCESS_KEY_ID=" + aws_access_key;

		processor.command("bash", "-c", cmd);

		executeProcess(processor);
	}

	private void setGlobalVarsLinux(String aws_access_key, String aws_secret_key, String aws_session_token)
	{
		// This function sets the AWS MFA global variables in
		// Bash.
		setAccessKeyLinux(aws_access_key);
		setSecretKeyLinux(aws_secret_key);
		setSessionTokenLinux(aws_session_token);
	}

	private void setSecretKeyLinux(String aws_secret_key)
	{
		// This function sets the Secret Key global variable.
		String cmd = "export AWS_SECRET_ACCESS_KEY=" + aws_secret_key;

		processor.command("bash", "-c", cmd);

		executeProcess(processor);
	}

	private void setSessionTokenLinux(String aws_session_token)
	{
		// This function sets the session token global variable.

		String cmd = "export AWS_SESSION_TOKEN=" + aws_session_token;

		processor.command("bash", "-c", cmd);

		executeProcess(processor);
	}

	private void unsetAccessKeyLinux()
	{
		// This function unsets the Access Key global variable.
		String cmd = "unset AWS_ACCESS_KEY_ID";

		processor.command("bash", "-c", cmd);

		executeProcess(processor);
	}

	private void unsetGlobalVarsLinux()
	{
		// This function unsets the AWS MFA global variables in
		// Bash.
		unsetAccessKeyLinux();
		unsetSecretKeyLinux();
		unsetSessionTokenLinux();
	}

	private void unsetSecretKeyLinux()
	{
		// This function unsets the Secret Key global variable.
		String cmd = "unset AWS_SECRET_ACCESS_KEY";

		processor.command("bash", "-c", cmd);

		executeProcess(processor);
	}

	private void unsetSessionTokenLinux()
	{
		// This function unsets the session token global variable.

		String cmd = "unset AWS_SESSION_TOKEN";

		processor.command("bash", "-c", cmd);

		executeProcess(processor);
	}

	private void writeToFile(String aws_profile, String aws_access_key, String aws_secret_key, String aws_session_token, String filePath)
	{
		// This updates the credentials file with the temporary access
		// credentials provided by AWS. It searches for a field with
		// the profile followed but _mfa. If that field isn't found,
		// new text is appended to the file.

		try
		{
			Boolean updatingCredentials = false;
			Boolean credentialsUpdated = false;
			int lineCounter = 0; // Track the number of lines to filter out.
			String profileTitle = "[" + aws_profile + "_mfa]";

			BufferedReader file = new BufferedReader(new FileReader(filePath));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null)
			{
				if(line.equals(profileTitle))
				{
					updatingCredentials = true;
				}

				if(!updatingCredentials)
				{
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
				else
				{
					switch (lineCounter)
					{
						case 0:
							inputBuffer.append(profileTitle);
							break;
						case 1:
							inputBuffer.append("aws_access_key_id = " + aws_access_key);
							break;
						case 2:
							inputBuffer.append("aws_secret_access_key = " + aws_secret_key);
							break;
						case 3:
							inputBuffer.append("aws_session_token = " + aws_session_token);
							break;
					}
					inputBuffer.append('\n');
					lineCounter++;
					
					// Stop filtering out inputs once we complete the update.
					if(lineCounter==4)
					{
						updatingCredentials = false;
						credentialsUpdated = true;
					}
				}
			}

			if(!credentialsUpdated)
			{
				inputBuffer.append('\n');
				inputBuffer.append(profileTitle);
				inputBuffer.append('\n');
				inputBuffer.append("aws_access_key_id = " + aws_access_key);
				inputBuffer.append('\n');
				inputBuffer.append("aws_secret_access_key = " + aws_secret_key);
				inputBuffer.append('\n');
				inputBuffer.append("aws_session_token = " + aws_session_token);
				inputBuffer.append('\n');
			}

			file.close();
			String outputString = inputBuffer.toString();

			// Write the new file back to the original location.
			FileOutputStream outFile = new FileOutputStream(filePath);
			outFile.write(outputString.getBytes());
			outFile.close();
			
			// Print success completion message  here as there is no 
			// way to test for success in the calling function.
			// Let the user know how to use mfa credentials.
			System.out.println("Success. MFA Credentials were obtained. MFA access can be achieved with the " + aws_profile + "_mfa profile.");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
