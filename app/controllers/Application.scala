package controllers

import play.api._
import play.api.mvc._

import nebula._

import global._

object Application extends Controller {  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def resultsTable(tableName: String) = Action {
    Ok(views.html.table(tableName, PlayGlobal.nameToTable(tableName)))
  }
}
