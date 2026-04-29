package com.kibernet.luaattachdebug.launch;

import com.intellij.openapi.options.SettingsEditor;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public final class LuaLaunchDebugSettingsPanel extends SettingsEditor<LuaLaunchDebugConfiguration> implements DocumentListener {
    private final JPanel panel = new JPanel(new GridBagLayout());
    private final JTextField program = new JTextField(28);
    private final JTextField workingDirectory = new JTextField(28);
    private final JTextField parameters = new JTextField(28);
    private final JCheckBox useWindowsTerminal = new JCheckBox("use windows terminal");
    public LuaLaunchDebugSettingsPanel() {
        addRow(0, "Program:", program); addRow(1, "Working Directory:", workingDirectory); addRow(2, "Parameters:", parameters); addRow(3, "", useWindowsTerminal);
        program.getDocument().addDocumentListener(this); workingDirectory.getDocument().addDocumentListener(this); parameters.getDocument().addDocumentListener(this); useWindowsTerminal.addActionListener(e -> fireEditorStateChanged());
    }
    private void addRow(int row, String label, JComponent component) {
        GridBagConstraints left = new GridBagConstraints(); left.gridx = 0; left.gridy = row; left.anchor = GridBagConstraints.WEST; left.insets = new Insets(4,4,4,8); panel.add(new JLabel(label), left);
        GridBagConstraints right = new GridBagConstraints(); right.gridx = 1; right.gridy = row; right.weightx = 1.0; right.fill = GridBagConstraints.HORIZONTAL; right.insets = new Insets(4,4,4,4); panel.add(component, right);
    }
    @Override protected void resetEditorFrom(LuaLaunchDebugConfiguration c) { program.setText(c.getProgram()); workingDirectory.setText(c.getWorkingDirectory()); parameters.setText(c.getParameter()); useWindowsTerminal.setSelected(c.getUseWindowsTerminal()); }
    @Override protected void applyEditorTo(LuaLaunchDebugConfiguration c) { c.setProgram(program.getText()); c.setWorkingDirectory(workingDirectory.getText()); c.setParameter(parameters.getText()); c.setUseWindowsTerminal(useWindowsTerminal.isSelected()); }
    @Override protected JComponent createEditor() { return panel; }
    @Override public void insertUpdate(DocumentEvent e) { fireEditorStateChanged(); }
    @Override public void removeUpdate(DocumentEvent e) { fireEditorStateChanged(); }
    @Override public void changedUpdate(DocumentEvent e) { fireEditorStateChanged(); }
}
