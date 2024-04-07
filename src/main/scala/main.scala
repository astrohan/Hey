import commons.{RRArbiter, SimpleRRArbiter}
import org.chipsalliance.cde.config.{Config, Parameters}


object main extends App { new main(args) }
class main(args: Array[String]) extends BaseGenerator(args) {
  lazy val design = designName match {
    case "simplerrarbiter" => new SimpleRRArbiter(4)
    case "rrarbiter" => new RRArbiter(4)
    //case "decoder" => new riscv.Decoder()(0, Parameters.empty)
    case _ => {
      throw new Exception(s"Invalid config name(${designName})")
    }
  }

  implicit val config = configName match {
    case _ => new Config(Parameters.empty)
  }
}
