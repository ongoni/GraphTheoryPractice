package re.graphtasks

import javafx.application.Application
import re.graphtasks.visualization.GraphVisualizationApp

fun main(args: Array<String>) {
    val graph = Graph("graph examples/flow.txt", directed = true, weighted = true)
//    graph.bfs(1, { print(it.toString() + " ") })
//    println()

//    Application.launch(GraphVisualizationApp::class.java, *args)

//    graph.floydDistanceBetween(1, 4)
//    graph.floydDistanceBetween(1, 8)

//    graph.fordBellman(1, 8, 4)

    println(graph.fordFulkerson(1, 8))
}
