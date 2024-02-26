import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements KeyListener, ActionListener {

    private final int UNIT_SIZE = 25;
    private final int SCREEN_WIDTH = 20;
    private final int SCREEN_HEIGHT = 20;
    private final int DELAY = 150;

    private boolean[][] gameBoard;
    private Food food;
    private Snake snake;
    private boolean running;
    private Timer timer;
    private Random random;
    private int score;

    public SnakeGame() {
        setPreferredSize(new Dimension(SCREEN_WIDTH * UNIT_SIZE, SCREEN_HEIGHT * UNIT_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        gameBoard = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];
        initGame();
    }

    private void initGame() {
        gameBoard = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];
        snake = new Snake();
        food = new Food();
        running = true;
        random = new Random();
        score = 0;
        spawnFood();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void spawnFood() {
        int x, y;
        do {
            x = random.nextInt(SCREEN_WIDTH);
            y = random.nextInt(SCREEN_HEIGHT);
        } while (gameBoard[x][y] || snake.contains(new Point(x, y)));
        food.setPosition(x, y);
    }

    private void move() {
        snake.move();
        if (snake.collidesWithWall(SCREEN_WIDTH, SCREEN_HEIGHT) || snake.collidesWithItself()) {
            gameOver();
        } else if (snake.head().equals(food.getPosition())) {
            snake.grow();
            spawnFood();
            score += 10; // Increase score when food is eaten
        }
    }

    private void gameOver() {
        running = false;
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score, "Game Over", JOptionPane.PLAIN_MESSAGE);
        int option = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Restart Game", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            initGame();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw snake
        g.setColor(Color.GREEN);
        for (Point segment : snake.getBody()) {
            g.fillRect(segment.x * UNIT_SIZE, segment.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
        }
        // Draw food
        g.setColor(Color.RED);
        g.fillRect(food.getPosition().x * UNIT_SIZE, food.getPosition().y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                snake.changeDirection('U');
                break;
            case KeyEvent.VK_DOWN:
                snake.changeDirection('D');
                break;
            case KeyEvent.VK_LEFT:
                snake.changeDirection('L');
                break;
            case KeyEvent.VK_RIGHT:
                snake.changeDirection('R');
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.requestFocusInWindow(); // Set focus on the game panel
    }

    private class Snake {
        private ArrayList<Point> body;
        private char direction;

        public Snake() {
            body = new ArrayList<>();
            body.add(new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
            direction = 'R';
        }

        public ArrayList<Point> getBody() {
            return body;
        }

        public Point head() {
            return body.get(0);
        }

        public boolean contains(Point point) {
            return body.contains(point);
        }

        public void move() {
            Point newHead = new Point(head());
            switch (direction) {
                case 'U':
                    newHead.y--;
                    break;
                case 'D':
                    newHead.y++;
                    break;
                case 'L':
                    newHead.x--;
                    break;
                case 'R':
                    newHead.x++;
                    break;
            }
            body.add(0, newHead);
            body.remove(body.size() - 1);
        }

        public boolean collidesWithWall(int screenWidth, int screenHeight) {
            Point head = head();
            return head.x < 0 || head.x >= screenWidth || head.y < 0 || head.y >= screenHeight;
        }

        public boolean collidesWithItself() {
            Point head = head();
            for (int i = 1; i < body.size(); i++) {
                if (body.get(i).equals(head)) {
                    return true;
                }
            }
            return false;
        }

        public void grow() {
            body.add(body.get(body.size() - 1));
        }

        public void changeDirection(char newDirection) {
            switch (newDirection) {
                case 'U':
                case 'D':
                    if (direction != 'U' && direction != 'D') {
                        direction = newDirection;
                    }
                    break;
                case 'L':
                case 'R':
                    if (direction != 'L' && direction != 'R') {
                        direction = newDirection;
                    }
                    break;
            }
        }
    }

    private class Food {
        private Point position;

        public Food() {
            position = new Point();
        }

        public Point getPosition() {
            return position;
        }

        public void setPosition(int x, int y) {
            position.setLocation(x, y);
        }
    }
}
