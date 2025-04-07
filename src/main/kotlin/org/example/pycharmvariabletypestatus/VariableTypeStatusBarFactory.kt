package org.example.pycharmvariabletypestatus

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class VariableTypeStatusBarFactory : StatusBarWidgetFactory {
    override fun getId(): String = "VariableTypeStatusBar"
    override fun getDisplayName(): String = "Variable Type Display"
    override fun isAvailable(project: Project): Boolean = true
    override fun createWidget(project: Project): StatusBarWidget = VariableTypeStatusBar()
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
}