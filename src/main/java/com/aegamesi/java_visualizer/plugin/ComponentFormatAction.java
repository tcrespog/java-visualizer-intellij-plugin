package com.aegamesi.java_visualizer.plugin;

import com.aegamesi.java_visualizer.ui.ComponentFormat;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;

public class ComponentFormatAction extends ToggleAction {

    private boolean state;


    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return state;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        MainPane pane = (MainPane) e.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (pane != null) {
            this.state = state;
            ComponentFormat format = state ? ComponentFormat.TABLE : ComponentFormat.LIST;
            pane.setComponentFormat(format);
        }
    }
}