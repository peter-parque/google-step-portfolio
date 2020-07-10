package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.gson.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles custom map markers.*/
@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String lat = request.getParameter("lat");
        String lng = request.getParameter("lng");
        String title = request.getParameter("title");
        System.out.println("here's ya boi " + lng);

        Entity markerEntity = new Entity("Marker");
        markerEntity.setProperty("lat", lat);
        markerEntity.setProperty("lng", lng);
        markerEntity.setProperty("title", title);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(markerEntity);

        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Query query = new Query("Marker");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        String jsonMarkers = createJSON(results);

        response.setContentType("text/html;");
        response.getWriter().println(jsonMarkers);
    }

    private String createJSON(PreparedQuery results) {
        List<Marker> markers = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            markers.add(new Marker(
                (String) entity.getProperty("lat"),
                (String) entity.getProperty("lng"),
                (String) entity.getProperty("title")
            ));
        }
        String jsonMarkers = new Gson().toJson(markers);
        return jsonMarkers;
    }
}

/** Class for Google Map markers. */
class Marker {

    private final String lat;
    private final String lng;
    private final String title;

    public Marker(String lat, String lng, String title) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
    }
}
