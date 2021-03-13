//=========================================================================
// MFADevices.java
//
// DESCRIPTION
// 	This is a target class for Google Gson's fromJson function.
//=========================================================================

class MFADevice
{
	DeviceParameters[] MFADevices;

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

