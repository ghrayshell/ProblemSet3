DE RAMOS, Ghrazielle
CUSTER, Mark John

NOTE: The implementation was supported using this concept below:

Queue Initialization:
The queue is a simple array (queue = []) in the consumer’s backend.
The queue holds the filenames of uploaded videos.

Queue Limit (QUEUE_MAX_SIZE):
The queue has a maximum size defined by the QUEUE_MAX_SIZE configuration.
When an upload request is received, the consumer checks if the queue has reached its maximum size:
If not full, the file is accepted, and the filename is added to the queue.
If full, the consumer deletes the uploaded file and responds with a 503 status code, telling the producer to retry later.

Leaky Bucket Simulation:
This is a simplified leaky bucket system where the consumer's queue acts as a buffer. When the buffer (queue) reaches its maximum size, additional uploads are rejected. This approach ensures the system doesn't crash due to overloading.

File Management:
Each uploaded file is stored in the uploads/ directory, and its filename is recorded in the queue for tracking.


Queue Management:
The queue array (queue) acts as the synchronization point for controlling upload flow. The producer and consumer communicate indirectly by checking whether the queue is full or not.
The consumer uses the queue to ensure that it doesn't accept more files than it can process at any time. The QUEUE_MAX_SIZE controls this capacity.

File Handling with fs Module:
The synchronous file operations (using fs.unlinkSync() to delete a file) ensure that files are deleted immediately when the queue is full, preventing unused files from piling up.

HTTP Communication:
HTTP requests are used for communication between the producer and consumer, ensuring that the consumer can process one upload at a time.
Error handling mechanisms (such as returning 503 when the queue is full) provide feedback between producer and consumer, ensuring synchronization of upload attempts.

Frontend Synchronization:
The React frontend uses fetch() to asynchronously pull the list of videos from the backend. If the backend is unavailable or the request fails, the frontend will catch errors and can display a relevant message or retry the request.
