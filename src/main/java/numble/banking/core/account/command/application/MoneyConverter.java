package numble.banking.core.account.command.application;

import javax.persistence.AttributeConverter;
import numble.banking.core.account.command.domain.Money;

public class MoneyConverter implements AttributeConverter<Money, Long> {

  @Override
  public Long convertToDatabaseColumn(Money money) {
    return money == null ? null : money.getMoney();
  }

  @Override
  public Money convertToEntityAttribute(Long dbData) {
    return dbData == null ? null : new Money(dbData);
  }
}
