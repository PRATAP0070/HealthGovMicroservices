package com.healthgov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.healthgov.dto.ResourceCreateRequest;
import com.healthgov.dto.ResourceResponse;
import com.healthgov.dto.ResourceUpdateRequest;
import com.healthgov.enums.ProgramStatus;
import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.external.ProgramFeignClient;
import com.healthgov.external.dto.ProgramStatusResponse;
import com.healthgov.model.Resource;
import com.healthgov.repository.ResourceRepository;
import com.healthgov.service.ResourceServiceImpl;

class ResourceServiceImplTest {
	// Mock repository to isolate service logic from database operations
	@Mock
	private ResourceRepository resourceRepo;

	@Mock
	private ProgramFeignClient programFeignClient;

	@InjectMocks
	private ResourceServiceImpl resourceService;

	// Initializes Mockito mocks before each test
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// ---------------- CREATE RESOURCE ----------------

	// Verifies successful resource creation when program exists and is ACTIVE
	@Test
	void createResource_success() {

		ResourceCreateRequest request = new ResourceCreateRequest();
		request.setProgramId(1L);
		request.setType(ResourceType.FUNDS);
		request.setQuantity(500);
		request.setStatus(ResourceStatus.ACTIVE);

		ProgramStatusResponse program = new ProgramStatusResponse(1L, ProgramStatus.ACTIVE);

		Resource savedResource = new Resource();
		savedResource.setResourceId(10L);
		savedResource.setProgramId(1L);
		savedResource.setType(ResourceType.FUNDS);
		savedResource.setQuantity(500);
		savedResource.setStatus(ResourceStatus.ACTIVE);

		when(programFeignClient.getProgramStatus(1L)).thenReturn(program);
		when(resourceRepo.save(any(Resource.class))).thenReturn(savedResource);

		ResourceResponse response = resourceService.createResource(request);

		assertNotNull(response);
		assertEquals(10L, response.getResourceId());
		assertEquals(1L, response.getProgramId());
		assertEquals(ResourceType.FUNDS, response.getType());
		assertEquals(500, response.getQuantity());
		assertEquals(ResourceStatus.ACTIVE, response.getStatus());

		verify(programFeignClient).getProgramStatus(1L);
		verify(resourceRepo).save(any(Resource.class));
	}

	// Ensures resource allocation is blocked when program is NOT ACTIVE
	@Test
	void createResource_programNotActive() {

		ResourceCreateRequest request = new ResourceCreateRequest();
		request.setProgramId(1L);
		request.setQuantity(100);

		ProgramStatusResponse program = new ProgramStatusResponse(1L, ProgramStatus.INACTIVE);

		when(programFeignClient.getProgramStatus(1L)).thenReturn(program);

		assertThrows(IllegalStateException.class, () -> resourceService.createResource(request));
	}

	// ---------------- UPDATE RESOURCE ----------------

	// Validates resource update when resource is mutable and input is valid
	@Test
	void updateResource_success() {

		ResourceUpdateRequest request = new ResourceUpdateRequest();
		request.setType(ResourceType.EQUIPMENT);
		request.setQuantity(50);
		request.setStatus(ResourceStatus.INACTIVE);

		Resource existingResource = new Resource();
		existingResource.setResourceId(5L);
		existingResource.setStatus(ResourceStatus.ACTIVE);

		when(resourceRepo.findById(5L)).thenReturn(Optional.of(existingResource));
		when(resourceRepo.save(existingResource)).thenReturn(existingResource);

		ResourceResponse response = resourceService.updateResource(5L, request);

		assertEquals(ResourceType.EQUIPMENT, response.getType());
		assertEquals(50, response.getQuantity());
		assertEquals(ResourceStatus.INACTIVE, response.getStatus());

		verify(resourceRepo).save(existingResource);
	}

	// Ensures COMPLETED resources are immutable and cannot be modified
	@Test
	void updateResource_completedResource() {

		ResourceUpdateRequest request = new ResourceUpdateRequest();
		request.setQuantity(10);

		Resource completedResource = new Resource();
		completedResource.setResourceId(6L);
		completedResource.setStatus(ResourceStatus.COMPLETED);

		when(resourceRepo.findById(6L)).thenReturn(Optional.of(completedResource));

		assertThrows(IllegalStateException.class, () -> resourceService.updateResource(6L, request));
	}

	// ---------------- DELETE RESOURCE ----------------

	// Allows deletion only for resources that are not ACTIVE or not COMPLETED
	@Test
	void deleteResource_success() {

		Resource resource = new Resource();
		resource.setResourceId(7L);
		resource.setStatus(ResourceStatus.PENDING);

		when(resourceRepo.findById(7L)).thenReturn(Optional.of(resource));

		resourceService.deleteResourceById(7L);

		verify(resourceRepo).delete(resource);
	}

	// Prevents deletion of ACTIVE resources to avoid data loss
	@Test
	void deleteResource_activeResource() {

		Resource resource = new Resource();
		resource.setResourceId(8L);
		resource.setStatus(ResourceStatus.ACTIVE);

		when(resourceRepo.findById(8L)).thenReturn(Optional.of(resource));

		assertThrows(IllegalStateException.class, () -> resourceService.deleteResourceById(8L));
	}

	// ---------------- GET BY ID ----------------

	// Retrieves resource successfully when it exists
	@Test
	void getResourceById_success() {

		Resource resource = new Resource();
		resource.setResourceId(3L);
		resource.setQuantity(100);

		when(resourceRepo.findById(3L)).thenReturn(Optional.of(resource));

		ResourceResponse response = resourceService.getResourceById(3L);

		assertEquals(3L, response.getResourceId());
		assertEquals(100, response.getQuantity());
	}

	// Throws exception when requested resource does not exist
	@Test
	void getResourceById_notFound() {

		when(resourceRepo.findById(4L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById(4L));
	}

	// ---------------- GET ALL ----------------

	// Returns all available resources without applying business
	@Test
	void getAllResources_success() {

		Resource r1 = new Resource();
		r1.setResourceId(1L);

		Resource r2 = new Resource();
		r2.setResourceId(2L);

		when(resourceRepo.findAll()).thenReturn(List.of(r1, r2));

		List<ResourceResponse> responses = resourceService.getAllResources();

		assertEquals(2, responses.size());
	}

	// ---------------- GET BY PROGRAM ID ----------------

	// Fetches resources associated with a specific program
	@Test
	void getResourcesByProgramId_success() {

		Resource resource = new Resource();
		resource.setResourceId(8L);
		resource.setProgramId(1L);

		when(resourceRepo.findByProgramId(1L)).thenReturn(List.of(resource));

		List<ResourceResponse> responses = resourceService.getResourcesByProgramId(1L);

		assertEquals(1, responses.size());
		assertEquals(8L, responses.get(0).getResourceId());
	}

	// ---------------- SEARCH ----------------

	// Searches resources using type and status filters
	@Test
	void getResourcesByTypeAndStatus_success() {

		Resource resource = new Resource();
		resource.setResourceId(11L);

		when(resourceRepo.findByTypeAndStatus(ResourceType.LAB, ResourceStatus.ACTIVE)).thenReturn(List.of(resource));

		List<ResourceResponse> responses = resourceService.getResourcesByTypeAndStatus(ResourceType.LAB,
				ResourceStatus.ACTIVE);

		assertEquals(1, responses.size());
		assertEquals(11L, responses.get(0).getResourceId());
	}
}