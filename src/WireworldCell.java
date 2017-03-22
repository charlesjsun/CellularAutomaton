import java.awt.Color;

public class WireworldCell implements ICell<WireworldCell>
{

	public static final int EMPTY = 0;
	public static final int HEAD = 1;
	public static final int TAIL = 2;
	public static final int CONDUCTOR = 3;

	private int state = 0;

	@Override
	public WireworldCell getNextState(CellularGrid<WireworldCell> grid, int x, int y)
	{
		WireworldCell newCell = new WireworldCell();
		switch (state)
		{
			case EMPTY:
				break;
			case HEAD:
				newCell.state = TAIL;
				break;
			case TAIL:
				newCell.state = CONDUCTOR;
				break;
			case CONDUCTOR:
				int heads = 0;
				for (int j = -1; j <= 1; j++)
				{
					for (int i = -1; i <= 1; i++)
					{
						if (!(i == 0 && j == 0) && grid.getCell(x + i, y + j) != null)
						{
							if (grid.getCell(x + i, y + j).state == HEAD)
							{
								heads++;
							}
						}
					}
				}
				if (heads == 1 || heads == 2)
					newCell.state = HEAD;
				else
					newCell.state = CONDUCTOR;
				break;
		}
		return newCell;
	}

	@Override
	public void onLeftClick()
	{
		if (state == EMPTY)
		{
			state = CONDUCTOR;
		}
		else
		{
			state = EMPTY;
		}
	}

	@Override
	public void onRightClick()
	{
		if (state == HEAD)
		{
			state = TAIL;
		}
		else if (state == TAIL)
		{
			state = CONDUCTOR;
		}
		else
		{
			state = HEAD;
		}
	}

	@Override
	public Color getColor()
	{
		switch (state)
		{
			case EMPTY:
				return Color.BLACK;
			case HEAD:
				return Color.BLUE;
			case TAIL:
				return Color.RED;
			case CONDUCTOR:
				return Color.YELLOW;
		}
		return null;
	}
}
