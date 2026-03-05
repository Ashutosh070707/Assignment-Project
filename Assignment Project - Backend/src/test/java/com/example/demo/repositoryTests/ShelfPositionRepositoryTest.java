package com.example.demo.repositoryTests;

import com.example.demo.entity.ShelfPosition;
import com.example.demo.repository.ShelfPositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.ExecutableQuery;
import org.neo4j.driver.Record;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.summary.SummaryCounters;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class ShelfPositionRepositoryTest {

    @Mock
    private Driver driver;

    @InjectMocks
    private ShelfPositionRepository shelfPositionRepository;

    @Test
    @SuppressWarnings("unchecked")
    void isShelfPositionPresent_ShouldReturnTrue_WhenRecordExists() {
        // Deep mock the Neo4j driver
        ExecutableQuery executableQueryMock = mock(ExecutableQuery.class);
        EagerResult eagerResultMock = mock(EagerResult.class);
        Record recordMock = mock(Record.class);

        when(driver.executableQuery(anyString())).thenReturn(executableQueryMock);
        when(executableQueryMock.withParameters(anyMap())).thenReturn(executableQueryMock);
        when(executableQueryMock.execute()).thenReturn(eagerResultMock);

        // Tell Neo4j to pretend it found the shelf position
        when(eagerResultMock.records()).thenReturn(List.of(recordMock));

        boolean isPresent = shelfPositionRepository.isShelfPositionPresent("pos-123");

        assertTrue(isPresent);
    }


    @Test
    @SuppressWarnings("unchecked")
    void createShelfPositionAndAttach_ShouldReturnOptional_WhenCreated() {
        ExecutableQuery executableQueryMock = mock(ExecutableQuery.class);
        EagerResult eagerResultMock = mock(EagerResult.class);
        Record recordMock = mock(Record.class);
        org.neo4j.driver.types.Node nodeMock = mock(org.neo4j.driver.types.Node.class);
        org.neo4j.driver.Value valueMock = mock(org.neo4j.driver.Value.class);

        when(driver.executableQuery(anyString())).thenReturn(executableQueryMock);
        when(executableQueryMock.withParameters(anyMap())).thenReturn(executableQueryMock);
        when(executableQueryMock.execute()).thenReturn(eagerResultMock);

        // Mock the record extraction
        when(eagerResultMock.records()).thenReturn(List.of(recordMock));
        when(recordMock.get("sp")).thenReturn(valueMock);
        when(valueMock.asNode()).thenReturn(nodeMock);

        // Mock the node properties
        org.neo4j.driver.Value idValueMock = mock(org.neo4j.driver.Value.class);
        org.neo4j.driver.Value deviceIdValueMock = mock(org.neo4j.driver.Value.class);
        when(nodeMock.get("id")).thenReturn(idValueMock);
        when(nodeMock.get("deviceId")).thenReturn(deviceIdValueMock);
        when(idValueMock.asString()).thenReturn("pos-123");
        when(deviceIdValueMock.asString()).thenReturn("device-123");

        ShelfPosition inputPosition = new ShelfPosition();
        inputPosition.setId("pos-123");
        inputPosition.setDeviceId("device-123");

        Optional<ShelfPosition> result = shelfPositionRepository.createShelfPositionAndAttach(inputPosition);

        assertTrue(result.isPresent());
        assertEquals("pos-123", result.get().getId());
    }

    @Test
    void deleteShelfPosition_ShouldSucceed_WhenRelationshipsDeleted() {
        ExecutableQuery executableQueryMock = mock(ExecutableQuery.class);
        EagerResult eagerResultMock = mock(EagerResult.class);
        ResultSummary summaryMock = mock(ResultSummary.class);
        SummaryCounters countersMock = mock(SummaryCounters.class);

        when(driver.executableQuery(anyString())).thenReturn(executableQueryMock);
        when(executableQueryMock.withParameters(anyMap())).thenReturn(executableQueryMock);
        when(executableQueryMock.execute()).thenReturn(eagerResultMock);

        // Mock the summary and counters
        when(eagerResultMock.summary()).thenReturn(summaryMock);
        when(summaryMock.counters()).thenReturn(countersMock);

        // Return 1 to simulate a successful deletion
        when(countersMock.relationshipsDeleted()).thenReturn(1);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            shelfPositionRepository.deleteShelfPosition("Core-Router-01", "pos-123");
        });
    }

    @Test
    void deleteShelfPosition_ShouldThrowIllegalArgumentException_WhenNoRelationshipsDeleted() {
        ExecutableQuery executableQueryMock = mock(ExecutableQuery.class);
        EagerResult eagerResultMock = mock(EagerResult.class);
        ResultSummary summaryMock = mock(ResultSummary.class);
        SummaryCounters countersMock = mock(SummaryCounters.class);

        when(driver.executableQuery(anyString())).thenReturn(executableQueryMock);
        when(executableQueryMock.withParameters(anyMap())).thenReturn(executableQueryMock);
        when(executableQueryMock.execute()).thenReturn(eagerResultMock);

        when(eagerResultMock.summary()).thenReturn(summaryMock);
        when(summaryMock.counters()).thenReturn(countersMock);

        // Return 0 to trigger the IllegalArgumentException
        when(countersMock.relationshipsDeleted()).thenReturn(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            shelfPositionRepository.deleteShelfPosition("Core-Router-01", "pos-123");
        });

        assertEquals("Current shelfPosition with id pos-123 is not attached with device deviceName Core-Router-01", exception.getMessage());
    }
}
