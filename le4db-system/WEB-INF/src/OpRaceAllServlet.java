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
public class OpRaceAllServlet extends HttpServlet {

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

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      //全部削除
      stmt.executeUpdate("DELETE FROM races" );

      ResultSet rs = null;

      int count = 0;
      int set = 0;
      int raceNo = 0;
      String[] raceName = { "K-1-1000m", "C-1-1000m", "WK-1-500m", "WC-1-500m",
                            "JK-1-500m", "JC-1-500m", "JWK-1-500m", "JWC-1-500m",
                            "K-2-1000m", "C-2-1000m", "WK-2-500m", "WC-2-500m",
                            "JK-2-500m", "JC-2-500m", "JWK-2-500m", "JWC-2-500m"};
      for( int i = 0; i < raceName.length ; i++ ){
        raceNo--;
			  rs = stmt.executeQuery("select count(*) from entries where race_name='" +raceName[i]+ "'");
        rs.next();
			  count = rs.getInt("count");
        //ペアの場合は半分
        if( raceName[i].matches(".*-2-.*")){ count = count / 2;}
        rs.close();
        //10~12組　H6 SF3 F1
        if( count >= 82 && count <= 108 ){
          set = (count + 8) / 9;
          stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
    			 "', 'F', null )");
          for( int j = 3; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'SF', " +j+ " )");
          }
          for( int j = 6; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'H', " +j+ " )");
          }
          for( int j = set; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', '1次H', " +j+ " )");
          }
        }
        //9組 H5 SF3 F1
        if( count >= 72 && count <= 81 ){
          stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
    			"', 'F', null )");
          for( int j = 3; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'SF', " +j+ " )");
          }
          for( int j = 5; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'H', " +j+ " )");
          }
          for( int j = 9; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', '1次H', " +j+ " )");
          }
        }
        //4~8組 SF3 F1
        else if( count >= 28 && count <= 72){
          set = (count + 8) / 9;
          stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
    			"', 'F', null )");
          for( int j = 3; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'SF', " +j+ " )");
          }
          for( int j = set; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'H', " +j+ " )");
          }
        }
        //3組　SF2
        else if( count >= 19 && count <= 27 ){
          stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
    			"', 'F', null )");
          for( int j = 2; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'SF', " +j+ " )");
          }
          for( int j = 3; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
      			 "', 'H', " +j+ " )");
          }
        }
        //2組
        else if( count >= 10 && count <= 18 ){
          stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
    			 "', 'F', null )");
          for( int j = 2; j >= 1; j-- ){
            raceNo--;
            stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
     			  "', 'H', " +j+ " )");
          }
        }
        //1組
        else if( count >= 1 && count <= 9 ){
          stmt.executeUpdate("INSERT INTO races VALUES(" +raceNo+ ", null, '" +raceName[i]+
    			 "', 'F', null )");
        }
        else {
          //do nothing
        }
      }

			out.println("自動作成しました。<br><br>");

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
