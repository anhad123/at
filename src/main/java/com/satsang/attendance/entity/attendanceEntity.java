package com.satsang.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "attendance")
public class attendanceEntity {

	@Id
	@Column(name = "S.No")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Sno;
	
	@Column(name = "time")
	private String time;
	
	@Column(name = "id")
	private String id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "doi")
	private String doi;

	public Long getSno() {
		return Sno;
	}

	public void setSno(Long sno) {
		Sno = sno;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	@Override
	public String toString() {
		return "attendanceEntity [Sno=" + Sno + ", time=" + time + ", id=" + id
				+ ", name=" + name + ", doi=" + doi + "]";
	}
	
}
