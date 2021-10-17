package life.majiang.community.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.QuestionExtMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class QuestionRateLimiter {

    private static Cache<Long, Integer> userLimiter = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .removalListener(entity -> log.info("QUESTIONS_RATE_LIMITER_REMOVE:{}", entity.getKey()))
            .build();

    public static boolean reachLimit(Long userId) {
        try {
            Integer limit = userLimiter.get(userId, () -> 0);
            userLimiter.put(userId, limit + 1);
            return limit > 1;
        } catch (ExecutionException e) {
            return false;
        }
    }
}