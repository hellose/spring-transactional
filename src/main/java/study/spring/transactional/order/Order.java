package study.spring.transactional.order;

import lombok.Data;

import javax.annotation.processing.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private String username; // 정상, 예외, 잔고 부족
    private String payStatus; // 대기, 완료

}
