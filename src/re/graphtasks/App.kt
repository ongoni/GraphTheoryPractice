package re.graphtasks

fun main(args: Array<String>) {
    val graph = Graph("graphInput.txt")
    val graph2 = Graph(4)
    println(graph.outdegreeOf(1) / 2.0)
    println(graph.indegreeOf(3))
    graph.bfs(1)
    graph.dfs(1)

    val intersection = graph.intersect(graph2)
    val union = graph.union(graph2)
    graph.join(graph2)

    intersection.show()
    union.show()
    graph.show()
}
