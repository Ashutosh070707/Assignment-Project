package com.example.demo.repositoryTests;

import com.example.demo.repository.ShelfRepository;
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
public class ShelfRepositoryTest {

    @Mock
    private Driver driver;

    @InjectMocks
    private ShelfRepository shelfRepository;

    @Test
    @SuppressWarnings("unchecked")
    void isShelfPresent_ShouldReturnTrue_WhenRecordExists() {
        // Deep mock the Neo4j driver
        ExecutableQuery executableQueryMock = mock(ExecutableQuery.class);
        EagerResult eagerResultMock = mock(EagerResult.class);
        Record recordMock = mock(Record.class);

        when(driver.executableQuery(anyString())).thenReturn(executableQueryMock);
        when(executableQueryMock.withParameters(anyMap())).thenReturn(executableQueryMock);
        when(executableQueryMock.execute()).thenReturn(eagerResultMock);

        // Tell Neo4j to pretend it found the shelf
        when(eagerResultMock.records()).thenReturn(List.of(recordMock));

        boolean isPresent = shelfRepository.isShelfPresent("Firewall-SF-01");

        assertTrue(isPresent);
    }
}