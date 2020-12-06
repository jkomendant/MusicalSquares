import javax.swing.*;
import java.awt.*;

public class SquaresFrame extends JFrame {

    private Squares squares;
    private SquaresView view;
    private JButton play;
    private JButton clear;
    private JButton stop;
    private JPanel playButtonsPanel;
    private JPanel viewAndButtons;
    private JPanel UIControlPanel;
    private int delay = 200;
    boolean playing = false;

    public SquaresFrame(SquareMouseListener listener, SquaresView view) {
        this.squares = view.getSquares();
        this.view = view;

        setSize(692, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Musical Squares");
        setLayout(new GridLayout(2,1));
        play = new JButton("Play");
        clear = new JButton("Clear");
        stop = new JButton("Stop");
        playButtonsPanel = new JPanel(new GridLayout(1, Squares.ROW));
        UIControlPanel = new JPanel(new GridLayout(1, 3));
        viewAndButtons = new JPanel(new BorderLayout());

        stop.addActionListener(ActionEvent -> stopPlaying());
        clear.addActionListener(ActionEvent -> squares.clearSquares());
        play.addActionListener(ActionEvent -> playNotes());

        //might want to separate out into a separate function
        for (int j = 0; j < Squares.ROW; j++) {
                JButton playStanzaButton = new JButton();
                playStanzaButton.setPreferredSize(new Dimension(SquaresView.CELL_SIZE, SquaresView.CELL_SIZE));
                ImageIcon playIcon = new ImageIcon(new ImageIcon ("icons8-circled-play-64.png").getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
                playStanzaButton.setIcon(playIcon);

                int finalStanza = j;
                playStanzaButton.addActionListener(ActionEvent -> playColumn(finalStanza));
                playButtonsPanel.add(playStanzaButton);
            }

        view.addMouseListener(listener);
        viewAndButtons.add(playButtonsPanel, BorderLayout.NORTH);
        viewAndButtons.add(view, BorderLayout.CENTER);
        add(viewAndButtons);


        UIControlPanel.add(play);
        UIControlPanel.add(clear);
        UIControlPanel.add(stop);
        add(UIControlPanel);

    }

    private void playColumn(int finalStanza) {
        squares.playStanza(finalStanza);
        squares.setStanza(finalStanza + 1);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // for some reason this line causes it not be highlighted
        squares.setStanza(0);
        //
    }

    private void stopPlaying() {
        playing = false;
        squares.setStanza(0);
    }

    private void playNotes() {
        playing = true;
        Thread thread = new Thread(() -> {
            while (playing) {
                squares.playNextLine();
                //view.repaint();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
