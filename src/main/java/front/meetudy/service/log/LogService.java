package front.meetudy.service.log;

import front.meetudy.auth.LoginUser;
import front.meetudy.dto.request.log.LogReqDto;
import front.meetudy.repository.log.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logInsert(LogReqDto logReqDto , LoginUser loginUser) {
        logRepository.save(logReqDto.toEntity(loginUser));
    }

}
