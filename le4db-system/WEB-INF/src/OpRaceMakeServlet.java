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
public class OpRaceMakeServlet extends HttpServlet {

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

		out.println("<h3>レース作成・更新</h3>");
		out.println("レース名：" + raceName);
    out.println("<br><br><a href=\"oprace\">エントリー一覧・レース作成に戻る</a><br><br>");

		out.println("<h3>現在のレース内容</h3>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();


			ResultSet rs = stmt
					.executeQuery("select * from races where race_name = '" +raceName+ "' order by race_no");
      out.println("<table border=\"1\">");
    	out.println("<tr><th>レースNo</th><th>時刻</th><th>レース名</th><th colspan=2>組</th><th>操作</th></tr>");
			while (rs.next()) {
				int race_no = rs.getInt("race_no");
				String race_time = rs.getString("race_time");
				String race_name = rs.getString("race_name");
        String stage = rs.getString("stage");
				String set = rs.getString("set");
        if( set == null ){ set = "";}

				out.println("<tr>");
				if( race_no <= 0 ){
					out.println("<td>未定</td>");
				}
				else{
					out.println("<td>" +race_no+ "</td>");
				}
				if( race_time == null ){
					out.println("<td>未定</td>");
				}
				else{
					out.println("<td>" +race_time+ "</td>");
				}
				out.println("<td>" + race_name + "</td>");
        out.println("<td style=\"width:40px;\">" + stage + "</td>");
				out.println("<td style=\"width:20px;\">" + set + "</td>");
        out.println("<td>");
        out.println("<div style=\"display:inline-flex\">");
        out.println("<form action=\"opracechange\" method=\"GET\">");
    		out.println("<input type=\"hidden\" name=\"changed_rno\" value=\"" +race_no+ "\" />");
				out.println("<input type=\"hidden\" name=\"changed_rname\" value=\"" +race_name+ "\" />");
    		out.println("レースNoを<input type=\"text\" name=\"change_rno\" style=\"width:25px;\"/>、");
    		out.println("時刻を<input type=\"text\" name=\"change_rtime\" style=\"width:35px;\"/>に");
    		out.println("<input type=\"submit\" value=\"変更\"/></form>　");
        out.println("レースを");
        out.println("<form action=\"opracedelete\" method=\"GET\">");
    		out.println("<input type=\"hidden\" name=\"delete_rname\" value=\"" +raceName+ "\" />");
    		out.println("<input type=\"hidden\" name=\"delete_rno\" value=\"" +race_no+ "\" />");
    		out.println("<input type=\"submit\" value=\"削除\"/>");
				out.println("</form></div></td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");

			out.println("<h3>レース追加</h3>");
			out.println("<form action=\"opraceadd\" method=\"GET\">");
			out.println("レースナンバー： ");
			out.println("<input type=\"text\" name=\"add_rno\" />");
			out.println("<br/>");
			out.println("時刻： ");
			out.println("<input type=\"text\" name=\"add_rtime\" />");
			out.println("<br/>");
			out.println("レース名： ");
			out.println("<input type=\"text\" name=\"add_race\" value=\"" +raceName+ "\" readonly />");
			out.println("<br/>");
			out.println("ステージ： ");
			//プルダウンメニュー
			out.println("<select name=\"add_stage\" size=\"1\">");
			out.println("<option value=\"1次H\">1次H</option>");
			out.println("<option value=\"H\">H</option>");
			out.println("<option value=\"SF\">SF</option>");
			out.println("<option value=\"F\">F</option>");
			out.println("</select>");
			out.println("<br/>");
			out.println("組： ");
			out.println("<input type=\"text\" name=\"add_set\" />※Fの場合は空欄でOK");
			out.println("<br/>");
			out.println("<input type=\"submit\" value=\"追加\"/>");
			out.println("</form>");

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
