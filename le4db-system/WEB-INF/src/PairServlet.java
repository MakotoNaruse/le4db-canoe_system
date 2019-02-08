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
public class PairServlet extends HttpServlet {

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

		out.println("<h3>ペア登録・変更</h3>");
		out.println("大学名：" + univName);

		out.println("<form action=\"entry\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName+ "\"/>");
		out.println("エントリー登録に");
		out.println("<input type=\"submit\" value=\"戻る\"/>");
		out.println("</form>");
    out.println("<form action=\"comfirm\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"univ_name\" value=\"" +univName+ "\"/>");
		out.println("この内容でよければ");
		out.println("<input type=\"submit\" value=\"登録内容の確認へ\"/>");
		out.println("</form>");

		out.println("<h3>現在のペア登録内容</h3>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>大学内No<th>選手名<th>エントリー<th>ペアNo<th>操作</th></tr>");

			ResultSet rs = stmt
      .executeQuery("select dom_no, p_name, race_name, pair_no from entries "
      + "natural left join (players natural left join pairs) where univ_name = '"
      + univName + "' and race_name like '%-2-%' order by race_name, dom_no");
			while (rs.next()) {
				int dom_no = rs.getInt("dom_no");
				String p_name = rs.getString("p_name");
				String race_name = rs.getString("race_name");
        int pair_no = rs.getInt("pair_no");

				out.println("<tr>");
				out.println("<td>" + dom_no + "</td>");
				out.println("<td>" + p_name + "</td>");
        out.println("<td>" + race_name + "</td>");
				out.println("<td>");
        if( pair_no == 0 ){
          out.println("<form action=\"pairadd\" method=\"GET\">");
          //out.println("<div style=\"vertical-align:middle;\">");
      		out.println("<input type=\"hidden\" name=\"add_uname\" value=\"" +univName+ "\" />");
      		out.println("<input type=\"hidden\" name=\"add_domno\" value=\"" +dom_no+ "\" />");
          out.println("<input type=\"text\" name=\"add_pairno\" style=\"width:50px;\" />");
          //out.println("</div>");
          out.println("</td>");
      		out.println("<td><input type=\"submit\" value=\"追加\"/>");
  				out.println("</form></td>");
        }
        else{
          out.println( pair_no + "</td>" );
          out.println("<td>");
          out.println("<form action=\"pairdelete\" method=\"GET\">");
    		  out.println("<input type=\"hidden\" name=\"delete_uname\" value=\"" +univName+ "\" />");
          out.println("<input type=\"hidden\" name=\"delete_no\" value=\"" +dom_no+ "\" />");
          out.println("<input type=\"hidden\" name=\"delete_pair\" value=\"" +pair_no+ "\" />");
    		  out.println("<input type=\"submit\" value=\"削除\"/>");
				  out.println("</form></td>");
				  out.println("</tr>");
        }
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
