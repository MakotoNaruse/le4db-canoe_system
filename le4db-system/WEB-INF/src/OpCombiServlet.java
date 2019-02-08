import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class OpCombiServlet extends HttpServlet {

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

		String raceName = request.getParameter("make_race");

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>抽選実施・組み合わせ確認</h3>");
    out.println("<a href=\"operation\">管理ページに戻る</a><br><br>");
		out.println("<form action=\"opcombido\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"password\" value=\"\"/>");
		out.println("<input type=\"submit\" value=\"抽選を実施する。\"/>");
		out.println("</form>");
		out.println("※抽選を実施します。既に組み合わせがある場合は新たに抽選します。");

		out.println("<h3>現在の組み合わせ内容</h3>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();


			ResultSet rs = stmt
					.executeQuery("select * from races natural left join (participates "
          + "natural left join (bibs natural left join players)) where race_name like '%-1-%' order by race_no, rane_no");
      out.println("<table border=\"1\">");
    	out.println("<tr><th>レースNo<th>レース名<th colspan=2>組<th>レーン<th>ゼッケン<th>名前<th>大学</tr>");
			while (rs.next()) {
				int race_no = rs.getInt("race_no");
				String race_name = rs.getString("race_name");
        String stage = rs.getString("stage");
				String set = rs.getString("set");
        int rane_no = rs.getInt("rane_no");
        int bib_no = rs.getInt("bib_no");
        String p_name = rs.getString("p_name");
				String univ_name = rs.getString("univ_name");
        if( set == null ){ set = "";}

        if( rane_no != 0 ){
  				out.println("<tr>");
  				out.println("<td>" + race_no + "</td>");
  				out.println("<td>" + race_name + "</td>");
          out.println("<td style=\"width:40px;\">" + stage + "</td>");
  				out.println("<td style=\"width:20px;\">" + set + "</td>");
          out.println("<td>" + rane_no + "</td>");
  				out.println("<td>" + bib_no + "</td>");
          out.println("<td>" + p_name + "</td>");
  				out.println("<td>" + univ_name + "</td>");
  				out.println("</tr>");
        }
			}
			rs.close();

			out.println("</table>");

		} catch (Exception e) {
			e.printStackTrace();
      out.println("エラー<br><br>");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

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
