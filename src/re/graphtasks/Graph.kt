package re.graphtasks

import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

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
        File(path).useLines { lines ->
            lines.forEach { lineList.add(it) }
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

    fun addEdge(from: Int, to: Int) {
        if (adjacencyList[from]!!.any { x -> x.to == to }) return

        adjacencyList[from]!!.add(Edge(from, to))
    }

    fun removeEdge(from: Int, to: Int) {
        if (!adjacencyList[from]!!.any { x -> x.to == to }) return

        adjacencyList[from]!!.removeIf { x -> x.from == from && x.to == to }
    }

    fun outcomeDegree(data: Int) : Int {
        return adjacencyList[data]!!.size
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

