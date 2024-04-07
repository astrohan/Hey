package commons

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex

object Assembler {
  def apply(x: Seq[String], xLen: Int): Seq[BigInt] = {
    val pw = new PrintWriter(new File("test.s"))
    pw.write(".global _start\n")
    pw.write("_start:\n")
    x.foreach(s => pw.write("    " + s + "\n"))
    pw.close()
    import sys.process._
    val march = if(xLen == 32) "-mabi=ilp32 -march=rv32gc" else "-mabi=lp64 -march=rv64gc"
    val comp = "/opt/riscv/bin/riscv64-unknown-elf-gcc " + march + " -nostdlib test.s -o test.elf"
    val dump = "sh -c \"/opt/riscv/bin/riscv64-unknown-elf-objdump -d test.elf > test.dump\""
    comp.!
    dump.!

    val source = Source.fromFile("test.dump")
    val machineCodeRegex: Regex = """\s+([0-9a-fA-F]{8})\s+""".r
    val machineCodes = source.getLines.flatMap { line =>
      machineCodeRegex.findFirstIn(line) match {
        case Some(code) =>
          val machineCode = code.trim
          Try(BigInt(machineCode, 16)).toOption
        case None => None
      }
    }.toSeq
    source.close()

    machineCodes
  }
}

object test extends App {
  val test = Seq(
    "lw x1, 0x0(x5)",
    "lw x2, 0x0(x8)",
    "sw x5, 0x0(x8)",
  )
  Assembler(test, 32)
}