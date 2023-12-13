import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Error
 */
@WebServlet("/Error")
public class Error extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Error() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String errorMessage = request.getParameter("message");
        String htmlPage = "<!DOCTYPE html>\n"
        		+ "<html lang=\"en\">\n"
        		+ "<head>\n"
        		+ "    <meta charset=\"UTF-8\">\n"
        		+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
        		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
        		+ "    <title>Message</title>\n"
        		+ "    <style>\n"
        		+ "		\n"
        		+ "        body {\n"
        		+ "		    font-family: 'IBM Plex Sans', sans-serif;\n"
        		+ "    		/* background-color: #f4f4f4; */\n"
        		+ "    		background: radial-gradient(at 50% 75%, rgb(255, 255, 255), rgb(39, 186, 255));\n"
        		+ "            display: flex;\n"
        		+ "            align-items: center;\n"
        		+ "            justify-content: center;\n"
        		+ "            height: 100vh;\n"
        		+ "            margin: 0;\n"
        		+ "        }\n"
        		+ "\n"
        		+ "        .container {\n"
        		+ "            text-align: center;\n"
        		+ "        }\n"
        		+ "\n"
        		+ "        .centeredText {\n"
        		+ "            font-size: 24px;\n"
        		+ "            margin-bottom: 20px;\n"
        		+ "            color:crimson\n"
        		+ "        }\n"
        		+ "\n"
        		+ "        .redirectButton {\n"
        		+ "            background-color: #4CAF50;\n"
        		+ "            color: white;\n"
        		+ "            padding: 10px 20px;\n"
        		+ "            text-align: center;\n"
        		+ "            text-decoration: none;\n"
        		+ "            display: inline-block;\n"
        		+ "            font-size: 16px;\n"
        		+ "            cursor: pointer;\n"
        		+ "            border-radius: 5px;\n"
        		+ "        }\n"
        		+ "    </style>\n"
        		+ "</head>\n"
        		+ "<body>\n"
        		+ "    <div class=\"container\">\n"
        		+ "        <div class=\"centeredText\">\n"
        		+ 				errorMessage
        		+ "        </div>\n"
        		+ "        <a href=\"index.html\" class=\"redirectButton\">Return to Login Page</a>\n"
        		+ "    </div>\n"
        		+ "</body>\n"
        		+ "</html>";
        
        response.setContentType("text/html");
        response.getWriter().write(htmlPage);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
