package Blokus;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlockTray extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static final int defaultSize = 500;
	private static final Polyomino[][] defaultLayout =
	{
		{ Polyomino.L5, Polyomino.L5, Polyomino.L5, Polyomino.L5, Polyomino.V5, Polyomino.V5, Polyomino.V5 },
		{ Polyomino.P5, Polyomino.P5, Polyomino.O0, Polyomino.L5, Polyomino.T4, Polyomino.O0, Polyomino.V5 },
		{ Polyomino.P5, Polyomino.P5, Polyomino.F5, Polyomino.T4, Polyomino.T4, Polyomino.T4, Polyomino.V5 },
		{ Polyomino.P5, Polyomino.F5, Polyomino.F5, Polyomino.F5, Polyomino.O0, Polyomino.O4, Polyomino.O4 },
		{ Polyomino.I2, Polyomino.O0, Polyomino.X5, Polyomino.F5, Polyomino.Y5, Polyomino.O4, Polyomino.O4 },
		{ Polyomino.I2, Polyomino.X5, Polyomino.X5, Polyomino.X5, Polyomino.Y5, Polyomino.Y5, Polyomino.I3 },
		{ Polyomino.L4, Polyomino.L4, Polyomino.X5, Polyomino.W5, Polyomino.Y5, Polyomino.O0, Polyomino.I3 },
		{ Polyomino.L4, Polyomino.O0, Polyomino.W5, Polyomino.W5, Polyomino.Y5, Polyomino.T5, Polyomino.I3 },
		{ Polyomino.L4, Polyomino.W5, Polyomino.W5, Polyomino.T5, Polyomino.T5, Polyomino.T5, Polyomino.I4 },
		{ Polyomino.I5, Polyomino.N5, Polyomino.V3, Polyomino.V3, Polyomino.O1, Polyomino.T5, Polyomino.I4 },
		{ Polyomino.I5, Polyomino.N5, Polyomino.N5, Polyomino.V3, Polyomino.O0, Polyomino.O0, Polyomino.I4 },
		{ Polyomino.I5, Polyomino.O0, Polyomino.N5, Polyomino.Z5, Polyomino.Z5, Polyomino.Z4, Polyomino.I4 },
		{ Polyomino.I5, Polyomino.U5, Polyomino.N5, Polyomino.U5, Polyomino.Z5, Polyomino.Z4, Polyomino.Z4 },
		{ Polyomino.I5, Polyomino.U5, Polyomino.U5, Polyomino.U5, Polyomino.Z5, Polyomino.Z5, Polyomino.Z4 }
	};

	private int width = 14;
	private int height = 7;
	private Block[][] blocks;
	private BlockInventory inventory;

	public BlockTray(BlockInventory inventory, int longEdgeSize, int quarterTurns)
	{
		setLayout(new GridBagLayout());
		this.inventory = inventory;

		// Create a temp copy of the default layout, so it can be rotated without issue
		Polyomino[][] layout = defaultLayout;
		for (int i = quarterTurns; i > 0; i--)
		{
			// Rotate the layout 90 degrees `quarterTurns` times
			Polyomino[][] result = new Polyomino[height][width];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					result[y][width-1-x] = layout[x][y];
				}
			}

			// Set the new layout and swap width/height
			layout = result;
			int temp = width;
			width = height;
			height = temp;
		}

		// Define some variables for the loop to create the Blocks
		GridBagConstraints c;
		Color color, playerColor = inventory.color, backgroundColor = Game.NOCOLOR;
		Block block;
		int blockSize = longEdgeSize / (quarterTurns % 2 == 0 ? width : height);
		blocks = new Block[width][height];
		int[] edges = new int[4];
		Polyomino poly;

		// Create a Block for each space in the layout, and put it on the tray
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				// Get the type of polyomino at that grid location
				poly = layout[x][y];

				// Check each direction. If it's not OOB, and it shares a polyomino type, 
				// then don't put a border between the two blocks. Otherwise, do.
				edges[0] = (y != 0        && poly == layout[x][y-1]) ? 0 : 1;
				edges[1] = (x != 0        && poly == layout[x-1][y]) ? 0 : 1;
				edges[2] = (y != height-1 && poly == layout[x][y+1]) ? 0 : 1;
				edges[3] = (x != width-1  && poly == layout[x+1][y]) ? 0 : 1;

				// Create the gridbagconstraints
				c = new GridBagConstraints();
				c.gridx = x;
				c.gridy = y;

				// Set the color. Any empty or used tile will be background 
				// colored, but will still be there (for structure)
				if (poly != Polyomino.O0 && inventory.isAvailable(poly))
				{
					color = playerColor;
				}
				else
				{
					color = backgroundColor;
				}

				block = new Block(color, blockSize, edges, poly);
				blocks[x][y] = block;
				add(block, c);
			}
		}
	}

	public BlockTray(BlockInventory inventory, int longEdgeSize) { this(inventory, longEdgeSize, 0); }
	public BlockTray(BlockInventory inventory) { this(inventory, defaultSize, 0); }

	public void setInventory(BlockInventory inventory)
	{
		this.inventory = inventory;
		refresh();
	}

	public BlockInventory getInventory()
	{
		return inventory;
	}

	public void refresh()
	{
		Color playerColor = inventory.color;
		Color backgroundColor = Game.NOCOLOR;
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				if (inventory.isAvailable(blocks[x][y].poly)
				&&  blocks[x][y].poly != Polyomino.O0)
				{
					blocks[x][y].setColor(playerColor);
				}
				else
				{
					blocks[x][y].setColor(backgroundColor);
				}
			}
		}
	}

	private class Block extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private int u, l, d, r;
		private Polyomino poly;

		public Block(Color color, int size, int[] edges, Polyomino poly)
		{
			setSize(size, size);
			setPreferredSize(new Dimension(size, size));

			u = edges[0];
			l = edges[1];
			d = edges[2];
			r = edges[3];
			this.poly = poly;

			setColor(color);
		}

		public void setColor(Color color)
		{
			setBackground(color);
			setBorder(BorderFactory.createMatteBorder(u, l, d, r, color.darker()));
		}
	}
}