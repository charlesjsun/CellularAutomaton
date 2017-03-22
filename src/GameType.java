import java.awt.Color;

public class GameType
{

	public static final GameType CONWAYS_GAME_OF_LIFE = new GameType(GameOfLifeCell.class, new Color(200, 200, 200));
	public static final GameType WIREWORLD = new GameType(WireworldCell.class, new Color(100, 100, 100));

	private Class<? extends ICell<?>> cellClass;
	private Color backgroundColor;

	public GameType(Class<? extends ICell<?>> cellClass, Color backgroundColor)
	{
		this.cellClass = cellClass;
		this.backgroundColor = backgroundColor;
	}

	public Class<? extends ICell<?>> getCellClass()
	{
		return cellClass;
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
}
