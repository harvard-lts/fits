package edu.harvard.hul.ois.fits.report;

public class ExtensionStat {
	String extension;
	int total;
	int valid;
	int notValid;
	int unknownValid;
	int withPUID;
	int withMimeType;
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getValid() {
		return valid;
	}
	public void setValid(int valid) {
		this.valid = valid;
	}
	public int getNotValid() {
		return notValid;
	}
	public void setNotValid(int notValid) {
		this.notValid = notValid;
	}
	public int getUnknownValid() {
		return unknownValid;
	}
	public void setUnknownValid(int unknownValid) {
		this.unknownValid = unknownValid;
	}
	public int getWithPUID() {
		return withPUID;
	}
	public void setWithPUID(int withPUID) {
		this.withPUID = withPUID;
	}
	public int getWithMimeType() {
		return withMimeType;
	}
	public void setWithMimeType(int withMimeType) {
		this.withMimeType = withMimeType;
	}
	
	
}
