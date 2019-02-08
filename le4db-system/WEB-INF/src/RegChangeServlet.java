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
public class RegChangeServlet extends HttpServlet {

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

		String changeUname = request.getParameter("change_uname");
		String changeNo = request.getParameter("change_no");

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      out.println("<h3>選手情報修正</h3>");

      out.println("<h4>変更前</h4>");
      String changePname = null;
      String changeType = null;
      int changeGrade = 0;
			ResultSet rs = stmt.executeQuery("SELECT * FROM players WHERE univ_name = '"
      +changeUname+ "' and dom_no = " +changeNo );
			while (rs.next()) {
				changePname = rs.getString("p_name");
        changeType = rs.getString("type");
        changeGrade = rs.getInt("grade");
        out.println("大学名：　" + changeUname + "<br/>");
        out.println("大学内ナンバー：　" + changeNo + "<br/>");
        out.println("選手名：　" + changePname + "<br/>");
        out.println("種目：　" + changeType + "<br/>");
        out.println("学年：　" + changeGrade + "<br/>");
			}
			rs.close();

      out.println("<br>↓<br>↓<br>↓<br><br>");
      out.println("<h4>変更後</h4>");
      out.println("<form action=\"regupdate\" method=\"GET\">");
      out.println("<input type=\"hidden\" name=\"update_no\" value=\"" +changeNo+ "\"/>");
  		out.println("大学名： ");
  		out.println("<input type=\"text\" name=\"update_uname\" value=\"" +changeUname+ "\" readonly />");
  		out.println("<br/>");
  		out.println("選手名： ");
  		out.println("<input type=\"text\" name=\"update_pname\" value=\"" +changePname+ "\" />");
  		out.println("<br/>");
  		out.println("種目： ");
  		//プルダウンメニュー
      String selectType[] = new String[8];
      for( int i = 0 ; i < 8 ; i++ ){ selectType[i] = ""; }
      if( changeType.equals("K") ) { selectType[0] = "selected"; }
      if( changeType.equals("C") ) { selectType[1] = "selected"; }
      if( changeType.equals("WK") ) { selectType[2] = "selected"; }
      if( changeType.equals("WC") ) { selectType[3] = "selected"; }
      if( changeType.equals("JK") ) { selectType[4] = "selected"; }
      if( changeType.equals("JC") ) { selectType[5] = "selected"; }
      if( changeType.equals("JWK") ) { selectType[6] = "selected"; }
      if( changeType.equals("JWC") ) { selectType[7] = "selected"; }
  		out.println("<select name=\"update_type\" size=\"1\">");
  		out.println("<option " +selectType[0]+ " value=\"K\">K</option>");
  		out.println("<option " +selectType[1]+ " value=\"C\">C</option>");
  		out.println("<option " +selectType[2]+ " value=\"WK\">WK</option>");
  		out.println("<option " +selectType[3]+ " value=\"WC\">WC</option>");
  		out.println("<option " +selectType[4]+ " value=\"JK\">JK</option>");
  		out.println("<option " +selectType[5]+ " value=\"JC\">JC</option>");
  		out.println("<option " +selectType[6]+ " value=\"JWK\">JWK</option>");
  		out.println("<option " +selectType[7]+ " value=\"JWC\">JWC</option>");
  		out.println("</select>");
  		out.println("<br/>");
  		out.println("学年： ");
  		//プルダウンメニュー
      String selectGrade[] = new String[4];
      for( int i = 0 ; i < 4 ; i++ ){ selectGrade[i] = ""; }
      if( changeGrade == 1 ) { selectGrade[0] = "selected"; }
      if( changeGrade == 2 ) { selectGrade[1] = "selected"; }
      if( changeGrade == 3 ) { selectGrade[2] = "selected"; }
      if( changeGrade == 4 ) { selectGrade[3] = "selected"; }
  		out.println("<select name=\"update_grade\" size=\"1\">");
  		out.println("<option " +selectGrade[0]+ " value=\"1\">1回生</option>");
  		out.println("<option " +selectGrade[1]+ " value=\"2\">2回生</option>");
  		out.println("<option " +selectGrade[2]+ " value=\"3\">3回生</option>");
  		out.println("<option " +selectGrade[3]+ " value=\"4\">4回生</option>");
  		out.println("</select>");
  		out.println("<br/><br/>");
  		out.println("<input type=\"submit\" value=\"変更\"/>");
  		out.println("</form>");


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
		out.println("<form action=\"search\" method=\"GET\">");
		out.println("大学名： ");
		out.println("<input type=\"text\" name=\"univ_name\" value=\"" +changeUname+ "\" readonly />");
		out.println("の選手登録に");
		out.println("<input type=\"submit\" value=\"戻る\"/>");
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
