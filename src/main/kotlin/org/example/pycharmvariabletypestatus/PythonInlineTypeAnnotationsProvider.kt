package org.example.pycharmvariabletypestatus

import com.intellij.codeInsight.hints.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

@Suppress("UnstableApiUsage")
class PythonInlineTypeAnnotationsProvider : InlayHintsProvider<PythonInlineTypeAnnotationsProvider.Settings> {

    data class Settings(
        var showInlineComments: Boolean = true,
    )

    override val key: SettingsKey<Settings> = SettingsKey("python.inline.type.annotations")
    override val name = "Inline Type Annotations"
    override val description = "Show inferred types as ghost text next to variables"
    override val previewText = null

    override val group: InlayGroup = InlayGroup.TYPES_GROUP

    override fun createSettings(): Settings = Settings()

    override fun createConfigurable(settings: Settings): ImmediateConfigurable = object : ImmediateConfigurable {
        override fun createComponent(listener: ChangeListener): JComponent = panel { }

        override val cases: List<ImmediateConfigurable.Case> = listOf(
            ImmediateConfigurable.Case(
                "Show inline comments",
                "inline.type.comments",
                settings::showInlineComments
            )
        )
    }

    override fun getCaseDescription(case: ImmediateConfigurable.Case) = when (case.id) {
        "inline.type.comments" -> "Show inferred types as ghost text comments after variables"
        else -> null
    }

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: Settings,
        sink: InlayHintsSink
    ): InlayHintsCollector = PythonInlineTypeAnnotationsCollector(editor, settings)
}