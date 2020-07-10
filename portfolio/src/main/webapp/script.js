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
 * Fetches comments from server and displayes them on the page.
 */
async function getComments() {
    const response = await fetch('/data');

    const text = await response.json();

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
        zoom: 3
    });
    getMarkers(map);

    map.addListener("click", function(e) {
        placeMarker(e.latLng, map);
    });
}

function placeMarker(latLng, map) {
    var confirmInput = prompt("Add your name to the marker and place?", "Name");
    if (confirmInput != null) {
        var marker = new google.maps.Marker({
            lat: latLng.lat(),
            lng: latLng.lng(),
            map: map,
            title: confirmInput
        });
        postMarker(marker);
    }
}

async function postMarker(marker) {
    var requestBody = new URLSearchParams();
    requestBody.append('lat', marker.lat);
    requestBody.append('lng', marker.lng);
    requestBody.append('title', marker.title);

    const request = new Request('/markers', {
        method: 'POST',
        body: requestBody
    });

    fetch(request).then(response => { window.location.href = response.url; });
}

async function getMarkers(map) {
    const response = await fetch('/markers');

    const markers = await response.json();

    for (markerJSON of markers) {
        var marker = new google.maps.Marker({
            position: new google.maps.LatLng(parseFloat(markerJSON.lat), parseFloat(markerJSON.lng)),
            map: map,
            title: markerJSON.title
        });
    }
}
