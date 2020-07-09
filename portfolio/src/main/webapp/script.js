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
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Fetches comments from server and displayes them on the page.
 */
async function getComments() {
    const response = await fetch('/data');

    const text = await response.json();
    console.log(text);

    const commentMarkup = `${text.map(comment =>
    `<div class="comment">
        <div class="comment-text">
            ${comment.text}
        </div>
        <div class="comment-author">
            ${comment.author}
        </div>
    </div>`).join('')}`;

    document.getElementById('comment-container').innerHTML = commentMarkup;
}

/**
 * Initializes a Google Map centered on the US.
 */
function initMap() {
    const map = new google.maps.Map(document.getElementById('map'), {
    center: { lat: 39.944, lng: -97.259 },
    zoom: 2
  });

  map.addListener("click", function(e) {
      placeMarker(e.latLng, map);
  });
}

function placeMarker(latLng, map) {
    var confirmInput = confirm("Place a marker here?");
    if (confirmInput) {
        var marker = new google.maps.Marker({
            position: latLng,
            map: map
        });
    }
}
