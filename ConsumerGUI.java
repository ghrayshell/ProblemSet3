import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.event.*;

public class ConsumerGUI {
    private JFrame frame;
    private JPanel panel;
    private BlockingQueue<File> queue = new ArrayBlockingQueue<>(10); // Queue for video files
    
    public ConsumerGUI() {
        frame = new JFrame("Media Upload Service");
        panel = new JPanel(new FlowLayout());
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(panel);

        // Start the consumer thread
        new Thread(new VideoConsumer()).start();

        // Set up GUI components for videos
        JButton loadButton = new JButton("Load Videos");
        loadButton.addActionListener(e -> loadVideos());
        panel.add(loadButton);
        
        frame.setVisible(true);
    }

    private void loadVideos() {
        panel.removeAll();
        try {
            File videoFolder = new File("videos");
            File[] videoFiles = videoFolder.listFiles((dir, name) -> name.endsWith(".mp4"));
            if (videoFiles != null) {
                for (File video : videoFiles) {
                    JButton videoButton = new JButton(video.getName());
                    videoButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // Play video on click
                            playVideo(video);
                        }
                    });
                    videoButton.addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) {
                            // Preview 10 seconds of video on hover
                            previewVideo(video);
                        }
                    });
                    panel.add(videoButton);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        panel.revalidate();
        panel.repaint();
    }

    private void previewVideo(File video) {
        // Logic to preview the first 10 seconds of the video
        System.out.println("Previewing video: " + video.getName());
    }

    private void playVideo(File video) {
        // Logic to play the full video
        System.out.println("Playing video: " + video.getName());
    }

    class VideoConsumer implements Runnable {
        public void run() {
            try {
                while (true) {
                    File file = queue.take();
                    // Handle file (e.g., save it or display in GUI)
                    System.out.println("Consumer received: " + file.getName());
                    loadVideos();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ConsumerGUI();
            }
        });
    }
    
}
