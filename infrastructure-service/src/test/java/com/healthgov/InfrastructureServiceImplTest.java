package com.healthgov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.healthgov.dto.InfrastructureCreateRequest;
import com.healthgov.dto.InfrastructureResponse;
import com.healthgov.dto.InfrastructureUpdateRequest;
import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.exceptions.InfrastructureNotFoundException;
import com.healthgov.model.Infrastructure;
import com.healthgov.repository.InfrastructureRepository;
import com.healthgov.service.InfrastructureServiceImpl;

class InfrastructureServiceImplTest {

	@Mock
	private InfrastructureRepository infraRepo;

	@InjectMocks
	private InfrastructureServiceImpl infrastructureService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// ---------------- CREATE ----------------

	@Test
	void createInfrastructure_success() {

		InfrastructureCreateRequest request = new InfrastructureCreateRequest();
		request.setProgramId(1L);
		request.setType(InfrastructureType.HOSPITAL);
		request.setLocation("Chennai");
		request.setCapacity(300);
		request.setStatus(InfrastructureStatus.OPERATIONAL);

		Infrastructure savedInfra = new Infrastructure();
		savedInfra.setInfraId(10L);
		savedInfra.setProgramId(1L);
		savedInfra.setType(request.getType());
		savedInfra.setLocation(request.getLocation());
		savedInfra.setCapacity(request.getCapacity());
		savedInfra.setStatus(request.getStatus());

		when(infraRepo.save(any(Infrastructure.class))).thenReturn(savedInfra);

		InfrastructureResponse response = infrastructureService.createInfrastructure(request);

		assertEquals(10L, response.getInfraId());
		assertEquals("Chennai", response.getLocation());
		assertEquals(InfrastructureStatus.OPERATIONAL, response.getStatus());

		verify(infraRepo).save(any(Infrastructure.class));
	}

	@Test
	void createInfrastructure_negativeCapacity() {

		InfrastructureCreateRequest request = new InfrastructureCreateRequest();
		request.setProgramId(1L);
		request.setCapacity(-10);

		assertThrows(IllegalArgumentException.class, () -> infrastructureService.createInfrastructure(request));

		verify(infraRepo, never()).save(any());
	}

	// ---------------- UPDATE ----------------

	@Test
	void updateInfrastructure_success() {

		InfrastructureUpdateRequest request = new InfrastructureUpdateRequest();
		request.setType(InfrastructureType.LAB);
		request.setLocation("Bangalore");
		request.setCapacity(100);
		request.setStatus(InfrastructureStatus.UNDER_MAINTENANCE);

		Infrastructure existingInfra = new Infrastructure();
		existingInfra.setInfraId(5L);
		existingInfra.setStatus(InfrastructureStatus.OPERATIONAL);

		when(infraRepo.findById(5L)).thenReturn(Optional.of(existingInfra));
		when(infraRepo.save(existingInfra)).thenReturn(existingInfra);

		InfrastructureResponse response = infrastructureService.updateInfrastructure(5L, request);

		assertEquals("Bangalore", response.getLocation());
		assertEquals(InfrastructureType.LAB, response.getType());
		assertEquals(InfrastructureStatus.UNDER_MAINTENANCE, response.getStatus());

		verify(infraRepo).save(existingInfra);
	}

	@Test
	void updateInfrastructure_decommissionedRejected() {

		InfrastructureUpdateRequest request = new InfrastructureUpdateRequest();
		request.setCapacity(50);

		Infrastructure existingInfra = new Infrastructure();
		existingInfra.setInfraId(6L);
		existingInfra.setStatus(InfrastructureStatus.DECOMMISSIONED);

		when(infraRepo.findById(6L)).thenReturn(Optional.of(existingInfra));

		assertThrows(IllegalStateException.class, () -> infrastructureService.updateInfrastructure(6L, request));
	}

	@Test
	void updateInfrastructure_notFound() {

		when(infraRepo.findById(20L)).thenReturn(Optional.empty());

		assertThrows(InfrastructureNotFoundException.class,
				() -> infrastructureService.updateInfrastructure(20L, new InfrastructureUpdateRequest()));
	}

	// ---------------- DELETE ----------------

	@Test
	void deleteInfrastructure_success() {

		Infrastructure infra = new Infrastructure();
		infra.setInfraId(7L);
		infra.setStatus(InfrastructureStatus.UNDER_MAINTENANCE);

		when(infraRepo.findById(7L)).thenReturn(Optional.of(infra));

		infrastructureService.deleteInfrastructureById(7L);

		verify(infraRepo).delete(infra);
	}

	@Test
	void deleteInfrastructure_operationalRejected() {

		Infrastructure infra = new Infrastructure();
		infra.setInfraId(8L);
		infra.setStatus(InfrastructureStatus.OPERATIONAL);

		when(infraRepo.findById(8L)).thenReturn(Optional.of(infra));

		assertThrows(IllegalStateException.class, () -> infrastructureService.deleteInfrastructureById(8L));

		verify(infraRepo, never()).delete(any());
	}

	@Test
	void deleteInfrastructure_notFound() {

		when(infraRepo.findById(9L)).thenReturn(Optional.empty());

		assertThrows(InfrastructureNotFoundException.class, () -> infrastructureService.deleteInfrastructureById(9L));
	}

	// ---------------- GET BY ID ----------------

	@Test
	void getInfrastructureById_success() {

		Infrastructure infra = new Infrastructure();
		infra.setInfraId(3L);
		infra.setLocation("Hyderabad");

		when(infraRepo.findById(3L)).thenReturn(Optional.of(infra));

		InfrastructureResponse response = infrastructureService.getInfrastructureById(3L);

		assertEquals(3L, response.getInfraId());
		assertEquals("Hyderabad", response.getLocation());
	}

	@Test
	void getInfrastructureById_notFound() {

		when(infraRepo.findById(4L)).thenReturn(Optional.empty());

		assertThrows(InfrastructureNotFoundException.class, () -> infrastructureService.getInfrastructureById(4L));
	}

	// ---------------- GET ALL ----------------

	@Test
	void getAllInfrastructures_success() {

		Infrastructure infra1 = new Infrastructure();
		infra1.setInfraId(1L);

		Infrastructure infra2 = new Infrastructure();
		infra2.setInfraId(2L);

		when(infraRepo.findAll()).thenReturn(List.of(infra1, infra2));

		List<InfrastructureResponse> responses = infrastructureService.getAllInfrastructures();

		assertEquals(2, responses.size());
	}

	// ---------------- GET BY PROGRAM ID ----------------

	@Test
	void getInfrastructuresByProgramId_success() {

		Infrastructure infra = new Infrastructure();
		infra.setInfraId(8L);
		infra.setProgramId(1L);

		when(infraRepo.findByProgramId(1L)).thenReturn(List.of(infra));

		List<InfrastructureResponse> responses = infrastructureService.getInfrastructuresByProgramId(1L);

		assertEquals(1, responses.size());
		assertEquals(8L, responses.get(0).getInfraId());
	}

	// ---------------- SEARCH ----------------

	@Test
	void getInfrastructuresByTypeLocationAndStatus_success() {

		Infrastructure infra = new Infrastructure();
		infra.setInfraId(11L);

		when(infraRepo.findByTypeAndLocationAndStatus(InfrastructureType.HOSPITAL, "Chennai",
				InfrastructureStatus.OPERATIONAL)).thenReturn(List.of(infra));

		List<InfrastructureResponse> responses = infrastructureService.getInfrastructuresByTypeLocationAndStatus(
				InfrastructureType.HOSPITAL, "Chennai", InfrastructureStatus.OPERATIONAL);

		assertEquals(1, responses.size());
		assertEquals(11L, responses.get(0).getInfraId());
	}
}