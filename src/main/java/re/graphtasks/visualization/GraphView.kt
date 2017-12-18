package re.graphtasks.visualization

import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class GraphView: View() {

    val controller: GraphController by inject()

    private fun rotate(center: Pair<Double, Double>, point: Pair<Double, Double>, angle: Double) : Pair<Double, Double> {
        return Pair(
                center.first + (point.first - center.first) * Math.cos(Math.toRadians(angle))
                        - (point.second - center.second) * Math.sin(Math.toRadians(angle)),
                center.second + (point.second - center.second) * Math.cos(Math.toRadians(angle))
                        + (point.first - center.first) * Math.sin(Math.toRadians(angle))
        )

    }

    override val root = stackpane {
        val graph = controller.graph
        val count = graph.size()
        val angle = 360.0 / count
        val center = Pair(0.0, 0.0)
        val pointRadius = 30.0
        val pointColor = Color.DARKGRAY
        var prevPoint = Pair(300.0, 0.0)
        val vertices = graph.getVertices().toMutableList()

        group {
            circle {
                centerX = prevPoint.first
                centerY = prevPoint.second
                radius = pointRadius
                fill = pointColor
            }
            text(vertices[0].toString()) {
                fill = Color.BLACK
                x = prevPoint.first - 8
                y = prevPoint.second + 10
                font = Font(30.0)
            }

            for (i in 2..count) {
                val newPoint = rotate(center, prevPoint, angle)
                circle {
                    centerX = newPoint.first
                    centerY = newPoint.second
                    radius = pointRadius
                    fill = pointColor
                }
                text(vertices[i - 1].toString()) {
                    fill = Color.BLACK
                    x = newPoint.first - 8
                    y = newPoint.second + 10
                    font = Font(30.0)
                }
                prevPoint = newPoint
            }
        }
    }

}