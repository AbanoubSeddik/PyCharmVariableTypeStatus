package org.example.pycharmvariabletypestatus

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidget.TextPresentation
import java.awt.event.MouseEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.CaretEvent

class VariableTypeStatusBar(private val project: Project) : StatusBarWidget, TextPresentation {

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
        TODO()
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
