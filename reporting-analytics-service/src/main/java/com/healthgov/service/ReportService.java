package com.healthgov.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthgov.client.ProgramClient;
import com.healthgov.dto.ProgramDTO;
import com.healthgov.dto.ProgramReportMetrics;
import com.healthgov.enums.ReportScope;
import com.healthgov.model.Report;
import com.healthgov.repository.ReportRepository;

@Service
public class ReportService {

	private final ProgramClient programClient;
	private final ReportRepository reportRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public ReportService(ProgramClient programClient, ReportRepository reportRepository) {
		this.programClient = programClient;
		this.reportRepository = reportRepository;
	}

	public Report generateProgramReport() throws Exception {

		List<ProgramDTO> programs = programClient.getAllPrograms();

		long total = programs.size();
		long active = programs.stream().filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).count();
		long completed = programs.stream().filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus())).count();
		long upcoming = programs.stream().filter(p -> "UPCOMING".equalsIgnoreCase(p.getStatus())).count();

		ProgramReportMetrics metrics = new ProgramReportMetrics();
		metrics.setTotalPrograms(total);
		metrics.setActivePrograms(active);
		metrics.setCompletedPrograms(completed);
		metrics.setUpcomingPrograms(upcoming);

		Report report = new Report();
		report.setScope(ReportScope.PROGRAM);
		report.setMetrics(objectMapper.writeValueAsString(metrics));
		report.setGeneratedDate(LocalDateTime.now());

		return reportRepository.save(report);
	}
}