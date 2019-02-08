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
public class OpEntryServlet extends HttpServlet {

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

		String raceName = request.getParameter("entry_race");

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>" +raceName+ "のエントリー一覧</h3>");

    out.println("<a href=\"oprace\">エントリー一覧・レース作成に戻る</a><br><br>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>ゼッケン</th><th>大学名</th><th>選手名</th><th>種目</th>");
      //if( raceName[i].matches(".*-2-.*"){　out.println("<th>ペアゼッケン</th>")}
      out.println("</tr>");

			ResultSet rs = stmt
					.executeQuery("select bib_no, univ_name, p_name, type, pair_no from entries "
          + "natural left join((players natural left join pairs) natural left join bibs)"
          + " where race_name = '" +raceName+ "'");
			while (rs.next()) {
				int bib_no = rs.getInt("bib_no");
				String univ_name = rs.getString("univ_name");
				String p_name = rs.getString("p_name");
        String type = rs.getString("type");

				out.println("<tr>");
				out.println("<td>" + bib_no + "</td>");
				out.println("<td>" + univ_name + "</td>");
				out.println("<td>" + p_name + "</td>");
        out.println("<td>" + type + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");

		} catch (Exception e) {
			e.printStackTrace();
      out.println("エラー");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

    out.println("<a href=\"oprace\">エントリー一覧・レース作成に戻る</a><br><br>");
    out.println("<a href=\"operation\">管理ページに戻る</a><br><br>");
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
