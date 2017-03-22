import java.awt.Color;

public interface ICell<T extends ICell<T>>
{
	T getNextState(final CellularGrid<T> grid, int x, int y);
	void onLeftClick();
	void onRightClick();
	Color getColor();
}
