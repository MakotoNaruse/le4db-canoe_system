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
public class EntryAddServlet extends HttpServlet {

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

		String addUname = request.getParameter("add_uname");
		String addDomNo = request.getParameter("add_domno");
		String addRace = request.getParameter("add_race");

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      String pName = null;
      ResultSet rs = stmt.executeQuery("SELECT p_name FROM players where univ_name = '" + addUname +
      "' and dom_no = " + addDomNo );
			while (rs.next()) {
				pName = rs.getString("p_name");
			}
      rs.close();

      if( pName == null ){
        out.println("該当選手がいませんでした。");
      }

      else{
			int max_no = 0;
			rs = stmt.executeQuery("SELECT MAX(ent_no) AS max_no FROM entries" );
			while (rs.next()) {
				max_no = rs.getInt("max_no");
			}
			rs.close();

			int addNo = max_no + 1;
			stmt.executeUpdate("INSERT INTO entries VALUES(" +addNo+ ", '" + addUname +
			 "', " + addDomNo + ", '" + addRace + "' )");

			out.println("以下のエントリーを追加しました。<br/><br/>");
			out.println("大学名: " + addUname + "<br/>");
			out.println("大学内No: " + addDomNo + "<br/>");
			out.println("参加レース: " + addRace + "<br/>");

      }

		} catch (Exception e) {
			e.printStackTrace();
			out.println("エラー：選手登録ができませんでした。");
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
		out.println("<form action=\"entry\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"univ_name\" value=\"" +addUname+ "\" readonly />");
		out.println("のエントリー登録を");
		out.println("<input type=\"submit\" value=\"続ける\"/>");
		out.println("</form>");

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
