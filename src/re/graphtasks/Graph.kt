package re.graphtasks

import java.io.File

class Graph {

    private data class Edge(val from: Int, val to: Int, val weight: Double = 0.0)

    private var adjacencyList: MutableMap<Int, MutableList<Edge>> = mutableMapOf()

    constructor(count: Int = 3) {
        for (i in 1..count) {
            adjacencyList.put(i, (1..count)
                            .filter { x -> x != i }
                            .map { Edge(i, it) }
                            .toMutableList())
        }
    }

    constructor(from: Graph) {
        adjacencyList = from.adjacencyList.toMutableMap()
    }

    constructor(path: String) {
        val lineList = mutableListOf<String>()
        File(path).useLines {
            lines -> lines.forEach {
                lineList.add(it)
            }
        }

        val size = lineList[0].toInt()
        lineList.removeAt(0)

        (0 until size)
                .map { i -> lineList[i].split(' ').map { it.toInt() } }
                .forEach { items ->
                    adjacencyList.put(items[0], items.subList(1, items.size)
                            .map { Edge(items[0], it) }.toMutableList())
                }
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

    fun addEdge(from: Int, to: Int, weight: Double = 0.0, directed: Boolean = false) {
        if (adjacencyList[from]!!.any { x -> x.to == to }) return

        if (directed) {
            adjacencyList[from]!!.add(Edge(from, to, weight))
        } else {
            adjacencyList[from]!!.add(Edge(from, to, weight))
            if (!adjacencyList[to]!!.any { x -> x.to == from }) {
                adjacencyList[to]!!.add(Edge(to, from, weight))
            }
        }
    }

    fun removeEdge(from: Int, to: Int, directed: Boolean = false) {
        if (!adjacencyList[from]!!.any { x -> x.to == to }) return

        if (directed) {
            adjacencyList[from]!!.removeIf { x -> x.to == to }
        } else {
            adjacencyList[from]!!.removeIf { x -> x.to == to }
            if (adjacencyList[to]!!.any { x -> x.to == from }) {
                adjacencyList[to]!!.removeIf { x -> x.to == from }
            }
        }
    }

    private fun getAdjacentVerticesOf(vertex: Int) : MutableList<Int> {
        return adjacencyList[vertex]!!.map { x -> x.to }.toMutableList()
    }

    fun outdegreeOf(vertex: Int) : Int {
        return adjacencyList[vertex]!!.size
    }

    fun indegreeOf(vertex: Int) : Int {
        return adjacencyList.values.map { it.count { x -> x.to == vertex } }.sum()
    }

    fun bfs(from: Int) {
        if (!adjacencyList.containsKey(from)) return

        val used: MutableSet<Int> = mutableSetOf()
        val queue: Queue<Int> = Queue()

        used.add(from)
        queue.push(from)
        print(from.toString() + " ")

        while (!queue.isEmpty()) {
            val current = queue.pop()

            for (vertex in getAdjacentVerticesOf(current)) {
                if (!used.contains(vertex)) {
                    queue.push(vertex)
                    used.add(vertex)

                    print(vertex.toString() + " ")
                }
            }
        }
        println()
    }

    fun dfs(from: Int) {
        if (!adjacencyList.containsKey(from)) return

        val used: MutableSet<Int> = mutableSetOf()
        val stack: Stack<Int> = Stack()

        used.add(from)
        stack.push(from)
        print(from.toString() + " ")

        while (!stack.isEmpty()) {
            val current = stack.pop()

            val adjacentVertices = getAdjacentVerticesOf(current).filter { x -> !used.contains(x) }
            if (!adjacentVertices.isEmpty()) {
                used.add(adjacentVertices.first())
                stack.push(adjacentVertices.first())

                print(adjacentVertices.first().toString() + " ")
            }
        }
        println()
    }

    fun show() {
        println("Adjacency list:")
        adjacencyList.forEach { x ->
            print(x.key.toString() + " - ")
            x.value.forEach {
                print(it.to.toString() + " ")
            }
            println()
        }
    }

}

