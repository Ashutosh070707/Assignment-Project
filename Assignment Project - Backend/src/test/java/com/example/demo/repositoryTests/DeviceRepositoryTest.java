package com.example.demo.repositoryTests;

import com.example.demo.repository.DeviceRepository;
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

@ExtendWith(MockitoExtension.class)
public class DeviceRepositoryTest {

    @Mock
    private Driver driver;

    @InjectMocks
    private DeviceRepository deviceRepository;

    @Test
    @SuppressWarnings("unchecked")
    void isDevicePresent_ShouldReturnTrue_WhenRecordExists() {
        // 1. Create the mocks for the fluent Neo4j API chain
        ExecutableQuery executableQueryMock = mock(ExecutableQuery.class);
        EagerResult eagerResultMock = mock(EagerResult.class);
        Record recordMock = mock(Record.class);

        // 2. Mock the chain: driver -> executableQuery -> withParameters -> execute -> records
        when(driver.executableQuery(anyString())).thenReturn(executableQueryMock);
        when(executableQueryMock.withParameters(anyMap())).thenReturn(executableQueryMock);
        when(executableQueryMock.execute()).thenReturn(eagerResultMock);

        // 3. Simulate Neo4j returning exactly 1 record
        when(eagerResultMock.records()).thenReturn(List.of(recordMock));

        // 4. Call your actual repository method
        boolean isPresent = deviceRepository.isDevicePresent("Core-Router-01");

        // 5. Assert it correctly returned true
        assertTrue(isPresent);
    }
}