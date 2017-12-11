package re.graphtasks

fun main(args: Array<String>) {
    val graph = Graph("directedGraph.txt")
    graph.show()
    graph.getInvertedGraph().show()
}
