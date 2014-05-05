package autocomplete;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

@WebServlet(name = "Searcher", urlPatterns = {"/Searcher"})
public class Searcher extends HttpServlet {

    private final SolrServer solrServer;

    public Searcher() {
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8983/solr");
        httpSolrServer.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
        httpSolrServer.setConnectionTimeout(5000); // 5 seconds to establish TCP
        // Setting the XML response parser is only required for cross
        // version compatibility and only when one side is 1.4.1 or
        // earlier and the other side is 3.1 or later.
        httpSolrServer.setParser(new XMLResponseParser()); // binary parser is used by default
        // The following settings are provided here for completeness.
        // They will not normally be required, and should only be used 
        // after consulting javadocs to know whether they are truly required.
        httpSolrServer.setSoTimeout(1000);  // socket read timeout
        httpSolrServer.setDefaultMaxConnectionsPerHost(100);
        httpSolrServer.setMaxTotalConnections(100);
        httpSolrServer.setFollowRedirects(false);  // defaults to false
        // allowCompression defaults to false.
        // Server side must support gzip or deflate for this to have any effect.
        httpSolrServer.setAllowCompression(true);
        this.solrServer = httpSolrServer;
    }

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

                SolrQuery query = new SolrQuery();
                QueryResponse rsp;
                try {
                    query.setQuery(request.getParameter("q"));
                    rsp = solrServer.query(query);
                    SolrDocumentList docs = rsp.getResults();
                    Iterator<SolrDocument> iter = docs.iterator();
                    boolean first = true;
                    while (iter.hasNext()) {
                        SolrDocument resultDoc = iter.next();
                        if (first) {
                            first = false;
                        } else {
                            out.print(",");
                        }
                        String id = (String) resultDoc.getFieldValue("id");
                        out.print("{\"id\":\"" + escapeString(id) + "\",");
                        String name = (String) resultDoc.getFieldValue("name");
                        out.print("\"name\":\"" + escapeString(name) + "\"}");
                    }
                } catch (SolrServerException ex) {
                    Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
                } catch( Exception e){
                    Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            out.println("]");
        }
    }
    
    private String escapeString(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\"' | c == '\\') {
                sb.append("\\").append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
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
