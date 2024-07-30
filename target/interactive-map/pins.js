// Funktion zum Abrufen und Anzeigen vorhandener Pins 
function fetchAndDisplayPins() {
    fetch('/interactive-map/pins')
        // konvertiere Daten zu einem JavaScript Objekt
        .then(response => response.json())
        .then(data => {
            // Pins bei einer neuen Anfrage entfernen, um duplikative 
            // Pins zu vermeiden 
            map.eachLayer(layer => {
                if (layer instanceof L.Marker) {
                    map.removeLayer(layer);
                }
            });
            $('#pin-list').empty();

            // Füge die neuen Pins hinzu der Weltkarte und der Tabelle hinzu
            data.forEach(pin => {
                var popupContent = `Status: ${pin.status}<br>Person: ${pin.personName}`;
                L.marker([pin.lat, pin.lng]).bindPopup(popupContent).addTo(map);
                addPinToTable(pin); 
            });

            // Neuinstanziierung der Tabelle um fehlerhafte Einträge zu vermeiden 
            $('#pin-table').DataTable().destroy(); 
            $('#pin-table').DataTable(); 
        });
}

// Fügt einen Event-Listener (Click-Event) zur Karte hintzu
// wird bei jedem Kartenklick ausgeführt 
map.on('click', function(e) {
    var lat = e.latlng.lat;
    var lng = e.latlng.lng;

    var status = "Neu"; // Setze Defaultstatus auf "Neu"
    var personName = prompt("Enter person's name:") || "Anonym"; // Setze Default-Name wenn leer auf "Anonym"

    // Erstelle das Popup
    var popupContent = `Status: ${status}<br>Person: ${personName}`;
    L.marker([lat, lng]).bindPopup(popupContent).addTo(map).openPopup();

    // Sendet eine Anfrage an den Server um den neuen Pin zu setzen 
    fetch('/interactive-map/pins', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ lat: lat, lng: lng, status: status, personName: personName })
    }).then(() => {
        // Aktualisieren der Pins 
        fetchAndDisplayPins();
    });
});
