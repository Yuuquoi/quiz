package com.example.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quiz.entity.Answer;

public interface AnswerDao extends JpaRepository<Answer, Integer> {
	
	public List<Answer> findByQuizIdOrderByQuId(int quizId);
	
	public boolean existsByQuizIdAndEmail(int quizId, String email);

}
