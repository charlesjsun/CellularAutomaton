import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class CellularPanel extends JPanel
{

	public static final int SIDE_PADDING = 0;
	public static final int TOP_PADDING = 0;
	public static final int SCROLL_BAR_WIDTH = 16;

	public static final int MENU_HEIGHT = 50;

	private GameType gameType;

	private Dimension dimension;
	private Dimension gameDimension;
	private Dimension menuDimension;

	private CellularGrid<? extends ICell<?>> grid;

	private int width;
	private int height;

	private Timer timer;

	private int deltatime; // in milliseconds

	private int size;

	private GameComponent gameComponent;

	private MenuComponent menuComponent;

	public class GameComponent extends JComponent
	{

		private JScrollBar hbar;
		private JScrollBar vbar;

		public GameComponent()
		{
			setPreferredSize(gameDimension);

			setLayout(new BorderLayout());

			hbar = new JScrollBar(JScrollBar.HORIZONTAL);
			vbar = new JScrollBar(JScrollBar.VERTICAL);

			hbar.setPreferredSize(new Dimension((int)gameDimension.getWidth(), SCROLL_BAR_WIDTH));
			vbar.setPreferredSize(new Dimension(SCROLL_BAR_WIDTH, (int) gameDimension.getHeight() - SCROLL_BAR_WIDTH));

			hbar.setUnitIncrement(1);
			hbar.setBlockIncrement(1);

			vbar.setUnitIncrement(1);
			vbar.setBlockIncrement(1);

			hbar.addAdjustmentListener(new AdjustmentListener()
			{
				public void adjustmentValueChanged(AdjustmentEvent e) {
					getParent().repaint();
				}
			});
			vbar.addAdjustmentListener(new AdjustmentListener()
			{
				public void adjustmentValueChanged(AdjustmentEvent e) {
					getParent().repaint();
				}
			});

			add(hbar, BorderLayout.SOUTH);
			add(vbar, BorderLayout.EAST);

			addMouseListener(new MouseListener()
			{
				@Override
				public void mouseClicked(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e)
				{
					if (e.getY() < gameDimension.getHeight() - SCROLL_BAR_WIDTH && e.getX() < gameDimension.getWidth() - SCROLL_BAR_WIDTH)
					{
						int x = e.getX() + hbar.getValue();
						int y = e.getY() + vbar.getValue();
						int gx = x / size;
						int gy = y / size;
						if (gx >= 0 && gx < width && gy >= 0 && gy < height)
						{
							if (e.getButton() == MouseEvent.BUTTON1)
							{
								grid.onLeftClick(gx, gy);
							}
							else if (e.getButton() == MouseEvent.BUTTON3)
							{
								grid.onRightClick(gx, gy);
							}
						}
					}
					getParent().repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}
			});

			addMouseWheelListener(new MouseWheelListener()
			{
				@Override
				public void mouseWheelMoved(MouseWheelEvent e)
				{
					if (e.getY() < gameDimension.getHeight() - SCROLL_BAR_WIDTH && e.getX() < gameDimension.getWidth() - SCROLL_BAR_WIDTH)
					{
						double x = e.getX() + hbar.getValue();
						double y = e.getY() + vbar.getValue();
						double gx = x / size;
						double gy = y / size;

						updateZoom(-e.getWheelRotation());

						//hbar.setValue((int)(gx * size - (gameDimension.getWidth() - SCROLL_BAR_WIDTH) / 2));
						//vbar.setValue((int)(gy * size - (gameDimension.getHeight() - SCROLL_BAR_WIDTH) / 2));
						hbar.setValue((int)(gx * size - e.getX()));
						vbar.setValue((int)(gy * size - e.getY()));

						getParent().repaint();
					}
					else if (hbar.getBounds().contains(e.getX(), e.getY()))
					{
						hbar.setValue(hbar.getValue() + e.getWheelRotation() * 10);
						hbar.setValueIsAdjusting(true);
					}
					else if (vbar.getBounds().contains(e.getX(), e.getY()))
					{
						vbar.setValue(vbar.getValue() + e.getWheelRotation() * 10);
						vbar.setValueIsAdjusting(true);
					}
				}
			});

		}

		public void updateBar()
		{
			hbar.setMaximum((int)(width * size - (gameDimension.getWidth() - SCROLL_BAR_WIDTH) + hbar.getModel().getExtent()));
			vbar.setMaximum((int)(height * size - (gameDimension.getHeight() - SCROLL_BAR_WIDTH) +  + vbar.getModel().getExtent()));
		}

		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			grid.draw(g, size, hbar.getValue(), vbar.getValue());
		}

	}

	public class MenuComponent extends JComponent
	{

		private JButton reset;
		private JButton start;
		private JButton pause;
		private JButton next;
		private JScrollBar speed;
		private JLabel speedLabel;

		private static final int MAX_SPEED = 1000;

		public MenuComponent()
		{
			setPreferredSize(menuDimension);

			setLayout(new FlowLayout());

			reset = new JButton("Reset");
			start = new JButton("Start");
			pause = new JButton("Pause");
			pause.setEnabled(false);
			next = new JButton("Next");
			add(reset);
			add(start);
			add(pause);
			add(next);

			start.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!timer.isRunning())
					{
						timer.start();
						start.setEnabled(false);
						pause.setEnabled(true);
						next.setEnabled(false);
					}
				}
			});
			pause.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (timer.isRunning())
					{
						timer.stop();
						start.setEnabled(true);
						pause.setEnabled(false);
						next.setEnabled(true);
					}
				}
			});
			next.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!timer.isRunning())
						tick();
				}
			});
			reset.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					setupCellularGrid();
					getParent().repaint();
				}
			});

			speedLabel = new JLabel("Speed: " + MAX_SPEED + "ms");
			speedLabel.setPreferredSize(new Dimension(90, 16));
			speedLabel.setHorizontalAlignment(JLabel.RIGHT);
			add(speedLabel);

			speed = new JScrollBar(JScrollBar.HORIZONTAL);
			speed.setMinimum(1);
			speed.setMaximum(MAX_SPEED + speed.getModel().getExtent());
			speed.setValue(MAX_SPEED);
			deltatime = MAX_SPEED;
			speed.setPreferredSize(new Dimension(150, 16));
			add(speed);
			speed.addAdjustmentListener(new AdjustmentListener()
			{
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e)
				{
					deltatime = e.getValue();
					timer.setDelay(deltatime);
					timer.setInitialDelay(0);
					speedLabel.setText("Speed: " + deltatime + "ms");
				}
			});

			addMouseWheelListener(new MouseWheelListener()
			{
				@Override
				public void mouseWheelMoved(MouseWheelEvent e)
				{
					if (speed.getBounds().contains(e.getX(), e.getY()))
					{
						speed.setValue(speed.getValue() + e.getWheelRotation());
						speed.setValueIsAdjusting(true);
					}
				}
			});

		}

		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		}

	}

	public CellularPanel(GameType gameType, Dimension d)
	{
		this.dimension = d;
		this.gameDimension = new Dimension((int)dimension.getWidth(), (int)dimension.getHeight() - MENU_HEIGHT);
		this.menuDimension = new Dimension((int)dimension.getWidth(), MENU_HEIGHT);

		this.gameType = gameType;

		this.width = (int)(gameDimension.getWidth() - SCROLL_BAR_WIDTH) / 2;
		this.height = (int)(gameDimension.getHeight() - SCROLL_BAR_WIDTH) / 2;

		this.deltatime = 10;
		this.size = 2;

		setPreferredSize(dimension);
		setBackground(gameType.getBackgroundColor());

		setupCellularGrid();
		setupUI();
		setupTimer();

	}

	private void setupCellularGrid()
	{
		grid = new CellularGrid(gameType.getCellClass(), width, height, gameDimension);
	}

	private void setupTimer()
	{
		timer = new Timer(deltatime, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				tick();
			}
		});
		timer.setInitialDelay(0);
	}

	private void tick()
	{
		grid.update();
		repaint();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

	}

	private void setupUI()
	{
		setLayout(new BorderLayout());

		gameComponent = new GameComponent();
		add(gameComponent, BorderLayout.NORTH);

		menuComponent = new MenuComponent();
		add(menuComponent, BorderLayout.SOUTH);

		updateZoom(2);
	}



	private void updateZoom(int increment)
	{
		size = Math.min(Math.max(2, size + increment), 30);
		gameComponent.updateBar();
	}

}
