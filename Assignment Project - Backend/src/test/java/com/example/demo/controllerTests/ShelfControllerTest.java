package com.example.demo.controllerTests;

import com.example.demo.DTO.CreateShelfAndAttach;
import com.example.demo.DTO.UpdateShelf;
import com.example.demo.controller.ShelfController;
import com.example.demo.entity.Shelf;
import com.example.demo.service.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ShelfController.class)
public class ShelfControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    @MockitoBean
    private ShelfService shelfService;

    private Shelf dummyShelf;
    private CreateShelfAndAttach createDto;
    private UpdateShelf updateDto;

    @BeforeEach
    void setUp() {
        dummyShelf = new Shelf();
        dummyShelf.setId("shelf-123");
        dummyShelf.setShelfName("Firewall-SF-01");
        dummyShelf.setPartNumber("SH-999");

        createDto = new CreateShelfAndAttach();
        createDto.setShelfPositionId("pos-123");
        createDto.setShelfName("Firewall-SF-01");
        createDto.setPartNumber("SH-999");

        updateDto = new UpdateShelf();
        updateDto.setId("shelf-123");
        updateDto.setPreviousShelfName("Old-Shelf");
        updateDto.setNewShelfName("Firewall-SF-01");
        updateDto.setNewPartNumber("SH-999");
    }

    @Test
    void createShelfAndAttach_ShouldReturn201_WhenValid() throws Exception {
        when(shelfService.createShelfAndAttach(any(CreateShelfAndAttach.class))).thenReturn(dummyShelf);

        mockMvc.perform(post("/api/shelves/createAndAttach")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shelfName").value("Firewall-SF-01"));
    }

    @Test
    void deleteShelf_ShouldReturn200() throws Exception {
        doNothing().when(shelfService).deleteShelf("Firewall-SF-01");

        mockMvc.perform(delete("/api/shelves/Firewall-SF-01"))
                .andExpect(status().isOk())
                .andExpect(content().string("Shelf deleted successfully"));
    }

    @Test
    void updateShelf_ShouldReturn200_WhenValid() throws Exception {
        when(shelfService.updateShelf(any(UpdateShelf.class))).thenReturn(dummyShelf);

        mockMvc.perform(put("/api/shelves/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shelfName").value("Firewall-SF-01"));
    }

    @Test
    void checkValidity_ShouldReturn200() throws Exception {
        doNothing().when(shelfService).checkValidity("New-Shelf");

        mockMvc.perform(get("/api/shelves/check/New-Shelf"))
                .andExpect(status().isOk())
                .andExpect(content().string("Valid newShelfName"));
    }
}