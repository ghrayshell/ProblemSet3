import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class Producer {
    private static final int MAX_QUEUE_LENGTH = 10; // Queue length
    private static BlockingQueue<File> queue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH); // Queue
    
    public static void main(String[] args) throws InterruptedException {
        // Get user inputs
        int p = Integer.parseInt(args[0]); // Number of producer threads
        int c = Integer.parseInt(args[1]); // Number of consumer threads
        int q = Integer.parseInt(args[2]); // Max queue length

        // Simulate reading files from separate folders
        for (int i = 0; i < p; i++) {
            new Thread(new VideoProducer(i)).start();
        }

        // Simulate the consumers accepting uploads
        for (int i = 0; i < c; i++) {
            new Thread(new Consumer(i)).start();
        }
    }

    // Producer thread class
    static class VideoProducer implements Runnable {
        private int id;

        public VideoProducer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                // Simulate reading video files from a folder
                File folder = new File("folder" + id); // Folder path
                File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp4"));

                if (files != null) {
                    for (File file : files) {
                        // Upload file to consumer
                        if (queue.remainingCapacity() > 0) {
                            queue.put(file); // Put file into the queue
                            System.out.println("Producer " + id + " uploaded: " + file.getName());
                        } else {
                            System.out.println("Queue is full. Dropping file: " + file.getName());
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Consumer thread class
    static class Consumer implements Runnable {
        private int id;

        public Consumer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                // Set up server socket for receiving the file uploads
                ServerSocket serverSocket = new ServerSocket(8080 + id);
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(new FileReceiver(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Receiver class to handle the file data
    static class FileReceiver implements Runnable {
        private Socket socket;

        public FileReceiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream in = socket.getInputStream()) {
                // Receive file data
                File outputFile = new File("output_" + socket.getPort() + ".mp4");
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Consumer saved: " + outputFile.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
