//=========================================================================
// MFADevices.java
//
// DESCRIPTION
// 	This is a target class for Google Gson's fromJson function.
//=========================================================================

class MFADevice
{
	DeviceParameters[] MFADevices = new DeviceParameters[1];
	
	// Had to create a constructor because I was having problems accessing
	// default values in the DeviceParameters class. Created this constructor
	// and the associated method requirements in both classes in order to 
	// get a default value of "none" for the SerialNumber variable to use
	// for error checking.
	//
	// Not sure if AWS IAM LIST-MFA-DEVICES lists more than one device, so I 
	// wanted this to be an array.
	//
	// This is probably a JAVA thing I was unaware of, but the call for
	// new DeviceParameters[] only reserved space in memory for the array,
	// but didn't actually initalize it. I had to specifically initialize
	// index [0] as new DeviceParameters() for the variabes to be assigned
	// to their defaults and not be null.
	
	public MFADevice()
	{
		MFADevices[0] = new DeviceParameters();
	}

	public String getUserName() { return MFADevices[0].getUserName(); }
	public String getSerialNumber() { return MFADevices[0].getSerialNumber(); }
	public String getEnableDate() { return MFADevices[0].getEnableDate(); }

	// Inner Class
	class DeviceParameters
	{
		String UserName = "none";
		String SerialNumber = "none";
		String EnableDate = "none";

		public String getUserName() { return UserName; }
		public String getSerialNumber() { return SerialNumber; }
		public String getEnableDate() { return EnableDate; }
	}
}

