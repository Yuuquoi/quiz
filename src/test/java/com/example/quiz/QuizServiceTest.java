package com.example.quiz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;

@SpringBootTest
public class QuizServiceTest {

	@Autowired
	private QuizService quizService;

	@Autowired
	private QuizDao quizDao;

	@BeforeEach
	private void addData() {
		CreateOrUpdateReq req = new CreateOrUpdateReq();
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "Qtest", "single", true, "A;B;C;D", false))));
		quizService.create(req);
	}

	private Quiz quiz;

	CreateOrUpdateReq req = new CreateOrUpdateReq(new ArrayList<>(Arrays.asList(quiz)), false);

	@Test
	public void createTest() {
		/*********** 代刚 req null ***********/
		CreateOrUpdateReq req = new CreateOrUpdateReq();
		BaseRes res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail:  req null !!");
		/*********** 代刚 QuizId ***********/
		quizIdTest(-1, res);
		/*********** 代刚 QuId ***********/
		quIdTest(-1, res);
		/*********** 代刚 QuizName ***********/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "", "testblabla", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "Qtest", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: QuizName !!");
		/*********** 代刚 startDate ***********/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", null,
				LocalDate.now().plusDays(9), "Qtest", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: startDate !!");
		/*********** 代刚 endDate ***********/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(2),
				null, "Qtest", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: endDate !!");
		/*********** 代刚 question name ***********/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: question name !!");
		/*********** 代刚 type ***********/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "Qtest", "", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: type !!");
		/*********** 代刚 startDate > endDate ***********/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(9),
				LocalDate.now().plusDays(2), "Qtest", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: startDate > endDate !!");
		/*********** 代刚 Success ***************/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "Qtest", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "Create Test Fail: Success !!");
		/*********** 代刚 repeat quiz ***************/
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "Qtest", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: repeat quiz !!");
		// delete
		quizDao.deleteAll(req.getQuizList());
	}

	private void newQuiz() {
		quiz = new Quiz(1, 1, "Test", "testblabla", LocalDate.now().plusDays(2), LocalDate.now().plusDays(9), "Qtest",
				"single", true, "A;B;C;D", false);
	}

	private void quizIdTest(int testNumber, BaseRes res) {
		newQuiz();
		quiz.setQuizId(testNumber);
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: QuizId !!");
	}

	private void quIdTest(int testNumber, BaseRes res) {
		newQuiz();
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, -1, "Test", "testblabla", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "Qtest", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "Create Test Fail: QuId !!");
	}

	@Test
	public void updateTest() {
		addData();
	}

	@Test
	public void searchTest() {

	}

	@Test
	public void deleteQuizTest() {

	}

}
