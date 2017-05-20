package com.ma42;

import com.sun.tools.javac.jvm.ClassFile.Version;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DBSelect1", urlPatterns = {"/DBSelect1"})
public class DBSelect1 extends HttpServlet {

  protected void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {

  }

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    List<String> statusComment = new ArrayList<>();
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      statusComment.add("ドライバのロードに成功しました");
      con = DriverManager
          .getConnection(
              "jdbc:mysql://localhost:3306/ma4299db01",
              "mysqluser",
              "mysqlpassword");
      statusComment.add("データベース接続に成功しました<hr>");
      st = con.createStatement();
      String selectSql = "SELECT * FROM t00_testtable";
      rs = st.executeQuery(selectSql);
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
      out.println("<title>DBSelect1</title></head>");
      out.println("<body>" + String.join("<br>", statusComment) + "</body></html>");
      out.close();
    }
  }
}

