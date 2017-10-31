package re.graphtasks

fun main(args: Array<String>) {
//    val graph = Graph("graphInput.txt")
    val graph = Graph("directedGraph.txt", directed = true)
    val graph2 = Graph(4)
    graph.show()
    graph2.show()
}
