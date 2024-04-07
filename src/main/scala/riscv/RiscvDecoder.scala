package riscv

import chisel3._
import chisel3.util._
import chisel3.util.experimental.decode.{DecodeField, DecodePattern, DecodeTable}
import org.chipsalliance.cde.config.Parameters

/**
 * The purpose of the Decoder is to obtain DecodeCmd
 */
object DecodeCmd {
  def apply(inst: UInt, instMap: Map[String, BitPat]): DecodeCmd = {
    val idecoder = new RiscvDecoder(instMap)
    idecoder.decode(inst)
  }
}

class DecodeCmd extends Bundle {
  val optype = UInt(OpType.SZ.W)
  val illegal = Bool()
  val ecall = Bool()
  val ebreak = Bool()
  val fense = Bool()
  val wfi = Bool()
  val selOpA = UInt(SelOp.SZ.W)
  val selOpB = UInt(SelOp.SZ.W)
  val selRd  = UInt(SelRd.SZ.W)
  val memCmd = UInt(MemCmd.SZ.W)
  val size   = UInt(2.W)
  val signed = Bool()
  val aluCmd = UInt(AluCmd.SZ.W)
}

class RiscvDecoder(instMap: Map[String, BitPat]) {
  val allInst = instMap.map(i => InstPattern(i._1, i._2)).toSeq
  val decoder = new DecodeTable(allInst, Seq(RiscvDecoderTable))

  def decode(inst: UInt) = decoder.decode(inst)(RiscvDecoderTable)
}

case class InstPattern(val name: String, val code: BitPat) extends DecodePattern {
  def bitPat = code
}

object OpType {
  val SZ = 3
  val I  = 0x0.U(SZ.W)
  val R  = 0x1.U(SZ.W)
  val U  = 0x2.U(SZ.W)
  val B  = 0x3.U(SZ.W)
  val J  = 0x4.U(SZ.W)
  val R4 = 0x5.U(SZ.W)
  val S  = 0x6.U(SZ.W)
  val MSIC = 0x7.U(SZ.W)
}

object SelOp {
  val SZ = 4
  val NONE    = 0x0.U(SZ.W)
  val REG     = 0x1.U(SZ.W)
  val IIMM    = 0x2.U(SZ.W)
  val UIMM    = 0x3.U(SZ.W)
  val JIMM    = 0x4.U(SZ.W)
  val SIMM    = 0x5.U(SZ.W)
  val PC      = 0x6.U(SZ.W)
  val CSR     = 0x7.U(SZ.W)
  val CSRIMMM = 0x8.U(SZ.W)
}

object SelRd {
  val SZ = 3
  val NONE        = 0x0.U(SZ.W)
  val ALU         = 0x1.U(SZ.W)
  val UIMM        = 0x2.U(SZ.W)
  val LOAD        = 0x3.U(SZ.W)
}

object AluCmd {
  val SZ = 4
  val NONE = 0x0.U(SZ.W)
  val ADD  = 0x1.U(SZ.W)
  val SUB  = 0x2.U(SZ.W)
  val SLL  = 0x3.U(SZ.W)
  val SLT  = 0x4.U(SZ.W)
  val SLTU = 0x5.U(SZ.W)
  val SRA  = 0x6.U(SZ.W)
  val SRL  = 0x7.U(SZ.W)
  val AND  = 0x8.U(SZ.W)
  val OR   = 0x9.U(SZ.W)
  val XOR  = 0xA.U(SZ.W)
}

object MemCmd {
  val SZ = 5
  val NONE = 0x0.U(SZ.W)
  val XRD  = 0x1.U(SZ.W) // write RD register
  val XWR  = 0x2.U(SZ.W) // only for Store
}

trait InstProperty {
  val name: String
  val optype  : UInt
  val opcode: Seq[String]
  def illegal : Seq[Bool] = Seq.fill(opcode.length)(false.B)
  def ecall   : Seq[Bool] = Seq.fill(opcode.length)(false.B)
  def ebreak  : Seq[Bool] = Seq.fill(opcode.length)(false.B)
  def fence   : Seq[Bool] = Seq.fill(opcode.length)(false.B)
  def wfi     : Seq[Bool] = Seq.fill(opcode.length)(false.B)
  def selOpA  : Seq[UInt] = Seq.fill(opcode.length)(0.U(SelOp.SZ.W))
  def selOpB  : Seq[UInt] = Seq.fill(opcode.length)(0.U(SelOp.SZ.W))
  def selRd   : Seq[UInt] = Seq.fill(opcode.length)(0.U(SelRd.SZ.W))
  def memCmd  : Seq[UInt] = Seq.fill(opcode.length)(0.U(MemCmd.SZ.W))
  def size    : Seq[UInt] = Seq.fill(opcode.length)(0.U(2.W))
  def signed  : Seq[Bool] = Seq.fill(opcode.length)(false.B)
  def aluCmd  : Seq[UInt] = Seq.fill(opcode.length)(0.U(AluCmd.SZ.W))

  // ip = instruction property
  def getProperties[T <: InstProperty](x: String): BitPat = {
    val i = this.opcode.indexOf(x)

    BitPat(this.optype)     ##
    BitPat(this.illegal(i)) ##
    BitPat(this.ecall(i))   ##
    BitPat(this.ebreak(i))  ##
    BitPat(this.fence(i))   ##
    BitPat(this.wfi(i))     ##
    BitPat(this.selOpA(i))  ##
    BitPat(this.selOpB(i))  ##
    BitPat(this.selRd(i))   ##
    BitPat(this.memCmd(i))  ##
    BitPat(this.size(i))    ##
    BitPat(this.signed(i))  ##
    BitPat(this.aluCmd(i))
  }
}

object RiscvDecoderTable extends DecodeField[InstPattern, DecodeCmd] {
  def name = "Instruction Decoder"

  def chiselType: DecodeCmd = new DecodeCmd

  def genTable(x: InstPattern): BitPat = x.name match {
    case op if (OpI.opcode.contains(op)) => OpI.getProperties(op)
    case opImm if (OpIMM.opcode.contains(opImm)) => OpIMM.getProperties(opImm)
    case load if (Load.opcode.contains(load)) => Load.getProperties(load)
    case store if (Store.opcode.contains(store)) => Store.getProperties(store)
    case jal if (Jal.opcode.contains(jal)) => Jal.getProperties(jal)
    case jalr if (Jalr.opcode.contains(jalr)) => Jalr.getProperties(jalr)
    case branch if (Branch.opcode.contains(branch)) => Branch.getProperties(branch)
    case auipc if (Auipc.opcode.contains(auipc)) => Auipc.getProperties(auipc)
    case lui if (Lui.opcode.contains(lui)) => Lui.getProperties(lui)
    case system if (System.opcode.contains(system)) => System.getProperties(system)
    case miscMem if (MiscMem.opcode.contains(miscMem)) => MiscMem.getProperties(miscMem)
    case _ => BitPat.dontCare((new DecodeCmd).getWidth)
  }
}

object OpI extends InstProperty {
  import AluCmd._
  val name    = "Op Instructions"
  val optype  = OpType.R
  val opcode  = Seq("ADD", "AND", "OR", "SLL", "SLT", "SLTU", "SRA", "SRL", "SUB", "XOR")
  override val selOpA  = Seq.fill(opcode.length)(SelOp.REG)
  override val selOpB  = Seq.fill(opcode.length)(SelOp.IIMM)
  override val selRd   = Seq.fill(opcode.length)(SelRd.ALU)
  override val memCmd  = Seq.fill(opcode.length)(MemCmd.XRD)
  override val aluCmd  = Seq(ADD, AND, OR, SLL, SLT, SLTU, SRA, SRL, SUB, XOR)
}
object OpIMM extends InstProperty {
  import AluCmd._
  val name    = "Op Instructions"
  val optype  = OpType.I
  val opcode  = Seq("ADDI", "ANDI", "ORI", "SLTI", "SLTIU", "XORI")
  override val selOpA  = Seq.fill(opcode.length)(SelOp.REG)
  override val selOpB  = Seq.fill(opcode.length)(SelOp.IIMM)
  override val selRd   = Seq.fill(opcode.length)(SelRd.ALU)
  override val memCmd  = Seq.fill(opcode.length)(MemCmd.XRD)
  override val aluCmd  = Seq(ADD, AND, OR, SLT, SLTU, XOR)
}
object Load extends InstProperty {
  val name    = "Load Instructions"
  val optype  = OpType.I
  val opcode  = Seq("LB", "LBU", "LH", "LHU", "LW")
  override val selOpA  = Seq.fill(opcode.length)(SelOp.REG)
  override val selOpB  = Seq.fill(opcode.length)(SelOp.IIMM)
  override val selRd   = Seq.fill(opcode.length)(SelRd.LOAD)
  override val memCmd  = Seq.fill(opcode.length)(MemCmd.XRD)
  override val size    = Seq(0.U(2.W), 0.U(2.W), 1.U(2.W), 1.U(2.W), 2.U(2.W))
  override val signed  = Seq(true.B, false.B, true.B, false.B, true.B)
  override val aluCmd  = Seq.fill(opcode.length)(AluCmd.ADD)
}
object Store extends InstProperty {
  val name    = "Store Instructions"
  val optype  = OpType.S
  val opcode  = Seq("SB", "SH", "SW")
  override val selOpA  = Seq.fill(opcode.length)(SelOp.REG)
  override val selOpB  = Seq.fill(opcode.length)(SelOp.IIMM)
  override val selRd   = Seq.fill(opcode.length)(SelRd.NONE)
  override val memCmd  = Seq.fill(opcode.length)(MemCmd.XWR)
  override val size    = Seq(0.U(2.W), 1.U(2.W), 2.U(2.W))
  override val aluCmd  = Seq.fill(opcode.length)(AluCmd.ADD)
}
object Jal extends InstProperty {
  val name    = "Jump And Link Instructions"
  val optype  = OpType.J
  val opcode  = Seq("JAL")
}
object Jalr extends InstProperty  {
  val name    = "Jump And Link Register Instructions"
  val optype  = OpType.I
  val opcode  = Seq("JARL")
}
object Branch extends InstProperty {
  val name    = "Branch Instructions"
  val optype  = OpType.B
  val opcode  = Seq("BEQ", "BGE", "BGEU", "BLT", "BLTU", "BNE")
  override val selOpA  = Seq.fill(opcode.length)(SelOp.REG)
  override val selOpB  = Seq.fill(opcode.length)(SelOp.REG)
  override val aluCmd  = Seq.fill(opcode.length)(AluCmd.ADD)
}
object Auipc extends InstProperty {
  val name    = "Add Upper Immediate to PC Instructions"
  val optype  = OpType.U
  val opcode  = Seq("AUIPI")
}
object Lui extends InstProperty {
  val name    = "Load Upper Immediate Instructions"
  val optype  = OpType.U
  val opcode  = Seq("LUI")
}
object System extends InstProperty {
  val name    = "System Instructions"
  val optype  = OpType.I
  val opcode  = Seq("EBREAK", "ECALL")
  override val ecall   = Seq(false.B, true.B)
  override val ebreak  = Seq(true.B, false.B)
}
object MiscMem extends InstProperty {
  val name    = "Misc-Mem Instructions"
  val optype  = OpType.MSIC
  val opcode  = Seq("FENCE")
}
