package musical.squares;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

public class SquaresFrame extends JFrame implements ItemListener {

    private final Squares SQUARES;
    private final SquaresView VIEW;
    private JButton play;
    private JButton clear;
    private JButton stop;
    private JPanel playButtonsPanel = new JPanel(new GridLayout(1, Squares.ROW));
    private JPanel instructionPanel = new JPanel(new GridLayout(1, Squares.ROW));
    private JPanel squaresAndPlays;
    private Box UIControlPanel;
    private JComboBox<Scale> scaleOptions;
    private JComboBox<Instruments> instrumentOptions;
    private JLabel scaleLabel;
    private JLabel instrumentLabel;
    private final int DELAY = 200;
    boolean playing = false;
    ButtonGroup controlButtons = new ButtonGroup();


    public SquaresFrame(SquareMouseListener listener, SquaresView view) {

        this.SQUARES = view.getSquares();
        this.VIEW = view;

        setSize(1175, 380);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Musical Squares");
        setLayout(new BorderLayout());

        setUpControlPanel();

        setUpInstructionPanel();
        setUpSquaresAndPlays(listener, view);

        add(squaresAndPlays);
        add(UIControlPanel, BorderLayout.EAST);
    }

    private void setUpSquaresAndPlays(SquareMouseListener listener, SquaresView view) {
        squaresAndPlays = new JPanel(new BorderLayout());
        squaresAndPlays.setBackground(Color.WHITE);
        stop.addActionListener(ActionEvent -> stopPlaying());
        clear.addActionListener(ActionEvent -> clearNotes());
        play.addActionListener(ActionEvent -> playNotes());
        createPlayStanzaButtons();
        view.addMouseListener(listener);
        squaresAndPlays.add(instructionPanel, BorderLayout.NORTH);
        squaresAndPlays.add(playButtonsPanel, BorderLayout.SOUTH);
        squaresAndPlays.add(view, BorderLayout.CENTER);
    }

    private void setUpInstructionPanel() {
        JTextArea welcome = new JTextArea("Welcome to Musical Squares! To play, click squares along the grid. Select a scale " +
                "and an instrument from the select buttons. \nHit play at the bottom of each column to play a single column or hit play on the right" +
                " to play all the stanzas in a row.");
        welcome.setLineWrap(true);
        instructionPanel.add(welcome);
    }

    private void setUpControlPanel() {
        Dimension panelSize = new Dimension(200, 80);
        sizePanel(panelSize);
        UIControlPanel.add(createFiller(20, 30));
        Dimension buttonSize = new Dimension(100, 30);
        setUpControlButtons(buttonSize);

        setUpScaleControl();
        setUpInstrumentControl();
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        addComponentsToControlPanel(blackLine);
    }

    private void addComponentsToControlPanel(Border blackLine) {

        UIControlPanel.add(scaleLabel);
        UIControlPanel.add(scaleOptions);
        UIControlPanel.add(createFiller(20, 20));
        UIControlPanel.add(instrumentLabel);
        UIControlPanel.add(instrumentOptions);
        UIControlPanel.add(createFiller(20, 20));
        UIControlPanel.setBorder(blackLine);
    }

    private void setUpInstrumentControl() {
        instrumentOptions = new JComboBox<>(Instruments.values());
        instrumentOptions.addItemListener(this);
        instrumentLabel = new JLabel("Choose Instrument:");
        instrumentLabel.setAlignmentX(UIControlPanel.getAlignmentX());
    }

    private void setUpScaleControl() {
        scaleOptions = new JComboBox<>(Scale.values());
        scaleOptions.addItemListener(this);
        scaleLabel = new JLabel("Choose Scale:");
        scaleLabel.setAlignmentX(UIControlPanel.getAlignmentX());
    }

    private void setUpControlButtons(Dimension buttonSize) {
        controlButtons.add(play = createButton("Play", buttonSize));
        play.setAlignmentX(UIControlPanel.getAlignmentX());
        UIControlPanel.add(play);
        UIControlPanel.add(createFiller(20, 20));
        controlButtons.add(stop = createButton("Stop", buttonSize));
        stop.setAlignmentX(UIControlPanel.getAlignmentX());
        UIControlPanel.add(stop);
        UIControlPanel.add(createFiller(20, 20));
        controlButtons.add(clear = createButton("Clear", buttonSize));
        clear.setAlignmentX(UIControlPanel.getAlignmentX());
        UIControlPanel.add(clear);
        UIControlPanel.add(createFiller(20, 20));
    }

    private void sizePanel(Dimension panelSize) {
        UIControlPanel = Box.createVerticalBox();
        UIControlPanel.setPreferredSize(panelSize);
        UIControlPanel.setMaximumSize(panelSize);
        UIControlPanel.setMinimumSize(panelSize);
    }

    private void createPlayStanzaButtons() {
        for (int j = 0; j < Squares.ROW; j++) {
            JButton playStanzaButton = new JButton();
            //playStanzaButton.setBackground(new Color(47, 191, 51));
            playStanzaButton.setPreferredSize(new Dimension(SquaresView.CELL_SIZE, SquaresView.CELL_SIZE));
            URL imageUrl = ClassLoader.getSystemResource("icons8-circled-play-64.png");
            ImageIcon playIcon = new ImageIcon(new ImageIcon(imageUrl).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            playStanzaButton.setIcon(playIcon);
            int stanza = j;
            playStanzaButton.addActionListener(ActionEvent -> {
                playColumn(stanza);
                VIEW.repaint();
            });
            playButtonsPanel.add(playStanzaButton);
        }
    }

    private void playColumn(int stanza)  {
        SQUARES.playStanza(stanza);
        SQUARES.setStanza(stanza + 1);
    }

    private void stopPlaying() {
        playing = false;
        SQUARES.setStanza(0);
    }

    private void clearNotes() {
        SQUARES.clearSquares();
        VIEW.repaint();
    }

    private void playNotes() {
        if (playing) {
            return;
        }
        playing = true;
        SQUARES.setStanza(0);
        VIEW.repaint();
        Thread thread = new Thread(() -> {
            while (playing) {
                SQUARES.playNextLine();
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                VIEW.repaint();
            }
        });
        thread.start();
    }

    private JButton createButton(String text, Dimension size) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        return button;
    }

    private Box.Filler createFiller(int width, int height) {
        Dimension minSize = new Dimension(width, height);
        Dimension prefSize = new Dimension(width, height);
        Dimension maxSize = new Dimension(Short.MAX_VALUE, height);
        return (new Box.Filler(minSize, prefSize, maxSize));
    }


    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == scaleOptions) {
            SQUARES.changeScales((Scale) scaleOptions.getSelectedItem());
        }
        if (event.getSource() == instrumentOptions) {
            SQUARES.changeInstrument((Instruments) instrumentOptions.getSelectedItem());
        }
    }
}