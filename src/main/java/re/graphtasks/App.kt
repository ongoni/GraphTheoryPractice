package re.graphtasks

import javafx.application.Application
import re.graphtasks.visualization.GraphVisualizationApp

fun main(args: Array<String>) {
    val graph = Graph("graph examples/primGraph.txt", directed = false, weighted = true)
//    graph.bfs(1, { print(it.toString() + " ") })
//    println()

//    Application.launch(GraphVisualizationApp::class.java, *args)

//    val pair = graph.floydWarshall()
//    graph.getPath(1, 4, pair.first, pair.second, { x -> print(x.toString() + " ") })

    graph.fordBellman(1, 8, 4)
}
