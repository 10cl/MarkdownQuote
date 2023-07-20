package com.toscl.markdownquote.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.toscl.markdownquote.settings.AppSettingsState;
import icons.SdkIcons;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;


import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action class to demonstrate how to interact with the IntelliJ Platform.
 * The only action this class performs is to provide the user with a popup dialog as feedback.
 * Typically this class is instantiated by the IntelliJ Platform framework based on declarations
 * in the plugin.xml file. But when added at runtime this class is instantiated by an action group.
 */
public class PopupDialogAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project currentProject = event.getProject();
        String dlgTitle = event.getPresentation().getDescription();
        String templateMdQuote = AppSettingsState.getInstance().getState().templateMdQuote;
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        VirtualFile file = event.getRequiredData(CommonDataKeys.VIRTUAL_FILE);

        CaretModel caretModel = editor.getCaretModel();
        int startOffset = caretModel.getPrimaryCaret().getSelectionStart();
        int endOffset = caretModel.getPrimaryCaret().getSelectionEnd();

        Document document = editor.getDocument();
        TextRange selectedRange = new TextRange(startOffset, endOffset);
        int startLine = document.getLineNumber(selectedRange.getStartOffset()) + 1;
        int endLine = document.getLineNumber(selectedRange.getEndOffset()) + 1;

        String highLines = "" + startLine;

        String language = file.getExtension();
        String filepath = file.getCanonicalPath();
        boolean haveLink = templateMdQuote.contains("http");
        String gitPath = isGitRepository(filepath);
        String code = editor.getSelectionModel().getSelectedText();

        String markQuote = templateMdQuote;
        String urlPatter = "({remote-url}/blob/{branch}/{relative-path}?#L{start-line}-L{end-line})";

        String replaceText = markQuote
                .replace("{language}", language == null ? "" : language);

        if (filepath != null && code != null && ((gitPath != null && templateMdQuote.contains(urlPatter)) || haveLink)) {

            if (gitPath != null) {
                Pair<String, String> pair = getCurrentRepositoryGitUrl(gitPath);
                if (pair != null){
                    String remoteUrl = pair.getFirst();
                    String branchName = pair.getSecond();

                    String relativePath = filepath.replace(gitPath.replaceAll("\\\\", "/"), "");
                    remoteUrl = remoteUrl.replace(".git", "");


                    replaceText = replaceText.replace("{remote-url}", remoteUrl)
                            .replace("{branch}", branchName)
                            .replace("{relative-path}", relativePath);
                }
            }else{
                replaceText = replaceText.replace(urlPatter, "");
            }

            markQuote = replaceText
                    .replace("{start-line}", String.valueOf(startLine))
                    .replace("{end-line}", String.valueOf(endLine))
                    .replace("{high-lines}", highLines)
                    .replace("{code}", code);

            copyAndToast(currentProject, markQuote, dlgTitle);
        } else if (code != null){
            markQuote = replaceText.replace(" {{start-line}-{end-line}} {{high-lines}} ({remote-url}/blob/{branch}/{relative-path}?#L{start-line}-L{end-line})", "")
                    .replace("{code}", code);
            copyAndToast(currentProject, markQuote, dlgTitle);
        } else {
            Messages.showMessageDialog(currentProject,
                    "Copy failed! update your template in settings -> tools -> markdownQuote",
                    dlgTitle, SdkIcons.Sdk_default_icon_info);
        }
    }

    private void copyAndToast(Project currentProject, String markQuote, String dlgTitle) {
        try {
            CopyPasteManager.getInstance().setContents(new StringSelection(markQuote));
        } catch (Exception ignore) {
        }

        Messages.showMessageDialog(currentProject,
                markQuote.replace("<", "&#60;").replace(" ", "&#32;"),
                dlgTitle, SdkIcons.Sdk_default_icon_info);
    }


    private static String isGitRepository(String directoryPath) {
        File gitDirectory = new File(directoryPath, ".git");
        if (gitDirectory.exists() && gitDirectory.isDirectory()) {
            return directoryPath;
        } else {
            File parentDirectory = new File(directoryPath).getParentFile();
            if (parentDirectory == null) {
                return null;
            } else {
                return isGitRepository(parentDirectory.getAbsolutePath());
            }
        }
    }

    public static Pair<String, String> getCurrentRepositoryGitUrl(String filepath) {
        try {
            File projectDir = new File(filepath);
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(projectDir);
            if (virtualFile == null) {
                return null;
            }

            String branchName = getGitBranchInfo(virtualFile);
            String remoteUrl = getGitRemoteURL(virtualFile);
            if (branchName != null && remoteUrl != null){
                return new Pair<String, String>(remoteUrl, branchName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getGitRemoteURL(VirtualFile virtualFile) throws IOException {
        File gitConfigFile = new File(virtualFile.getPath(), ".git/config");
        if (!gitConfigFile.exists()) {
            //return "Not a Git repository";
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(gitConfigFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("url")) {
                    return line.split("=")[1].trim();
                }
            }
        }

        //return "Remote URL not found";
        return null;
    }

    private static String getGitBranchInfo(VirtualFile virtualFile) throws IOException {
        File headFile = new File(virtualFile.getPath(), ".git/HEAD");
        if (!headFile.exists()) {
            //return "Not a Git repository";
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(headFile))) {
            String ref = reader.readLine();
            Pattern pattern = Pattern.compile("ref: refs/heads/(.+)");
            Matcher matcher = pattern.matcher(ref);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                //return "Detached HEAD";
                return null;
            }
        }
    }

    /**
     * Determines whether this menu item is available for the current context.
     * Requires a project to be open.
     *
     * @param e Event received when the associated group-id menu is chosen.
     */
    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

}
