$('#neue_gruppe_form').hide()

$('#neue_gruppe_btn').click(show_new_gruppe_form)

if ($('#grouptitleid').val() != '') {
  show_new_gruppe_form()
}

function show_new_gruppe_form() {
  $('#neue_gruppe_form').show()
  $('#neue_gruppe_btn').hide()
}