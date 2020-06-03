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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/** Servlet that returns content and comments*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    ArrayList<String> words = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Task");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    //iterating over entities within Datastore
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String title = (String) entity.getProperty("user-comments");
    }

    response.setContentType("application/json;");

    String json = "";

    //get max number of comments
    int numComments = getNumComments(request);

    //convert request number of comments to json
    if  (words.size() <= numComments){
        json = new Gson().toJson(words);
    }else{
        for (int i = 0; i < numComments; i++){
            json += new Gson().toJson(words.get(i));
        }
    }

    response.getWriter().println(json);
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String userComment = request.getParameter("user-comments");

    //add user comment to ArrayList
    words.add(userComment);

    //entity with comment property to store user comments
    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("user-comments", userComment);

    //storing using Datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

/** Returns the choice entered by the player, or -1 if the choice was invalid. */
  private int getNumComments(HttpServletRequest request) {
    // Get the input from the form.
    String numCommentsStr = request.getParameter("num-comments");

    // Convert the input to an int.
    int numComments;
    try {
      numComments = Integer.parseInt(numCommentsStr);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numCommentsStr);
      return -1;
    }

    return numComments;
  }

}
