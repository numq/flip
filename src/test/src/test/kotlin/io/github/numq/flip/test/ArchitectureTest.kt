package io.github.numq.flip.test

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPath
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class ArchitectureTest {

    // ==================== Helpers ====================

    private fun fileFeatureName(file: com.lemonappdev.konsist.api.declaration.KoFileDeclaration) =
        file.path.split("/").dropWhile { it != "feature" }.getOrNull(1) ?: ""

    private fun fileServiceName(file: com.lemonappdev.konsist.api.declaration.KoFileDeclaration) =
        file.path.split("/").dropWhile { it != "service" }.getOrNull(1) ?: ""

    // ==================== Horizontal Isolation ====================

    @Test
    fun `feature modules must NOT depend on other feature modules`() {
        Konsist.scopeFromProject().files.withPath("/feature/").assertFalse { file ->
            val myFeatureName = fileFeatureName(file)
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                val importFeatureName = importPath.split("/").dropWhile { it != "feature" }.getOrNull(1) ?: ""
                importFeatureName.isNotEmpty() && importFeatureName != myFeatureName
            }
        }
    }

    @Test
    fun `service modules must NOT depend on other service modules`() {
        Konsist.scopeFromProject().files.withPath("/service/").assertFalse { file ->
            val myServiceName = fileServiceName(file)
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                val importServiceName = importPath.split("/").dropWhile { it != "service" }.getOrNull(1) ?: ""
                importServiceName.isNotEmpty() && importServiceName != myServiceName
            }
        }
    }

    @Test
    fun `platform modules must NOT depend on other platform modules`() {
        Konsist.scopeFromProject().files.withPath("/platform/").assertFalse { file ->
            val myPlatform = file.path.split("/").dropWhile { it != "platform" }.getOrNull(1) ?: ""
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                val importPlatform = importPath.split("/").dropWhile { it != "platform" }.getOrNull(1) ?: ""
                importPlatform.isNotEmpty() && importPlatform != myPlatform
            }
        }
    }

    // ==================== Vertical Dependencies ====================

    @Test
    fun `service modules must NOT depend on feature modules`() {
        Konsist.scopeFromProject().files.withPath("/service/").assertFalse { file ->
            file.imports.any { imp ->
                imp.name.replace(".", "/").contains("/feature/")
            }
        }
    }

    @Test
    fun `service modules must NOT depend on entrypoint or platform`() {
        Konsist.scopeFromProject().files.withPath("/service/").assertFalse { file ->
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                importPath.contains("/entrypoint/") || importPath.contains("/platform/")
            }
        }
    }

    @Test
    fun `feature core must NOT depend on feature presentation`() {
        Konsist.scopeFromProject().files.withPath("/feature/").withPath("/core/").assertFalse { file ->
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                importPath.contains("/feature/") && importPath.contains("/presentation/")
            }
        }
    }

    @Test
    fun `feature presentation must NOT depend on service modules`() {
        Konsist.scopeFromProject().files.withPath("/feature/").withPath("/presentation/").assertFalse { file ->
            file.imports.any { imp ->
                imp.name.replace(".", "/").contains("/service/")
            }
        }
    }

    @Test
    fun `feature presentation must NOT depend on other feature presentations`() {
        Konsist.scopeFromProject().files.withPath("/feature/").withPath("/presentation/").assertFalse { file ->
            val myFeatureName = fileFeatureName(file)
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                val importFeatureName = importPath.split("/").dropWhile { it != "feature" }.getOrNull(1) ?: ""
                importFeatureName.isNotEmpty() && importFeatureName != myFeatureName && importPath.contains("/presentation/")
            }
        }
    }

    // ==================== Common Isolation ====================

    @Test
    fun `common core must NOT depend on any FLIP layer`() {
        Konsist.scopeFromProject().files.withPath("/common/core/").assertFalse { file ->
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                importPath.contains("/service/") || importPath.contains("/feature/") || importPath.contains("/entrypoint/") || importPath.contains(
                    "/platform/"
                )
            }
        }
    }

    @Test
    fun `common presentation must only depend on common core or external`() {
        Konsist.scopeFromProject().files.withPath("/common/presentation/").assertFalse { file ->
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                importPath.contains("/service/") || importPath.contains("/feature/") || importPath.contains("/entrypoint/") || importPath.contains(
                    "/platform/"
                )
            }
        }
    }

    // ==================== Entrypoint ====================

    @Test
    fun `entrypoint must NOT depend on platform modules`() {
        Konsist.scopeFromProject().files.withPath("/entrypoint/").assertFalse { file ->
            file.imports.any { imp ->
                imp.name.replace(".", "/").contains("/platform/")
            }
        }
    }

    // ==================== Platform ====================

    @Test
    fun `platform modules must only depend on entrypoint`() {
        Konsist.scopeFromProject().files.withPath("/platform/").assertFalse { file ->
            file.imports.any { imp ->
                val importPath = imp.name.replace(".", "/")
                importPath.contains("/feature/") || importPath.contains("/service/")
            }
        }
    }

    // ==================== Naming ====================

    @Test
    fun `service and feature must not share the same name`() {
        val serviceNames = Konsist.scopeFromProject().files.withPath("/service/").map { fileServiceName(it) }
            .filter { it.isNotEmpty() }.toSet()

        val featureNames = Konsist.scopeFromProject().files.withPath("/feature/").map { fileFeatureName(it) }
            .filter { it.isNotEmpty() }.toSet()

        val conflicts = serviceNames.intersect(featureNames)

        assertTrue(conflicts.isEmpty()) {
            "Service and feature must not share names. Conflicts: $conflicts"
        }
    }
}