# aws_mfa_login
created by: etruyj

This is a simple JAVA script to simplify the process of CLI MFA login to an AWS account. It generally works with Linux, MacOS, and Windows although specific distro support may need to be included in the bash script to include specific OS support.
 
Basic command structure from the CLI is aws_mfa --token ######
Use -h or --help for help.

Requirements:
- Jave Runtime Environment
- This script requires the AWS CLI to be installed on the computer. 
- WINDOWS: An environmental variable attaching 'aws' to the path to the CLI exe is also required if it is not automatically configured on installation. Instructions can be found here: https://docs.aws.amazon.com/cli/latest/userguide/install-windows.html#awscli-install-windows-path

Process:

AWS is queried for the Serial Number of the MFA device. This requires a policy in place to allow "iam:ListMFADevices". 

AWS is then queried for temporary login credentials using the specified token and MFA Serial Number.

Google's Gson v 2.8.6 is used to parse Amazon's JSON responses into usable variables.

The .aws/credentials file is then updated with the temporary Access_Key, Secret_Key, and Session_Token. The script looks for a profile with the profile name specified by the script with an '_mfa' suffix. If this profile does not exist, it is appended at the end of the file. AWS MFA CLI access is then available using that 'profile'_mfa profile when making CLI commands until the token expires.
