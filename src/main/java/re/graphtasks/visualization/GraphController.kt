package re.graphtasks.visualization

import re.graphtasks.Graph
import tornadofx.Controller

class GraphController: Controller() {

    val graph: Graph?
    val runAnimation: Boolean?

    init {
        print("load default graph? ")
        val answer = readLine()!!
        graph = when(answer) {
            "yes" -> Graph("graph examples/primGraph.txt", directed = false, weighted = true)
            else -> {
                print("enter path: graph examples/")
                val path = "graph examples/" + readLine()!!
                print("directed? ")
                val directed = readLine()!!.toBoolean()
                print("weighted? ")
                val weighted = readLine()!!.toBoolean()
                Graph(path, directed, weighted)
            }
        }

        print("animate? ")
        runAnimation = when(readLine()!!) {
            "no" -> false
            else -> true
        }
    }

}