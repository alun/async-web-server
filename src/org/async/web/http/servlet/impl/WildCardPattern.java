package org.async.web.http.servlet.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WildCardPattern implements Comparable<WildCardPattern> {

	private String pattern;
	private List<String> strings = new ArrayList<String>();
	private boolean lastWild = false;
	private boolean firstWild = false;

	public WildCardPattern(String s) {
		this.pattern = s;
		int i = 0;
		int j = -1;
		lastWild = s.endsWith("*");
		firstWild = s.startsWith("*");
		while ((i = s.indexOf('*', j + 1)) >= 0) {
			add(s.substring(j + 1, i));
			j = i;
		}
		add(s.substring(j + 1, s.length()));
	}

	private void add(String token) {
		int l = token.length();
		if (l > 0) {
			strings.add(token);
		}
	}

	public boolean matches(String s) {
		int pos = 0;
		List<String> copyStrings = strings;
		if(firstWild){
			Collections.reverse(copyStrings);
		}
		for (int i = 0; i < copyStrings.size(); i++) {
			String token = copyStrings.get(i);
			if (firstWild){
				pos = s.lastIndexOf(token, i == 0 ? s.length()-1 : pos-1);
			}else{
				pos = s.indexOf(token, i == 0 ? pos : pos + token.length());
			}
			if (pos < 0) {
				return false;
			} else if (i == 0 && !firstWild && pos != 0) {
				return false;
			} else if (i == copyStrings.size() - 1 && !lastWild
					&& pos + token.length() != s.length()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(WildCardPattern o) {
		int sizeInner = strings.size();
		int sizeOuter = o.strings.size();
		for (int i = 0, m = Math.min(sizeOuter, sizeInner); i < m; i++) {
			int lengthInner = strings.get(i).length();
			int lengthOuter = o.strings.get(i).length();
			if (lengthInner < lengthOuter) {
				return 1;
			} else if (lengthInner > lengthOuter) {
				return -1;
			}
		}
		if (sizeInner == sizeOuter)
			return 0;
		return (sizeInner > sizeOuter) ? 1 : -1;
	}

	public String getPattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return strings.toString();
	}



}
