package edu.luc.etl.osdi.processtree.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessTree {

	private static int IO_BUF_SIZE = 8192;
	private static int CHILD_LIST_SIZE = 16;

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	private final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out), IO_BUF_SIZE );
	private final Map<Integer, String> pMap = new HashMap<Integer, String>();
	private final Map<Integer, List<Integer>> tMap = new HashMap<Integer, List<Integer>>();

	public static void main(String[] args) throws Throwable { new ProcessTree().run(); }

	public void run() throws Throwable {
		final long start = System.currentTimeMillis();
		final Process.Parser parser = Process.createParser(in.readLine());

		String line = null;
		while ((line = in.readLine()) != null) {
			final Process p = parser.parseString(line);
			pMap.put(p.pid, p.cmd);
			if (! tMap.containsKey(p.ppid))
				tMap.put(p.ppid, new ArrayList<Integer>(CHILD_LIST_SIZE));
			tMap.get(p.ppid).add(p.pid);
		}

		printTrees(0, 0);
		out.flush();

		System.out.println(System.currentTimeMillis() - start + " ms");
	}

	protected void printTrees(int level, int ppid) throws Throwable {
		for (final int pid: tMap.get(ppid))
			printTree(level + 1, pid);
	}

	protected void printTree(int level, int pid) throws Throwable {
		for (int k = 0; k < level; k++)
			out.append(' ');
		out.append(String.valueOf(pid));
		out.append(": ");
		out.append(pMap.get(pid));
		out.newLine();
		if (tMap.containsKey(pid))
			printTrees(level, pid);
	}
}
