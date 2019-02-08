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
public class RegUpdateServlet extends HttpServlet {

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

    String updateNo = request.getParameter("update_no");
		String updateUname = request.getParameter("update_uname");
		String updatePname = request.getParameter("update_pname");
		String updateType = request.getParameter("update_type");
		String updateGrade = request.getParameter("update_grade");

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      stmt.executeUpdate("UPDATE players SET p_name = '" + updatePname+
      "', type = '" + updateType + "', grade = " +updateGrade+
      " WHERE univ_name = '" +updateUname+ "' and dom_no = " +updateNo );

			out.println("以下の内容に変更しました。<br/><br/>");
			out.println("大学名: " + updateUname + "<br/>");
			out.println("大学内No: " + updateNo + "<br/>");
			out.println("選手名: " + updatePname + "<br/>");
			out.println("種目: " + updateType + "<br/>");
			out.println("学年: " + updateGrade + "<br/>");

		} catch (Exception e) {
			e.printStackTrace();
			out.println("エラー：変更ができませんでした。");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		out.println("<br><br><br>");
		out.println("<form action=\"search\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"univ_name\" value=\"" +updateUname+ "\" readonly />");
		out.println("の選手登録を");
		out.println("<input type=\"submit\" value=\"続ける\"/>");
		out.println("</form>");

		out.println("<br><br/>");
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
