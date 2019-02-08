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
public class OpCombiDoServlet extends HttpServlet {

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
    ResultSet rs = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

      //全部削除
      stmt.executeUpdate("DELETE FROM participates" );

      //大学リストを取得
      String[] univName = new String[64];
      int univCount = 0;
      rs = stmt.executeQuery("SELECT univ_name FROM reads order by convert_to(univ_read, 'UTF8')");
			while (rs.next()) {
				univName[univCount] = rs.getString("univ_name");
        univCount++;
			}
			rs.close();


      //レースリスト
      String[] raceName = { "K-1-1000m", "C-1-1000m", "WK-1-500m", "WC-1-500m",
                            "JK-1-500m", "JC-1-500m", "JWK-1-500m", "JWC-1-500m",
                            "K-2-1000m", "C-2-1000m", "WK-2-500m", "WC-2-500m",
                            "JK-2-500m", "JC-2-500m", "JWK-2-500m", "JWC-2-500m"};

      //各大学のエントリーリストを収納
      int[][][] bibs = new int[64][64][1024];
      int[][] entCounts = new int[64][64];
      for( int i = 0; i < raceName.length; i++ ){
        for( int j = 0; j < univCount; j++ ){
          rs = stmt.executeQuery("select * from entries natural left join bibs"
          + " where race_name = '" +raceName[i]+ "' and univ_name = '" +univName[j]+ "'");
          for( int k = 0; rs.next(); k++ ){
            bibs[i][j][k] = rs.getInt("bib_no");
            entCounts[i][j]++;
          }
          rs.close();
        }
      }


      //大学内でシャッフルする
      for( int i = 0; i < raceName.length; i++ ){
        for( int j = 0; j < univCount; j++ ){
          //シャッフル実行
          for( int k = 0; k < entCounts[i][j]; k++ ){
            int rnd = (int)( Math.random() * (double)entCounts[i][j] );
            int c = bibs[i][j][k];
            bibs[i][j][k] = bibs[i][j][rnd];
            bibs[i][j][rnd] = c;
          }
        }
      }

      //第１レースの組数を計算
      int count = 0;
      int[] setCount = new int[raceName.length];
      for( int i = 0; i < raceName.length ; i++ ){
			  rs = stmt.executeQuery("select count(*) from entries where race_name='" +raceName[i]+ "'");
        rs.next();
			  count = rs.getInt("count");
        //ペアの場合は半分
        if( raceName[i].matches(".*-2-.*")){ count = count / 2;}
        rs.close();
        setCount[i] = (count + 8) / 9;
      }

      //組に割り振る
      int set = 0;
      int rane = 0;
      int raceNo = 0;

      for( int i = 0; i < raceName.length; i++ ){
        set = 1;
        rane = 1;
        for( int j = 0; j < univCount; j++ ){
          for( int k = 0; k < entCounts[i][j]; k++ ){
          if( bibs[i][j][k] != 0 ){
            //1次Hからある時
            if( setCount[i] >= 9 && setCount[i] <= 12 ){
              //レースナンバーを取得
              rs = stmt.executeQuery("select * from races where race_name = '"
               +raceName[i]+ "' and stage = '1次H' and set = " +set );
              rs.next();
              raceNo = rs.getInt("race_no");
              rs.close();
              //レースに参加
              stmt.executeUpdate("INSERT INTO participates VALUES(" +raceNo+
              ", " +rane+ ", " +bibs[i][j][k]+ " )");
            }
            //Hからある時
            else if( setCount[i] >= 2 && setCount[i] <= 8 ){
              //レースナンバーを取得
              rs = stmt.executeQuery("select * from races where race_name = '"
              +raceName[i]+ "' and stage = 'H' and set = " +set );
              rs.next();
              raceNo = rs.getInt("race_no");
              rs.close();
              //レースに参加
              stmt.executeUpdate("INSERT INTO participates VALUES(" +raceNo+
              ", " +rane+ ", " +bibs[i][j][k]+ " )");
            }
            else if( setCount[i] == 1 ){
              //レースナンバーを取得
              rs = stmt.executeQuery("select * from races where race_name = '"
              +raceName[i]+ "' and stage = 'F'");
              rs.next();
              raceNo = rs.getInt("race_no");
              rs.close();
              //レースに参加
              stmt.executeUpdate("INSERT INTO participates VALUES(" +raceNo+
              ", " +rane+ ", " +bibs[i][j][k]+ " )");
            }
            else { /* donothing */ }

            if( set == setCount[i] ){
              set = 1;
              rane++;
            }
            else{ set++; }
          }
          }
        }
      }

      //レーンごとにシャッフル
      int[] sfBibs = new int[18];
      String[] sfUniv = new String[18];
      int sfCount;
      rs = stmt.executeQuery("SELECT MAX(race_no) AS max_no FROM races" );
      rs.next();
      int max_no = rs.getInt("max_no");
      rs.close();
      rs = stmt.executeQuery("SELECT MIN(race_no) AS min_no FROM races" );
      rs.next();
      int min_no = rs.getInt("min_no");
      rs.close();
      boolean brk = false;

      //レースごとに実行
      for( int i = min_no; i <= max_no; i++ ){
        for( int j = 0; j < 9; j++ ){
          sfBibs[j] = 0;
          sfUniv[j] = null;
        }
        sfCount = 0;
        rs = stmt.executeQuery("SELECT * from participates natural left join"
         + " (bibs natural left join players) where race_no = " +i );
        for( int j = 0; rs.next(); j++ ){
          sfBibs[j] = rs.getInt("bib_no");
          sfUniv[j] = rs.getString("univ_name");
          sfCount++;
        }
        if( sfBibs[0] != 0 && sfCount != 1){
        //削除
        stmt.executeUpdate("DELETE FROM participates where race_no = " +i );
        //ステージ判定(未実装)

        //シャッフル実行(100回まで)
        for( int j = 0; j < 100; j++ ){
          //シャッフル
          for( int k = 0; k < sfCount; k++ ){
            int rnd = (int)( Math.random() * (double)sfCount );
            int c = sfBibs[k];
            String s = sfUniv[k];
            sfBibs[k] = sfBibs[rnd];
            sfUniv[k] = sfUniv[rnd];
            sfBibs[rnd] = c;
            sfUniv[rnd] = s;
          }
          //大学確認
          for( int k = 0; k < sfCount-1; k++ ){
            if( sfUniv[k].equals(sfUniv[k+1]) ) {
              brk = false;
              break;
            }
            else{
              brk = true;
            }
          }
          if( brk ) break;
        }
        //挿入
        for( int k = 0; k < sfCount; k++ ){
          int n = k + 1;
          stmt.executeUpdate("INSERT INTO participates VALUES(" +i+ ", " +n+ ", " +sfBibs[k]+ ")");
        }
        }
      }


      out.println("抽選が完了しました。<br><br>");


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

    out.println("<a href=\"opcombi\">組み合わせ一覧に戻る</a><br><br>");
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
