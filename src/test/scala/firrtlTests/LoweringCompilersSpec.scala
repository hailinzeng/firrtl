// See LICENSE for license details.

package firrtlTests

import org.scalatest.{FlatSpec, Matchers}

import firrtl.{ChirrtlToHighFirrtl, HighFirrtlToMiddleFirrtl, IRToWorkingIR, LowFirrtlOptimization,
  MiddleFirrtlToLowFirrtl, MinimumLowFirrtlOptimization, ResolveAndCheck, Transform}
import firrtl.passes
import firrtl.stage.{Forms, TransformManager}

class LoweringCompilersSpec extends FlatSpec with Matchers {

  behavior of "ChirrtlToHighFirrtl"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[passes.CheckChirrtl],
      classOf[passes.CInferTypes],
      classOf[passes.CInferMDir],
      classOf[passes.RemoveCHIRRTL] )
    (new ChirrtlToHighFirrtl).transforms.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "IRToWorkingIR"

  it should "replicate the old order" in {
    val oldOrder = Seq(classOf[passes.ToWorkingIR])
    (new IRToWorkingIR).transforms.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "ResolveAndCheck"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[passes.CheckHighForm],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.Uniquify],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ResolveGenders],
      classOf[passes.CheckGenders],
      classOf[passes.InferWidths],
      classOf[passes.CheckWidths] )
    (new ResolveAndCheck).transforms.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "HighFirrtlToMiddleFirrtl"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[passes.PullMuxes],
      classOf[passes.ReplaceAccesses],
      classOf[passes.ExpandConnects],
      classOf[passes.RemoveAccesses],
      classOf[passes.Uniquify],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ExpandWhensAndCheck],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ResolveGenders],
      classOf[passes.CheckGenders],
      classOf[passes.InferWidths],
      classOf[passes.CheckWidths],
      classOf[passes.ConvertFixedToSInt],
      classOf[passes.ZeroWidth],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes] )
    (new HighFirrtlToMiddleFirrtl).transforms.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "MiddleFirrtlToLowFirrtl"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[passes.LowerTypes],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ResolveGenders],
      classOf[passes.CheckGenders],
      classOf[passes.InferWidths],
      classOf[passes.CheckWidths],
      classOf[passes.Legalize],
      classOf[firrtl.transforms.RemoveReset],
      classOf[firrtl.transforms.CheckCombLoops],
      classOf[firrtl.transforms.RemoveWires] )
    (new MiddleFirrtlToLowFirrtl).transforms.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "MinimumLowFirrtlOptimization"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[passes.RemoveValidIf],
      classOf[passes.Legalize],
      classOf[passes.memlib.VerilogMemDelays],
      classOf[passes.SplitExpressions] )
    (new MinimumLowFirrtlOptimization).transforms.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "LowFirrtlOptimization"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[passes.RemoveValidIf],
      classOf[passes.Legalize],
      classOf[firrtl.transforms.ConstantPropagation],
      classOf[passes.PadWidths],
      classOf[passes.Legalize],
      classOf[firrtl.transforms.ConstantPropagation],
      classOf[passes.memlib.VerilogMemDelays],
      classOf[firrtl.transforms.ConstantPropagation],
      classOf[passes.SplitExpressions],
      classOf[firrtl.transforms.CombineCats],
      classOf[passes.CommonSubexpressionElimination],
      classOf[firrtl.transforms.DeadCodeElimination] )
    (new LowFirrtlOptimization).transforms.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "VerilogMinimumOptimized"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[firrtl.transforms.BlackBoxSourceHelper],
      classOf[firrtl.transforms.ReplaceTruncatingArithmetic],
      classOf[firrtl.transforms.FlattenRegUpdate],
      classOf[passes.VerilogModulusCleanup],
      classOf[firrtl.transforms.VerilogRename],
      classOf[passes.VerilogPrep],
      classOf[firrtl.AddDescriptionNodes] )

    (new TransformManager(Forms.VerilogMinimumOptimized, Forms.LowFormMinimumOptimized))
      .flattenedTransformOrder
      .map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "VerilogOptimized"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[firrtl.transforms.BlackBoxSourceHelper],
      classOf[firrtl.transforms.ReplaceTruncatingArithmetic],
      classOf[firrtl.transforms.FlattenRegUpdate],
      classOf[firrtl.transforms.DeadCodeElimination],
      classOf[passes.VerilogModulusCleanup],
      classOf[firrtl.transforms.VerilogRename],
      classOf[passes.VerilogPrep],
      classOf[firrtl.AddDescriptionNodes] )

    (new TransformManager(Forms.VerilogOptimized, Forms.LowFormOptimized))
      .flattenedTransformOrder
      .map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

  behavior of "Chirrtl to Verilog"

  it should "replicate the old order" in {
    val oldOrder = Seq(
      classOf[passes.CheckChirrtl],
      classOf[passes.CInferTypes],
      classOf[passes.CInferMDir],
      classOf[passes.RemoveCHIRRTL],

      classOf[passes.ToWorkingIR],

      classOf[passes.CheckHighForm],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.Uniquify],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ResolveGenders],
      classOf[passes.CheckGenders],
      classOf[passes.InferWidths],
      classOf[passes.CheckWidths],

      classOf[firrtl.transforms.DedupModules],

      classOf[passes.PullMuxes],
      classOf[passes.ReplaceAccesses],
      classOf[passes.ExpandConnects],
      classOf[passes.RemoveAccesses],
      classOf[passes.Uniquify],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ExpandWhensAndCheck],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ResolveGenders],
      classOf[passes.CheckGenders],
      classOf[passes.InferWidths],
      classOf[passes.CheckWidths],
      classOf[passes.ConvertFixedToSInt],
      classOf[passes.ZeroWidth],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],

      classOf[passes.LowerTypes],
      classOf[passes.ResolveKinds],
      classOf[passes.InferTypes],
      classOf[passes.CheckTypes],
      classOf[passes.ResolveGenders],
      classOf[passes.CheckGenders],
      classOf[passes.InferWidths],
      classOf[passes.CheckWidths],
      classOf[passes.Legalize],
      classOf[firrtl.transforms.RemoveReset],
      classOf[firrtl.transforms.CheckCombLoops],
      classOf[firrtl.transforms.RemoveWires],

      classOf[passes.RemoveValidIf],
      classOf[passes.Legalize],
      classOf[firrtl.transforms.ConstantPropagation],
      classOf[passes.PadWidths],
      classOf[passes.Legalize],
      classOf[firrtl.transforms.ConstantPropagation],
      classOf[passes.memlib.VerilogMemDelays],
      classOf[firrtl.transforms.ConstantPropagation],
      classOf[passes.SplitExpressions],
      classOf[firrtl.transforms.CombineCats],
      classOf[passes.CommonSubexpressionElimination],
      classOf[firrtl.transforms.DeadCodeElimination],

      classOf[firrtl.VerilogEmitter] )

    (new TransformManager(Set(classOf[firrtl.VerilogEmitter]).asInstanceOf[Set[Class[Transform]]], Forms.ChirrtlForm))
      .flattenedTransformOrder.map(_.getClass.asInstanceOf[Class[Transform]]) should be (oldOrder)
  }

}
