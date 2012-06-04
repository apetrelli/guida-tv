package com.google.code.guidatv.server.html;

import java.util.Deque;

import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

public abstract class PreprocessingRule extends Rule {
	private final Deque<Boolean> processingStack;

	public PreprocessingRule(Deque<Boolean> processingStack) {
		this.processingStack = processingStack;
	}

	@Override
	public void begin(String namespace, String name,
			Attributes attributes) throws Exception {
		processingStack.push(isProcessable(attributes));
	}

	public abstract boolean isProcessable(Attributes attributes);

	@Override
	public void end(String namespace, String name) {
		processingStack.pop();
	}
}