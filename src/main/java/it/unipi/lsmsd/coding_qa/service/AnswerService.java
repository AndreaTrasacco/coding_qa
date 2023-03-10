package it.unipi.lsmsd.coding_qa.service;

import it.unipi.lsmsd.coding_qa.dto.AnswerDTO;
import it.unipi.lsmsd.coding_qa.dto.PageDTO;
import it.unipi.lsmsd.coding_qa.dto.VoteDTO;
import it.unipi.lsmsd.coding_qa.service.exception.BusinessException;

public interface AnswerService {
    void addAnswer(String questionId, AnswerDTO answerDTO) throws BusinessException;

    void updateAnswer(String answerId, String body) throws BusinessException;

    void deleteAnswer(AnswerDTO answerDTO) throws BusinessException;

    boolean voteAnswer(VoteDTO voteDTO) throws BusinessException; // true: upvote, false: downvote

    void reportAnswer(String answerId, boolean report) throws BusinessException;

    PageDTO<AnswerDTO> getReportedAnswers(int page) throws BusinessException;

    boolean acceptAnswer(String answerId) throws BusinessException;

    PageDTO<AnswerDTO> getAnswersPage(int page, String questionId) throws BusinessException;

    void getCompleteAnswer(AnswerDTO answerDTO) throws BusinessException;
}
