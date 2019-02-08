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
public class ResultServlet extends HttpServlet {

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

		out.println("<h3>スタートリスト・記録速報</h3>");

		out.println("<form action=\"resultsearch\" method=\"GET\">");
		out.println("レース名： ");
    //プルダウンメニュー
		out.println("<select name=\"search_race\" size=\"1\">");
    out.println("<option value=\"%\" selected>すべて</option>");
		out.println("<option value=\"K-1-1000m\">K-1-1000m</option>");
		out.println("<option value=\"K-2-1000m\">K-2-1000m</option>");
		out.println("<option value=\"C-1-1000m\">C-1-1000m</option>");
		out.println("<option value=\"C-2-1000m\">C-2-1000m</option>");
    out.println("<option value=\"WK-1-500m\">WK-1-500m</option>");
		out.println("<option value=\"WK-2-500m\">WK-2-500m</option>");
    out.println("<option value=\"WC-1-500m\">WC-1-500m</option>");
		out.println("<option value=\"WC-2-500m\">WC-2-500m</option>");
    out.println("<option value=\"JK-1-500m\">JK-1-500m</option>");
		out.println("<option value=\"JK-2-500m\">JK-2-500m</option>");
    out.println("<option value=\"JC-1-500m\">JC-1-500m</option>");
		out.println("<option value=\"JC-2-500m\">JC-2-500m</option>");
    out.println("<option value=\"JWK-1-500m\">JWK-1-500m</option>");
		out.println("<option value=\"JWK-2-500m\">JWK-2-500m</option>");
    out.println("<option value=\"JWC-1-500m\">JWC-1-500m</option>");
		out.println("<option value=\"JWC-2-500m\">JWC-2-500m</option>");
		out.println("</select>　");
    out.println("ステージ： ");
    //プルダウンメニュー
    out.println("<select name=\"search_stage\" size=\"1\">");
    out.println("<option value=\"%\" selected>すべて</option>");
    out.println("<option value=\"1次H\">1次H</option>");
    out.println("<option value=\"H\">H</option>");
    out.println("<option value=\"SF\">SF</option>");
    out.println("<option value=\"F\">F</option>");
    out.println("</select>　");
		out.println("組： ");
    out.println("<select name=\"search_set\" size=\"1\">");
    out.println("<option value=\"%\" selected>すべて</option>");
    for( int i = 1; i < 12; i++ ){
      out.println("<option value=\"" +i+ "\">" +i+ "</option>");
    }
    out.println("</select>　");
		out.println("<input type=\"submit\" value=\"検索\"/>");
		out.println("</form>");
    out.println("※決勝は組「すべて」を選択してください。");



		out.println("<br/><br>");
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
