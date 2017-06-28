/**
 * Created by Nett on 16.06.2017.
 */

'use strict'

/**
 * Die Seite maesurement.html dient zur Simulation einer Messung, bei welcher die Z-Achse schrittweise faehrt, und pro
 * angefahrene Position die Messwerte einliest und in einer Tabelle darstellt.
 *
 * Nach Aufbau der Seite wird die Tabelle mit den verfuegbaren Devices geladen
 */
let MAXPOS = 100;
let STEP = 10;
let STOP = 600;
let ERROR = 500;
let time = 0;
let position = 0;

let xmlhttpDevList, tableDevice, row, cellName;
xmlhttpDevList = new XMLHttpRequest();
xmlhttpDevList.onload = function (e) {
    if (xmlhttpDevList.readyState === 4) {
        if (xmlhttpDevList.status === 200) {
            let json = JSON.parse(xmlhttpDevList.responseText);

            for (let node in json) {
                tableDevice = document.getElementById("deviceTable");
                row = tableDevice.insertRow(tableDevice.rows.length);

                row.insertCell(0).innerHTML = '<input class="checkbox-primary" type="checkbox">';
                cellName = row.insertCell(1);
                cellName.innerHTML = json[node].name;
                cellName.className = "deviceName";
                row.insertCell(2).innerHTML = json[node].hostIp;
                row.insertCell(3).innerHTML = json[node].dataSource;
                row.insertCell(4).innerHTML = json[node].group;
                row.insertCell(5).innerHTML = json[node].protocol;
                row.insertCell(6).innerHTML = '<select class="form-control" id="direction"><option>X-Richtung</option><option>Y-Richtung</option></select>';
            }
            registerHandlers();
            addSortable();
        }else if (xmlhttpDevList.status == 400) {
            showError(xmlhttpDevList.responseText);
            document.querySelector("#start").disabled = true;
        }
    }
};

xmlhttpDevList.ontimeout = function(){
    showError("Der Dienst messdaten-server antwortet nicht");
};

xmlhttpDevList.open("GET", "http://localhost:9000/listJson" + '?_=' + new Date().getTime(), true);
xmlhttpDevList.timeout = 2000;
xmlhttpDevList.send();

/**
 * Register die Change-Handler der Checkboxen und enable/disable diese und den Programm-Start-Button
 * in Abhaengigkeit der Anzahl der selektierten Devices.
 *
 * Voraussetzungen:
 * Es können maximal zwei Devices Devices selektiert werden.
 * Zum Programm-Start muessen zwei Devices selektiert sein.
 */
function registerHandlers() {
    let count = 0;
    document.querySelector("#start").disabled = true;
    $('input[type="checkbox"]').change(function () {
        if ($(this).prop('checked')) {
            count++;
        } else {
            count--;
        }
        if (count > 1) {
            changeCheckboxState(true);
        } else {
            changeCheckboxState(false);
        }
    });
}

function addSortable(){

    $('#tablelist').DataTable({
        //removing arrows from 'checkbox' and 'direction' columns
        "aoColumnDefs": [{"bSortable": false, "aTargets": [0, 6]}]
     });
}

/**
 * Enable/disable der Checkboxen und des Programm-Start-Buttons gemaess cmd und prgEnd
 *
 * @param cmd
 * @param prgEnd
 */
function changeCheckboxState(cmd, prgEnd) {
    $('#deviceTable').find('tr').each(function () {
        let row = $(this);
        if (!row.find('input[type="checkbox"]').is(':checked')) {
            this.querySelector("input[type='checkbox']").disabled = cmd;
            this.querySelector("select").disabled = cmd;
        } else {
            if (prgEnd) {
                this.querySelector("input[type='checkbox']").disabled = !cmd;
                this.querySelector("select").disabled = !cmd;
            }
        }

    });
    document.querySelector("#start").disabled = !cmd;
}

/**
 * Stoppt das Programm und setzt die selektierten Checkboxen auf enable
 */
function stopProgramm() {
    position = STOP;
    changeCheckboxState(true, true);
}

/**
 * Disable alle Checkboxen und Button Programm-Start
 */
function changeAllCheckboxState(cmd) {
    $('#deviceTable').find('tr').each(function () {
        let row = $(this);
        this.querySelector("input[type='checkbox']").disabled = cmd;
        this.querySelector("select").disabled = cmd;
    });
    document.querySelector("#start").disabled = cmd;
}

/**
 * Die Funktion ruft im Intervall von 2s. die innere Funktion addData() auf, bis die position groesser ist.
 * Die Variable position soll die zu messende Z-Achse simulieren und wird pro Durchgang um erhoeht,
 * Die Funktion addData() iteriert über die Tabelle der verfuegbaren Devices, und sendet für die zwei markierten
 * Devices einen XMLHttpRequest um den aktuellen Messwert anzufordern.
 * Pro Messposition werden die Messwerte beider Devices in eine neue Zeile der Tabelle mit den Resultaten eingefuegt.
 *
 */
function addDataDelayed() {
    let deviceValues = [];
    position = 0;
    resetErrorMessage();
    resetHeader();
    clearTable();
    let retVar = setInterval(addData, 2000);

    function addData() {
        if (position > MAXPOS) {
            changeCheckboxState(true, true);
            clearInterval(retVar);
            selectMessage();
        } else {
            let table = document.getElementById("resultTable");
            let rowCount = table.rows.length;
            let newrow = table.insertRow(rowCount);

            changeAllCheckboxState(true);
            $('#deviceTable').find('tr').each(function () {
                let row = $(this);
                deviceValues.length = 0;

                if (row.find('input[type="checkbox"]').is(':checked')) {
                    let name = row.find(".deviceName").text();
                    let xmlhttp;
                    xmlhttp = new XMLHttpRequest();

                    // Die Texte der Header werden nur aktualisiert, wenn nicht schon beide Default-Titel ueberschrieben sind
                    if ($("th.device1").html().indexOf('Device-Name') != -1 || $("th.device2").html().indexOf('Device-Name') != -1) {
                        addHeaderDescription(name, row);
                    }
                    xmlhttp.onload = function (e) {
                        if (xmlhttp.readyState === 4) {
                            if (xmlhttp.status === 200) {

                                let json = JSON.parse(xmlhttp.responseText);
                                if (!parseJsonResponse(json, deviceValues)) {
                                    position = ERROR;
                                }
                                if (checkArray(deviceValues)) {
                                    newrow.insertCell(0).innerHTML = deviceValues[0];
                                    newrow.insertCell(1).innerHTML = deviceValues[1];
                                    newrow.insertCell(2).innerHTML = deviceValues[2];
                                    newrow.insertCell(3).innerHTML = deviceValues[3];
                                    newrow.insertCell(4).innerHTML = position;
                                    position += STEP;
                                }
                            } else if (xmlhttp.status == 400) {
                                showError(xmlhttp.responseText);
                                position = ERROR;
                            }
                        }
                    };
                    xmlhttp.ontimeout = function(){
                        showError("Der Dienst messdaten-server antwortet nicht");
                        position = ERROR;
                    };

                    xmlhttp.open("GET", "http://localhost:9000/device/value/" + name + '?_=' + new Date().getTime(), true);
                    xmlhttp.timeout = 2000;
                    xmlhttp.send();
                }
            });
        }
    }
}

/**
 * Loescht die Resultate aus der Tabelle der Messwerte
 */
function clearTable() {
    $("#resultTable").find("tr").remove();
}

/**
 * Fuegt in den Header der Tabelle mit den Messwerten den Namen des Devices und die gewaehlte Messrichtung ein.
 * Der erste Device wird in die erste Kollonne eingefuegt, falls beide Kollonnen noch die Default-Bezeichnung enthalten.
 *
 * @param name
 * @param row
 */
function addHeaderDescription(name, row) {

    if ($("th.device1").html().indexOf('Device-Name') != -1 && $("th.device2").html().indexOf('Device-Name') != -1) {
        $("th.device1").html(name + " : " + row.find('#direction option:selected').text());
    } else {
        $("th.device2").html(name + " : " + row.find('#direction option:selected').text());
    }
}

/**
 * Die Funktion prueft den Zeitstempel der Messwerte auf Gueltigkeit, und loest im Fehlerfall eine Meldung aus.
 * Die Funktion wird pro Device ein Mal durchlaufen, und schreibt in das Array deviceValues[] jeweils den
 * aktuellen Messwert und den Zeitstempel.
 * Die Werte werden in die Kollonne mit der entsprechenden Bezeichnung des Devices geschrieben.
 *
 * @param json
 * @param deviceValues
 * @returns {number}
 */
function parseJsonResponse(json, deviceValues) {

    if (time < json.time) {
        if ($("th.device1").html().indexOf(json.id) != -1) {
            deviceValues[$("th.device1").index()] = json.value;
            deviceValues[$("th.device1").index() + 1] = new Date(json.time).toLocaleTimeString();
        } else {
            deviceValues[$("th.device2").index()] = json.value;
            deviceValues[$("th.device2").index() + 1] = new Date(json.time).toLocaleTimeString();
        }
        if (checkArray(deviceValues)) {
            time = json.time;
        }
    } else {
        showError('Messprogramm gestoppt!\nKeine gültigen Messwerte von Device ' + json.id + ' verfügbar');
        return false;
    }
    return true;
}

/**
 * Prüft ob das Array deviceValues[] 4 gueltige Werte enthaelt.
 * Gueltig ist ein Wert der weder null, undefined noch leer ist.
 *
 * @param deviceValues
 * @returns {number}
 */
function checkArray(deviceValues) {

    if (deviceValues.length < 4) {
        return false;
    }
    for (let idx = 0; idx < 4; idx++) {
        if (deviceValues[idx] === null) {
            return false;
        }
        if (deviceValues[idx] === undefined) {
            return false;
        }
        if (deviceValues[idx] === '') {
            return false;
        }
    }
    return true;
}

/**
 * Loest die entsprechende Message aus
 */
function selectMessage(){

     switch(position){
        case MAXPOS + STEP:
            showMessage('Das Programm wurde erfolgreich beendet!');
            break;
        case STOP:
            showMessage('Das Programm wurde abgebrochen!');
            break;
        default:
            break;
     }
}

/**
 * Loest eine Fehlermeldung mit der uebergebenen Meldung aus.
 *
 * @param message
 */
function showError(message) {
    $("#error").html('<div class="text-center alert alert-danger"></div>');
    $(".alert-danger").html(message);
}

/**
 * Loest eine Betriebsmeldung mit dem uebergebenen Text aus.
 *
 * @param message
 */
function showMessage(message) {
    $("#error").html('<div class="text-center alert alert-info"></div>');
    $(".alert-info").html(message);
}

/**
 * Loescht die Fehlermeldung
 */
function resetErrorMessage() {
    $("#error").html('');
}

/**
 * Setzt die Texte der Header der Tabelle mit den Messwerte zurueck
 */
function resetHeader() {
    $("th.device1").html('Device-Name : Direction');
    $("th.device2").html('Device-Name : Direction');
}

/**
* Minimiert/Maximiert die Liste der Devices und passt die Bezeichnung des Symbols an
*
*/
function minMaxList(){
   $("#tablelist tbody").toggle("fast");

   if($("#mini").html().indexOf('-') != -1){
    $("#mini").html('+');
   }else{
    $("#mini").html('-');
   }

}
