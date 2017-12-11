package re.graphtasks.collections

class Queue<T> {

    private var items: MutableList<T> = mutableListOf()

    fun push(value: T) {
        items.add(value)
    }

    fun top(): T? {
        return items.firstOrNull()
    }

    fun pop(): T {
        val item = items.first()
        items.removeAt(0)
        return item
    }

    fun isEmpty() : Boolean {
        return items.isEmpty()
    }

}