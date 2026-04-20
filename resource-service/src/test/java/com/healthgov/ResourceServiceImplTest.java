package com.healthgov;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    // Mock repository to isolate service logic from DB
    @Mock
    private ResourceRepository resourceRepo;

    // Mock Feign client for Program service
    @Mock
    private ProgramFeignClient programFeignClient;

    // Inject mocks into service
    @InjectMocks
    private ResourceServiceImpl resourceService;

    // Initialize mocks before each test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------------------------------------------------------
    // CREATE RESOURCE
    // ------------------------------------------------------------------

    // FUNDS → ALLOCATED when budget is sufficient
    @Test
    void createResource_fundsAllocated() {

        ResourceCreateRequest request = new ResourceCreateRequest();
        request.setProgramId(1L);
        request.setType(ResourceType.FUNDS);
        request.setQuantity(400);

        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 1000.0, ProgramStatus.ACTIVE));

        when(resourceRepo.findByProgramIdAndTypeAndStatus(
                1L, ResourceType.FUNDS, ResourceStatus.ALLOCATED))
                .thenReturn(List.of());

        Resource saved = new Resource();
        saved.setResourceId(10L);
        saved.setStatus(ResourceStatus.ALLOCATED);

        when(resourceRepo.save(any(Resource.class))).thenReturn(saved);

        ResourceResponse response = resourceService.createResource(request);

        assertEquals(ResourceStatus.ALLOCATED, response.getStatus());
    }

    // FUNDS → PENDING when budget is insufficient
    @Test
    void createResource_fundsPendingDueToBudget() {

        ResourceCreateRequest request = new ResourceCreateRequest();
        request.setProgramId(1L);
        request.setType(ResourceType.FUNDS);
        request.setQuantity(900);

        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 1000.0, ProgramStatus.ACTIVE));

        Resource existing = new Resource();
        existing.setQuantity(200);

        when(resourceRepo.findByProgramIdAndTypeAndStatus(
                1L, ResourceType.FUNDS, ResourceStatus.ALLOCATED))
                .thenReturn(List.of(existing));

        Resource saved = new Resource();
        saved.setStatus(ResourceStatus.PENDING);

        when(resourceRepo.save(any(Resource.class))).thenReturn(saved);

        ResourceResponse response = resourceService.createResource(request);

        assertEquals(ResourceStatus.PENDING, response.getStatus());
    }

    // NON-FUNDS cannot be created as PENDING
    @Test
    void createResource_nonFundsPendingRejected() {

        ResourceCreateRequest request = new ResourceCreateRequest();
        request.setProgramId(1L);
        request.setType(ResourceType.LAB);
        request.setQuantity(1);
        request.setStatus(ResourceStatus.PENDING);

        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 0.0, ProgramStatus.ACTIVE));

        assertThrows(IllegalStateException.class,
                () -> resourceService.createResource(request));
    }

    // Negative quantity on create
    @Test
    void createResource_negativeQuantityRejected() {

        ResourceCreateRequest request = new ResourceCreateRequest();
        request.setProgramId(1L);
        request.setQuantity(-10);

        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 0.0, ProgramStatus.ACTIVE));

        assertThrows(IllegalArgumentException.class,
                () -> resourceService.createResource(request));
    }

    // ------------------------------------------------------------------
    // UPDATE RESOURCE
    // ------------------------------------------------------------------

    // NON-FUNDS update success
    @Test
    void updateResource_nonFundsSuccess() {

        ResourceUpdateRequest request = new ResourceUpdateRequest();
        request.setType(ResourceType.EQUIPMENT);
        request.setQuantity(50);
        request.setStatus(ResourceStatus.INACTIVE);

        Resource entity = new Resource();
        entity.setResourceId(5L);
        entity.setProgramId(1L); // required for program validation
        entity.setStatus(ResourceStatus.ACTIVE);

        when(resourceRepo.findById(5L)).thenReturn(Optional.of(entity));
        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 0.0, ProgramStatus.ACTIVE));
        when(resourceRepo.save(any(Resource.class))).thenReturn(entity);

        ResourceResponse response = resourceService.updateResource(5L, request);

        assertEquals(ResourceType.EQUIPMENT, response.getType());
        assertEquals(ResourceStatus.INACTIVE, response.getStatus());
    }

    // FUNDS update within budget
    @Test
    void updateResource_fundsWithinBudget() {

        ResourceUpdateRequest request = new ResourceUpdateRequest();
        request.setType(ResourceType.FUNDS);
        request.setQuantity(300);
        request.setStatus(ResourceStatus.ALLOCATED);

        Resource entity = new Resource();
        entity.setProgramId(1L);
        entity.setType(ResourceType.FUNDS);
        entity.setQuantity(200);
        entity.setStatus(ResourceStatus.ALLOCATED);

        when(resourceRepo.findById(1L)).thenReturn(Optional.of(entity));
        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 1000.0, ProgramStatus.ACTIVE));
        when(resourceRepo.findByProgramIdAndTypeAndStatus(
                1L, ResourceType.FUNDS, ResourceStatus.ALLOCATED))
                .thenReturn(List.of(entity));
        when(resourceRepo.save(any(Resource.class))).thenReturn(entity);

        ResourceResponse response = resourceService.updateResource(1L, request);

        assertEquals(300, response.getQuantity());
    }

    // FUNDS update exceeding budget
    @Test
    void updateResource_fundsBudgetExceeded() {

        ResourceUpdateRequest request = new ResourceUpdateRequest();
        request.setType(ResourceType.FUNDS);
        request.setQuantity(1200); // ✅ increased
        request.setStatus(ResourceStatus.ALLOCATED);

        Resource entity = new Resource();
        entity.setProgramId(1L);
        entity.setType(ResourceType.FUNDS);
        entity.setQuantity(200);
        entity.setStatus(ResourceStatus.ALLOCATED);

        when(resourceRepo.findById(1L)).thenReturn(Optional.of(entity));
        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 1000.0, ProgramStatus.ACTIVE));
        when(resourceRepo.findByProgramIdAndTypeAndStatus(
                1L, ResourceType.FUNDS, ResourceStatus.ALLOCATED))
                .thenReturn(List.of(entity));

        assertThrows(IllegalStateException.class,
                () -> resourceService.updateResource(1L, request));
    }

    // COMPLETED resource cannot be updated
    @Test
    void updateResource_completedRejected() {

        ResourceUpdateRequest request = new ResourceUpdateRequest();
        request.setQuantity(10);

        Resource entity = new Resource();
        entity.setProgramId(1L);
        entity.setStatus(ResourceStatus.COMPLETED);

        when(resourceRepo.findById(6L)).thenReturn(Optional.of(entity));
        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 0.0, ProgramStatus.ACTIVE));

        assertThrows(IllegalStateException.class,
                () -> resourceService.updateResource(6L, request));
    }

    // COMPLETED is allowed only from ACTIVE
    @Test
    void updateResource_completedFromNonActiveRejected() {

        ResourceUpdateRequest request = new ResourceUpdateRequest();
        request.setType(ResourceType.LAB);
        request.setQuantity(10);
        request.setStatus(ResourceStatus.COMPLETED);

        Resource entity = new Resource();
        entity.setProgramId(1L);
        entity.setStatus(ResourceStatus.PENDING);

        when(resourceRepo.findById(7L)).thenReturn(Optional.of(entity));
        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 0.0, ProgramStatus.ACTIVE));

        assertThrows(IllegalStateException.class,
                () -> resourceService.updateResource(7L, request));
    }

    // NON-FUNDS cannot move to PENDING
    @Test
    void updateResource_nonFundsPendingRejected() {

        ResourceUpdateRequest request = new ResourceUpdateRequest();
        request.setType(ResourceType.LAB);
        request.setQuantity(5);
        request.setStatus(ResourceStatus.PENDING);

        Resource entity = new Resource();
        entity.setProgramId(1L);
        entity.setStatus(ResourceStatus.ACTIVE);

        when(resourceRepo.findById(8L)).thenReturn(Optional.of(entity));
        when(programFeignClient.getProgramStatus(1L))
                .thenReturn(new ProgramStatusResponse(1L, 0.0, ProgramStatus.ACTIVE));

        assertThrows(IllegalStateException.class,
                () -> resourceService.updateResource(8L, request));
    }

    // ------------------------------------------------------------------
    // DELETE RESOURCE
    // ------------------------------------------------------------------

    // DELETE allowed when resource is not ACTIVE or COMPLETED
    @Test
    void deleteResource_success() {

        Resource resource = new Resource();
        resource.setStatus(ResourceStatus.INACTIVE);

        when(resourceRepo.findById(9L)).thenReturn(Optional.of(resource));

        resourceService.deleteResourceById(9L);

        verify(resourceRepo).delete(resource);
    }

    // DELETE not allowed for COMPLETED resource
    @Test
    void deleteResource_completedRejected() {

        Resource resource = new Resource();
        resource.setStatus(ResourceStatus.COMPLETED);

        when(resourceRepo.findById(10L)).thenReturn(Optional.of(resource));

        assertThrows(IllegalStateException.class,
                () -> resourceService.deleteResourceById(10L));
    }

    // ------------------------------------------------------------------
    // GET METHODS
    // ------------------------------------------------------------------

    // Get resource by ID – success
    @Test
    void getResourceById_success() {

        Resource resource = new Resource();
        resource.setResourceId(3L);
        resource.setType(ResourceType.LAB);
        resource.setQuantity(10);
        resource.setStatus(ResourceStatus.ACTIVE);

        when(resourceRepo.findById(3L)).thenReturn(Optional.of(resource));

        ResourceResponse response = resourceService.getResourceById(3L);

        assertEquals(3L, response.getResourceId());
        assertEquals(ResourceType.LAB, response.getType());
    }

    // Get resource by ID – not found
    @Test
    void getResourceById_notFound() {

        when(resourceRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> resourceService.getResourceById(99L));
    }

    // Get all resources
    @Test
    void getAllResources_success() {

        when(resourceRepo.findAll()).thenReturn(List.of(new Resource(), new Resource()));

        List<ResourceResponse> responses = resourceService.getAllResources();

        assertEquals(2, responses.size());
    }

    // Get resources by programId
    @Test
    void getResourcesByProgramId_success() {

        Resource resource = new Resource();
        resource.setResourceId(11L);
        resource.setProgramId(1L);

        when(resourceRepo.findByProgramId(1L)).thenReturn(List.of(resource));

        List<ResourceResponse> responses =
                resourceService.getResourcesByProgramId(1L);

        assertEquals(1, responses.size());
        assertEquals(11L, responses.get(0).getResourceId());
    }

    // Get resources by type and status
    @Test
    void getResourcesByTypeAndStatus_success() {

        Resource resource = new Resource();
        resource.setResourceId(12L);

        when(resourceRepo.findByTypeAndStatus(ResourceType.LAB, ResourceStatus.ACTIVE))
                .thenReturn(List.of(resource));

        List<ResourceResponse> responses =
                resourceService.getResourcesByTypeAndStatus(ResourceType.LAB, ResourceStatus.ACTIVE);

        assertEquals(1, responses.size());
    }
}