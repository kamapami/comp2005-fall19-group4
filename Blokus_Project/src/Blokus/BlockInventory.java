package Blokus;
import java.awt.Color;

public class BlockInventory {
	public Color color;

	public BlockInventory(Color color)
	{
		this.color = color;
	}

	public boolean isAvailable(Polyomino poly)
	{
		return true;
	}
}