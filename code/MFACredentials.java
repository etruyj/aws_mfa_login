//=========================================================================
// MFACredentials.java
//
// DESCRIPTION
// 	This is a target class for Google Gson's fromJson function.
//=========================================================================

class MFACredentials
{
	AWSCredentials Credentials;

	public String getAccessKey() { return Credentials.getAccessKey(); }
	public String getSecretKey() { return Credentials.getSecretKey(); }
	public String getSessionToken() { return Credentials.getSessionToken(); }
	public String getExpiration() { return Credentials.getExpiration(); }

	// Inner Class
	private class AWSCredentials
	{
		String AccessKeyId = "none";
		String SecretAccessKey = "none";
		String SessionToken = "none";
		String Expiration = "never";

		public String getAccessKey() { return AccessKeyId; }
		public String getSecretKey() { return SecretAccessKey; }
		public String getSessionToken() { return SessionToken; }
		public String getExpiration() { return Expiration; }
	}
}
