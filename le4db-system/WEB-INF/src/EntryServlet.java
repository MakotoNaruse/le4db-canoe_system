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
public class EntryServlet extends HttpServlet {

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

		out.println("<h3>エントリー登録・変更</h3>");
		out.println("大学名：" + univName);

		out.println("<form action=\"search\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName+ "\"/>");
		out.println("選手登録に");
		out.println("<input type=\"submit\" value=\"戻る\"/>");
		out.println("</form>");
    out.println("<form action=\"pair\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName+ "\"/>");
		out.println("この内容でよければ");
		out.println("<input type=\"submit\" value=\"ペア登録へ\"/>");
		out.println("</form>");

		out.println("<h3>現在のエントリー内容</h3>");
    out.println("<form action=\"entryall\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName+ "\"/>");
		out.println("<input type=\"submit\" value=\"全員シングル・ペアにエントリーする\"/>");
		out.println("</form>");
    out.println("<form action=\"entryinit\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName+ "\"/>");
		out.println("<input type=\"submit\" value=\"全て削除して初期化する\"/>");
		out.println("</form>");
    out.println("<br>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>大学内No</th><th>選手名</th><th>種目</th><th>エントリー</th><th>操作</th></tr>");

			ResultSet rs = stmt
					.executeQuery("select ent_no, dom_no, p_name, type, race_name from entries "
          + "natural left join players where univ_name = '" + univName + "' order by dom_no, race_name");
			while (rs.next()) {
				int ent_no = rs.getInt("ent_no");
				int dom_no = rs.getInt("dom_no");
				String p_name = rs.getString("p_name");
        String type = rs.getString("type");
				String race_name = rs.getString("race_name");

				out.println("<tr>");
				//out.println("<td>" + ent_no + "</td>");
				out.println("<td>" + dom_no + "</td>");
				out.println("<td>" + p_name + "</td>");
        out.println("<td>" + type + "</td>");
				out.println("<td>" + race_name + "</td>");
        out.println("<td>");
        out.println("<form action=\"entrydelete\" method=\"GET\">");
    		out.println("<input type=\"hidden\" name=\"delete_uname\" value=\"" +univName+ "\" />");
    		out.println("<input type=\"hidden\" name=\"delete_no\" value=\"" +ent_no+ "\" />");
    		out.println("<input type=\"submit\" value=\"削除\"/>");
				out.println("</form></td>");
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

		out.println("<h3>エントリー追加</h3>");
		out.println("<form action=\"entryadd\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"add_uname\" value=\"" +univName+ "\" readonly />");
		out.println("<br/>");
		out.println("大学内ナンバー： ");
		out.println("<input type=\"text\" name=\"add_domno\"/>");
		out.println("<br/>");
		out.println("レース： ");
		//プルダウンメニュー
		out.println("<select name=\"add_race\" size=\"1\">");
		out.println("<option value=\"K-1-1000m\">K-1-1000m</option>");
		out.println("<option value=\"K-2-1000m\">K-2-1000m</option>");
		out.println("<option value=\"C-1-1000m\">C-1-1000m</option>");
		out.println("<option value=\"C-2-1000m\">C-2-1000m</option>");
    out.println("<option value=\"WK-1-500m\">WK-1-500m</option>");
		out.println("<option value=\"WK-2-500m\">WK-2-500m</option>");
    out.println("<option value=\"WC-1-500m\">WC-1-500m</option>");
		out.println("<option value=\"WC-2-500m\">WC-2-500m</option>");
    out.println("<option value=\"JK-1-500m\">JK-1-500m</option>");
		out.println("<option value=\"JK-2-500m\">JK-2-500m</option>");
    out.println("<option value=\"JC-1-500m\">JC-1-500m</option>");
		out.println("<option value=\"JC-2-500m\">JC-2-500m</option>");
    out.println("<option value=\"JWK-1-500m\">JWK-1-500m</option>");
		out.println("<option value=\"JWK-2-500m\">JWK-2-500m</option>");
    out.println("<option value=\"JWC-1-500m\">JWC-1-500m</option>");
		out.println("<option value=\"JWC-2-500m\">JWC-2-500m</option>");
		out.println("</select>");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"追加\"/>");
		out.println("</form>");

    /*
		out.println("<br>");
		out.println("<h3>エントリー削除</h3>");
		out.println("<form action=\"entrydelete\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"delete_uname\" value=\"" +univName+ "\" readonly />");
		out.println("<br>");
		out.println("エントリー管理ナンバー： ");
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
