package hl.nio.codec;

public enum Category {
	
	BuildIn(0x0100),
	UserCustom(0x0200),
	Unknown(0x0000);
	private final short value;
	
	Category(int value) {
		this.value = (short)value;
	}
	
	public short value() {
		return value;
	}
	
	public static Category valueOf(short value) {
		for(Category version : values()) {
			if(version.value == value) {
				return version;
			}
		}
		return Unknown;
	}
	
}
