package com.satsang.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.satsang.attendance.entity.attendanceEntity;
@Repository
public interface attendanceDataRepository extends JpaRepository<attendanceEntity,Long> {
	List<attendanceEntity> findById(String id);
	

}
