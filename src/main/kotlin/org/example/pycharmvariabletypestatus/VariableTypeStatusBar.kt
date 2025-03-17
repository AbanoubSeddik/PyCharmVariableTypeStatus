package org.example.pycharmvariabletypestatus

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidget.TextPresentation
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.PyExpression
import com.jetbrains.python.psi.types.TypeEvalContext
import com.intellij.psi.PsiDocumentManager
import java.awt.event.MouseEvent

class VariableTypeStatusBar(project: Project) : StatusBarWidget, TextPresentation {

    private var currentType: String = "Unknown"
    private var statusBar: StatusBar? = null

    init {
        EditorFactory.getInstance().eventMulticaster.addCaretListener(object : CaretListener {
            override fun caretPositionChanged(event: CaretEvent) {
                updateVariableType(event)
            }
        }, project)
    }

    private fun updateVariableType(event: CaretEvent) {
        val editor = event.editor
        val project = editor.project ?: return
        val caretOffset = event.caret?.offset ?: return
        val document = editor.document

        try {
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)

            if (psiFile == null) {
                currentType = "No PSI"
                statusBar?.updateWidget(ID())
                return
            }

            if (!psiFile.language.id.contains("Python", ignoreCase = true)) {
                currentType = "Not Python"
                statusBar?.updateWidget(ID())
                return
            }

            val elementAtCaret = psiFile.findElementAt(caretOffset)

            if (elementAtCaret == null) {
                currentType = "No Element"
                statusBar?.updateWidget(ID())
                return
            }

            val pyExpression = PsiTreeUtil.getParentOfType(elementAtCaret, PyExpression::class.java, false)

            if (pyExpression != null) {
                val context = TypeEvalContext.userInitiated(project, psiFile)
                val exprType = context.getType(pyExpression)
                currentType = exprType?.name ?: "Unknown Type"
            } else {
                currentType = "No Variable"
            }
        } catch (e: Exception) {
            currentType = "Error: ${e.message}"
        }

        statusBar?.updateWidget(ID())
    }

    override fun ID() = "VariableTypeStatusBar"
    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this
    override fun getAlignment(): Float = 0.5f

    override fun getText(): String = "Type: $currentType"
    override fun getTooltipText(): String = "Displays the type of the variable under the caret"

    override fun getClickConsumer(): com.intellij.util.Consumer<MouseEvent>? = null

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        statusBar = null
    }
}