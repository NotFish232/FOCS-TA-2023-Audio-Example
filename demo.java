import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

class DemoPanel extends JPanel {
    private static final String song_dir = "songs/";
    private static final String assets_dir = "assets/";
    private static final int button_width = 50;
    private static final int button_height = 50;
    private static final ImageIcon pause_icon = load_image(assets_dir + "pause.png", button_width, button_height);
    private static final ImageIcon play_icon = load_image(assets_dir + "play.png", button_width, button_height);
    private static final ImageIcon previous_icon = load_image(assets_dir + "previous.png", button_width, button_height);
    private static final ImageIcon next_icon = load_image(assets_dir + "next.png", button_width, button_height);

    private JButton previous_btn, pause_btn, next_btn;
    private JLabel current_song_label;
    private ArrayList<File> song_files;
    private Clip current_song;
    private int song_idx;

    private class PreviousBtnListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            current_song.stop();
            song_idx -= 1;
            if (song_idx < 0) {
                song_idx = song_files.size() - 1;
            }
            play_song();
        }
    }

    private class PauseBtnListener implements ActionListener {
        private boolean state;

        public PauseBtnListener() {
            state = false;
        }

        public void set_state(boolean state) {
            this.state = state;
            ImageIcon new_icon = state ? play_icon : pause_icon;
            pause_btn.setIcon(new_icon);

            if (state) {
                current_song.start();
            } else {
                current_song.stop();
            }

        }

        public void actionPerformed(ActionEvent e) {
            set_state(!state);
        }
    }

    private class NextBtnListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            current_song.stop();
            song_idx = (song_idx + 1) % song_files.size();
            play_song();

        }
    }

    private class ClipLineListener implements LineListener {
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.CLOSE) {
                song_idx = (song_idx + 1) % song_files.size();
                play_song();
            }
        }
    }

    private static ImageIcon load_image(String path, int width, int height) {
        ImageIcon image = new ImageIcon(path);
        Image resized_image = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized_image);
    }

    public DemoPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel bottom_sub_panel = new JPanel();

        previous_btn = new JButton(previous_icon);
        pause_btn = new JButton(pause_icon);
        next_btn = new JButton(next_icon);

        previous_btn.addActionListener(new PreviousBtnListener());
        pause_btn.addActionListener(new PauseBtnListener());
        next_btn.addActionListener(new NextBtnListener());

        bottom_sub_panel.add(previous_btn);
        bottom_sub_panel.add(pause_btn);
        bottom_sub_panel.add(next_btn);

        add(bottom_sub_panel, BorderLayout.SOUTH);

        File[] files = (new File(song_dir)).listFiles();
        song_files = new ArrayList<File>(Arrays.asList(files));
        load_song();

        current_song_label = new JLabel();
        current_song_label.setHorizontalAlignment(SwingConstants.CENTER);
        add(current_song_label, BorderLayout.CENTER);
        set_current_song_label();

    }

    private void set_current_song_label() {
        String song_name = song_files.get(song_idx).getName();
        String label_text = String.format("Now Playing: '%s'", song_name);
        current_song_label.setText(label_text);

    }

    private void play_song() {
        load_song();
        set_current_song_label();

        PauseBtnListener listener = (PauseBtnListener) pause_btn.getActionListeners()[0];
        listener.set_state(true);
    }

    private void load_song() {
        try {
            File song_file = song_files.get(song_idx);
            AudioInputStream audio = AudioSystem.getAudioInputStream(song_file);
            current_song = AudioSystem.getClip();
            current_song.open(audio);
            current_song.addLineListener(new ClipLineListener());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

public class demo {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Audio Demo");
        frame.setSize(400, 400);
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new DemoPanel());
        frame.setVisible(true);

    }
}
