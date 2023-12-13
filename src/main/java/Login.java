import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Login() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        String errorMessage = "Default";
        PrintWriter out = response.getWriter();
        // Get the username and password from the form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // Get the button pressed
        String buttonPressed = request.getParameter("buttonClicked");
        // Create variables for the connection and the SQL Statement
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        // Attempt to connect to the DB
        try {
            DBConnectionDelta.getDBConnection();
            connection = DBConnectionDelta.connection;
	        // Create the select statement
	        String selectSQL = "SELECT * FROM userAccounts WHERE USERNAME LIKE ?";
	        preparedStatement = connection.prepareStatement(selectSQL);
	        preparedStatement.setString(1, username);
	        // Store the results of the query
	        ResultSet result = preparedStatement.executeQuery();
	        // Check which button was pressed
	        if (buttonPressed.equals("login")) {
	            // Check if an account was found
	            if (!result.next()) {
	            	errorMessage = "No account exists with the given username.";
	            	response.sendRedirect("Error?message=" + errorMessage);
	            	return;
	            }
	
	            // If an account was found, retrieve the password from the DB
	            String userPassword = result.getString("PASSWORD").trim();
	            // Check if the input password matches the password in the DB
	            if (userPassword.equals(password)) {
	                response.sendRedirect("Customer_Calendar.html");
	            } else {
	            	errorMessage = "Incorrect password, login failed.";
	            	response.sendRedirect("Error?message=" + errorMessage);
	                return;
	            }
	            
            } else {
            	// Check if an account with that username already exists
            	if (result.next()) {
            		errorMessage = "An account with that username already exists.";
            		response.sendRedirect("Error?message=" + errorMessage);
            		return;
            	}
            	// Create the insert statement
            	String createSQL = "INSERT INTO userAccounts (USERNAME, PASSWORD) VALUES (?, ?)";
            	preparedStatement = connection.prepareStatement(createSQL);
            	preparedStatement.setString(1, username);
            	preparedStatement.setString(2, password);
            	// Execute the insert
            	preparedStatement.executeUpdate();
            	// Display success
            	errorMessage = "Account successfully created!";
            	response.sendRedirect("Error?message=" + errorMessage);
            	return;
            }

        } catch (SQLException se) {
            se.printStackTrace();
            out.println("<html>SQL Exception</html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html>DBConnection Failed</html>");
        }
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
