// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

/** Servlet that handles comments.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = request.getParameter("comment-text");
        String author = request.getParameter("author");
        long timestamp = System.currentTimeMillis();

        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("text", comment);
        commentEntity.setProperty("author", author);
        commentEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Query query = new Query("Comment"); //.addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        String jsonComments = createJSON(results);

        response.setContentType("text/html;");
        response.getWriter().println(jsonComments);
    }

    /** Creates formatted JSON strings from Comment Entity query results. */
    private String createJSON(PreparedQuery results) {
        List<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            comments.add(new Comment.Builder()
                .withText((String) entity.getProperty("text"))
                .withAuthor((String) entity.getProperty("author"))
                .build()
            );

        }
        String jsonComments = new Gson().toJson(comments);
        return jsonComments;
    }
}

/** Class for simple blog comments.*/
class Comment {

    public static class Builder {
        private String text;
        private String author;

        public Builder() {
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public Comment build() {
            Comment comment = new Comment();
            comment.text = this.text;
            comment.author = this.author;

            return comment;
        }
    }

    private String text;
    private String author;

    private Comment() {
    }

    @Override
    public String toString() {
        String result = "";
        result += text + " " + author;
        return result;
    }
}
