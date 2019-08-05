package com.codingheaven.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class GameManager extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public boolean running = false; // true if the game is running
	private Thread gameThread; // thread where the game is updated AND rendered (single thread game)

	// Game properties...
	private Puzzle15Board puzzle;

	public GameManager() {

		initialise();
		canvasSetup();

		newWindow();

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int i = e.getX() / puzzle.TILE_SIZE;
				int j = e.getY() / puzzle.TILE_SIZE;

				puzzle.tryMove(i, j);

			}

		});

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				char key = e.getKeyChar();

				if (key == 's') {
					puzzle.animatedShuffle();
				}
			}

		});

		this.setFocusable(true);

	}

	private void newWindow() {
		JFrame frame = new JFrame("15 Puzzle");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		start();
	}

	/**
	 * initialize all our game objects
	 */
	private void initialise() {

		puzzle = new Puzzle15Board();

	}

	/**
	 * just to setup the canvas to our desired settings and sizes
	 */
	private void canvasSetup() {

		this.setPreferredSize(new Dimension(puzzle.WIDTH, puzzle.HEIGHT));
		this.setMaximumSize(new Dimension(puzzle.WIDTH, puzzle.HEIGHT));
		this.setMinimumSize(new Dimension(puzzle.WIDTH, puzzle.HEIGHT));
	}

	/**
	 * Game loop
	 */
	@Override
	public void run() {
		// so you can keep your sanity, I won't explain the game loop... you're welcome

		this.requestFocus();

		// game timer
		final double MAX_FRAMES_PER_SECOND = 60.0;
		final double MAX_UPDATES_PER_SECOND = 60.0;

		long startTime = System.nanoTime();
		final double uOptimalTime = 1000000000 / MAX_UPDATES_PER_SECOND;
		final double fOptimalTime = 1000000000 / MAX_FRAMES_PER_SECOND;
		double uDeltaTime = 0, fDeltaTime = 0;
		int frames = 0, updates = 0;
		long timer = System.currentTimeMillis();

		while (running) {

			long currentTime = System.nanoTime();
			uDeltaTime += (currentTime - startTime) / uOptimalTime;
			fDeltaTime += (currentTime - startTime) / fOptimalTime;
			startTime = currentTime;

			while (uDeltaTime >= 1) {
				update();
				updates++;
				uDeltaTime--;
			}

			while (fDeltaTime >= 1) {
				render();
				frames++;
				fDeltaTime--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {

				System.out.println("UPS: " + updates + ", FPS: " + frames);

				frames = 0;
				updates = 0;
				timer += 1000;
			}
		}

		stop();
	}

	/**
	 * start the thread and the game
	 */
	public synchronized void start() {
		gameThread = new Thread(this);
		/*
		 * since "this" is the "Game" Class you are in right now and it implements the
		 * Runnable Interface we can give it to a thread constructor. That thread with
		 * call it's "run" method which this class inherited (it's directly above)
		 */
		gameThread.start(); // start thread
		running = true;
	}

	/**
	 * Stop the thread and the game
	 */
	public void stop() {
		try {
			gameThread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * render the back and all the objects
	 */
	public void render() {
		// Initialize drawing tools first before drawing

		BufferStrategy buffer = this.getBufferStrategy(); // extract buffer so we can use them
		// a buffer is basically like a blank canvas we can draw on

		if (buffer == null) { // if it does not exist, we can't draw! So create it please
			this.createBufferStrategy(3); // Creating a Triple Buffer
			/*
			 * triple buffering basically means we have 3 different canvases this is used to
			 * improve performance but the drawbacks are the more buffers, the more memory
			 * needed so if you get like a memory error or something, put 2 instead of 3 or
			 * even 1...if you run a computer from 2002...
			 * 
			 * BufferStrategy:
			 * https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferStrategy.html
			 */

			return;
		}

		Graphics g = buffer.getDrawGraphics(); // extract drawing tool from the buffers
		/*
		 * Graphics is class used to draw rectangles, ovals and all sorts of shapes and
		 * pictures so it's a tool used to draw on a buffer
		 * 
		 * Graphics: https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html
		 */

		// draw Game Objects here
		g.setColor(Color.black);
		g.fillRect(0, 0, puzzle.WIDTH, puzzle.HEIGHT);

		puzzle.draw(g);

		// actually draw
		g.dispose();
		buffer.show();

	}

	/**
	 * update settings and move all objects
	 */
	public void update() {

		// update Game Objects here
		puzzle.update();

	}

	/**
	 * start of the program
	 */
	public static void main(String[] args) {
		new GameManager();
	}

}
