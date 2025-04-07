package org.example.pycharmvariabletypestatus

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.jetbrains.python.psi.*
import com.jetbrains.python.psi.types.TypeEvalContext

@Suppress("UnstableApiUsage")
class PythonInlineTypeAnnotationsCollector(
    editor: Editor,
    private val settings: PythonInlineTypeAnnotationsProvider.Settings
) : FactoryInlayHintsCollector(editor) {

    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        if (!element.isValid || !settings.showInlineComments) return true

        if (!element.containingFile.language.id.contains("Python", ignoreCase = true)) return true

        when (element) {
            is PyAssignmentStatement -> processAssignment(element, editor, sink)
            is PyTargetExpression -> processTarget(element)
        }

        return true
    }

    private fun processAssignment(assignment: PyAssignmentStatement, editor: Editor, sink: InlayHintsSink) {
        val document = editor.document
        val lineEndOffset = document.getLineEndOffset(document.getLineNumber(assignment.textRange.endOffset))

        val project = editor.project ?: return
        val psiFile = assignment.containingFile
        val context = TypeEvalContext.userInitiated(project, psiFile)

        val valueExpr = assignment.assignedValue ?: return
        val valueType = context.getType(valueExpr)?.name ?: return

        val presentation = factory.smallText(" # $valueType")

        sink.addInlineElement(
            lineEndOffset,
            true,
            presentation,
            false
        )
    }

    private fun processTarget(target: PyTargetExpression) {
        if (target.parent !is PyAssignmentStatement || target.annotation != null) return

        val isClassAttribute = target.containingClass != null && target.parent !is PyAssignmentStatement
        val isFunctionParameter = target.parent is PyParameter
        if (isClassAttribute || isFunctionParameter) return
    }
}