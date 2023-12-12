import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


class Cust_Calendar_DeletionTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    PrintWriter writer;

    @Mock
    Connection mockConnection;

    @Mock
    PreparedStatement mockStatement;

    @Mock
    ResultSet mockResultSet;

    // This will hold our mocked static block
    MockedStatic<DBConnectionDelta> mockedDBConnection;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
     // Create mock objects for connection, preparedStatement, etc.
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Define behavior for mock objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Example behavior

        // Mock the static method and set the static connection field
        mockedDBConnection = Mockito.mockStatic(DBConnectionDelta.class);
        mockedDBConnection.when(DBConnectionDelta::getDBConnection)
                          .then(invocation -> DBConnectionDelta.connection = mockConnection);

        // Configure the mock objects as necessary
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Example behavior

        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testDoPost() throws Exception {
        // Arrange
        when(request.getReader()).thenReturn(
            new BufferedReader(new StringReader(
                "{\"id\":\"1\"}"
            ))
        );
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        
        
        when(mockConnection.prepareStatement(
    		"DELETE FROM Reservations WHERE id = ?"
        )).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        // Act
        new Cust_Calendar_Deletion().doPost(request, response);

        // Assert
        verify(mockStatement).executeUpdate();
        assertTrue(stringWriter.toString().contains("success"));
    }

    @Test
    void testDoPostJSONException() throws Exception {
        // Arrange
        String invalidJson = "{\"first_name\":\"John\";\"last_name\":\"Doe\"}"; // Note the semicolon, which is invalid JSON
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        new Cust_Calendar_Deletion().doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        // Optionally, check for error message content if it's sent in the response
    }
    
    @Test
    void testDoPostSQLException() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(request.getReader()).thenReturn(
            new BufferedReader(new StringReader(
                "{\"first_name\":\"John\",\"last_name\":\"Doe\",\"phone\":\"1234567890\",\"email\":\"johndoe@example.com\",\"date\":\"2023-12-31\",\"time\":\"18:00\"}"
            ))
        ); // needed so request.getReader().lines() doesn't return null.
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        new Cust_Calendar_Deletion().doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        // Optionally, check for error message content if it's sent in the response
    }
    

    // Remember to close the mocked static block
    @AfterEach
    public void tearDown() {
        mockedDBConnection.close();
    }
}
