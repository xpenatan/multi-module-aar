import java.util.*
import kotlin.collections.HashMap

class AARProjectDependency {

    val dependency = HashMap<String, TreeSet<String>>()

    fun addProject(module: String, subModule: String) {
        var set = dependency[module]
        if(set == null) {
            set = TreeSet()
            dependency[module] = set
        }
        set.add(subModule)
    }
}