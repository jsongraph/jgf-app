package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.util.Pair;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.Collection;

import static info.json_graph_format.jgfapp.api.util.URIUtility.openInBrowser;
import static java.lang.String.format;

public class About extends JPanel {

    public static void showDialog(String name, String description, String version,
                                  Icon icon, Collection<Pair<String, String>> links) {
        JDialog dialog = new JDialog();
        dialog.setTitle(format("About %s", name));
        dialog.setContentPane(new About(name, description, version, icon, links));
        dialog.setSize(new Dimension(400, 250));
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public About(String name, String description, String version, Icon icon,
                 Collection<Pair<String, String>> links) {
        // (sad panda)
        setBackground(Color.white);
        setLayout(new BorderLayout(20, 0));

        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(Color.white);
        iconPanel.setLayout(new GridBagLayout());
        JLabel iconLabel = new JLabel(icon);
        iconPanel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10),
                0, 0));
        add(iconPanel, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setBackground(Color.white);
        details.setLayout(new GridBagLayout());

        details.add(new JLabel("<html><u>Name</u></html>"), new GridBagConstraints(
                0, 0, 1, 1, 0.10, 0.10,
                GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        details.add(new JLabel(name), new GridBagConstraints(
                0, 1, 1, 1, 0.90, 0.10,
                GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 20, 0, 10), 0, 0));

        details.add(new JLabel("<html><u>Description</u></html>"), new GridBagConstraints(
                0, 2, 1, 1, 0.20, 0.10,
                GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        JTextArea descText = new JTextArea(description);
        descText.setLineWrap(true);
        descText.setWrapStyleWord(true);
        descText.setEditable(false);
        descText.setBorder(BorderFactory.createEmptyBorder());
        details.add(descText, new GridBagConstraints(
                0, 3, 1, 1, 0.80, 0.40,
                GridBagConstraints.PAGE_START,
                GridBagConstraints.BOTH,
                new Insets(0, 20, 0, 10), 0, 0));

        details.add(new JLabel("<html><u>Version</u></html>"), new GridBagConstraints(
                0, 4, 1, 1, 0.10, 0.10,
                GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        details.add(new JLabel(version), new GridBagConstraints(
                0, 5, 1, 1, 0.90, 0.10,
                GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 20, 0, 10), 0, 0));

        details.add(new JLabel("<html><u>Links</u></html>"), new GridBagConstraints(
                0, 6, 1, 1, 0.10, 0.10,
                GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        JPanel linksPanel = new JPanel();
        linksPanel.setLayout(new GridBagLayout());
        linksPanel.setBackground(Color.white);
        int y = 0;
        for (final Pair<String, String> u : links) {
            JXHyperlink link = new JXHyperlink(new AbstractAction(u.first()) {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        openInBrowser(new URI(u.second()));
                    } catch (Exception e) { /* swallow; shouldn't happen */ }
                }
            });
            link.setToolTipText(u.second());
            linksPanel.add(link, new GridBagConstraints(
                    0, y++, 1, 1, 1.0, (1.0 / links.size()),
                    GridBagConstraints.FIRST_LINE_START,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(0, 20, 0, 0), 0, 0
            ));
        }
        details.add(linksPanel, new GridBagConstraints(
                0, 7, 1, 1, 0.90, 0.10,
                GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 10), 0, 0));

        add(details, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.white);
        final JPanel thisPanel = this;
        JButton ok = new JButton(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = thisPanel.getParent();
                while (parent != null && !(parent instanceof Window))
                    parent = parent.getParent();
                if (parent != null)
                    ((Window) parent).dispose();
            }
        });
        ok.setPreferredSize(new Dimension(45, 30));
        buttonPanel.add(ok);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
