package com.healthgov.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.model.AduitLog;
import com.healthgov.service.AuditLogService;

@RestController
@RequestMapping("/audit_log")
public class AuditLogApi {
	
	@Autowired
	private AuditLogService auditLogService;

	 @GetMapping("/getLogById/{userId}")
	    public List<AduitLog> getAuditLogs(@PathVariable Long userId) {
	        return auditLogService.getAuditLogsByUser(userId);
	    }
}
