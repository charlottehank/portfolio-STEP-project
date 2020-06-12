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

/**
 * Fetch and add comments to the page.
 */
function getMaxNumberOfComments() {
  fetch('/data').then(response=>response.json()).then((data)=> {
      let numComments = document.getElementById('num-comments').value;
      const comments = document.getElementById('data-container');
      comments.innerHTML = '';
      
      // Display the maximum number of comments requested by the user.
      for (let i=0; i<numComments && i<data.length; i++) {
          // Format i comments in a list and display to the webpage.
          if (data[i] != "") {
              comments.appendChild(createListElement(data[i]));
            }
        }
    });
}

/** Create an <li> element containing text.*/
function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
}

/** Fetches comments from the server and deletes them.*/
function deleteComments() {
    fetch('/delete-data', {method: 'POST'}).then((response)=>{
         this.getMaxNumberOfComments();
    });
}

/** Creates a chart and adds it to the page. */
function drawChart() {
  fetch('/icecream-data').then(response => response.json())
  .then((iceCreamVotes) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Flavor');
    data.addColumn('number', 'Votes');
    Object.keys(iceCreamVotes).forEach((flavor) => {
      data.addRow([flavor, iceCreamVotes[flavor]]);
    });

    const options = {
      'title': 'Favorite Ice Cream Flavors',
      'width':500,
      'height':400,
    };

    const chart = new google.visualization.PieChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}