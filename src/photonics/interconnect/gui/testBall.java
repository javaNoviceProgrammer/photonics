package photonics.interconnect.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class testBall {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new testBall();
				
			}
		}) ;
    }

    public testBall() {
        JPanel testPane = new JPanel();
        testPane.setBackground(Color.white);
        testPane.setLayout(new GridBagLayout());
        testPane.add(new MyBall(30,30,10));

        JFrame frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new MyBall(30,30,10));
        frame.add(new MyBall(100,100,10)) ;
        frame.pack();
        frame.setSize(500, 300); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class MyBall extends JComponent
{
    private static final long serialVersionUID = 1L;
    public MyBall() { }
    public MyBall(int x, int y, int diameter)
    {
        super();
        this.setLocation(x, y);
        this.setSize(diameter, diameter);

    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.red);
        g.fillOval(0, 0, 50, 100);
        
    }
}