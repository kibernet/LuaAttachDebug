package com.kibernet.luaattachdebug.attach;

import com.intellij.openapi.options.SettingsEditor;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public final class LuaAttachDebugSettingsPanel extends SettingsEditor<LuaAttachDebugConfiguration> implements DocumentListener {
    private final JPanel panel = new JPanel(new GridBagLayout());
    private final JTextField processId = new JTextField(24);
    private final JTextField processName = new JTextField(24);
    private final JTextField encoding = new JTextField("gbk", 24);
    private final JRadioButton usePid = new JRadioButton("use pid");
    private final JRadioButton useProcessName = new JRadioButton("use name");
    public LuaAttachDebugSettingsPanel() {
        ButtonGroup group = new ButtonGroup(); group.add(usePid); group.add(useProcessName); usePid.setSelected(true);
        addRow(0, "Process Id:", processId); addRow(1, "Process Name:", processName); addRow(2, "Encoding:", encoding);
        JPanel modePanel = new JPanel(); modePanel.add(usePid); modePanel.add(useProcessName); addRow(3, "Attach Mode:", modePanel);
        processId.getDocument().addDocumentListener(this); processName.getDocument().addDocumentListener(this); encoding.getDocument().addDocumentListener(this);
        usePid.addActionListener(e -> fireEditorStateChanged()); useProcessName.addActionListener(e -> fireEditorStateChanged());
    }
    private void addRow(int row, String label, JComponent component) {
        GridBagConstraints left = new GridBagConstraints(); left.gridx = 0; left.gridy = row; left.anchor = GridBagConstraints.WEST; left.insets = new Insets(4,4,4,8); panel.add(new JLabel(label), left);
        GridBagConstraints right = new GridBagConstraints(); right.gridx = 1; right.gridy = row; right.weightx = 1.0; right.fill = GridBagConstraints.HORIZONTAL; right.insets = new Insets(4,4,4,4); panel.add(component, right);
    }
    @Override protected void resetEditorFrom(LuaAttachDebugConfiguration c) { processId.setText(c.getPid()); processName.setText(c.getProcessName()); encoding.setText(c.getEncoding()); usePid.setSelected(c.getAttachMode() == LuaAttachMode.Pid); useProcessName.setSelected(c.getAttachMode() == LuaAttachMode.ProcessName); }
    @Override protected void applyEditorTo(LuaAttachDebugConfiguration c) { c.setPid(processId.getText()); c.setProcessName(processName.getText()); c.setEncoding(encoding.getText()); c.setAttachMode(useProcessName.isSelected() ? LuaAttachMode.ProcessName : LuaAttachMode.Pid); }
    @Override protected JComponent createEditor() { return panel; }
    @Override public void insertUpdate(DocumentEvent e) { fireEditorStateChanged(); }
    @Override public void removeUpdate(DocumentEvent e) { fireEditorStateChanged(); }
    @Override public void changedUpdate(DocumentEvent e) { fireEditorStateChanged(); }
}
