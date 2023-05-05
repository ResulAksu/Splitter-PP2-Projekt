$('#neues_mitglied_form').hide()
$('#neue_ausgabe_form').hide()

$('#neues_mitglied_btn').click(show_neues_mitglied_form)
$('#neue_ausgabe_btn').click(show_neue_ausgabe_form)

function show_neues_mitglied_form() {
  $('#neues_mitglied_form').show()
  $('#neues_mitglied_btn').hide()
}

function show_neue_ausgabe_form() {
  $('#neue_ausgabe_form').show()
  $('#neue_ausgabe_btn').hide()
}

if ($('#mitgliedid').val() !== '') {
  show_neues_mitglied_form()
}

if ($('#ausgabeid').val() !== '' || $('#mengeid').val() != 0
    || $('#schuldnerid').val() != null
    || $('#teilnehmerid :selected').length !== 0) {
  show_neue_ausgabe_form()
}