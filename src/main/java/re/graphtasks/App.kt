package re.graphtasks

import javafx.application.Application
import re.graphtasks.visualization.GraphController
import re.graphtasks.visualization.GraphVisualizationApp

fun main(args: Array<String>) {
//    val graph = Graph("graph examples/primGraph.txt", directed = true, weighted = true)
//    graph.show()
//    graph.getInvertedGraph().show()
//    graph.prim(1).show()
//    graph.dijkstra(1)
    val graph = Graph("graph examples/kosarajuGraph.txt", directed = true, weighted = false)
//    graph.show()
//    graph.kosaraju()
    val controller: GraphController = GraphController()
    controller.graph = graph
    Application.launch(GraphVisualizationApp::class.java, *args)
}
