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
public class OpRaceServlet extends HttpServlet {

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

		out.println("<h3>エントリー一覧・レース作成</h3>");
		out.println("<a href=\"operation\">管理ページに戻る</a><br><br>");
		out.println("<form action=\"opraceall\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"password\" value=\"\"/>");
		out.println("<input type=\"submit\" value=\"一括自動作成する\"/>");
		out.println("</form>");
		out.println("※初回のみ実行してください。レースNoや時間がリセットされます。");



		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th rowspan=2>レース名<th rowspan=2>エントリー<br>艇(組)数<th rowspan=2>エントリー<th colspan=4>組数<th rowspan=2>操作</tr>");
      out.println("<tr><th>1次H<th>　H　<th>　SF　<th>　F　</tr>");

      ResultSet rs = null;
      int count = 0;
      String[] raceName = { "K-1-1000m", "C-1-1000m", "WK-1-500m", "WC-1-500m",
                            "JK-1-500m", "JC-1-500m", "JWK-1-500m", "JWC-1-500m",
                            "K-2-1000m", "C-2-1000m", "WK-2-500m", "WC-2-500m",
                            "JK-2-500m", "JC-2-500m", "JWK-2-500m", "JWC-2-500m"};
      for( int i = 0; i < raceName.length ; i++ ){
			  rs = stmt.executeQuery("select count(*) from entries where race_name='" +raceName[i]+ "'");
        rs.next();
			  count = rs.getInt("count");
        //ペアの場合は半分
        if( raceName[i].matches(".*-2-.*")){ count = count / 2;}
        out.println("<tr>");
        out.println("<td>" +raceName[i]+ "</td>");
        out.println("<td>" +count+  "</td>");
        out.println("<td>");
        out.println("<form action=\"opentry\" method=\"GET\">");
    		out.println("<input type=\"hidden\" name=\"entry_race\" value=\"" +raceName[i]+ "\" />");
    		out.println("<input type=\"submit\" value=\"一覧\"/>");
				out.println("</form></td>");
        rs.close();
        rs = stmt.executeQuery("select count(*) from races where race_name='" +raceName[i]+ "' and stage = '1次H'");
        rs.next();
        count = rs.getInt("count");
        out.println("<td>" +count+ "</td>");
        rs.close();
        rs = stmt.executeQuery("select count(*) from races where race_name='" +raceName[i]+ "' and stage = 'H'");
        rs.next();
        count = rs.getInt("count");
        out.println("<td>" +count+ "</td>");
        rs.close();
        rs = stmt.executeQuery("select count(*) from races where race_name='" +raceName[i]+ "' and stage = 'SF'");
        rs.next();
        count = rs.getInt("count");
        out.println("<td>" +count+ "</td>");
        rs.close();
        rs = stmt.executeQuery("select count(*) from races where race_name='" +raceName[i]+ "' and stage = 'F'");
        rs.next();
        count = rs.getInt("count");
        out.println("<td>" +count+ "</td>");
        rs.close();
        out.println("<td>");
        out.println("<form action=\"opracemake\" method=\"GET\">");
    		out.println("<input type=\"hidden\" name=\"make_race\" value=\"" +raceName[i]+ "\" />");
    		out.println("<input type=\"submit\" value=\"作成・更新\"/>");
				out.println("</form></td>");
        out.println("</tr>");
      }

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

    out.println("<a href=\"operation\">管理ページに戻る</a><br>");
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
