package watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Set;
import java.util.TreeSet;

import server.Server;

public class MainWatch implements Runnable {

	public Set<File> jarsToAdd = new TreeSet<File>();
	public Set<File> jarsToRemove = new TreeSet<File>();

	public Path dir;

	public MainWatch(Path dir) {
		this.dir = dir;
		
	}

	public Set<File> getJarsToAdd() {
		return jarsToAdd;
	}

	public Set<File> getJarsToRemove() {
		return jarsToRemove;
		
	}

	// watches our directory for current jars and new jars to be dropped
	public void run() {

		// handles watching for new jars when they get dropped in
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE);

			while (true) {
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException ex) {
					return;
				}

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();

					Path filePath = dir.resolve(filename);
					// this if statement handles the case when a new file gets
					// dropped
					if (kind == ENTRY_CREATE) {
						if (filePath.toString().contains(".jar")) {
							File fileToAdd = new File(filePath.toString());
							Server.accessLogger.info("file to Add is: " + filePath.toString());
							jarsToAdd.add(fileToAdd);

						}
					}
					// if the jar has been deleted, add it to remove set
					else if (kind == ENTRY_DELETE) {

						jarsToRemove.add(new File(filePath.toString()));
					}

				}
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}

		}
		// should only occur if the directory does not exist
		catch (IOException ex) {
			System.err.println(ex);
		}

	}

	// Below in comments is a proof of concept to show that the watcher service
	// works
	//
	// public static void main(String[] args) throws IOException,
	// InterruptedException {
	// // Folder we are going to watch
	// Path folder = Paths.get("jars");
	// MainWatch watcher = new MainWatch(folder);
	// watcher.run();
	//
	// }
}
