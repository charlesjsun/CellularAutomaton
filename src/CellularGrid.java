import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

public class CellularGrid<T extends ICell<T>>
{

	private T[] grid;
	private T[] newGrid;

	private int width;
	private int height;

	private Dimension panelDim;

	public CellularGrid(Class<T> type, int width, int height, Dimension panelDim)
	{
		this.width = width;
		this.height = height;
		this.panelDim = panelDim;
		initGrid(type);
	}

	public static CellularGrid<? extends ICell<?>> parseGrid(GameType gameType, File file, Dimension dim)
	{
		Scanner scan;
		try
		{
			scan = new Scanner(file);
		}
		catch (Exception e)
		{
			System.out.println("Ouch!  Problem with file!! " + e);
			return null;
		}

		int w = scan.nextInt();
		int h = scan.nextInt();

		CellularGrid newGrid = new CellularGrid(gameType.getCellClass(), w, h, dim);

		int i = 0;
		while (scan.hasNext())
		{
			int nv = scan.nextInt();
			if (!newGrid.parseCell(i, nv))
			{
				return null;
			}
			++i;
		}

		System.out.println(i);

		if (i != w * h)
		{
			return null;
		}

		scan.close();

		return newGrid;
	}

	private boolean parseCell(int index, int value)
	{
		try
		{
			grid[index] = grid[index].makeNew(value);
		}
		catch (Exception e)
		{
			return false;
		}
		return grid[index] != null;
	}

	private void initGrid(Class<T> type)
	{
		grid = (T[]) Array.newInstance(type, width * height);
		for (int i = 0; i < grid.length; i++)
		{
			try
			{
				grid[i] = type.newInstance();
			}
			catch (IllegalAccessException | InstantiationException e)
			{
				System.err.println("ICell MUST have a DEFAULT CONSTRUCTOR (empty parameter list)");
				e.printStackTrace();
			}
		}
		newGrid = Arrays.copyOf(grid, grid.length);
	}

	public void update()
	{
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				T newCell = getCell(x, y).getNextState(this, x, y);

				if (newCell == getCell(x, y))
					throw new ValueException("ICell's getNextState(...) MUST RETURN A NEW INSTANCE");
				if (newCell == null)
					throw new ValueException("ICell's getNextState(...) MUST NOT RETURN A NULL VALUE");

				newGrid[x + y * width] = newCell;
			}
		}
		grid = Arrays.copyOf(newGrid, newGrid.length);
	}

	public void onLeftClick(int x, int y)
	{
		if (isInBound(x, y))
		{
			getCell(x, y).onLeftClick();
		}
	}

	public void onRightClick(int x, int y)
	{
		if (isInBound(x, y))
		{
			getCell(x, y).onRightClick();
		}
	}

	public T getCell(int x, int y)
	{
		if (isInBound(x, y))
		{
			return grid[x + y * width];
		}
		return null;
	}

	public boolean isInBound(int x, int y)
	{
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public void draw(Graphics g, int size, int xOffset, int yOffset)
	{
		int xgo = Math.max(0, xOffset / size);
		int ygo = Math.max(0, yOffset / size);
		int xgm = Math.min((int) (panelDim.getWidth() / size) + xgo + 1, width);
		int ygm = Math.min((int) (panelDim.getHeight() / size) + ygo + 1, height);
		for (int y = ygo; y < ygm; y++)
		{
			for (int x = xgo; x < xgm; x++)
			{
				g.setColor(getCell(x, y).getColor());
				g.fillRect(CellularPanel.SIDE_PADDING + x * size - xOffset, CellularPanel.TOP_PADDING + y * size - yOffset, size - 1, size - 1);
			}
		}
	}

	public void print()
	{
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				System.out.print(getCell(x, y) + " ");
			}
			System.out.println();
		}
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

}
