package de.hshannover.inform.trust.ironcontrol.view.irondetect;

public class GuiData {

	public enum GuiDataType {

		RULES,
		SIGNATURES,
		ANOMALY,
		CONDITIONS

	}

	private GuiDataType type;

	private String rowCount;

	private String device;

	private String id;

	private String value;

	private String timeStamp;

	public GuiDataType getType() {
		return type;
	}

	public void setType(GuiDataType type) {
		this.type = type;
	}

	public String getRowCount() {
		return rowCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String isValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

}
