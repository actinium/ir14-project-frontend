package autocomplete;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;

@WebServlet(name = "Suggester", urlPatterns = {"/Suggester"})
public class Suggester extends HttpServlet {

    SolrServer solr = new HttpSolrServer("http://localhost:8983/solr");
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("[");
            if (request.getParameter("q") != null && request.getParameter("q").length() > 0) {
                ModifiableSolrParams params = new ModifiableSolrParams();
                params.set("qt", "/suggest");
                params.set("q", request.getParameter("q"));
                try {
                    QueryResponse qr = solr.query(params);
                    NamedList splchc = (NamedList)qr.getResponse().get("spellcheck");
                    NamedList suggs = (NamedList)splchc.get("suggestions");
                    NamedList b = (NamedList)suggs.get(request.getParameter("q"));
                    if(b != null){
                        ArrayList sugg = (ArrayList)b.get("suggestion");
                        for(int i=0; i < sugg.size(); i++){
                            if(i>0){
                                out.print(",");
                            }
                            out.print("\"" + sugg.get(i).toString() + "\"");
                        }
                    }
                } catch (SolrServerException ex) {
                    Logger.getLogger(Suggester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            out.print("]");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}