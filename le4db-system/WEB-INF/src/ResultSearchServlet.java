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
public class ResultSearchServlet extends HttpServlet {

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

		//String search_rno = request.getParameter("univ_name");
    String search_stage = request.getParameter("search_stage");
    String search_set = request.getParameter("search_set");
    String search_race = request.getParameter("search_race");
    //String search_race = request.getParameter("univ_name");
    //String search_race = request.getParameter("univ_name");

		out.println("<html>");
		/*out.println("<head>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"http://localhost:8080/base.css\" >");
		out.println("</head>"); */
		out.println("<body>");

    out.println("<h3>スタートリスト・記録速報</h3>");

		out.println("<form action=\"resultsearch\" method=\"GET\">");
		out.println("レース名： ");
    //プルダウンメニュー
    String[] raceName = { "K-1-1000m", "C-1-1000m", "WK-1-500m", "WC-1-500m",
                          "JK-1-500m", "JC-1-500m", "JWK-1-500m", "JWC-1-500m",
                          "K-2-1000m", "C-2-1000m", "WK-2-500m", "WC-2-500m",
                          "JK-2-500m", "JC-2-500m", "JWK-2-500m", "JWC-2-500m"};
		out.println("<select name=\"search_race\" size=\"1\">");
    if( search_race.equals("%")) out.println("<option value=\"%\" selected>すべて</option>");
    else out.println("<option value=\"%\" >すべて</option>");
    for( int i = 0; i < raceName.length; i++ ){
      if( raceName[i].equals(search_race) ){
        out.println("<option value=\"" +raceName[i]+ "\" selected>" +raceName[i]+ "</option>");
      }
      else{
        out.println("<option value=\"" +raceName[i]+ "\">" +raceName[i]+ "</option>");
      }
    }
		out.println("</select>　");

    out.println("ステージ： ");
    //プルダウンメニュー
    String[] stageName = { "1次H", "H", "SF", "F" };
    out.println("<select name=\"search_stage\" size=\"1\">");
    if( search_stage.equals("%")) out.println("<option value=\"%\" selected>すべて</option>");
    else out.println("<option value=\"%\" >すべて</option>");
    for( int i = 0; i < stageName.length; i++ ){
      if( stageName[i].equals(search_stage) ){
        out.println("<option value=\"" +stageName[i]+ "\" selected>" +stageName[i]+ "</option>");
      }
      else{
        out.println("<option value=\"" +stageName[i]+ "\">" +stageName[i]+ "</option>");
      }
    }
		out.println("</select>　");
		out.println("組： ");
    out.println("<select name=\"search_set\" size=\"1\">");
    if( search_set.equals("%")) out.println("<option value=\"%\" selected>すべて</option>");
    else out.println("<option value=\"%\" >すべて</option>");
    for( int i = 1; i < 12; i++ ){
      if( search_set.equals(Integer.toString(i)) ){
        out.println("<option value=\"" +i+ "\" selected>" +i+ "</option>");
      }
      else{
        out.println("<option value=\"" +i+ "\">" +i+ "</option>");
      }
    }
    out.println("</select>　");
		out.println("<input type=\"submit\" value=\"検索\"/>");
		out.println("</form>");
    out.println("※決勝は組「すべて」を選択してください。");

    out.println("<h3>検索結果</h3>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			boolean prime = true;

			ResultSet rs = null;
			if( search_set.equals("%") ){
				rs = stmt
						.executeQuery("select * from ((races natural left join (participates "
	          + "natural left join (bibs natural left join players)) natural left join "
	          + "ranks) natural left join results) where race_name like '" +search_race+
	            "' and stage like '" +search_stage+ "' order by race_no, rane_no");
			}
			else{
				rs = stmt
					.executeQuery("select * from ((races natural left join (participates "
          + "natural left join (bibs natural left join players)) natural left join "
          + "ranks) natural left join results) where race_name like '" +search_race+
            "' and stage like '" +search_stage+ "' and cast(set as text) like '" +search_set+
            "' order by race_no, rane_no");
			}
			while (rs.next()) {
				int race_no = rs.getInt("race_no");
        String race_name = rs.getString("race_name");
        String stage = rs.getString("stage");
        int set = rs.getInt("set");
				int rane_no = rs.getInt("rane_no");
        int bib_no = rs.getInt("bib_no");
				String p_name = rs.getString("p_name");
        String univ_name = rs.getString("univ_name");
        int rank = rs.getInt("rank");
				String time = rs.getString("time");

				if( prime ){
					if( rane_no == 0 ){
						out.println("レースが存在しません。<br>");
						prime = false;
						break;
					}
					else{
						out.println("<table border=\"1\" ");
						out.println("<tr><th>レースNo<th>レース名<th>ステージ<th>　組　<th>レーン<th>ゼッケン<th>名前<th>大学<th>順位<th>タイム</tr>");
						prime = false;
					}
				}
				if( rane_no != 0){
					out.println("<tr>");
					out.println("<td>" + race_no + "</td>");
					out.println("<td>" + race_name + "</td>");
					out.println("<td>" + stage + "</td>");
					if( set == 0 ){
						out.println("<td><br></td>");
					}
					else{
	        	out.println("<td>" + set + "</td>");
					}
					out.println("<td>" + rane_no + "</td>");
	        out.println("<td>" + bib_no + "</td>");
					out.println("<td>" + p_name + "</td>");
	        out.println("<td>" + univ_name + "</td>");
	        if( rank == 0 ) out.println("<td><br></td>");
					else out.println("<td>" + rank + "</td>");
	        if( time == null ) out.println("<td><br></td>");
	        else out.println("<td>" + time + "</td>");
					out.println("</tr>");
				}
			}
			rs.close();

			if( prime == true ){
				out.println("レースが存在しません。<br>");
			}
			else{
				out.println("</table>");
			}



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
