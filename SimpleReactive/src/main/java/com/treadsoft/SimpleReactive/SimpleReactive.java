package com.treadsoft.SimpleReactive;

import java.util.Arrays;
import java.util.List;

import rx.Observable;

public class SimpleReactive {

	public static void main(String[] args) {
		List<String> words = Arrays.asList(
				 "the",
				 "quick",
				 "brown",
				 "fox",
				 "jumped",
				 "over",
				 "the",
				 "lazy",
				 "dog"
				);

				Observable.from(words)
				          .subscribe(word->System.out.println(word));
	}
}
