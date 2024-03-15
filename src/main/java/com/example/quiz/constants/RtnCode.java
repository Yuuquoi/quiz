package com.example.quiz.constants;

public enum RtnCode {

	SUCCESS(200, "Success!!"), //
	PARAM_ERROR(400, "Param Error !!"), //
	QUIZ_EXISTS(400, "Quiz Existes !!"), //
	QUIZ_NOT_FOUND(400, "Quiz Not Found !!"), //
	QUIZ_ID_DOES_NOT_MATCH(400, "Quiz Id Does Not Match !!"), //
	QUIZ_ID_ERROR(400, "Quiz Id Error !!"), //
	QUESTION_NO_ANSWER(400, "Question no answer !!"), //
	DUPLICATED_QUESTION_ID(400, "Duplicated Question Id !!"), //
	DUPLICATED_QUIZ_ANSWER(400, "Duplicated quiz answer !!"), //
	TIME_FORMAT_ERROR(400, "Time Format Error !!");

	private int code;

	private String message;

	private RtnCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
