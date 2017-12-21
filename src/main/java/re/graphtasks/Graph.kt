package re.graphtasks

import re.graphtasks.collections.Queue
import re.graphtasks.collections.Stack
import re.graphtasks.exceptions.NotWeightedGraphGivenException
import java.io.File
import java.lang.Double.POSITIVE_INFINITY
import java.util.*
import kotlin.Comparator
import kotlin.streams.asSequence

class Graph {

    data class Edge(val from: Int, val to: Int, val weight: Int = 0)

    private var adjacencyList: MutableMap<Int, MutableList<Edge>> = mutableMapOf()

    public val directed: Boolean?
    public val weighted: Boolean?

    private var used = mutableSetOf<Int>()
    private var outTimeOrder = mutableMapOf<Int, Int>()
    private var timer = 0

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

    private constructor(adjacencyList: MutableMap<Int, MutableList<Edge>>, directed: Boolean = false,
                        weighted: Boolean = false) {
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
                            if (items.size != 1)
                                if (weighted)
                                    (1 until items.size step 2)
                                            .map { Edge(items[0], items[it], items[it + 1]) }
                                            .toMutableList()
                                else
                                    items
                                            .subList(1, items.size)
                                            .map { Edge(items[0], it) }
                                            .toMutableList()
                            else
                                mutableListOf()
                    )
                }
    }

    private fun getAdjacentVerticesOf(vertex: Int): MutableList<Int>
            = adjacencyList[vertex]!!.map { x -> x.to }.toMutableList()

    private fun getAdjacentVerticesInInvertedGraph(vertex: Int) : MutableList<Int>
            = this.inverted().adjacencyList[vertex]!!.map { x -> x.to }.toMutableList()

    private fun sortByKey(): Graph {
        adjacencyList = adjacencyList.toSortedMap(Comparator<Int> { v1, v2 ->
            v1 - v2
        })

        return this
    }

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

    fun getVertices() = adjacencyList.keys

    fun getEdges() : MutableList<Edge> {
        val result = mutableListOf<Edge>()

        adjacencyList.values.forEach {
            result.addAll(it)
        }

        return result
    }

    fun getAdjacencyList() = adjacencyList

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
//        if (!adjacencyList.containsKey(from)) throw InvalidArgumentException()

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

    fun getDfsOrder(from: Int) : MutableList<Int> {
        val order = mutableListOf<Int>()

        recursiveDfs(from, { order.add(it) })

        return order
    }

    fun getBfsOrder(from: Int) : MutableList<Int> {
        val order = mutableListOf<Int>()

        bfs(from, { order.add(it) })

        return order
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
                adjacencyList[it.key]!!.addAll(
                        graph.adjacencyList[it.key]!!.filter {
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
                result.adjacencyList[it.key]!!.addAll(
                        graph.adjacencyList[it.key]!!.filter {
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

    fun inverted() : Graph {
        val edges: MutableList<Edge> = mutableListOf()
        this.adjacencyList.values.forEach { edges.addAll(it) }

        val invertedEdges = edges.map { Edge(it.to, it.from, it.weight) }.sortedBy { x -> x.from }.toMutableList()
        val adjacencyList: MutableMap<Int, MutableList<Edge>> = mutableMapOf()
        invertedEdges.forEach {
            if (!adjacencyList.containsKey(it.from)) {
                adjacencyList.put(
                        it.from,
                        invertedEdges.filter { x -> x.from == it.from }.toMutableList()
                )
            }
        }
        this.adjacencyList.keys.filter { x -> !adjacencyList.containsKey(x) }.forEach {
            adjacencyList.put(it, mutableListOf())
        }

        return Graph(adjacencyList, this.directed!!, this.weighted!!)
    }

    fun kosaraju() {
        val order = getOutTimeOrder()
        order.reverse()
        used = mutableSetOf()

        println("Components:")
        order.forEach {
            if (!used.contains(it)) {
                recursiveDfsForInvertedGraph(it, { x -> print(x.toString() + " ") })
                println()
            }
        }
    }

    private fun getOutTimeOrder() : MutableList<Int> {
        adjacencyList.keys.forEach {
            if (!used.contains(it)) recursiveDfs(it, {  })
        }

        return outTimeOrder.keys.toMutableList()
    }

    fun recursiveDfs(from: Int, handler: (Int) -> Unit) {
        used.add(from)
        handler(from)

        for (u in getAdjacentVerticesOf(from)) {
            if (!used.contains(u)) {
                recursiveDfs(u, handler)
            }
        }

        outTimeOrder[from] = timer++
    }

    private fun recursiveDfsForInvertedGraph(from: Int, handler: (Int) -> Unit) {
        used.add(from)
        handler(from)

        for (u in getAdjacentVerticesInInvertedGraph(from)) {
            if (!used.contains(u)) {
                recursiveDfsForInvertedGraph(u, handler)
            }
        }
    }

    fun getPendantVertices() : MutableSet<Int> = adjacencyList.filter { x -> x.value.size == 1 }.toMutableMap().keys

    fun prim(vertex: Int): Graph {
        if (!weighted!!) throw NotWeightedGraphGivenException("Weighted graph expected.")

        val usedVertices = mutableListOf<Int>()
        val newAdjList = mutableMapOf<Int, MutableList<Edge>>()
        val edges: PriorityQueue<Edge> = PriorityQueue(Comparator<Edge> {
            e1, e2 -> e1!!.weight - e2!!.weight
        })

        newAdjList.put(vertex, mutableListOf())
        usedVertices.add(vertex)

        while (usedVertices.size != adjacencyList.keys.size) {
            adjacencyList.values.forEach {
                it.forEach {
                    if (usedVertices.contains(it.from) && !usedVertices.contains(it.to)) {
                        edges.add(it)
                    }
                }
            }

            val edge = edges.poll()
            edges.clear()

            if (newAdjList.containsKey(edge.from)) newAdjList[edge.from]!!.add(edge)
            else newAdjList.put(edge.from, mutableListOf(edge))

            usedVertices.add(edge.to)
        }

        val result = Graph(newAdjList, this.directed!!, this.weighted)
        return result.union(result.inverted()).sortByKey()
    }

    fun getPrimOrderedEdges(vertex: Int) : MutableList<Edge> {
        val orderedEdges = mutableListOf<Edge>()
        val usedVertices = mutableListOf<Int>()
        val newAdjList = mutableMapOf<Int, MutableList<Edge>>()
        val edges: PriorityQueue<Edge> = PriorityQueue(Comparator<Edge> {
            e1, e2 -> e1!!.weight - e2!!.weight
        })

        newAdjList.put(vertex, mutableListOf())
        usedVertices.add(vertex)

        while (usedVertices.size != adjacencyList.keys.size) {
            adjacencyList.values.forEach {
                it.forEach {
                    if (usedVertices.contains(it.from) && !usedVertices.contains(it.to)) {
                        edges.add(it)
                    }
                }
            }

            val edge = edges.poll()
            edges.clear()

            orderedEdges.add(edge)

            usedVertices.add(edge.to)
        }

        return orderedEdges
    }

    fun dijkstra(vertex: Int) : Pair<Array<Int>, MutableMap<Int, Int>> {
        val distances = Array(adjacencyList.keys.size, { Double.POSITIVE_INFINITY.toInt() })
        val previous = mutableMapOf<Int, Int>()
        val edgeQueue = Queue<Edge>()

        distances[vertex - 1] = 0
        previous[vertex] = vertex

        adjacencyList[vertex]!!.forEach { edgeQueue.push(it) }

        while (!edgeQueue.isEmpty()) {
            val edge = edgeQueue.pop()

            if (distances[edge.to - 1] > distances[edge.from - 1] + edge.weight) {
                distances[edge.to - 1] = distances[edge.from - 1] + edge.weight
                previous[edge.to] = edge.from

                adjacencyList[edge.to]!!.forEach {
                    edgeQueue.push(it)
                }
            }
        }

        println(distances.toList())
        println(previous.toList())

        return Pair(distances, previous)
    }

    fun recoverPath(previous: MutableMap<Int, Int>, vertex: Int, handler: (Int) -> Unit) {
        if (previous[vertex] == vertex) {
            handler(vertex)
            return
        }

        recoverPath(previous, previous[vertex]!!, handler)

        handler(vertex)
    }

    fun getEccentricity(source: Int = adjacencyList.keys.first()) : Int {
        val dijkstraPaths = dijkstra(source).second
        var eccentricity = Int.MIN_VALUE
        var longestPath = mutableListOf<Int>()

        for (u in adjacencyList.keys) {
            val path = mutableListOf<Int>()
            recoverPath(dijkstraPaths, u, { x -> path.add(x) })

            if (path.size - 1 > eccentricity) {
                eccentricity = path.size - 1
                longestPath = path
            }
        }

        for (item in longestPath) print(item.toString() + " ")
        return eccentricity
    }

    fun getRadius() : Int {
        return adjacencyList.keys
                .map { getEccentricity(it) }
                .min()
                ?: Int.MAX_VALUE
    }

    fun getCenter() : MutableList<Int> {
        val radius = getRadius()
        return adjacencyList.keys
                .filter { getEccentricity(it) == radius }
                .toMutableList()
    }

    fun fordBellman(vertex: Int, v1: Int, v2: Int){
        var count = 0
        val distances = Array(adjacencyList.keys.size, { Double.POSITIVE_INFINITY.toInt() })
        val previous = mutableMapOf<Int, Int>()

        distances[vertex - 1] = 0
        previous[vertex] = -1

        while (true) {
            var any = false
            count++

            adjacencyList.values.forEach {
                it.forEach {
                    if (distances[it.from - 1] < Double.POSITIVE_INFINITY.toInt()
                            && distances[it.to - 1] > distances[it.from - 1] + it.weight) {
                        distances[it.to - 1] = distances[it.from - 1] + it.weight
                        previous[it.to] = it.from
                        any = true
                    }
                }

            }
            if (!any || count > adjacencyList.keys.size) break
        }

        printPathForBellman(vertex, v1, distances, previous)
        printPathForBellman(vertex, v2, distances, previous)
    }

    private fun printPathForBellman(from: Int, to: Int, distances: Array<Int>, previous: MutableMap<Int, Int>) {
        if (distances[to - 1] != Double.POSITIVE_INFINITY.toInt()) {
            println("shortest path from $from to $to: ")
            val path = mutableListOf<Int>()
            var current = to

            while (current != -1) {
                path.add(current)
                current = previous[current]!!
            }

            path.reverse()
            print(path)
            println(" with summary ${distances[to - 1]}")
        } else {
            println("no path from $from to $to")
        }
    }

    fun getPath(from: Int, to: Int, distances: ArrayList<Array<Int>>, next: ArrayList<Array<Int>>,
                handler: (Int) -> Unit) {
//        if (next[from - 1][to - 1] != from - 1) {
//
//            var current = from - 1
//            while (current != to - 1) {
//                handler(current + 1)
//                current = next[current][to - 1]
//            }
//            handler(to)
//        }
    }

    fun floydWarshall() : Pair<ArrayList<Array<Int>>, ArrayList<Array<Int>>>{
        val distances = arrayListOf(Array(0, { 0 }))
        val previous = arrayListOf(Array(0, { 0 }))
        val size = adjacencyList.keys.size

        distances.clear()
        previous.clear()

        for (i in 0 until size) {
            distances.add(Array(size, { POSITIVE_INFINITY.toInt() }))
            previous.add(Array(size, { 0 }))
            distances[i][i] = 0
        }

        adjacencyList.forEach {
            it.value.forEach {
                distances[it.from - 1][it.to - 1] = it.weight
            }
        }

        for (i in 0 until size) {
            for (j in 0 until size) {
                for (k in 0 until size) {
                    if (distances[j][i] + distances[i][k] < distances[j][k] ) {
                        distances[j][k] = Math.min(distances[j][k], distances[j][i] + distances[i][k])
                        previous[j][k] = i
                    }
                }
            }
        }

        distances.forEach {
            println(it.toList())
        }

        return Pair(distances, previous)
    }

    fun size(): Int = adjacencyList.size

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
