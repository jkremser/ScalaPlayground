import scala.util.parsing.combinator._

// subset of grammar defined on https://docs.jboss.org/author/display/RHQ/Group+Definitions
object DynagroupParser extends RegexParsers {

  def resourceExpression = "resource.name" | "resource.version" | "resource.type.plugin" | "resource.type.name" | "resource.type.category" | "resource.parent.id" | "resource.parent.name" | "resource.parent.version" | "resource.parent.type.plugin" | "resource.parent.type.name" | "resource.parent.type.category" | "resource.grandParent.id" | "resource.grandParent.name" | "resource.grandParent.version" | "resource.grandParent.type.plugin" | "resource.grandParent.type.name" | "resource.grandParent.type.category" | "resource.pluginConfiguration" | "resource.resourceConfiguration" | "resource.trait"
  
  def simpleExpression = resourceExpression ~ "=" ~ valueExpression

  def valueExpression = """.+"""r

  def expression = simpleExpression | pivotedExpression

  def finalExpression = rep(expression)

  def pivotedExpression = "groupby"~resourceExpression 

  def parseExpression = (foo: String) => parseAll(DynagroupParser.expression, foo)
}
