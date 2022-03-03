import java.awt.Dimension;
import java.awt.List;
import java.awt.MouseInfo;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

class Clipboard {

    public static void main(String[] args) {
        var location = MouseInfo.getPointerInfo().getLocation();

        var window = new JFrame("Clipboard");
        window.setLocation(location);
        window.setSize(250, 250);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var panel = new JPanel();
        var layout = new BoxLayout(panel, BoxLayout.Y_AXIS);

        panel.setLayout(layout);

        // TODO: get the content of the clipboard
        // TODO: listen for change event of the clipboard
        // TODO: add the new content of the clipboard to the start of the list

        var list = new List();
        list.add("Hello");
        list.add("Bonjour");
        list.add("Holladmedkdendndnejndjnejndejnjndjndjnjenjendjnkjwnfjwnfj;wnfjnfjwfnrw");
        list.add("Holladmedkdendndnejndjnejn");
        list.add(";wnfjnfjwfnrw");
        list.add("end");

        panel.add(list);

        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                switch (e.getKeyChar()) {
                    case 'k' -> {
                        int index = list.getSelectedIndex() - 1;
                        list.select(Math.max(0, index));
                    }
                    case 'j' -> {
                        int index = list.getSelectedIndex() + 1;
                        list.select(Math.min(list.getItemCount() - 1, index));
                    }
                    //TODO: add a way to enter new item
                    case 'i' -> System.out.println("New");
                    case 'd' -> {
                        list.remove(list.getSelectedIndex());
                    }
                    case 'c' -> {
                        list.setEnabled(false);
                        editItem(list.getSelectedItem(), window, list);
                    }
                    //TODO: copy the selected text to the clipboard
                    case '\n' -> {
                    }
                }
            }
        });
        window.setContentPane(panel);
        window.setVisible(true);
    }

    private static void editItem(String item, JFrame component, List list) {

        assert (component != null);
        assert (list != null);

        JTextField textField = new JTextField(item);
        textField.setMaximumSize(new Dimension(component.getWidth(), 25));

        component.getContentPane().add(textField);
        component.validate();
        component.repaint();
        textField.grabFocus();

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                switch (e.getKeyChar()) {
                    case '\n' -> {
                        component.getContentPane().remove(textField);
                        component.validate();
                        component.repaint();
                        list.setEnabled(true);
                        list.requestFocus();
                        int selectedIndex = list.getSelectedIndex();
                        list.replaceItem(textField.getText(), selectedIndex);
                        list.select(selectedIndex);
                    }
                }
            }
        });
    }
}
