package re.graphtasks

fun main(args: Array<String>) {
    val graph = Graph("graph examples/primGraph.txt", directed = true, weighted = true)
    graph.show()
//    graph.getInvertedGraph().show()
    graph.prim(1).show()
//    graph.dijkstra(1)
}
