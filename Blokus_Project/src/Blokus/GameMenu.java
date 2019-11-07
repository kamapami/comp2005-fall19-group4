package Blokus;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
		
public class GameMenu extends JPanel
{
	private static final long serialVersionUID = -7887556954455476971L;
	private Game game;
	private JPanel boardPanel = new JPanel();
	private BlockTray mainTray, leftTray, topTray, rightTray;
	private BlockDisplay display;
	private Tile[][] board;
	private int boardSize = 400;

	public GameMenu()
	{
		setLayout(new GridBagLayout());

		// Begin setting up board
		board = new Tile[Game.size][Game.size];
		boardPanel.setLayout(new GridLayout(Game.size, Game.size));
		GridBagConstraints c;
		
		c = new GridBagConstraints();
		c.gridx = c.gridy = 1;
		c.weightx = c.weighty = 0.2;
		add(boardPanel, c);
		Tile tile;

		for (int y = 0; y < Game.size; y++)
		{
			for (int x = 0; x < Game.size; x++)
			{
				tile = new Tile(boardSize / Game.size, Game.NOCOLOR);
				boardPanel.add(tile);
				board[x][y] = tile;
			}
		}
		// Finished setting up board

		// Set up active player block pool display
		mainTray = new BlockTray(new BlockInventory(Game.P1COLOR), boardSize);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = c.weighty = 0.2;
		add(mainTray, c);
		
		// Begin setting up opponent block pool displays
		leftTray = new BlockTray(new BlockInventory(Game.P2COLOR), (int) (boardSize*0.7), 3);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = c.weighty = 0.2;
		add(leftTray, c);

		topTray = new BlockTray(new BlockInventory(Game.P3COLOR), (int) (boardSize*0.7), 2);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = c.weighty = 0.2;
		add(topTray, c);

		rightTray = new BlockTray(new BlockInventory(Game.P4COLOR), (int) (boardSize*0.7), 1);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = c.weighty = 0.2;
		add(rightTray, c);
		// Finished setting up opponent block pool displays

		// Add button to go to next turn
		JButton nextTurnButton = new JButton("Next Turn");
		nextTurnButton.addActionListener(new nextTurnButtonListener());
		// cycleButton.setSize()
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		add(nextTurnButton, c);

		// Add selected block display
		display = new BlockDisplay((int) (boardSize*0.5) / 2);
		display.refresh();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		add(display, c);
	}

	public void newGame()
	{
		setGame(new Game(this));
	}

	public void setGame(Game game)
	{
		this.game = game;
		mainTray.getInventory().game = game;
		rightTray.getInventory().game = game;
		topTray.getInventory().game = game;
		leftTray.getInventory().game = game;
	}

	public void refreshBoard()
	{
		for (int y = 0; y < Game.size; y++)
		{
			for (int x = 0; x < Game.size; x++)
			{
				switch (game.getStateAt(x, y))
				{
					case Game.PLAYER1: board[x][y].setBackground(Game.P1COLOR); break;
					case Game.PLAYER2: board[x][y].setBackground(Game.P2COLOR); break;
					case Game.PLAYER3: board[x][y].setBackground(Game.P3COLOR); break;
					case Game.PLAYER4: board[x][y].setBackground(Game.P4COLOR); break;
					default: board[x][y].setBackground(Game.NOCOLOR);
				}
			}
		}
	}

	public void cycleTrays()
	{
		BlockInventory temp = mainTray.getInventory();
		mainTray.setInventory(leftTray.getInventory());
		leftTray.setInventory(topTray.getInventory());
		topTray.setInventory(rightTray.getInventory());
		rightTray.setInventory(temp);
	}

	public void refresh()
	{
		refreshBoard();
		mainTray.refresh();
		rightTray.refresh();
		topTray.refresh();
		leftTray.refresh();

		display.setPolyomino(game.selected);
		display.setColor(game.color);
		display.refresh();
	}

	private class nextTurnButtonListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent event) 
		{
			cycleTrays();
			game.nextTurn();
		}
	}
}