package global

import play.api._

import java.io.File

import nebula._

object PlayGlobal extends GlobalSettings {
  // TODO: Remove dependence on env variables.
  val lucidPresentationRootEnv = System.getenv("LUCID_PRESENTATION_ROOT")
  assert(lucidPresentationRootEnv != null, "You must define this environment variable")
  val publicRoot = new File(lucidPresentationRootEnv + "/public")
  assert(publicRoot.exists && publicRoot.isDirectory)

  val runtimeConfigEnv = System.getenv("LUCID_RUNTIME_CONFIG")
  assert(runtimeConfigEnv != null, "You must define this environment variable")
  val runtimeConfigFile = new File(runtimeConfigEnv)
  assert(runtimeConfigFile.exists && runtimeConfigFile.isFile)

  val tableConfigDirectoryEnv = System.getenv("LUCID_TABLE_CONFIG_DIRECTORY")
  assert(tableConfigDirectoryEnv != null, "You must define this environment variable")
  val tableConfigDirectory = new File(tableConfigDirectoryEnv)
  assert(tableConfigDirectory.exists && tableConfigDirectory.isDirectory)

  ///////////////////////////////////////////////////////////  

  lazy val tableConfigFiles = {
    tableConfigDirectory.listFiles.filter(_.toString.endsWith(".scala")).toSeq
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
    RuntimeConfig.init(runtimeConfigFile)

    Logger.info("in play global")
    println("using println")
  }
}
