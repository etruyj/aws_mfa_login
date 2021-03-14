# aws_mfa_login
created by: etruyj


This is a simple script to simplify the process of CLI MFA login to an AWS account. This script requires the AWS CLI to be installed on the computer. 

AWS is queried for the Serial Number of the MFA device. This requires a policy in place to allow "iam:ListMFADevices" on the "arn:ws:iam::*:user/${aws:username} resource.

AWS is then queried for temporary login credentials using the specified token and MFA Serial Number.

Google's Gson v 2.8.6 is used to parse Amazon's JSON responses into usable variables.

The .aws/credentials file is then updated with the temporary Access_Key, Secret_Key, and Session_Token. The script looks for a profile with the profile name specified by the script with an '_mfa' suffix. If this profile does not exist, it is appended at the end of the file. AWS MFA CLI access is then available using that 'profile'_mfa profile when making CLI commands until the token expires.
