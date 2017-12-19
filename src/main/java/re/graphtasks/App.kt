package re.graphtasks

import javafx.application.Application
import re.graphtasks.visualization.GraphVisualizationApp

fun main(args: Array<String>) {
    val graph = Graph("graph examples/primGraph.txt", directed = true, weighted = true)
//    graph.prim(1).show()

//    println(graph.getEccentricity(3))
//    println(graph.getRadius())
//    for (item in graph.getCenter()) print(item.toString() + " ")

//    graph.fordBellman(1, 8, 5)

    Application.launch(GraphVisualizationApp::class.java, *args)
}
