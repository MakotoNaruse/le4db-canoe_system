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
public class OperationServlet extends HttpServlet {


	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<body>");

    out.println("<h1>管理用ページ</h1>");
		out.println("<a href=\"opread\">参加大学一覧・大学ふりがな</a><br><br>");
    out.println("<a href=\"opbibs\">選手名簿・ゼッケンナンバー割り振り</a><br><br>");
		out.println("<a href=\"oprace\">エントリー一覧・レース作成</a><br><br>");
    out.println("<a href=\"opcombi\">抽選実施・組み合わせ確認</a><br><br>");
    out.println("<a href=\"oprank\">結果順位入力</a><br><br>");
    out.println("<a href=\"optime\">結果タイム入力</a><br><br>");

		out.println("<br><a href=\"toppage\">トップページに戻る</a>");

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
