package re.graphtasks

import java.io.File
import kotlin.streams.asSequence

class Graph {

    private data class Edge(val from: Int, val to: Int, val weight: Int = 0)

    private var adjacencyList: MutableMap<Int, MutableList<Edge>> = mutableMapOf()

    public val directed: Boolean?
    public val weighted: Boolean?

    constructor(count: Int = 3) {
        directed = false
        weighted = false
        for (i in 1..count) {
            adjacencyList.put(
                    i,
                    (1..count)
                            .filter { x -> x != i }
                            .map { Edge(i, it) }
                            .toMutableList()
            )
        }
    }

    private constructor(adjacencyList: MutableMap<Int, MutableList<Edge>>, directed: Boolean = false, weighted: Boolean = false) {
        this.adjacencyList = adjacencyList
        this.directed = directed
        this.weighted = weighted
    }

    constructor(from: Graph) {
        adjacencyList = from.adjacencyList.toMutableMap()
        directed = from.directed
        weighted = from.weighted
    }

    constructor(path: String, directed: Boolean = false, weighted: Boolean = false) {
        val lineList = File(path).bufferedReader().lines().asSequence().toMutableList()
        val size = lineList.count()
        this.directed = directed
        this.weighted = weighted

        (0 until size)
                .map { i -> lineList[i].split(' ').map { it.toInt() } }
                .forEach { items ->
                    adjacencyList.put(
                            items[0],
                            if (weighted)
                                (1 until items.size step 2)
                                        .map { Edge(items[0], items[it], items[it + 1]) }
                                        .toMutableList()
                            else
                                items
                                        .subList(1, items.size)
                                        .map { Edge(items[0], it) }
                                        .toMutableList()
                    )
                }
    }

    private fun getAdjacentVerticesOf(vertex: Int) : MutableList<Int> = adjacencyList[vertex]!!.map { x -> x.to }.toMutableList()

    fun addVertex(data: Int) {
        if (adjacencyList.any { x -> x.key == data }) return

        adjacencyList.put(data, mutableListOf())
    }

    fun removeVertex(data: Int) {
        if (!adjacencyList.any { x -> x.key == data }) return

        adjacencyList.remove(data)
        adjacencyList.values.forEach {
            it.removeIf { x -> x.to == data }
        }
    }

    fun addEdge(from: Int, to: Int, weight: Int = 0) {
        if (adjacencyList[from]!!.any { x -> x.to == to }) return

        if (directed!!) {
            adjacencyList[from]!!.add(Edge(from, to, weight))
        } else {
            adjacencyList[from]!!.add(Edge(from, to, weight))
            if (!adjacencyList[to]!!.any { x -> x.to == from }) {
                adjacencyList[to]!!.add(Edge(to, from, weight))
            }
        }
    }

    fun removeEdge(from: Int, to: Int) {
        if (!adjacencyList[from]!!.any { x -> x.to == to }) return

        if (directed!!) {
            adjacencyList[from]!!.removeIf { x -> x.to == to }
        } else {
            adjacencyList[from]!!.removeIf { x -> x.to == to }
            if (adjacencyList[to]!!.any { x -> x.to == from }) {
                adjacencyList[to]!!.removeIf { x -> x.to == from }
            }
        }
    }

    fun outdegreeOf(vertex: Int) : Int = adjacencyList[vertex]!!.size

    fun indegreeOf(vertex: Int) : Int = adjacencyList.values.map { it.count { x -> x.to == vertex } }.sum()

    fun bfs(from: Int, handler: (Int) -> Unit) {
        if (!adjacencyList.containsKey(from)) return

        val used: MutableSet<Int> = mutableSetOf()
        val queue: Queue<Int> = Queue()

        used.add(from)
        queue.push(from)

        while (!queue.isEmpty()) {
            val current = queue.pop()
            handler(current)

            for (vertex in getAdjacentVerticesOf(current)) {
                if (!used.contains(vertex)) {
                    queue.push(vertex)
                    used.add(vertex)
                }
            }
        }
    }

    fun dfs(from: Int, handler: (Int) -> Unit) {
        if (!adjacencyList.containsKey(from)) return

        val used: MutableSet<Int> = mutableSetOf()
        val stack: Stack<Int> = Stack()

        used.add(from)
        stack.push(from)

        while (!stack.isEmpty()) {
            val current = stack.pop()
            handler(current)

            val adjacentVertices = getAdjacentVerticesOf(current).filter { x -> !used.contains(x) }
            if (!adjacentVertices.isEmpty()) {
                used.add(adjacentVertices.first())
                stack.push(adjacentVertices.first())
            }
        }
    }

    fun getGraphWithRemovedEdgesBetweenOddVertices() : Graph {
        val result = Graph(this)

        result.adjacencyList.forEach {
            it.value.removeIf { x -> x.to % 2 == 1 && x.from % 2 == 1 }
        }

        return result
    }

    fun join(graph: Graph) {
        graph.adjacencyList.forEach {
            if (adjacencyList.containsKey(it.key)) {
                adjacencyList[it.key]!!
                        .addAll(
                                graph.adjacencyList[it.key]!!
                                        .filter {
                                            x -> adjacencyList[it.key]!!.none { y -> y.to == x.to }
                                        }
                        )
                adjacencyList[it.key]!!.sortBy { x -> x.to }
            } else {
                adjacencyList.put(it.key, it.value)
            }
        }
    }

    fun union(graph: Graph) : Graph {
        val result = Graph(this)

        graph.adjacencyList.forEach {
            if (result.adjacencyList.containsKey(it.key)) {
                result.adjacencyList[it.key]!!
                        .addAll(
                                graph.adjacencyList[it.key]!!
                                        .filter {
                                            x -> result.adjacencyList[it.key]!!.none { y -> y.to == x.to }
                                        }
                        )
                result.adjacencyList[it.key]!!.sortBy { x -> x.to }
            } else {
                result.adjacencyList.put(it.key, it.value)
            }
        }

        return result
    }

    fun intersect(graph: Graph) : Graph {
        val result = Graph()

        val maxSizedGraph = if (this.adjacencyList.size >= graph.adjacencyList.size) this else graph
        val minSizedGraph = if (this.adjacencyList.size < graph.adjacencyList.size) this else graph

        maxSizedGraph.adjacencyList.forEach {
            if (minSizedGraph.adjacencyList.containsKey(it.key)) {
                result.adjacencyList.put(
                        it.key,
                        it.value.intersect(minSizedGraph.adjacencyList[it.key]!!)
                                .toMutableList())
            }
        }

        return result
    }

    fun getInvertedGraph() : Graph {
        val edges: MutableList<Edge> = mutableListOf()
        this.adjacencyList.values.forEach { edges.addAll(it) }

        val invertedEdges = edges.map { Edge(it.to, it.from, it.weight) }.sortedBy { x -> x.from }
        val adjacencyList: MutableMap<Int, MutableList<Edge>> = mutableMapOf()
        invertedEdges.forEach {
            if (!adjacencyList.containsKey(it.from)) {
                adjacencyList.put(
                        it.from,
                        invertedEdges.filter { x -> x.from == it.from }.toMutableList()
                )
            }
        }

        return Graph(adjacencyList, this.directed!!, this.weighted!!)
    }

    fun show() {
        println("Adjacency list:")
        adjacencyList.forEach { x ->
            print(x.key.toString() + " - ")
            x.value.forEach {
                print(it.to.toString() + " " + if (weighted!!) "with ${it.weight}; " else "")
            }
            println()
        }
    }

}
