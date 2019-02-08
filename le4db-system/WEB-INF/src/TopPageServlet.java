import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TopPageServlet extends HttpServlet {


	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<body>");

    out.println("<h1>選手登録</h1>");
   out.println("選手登録・変更される大学代表者の方は<a href=\"reguniv\">こちら</a><br>");
   out.println("<h1>記録速報</h1>");
   out.println("記録速報は<a href=\"result\">こちら</a>で実施中です</a><br>");
   out.println("<h2>関係者用</h2>");
   out.println("<a href=\"operation\">要パスワード</a><br>");

		out.println("</body>");
		out.println("</html>");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
