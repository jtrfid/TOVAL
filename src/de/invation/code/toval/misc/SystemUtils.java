package de.invation.code.toval.misc;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemUtils {

	public static OperatingSystemType getOperatingSystem() {
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.contains("win"))
			return OperatingSystemType.win;
		if (OS.contains("mac"))
			return OperatingSystemType.mac;
		if (OS.contains("nux") || OS.contains("sunos"))
			return OperatingSystemType.linux;
		return null;
	}
	
	public static boolean runCommand(String[] command) {
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String output = null;
			while ((output = in.readLine()) != null) {}
			output = null;
			while ((output = err.readLine()) != null) {}

			in.close();
			p.waitFor();
//			System.out.println("ExitValue: " + p.exitValue());
//			//			System.out.println(p.getInputStream().read());
//			//			System.out.println(p.getErrorStream().read());
			p.exitValue();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public enum OperatingSystemType {
		win, linux, mac;
	}

}
