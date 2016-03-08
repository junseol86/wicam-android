package com.wicam.d_default_handong_bus;

public class BusYgrData {

	private String s1 = "", s2 = "", s3 = "", s4 = "", s5 = "", s123 = "", s45 = "";
	String divider = "";
	Boolean hide_line = false, past = false;
	
	public BusYgrData(String s1, String s2, String s3, String s4, String s5, String s123, String s45, String divider, Boolean hide_line, boolean past) {
		
		super();
		
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		this.s4 = s4;
		this.s5 = s5;
		this.s123 = s123;
		this.s45 = s45;
		this.divider = divider;
		this.hide_line = hide_line;
		this.past = past;
	}

	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public String getS2() {
		return s2;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}

	public String getS3() {
		return s3;
	}

	public void setS3(String s3) {
		this.s3 = s3;
	}

	public String getS4() {
		return s4;
	}

	public void setS4(String s4) {
		this.s4 = s4;
	}

	public String getS5() {
		return s5;
	}

	public void setS5(String s5) {
		this.s5 = s5;
	}

	public String getS123() {
		return s123;
	}

	public void setS123(String s123) {
		this.s123 = s123;
	}

	public String getS45() {
		return s45;
	}

	public void setS45(String s45) {
		this.s45 = s45;
	}

	public String getDivider() {
		return divider;
	}

	public void setDivider(String divider) {
		this.divider = divider;
	}

	public Boolean getHide_line() {
		return hide_line;
	}

	public void setHide_line(Boolean hide_line) {
		this.hide_line = hide_line;
	}

	public Boolean getPast() {
		return past;
	}

	public void setPast(Boolean past) {
		this.past = past;
	}

}
