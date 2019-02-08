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
public class OpReadServlet extends HttpServlet {

	private String _hostname = null;
	private String _dbname = null;
	private String _username = null;
	private String _password = null;

	public void init() throws ServletException {
		// iniファイルから自分のデータベース情報を読み込む
		String iniFilePath = getServletConfig().getServletContext()
				.getRealPath("WEB-INF/le4db.ini");
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			Properties prop = new Properties();
			prop.load(fis);
			_hostname = prop.getProperty("hostname");
			_dbname = prop.getProperty("dbname");
			_username = prop.getProperty("username");
			_password = prop.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>参加大学一覧・読み仮名指定</h3>");
    out.println("<a href=\"operation\">管理ページに戻る</a><br>");

		out.println("<h3>参加大学一覧</h3>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>大学名</th></tr>");

			ResultSet rs = stmt
					.executeQuery("SELECT DISTINCT univ_name FROM players");
			while (rs.next()) {
				String univ_name = rs.getString("univ_name");

				out.println("<tr>");
				out.println("<td>" + univ_name + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");

      out.println("<h3>大学読み仮名一覧</h3>");

      out.println("<table border=\"1\">");
			out.println("<tr><th>大学名</th><th>読みがな</th><th>操作</th></tr>");

      rs = stmt.executeQuery("SELECT * FROM reads");
			while (rs.next()) {
				String univ_name = rs.getString("univ_name");
        String univ_read = rs.getString("univ_read");
				out.println("<tr>");
				out.println("<td>" + univ_name + "</td>");
        out.println("<td>" + univ_read + "</td>");
        out.println("<td>");
        out.println("<form action=\"opreaddelete\" method=\"GET\">");
    		out.println("<input type=\"hidden\" name=\"delete_uname\" value=\"" +univ_name+ "\" />");
        out.println("<input type=\"hidden\" name=\"delete_uread\" value=\"" +univ_read+ "\" />");
    		out.println("<input type=\"submit\" value=\"削除\"/>");
				out.println("</form></td>");
				out.println("</tr>");
			}
			rs.close();
      out.println("</table>");

      out.println("<h4>読みがな追加</h4>");
  		out.println("<form action=\"opreadadd\" method=\"GET\">");
  		out.println("大学名： ");
  		out.println("<input type=\"text\" name=\"add_uname\" />");
  		out.println("<br/>");
  		out.println("読みがな： ");
  		out.println("<input type=\"text\" name=\"add_uread\"/>");
      out.println("<br/>");
  		out.println("<input type=\"submit\" value=\"追加\"/>");
  		out.println("</form>");

		} catch (Exception e) {
      out.println("エラー：管理者に問い合わせてください。");
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

    out.println("<br/>");
    out.println("<a href=\"operation\">管理ページに戻る</a><br>");
		out.println("<br/>");
		out.println("<a href=\"toppage\">トップページに戻る</a>");

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
