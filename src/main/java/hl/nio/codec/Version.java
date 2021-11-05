package hl.nio.codec;

public enum Version {
	V1_0(0x0100),
	Unknown(0x0000);
	private final short value;

	Version(int value) {
		this.value = (short)value;
	}
	
	public short value() {
		return value;
	}
	
	public static Version valueOf(short value) {
		for(Version version : values()) {
			if(version.value == value) {
				return version;
			}
		}
		return Unknown;
	}
	
}
