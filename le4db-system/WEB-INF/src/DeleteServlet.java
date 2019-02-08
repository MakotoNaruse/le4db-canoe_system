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
public class DeleteServlet extends HttpServlet {

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

		String deleteUname = request.getParameter("delete_uname");
		String deleteNo = request.getParameter("delete_no");

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("以下の選手を削除しました。<br/><br/>");
			out.println("大学名: " + deleteUname + "<br/>");
			out.println("大学内ナンバー: " + deleteNo + "<br/>");

			ResultSet rs = stmt.executeQuery("SELECT * FROM players WHERE univ_name = '" +deleteUname + "' and dom_no = " + deleteNo);
			while (rs.next()) {
				String pName = rs.getString("p_name");
				String type = rs.getString("type");
				int grade = rs.getInt("grade");

				out.println("選手名: " + pName + "<br/>");
				out.println("種目: " + type + "<br/>");
				out.println("学年: " + grade + "<br/>");
			}
			rs.close();

			stmt.executeUpdate("DELETE FROM players WHERE univ_name = '" +deleteUname + "' and dom_no = " + deleteNo);

		} catch (Exception e) {
			e.printStackTrace();
			out.println("エラー：該当選手がいませんでした。<br><br>");
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
		out.println("<input type=\"text\" name=\"univ_name\" value=\"" +deleteUname+ "\" readonly />");
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
