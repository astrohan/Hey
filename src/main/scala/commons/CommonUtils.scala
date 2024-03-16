package commons

import chisel3._
import chisel3.util.log2Ceil

import scala.annotation.tailrec


object CommonUtils {
  def UIntToThermo(x: UInt): UInt = {
    val length = x.getWidth
    val nStage = log2Ceil(length)

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
    VecInit(loop(0, x.asBools)).asUInt
  }
}
