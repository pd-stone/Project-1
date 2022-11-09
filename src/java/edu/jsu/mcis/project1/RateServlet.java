package edu.jsu.mcis.project1;

import edu.jsu.mcis.project1.dao.DAOFactory;
import edu.jsu.mcis.project1.dao.RateDAO;
import edu.jsu.mcis.project1.dao.UserDAO;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


public class RateServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");out.println("<html>");out.println("<head>");
            out.println("<title>Servlet RateServlet</title>");out.println("</head>");out.println("<body>");
            out.println("<h1>Servlet RateServlet at " + request.getContextPath() + "</h1>"); out.println("</body>");
            out.println("</html>");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        DAOFactory daoFactory = null;

        ServletContext context = request.getServletContext();

        if (context.getAttribute("daoFactory") == null) {
            System.err.println("*** Creating new DAOFactory ...");
            daoFactory = new DAOFactory();
            context.setAttribute("daoFactory", daoFactory);
        }
        else {
            daoFactory = (DAOFactory) context.getAttribute("daoFactory");
        }
        
        response.setContentType("application/json; charset=UTF-8");
        
        try ( PrintWriter out = response.getWriter()) {
            
            String uri = request.getRequestURI();
            String[] path = uri.split("/");
        
            String date = request.getParameter("date");         
            String currency = request.getParameter("currency");
            String key = request.getParameter("key");
            
            if (path.length >= 4) { date = path[3]; }
            if (path.length >= 5) { currency = path[4]; }
                                    
            RateDAO dao = daoFactory.getRateDAO();
            UserDAO userDAO = daoFactory.getUserDAO();
            
            boolean noCurrency = currency == null || "".equals(currency);
            boolean noDate = date == null || "".equals(date);
            boolean noKey = key == null || "".equals(key);
            
            
            if (!noKey) {
                int access_count = userDAO.getAccessCount(key);
                if (access_count >= 10 || access_count == -1) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.println("{\"message\": \"You have exceeded the maximum number of requests per day.\",\"success\": false}");                 
                }
 
                else {
                    if (noCurrency && noDate) {out.println(dao.list());}
                    else if (noCurrency && !noDate) {out.println(dao.list(date));}
                    else {out.println(dao.list(currency, date));}
                }
            }
            else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"message\": \"This API requires a valid access key.\", \"success\": false}");
            }
            
            
        }
        catch (Exception e) {e.printStackTrace();}
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
