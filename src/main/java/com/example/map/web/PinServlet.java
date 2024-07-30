package com.example.map.web;

import com.example.map.model.Pin;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/interactive-map/pins")
public class PinServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/interactive_map";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    // wird aufgerufen wenn eine GET-Anfrage gesendet wird 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Pin> pins = fetchPinsFromDatabase();  
        // konvertiert die Pins als JSON String und sendet diese zurück
        // dient der Datenübertragung zwischen Server und Client 
        sendJsonResponse(response, pins); 
    }

    // wird aufgerufen, wenn eine POST-Anfrage gesendet wird 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper(); // zum Lesen von JSON
        // Lesen JSON aus dem Anfragetext und konviertiere in Pin-Objekt
        Pin newPin = mapper.readValue(request.getReader(), Pin.class);

        // Baut die Datenbankverbindung auf und führt ein Update der Pins aus
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO pins (lat, lng, status, person_name) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, newPin.getLat());
            statement.setDouble(2, newPin.getLng());
            statement.setString(3, newPin.getStatus());
            statement.setString(4, newPin.getPersonName());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                newPin.setId(id);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error"); 
            throw new ServletException(e);
        }

        List<Pin> pins = fetchPinsFromDatabase();
        sendJsonResponse(response, pins);
    }

    // Funktion holt die Pins aus der Datenbank 
    private List<Pin> fetchPinsFromDatabase() throws ServletException {
        List<Pin> pins = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM pins")) {
            while (resultSet.next()) {
                Pin pin = new Pin(resultSet.getInt("id"),
                        resultSet.getDouble("lat"),
                        resultSet.getDouble("lng"),
                        resultSet.getString("status"),
                        resultSet.getString("person_name"));
                pins.add(pin);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error"); 
            throw new ServletException(e);
        }
        return pins;
    }

    // sendet die Liste der Pins als JSON Antwort 
    private void sendJsonResponse(HttpServletResponse response, List<Pin> pins) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(pins);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
