import org.gradle.api.logging.Logger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AARProjectDependency {

    private val dependency = HashMap<String, ArrayList<ProjectDependencyNode>>()
    private var subDepUpdated = false

    fun addProject(module: String, subModule: String, type: DependencyType) {
        var array = dependency[module]
        if(array == null) {
            array = ArrayList()
            dependency[module] = array
        }
        array.add(ProjectDependencyNode(module, subModule, type))
    }

    fun contains(module: String, list: ArrayList<ProjectDependencyNode>): Boolean {
        list.forEach { value ->
            if(value.moduleName == module) {
                return true;
            }
        }
        return false;
    }

    data class ProjectDependencyNode(val parent: String, val moduleName: String, val type: DependencyType)

    fun updateSubDependency() {
        if(subDepUpdated)
            return
        subDepUpdated = true
        dependency.toList().forEach { entry ->
            val key = entry.first
            val value = entry.second
            val toMutableList = value.toMutableList()
            toMutableList.forEachIndexed { index, childDep ->
                addAllChildDependencyIN(childDep, value)
            }
        }
    }

    private fun addAllChildDependencyIN(parentDependency: ProjectDependencyNode, array: ArrayList<ProjectDependencyNode>) {
        if(parentDependency.type == DependencyType.DEP) {
            val depKey = parentDependency.moduleName
            val childDepValues = dependency[depKey]
            childDepValues?.forEachIndexed { index, value ->
                if(value.type == DependencyType.DEP) {
                    val depChildKey = value.moduleName
                    if(!contains(depChildKey, array)) {
                        val node = ProjectDependencyNode(parent = value.parent, depChildKey, DependencyType.SUB_DEP)
                        array.add(node)
                        addAllChildDependencyIN(value, array)
                    }
                }
            }
        }
    }

    fun printDependency(logger: Logger) {
        updateSubDependency()

        logger.error("Project Dependency size: " + dependency.size)
        dependency.forEach { entry ->
            val key = entry.key
            val value = entry.value

            val depSize = value.filter { str -> str.type == DependencyType.DEP }.size
            val subDepSize = value.filter { str -> str.type == DependencyType.SUB_DEP }.size
            val refSize = value.filter { str -> str.type == DependencyType.REF }.size
            logger.error("\n- $key | DEP: $depSize | REF: $refSize | SUB_DEP: $subDepSize")
            value.sortBy { it.type.index}
            value.forEach { module ->
                val text = module.moduleName + " " + module.type
                if(module.type == DependencyType.SUB_DEP) {
                    logger.error("------ $text | parent: ${module.parent}")
                }
                else {
                    logger.error("--- $text")
                }
            }
        }
    }

    enum class DependencyType(val index: Int) {
        DEP(0),
        REF(1),
        SUB_DEP(2)
    }
}