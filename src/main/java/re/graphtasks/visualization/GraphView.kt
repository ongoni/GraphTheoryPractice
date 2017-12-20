package re.graphtasks.visualization

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.util.Duration
import tornadofx.*

class GraphView: View() {

    private val controller: GraphController by inject()

    private fun rotate(center: Pair<Double, Double>, point: Pair<Double, Double>, angle: Double): Pair<Double, Double> {
        return Pair(
                center.first + (point.first - center.first) * Math.cos(Math.toRadians(angle))
                        - (point.second - center.second) * Math.sin(Math.toRadians(angle)),
                center.second + (point.second - center.second) * Math.cos(Math.toRadians(angle))
                        + (point.first - center.first) * Math.sin(Math.toRadians(angle))
        )
    }

    override val root = stackpane {
        val graph = controller.graph
        val angle = 360.0 / graph!!.size()
        val center = Pair(0.0, 0.0)
        val pointRadius = 30.0
        val pointColor = Color.DARKGRAY
        val vertices = graph.getVertices().toMutableList()
        val vertexList = mutableListOf<Vertex>()

        var prevPoint = Pair(300.0, 0.0)
        vertexList.add(Vertex(vertices.first(), prevPoint.first, prevPoint.second))

        vertices.subList(1, vertices.size).forEach {
            val newPoint = rotate(center, prevPoint, angle)
            vertexList.add(Vertex(it, newPoint.first, newPoint.second))
            prevPoint = newPoint
        }

        group {
            for (edge in graph.getEdges()) {
                val start = vertexList.first { x -> x.id == edge.from }
                val end = vertexList.first { x -> x.id == edge.to }

                line {
                    startX = start.x
                    startY = start.y
                    endX = end.x
                    endY = end.y
                    strokeWidth = 5.0
                }
            }

            for (vertex in vertexList) {
                circle {
                    centerX = vertex.x
                    centerY = vertex.y
                    radius = pointRadius
                    fill = pointColor
                }
                text(vertex.id.toString()) {
                    fill = Color.BLACK
                    x = vertex.x - 8
                    y = vertex.y + 10
                    font = Font(30.0)
                }
            }
        }

        val orderedVertices = mutableListOf<Vertex>()
        graph.dfs(vertices.first(), { orderedVertices.add(vertexList.first { x -> x.id == it }) })

        println()
    }
}
