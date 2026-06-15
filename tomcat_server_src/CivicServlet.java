package com.example.civic;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Java compatibile con Tomcat per gestire le segnalazioni urbane.
 * Definisce un endpoint GET per ritirare i dati ed un endpoint POST per inviarne di nuovi.
 */
@WebServlet("/reports")
public class CivicServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReportDao reportDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        // Inizializzazione della connessione ed utilizzo del Pattern DAO (valutato all'esame)
        this.reportDao = new ReportDao();
        this.gson = new Gson();
    }

    /**
     * GET: Restituisce l'elenco delle segnalazioni in formato JSON
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<ReportModel> reports = reportDao.getAllReports();
            String jsonOutput = this.gson.toJson(reports);
            out.print(jsonOutput);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Errore nel recupero dei dati dal Database: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }

    /**
     * POST: Riceve una singola segnalazione in JSON e la salva persistendola sui record
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // 1. Legge il corpo JSON della richiesta
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        try {
            // 2. Deserializza l'oggetto da JSON tramite Gson
            ReportModel incomingReport = this.gson.fromJson(sb.toString(), ReportModel.class);
            
            if (incomingReport == null || incomingReport.getTitle() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Dati in entrata non validi o titolo mancante\"}");
                return;
            }

            // Imposta timestamp lato server se mancante
            if (incomingReport.getTimestamp() <= 0) {
                incomingReport.setTimestamp(System.currentTimeMillis());
            }

            // 3. Salva l'oggetto nel database primario tramite DAO
            int generatedId = reportDao.insertReport(incomingReport);
            incomingReport.setId(generatedId);

            // 4. Risponde con l'oggetto salvato e ID univoco assegnato
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(this.gson.toJson(incomingReport));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Errore nel salvataggio lato server: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}
