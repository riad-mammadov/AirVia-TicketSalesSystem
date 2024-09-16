package ButtonUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 The HoverButton class is a custom implementation of a JButton that changes its background color
 when the mouse hovers over it. It also sets some common button properties.
 */
public class HoverButton extends JButton{


    /**
     Sets common button properties and adds a mouse listener that changes the button's background
     color when the mouse hovers over it.
     @param button The button to set properties for.
     */
    public static void setButtonProperties(JButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBorder(new EmptyBorder(2, 2, 2, 2));
        System.out.println("test");

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIManager.getColor("Button.background"));
            }
        });
    }
}
