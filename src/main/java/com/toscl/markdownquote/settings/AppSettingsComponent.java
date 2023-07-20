package com.toscl.markdownquote.settings;

import com.intellij.ui.ClickListener;
import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBEmptyBorder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

  private final JPanel myMainPanel;

  public static final String TEMPLATE_FULL = "```{language} {{start-line}-{end-line}} {{high-lines}} ({remote-url}/blob/{branch}/{relative-path}?#L{start-line}-L{end-line})\n" +
          "{code}\n" +
          "```";
  public static final String TEMPLATE_LINK = "```{language}\n" +
          "{code}\n" +
          "```\n" +
          "[{relative-path}?#L{start-line}-L{end-line}]({remote-url}/blob/{branch}/{relative-path}?#L{start-line}-L{end-line})";

  public static final String TEMPLATE_LANGUAGE = "```{language}\n" +
          "{code}\n" +
          "```";
  private final JBTextArea mMdPanel;
  private final JButton templateFull;
  private final JButton templateLink;
  private final JButton templateLanguage;

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
    templateFull = new JButton("Template: language & wrap lines & high lines & link & code");
    templateLink = new JButton("Template: language & link & code");
    templateLanguage = new JButton("Template: language & code");

    new ClickListener() {
      @Override
      public boolean onClick(@NotNull MouseEvent event, int clickCount) {
        setPanelTemplate(TEMPLATE_FULL);
        return true;
      }
    }.installOn(templateFull);

    new ClickListener() {
      @Override
      public boolean onClick(@NotNull MouseEvent event, int clickCount) {
        setPanelTemplate(TEMPLATE_LINK);
        return true;
      }
    }.installOn(templateLink);

    new ClickListener() {
      @Override
      public boolean onClick(@NotNull MouseEvent event, int clickCount) {
        setPanelTemplate(TEMPLATE_LANGUAGE);
        return true;
      }
    }.installOn(templateLanguage);

    myMainPanel = FormBuilder.createFormBuilder()
            .addComponent(new JBLabel("Template of Copied"))
            .addComponent(scrollPane)
            .addComponent(templateFull)
            .addComponent(templateLink)
            .addComponent(templateLanguage)
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
  public String getPanelTemplate() {
    return mMdPanel.getText();
  }

  public void setPanelTemplate(@NotNull String newText) {
    mMdPanel.setText(newText);
  }

}
