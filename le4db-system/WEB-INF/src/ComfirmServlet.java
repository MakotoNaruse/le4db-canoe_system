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
public class ComfirmServlet extends HttpServlet {

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

		String univName = request.getParameter("univ_name");

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>登録内容確認</h3>");
		out.println("大学名：" + univName);

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      out.println("<h3>選手名簿</h3>");

			out.println("<table border=\"1\">");
			out.println("<tr><th>大学内No</th><th>選手名</th><th>種目</th><th>学年</th></tr>");

			ResultSet rs = stmt
					.executeQuery("SELECT dom_no, p_name, type, grade FROM players WHERE univ_name = '" + univName + "' order by dom_no");
			while (rs.next()) {
				int dom_no = rs.getInt("dom_no");
				String p_name = rs.getString("p_name");
				String type = rs.getString("type");
				int grade = rs.getInt("grade");

				out.println("<tr>");
				out.println("<td>" + dom_no + "</td>");
				out.println("<td>" + p_name + "</td>");
				out.println("<td>" + type + "</td>");
				out.println("<td>" + grade + "</td>");
				out.println("</tr>");
			}
			rs.close();
      out.println("</table>");
      out.println("<form action=\"search\" method=\"GET\">");
  		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName + "\"/>");
  		out.println("選手名簿登録へ");
  		out.println("<input type=\"submit\" value=\"戻る\"/>");
  		out.println("</form>");

      out.println("<h3>エントリー内容</h3>");

      out.println("<table border=\"1\">");
			out.println("<tr><th>エントリー処理No</th><th>大学内No</th><th>選手名</th><th>種目</th><th>エントリー</th></tr>");

			rs = stmt
					.executeQuery("select ent_no, dom_no, p_name, type, race_name from entries "
          + "natural left join players where univ_name = '" + univName + "' order by dom_no, race_name");
			while (rs.next()) {
				int ent_no = rs.getInt("ent_no");
				int dom_no = rs.getInt("dom_no");
				String p_name = rs.getString("p_name");
        String type = rs.getString("type");
				String race_name = rs.getString("race_name");

				out.println("<tr>");
				out.println("<td>" + ent_no + "</td>");
				out.println("<td>" + dom_no + "</td>");
				out.println("<td>" + p_name + "</td>");
        out.println("<td>" + type + "</td>");
				out.println("<td>" + race_name + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");
      out.println("<form action=\"entry\" method=\"GET\">");
  		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName + "\"/>");
  		out.println("エントリー登録へ");
  		out.println("<input type=\"submit\" value=\"戻る\"/>");
  		out.println("</form>");

      out.println("<h3>ペア登録内容</h3>");

      out.println("<table border=\"1\">");
			out.println("<tr><th>大学内No<th>選手名<th>エントリー<th>ペアNo</th></tr>");

			rs = stmt
      .executeQuery("select dom_no, p_name, race_name, pair_no from entries "
      + "natural left join (players natural left join pairs) where univ_name = '"
      + univName + "' and race_name like '%-2-%' order by race_name, dom_no");
			while (rs.next()) {
				int dom_no = rs.getInt("dom_no");
				String p_name = rs.getString("p_name");
				String race_name = rs.getString("race_name");
        int pair_no = rs.getInt("pair_no");

				out.println("<tr>");
				out.println("<td>" + dom_no + "</td>");
				out.println("<td>" + p_name + "</td>");
        out.println("<td>" + race_name + "</td>");
				out.println("<td>" + pair_no + "</td>" );
			}
			rs.close();

			out.println("</table>");
      out.println("<form action=\"pair\" method=\"GET\">");
  		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName + "\"/>");
  		out.println("ペア登録へ");
  		out.println("<input type=\"submit\" value=\"戻る\"/>");
  		out.println("</form>");


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

		out.println("<br/>");
    out.println("以上の内容でよろしければ");
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
