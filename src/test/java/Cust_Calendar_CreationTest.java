import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
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
import java.sql.Statement;


class Cust_Calendar_CreationTest {

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
    
    @Mock
    JSONObject mockJSONObject;

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
    void testDoGet() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        new Cust_Calendar_Creation().doGet(request, response);

        // Assert
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("success"));
    }

    @Test
    void testDoPost() throws Exception {
        // Arrange
        when(request.getReader()).thenReturn(
            new BufferedReader(new StringReader(
                "{\"first_name\":\"John\",\"last_name\":\"Doe\",\"phone\":\"123-456-7890\",\"email\":\"johndoe@example.com\",\"date\":\"2023-12-31\",\"time\":\"18:00\"}"
            ))
        );
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        
        when(mockConnection.prepareStatement(
        		"INSERT INTO Reservations (first_name, last_name, phone, email, date, time) VALUES (?, ?, ?, ?, ?, ?)", 
        		Statement.RETURN_GENERATED_KEYS
        )).thenReturn(mockStatement);

        // Act
        new Cust_Calendar_Creation().doPost(request, response);
        
        // Assert
        assertTrue(stringWriter.toString().contains("success"));
    }
    
    @Test
    void testDoGetSQLException() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        new Cust_Calendar_Creation().doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        // Optionally, check for error message content if it's sent in the response
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
        new Cust_Calendar_Creation().doPost(request, response);

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
