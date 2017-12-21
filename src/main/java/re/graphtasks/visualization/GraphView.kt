package re.graphtasks.visualization

import javafx.animation.SequentialTransition
import javafx.animation.Timeline
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.util.Duration
import re.graphtasks.Graph
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
                if (graph.weighted!!) {
                    text(edge.weight.toString()) {
                        fill = Color.ROYALBLUE
                        x = (start.x + end.x - 10) / 2
                        y = (start.y + end.y - 10) / 2
                        font = Font(30.0)
                    }
                }
            }

            for (vertex in vertexList) {
                circle {
                    centerX = vertex.x
                    centerY = vertex.y
                    radius = pointRadius
                    fill = pointColor
                    effect = DropShadow()
                }
                text(vertex.id.toString()) {
                    fill = Color.BLACK
                    x = vertex.x - 8
                    y = vertex.y + 10
                    font = Font(30.0)
                }
            }

            if (controller.runAnimation!!) {
                val orderedEdgesWithCoords = mutableListOf<Edge>()
                val orderedEdges = graph.getPrimOrderedEdges(1)
                orderedEdges.mapTo(orderedEdgesWithCoords) {
                    Edge(
                            edge = it,
                            start = vertexList.first { x -> x.id == it.from },
                            end = vertexList.first { x -> x.id == it.to }
                    )
                }
                var timer = 0.0
                val timelines = mutableListOf<Timeline>()
                for (edge in orderedEdgesWithCoords) {
                    val line = line {
                        startX = edge.start.x
                        startY = edge.start.y
                        endX = edge.start.x
                        endY = edge.start.y
                        stroke = Color.ORANGERED
                        strokeWidth = 6.0
                    }
                    timelines.add(
                            timeline {
                                keyframe(Duration.seconds(2.0)) {
                                    keyvalue(line.endXProperty(), edge.end.x)
                                    keyvalue(line.endYProperty(), edge.end.y)
                                }
                                delay = Duration.seconds(timer)
                            }
                    )
                    timer += 2.0
                }
                val transition = SequentialTransition()
                timelines.forEach {
                    transition.children.add(it)
                }

                transition.playFromStart()
            }
        }
    }
}
