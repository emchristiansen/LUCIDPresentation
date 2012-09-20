package global

import play.api._

import java.io.File

import nebula._

object PlayGlobal extends GlobalSettings {
  // TODO: Make this system agnostic.
  lazy val publicRoot = new File("/u/echristiansen/time/2012_08_19/LUCIDPresentation/public")

  lazy val tableConfigFiles = {
    val directory = new File(
      Play.current.configuration.getString("experiment.tableConfigDirectory").get)
    assert(directory.isDirectory)
    directory.listFiles.filter(_.toString.endsWith(".scala")).toSeq
  }

  lazy val tableNames = tableConfigFiles.map(_.getName.replace(".scala", "").replace("table_", ""))

  lazy val nameToFile: Map[String, File] = tableNames.zip(tableConfigFiles).toMap

  def nameToTableUnrendered(name: String): TableUnrendered = {
    val experiments = 
      IO.interpretFile[Seq[Seq[CorrespondenceExperiment]]](nameToFile(name))
    TableUnrendered(experiments)
  }

  lazy val nameToTableUnrenderedMemo: String => TableUnrendered = 
    Memoize(nameToTableUnrendered _)

  def nameToTable(name: String): Table = {
    val table = nameToTableUnrenderedMemo(name)
    def summarizers(experiment: CorrespondenceExperiment) = {
      Seq(
	ExperimentSummaryDouble(
	  "recognitionRate", 
	  TableUnrendered.recognitionRate(experiment)),
	ExperimentSummaryBufferedImage(
	  "histogram",
	  Histogram(experiment, "").render))
    }

    table.render(summarizers)
  }

  lazy val tables: Seq[TableUnrendered] = {
    val experiments = tableConfigFiles.map(IO.interpretFile[Seq[Seq[CorrespondenceExperiment]]])
    experiments.map(TableUnrendered.apply _)
  }

  override def onStart(app: Application) {
    RuntimeConfig.init(
      new File(Play.current.configuration.getString("experiment.runtimeConfigFile").get))

    Logger.info("in play global")
    println("using println")
  }
}
