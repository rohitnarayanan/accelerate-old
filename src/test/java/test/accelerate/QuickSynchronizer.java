package test.accelerate;

import java.io.File;

import accelerate.util.JSONUtil;
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
			input.overwriteTarget = true;
			input.ignoreExtns.add("DS_Store");
			input.sourceDir = new File("D:\\Rogger\\Docs\\TCS Docs");
			input.targetDir = new File("\\\\192.168.1.46\\rohitnarayanan\\Documents\\Docs\\TCS Docs");

			DirSyncOutput output = DirectorySynchronizer.compare(input);

			System.out.println("New Source Files -----------------------");
			output.newSourceFiles.forEach(System.out::println);
			System.out.println("\n\n\nNew Source Files -----------------------");
			output.newTargetFiles.forEach(System.out::println);
			System.out.println("\n\n\nConflit Files -----------------------");
			output.conflictedFiles.forEach((k, v) -> {
				System.out.println(k + "####" + v.conflictReason);
			});
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
