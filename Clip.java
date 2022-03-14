import java.awt.MouseInfo;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


//TODO: save the current state of the app
//TODO: Make a real Linux app

class Clip extends Thread implements ClipboardOwner {
  private Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
  private static List list = new List();
  
  public void run() {
    Transferable trans = sysClip.getContents(this);
    regainOwnership(trans);
    System.out.println("Listening to board...");
    while(true) {}
  }
  
  @Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
	    Transferable contents = sysClip.getContents(this); //EXCEPTION
	    processContents(contents);
	    regainOwnership(contents);		
	}
  
  void processContents(Transferable t) {
    System.out.println("Processing: " + t);
    try {
		System.out.println(t.getTransferData(DataFlavor.stringFlavor));
		list.add(t.getTransferData(DataFlavor.stringFlavor).toString());
	} catch (UnsupportedFlavorException | IOException e) {
		e.printStackTrace();
	}
  }
  
  void regainOwnership(Transferable t) {
    sysClip.setContents(t, this);
  }
  
  public static void main(String[] args) {
    var b = new Clip();
    b.start();
    var window = new JFrame("Clipboard");
    window.setLocation(MouseInfo.getPointerInfo().getLocation());
    window.setSize(250, 250);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    var panel = new JPanel();
  	var layout = new BoxLayout(panel, BoxLayout.Y_AXIS);

  	panel.setLayout(layout);

  list.addKeyListener(new KeyAdapter() {
  @Override
  public void keyTyped(KeyEvent e) {
      super.keyTyped(e);
      switch (e.getKeyChar()) {
          case 'k' -> {
              int index = list.getSelectedIndex() - 1;
              list.select(Math.max(0, index));
          }
          case 'p' -> window.setVisible(true);
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
  
  	panel.add(list);
  	
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