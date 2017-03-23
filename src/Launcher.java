import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Launcher
{

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		Dimension d = new Dimension(640 * 3 / 2, 380 * 3 / 2);
		CellularPanel panel = new CellularPanel(GameType.CONWAYS_GAME_OF_LIFE, d);
		//CellularPanel panel = new CellularPanel(GameType.WIREWORLD, d);

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

		//CellularGrid<GameOfLifeCell> grid = new CellularGrid<>(GameOfLifeCell.class, 20, 20);
		//grid.onClick(0, 1);
		//grid.onClick(1, 2);
		//grid.onClick(2, 0);
		//grid.onClick(2, 1);
		//grid.onClick(2, 2);
		//grid.print();
//
		//for (int i = 0; i < 50; i++)
		//{
		//	System.out.println();
		//	grid.update();
		//	grid.print();
		//}
	}

}
