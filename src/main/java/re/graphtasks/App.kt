package re.graphtasks

fun main(args: Array<String>) {
    val graph = Graph("weightedGraph.txt", directed = true, weighted = true)
    graph.show()
    graph.getInvertedGraph().show()
}
