package com.example;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "hello", urlPatterns = "/hello/hello1.html")
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("HelloServlet.doGet");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        //HelloServlet 출력
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>HelloServlet 호출</h1>");
        out.println("</body></html>");
    }
}