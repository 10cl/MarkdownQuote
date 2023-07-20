package com.toscl.markdownquote.settings;

import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBFont;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

  private final JPanel myMainPanel;

  private static final String TEMPLATE = "```{language} {{start-line}-{end-line}} {{high-lines}} ({remote-url}/blob/{branch}/{relative-path}?#L{start-line}-L{end-line})\n" +
          "{code}\n" +
          "```";
  private final JBTextArea mMdPanel;

  public AppSettingsComponent() {
    mMdPanel = new JBTextArea();
    mMdPanel.setLineWrap(true);
    mMdPanel.setWrapStyleWord(true);
//    mMdPanel.getEmptyText().setText(TEMPLATE);
//    mMdPanel.getEmptyText().setFont(JBFont.regular());
    mMdPanel.setBorder(new JBEmptyBorder(3, 5, 3, 5));
//    mMdPanel.setFont(JBFont.regular());

    JBScrollPane scrollPane = new JBScrollPane(mMdPanel);
    scrollPane.setPreferredSize(new Dimension(30, 30));
    myMainPanel = FormBuilder.createFormBuilder()
            .addComponent(new JBLabel("Template of Copied"))
            .addComponent(scrollPane)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
  }

  public JPanel getPanel() {
    return myMainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return mMdPanel;
  }

  @NotNull
  public String getUserNameText() {
    return mMdPanel.getText();
  }

  public void setUserNameText(@NotNull String newText) {
    mMdPanel.setText(newText);
  }

}
