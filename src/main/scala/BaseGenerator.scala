import chisel3._
import freechips.rocketchip.util.HasRocketChipStageUtils
import org.chipsalliance.cde.config.Config


abstract class BaseGenerator(args: Array[String]) extends HasRocketChipStageUtils {
  val opts = args.grouped(2).collect { case Array(k, v) => k -> v }.toMap

  val configName: String = opts.getOrElse("--config", "default").toLowerCase
  val designName: String = opts.getOrElse("--design", "default").toLowerCase
  println("Generate systemverilog:")
  println(s" - design: ${designName}")
  println(s" - config: ${configName}")

  val packageName = this.getClass.getPackage.getName
  val baseDir = System.getProperty("user.dir")
  val fullName = s"$designName.$configName"
  val targetDir = s"$baseDir/rtl_codes/fromChisel/$fullName"

  implicit val config: Config
  val design: Module

  new chisel3.stage.ChiselStage().emitFirrtl(
    design,
    Array(
      "--target-dir", targetDir,
      "-fct", "firrtl.passes.InlineInstances",
      "--output-annotation-file", fullName,
    )
  )

  import sys.process._
  val genSV = Seq(
    "/opt/firtool-1.68.0/bin/firtool",
    "--format=fir",
    "--split-verilog",
    "--disable-all-randomization",
    "--emit-chisel-asserts-as-sva",
    "--fixup-eicg-wrapper",
    "--emit-separate-always-blocks",
    "--export-module-hierarchy",
    "--preserve-aggregate=none", // vec, all
    "--preserve-values=all",
    "--lowering-options=explicitBitcast",
    "--lowering-options=caseInsensitiveKeywords",
    "--lowering-options=disallowExpressionInliningInPorts",
    "--lowering-options=disallowLocalVariables",
    "--repl-seq-mem", s"--repl-seq-mem-file=${targetDir}/${fullName}.sram.conf",
    s"-o ${targetDir}/rtl_codes ${targetDir}/${design.name}.fir"
  ).reduce(_ + " " + _)
  println(genSV)
  genSV.!

  val genSimSRAM = Seq(
    "/opt/utils/vlsi_mem_gen",
    s"${targetDir}/${fullName}.sram.conf",
    s"--output_file ${targetDir}/rtl_codes/${fullName}.sram.v",
  ).reduce(_ + " " + _)
  println(genSimSRAM)
  scala.io.Source.fromFile(s"${targetDir}/${fullName}.sram.conf").getLines().nextOption() match {
    case Some(line) if line.isEmpty => println(s"${fullName} does not have SRAM")
    case _ => genSimSRAM.!
  }
}
