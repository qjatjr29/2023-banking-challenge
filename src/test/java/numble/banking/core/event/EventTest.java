//package numble.banking.core.event;
//
//
//import java.time.LocalDateTime;
//import numble.banking.core.account.command.domain.Money;
//import numble.banking.core.account.command.domain.TransferCompletedEvent;
//import numble.banking.core.common.event.Events;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.event.ApplicationEvents;
//import org.springframework.test.context.event.RecordApplicationEvents;
//
//@DisplayName("이벤트 관련 테스트")
//@SpringBootTest
//@ActiveProfiles("test")
//@RecordApplicationEvents
//public class EventTest {
//
//  @Autowired
//  ApplicationEvents events;
//
//  @Test
//  @DisplayName("이체 성공 이벤트 테스트")
//  void transferCompletedEventTest() {
//
//    // given
//    TransferCompletedEvent event = new TransferCompletedEvent("testUser", "0000-000-000000", new Money(1000L), new Money(1000L), true, LocalDateTime.now());
//
//    // when
//    Events.raise(event);
//
//    // then
//
//    Assertions.assertThat((int) events.stream(TransferCompletedEvent.class).count()).isEqualTo(1);
//  }
//}
