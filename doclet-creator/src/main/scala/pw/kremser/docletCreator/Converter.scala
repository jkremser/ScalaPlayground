package org.scalaquery.examples

import org.scalaquery.session._
import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.extended.{ExtendedProfile, SQLiteDriver}
import org.scalaquery.ql.extended.{ExtendedTable => Table}

object Converter {


  object Type extends Enumeration {
    type Type = Value
    val Attribute, Binding, Builtin, Callback, Category, Class, Command, Component, Constant,
    Constructor, Define, Delegate, Directive, Element, Entry, Enum, Error, Event,
    Exception, Field, File, Filter, Framework, Function, Global, Guide, Instance,
    Instruction, Interface, Keyword, Library, Literal, Macro, Method, Mixin, Module,
    Namespace, Notation, Object, Operator, Option, Package, Parameter, Procedure,
    Property, Protocol, Record, Resource, Sample, Section, Service, Struct, Style,
    Subroutine, Tag, Trait, Type, Union, Variable = Value
  }

  import Type._

  class DAO(driver: ExtendedProfile) {
    // Import the implicit conversions and values provided by the driver
    import driver.Implicit._

    val searchIndex = new Table[(Int, String, String, String)]("searchIndex") {
      def id = column[Int]("id", O NotNull, O PrimaryKey, O AutoInc)
      def name = column[String]("name", O NotNull)
      def c_type = column[String]("type", O NotNull)
      def path = column[String]("path", O NotNull)
      // Recommended: you can easily prevent adding duplicate entries in the index
      def anchor = index("anchor", name ~ c_type ~ path, unique = true)
      def uName = index("uk_name", name, unique = true)
      def * = id ~ name ~ c_type ~ path
      def tail = name ~ c_type ~ path
    }

    def create(implicit session: Session) = searchIndex.ddl.create
    def selectAll(implicit session: Session) = Query(searchIndex) foreach {
      case (id, name, c_type, path) =>
        println("  " + id + "\t" + name + "\t" + c_type + "\t" + path)
    }
    def insert(name: String, c_type: Type, path: String)(implicit session: Session) = searchIndex.tail.insert(name, c_type.toString, path)
    def insertAll(values: Seq[(String, String, String)])(implicit session: Session) = searchIndex.tail.insertAll(values.toSeq:_*)

  }

  def run(path: String, dao: DAO, db: Database) {
    db withSession { session: Session =>
      implicit val implicitSession = session
      dao.create
      processTokensFile(path, dao)
      println("DB:")
      dao selectAll
    }
  }

  // parses the output of doxygen tool Tokens.xml
  def processTokensFile(path2file: String, dao: DAO)(implicit session: Session) = {
    import scala.xml._
    import scala.collection.mutable.Buffer

    def fst(nSeq: Seq[Node]) = nSeq match {
      case Seq(el) if !el.text.isEmpty => Some(el.text)
      case _ => None
    }

    val values = Buffer[(String, String, String)]()
    val tokensString = scala.io.Source.fromFile(path2file).mkString
    val xml = XML.loadString(tokensString)
    (xml \\ "Token") foreach {
      token => {
        val op_type = fst(token \\ "Type")
        op_type match {
          case Some(st_type) => {
            val name = fst(token \\ "Name")
            val anchor = fst(token \\ "Anchor")
            val path = fst(token \\ "Path").get + (if (!anchor.isEmpty) "#" + anchor.get else "")
            val final_type = st_type match {
              case "clm" => "Static_Method"
              case "instm" => "Method"
              case "cl" if path startsWith "interface" => "Interface"
              case "cl" if path endsWith "Exception" => "Exception"
              case "cl" => "Class"
              case "data" => "Constant"
              case id: String => id
            }
            val id = fst(token \\ "Scope").get + (if (!anchor.isEmpty) "." + name.get else "")
            values append ((id, final_type, path))
          }
          case _ => ()
        }
      }
    }
    dao.insertAll(values)
  }

  def usage = {
    System.err.println("\nUsage: sbt \"run path_to_Tokens.xml\"\n(Tokens.xml is generated by doxygen tool)\n")
    System exit 1
  }

  def main(args: Array[String]) {
    println(args.length)
    if (args.length != 1) usage

    run(args(0), new DAO(SQLiteDriver), Database.forURL("jdbc:sqlite:docSet.dsidx", driver = "org.sqlite.JDBC"))
  }
}