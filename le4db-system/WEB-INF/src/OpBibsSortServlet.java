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
public class OpBibsSortServlet extends HttpServlet {

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
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      //ゼッケン番号を削除
      stmt.executeUpdate("DELETE FROM bibs" );

      String[] univName = new String[1024];
      int[] domNo = new int[1024];
      int count = 0;

      //大学名、大学内Noを保存
			ResultSet rs = stmt
					.executeQuery("select * from (players natural left join reads)"
          + " order by convert_to(univ_read, 'UTF8'), grade desc, type, dom_no");
			while (rs.next()) {
        univName[count] = rs.getString("univ_name");
				domNo[count] = rs.getInt("dom_no");
        count++;
			}
			rs.close();

      //ゼッケンを割り振り
      for( int i = 0; i < count ; i++ ){
        int bibs = i + 1;
        stmt.executeUpdate("INSERT INTO bibs VALUES(" +bibs+ ", '" + univName[i] + "', " +domNo[i]+  ")");
      }

      out.println("ゼッケン番号を割り振りました。<br><br>");

		} catch (Exception e) {
      out.println("エラー");
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

    out.println("<a href=\"opbibs\">選手名簿に戻る</a><br><br>");
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
