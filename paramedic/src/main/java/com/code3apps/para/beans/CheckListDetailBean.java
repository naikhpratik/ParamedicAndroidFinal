package com.code3apps.para.beans;

public class CheckListDetailBean {

	private int _id = -1;
	private String precheck = "";
	private String name = "";
	private String information1 = "";
	private String information2 = "";
	private String information3 = "";
	private String information4 = "";
	private int checked = 0;

	public int get_id() {
		return _id;
	}

	public void set_id(int id) {
		_id = id;
	}

	public String getPrecheck() {
		return precheck;
	}

	public void setPrecheck(String precheck) {
		this.precheck = precheck;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInformation1() {
		return information1;
	}

	public void setInformation1(String information1) {
		this.information1 = information1;
	}

	public String getInformation2() {
		return information2;
	}

	public void setInformation2(String information2) {
		this.information2 = information2;
	}

	public String getInformation3() {
		return information3;
	}

	public void setInformation3(String information3) {
		this.information3 = information3;
	}

	public String getInformation4() {
		return information4;
	}

	public void setInformation4(String information4) {
		this.information4 = information4;
	}

	public int getChecked() {
		return checked;
	}

	public void setChecked(int checked) {
		this.checked = checked;
	}

}
