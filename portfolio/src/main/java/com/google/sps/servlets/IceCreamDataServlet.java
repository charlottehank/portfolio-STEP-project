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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


@WebServlet("/icecream-data")
public class IceCreamDataServlet extends HttpServlet {

  private Map<String, Long> iceCreamVotes = new HashMap<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Query for ice cream flavor storage through datastore.
    Query query = new Query("Flavors");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    //Populate flavors with ice cream titles from Datastore.
    for (Entity entity : results.asIterable()) {
      String title = (String) entity.getProperty("flavor");
      long votes = (long) entity.getProperty("votes");
      iceCreamVotes.put(title, votes);
    }

    System.out.println(iceCreamVotes.size());
    
   response.setContentType("application/json");
   Gson gson = new Gson();
   String json = gson.toJson(iceCreamVotes);
   response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String flavor = request.getParameter("flavor");
    long currentVotes = 0;
    
    //Single query to get ice cream votes within datastore.
    Query query = new Query("Flavors").setFilter(new FilterPredicate("flavor", FilterOperator.EQUAL, flavor));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    Entity result = results.asSingleEntity();

    if (result!=null){
       //Add another vote to an existing flavor.
       currentVotes = (long)result.getProperty("votes") + 1;
    }else{
        //The flavor is not already in the datastore. 
        currentVotes = 1;
    }

    //Create entity with flavor and votes properties to store favorite ice cream flavors.
    Entity flavorEntity = new Entity("Flavors", flavor);
    flavorEntity.setProperty("flavor", flavor); 
    flavorEntity.setProperty("votes", currentVotes); 
       
    //Add votes and flavor type to the Datastore for longterm storage.
    datastore.put(flavorEntity);

    //Send the user back to the index page after adding their favorite flavor.
    response.sendRedirect("/index.html");
  }
}
