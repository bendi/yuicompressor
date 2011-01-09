/*
 * YUI Compressor
 * Author: Julien Lecomte - http://www.julienlecomte.net/
 * Copyright (c) 2009 Yahoo! Inc.  All rights reserved.
 * The copyrights embodied in the content of this file are licensed
 * by Yahoo! Inc. under the BSD (revised) open source license.
 */

package com.yahoo.platform.yui.compressor;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.Config.TYPE;

public class YUICompressor {

	public static void main(String args[]) {

		Config cfg = new Config();
		CmdLineParser parser = new CmdLineParser(cfg);

		Reader in = null;
		Writer out = null;

		try {

			//parse the arguments
			parser.parseArgument(args);


			boolean verbose = cfg.isVerbose();

			String charset = cfg.getCharset();
			if (verbose) {
				System.err.println("\n[INFO] Using charset " + charset);
			}

			int linebreakpos = cfg.getLineBreak();

			Config.TYPE type = cfg.getType();

			List<String> files = cfg.getArguments();
			if (files.isEmpty()) {
				files = new ArrayList<String>();
				files.add("-"); // read from stdin
			}

			File output = cfg.getOutputFile();
//			String pattern[] = output != null ? output.split(":") : new String[0];

			for(String inputFilename : files) {

				try {
					if (inputFilename.equals("-")) {

						in = new InputStreamReader(System.in, charset);

					} else {

						if (type == null) {
							int idx = inputFilename.lastIndexOf('.');
							if (idx >= 0 && idx < inputFilename.length() - 1) {
								type = TYPE.valueOf(inputFilename.substring(idx + 1).toUpperCase());
							}
						}

						in = new InputStreamReader(new FileInputStream(inputFilename), charset);
					}

//					String outputFilename = output;
//					// if a substitution pattern was passed in
//					if (pattern.length > 1 && files.size() > 1) {
//						outputFilename = inputFilename.replaceFirst(pattern[0], pattern[1]);
//					}

					if (TYPE.JS == type) {

						try {

							JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {

								public void warning(String message, String sourceName,
										int line, String lineSource, int lineOffset) {
									if (line < 0) {
										System.err.println("\n[WARNING] " + message);
									} else {
										System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
									}
								}

								public void error(String message, String sourceName,
										int line, String lineSource, int lineOffset) {
									if (line < 0) {
										System.err.println("\n[ERROR] " + message);
									} else {
										System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
									}
								}

								public EvaluatorException runtimeError(String message, String sourceName,
										int line, String lineSource, int lineOffset) {
									error(message, sourceName, line, lineSource, lineOffset);
									return new EvaluatorException(message);
								}
							});

							// Close the input stream first, and then open the output stream,
							// in case the output file should override the input file.
							in.close(); in = null;

							if (output == null) {
								out = new OutputStreamWriter(System.out, charset);
							} else {
								out = new OutputStreamWriter(new FileOutputStream(output), charset);
							}

							boolean munge = cfg.isMunge();
							boolean preserveAllSemiColons = cfg.isPreserveAllSemiColons();
							boolean disableOptimizations = cfg.isDisableOptiomizations();

							compressor.compress(out, linebreakpos, munge, verbose,
									preserveAllSemiColons, disableOptimizations);

						} catch (EvaluatorException e) {

							e.printStackTrace();
							// Return a special error code used specifically by the web front-end.
							System.exit(2);

						}

					} else if (TYPE.CSS == type) {

						CssCompressor compressor = new CssCompressor(in);

						// Close the input stream first, and then open the output stream,
						// in case the output file should override the input file.
						in.close(); in = null;

						if (output == null) {
							out = new OutputStreamWriter(System.out, charset);
						} else {
							out = new OutputStreamWriter(new FileOutputStream(output), charset);
						}

						compressor.compress(out, linebreakpos);
					}

				} catch (IOException e) {

					e.printStackTrace();
					System.exit(1);

				} finally {

					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("Usage: java -jar yuicompressor-x.y.z.jar [options] [input file]\n");
			parser.printUsage(System.err);
		}
	}
}
