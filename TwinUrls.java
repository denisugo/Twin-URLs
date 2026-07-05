import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class TwinUrls {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Twin URLs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        JComponent urlWithImportantQueryInput = createInputFieldComponent("url with query params",
                TwinUrls::setUrlWithImportantQuery);
        JComponent urlWithImportantEndpointInput = createInputFieldComponent("url with endpoint",
                TwinUrls::setUrlWithImportantEndpoint);

        Button mixButton = new Button("Mix & Copy URL to clippboard", TwinUrls::copyToClipboard);

        panel.add(urlWithImportantQueryInput);
        panel.add(urlWithImportantEndpointInput);
        panel.add(mixButton);
        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }

    private static String urlWithImportantQuery;
    private static String urlWithImportantEndpoint;

    private static void setUrlWithImportantQuery(String value) {
        urlWithImportantQuery = value;
    }

    private static void setUrlWithImportantEndpoint(String value) {
        urlWithImportantEndpoint = value;
    }

    private static void copyToClipboard() {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(assembleUrl());
        cb.setContents(data, null);
    }

    private static JComponent createInputFieldComponent(String name, Consumer<String> setter) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(name);
        InputField inputField = new InputField(setter);
        Button eraseButton = new Button("x", () -> inputField.setText(""));

        JPanel panelForLabelWithInputField = new JPanel(new GridLayout(2, 1));
        panelForLabelWithInputField.add(label);
        panelForLabelWithInputField.add(inputField);
        panelForLabelWithInputField.setBorder(BorderFactory.createEmptyBorder(10, 10,
                10, 10));

        // avoiding size manipulations by the label text
        panelForLabelWithInputField
                .setPreferredSize(new Dimension(0, panelForLabelWithInputField.getPreferredSize().height));
        panelForLabelWithInputField.setMinimumSize(new Dimension(0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.weightx = 0.9;
        panel.add(panelForLabelWithInputField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.1;
        panel.add(eraseButton, gbc);

        return panel;
    }

    private static String assembleUrl() {
        String endpoint = urlWithImportantEndpoint.split("\\?")[0];
        String[] splittedUrlWithImportantQuery = urlWithImportantQuery.split("\\?");
        String query = splittedUrlWithImportantQuery.length == 2 ? splittedUrlWithImportantQuery[1] : "";
        return "%s?%s".formatted(endpoint, query);
    }

    private static class Button extends JButton {
        public Button(String text, Runnable listener) {
            super();
            this.setText(text);
            this.addActionListener(_ignored -> listener.run());
        }
    }

    private static class InputField extends JTextField {

        public InputField(Consumer<String> setter) {
            super();
            this.getDocument().addDocumentListener(createInputFieldDocumentListener(setter));

        }

        private DocumentListener createInputFieldDocumentListener(Consumer<String> setter) {
            return new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    handleType();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    handleType();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // Plain text components do not trigger this, but good practice to include
                    handleType();
                }

                // Helper method to execute on any text change
                private void handleType() {
                    setter.accept(getText());
                }
            };
        }
    }
}
