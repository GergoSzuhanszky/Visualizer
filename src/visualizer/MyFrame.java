package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class MyFrame extends JFrame{
	static JMenuBar mb;
	MyFrame(){
		this.add(new Panel());
		this.setTitle("Visualizer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		
		mb = new JMenuBar();
		mb.add(Panel.run);
		mb.add(Panel.startm);
		mb.add(Panel.goalm);
		mb.add(Panel.clear);
		mb.add(Panel.algos);
		this.setJMenuBar(mb);
	}
}
