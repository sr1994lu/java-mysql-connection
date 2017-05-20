package com.ma42;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DBDelete1", urlPatterns = {"/DBDelete1"})
public class DBDelete1 extends HttpServlet {

  protected void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {

  }

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    List<String> statusComment = new ArrayList<>();
    String OptNullabbleUserId = hasAtai(request, "userid").orElse("");
    String OptNullabblePasswd = hasAtai(request, "passwd").orElse("");
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      if (!hasAtai(request, "userid").isPresent()
          && !hasAtai(request, "passwd").isPresent()) {
        // do not have textboxes
        statusComment.add("変更したいusernoのテキストボックスが存在しません");
        statusComment.add("変更したいpasswdのテキストボックスが存在しません");
        throw new NullPointerException();

      } else if (!hasAtai(request, "userid").isPresent()) {
        // do not have textbox
        statusComment.add("変更したいusernoのテキストボックスが存在しません");
        throw new NullPointerException();

      } else if (!hasAtai(request, "passwd").isPresent()) {
        // do not have textbox
        statusComment.add("変更したいpasswdのテキストボックスが存在しません");
        throw new NullPointerException();

      } else {
        // have textbox
        if (Objects.equals(OptNullabbleUserId, "")
            && Objects.equals(OptNullabblePasswd, "")) {
          // not yet entered
          statusComment.add("変更したいusernoのテキストボックスが未入力です");
          statusComment.add("変更したいpasswdのテキストボックスが未入力です");
          throw new Exception();

        } else if (Objects.equals(OptNullabbleUserId, "")) {
          // not yet entered
          statusComment.add("変更したいusernoのテキストボックスが未入力です");
          throw new Exception();

        } else if (Objects.equals(OptNullabblePasswd, "")) {
          // not yet entered
          statusComment.add("変更したいpasswdのテキストボックスが未入力です");
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
        pst = con.prepareStatement(
            "SELECT userno , passwd FROM t00_testtable WHERE userno = ? AND passwd = ?");
        pst.setInt(1, Integer.parseInt(OptNullabbleUserId));
        pst.setString(2, OptNullabblePasswd);
        rs = pst.executeQuery();
        if (rs.next()) {
          pst = con.prepareStatement("DELETE FROM t00_testtable WHERE userno = ?");
          pst.setInt(1, Integer.parseInt(OptNullabbleUserId));
          pst.executeUpdate();
        } else {
          statusComment.add("入力データに誤りがあります");
        }
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
      out.println("<title>DBDelete1</title></head>");
      out.println("<body>" + String.join("<br>", statusComment) + "</body></html>");
      out.close();
    }
  }

  private Optional<String> hasAtai(HttpServletRequest request, String param) {
    return Optional.ofNullable(request.getParameter(param));
  }
}
