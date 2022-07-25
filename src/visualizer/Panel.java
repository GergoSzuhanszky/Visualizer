package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;
import javax.swing.Timer;
import visualizer.Panel;
import java.lang.Math.*;


public class Panel extends JPanel implements ActionListener {
	
	static JMenuItem run;
	static JMenuItem startm;
	static JMenuItem goalm;
	static JMenuItem clear;
	static JMenu algos;
	static JMenuItem a1;
	
	static final int SCREEN_WIDTH = 800;
	static final int SCREEN_HEIGHT = 800;
	static final int UNIT_SIZE = 40;										// SCREEN_WIDTH or SCREEN_HEIGHT % UNIT_SIZE == 0 has to be true
	static final int UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;											// The higher the slower it will run
	boolean startclicked = false;
	boolean goalclicked = false;
	boolean foundgoal = false;
	Timer timer;
	
	enum Mode {IDLE ,START, GOAL, RUN}
	Mode mode;
	
	Queue<Point> visits;
	
	Queue<Point> displays;
	
	Queue<Point> route;
	
	Point mouse = new Point();

	Point start = new Point();
	
	Point goal = new Point();
	
	Point current = new Point();

	Panel(){
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addMouseListener(new MyMouseAdapter());
		
		mode = Mode.START;
		
		run = new JMenuItem("Run");
		startm = new JMenuItem("Starting point");
		goalm = new JMenuItem("Goal point");
		clear = new JMenuItem("Clear");
		algos = new JMenu("Algorithms");
		a1 = new JMenuItem("BFS");
		algos.add(a1);
		run.addActionListener(this);
		startm.addActionListener(this);
		goalm.addActionListener(this);
		clear.addActionListener(this);
		a1.addActionListener(this);
		
		// Start = 1; Goal = 2; No fill = 0; queued = 3; visited = 4;
		
		start();
	}
	
	public void start() {
		timer = new Timer(DELAY, this);
		timer.start();
		visits = new LinkedList<Point>();
		displays = new LinkedList<Point>();
		route = new LinkedList<Point>();
		mode = Mode.IDLE;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	
	
	
	public void draw(Graphics g) {
		for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
			g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
			g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
		}
		if (mode != Mode.IDLE) {
			if (startclicked) {
				// Drawing starting point
				g.setColor(new Color(0, 102, 51));
				g.fillRect(start.x * UNIT_SIZE + 1, start.y * UNIT_SIZE + 1, UNIT_SIZE - 1, UNIT_SIZE - 1);
			}
			
			if (goalclicked) {
				// Drawing goal point
				g.setColor(new Color(204, 0, 0));
				g.fillRect(goal.x * UNIT_SIZE + 1, goal.y * UNIT_SIZE + 1, UNIT_SIZE - 1, UNIT_SIZE - 1);
			}
			for (Point p : displays) {
				g.setColor(new Color(204, 204, 255));
				g.fillRect(p.x * UNIT_SIZE + 1, p.y * UNIT_SIZE + 1, UNIT_SIZE - 1, UNIT_SIZE - 1);
			}
			if (foundgoal) {
				for (Point p : route) {
					g.setColor(new Color(127, 0, 255));
					g.fillRect(p.x * UNIT_SIZE + 1, p.y * UNIT_SIZE + 1, UNIT_SIZE - 1, UNIT_SIZE - 1);
				}
			}
		}
	}
	
	public boolean checkFound(int x, int y) {
		if (x == goal.x && y == goal.y) {
			return true;
		}
		return false;
	}
	
	// inspiration: https://algorithms.tutorialhorizon.com/breadth-first-search-bfs-in-2d-matrix-2d-array/
	// tweaked with it a little
	public void BFS() {
        int h = SCREEN_WIDTH/UNIT_SIZE;
        if (h == 0)
            return;
        int l = SCREEN_HEIGHT/UNIT_SIZE;

        boolean[][] visited = new boolean[h][l];

        Queue<Point> queue = new LinkedList<>();

        queue.add(new Point(start.x, start.y));

        while (queue.isEmpty() == false) {

            Point current = queue.remove();
            
            if (current.x < 0 || current.y < 0 || current.x >= h || current.y >= l || visited[current.x][current.y])
               	continue;
            
           	if (!current.equals(start) && !current.equals(goal)) {
           		visits.add(new Point(current.x, current.y));
            }
            
           	visited[current.x][current.y] = true;
           	
           	queue.add(new Point(current.x, current.y - 1)); //go left
           	queue.add(new Point(current.x, current.y + 1)); //go right
           	queue.add(new Point(current.x - 1, current.y)); //go up
           	queue.add(new Point(current.x + 1, current.y)); //go down
           	
           	if (current.equals(goal)) {
           		return;
            }
        }
        
        mode = Mode.START;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startm) {
			mode = Mode.START;
			System.out.println("Mode set to START");
		}
		if (e.getSource() == goalm) {
			mode = Mode.GOAL;
			System.out.println("Mode set to GOAL");
		}
		if (e.getSource() == run && startclicked == true) {
			mode = Mode.RUN;
			System.out.println("Running");
			BFS();
			Timer speed = new Timer(1, expression -> {
				if (!visits.isEmpty()) {
					displays.add(visits.remove());
				}
			});
			speed.setRepeats(true);
			speed.start();
		}
		if (e.getSource() == clear) {
			mode = Mode.IDLE;
			startclicked = false;
			goalclicked = false;
			visits.clear();
			displays.clear();
		}
		if (e.getSource() == a1) {
			System.out.println("Yo");
		}
		repaint();
	}

	public class MyMouseAdapter extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			mouse.x = e.getX() - (e.getX() % UNIT_SIZE);
			mouse.y = e.getY() - (e.getY() % UNIT_SIZE);
			System.out.println(mouse.x + " " + mouse.y);
			if (mode == Mode.START) {
				start.x = mouse.x / UNIT_SIZE;
				start.y = mouse.y / UNIT_SIZE;
				startclicked = true;
			}
			if (mode == Mode.GOAL) {
				goal.x = mouse.x / UNIT_SIZE;
				goal.y = mouse.y / UNIT_SIZE;
				goalclicked = true;
			}
		}
	}
}
