package commons

import chisel3._
import chisel3.util.log2Ceil

import scala.annotation.tailrec


object CommonUtils {
  def log2(x: Double) = math.log10(x) / math.log10(2)
  def UIntToThermo(x: UInt): UInt = {
    val length = x.getWidth
    val nStage = log2Ceil(length)
    val req = Wire(UInt((1<<log2Ceil(length)).W))
    req := x
    
    @tailrec
    def loop(s: Int, prev: Seq[Bool]): Seq[Bool] = {
      if(s == nStage) prev
      else {
        val next = Seq.tabulate(length){ i =>
          val isBypass = (i>>s)%2 == 0
          val j = ((i>>s)<<s)-1
          if(isBypass) prev(i) else prev(i) || prev(j)
        }
        loop(s+1, next)
      }
    }
    VecInit(loop(0, req.asBools)).asUInt
  }

  implicit class UtilsForUInt[T<:UInt](x: T) {
    def inside(a: UInt*) = a.map(_ === x).reduce(_ || _)
  }

  implicit class UtilsForBundle[T<:Bundle](x: T) {
    def connect(elems: (T => (Data, Data))*): T = {
      elems.map{ fn => fn(x) }
      x
    }

    def connect(init: T, elems: (T => (Data, Data))*): T = {
      x := init
      elems.map{ fn => fn(x) }
      x
    }
  }
}
