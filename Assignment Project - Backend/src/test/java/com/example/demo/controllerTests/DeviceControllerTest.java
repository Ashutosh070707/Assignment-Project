package com.example.demo.controllerTests;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.DTO.UpdateDevice;
import com.example.demo.controller.DeviceController;
import com.example.demo.entity.Device;
import com.example.demo.enums.BuildingName;
import com.example.demo.enums.DeviceType;
import com.example.demo.service.DeviceService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
public class DeviceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeviceService deviceService;

    private Device dummyDevice;

    @BeforeEach
    void setUp() {
        dummyDevice = new Device();
        dummyDevice.setId("123");
        dummyDevice.setDeviceName("Core-Router-01");
        dummyDevice.setDeviceType(DeviceType.CORE_ROUTER);
        dummyDevice.setPartNumber("CR-999");
        dummyDevice.setBuildingName(BuildingName.Hanover_US);
        dummyDevice.setNumberOfShelfPositions(4);
    }

    @Test
    void getAllDevices_ShouldReturn200AndList() throws Exception {
        when(deviceService.getAllDevices()).thenReturn(List.of(dummyDevice));

        mockMvc.perform(get("/api/devices/allDevices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].deviceName").value("Core-Router-01"));
    }

    @Test
    void createDevice_ShouldReturn201_WhenValid() throws Exception {
        when(deviceService.createDevice(any(Device.class))).thenReturn(dummyDevice);

        mockMvc.perform(post("/api/devices/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyDevice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deviceName").value("Core-Router-01"));
    }

    @Test
    void deleteDevice_ShouldReturn200() throws Exception {
        doNothing().when(deviceService).deleteDevice("Core-Router-01");

        mockMvc.perform(delete("/api/devices/Core-Router-01"))
                .andExpect(status().isOk())
                .andExpect(content().string("Device deleted successfully"));

        verify(deviceService, times(1)).deleteDevice("Core-Router-01");
    }

    @Test
    void checkValidity_ShouldReturn200() throws Exception {
        doNothing().when(deviceService).checkValidity("New-Router");

        mockMvc.perform(get("/api/devices/check/New-Router"))
                .andExpect(status().isOk())
                .andExpect(content().string("Valid newDeviceName"));
    }

    @Test
    void getDeviceDetails_ShouldReturn200AndSummary() throws Exception {
        DeviceSummary dummySummary = new DeviceSummary();
        dummySummary.setDevice(dummyDevice);

        when(deviceService.getDeviceDetails("Core-Router-01")).thenReturn(dummySummary);

        mockMvc.perform(get("/api/devices/deviceDetails/Core-Router-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.device.deviceName").value("Core-Router-01"));
    }

    @Test
    void updateDevice_ShouldReturn200_WhenValid() throws Exception {
        UpdateDevice updateDto = new UpdateDevice("123", "Core-Router-01", "Core-Router-02", "CR-888", BuildingName.Hanover_US, DeviceType.CORE_ROUTER);

        Device updatedDevice = new Device();
        updatedDevice.setDeviceName("Core-Router-02");

        when(deviceService.updateDevice(any(UpdateDevice.class))).thenReturn(updatedDevice);

        mockMvc.perform(put("/api/devices/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceName").value("Core-Router-02"));
    }
}