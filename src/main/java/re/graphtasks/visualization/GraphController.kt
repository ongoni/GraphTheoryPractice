package re.graphtasks.visualization

import re.graphtasks.Graph
import tornadofx.Controller

class GraphController: Controller() {

    var graph: Graph = Graph("graph examples/graphInput.txt")

}