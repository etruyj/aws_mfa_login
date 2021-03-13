//=================================================================
// BashProcessor.java
//
// DESCRIPTION:
// 	This script handles all calls to the bash.
//=================================================================

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.lang.StringBuilder;

class BashProcessor
{
	private ProcessBuilder processor = new ProcessBuilder();
	private Gson gson = new Gson();
	private MFADevice mfa_device = new MFADevice();
	private MFACredentials mfa_credentials = new MFACredentials();

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

		mfa_credentials = gson.fromJson(json, MFACredentials.class);
	}

	public void getMFADeviceLinux(String aws_profile)
	{
		String json = "none";
		String cmd = "aws iam list-mfa-devices --profile " + aws_profile;
		processor.command("bash", "-c", cmd);

		json = executeProcess(processor);

		mfa_device = gson.fromJson(json, MFADevice.class);

	}

	public void mfaLoginLinux(String aws_profile, String aws_token)
	{
		// This is the primary function for executing these scripts in a Linux/MacOs environment.

		// In order to authenticate with MFA, we need the Serial Number of the MFA device.
		// Fundementally, this is the Amazon Resource Number (ARN) for the device.
		getMFADeviceLinux(aws_profile);
		System.out.println(mfa_device.getSerialNumber());

		// Using this information, we'll authenticate our token with AWS.
		getMFACredentialsLinux(aws_profile, aws_token, mfa_device.getSerialNumber());
		System.out.println(mfa_credentials.getSecretKey());
	
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

}
