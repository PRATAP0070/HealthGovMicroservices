package com.healthgov.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthgov.dto.AuditLogDto;

import com.healthgov.model.AduitLog;
import com.healthgov.repo.AuditLogRepo;


@Service
public class AuditLogServiceImpl implements AuditLogService {

	@Autowired
	private AuditLogRepo auditLogRepo;

	@Override
	public void createAuditLog(AuditLogDto dto, String status, String resource) {

		AduitLog auditLog = new AduitLog();
		auditLog.setUserId(dto.getUserId());;
		auditLog.setResource(resource);
		auditLog.setAction(status);
		auditLog.setTimestamp(new Date());

		auditLogRepo.save(auditLog);
	}

	@Override
	public List<AduitLog> getAuditLogsByUser(Long userId) {
		return auditLogRepo.findByUserId(userId);
	}
}