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
public class OpBibsServlet extends HttpServlet {

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

		out.println("<h3>現在の選手登録内容(選手名簿)</h3>");
    out.println("<a href=\"operation\">管理ページに戻る</a><br><br>");
    out.println("<form action=\"opbibssort\" method=\"GET\">");
		out.println("<input type=\"submit\" value=\"ゼッケン番号を割り振る・再度割り振る\"/>");
		out.println("</form>");
    out.println("※初回割り振り時や、ゼッケン番号が抜けている時、順番がおかしい時に押してください。<br><br>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>ゼッケンNo<th>大学名<th>名前<th>種目<th>学年</th></tr>");

			ResultSet rs = stmt
					.executeQuery("select * from (players natural left join reads)"
          + " natural left join bibs order by convert_to(univ_read, 'UTF8'),"
          + "grade desc, type, dom_no");
			while (rs.next()) {
        String univ_name = rs.getString("univ_name");
				String p_name = rs.getString("p_name");
        int grade = rs.getInt("grade");
				String type = rs.getString("type");
        int bib_no = rs.getInt("bib_no");

				out.println("<tr>");
        out.println("<td>" + bib_no + "</td>");
        out.println("<td>" + univ_name + "</td>");
				out.println("<td>" + p_name + "</td>");
				out.println("<td>" + grade + "</td>");
				out.println("<td>" + type + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");

		} catch (Exception e) {
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
