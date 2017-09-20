package re.graphtasks

fun main(args: Array<String>) {
    val graph = Graph("graphInput.txt")
    println(graph.outdegreeOf(1) / 2.0)
    println(graph.indegreeOf(3))
    graph.bfs(1)
    graph.dfs(1)

    val graph2 = Graph(4)
    val union = graph.union(graph2)
    graph.join(graph2)

    graph.show()
    union.show()

}
