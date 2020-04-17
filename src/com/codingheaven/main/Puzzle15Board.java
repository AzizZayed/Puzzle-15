package com.codingheaven.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

/**
 * the board with the game logic
 * 
 * @author Zayed
 *
 */
public class Puzzle15Board {

	private final int kSIZE = 4; // size of the board
	private final int INVISIBLE_TILE = kSIZE * kSIZE; // the number off the invisible tile

	private final int TILE_SIZE = 75; // size of a tile in pixels
	private final int WIDTH = TILE_SIZE * kSIZE; // width of the board in pixels
	private final int HEIGHT = WIDTH;// height of the board in pixels

	private int grid[][]; // data structure containing the numbers for each tile
	private boolean solved = true; // true if the board is solved

	// indices of the empty/invisible tile
	private int iEmpty;
	private int jEmpty;

	// animation variables
	private float xVel = 0, yVel = 0;
	private float speed = TILE_SIZE / 4;
	private int xAnim, yAnim;
	private int xDest, yDest;
	private int iAnim = -1, jAnim = -1;
	private boolean animating = false;

	/**
	 * constructor
	 */
	public Puzzle15Board() {
		initializeGrid();

		iEmpty = kSIZE - 1;
		jEmpty = kSIZE - 1;
	}

	/**
	 * @return the WIDTH
	 */
	public int getWidth() {
		return WIDTH;
	}

	/**
	 * @return the HEIGHT
	 */
	public int getHeight() {
		return HEIGHT;
	}

	/**
	 * initialize the gird of numbersF
	 */
	private void initializeGrid() {
		grid = new int[kSIZE][kSIZE];

		for (int i = 0; i < kSIZE; i++) {
			for (int j = 0; j < kSIZE; j++) {
				grid[i][j] = (int) ((i + 1) + j * kSIZE);
			}
		}
	}

	/**
	 * try to move the tile
	 * 
	 * @param x       - x position, either in the board or in pixels
	 * @param y       - y position, either in the board or in pixels
	 * @param indices - true if the parameter passed were indices, false if
	 *                coordinates and need conversion to indices
	 * @return true if the move is possible, false otherwise
	 */
	public boolean tryMove(int x, int y, boolean indices) {

		int i = x;
		int j = y;

		if (!indices) {
			i /= TILE_SIZE;
			j /= TILE_SIZE;
		}

		if (i < 0 || j < 0 || i >= kSIZE || j >= kSIZE) // out of bounds
			return false;

		if (animating) // not to disturb current animation (if there is one)
			return false;

		if ((Math.abs(iEmpty - i) == 1 && jEmpty == j) || (Math.abs(jEmpty - j) == 1 && iEmpty == i)) {
			startSwap(i, j, iEmpty - i, jEmpty - j);
			return true;
		}

		return false;
	}

	/**
	 * initialize the swap animation
	 * 
	 * @param i  - desired i index of position to swap
	 * @param j  - desired j index of position to swap
	 * @param di - horizontal swap direction
	 * @param dj - vertical swap direction
	 */
	private void startSwap(int i, int j, int di, int dj) {

		animating = true;

		iAnim = i;
		jAnim = j;

		xVel = di * speed;
		yVel = dj * speed;

		xAnim = i * TILE_SIZE;
		yAnim = j * TILE_SIZE;

		xDest = iEmpty * TILE_SIZE;
		yDest = jEmpty * TILE_SIZE;

	}

	/**
	 * animate the shuffle of the tiles
	 */
	public void animatedShuffle() {
		int n = 0;
		int max = kSIZE * 75;
		Random r = new Random();

		float initSpeed = speed;
		speed = TILE_SIZE / 2;
		solved = false;

		while (n < max) {
			int x = (int) (iEmpty + r.nextInt(3) - 1);
			int y = (int) (jEmpty + r.nextInt(3) - 1);

			if (tryMove(x, y, true))
				n++;
		}

		speed = initSpeed;
	}

	/**
	 * check if the board is complete
	 */
	private void checkWin() {
		boolean won = true;
		int i = 0, j = 0;

		while (won && i < kSIZE) {
			j = 0;
			while (won && j < kSIZE) {
				won = (grid[i][j] == (i + 1) + j * kSIZE);
				j++;
			}
			i++;
		}

		solved = won;
	}

	/**
	 * draw the board
	 * 
	 * @param g - Graphics drawing tool
	 */
	public void draw(Graphics g) {
		int size = TILE_SIZE - (kSIZE > 10 ? 30 : 10); // in case someone wants to change the constant
		g.setFont(new Font("Times New Roman", Font.BOLD, size));

		for (int i = 0; i < kSIZE; i++) {
			for (int j = 0; j < kSIZE; j++) {

				int value = grid[i][j];

				if (value == INVISIBLE_TILE) // 16th tile
					continue;

				int x, y;

				if (i == iAnim && j == jAnim) {
					x = xAnim;
					y = yAnim;
				} else {
					x = i * TILE_SIZE;
					y = j * TILE_SIZE;
				}

				g.setColor(Color.WHITE);
				if (solved)
					g.setColor(Color.GREEN);
				g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

				g.setColor(Color.BLACK);
				g.drawRect(x, y, TILE_SIZE, TILE_SIZE);

				String text = Integer.toString(value);

				int strW = g.getFontMetrics().stringWidth(text);
				int strH = g.getFontMetrics().getHeight();

				x += TILE_SIZE / 2 - strW / 2;
				y += TILE_SIZE / 2 + strH / 4;

				g.setColor(Color.BLACK);
				g.drawString(text, x, y);
			}
		}
	}

	/**
	 * update animation every frame
	 */
	public void update() {
		if (animating) {
			xAnim += xVel;
			yAnim += yVel;

			int di = iAnim - iEmpty;
			int dj = jAnim - jEmpty;

			if (xAnim * di <= xDest * di && yAnim * dj <= yDest * dj) {

				// swap
				int temp = grid[iAnim][jAnim];
				grid[iAnim][jAnim] = INVISIBLE_TILE;
				grid[iEmpty][jEmpty] = temp;

				// new position for empty
				iEmpty = iAnim;
				jEmpty = jAnim;

				animating = false;

				checkWin();
			}
		}
	}
}
