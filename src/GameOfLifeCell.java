import java.awt.Color;

public class GameOfLifeCell implements ICell<GameOfLifeCell>
{

	private boolean isAlive = false;

	public GameOfLifeCell makeNew(int value)
	{
		GameOfLifeCell c = new GameOfLifeCell();
		c.isAlive = value == 1;
		return c;
	}

	@Override
	public GameOfLifeCell getNextState(CellularGrid<GameOfLifeCell> grid, int x, int y)
	{
		int countAlive = 0;
		for (int j = -1; j <= 1; j++)
		{
			for (int i = -1; i <= 1; i++)
			{
				if (!(i == 0 && j == 0) && grid.getCell(x + i, y + j) != null)
				{
					if (grid.getCell(x + i, y + j).isAlive)
					{
						countAlive++;
					}
				}
			}
		}

		GameOfLifeCell newCell = new GameOfLifeCell();
		if (isAlive)
		{
			if (countAlive < 2 || countAlive > 3)
			{
				newCell.isAlive = false;
			}
			else
			{
				newCell.isAlive = true;
			}
		}
		else
		{
			if (countAlive == 3)
			{
				newCell.isAlive = true;
			}
		}

		return newCell;
	}

	@Override
	public void onLeftClick()
	{
		isAlive = true;
	}

	@Override
	public void onRightClick()
	{
		isAlive = false;
	}

	@Override
	public Color getColor()
	{
		return isAlive ? new Color(255, 191, 0) : new Color(150, 150, 150);
	}

	@Override
	public String toString()
	{
		return isAlive ? "1" : "0";
	}

}
