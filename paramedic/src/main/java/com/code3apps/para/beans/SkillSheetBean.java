package com.code3apps.para.beans;

public class SkillSheetBean {

	private int _id = -1;
	
	private String skillName = "";
	
	private String fileName = "";
	
	private int s_order = -1;

	public int get_id() {
		return _id;
	}

	public void set_id(int id) {
		_id = id;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getS_order() {
		return s_order;
	}

	public void setS_order(int sOrder) {
		s_order = sOrder;
	}

}
