package com.quizai.history.service;

import com.quizai.domain.R;
import com.quizai.history.domain.dto.HistorySubmitRequest;

public interface PracticeHistoryService {

    R submitHistory(HistorySubmitRequest request, Integer userId);

    R getHistoryList(Integer userId);

    R getHistoryDetail(Long recordId, Integer userId);
}
