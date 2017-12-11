package re.graphtasks.collections

class Stack<T> {

    private var items: MutableList<T> = mutableListOf()

    fun push(value: T) {
        items.add(value)
    }

    fun top(): T? {
        return items.lastOrNull()
    }

    fun pop(): T {
        val item = items.last()
        items.removeAt(items.lastIndex)
        return item
    }

    fun isEmpty() : Boolean {
        return items.isEmpty()
    }

}