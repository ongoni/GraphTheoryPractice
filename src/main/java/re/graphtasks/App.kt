package re.graphtasks

import javafx.application.Application
import re.graphtasks.visualization.GraphVisualizationApp

fun main(args: Array<String>) {
    val graph = Graph("graph examples/primGraph.txt", directed = true, weighted = true)
//    graph.show()
//    graph.getInvertedGraph().show()
//    graph.prim(1).show()
//    val graph = Graph("graph examples/kosarajuGraph.txt", directed = true, weighted = false)
//    graph.show()
//    graph.kosaraju()
    Application.launch(GraphVisualizationApp::class.java, *args)
//    println(graph.getEccentricity(3))
//    println(graph.getRadius())
//    for (item in graph.getCenter()) print(item.toString() + " ")
//    val pair = graph.floydWarshall()
//    graph.getPath(1, 4, pair.first, pair.second, { x -> print(x.toString() + " ") })
//    graph.fordBellman(1, 8, 5)
}
