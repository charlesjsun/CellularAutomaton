import java.awt.Color;

public interface ICell<T extends ICell<T>>
{

	T makeNew(int value);

	T getNextState(final CellularGrid<T> grid, int x, int y);

	void onLeftClick();

	void onRightClick();

	Color getColor();

}
