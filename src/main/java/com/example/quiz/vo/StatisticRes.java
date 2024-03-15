package com.example.quiz.vo;

import java.util.Map;

public class StatisticRes extends BaseRes {
	
	private Map<Integer, Map<String, Integer>> result;

	public StatisticRes() {
		super();
	}

	public StatisticRes(int code, String message) {
		super(code, message);
	}

	public StatisticRes(int code, String message, Map<Integer, Map<String, Integer>> result) {
		super(code, message);
		this.result = result;
	}

	public Map<Integer, Map<String, Integer>> getResult() {
		return result;
	}

	public void setResult(Map<Integer, Map<String, Integer>> result) {
		this.result = result;
	}
	
}
