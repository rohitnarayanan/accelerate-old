package test.accelerate;

import java.io.File;
import java.net.URI;

import accelerate.util.file.DirectorySynchronizer;
import accelerate.util.file.DirectorySynchronizer.DirSyncInput;
import accelerate.util.file.DirectorySynchronizer.DirSyncOutput;

/**
 * Basic class to quick test code
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@SuppressWarnings("all")
public class QuickSynchronizer {
	public static void main(String[] args) {
		try {
			DirSyncInput input = new DirSyncInput();
			input.copyToSource = false;
			input.copyToTarget = true;
			// input.overwriteTarget = true;
			input.ignoreExtns.add("DS_Store");
			input.sourceDir = new File(new URI("file:/D:/Rogger/Docs/TCS%20Docs/"));
			input.targetDir = new File(new URI("file:////192.168.1.46/rohitnarayanan/Documents/Docs/TCS%20Docs/"));

			DirSyncOutput output = DirectorySynchronizer.compare(input);

			if (output.errorFlag) {
				System.out.println("Errors -----------------------");
				System.out.println(output.message);
				return;
			}

			System.out.println("New Source Files -----------------------");
			output.newSourceFiles.forEach(System.out::println);
			System.out.println("\n\n\nNew Target Files -----------------------");
			output.newTargetFiles.forEach(System.out::println);
			System.out.println("\n\n\nConflit Files -----------------------");
			output.conflictedFiles.forEach((k, v) -> {
				System.out.println(k + "####" + v.conflictReason);
			});

			System.out.println("\n\n\nCopy Files -----------------------");
			DirectorySynchronizer.synchronize(input, output, 20);
			System.out.println(output.message);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
