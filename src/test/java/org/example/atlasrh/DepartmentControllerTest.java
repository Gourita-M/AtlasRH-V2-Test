package org.example.atlasrh;

import org.example.atlasrh.controller.DepartmentController;
import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @Test
    void returnsAllDepartmentsAsJson() throws Exception {
        when(departmentService.viewAll()).thenReturn(List.of(
                new DepartmentResponseDTO(1L, "People", "Human Resources"),
                new DepartmentResponseDTO(2L, "Engineering", "Product development")));

        mockMvc.perform(get("/api/department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("People"))
                .andExpect(jsonPath("$[0].description").value("Human Resources"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Engineering"));

        verify(departmentService).viewAll();
    }

    @Test
    void returnsEmptyArrayWhenThereAreNoDepartments() throws Exception {
        when(departmentService.viewAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/department"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void createsDepartmentAndReturnsCreatedJson() throws Exception {
        when(departmentService.create(any(Department.class)))
                .thenReturn(new DepartmentResponseDTO(7L, "People", "Human Resources"));

        mockMvc.perform(post("/api/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"People","description":"Human Resources"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("People"))
                .andExpect(jsonPath("$.description").value("Human Resources"));

        var departmentCaptor = org.mockito.ArgumentCaptor.forClass(Department.class);
        verify(departmentService).create(departmentCaptor.capture());
        assertEquals("People", departmentCaptor.getValue().getName());
        assertEquals("Human Resources", departmentCaptor.getValue().getDescription());
    }

    @Test
    void rejectsDepartmentWithBlankName() throws Exception {
        mockMvc.perform(post("/api/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \",\"description\":\"HR\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(departmentService);
    }

    @Test
    void rejectsDepartmentWithBlankDescription() throws Exception {
        mockMvc.perform(post("/api/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"People\",\"description\":\" \"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(departmentService);
    }

    @Test
    void rejectsDepartmentWithMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(departmentService);
    }

    @Test
    void rejectsMalformedDepartmentJson() throws Exception {
        mockMvc.perform(post("/api/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(departmentService);
    }

    @Test
    void returnsBadRequestWhenDepartmentCreationFails() throws Exception {
        when(departmentService.create(any(Department.class)))
                .thenThrow(new IllegalArgumentException("Department already exists"));

        mockMvc.perform(post("/api/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"People\",\"description\":\"HR\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Department already exists"));
    }

    @Test
    void deletesDepartmentAndReturnsNoContent() throws Exception {
        when(departmentService.deleteDepartment(7L)).thenReturn(true);

        mockMvc.perform(delete("/api/department/7"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(departmentService).deleteDepartment(7L);
    }

    @Test
    void returnsConflictWhenDepartmentContainsEmployees() throws Exception {
        doThrow(new IllegalStateException("Cannot delete a department that has employees"))
                .when(departmentService).deleteDepartment(7L);

        mockMvc.perform(delete("/api/department/7"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Cannot delete a department that has employees"));
    }

    @Test
    void returnsNotFoundWhenDepartmentToDeleteDoesNotExist() throws Exception {
        doThrow(new RuntimeException("There is no Department with this ID"))
                .when(departmentService).deleteDepartment(99L);

        mockMvc.perform(delete("/api/department/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no Department with this ID"));
    }
}
