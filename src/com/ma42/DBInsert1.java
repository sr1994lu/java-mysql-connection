package com.ma42;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DBInsert1", urlPatterns = {"/DBInsert1"})
public class DBInsert1 extends HttpServlet {

  protected void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {

  }

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    List<String> statusComment = new ArrayList<>();
    String OptNullabblePasswd = hasAtai(request, "passwd").orElse("");
    Connection con = null;
    PreparedStatement pst = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      if (!hasAtai(request, "passwd").isPresent()) {
        // do not have textbox
        statusComment.add("登録したいPASSWDのテキストボックスが存在しません");
        throw new NullPointerException();

      } else if (Objects.equals(OptNullabblePasswd, "")) {
        // not yet entered
        statusComment.add("登録したいPASSWDのテキストボックスが未入力です");
        throw new Exception();

      }
      // entered
      Class.forName("com.mysql.cj.jdbc.Driver");
      statusComment.add("ドライバのロードに成功しました");
      con = DriverManager
          .getConnection(
              "jdbc:mysql://localhost:3306/ma4299db01",
              "mysqluser",
              "mysqlpassword");
      statusComment.add("データベース接続に成功しました<hr>");
      st = con.createStatement();
      rs = st.executeQuery("SELECT LAST_INSERT_ID(userno + 1) AS last_id FROM t00_testtable");
      if (!rs.next()) {
        pst = con.prepareStatement("INSERT INTO t00_testtable(userno , passwd) VALUES(?,?)");
        pst.setInt(1, 1);
        pst.setString(2, OptNullabblePasswd);
        pst.executeUpdate();
      } else {
        String lastId = rs.getString("last_id");
        pst = con.prepareStatement("INSERT INTO t00_testtable(userno , passwd) VALUES(?,?)");
        pst.setInt(1, Integer.parseInt(lastId));
        pst.setString(2, OptNullabblePasswd);
        pst.executeUpdate();
      }
    } catch (ClassNotFoundException e) {
      statusComment.add("ClassNotFoundException: " + e.getMessage());
      return;
    } catch (SQLException e) {
      statusComment.add("SQLException: " + e.getMessage());
      return;
    } catch (Exception e) {
      statusComment.add("Exception: " + e.getMessage());
      return;
    } finally {
      try {
        if (rs != null) {
          rs.close();
          statusComment.add("ResultSet closed");
        } else {
          statusComment.add("Can not closed ResultSet");
        }
        if (st != null) {
          st.close();
          statusComment.add("Statement closed");
        } else {
          statusComment.add("Can not closed Statement");
        }
        if (pst != null) {
          pst.close();
          statusComment.add("PrepareStatement closed");
        } else {
          statusComment.add("Can not closed PrepareStatement");
        }
        if (con != null) {
          con.close();
          statusComment.add("Connection closed");
        } else {
          statusComment.add("Can not closed Connection");
        }
      } catch (SQLException e) {
        statusComment.add("SQLException: " + e.getMessage());
      }
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<html><head><meta charset=\"UTF-8\">");
      out.println("<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, "
          + "initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">");
      out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">");
      out.println("<title>DBInsert1</title></head>");
      out.println("<body>" + String.join("<br>", statusComment) + "</body></html>");
      out.close();
    }
  }

  private Optional<String> hasAtai(HttpServletRequest request, String param) {
    return Optional.ofNullable(request.getParameter(param));
  }
}
