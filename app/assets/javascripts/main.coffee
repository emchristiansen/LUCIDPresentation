assert = (predicate) ->
  if (predicate != true)
    throw "assertion violated"

filter = (list, func) -> x for x in list when func(x)

highlightTable = () ->
  table = document.getElementById("resultsTable")
  tbodytds = table.getElementsByTagName("td")
  tdLabel = (attributeName, node) ->
    attributeValue = $(node).attr(attributeName)
    labels = filter table.getElementsByTagName("th"), (x) -> $(x).hasClass(attributeName + "-label") && $(x).attr(attributeName) == attributeValue
    assert(labels.length == 1)
    labels[0]
  updateImage = (node) ->
    div = node.getElementsByTagName("div")[0]
    imageLocation = $(div).attr("histogram")
    img = document.getElementsByTagName("img")
    $(img).attr("src", imageLocation)

  register = (node) ->
    node.onmouseover = () ->
      $(node).addClass("td-hover")
      $(tdLabel("column", node)).addClass("td-hover")
      $(tdLabel("row", node)).addClass("td-hover")
      updateImage(node)
    node.onmouseout = () ->
      $(node).removeClass("td-hover")
      $(tdLabel("column", node)).removeClass("td-hover")
      $(tdLabel("row", node)).removeClass("td-hover")

  for node in tbodytds
    register node

setupTableSelector = () ->
  list = document.getElementById("tableNames")
  items = list.getElementsByTagName("li")

  register = (node) ->
    node.onclick = () ->
      tableName = $(node).attr("tableName")
      $("#resultsTable").load("ajax/resultsTable/" + tableName, highlightTable)

  for node in items
    register node

window.onload = () ->
#  jQuery.ajaxSetup({async:false})
  setupTableSelector()
  $("#resultsTable").load("ajax/resultsTable/small", highlightTable)        
