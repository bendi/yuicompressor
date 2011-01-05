package com.yahoo.platform.yui.compressor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option typeOpt = parser.addStringOption("type");
		CmdLineParser.Option verboseOpt = parser.addBooleanOption('v', "verbose");
		CmdLineParser.Option nomungeOpt = parser.addBooleanOption("nomunge");
		CmdLineParser.Option linebreakOpt = parser.addStringOption("line-break");
		CmdLineParser.Option preserveSemiOpt = parser.addBooleanOption("preserve-semi");
		CmdLineParser.Option disableOptimizationsOpt = parser.addBooleanOption("disable-optimizations");
		CmdLineParser.Option helpOpt = parser.addBooleanOption('h', "help");
		CmdLineParser.Option charsetOpt = parser.addStringOption("charset");
		CmdLineParser.Option outputFilenameOpt = parser.addStringOption('o', "output");
 *

private static void usage() {
	System.err.println(
			"\nUsage: java -jar yuicompressor-x.y.z.jar [options] [input file]\n\n"

					+ "Global Options\n"
					+ "  -h, --help                Displays this information\n"
					+ "  --type <js|css>           Specifies the type of the input file\n"
					+ "  --charset <charset>       Read the input file using <charset>\n"
					+ "  --line-break <column>     Insert a line break after the specified column number\n"
					+ "  -v, --verbose             Display informational messages and warnings\n"
					+ "  -o <file>                 Place the output into <file>. Defaults to stdout.\n"
					+ "                            Multiple files can be processed using the following syntax:\n"
					+ "                            java -jar yuicompressor.jar -o '.css$:-min.css' *.css\n"
					+ "                            java -jar yuicompressor.jar -o '.js$:-min.js' *.js\n\n"

					+ "JavaScript Options\n"
					+ "  --nomunge                 Minify only, do not obfuscate\n"
					+ "  --preserve-semi           Preserve all semicolons\n"
					+ "  --disable-optimizations   Disable all micro optimizations\n\n"

					+ "If no input file is specified, it defaults to stdin. In this case, the 'type'\n"
					+ "option is required. Otherwise, the 'type' option is required only if the input\n"
					+ "file extension is neither 'js' nor 'css'.");
}
*/
public class Config {

	@Option(name="--disable-optimizations", usage="Disable all micro optimizations")
	private boolean disableOptiomizations;

	@Option(name="--preserve-semi", usage="Preserve all semicolons")
	private boolean preserveAllSemiColons;

	@Option(name="--nomunge", usage="Minify only, do not obfuscate")
	private boolean nomunge;

	@Option(name="-v", aliases={"--verbose"}, usage="Display informational messages and warnings")
	private boolean verbose = false;

	@Option(name="-s", aliases={"--separator"}, usage="Output a separator between combined files")
	private boolean separator = false;

	@Option(name="-charset", usage="Read the input file using <charset>, default is UTF-8")
	private String charset = "UTF-8";

	@Option(name="-o", aliases={"--output"}, usage="Place the output into <file>. Defaults to stdout.")
	private File outputFile = null;

	public enum TYPE{CSS,JS}

	@Option(name="-t", aliases={"--type"}, usage="Specify type of combiner, allowed: js, css")
	private TYPE type = TYPE.JS;

	@Option(name="-line-break", usage="Insert a line break after the specified column number")
	private int lineBreak = -1;

	@Argument
	private List<String> arguments = new ArrayList<String>();

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) throws IOException {
		this.arguments = arguments;
	}
	public boolean isVerbose() {
		return verbose;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public boolean isSeparator() {
		return separator;
	}
	public void setSeparator(boolean separator) {
		this.separator = separator;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public File getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public int getLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(int lineBreak) {
		this.lineBreak = lineBreak;
	}

	public boolean isDisableOptiomizations() {
		return disableOptiomizations;
	}

	public void setDisableOptiomizations(boolean disableOptiomizations) {
		this.disableOptiomizations = disableOptiomizations;
	}

	public boolean isNomunge() {
		return nomunge;
	}

	public void setNomunge(boolean nomunge) {
		this.nomunge = nomunge;
	}

	public boolean isPreserveAllSemiColons() {
		return preserveAllSemiColons;
	}

	public void setPreserveAllSemiColons(boolean preserveAllSemiColons) {
		this.preserveAllSemiColons = preserveAllSemiColons;
	}

}
