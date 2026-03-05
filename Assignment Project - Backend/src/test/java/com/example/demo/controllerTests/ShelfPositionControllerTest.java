package com.example.demo.controllerTests;

import com.example.demo.controller.ShelfPositionController;
import com.example.demo.entity.ShelfPosition;
import com.example.demo.service.ShelfPositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ShelfPositionController.class)
public class ShelfPositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    @MockitoBean
    private ShelfPositionService shelfPositionService;

    private ShelfPosition dummyPosition;

    @BeforeEach
    void setUp() {
        dummyPosition = new ShelfPosition();
        dummyPosition.setId("pos-123");
        dummyPosition.setDeviceId("device-123");
    }

    @Test
    void createShelfPositionAndAttach_ShouldReturn201() throws Exception {
        when(shelfPositionService.createShelfPositionAndAttach(any(ShelfPosition.class))).thenReturn(dummyPosition);

        mockMvc.perform(post("/api/shelfPositions/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyPosition)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("pos-123"))
                .andExpect(jsonPath("$.deviceId").value("device-123"));
    }

    @Test
    void deleteShelfPosition_ShouldReturn200() throws Exception {
        doNothing().when(shelfPositionService).deleteShelfPosition("Core-Router-01", "pos-123");

        mockMvc.perform(delete("/api/shelfPositions/Core-Router-01/pos-123"))
                .andExpect(status().isOk())
                .andExpect(content().string("ShelfPosition deleted successfully"));
    }
}
