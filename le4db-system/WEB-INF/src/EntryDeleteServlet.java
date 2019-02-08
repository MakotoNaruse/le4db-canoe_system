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
public class EntryDeleteServlet extends HttpServlet {

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

      String deletedUname = "";
      ResultSet rs = stmt.executeQuery("SELECT univ_name FROM entries WHERE ent_no = " +deleteNo  );
			while (rs.next()) {
				deletedUname = rs.getString("univ_name");
			}
      rs.close();

      if( deleteUname.equals(deletedUname) ){
        out.println("以下のエントリーを削除しました。<br/><br/>");
  			out.println("大学名: " + deleteUname + "<br/>");

        rs = stmt.executeQuery("SELECT * FROM entries natural left join players WHERE ent_no = " +deleteNo  );
  			while (rs.next()) {
  				int domNo = rs.getInt("dom_no");
  				String pName = rs.getString("p_name");
  				String race = rs.getString("race_name");

  				out.println("大学内ナンバー: " + domNo + "<br/>");
  				//out.println("選手名: " + pName + "<br/>");
  				out.println("レース: " + race + "<br/>");
  			}
  			rs.close();
        stmt.executeUpdate("DELETE FROM entries WHERE ent_no = " +deleteNo );
      }

      else{
        out.println("エラー：エントリーが存在しない、または自大学以外のエントリーに対する削除です。");
      }


		} catch (Exception e) {
			e.printStackTrace();
			out.println("エラー：該当エントリーがありませんでした。<br><br>");
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
		out.println("<input type=\"text\" name=\"univ_name\" value=\"" +deleteUname+ "\" readonly />");
		out.println("のエントリー登録を");
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
