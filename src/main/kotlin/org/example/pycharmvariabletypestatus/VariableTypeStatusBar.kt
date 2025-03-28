package org.example.pycharmvariabletypestatus

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidget.TextPresentation
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.*
import com.jetbrains.python.psi.types.TypeEvalContext
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiTreeChangeAdapter
import com.intellij.psi.PsiTreeChangeEvent
import com.intellij.psi.PsiManager
import java.awt.event.MouseEvent
import java.util.WeakHashMap

class VariableTypeStatusBar(private val project: Project) : StatusBarWidget, TextPresentation {

    private var currentType: String = "Unknown"
    private var statusBar: StatusBar? = null
    private val typeCache = WeakHashMap<PsiElement, String>()

    init {
        EditorFactory.getInstance().eventMulticaster.addCaretListener(object : CaretListener {
            override fun caretPositionChanged(event: CaretEvent) {
                updateVariableType(event)
            }
        }, project)

        PsiManager.getInstance(project).addPsiTreeChangeListener(object : PsiTreeChangeAdapter() {
            override fun childrenChanged(event: PsiTreeChangeEvent) {
                typeCache.clear()
            }
        }, this)
    }

    private fun updateVariableType(event: CaretEvent) {
        val editor = event.editor
        val project = editor.project ?: return
        val caretOffset = event.caret?.offset ?: return
        val document = editor.document

        try {
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

            if (!psiFile.language.id.contains("Python", ignoreCase = true)) {
                currentType = "Not Python"
                statusBar?.updateWidget(ID())
                return
            }

            val elementAtCaret = psiFile.findElementAt(caretOffset) ?: return

            if (typeCache.containsKey(elementAtCaret)) {
                currentType = "Cached: ${typeCache[elementAtCaret]}"
                statusBar?.updateWidget(ID())
                return
            }

            val context = TypeEvalContext.userInitiated(project, psiFile)

            currentType = inferType(elementAtCaret, context) ?: "Unknown Type"

            typeCache[elementAtCaret] = currentType

        } catch (e: Exception) {
            currentType = "Error: ${e.message}"
        }

        statusBar?.updateWidget(ID())
    }

    private fun inferType(element: PsiElement, context: TypeEvalContext): String? {
        val pyExpression = PsiTreeUtil.getParentOfType(element, PyExpression::class.java, false)
        if (pyExpression != null) {
            return context.getType(pyExpression)?.name
        }

        val pyFunction = PsiTreeUtil.getParentOfType(element, PyFunction::class.java, false)
        if (pyFunction != null) {
            return context.getReturnType(pyFunction)?.name ?: "Unknown Return Type"
        }

        return null
    }

    override fun ID() = "VariableTypeStatusBar"
    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this
    override fun getAlignment(): Float = 0.5f

    override fun getText(): String = "Type: $currentType"
    override fun getTooltipText(): String = "Displays the type of the variable or function under the caret"

    override fun getClickConsumer(): com.intellij.util.Consumer<MouseEvent>? = null

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        statusBar = null
    }
}