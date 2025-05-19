CREATE TABLE IF NOT EXISTS event_source (
                                            source_id VARCHAR(100) NOT NULL COMMENT '이벤트 출처 아이디',
    source_type VARCHAR(50) NOT NULL COMMENT '이벤트 출처 타입',
    PRIMARY KEY (source_id, source_type)
    );

CREATE TABLE IF NOT EXISTS events (
                                      event_no BIGINT AUTO_INCREMENT NOT NULL COMMENT '이벤트 번호',
                                      department_id VARCHAR(50) NOT NULL COMMENT '부서 아이디',
    event_at DATETIME COMMENT '이벤트 발생 일자',
    event_details TEXT NOT NULL COMMENT '이벤트 내용',
    level_name VARCHAR(50) NOT NULL COMMENT '이벤트 레벨',
    source_id VARCHAR(100),
    source_type VARCHAR(50),
    PRIMARY KEY (event_no),
    FOREIGN KEY (source_id, source_type) REFERENCES event_source(source_id, source_type)
    );

CREATE TABLE IF NOT EXISTS notifications (
                                             notification_no BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             is_read TINYINT,
                                             user_no BIGINT,
                                             event_no BIGINT NOT NULL,
                                             FOREIGN KEY (event_no) REFERENCES events(event_no)
    );
