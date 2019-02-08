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
public class OpTimeAddServlet extends HttpServlet {

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

		String addRno = request.getParameter("add_rno");
		String[] addTime = new String[10];
    int count = 0;
    for( int i = 1; i <= 10; i++ ){
      addTime[i-1] = request.getParameter("no" +i );
      if( addTime[i-1] != null ){
        if( !addTime[i-1].equals("") ) count++;
      }
    }

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      stmt.executeUpdate("DELETE FROM results WHERE race_no = " +addRno );

      for(int i = 0; i < count; i++ ){
        int rank = i + 1;
			  stmt.executeUpdate("INSERT INTO results VALUES(" +addRno+ ", " +rank+ ", '" +addTime[i]+ "' )");
      }

      out.println("<br>登録しました。<br><br>");

		} catch (Exception e) {
			e.printStackTrace();
			out.println("エラー：正しく入力されていません。");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		out.println("<a href=\"optime\">タイム登録に戻る</a><br><br>");
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
