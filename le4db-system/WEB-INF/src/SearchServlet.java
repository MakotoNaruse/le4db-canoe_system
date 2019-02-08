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
public class SearchServlet extends HttpServlet {

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

		out.println("<h3>選手登録・変更</h3>");
		out.println("大学名：" + univName);

		out.println("<form action=\"entry\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName+ "\"/>");
		out.println("この内容でよければ");
		out.println("<input type=\"submit\" value=\"種目登録へ\"/>");
		out.println("</form>");

		out.println("<h3>現在の選手登録内容</h3>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>大学内No</th><th>選手名</th><th>種目</th><th>学年</th><th>操作</th></tr>");

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
				out.println("<td>");
				out.println("<div style=\"display:inline-flex\">");
				out.println("<form action=\"delete\" method=\"GET\">");
				out.println("<input type=\"hidden\" name=\"delete_uname\" value=\"" +univName+ "\" />");
				out.println("<input type=\"hidden\" name=\"delete_no\" value=\"" + dom_no + "\" />");
				out.println("<input type=\"submit\" value=\"削除\"/>");
				out.println("</form>");
				out.println("<form action=\"regchange\" method=\"GET\">");
				out.println("<input type=\"hidden\" name=\"change_uname\" value=\"" +univName+ "\" />");
				out.println("<input type=\"hidden\" name=\"change_no\" value=\"" + dom_no + "\" />");
				out.println("<input type=\"submit\" value=\"修正\"/>");
				out.println("</form></div></td>");
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

		out.println("<h3>選手追加</h3>");
		out.println("<form action=\"add\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"add_uname\" value=\"" +univName+ "\" readonly />");
		out.println("<br/>");
		out.println("選手名： ");
		out.println("<input type=\"text\" name=\"add_pname\"/>");
		out.println("<br/>");
		out.println("種目： ");
		//プルダウンメニュー
		out.println("<select name=\"add_type\" size=\"1\">");
		out.println("<option value=\"K\">K</option>");
		out.println("<option value=\"C\">C</option>");
		out.println("<option value=\"WK\">WK</option>");
		out.println("<option value=\"WC\">WC</option>");
		out.println("<option value=\"JK\">JK</option>");
		out.println("<option value=\"JC\">JC</option>");
		out.println("<option value=\"JWK\">JWK</option>");
		out.println("<option value=\"JWC\">JWC</option>");
		out.println("</select>");
		out.println("<br/>");
		out.println("学年： ");
		//プルダウンメニュー
		out.println("<select name=\"add_grade\" size=\"1\">");
		out.println("<option value=\"1\">1回生</option>");
		out.println("<option value=\"2\">2回生</option>");
		out.println("<option value=\"3\">3回生</option>");
		out.println("<option value=\"4\">4回生</option>");
		out.println("</select>");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"追加\"/>");
		out.println("</form>");

		/*
		out.println("<br>");
		out.println("<h3>選手削除</h3>");
		out.println("<form action=\"delete\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"delete_uname\" value=\"" +univName+ "\" readonly />");
		out.println("<br>");
		out.println("大学内ナンバー： ");
		out.println("<input type=\"text\" name=\"delete_no\"/>");
		out.println("<br>");
		out.println("<input type=\"submit\" value=\"削除\"/>");
		out.println("</form>");
		*/


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
