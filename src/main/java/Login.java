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
        PrintWriter out = response.getWriter();
        // Get the username and password from the form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
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

            // Check if an account was found
            if (!result.next()) {
                out.println("<html>No user found with this username</html>");
                return;
            }

            // If an account was found, retrieve the password from the DB
            String userPassword = result.getString("PASSWORD").trim();
            // Check if the input password matches the password in the DB
            if (userPassword.equals(password)) {
                out.println("<html>Login success!</html>");
                response.sendRedirect("Customer_Calendar.html");
            } else {
                out.println("<html>Incorrect password, login failed.</html>");
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
