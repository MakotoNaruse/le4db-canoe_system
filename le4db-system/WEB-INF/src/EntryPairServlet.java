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
public class EntryPairServlet extends HttpServlet {

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

		String addUname = request.getParameter("univ_name");

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
      //conn.setAutoCommit(false); // 自動コミットを無効に

			stmt = conn.createStatement();

			int max_no = 0;
			ResultSet rs = stmt.executeQuery("SELECT MAX(ent_no) AS max_no FROM entries" );
			while (rs.next()) {
				max_no = rs.getInt("max_no");
			}
			rs.close();

			int addNo = max_no + 1;

      int count = 0;
      int[] addDomNo = new int[256];
      String[] addRace = new String[256];
      rs = stmt.executeQuery("select * from players where univ_name = '" +addUname+ "'" );
      while ( rs.next() ) {
    		addDomNo[count] = rs.getInt("dom_no");
    		String type = rs.getString("type");
        if( type.equals("K") ) { addRace[count] = "K-2-1000m"; }
        if( type.equals("C") ) { addRace[count] = "C-2-1000m"; }
        if( type.equals("WK") ) { addRace[count] = "WK-2-500m"; }
        if( type.equals("WC") ) { addRace[count] = "WC-2-500m"; }
        if( type.equals("JK") ) { addRace[count] = "JK-2-500m"; }
        if( type.equals("JC") ) { addRace[count] = "JC-2-500m"; }
        if( type.equals("JWK") ) { addRace[count] = "JWK-2-500m"; }
        if( type.equals("JWC") ) { addRace[count] = "JWC-2-500m"; }
        count++;
      }
      rs.close();

      for( int i = 0; i < count ; i++ ){
        stmt.executeUpdate("INSERT INTO entries VALUES(" +addNo+ ", '" + addUname +
        "', " + addDomNo[i] + ", '" + addRace[i] + "' )");
        addNo++;
      }
      rs.close();

      //conn.commit();

			out.println("以下の大学のペアエントリーを一括登録しました。<br/><br/>");
			out.println("大学名: " + addUname + "<br/>");



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

		out.println("<br><br><br>");
		out.println("<form action=\"entry\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"univ_name\" value=\"" +addUname+ "\" readonly />");
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
