package quanto.gui.graphview

import java.awt.geom.{Rectangle2D, Ellipse2D, Point2D}
import java.awt.{FontMetrics, Color, Shape}
import math._
import quanto.data._
import quanto.gui._

case class VDisplay(shape: Shape, color: Color, label: Option[LabelDisplayData]) {
  def pointHit(pt: Point2D) = shape.contains(pt)
  def rectHit(r: Rectangle2D) = shape.intersects(r)
}

trait VertexDisplayData { self: GraphView =>

  val vertexDisplay = collection.mutable.Map[VName,VDisplay]()

  // returns the contact point at the given angle, in graph coordinates
  protected def vertexContactPoint(vn: VName, angle: Double): (Double,Double) = {
    // TODO: replace this with proper boundary detection
    val c = graph.vdata(vn).coord

    vertexDisplay(vn).shape match {
      case _: Ellipse2D => (c._1 + GraphView.NodeRadius * cos(angle), c._2 + GraphView.NodeRadius * sin(angle))
      case r: Rectangle2D =>
        val chopX = (trans scaleFromScreen r.getWidth) / 2 + 0.01
        val chopY = (trans scaleFromScreen r.getHeight) / 2 + 0.01
        val tryRad = max(chopX, chopY)

        val rad = if (abs(tryRad * cos(angle)) > chopX) {
          abs(chopX / cos(angle))
        } else if (abs(tryRad * sin(angle)) > chopY) {
          abs(chopY / sin(angle))
        } else {
          tryRad
        }

        (c._1 + rad * cos(angle), c._2 + rad * sin(angle))
    }
  }

  protected def computeVertexDisplay() {
    val trWireWidth = 0.707 * (trans scaleToScreen GraphView.WireRadius)

    for ((v,data) <- graph.vdata if !vertexDisplay.contains(v)) {
      val (x,y) = trans toScreen data.coord

      vertexDisplay(v) = data match {
        case vertexData : NodeV =>
          val style = vertexData.typeInfo.style
          val text = vertexData.typeInfo.value.typ match {
            case Theory.ValueType.String => vertexData.value
            case _ => ""
          }

          val fm = peer.getGraphics.getFontMetrics(GraphView.VertexLabelFont)
          val labelDisplay = LabelDisplayData(
            text, (x,y), fm,
            vertexData.typeInfo.style.labelForegroundColor,
            vertexData.typeInfo.style.labelBackgroundColor)

          val shape = style.shape match {
            case Theory.VertexShape.Rectangle =>
              new Rectangle2D.Double(
                labelDisplay.bounds.getMinX - 5.0, labelDisplay.bounds.getMinY - 3.0,
                labelDisplay.bounds.getWidth + 10.0, labelDisplay.bounds.getHeight + 6.0)
            case Theory.VertexShape.Circle =>
              // TODO: fix ellipse case
              new Ellipse2D.Double(
                labelDisplay.bounds.getMinX - 3.0, labelDisplay.bounds.getMinY - 3.0,
                labelDisplay.bounds.getWidth + 6.0, labelDisplay.bounds.getHeight + 6.0)
            case _ => throw new Exception("Shape not supported yet")
          }

          VDisplay(shape, style.fillColor, Some(labelDisplay))
        case _: WireV =>
          VDisplay(
            new Rectangle2D.Double(
              x - trWireWidth, y - trWireWidth,
              2.0 * trWireWidth, 2.0 * trWireWidth),
            Color.GRAY,None)
      }
    }
  }

  protected def boundsForVertexSet(vset: Set[VName]) = {
    var init = false
    var ulx,uly,lrx,lry = 0.0

    vset.foreach { v =>
      val rect = vertexDisplay(v).shape.getBounds
      if (init) {
        ulx = min(ulx, rect.getX)
        uly = min(uly, rect.getY)
        lrx = max(lrx, rect.getMaxX)
        lry = max(lry, rect.getMaxY)
      } else {
        ulx = rect.getX
        uly = rect.getY
        lrx = rect.getMaxX
        lry = rect.getMaxY
        init = true
      }
    }
    
    new Rectangle2D.Double(ulx, uly, lrx - ulx, lry - uly)
  }

  def invalidateAllVerts() { vertexDisplay.clear() }
  def invalidateVertex(n: VName) = vertexDisplay -= n
}