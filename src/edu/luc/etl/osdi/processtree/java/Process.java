package edu.luc.etl.osdi.processtree.java;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Process {

	public final int pid;
	public final int ppid;
	public final String cmd;

	public Process(final int pid, final int ppid, final String cmd) {
		this.pid = pid;
		this.ppid = ppid;
		this.cmd = cmd;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Proc(");
		sb.append(pid);
		sb.append(", ");
		sb.append(ppid);
		sb.append(", ");
		sb.append(cmd);
		sb.append(")");
		return sb.toString();
	}

	public static interface Parser {
		Process parseString(String line);
	}

	public static Parser createParser(final String header) {
		final StringTokenizer st = new StringTokenizer(header, " \t");
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens())
			tokens.add(st.nextToken());
		final int iPid = tokens.indexOf("PID");
		final int iPPid = tokens.indexOf("PPID");
		final int iCmd = Math.max(header.indexOf("CMD"), header.indexOf("COMMAND"));
		final int iFirst = Math.min(iPid, iPPid);
		final int iSecond = Math.max(iPid, iPPid);
		final boolean pidFirst = iPid < iPPid;
		if (iPid < 0) throw new RuntimeException("required header field PID missing");
		if (iPPid < 0) throw new RuntimeException("required header field PPID missing");
		if (iCmd <= iSecond) throw new RuntimeException("required header field CMD or COMMAND missing");
		return new Parser() {
			public Process parseString(final String line) {
				final StringTokenizer st = new StringTokenizer(line, " \t");
				for (int i = 0; i < iFirst; i++) st.nextToken();
				final int first = Integer.parseInt(st.nextToken());
				for (int i = iFirst + 1; i < iSecond; i++) st.nextToken();
				final int second = Integer.parseInt(st.nextToken());
				final String cmd = line.substring(iCmd);
				return new Process(pidFirst ? first : second, pidFirst ? second : first, cmd);
			}
		};
	}
}
