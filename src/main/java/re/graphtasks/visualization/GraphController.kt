package re.graphtasks.visualization

import re.graphtasks.Graph
import tornadofx.Controller

class GraphController: Controller() {

    val graph: Graph?

    init {
        print("enter path: ")
        val path = readLine()!!
        print("is this graph directed? ")
        val directed = readLine()!!.toBoolean()
        print("is this graph weighted? ")
        val weighted = readLine()!!.toBoolean()
        graph = Graph(path, directed, weighted)
    }

}