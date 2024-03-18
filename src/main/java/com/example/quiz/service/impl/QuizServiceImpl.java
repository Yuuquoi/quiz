package com.example.quiz.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.RtnCode;
import com.example.quiz.entity.Answer;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.AnswerDao;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticRes;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private AnswerDao answerDao;

	@Override
	public BaseRes create(CreateOrUpdateReq req) {
		return checkParams(req, true);
	}

	@Override
	public SearchRes search(String quizName, LocalDate startDate, LocalDate endDate, boolean backend) {
		if (!StringUtils.hasText(quizName)) {
			quizName = "";
		}
		if (startDate == null) {
			startDate = LocalDate.of(1912, 1, 1);
		}
		if (endDate == null) {
			endDate = LocalDate.of(9999, 12, 31);
		}
		if (backend) {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(quizName,
							startDate, endDate));
		} else {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
							quizName, startDate, endDate));
		}
	}

	@Override
	public BaseRes deleteQuiz(List<Integer> quizIds) {
		if (CollectionUtils.isEmpty(quizIds)) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		quizDao.deleteAllByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(quizIds, quizIds, LocalDate.now());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes deleteQuestions(int quizId, List<Integer> quIds) {
		if (quizId <= 0 || CollectionUtils.isEmpty(quIds)) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		List<Quiz> res = quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(quizId, quizId,
				LocalDate.now());
		if (res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());
		}
		int i = 1;
		for (int j = 0; j < res.size(); j++) {
			if (!quIds.contains(res.get(j).getQuId())) {
				res.get(j).setQuId(i);
				i++;
			} else {
				res.remove(j);
			}
		}
		quizDao.deleteByQuizId(quizId);
		if (!res.isEmpty()) {
			quizDao.saveAll(res);
		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes update(CreateOrUpdateReq req) {
		return checkParams(req, false);
	}

	private BaseRes checkParams(CreateOrUpdateReq req, boolean isCreate) {
		if (CollectionUtils.isEmpty(req.getQuizList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		// 檢查必填項目
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
		Set<Integer> quizIds = new HashSet<>(); // set 不會存在相同的值
		Set<Integer> quIds = new HashSet<>(); // 檢查問題編號是否重複
		for (Quiz item : req.getQuizList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_DOES_NOT_MATCH.getCode(), RtnCode.QUIZ_ID_DOES_NOT_MATCH.getMessage());
		}
		if (quIds.size() != req.getQuizList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}
		// 檢查時間
		for (Quiz item : req.getQuizList()) {
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ERROR.getCode(), RtnCode.TIME_FORMAT_ERROR.getMessage());
			}
		}
		if (isCreate) {
			if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {
				return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(), RtnCode.QUIZ_EXISTS.getMessage());
			}
		} else {
			if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
					req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
				return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());
			}
			try {
				quizDao.deleteByQuizId(req.getQuizList().get(0).getQuId());				
			} catch (Exception e) {
				return new BaseRes(RtnCode.DELETE_QUIZ_ERROR.getCode(), RtnCode.DELETE_QUIZ_ERROR.getMessage());
			}
		}
		// 防呆：怕前端沒修改
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		try {
			quizDao.saveAll(req.getQuizList());			
		} catch (Exception e) {
			return new BaseRes(RtnCode.SAVE_QUIZ_ERROR.getCode(), RtnCode.SAVE_QUIZ_ERROR.getMessage());
		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes answer(AnswerReq req) {
		if (CollectionUtils.isEmpty(req.getAnswerList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		for (Answer item : req.getAnswerList()) {
			if (!StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() < 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
		Set<Integer> quizIds = new HashSet<>();
		Set<Integer> quIds = new HashSet<>();
		for (Answer item : req.getAnswerList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_ERROR.getCode(), RtnCode.QUIZ_ID_ERROR.getMessage());
		}
		if (quIds.size() != req.getAnswerList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}
		// 檢查必填問題是否有回答
		List<Integer> res = quizDao.findQuIdsByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
		for (Answer item : req.getAnswerList()) {
			if (!res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_NO_ANSWER.getCode(), RtnCode.QUESTION_NO_ANSWER.getMessage());
			}
		}
		// 確認同一個 email 不能重複填寫同一張問卷
		if (answerDao.existsByQuizIdAndEmail(req.getAnswerList().get(0).getQuizId(),
				req.getAnswerList().get(0).getEmail())) {
			return new BaseRes(RtnCode.DUPLICATED_QUIZ_ANSWER.getCode(), RtnCode.DUPLICATED_QUIZ_ANSWER.getMessage());
		}
		answerDao.saveAll(req.getAnswerList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public StatisticRes statistics(int quizId) {
		if (quizId <= 0) {
			return new StatisticRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		List<Quiz> quizs = quizDao.findByQuizId(quizId);
		List<Answer> answers = answerDao.findByQuizIdOrderByQuId(quizId);
		List<Integer> qus = new ArrayList<>();
		for (Quiz item : quizs) {
			if (StringUtils.hasText(item.getOptions())) {
				qus.add(item.getQuId());
			}
		}
		int a = 0;
		String ans = new String();
		for (Integer item : qus) {
			boolean isMatch = false;
			// 如果遇到過，就跳到下一個 QUS
			// 否則還沒到剛好的 QUS
			// QUS: 23
			// answer: 1111111111222222333334444
			for (int i = a; i < answers.size(); i++) {
				if (answers.get(i).getQuId() == item) {
					isMatch = true;
					ans += answers.get(i).getAnswer();
					continue;
				}
				if (isMatch) {
					a = i;
					break;
				}
			}
			countOption(quizs.get(item - 1), ans); // 計算該題的各選項數目
			ans = null; // 重置
		}
		return new StatisticRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(), answerCountMap);
	}

	private Map<Integer, Map<String, Integer>> answerCountMap = new HashMap<>();

	private void countOption(Quiz quiz, String ans) {
		if (!StringUtils.hasText(ans)) { // 如果最後一題不是選擇題，他帶入的 String 必然為空
			return;
		}
		Map<String, Integer> ansCountMap = new HashMap<>(); // 每題進入都會重置一次
		String[] optionList = quiz.getOptions().split(";");
		for (String option : optionList) {
			int lengthBefore = ans.length();
			ans = ans.replace(option, "");
			int lengthAfter = ans.length();
			int count = (lengthBefore - lengthAfter) / option.length();
			ansCountMap.put(option, count);
			ansCountMap.entrySet();
		}
		answerCountMap.put(quiz.getQuId(), ansCountMap);
	}

	@Override
	public BaseRes objMapper(String str) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Quiz quiz = mapper.readValue(str, Quiz.class);
		} catch (Exception e) {
			// 法一：回傳固定錯誤訊息
//			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			// 法二：回傳 catch 中 exception 的錯誤訊息
			return new BaseRes(RtnCode.ERROR_CODE, e.getMessage());

		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}
	
	

}
