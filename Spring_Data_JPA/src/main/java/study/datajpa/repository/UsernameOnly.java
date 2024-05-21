package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    /**
     * SpEL 문법도 사용 가능 /  But select 절 최적화 x  => entity 전체 필드 조회해서 계산
     */
//    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
