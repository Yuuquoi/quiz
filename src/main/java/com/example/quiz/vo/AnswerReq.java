package com.example.quiz.vo;

import java.util.List;

import com.example.quiz.entity.Answer;

public class AnswerReq extends Answer {

	private List<Answer> answerList;

	public AnswerReq() {
		super();
	}

	public AnswerReq(String name, String phone, String email, int age, int quizId, int quId, String answer) {
		super(name, phone, email, age, quizId, quId, answer);
	}

	public AnswerReq(List<Answer> answerList) {
		super();
		this.answerList = answerList;
	}

	public List<Answer> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<Answer> answerList) {
		this.answerList = answerList;
	}

}
