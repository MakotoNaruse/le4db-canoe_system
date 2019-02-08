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
public class EntryAllServlet extends HttpServlet {

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
      String[] addType = new String[256];
      rs = stmt.executeQuery("select * from players where univ_name = '" +addUname+ "'" );
      while ( rs.next() ) {
    		addDomNo[count] = rs.getInt("dom_no");
    		addType[count] = rs.getString("type");
        count++;
      }
      rs.close();

      for( int i = 0; i < count ; i++ ){
        String addRace1 = "";
        String addRace2 = "";
        if( addType[i].equals("K") ) { addRace1 = "K-1-1000m"; addRace2 = "K-2-1000m";}
        if( addType[i].equals("C") ) { addRace1 = "C-1-1000m"; addRace2 = "C-2-1000m";}
        if( addType[i].equals("WK") ) { addRace1 = "WK-1-500m"; addRace2 = "WK-2-500m";}
        if( addType[i].equals("WC") ) { addRace1 = "WC-1-500m"; addRace2 = "WC-2-500m";}
        if( addType[i].equals("JK") ) { addRace1 = "JK-1-500m"; addRace2 = "JK-2-500m";}
        if( addType[i].equals("JC") ) { addRace1 = "JC-1-500m"; addRace2 = "JC-2-500m";}
        if( addType[i].equals("JWK") ) { addRace1 = "JWK-1-500m"; addRace2 = "JWK-2-500m";}
        if( addType[i].equals("JWC") ) { addRace1 = "JWC-1-500m"; addRace2 = "JWC-2-500m";}
        stmt.executeUpdate("INSERT INTO entries VALUES(" +addNo+ ", '" + addUname +
        "', " + addDomNo[i] + ", '" + addRace1 + "' )");
        addNo++;
        stmt.executeUpdate("INSERT INTO entries VALUES(" +addNo+ ", '" + addUname +
        "', " + addDomNo[i] + ", '" + addRace2 + "' )");
        addNo++;
      }
      rs.close();

      //conn.commit();

			out.println("以下の大学のエントリーを一括登録しました。<br/><br/>");
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
