package com.codingheaven.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

public class Puzzle15Board {

	public final int kSIZE = 4;
	public final int INVISIBLE_TILE = kSIZE * kSIZE;
	
	public final int TILE_SIZE = 75;
	public final int WIDTH = TILE_SIZE * kSIZE;
	public final int HEIGHT = WIDTH;

	private int grid[][];
	private boolean solved = true;

	private int iEmpty;
	private int jEmpty;

	// animation variables
	private float xVel = 0, yVel = 0;
	private float speed = TILE_SIZE / 4;
	private int xAnim, yAnim;
	private int xDest, yDest;
	private int iAnim = -1, jAnim = -1;
	private boolean animating = false;

	public Puzzle15Board() {

		initializeGrid();

		iEmpty = kSIZE - 1;
		jEmpty = kSIZE - 1;

	}

	private void initializeGrid() {
		grid = new int[kSIZE][kSIZE];

		for (int i = 0; i < kSIZE; i++) {
			for (int j = 0; j < kSIZE; j++) {
				grid[i][j] = (int) ((i + 1) + j * kSIZE);
			}
		}
	}

	public boolean tryMove(int i, int j) {

		if (i < 0 || j < 0 || i >= kSIZE || j >= kSIZE) // out of bounds
			return false;
		
		if (animating) // not to disturb current animation (if there is one)
			return false;

		if ((Math.abs(iEmpty - i) == 1 && jEmpty == j) || (Math.abs(jEmpty - j) == 1 && iEmpty == i)) {
			startSwap(i, j, (int) (iEmpty - i), (int) (jEmpty - j));
			return true;
		}

		return false;

	}

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

			if (tryMove(x, y))
				n++;
		}

		speed = initSpeed;
	}

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

	public void draw(Graphics g) {
		Font font = new Font("Times New Roman", Font.BOLD, TILE_SIZE - 10);
		g.setFont(font);

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

				int size = TILE_SIZE;

				g.setColor(Color.WHITE);
				if (solved)
					g.setColor(Color.GREEN);
				g.fillRect(x, y, size, size);

				g.setColor(Color.BLACK);
				g.drawRect(x, y, size, size);

				String text = Integer.toString(value);

				int strW = g.getFontMetrics(font).stringWidth(text);
				int strH = g.getFontMetrics().getHeight();

				x += TILE_SIZE / 2 - strW / 2;
				y += TILE_SIZE / 2 + strH / 4;

				g.setColor(Color.BLACK);
				g.drawString(text, x, y);
			}
		}
	}

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
