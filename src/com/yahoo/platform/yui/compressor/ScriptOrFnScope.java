/*
 * YUI Compressor
 * Author: Julien Lecomte - http://www.julienlecomte.net/
 * Copyright (c) 2009 Yahoo! Inc.  All rights reserved.
 * The copyrights embodied in the content of this file are licensed
 * by Yahoo! Inc. under the BSD (revised) open source license.
 */

package com.yahoo.platform.yui.compressor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ScriptOrFnScope {

	private int braceNesting;
	private ScriptOrFnScope parentScope;
	private List<ScriptOrFnScope> subScopes;
	private Map<String, JavaScriptIdentifier> identifiers = new HashMap<String, JavaScriptIdentifier>();
	private Map<String, String> hints = new HashMap<String, String>();
	private boolean markedForMunging = true;
	private int varcount = 0;

	ScriptOrFnScope(int braceNesting, ScriptOrFnScope parentScope) {
		this.braceNesting = braceNesting;
		this.parentScope = parentScope;
		this.subScopes = new ArrayList<ScriptOrFnScope>();
		if (parentScope != null) {
			parentScope.subScopes.add(this);
		}
	}

	int getBraceNesting() {
		return braceNesting;
	}

	ScriptOrFnScope getParentScope() {
		return parentScope;
	}

	JavaScriptIdentifier declareIdentifier(String symbol) {
		JavaScriptIdentifier identifier = identifiers.get(symbol);
		if (identifier == null) {
			identifier = new JavaScriptIdentifier(symbol, this);
			identifiers.put(symbol, identifier);
		}
		return identifier;
	}

	JavaScriptIdentifier getIdentifier(String symbol) {
		return (JavaScriptIdentifier) identifiers.get(symbol);
	}

	void addHint(String variableName, String variableType) {
		hints.put(variableName, variableType);
	}

	void preventMunging() {
		if (parentScope != null) {
			// The symbols in the global scope don't get munged,
			// but the sub-scopes it contains do get munged.
			markedForMunging = false;
		}
	}

	private List<String> getUsedSymbols() {
		List<String> result = new ArrayList<String>();
		for(JavaScriptIdentifier identifier  : identifiers.values()) {
			String mungedValue = identifier.getMungedValue();
			if (mungedValue == null) {
				mungedValue = identifier.getValue();
			}
			result.add(mungedValue);
		}
		return result;
	}

	private List<String> getAllUsedSymbols() {
		List<String> result = new ArrayList<String>();
		ScriptOrFnScope scope = this;
		while (scope != null) {
			result.addAll(scope.getUsedSymbols());
			scope = scope.parentScope;
		}
		return result;
	}

	int incrementVarCount() {
		varcount++;
		return varcount;
	}

	void munge() {

		if (!markedForMunging) {
			// Stop right here if this scope was flagged as unsafe for munging.
			return;
		}

		int pickFromSet = 1;

		// Do not munge symbols in the global scope!
		if (parentScope != null) {

			List<String> freeSymbols = new ArrayList<String>();

			freeSymbols.addAll(JavaScriptCompressor.ones);
			freeSymbols.removeAll(getAllUsedSymbols());
			if (freeSymbols.isEmpty()) {
				pickFromSet = 2;
				freeSymbols.addAll(JavaScriptCompressor.twos);
				freeSymbols.removeAll(getAllUsedSymbols());
			}
			if (freeSymbols.isEmpty()) {
				pickFromSet = 3;
				freeSymbols.addAll(JavaScriptCompressor.threes);
				freeSymbols.removeAll(getAllUsedSymbols());
			}
			if (freeSymbols.isEmpty()) {
				throw new IllegalStateException("The YUI Compressor ran out of symbols. Aborting...");
			}

			for( JavaScriptIdentifier identifier : identifiers.values()) {
				if (freeSymbols.size() == 0) {
					pickFromSet++;
					if (pickFromSet == 2) {
						freeSymbols.addAll(JavaScriptCompressor.twos);
					} else if (pickFromSet == 3) {
						freeSymbols.addAll(JavaScriptCompressor.threes);
					} else {
						throw new IllegalStateException("The YUI Compressor ran out of symbols. Aborting...");
					}
					// It is essential to remove the symbols already used in
					// the containing scopes, or some of the variables declared
					// in the containing scopes will be redeclared, which can
					// lead to errors.
					freeSymbols.removeAll(getAllUsedSymbols());
				}

				String mungedValue;
				if (identifier.isMarkedForMunging()) {
					mungedValue = freeSymbols.remove(0);
				} else {
					mungedValue = identifier.getValue();
				}
				identifier.setMungedValue(mungedValue);
			}
		}

		for(ScriptOrFnScope scope : subScopes) {
			scope.munge();
		}
	}
}
