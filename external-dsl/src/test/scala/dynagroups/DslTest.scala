import org.scalatest.FlatSpec
import org.scalatest._
class StackSpec extends FlatSpec with Matchers{

  behavior of "Dynamic groups DSL"

  it should "parse the simple expressions" in {
    val sentence1 = DynagroupParser.parseExpression("resource.name='foo'")
    sentence1.successful should be (true)
    val sentence2 = DynagroupParser.parseExpression("resource.parent.id='ahoj'")
    sentence2.successful should be (true)
  }

  it should "parse more complicated expressions" in {
    val sentence1 = DynagroupParser.parseExpression("resource.grandParent.type.category='Platform' groupby resource.type.name")
    sentence1.successful should be (true)
    val sentence2 = DynagroupParser.parseExpression("resource.type.category='Server' groupby resource.type.plugin")
    sentence2.successful should be (true)
  }

}
