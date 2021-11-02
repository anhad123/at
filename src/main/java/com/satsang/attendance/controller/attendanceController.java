package com.satsang.attendance.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.satsang.attendance.entity.attendanceEntity;
import com.satsang.attendance.entity.resultEntity;
import com.satsang.attendance.service.attendanceDataService;

@CrossOrigin(origins="http://localhost:4200")
//@CrossOrigin(origins="http://lucknowbranchattendance.s3-website-us-east-1.amazonaws.com")
@RestController
@RequestMapping(value = "/lucknow")
public class attendanceController {

	@Autowired
	attendanceDataService ads;
	
	@GetMapping("/getAllAttendence")
	public List<attendanceEntity> getAllStoreMembers() {
		return ads.getAllData();
	}
	
	@GetMapping("/getAllAttendence/{uid}")
	public List<attendanceEntity> getAllDataByUid(@PathVariable String uid) {
		return ads.getDataById(uid);
	}
	
	@GetMapping("/getAllAttendenceByTime/{time}")
	public List<attendanceEntity> getAllDataByTime(@PathVariable String time) throws ParseException {
		//   Date date = new Date();  
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
		Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(time);  //  2021-05-23 ---> 23-05-2021
		 String strDate= formatter.format(date1); 
		time=time.replace("-", "/");
		return ads.getDataByDate(strDate);
	}
	
	@GetMapping("/addData")
	public List<resultEntity> addData() throws GeneralSecurityException, IOException{
		resultEntity re = new resultEntity();
		List<resultEntity> rl =new ArrayList<>(); 
		re.setResult(ads.addData());
		rl.add(re);	
		return (rl);
	}
}
