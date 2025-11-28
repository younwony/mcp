# Java 리팩토링 가이드

## 목차
1. [리팩토링 원칙](#리팩토링-원칙)
2. [언제 리팩토링을 해야 하는가](#언제-리팩토링을-해야-하는가)
3. [코드 스멜 (Code Smells)](#코드-스멜-code-smells)
4. [리팩토링 기법](#리팩토링-기법)
5. [객체지향 리팩토링 패턴](#객체지향-리팩토링-패턴)
6. [SOLID 원칙](#solid-원칙)
7. [리팩토링 체크리스트](#리팩토링-체크리스트)

---

## 리팩토링 원칙

### 기본 원칙
1. **테스트 코드를 먼저 작성하라**
   - 리팩토링 전에 충분한 테스트 코드가 있어야 합니다.
   - 테스트가 없다면, 먼저 테스트를 작성하세요.

2. **작은 단위로 리팩토링하라**
   - 한 번에 하나의 변경만 수행합니다.
   - 각 단계마다 테스트를 실행하여 동작을 확인합니다.

3. **기능을 유지하라**
   - 리팩토링은 외부 동작을 변경하지 않고 내부 구조를 개선하는 것입니다.
   - 새로운 기능 추가와 리팩토링을 동시에 하지 마세요.

4. **커밋을 자주 하라**
   - 각 리팩토링 단계마다 커밋합니다.
   - 문제가 발생하면 쉽게 되돌릴 수 있습니다.

---

## 언제 리팩토링을 해야 하는가

### 리팩토링이 필요한 시점

#### 1. 3의 법칙 (Rule of Three)
- 같은 코드를 세 번째 작성할 때 리팩토링합니다.
- 중복 코드는 추상화의 신호입니다.

#### 2. 기능 추가 전
- 새로운 기능을 추가하기 전에 코드를 이해하기 쉽게 정리합니다.
- 기존 구조가 새 기능을 받아들이기 어렵다면 먼저 리팩토링합니다.

#### 3. 버그 수정 시
- 버그를 발견했다면, 코드 구조에 문제가 있을 수 있습니다.
- 버그 수정 전에 코드를 명확하게 만드세요.

#### 4. 코드 리뷰 중
- 다른 사람이 이해하기 어려운 코드를 발견하면 리팩토링합니다.

### 리팩토링을 하지 말아야 할 때

- **마감 직전**: 리팩토링은 즉각적인 가치를 제공하지 않습니다.
- **처음부터 다시 작성하는 것이 나을 때**: 전체를 재작성하는 것이 더 효율적일 수 있습니다.
- **외부 API/라이브러리 코드**: 직접 수정할 수 없는 코드는 리팩토링하지 마세요.

---

## 코드 스멜 (Code Smells)

### 1. 메서드 레벨

#### 긴 메서드 (Long Method)
```java
// Bad
public void processOrder(Order order) {
    // 100줄이 넘는 메서드
    // 주문 검증
    // 재고 확인
    // 결제 처리
    // 배송 준비
    // 이메일 발송
}

// Good
public void processOrder(Order order) {
    validateOrder(order);
    checkInventory(order);
    processPayment(order);
    prepareShipping(order);
    sendConfirmationEmail(order);
}
```

**리팩토링 기법**: Extract Method (메서드 추출)

#### 긴 매개변수 목록 (Long Parameter List)
```java
// Bad
public void createUser(String name, String email, String password,
                      String address, String city, String zipCode,
                      String country, String phone) {
    // ...
}

// Good
public void createUser(UserRegistrationDto registrationDto) {
    // ...
}
```

**리팩토링 기법**: Introduce Parameter Object (매개변수 객체 도입)

#### 복잡한 조건문 (Complex Conditional)
```java
// Bad
if (user.getRole().equals("ADMIN") ||
    (user.getRole().equals("MANAGER") && user.getDepartment().equals("IT"))) {
    // ...
}

// Good
if (hasAdminPrivileges(user)) {
    // ...
}

private boolean hasAdminPrivileges(User user) {
    return user.getRole().equals("ADMIN") ||
           (user.getRole().equals("MANAGER") && user.getDepartment().equals("IT"));
}
```

**리팩토링 기법**: Extract Method, Replace Conditional with Polymorphism

### 2. 클래스 레벨

#### 거대한 클래스 (Large Class)
```java
// Bad
public class Order {
    // 주문 관련 필드 (10개)
    // 고객 관련 필드 (10개)
    // 배송 관련 필드 (10개)
    // 결제 관련 필드 (10개)
    // 50개 이상의 메서드
}

// Good
public class Order {
    private OrderInfo orderInfo;
    private Customer customer;
    private Shipping shipping;
    private Payment payment;
    // ...
}
```

**리팩토링 기법**: Extract Class (클래스 추출)

#### 데이터 클래스 (Data Class)
```java
// Bad
@Data
public class UserDto {
    private String name;
    private String email;
    // getter/setter만 있고 로직이 없음
}

// 사용하는 곳에서
public void processUser(UserDto user) {
    if (user.getEmail() == null || !user.getEmail().contains("@")) {
        throw new IllegalArgumentException("Invalid email");
    }
    // ...
}

// Good
@Data
public class UserDto {
    private String name;
    private String email;

    public void validateEmail() {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}
```

**리팩토링 기법**: Move Method (메서드 이동)

#### 기능에 대한 선망 (Feature Envy)
```java
// Bad
public class OrderService {
    public double calculateTotal(Order order) {
        double total = 0;
        for (OrderItem item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        total -= order.getDiscount();
        return total;
    }
}

// Good
public class Order {
    public double calculateTotal() {
        double total = items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        return total - discount;
    }
}
```

**리팩토링 기법**: Move Method

### 3. 코드 구조

#### 중복 코드 (Duplicated Code)
```java
// Bad
public void sendWelcomeEmail(User user) {
    Email email = new Email();
    email.setTo(user.getEmail());
    email.setFrom("noreply@example.com");
    email.setSubject("Welcome!");
    emailService.send(email);
}

public void sendPasswordResetEmail(User user) {
    Email email = new Email();
    email.setTo(user.getEmail());
    email.setFrom("noreply@example.com");
    email.setSubject("Password Reset");
    emailService.send(email);
}

// Good
public void sendWelcomeEmail(User user) {
    sendEmail(user.getEmail(), "Welcome!", welcomeTemplate);
}

public void sendPasswordResetEmail(User user) {
    sendEmail(user.getEmail(), "Password Reset", resetTemplate);
}

private void sendEmail(String to, String subject, String template) {
    Email email = Email.builder()
        .to(to)
        .from("noreply@example.com")
        .subject(subject)
        .template(template)
        .build();
    emailService.send(email);
}
```

**리팩토링 기법**: Extract Method

#### 매직 넘버/문자열 (Magic Numbers/Strings)
```java
// Bad
if (user.getAge() > 18) {
    // ...
}
if (order.getStatus().equals("COMPLETED")) {
    // ...
}

// Good
private static final int LEGAL_AGE = 18;
private static final String ORDER_STATUS_COMPLETED = "COMPLETED";

if (user.getAge() > LEGAL_AGE) {
    // ...
}
if (order.getStatus().equals(ORDER_STATUS_COMPLETED)) {
    // ...
}

// Better - Enum 사용
public enum OrderStatus {
    PENDING, PROCESSING, COMPLETED, CANCELLED
}

if (order.getStatus() == OrderStatus.COMPLETED) {
    // ...
}
```

**리팩토링 기법**: Replace Magic Number with Symbolic Constant, Replace Type Code with Enum

#### 주석으로 설명되는 코드 (Comments)
```java
// Bad
// 사용자가 성인인지 확인
if (user.getAge() >= 18) {
    // ...
}

// Good
if (isAdult(user)) {
    // ...
}

private boolean isAdult(User user) {
    return user.getAge() >= LEGAL_AGE;
}
```

**원칙**: 주석이 필요하다면, 코드를 더 명확하게 만들어야 합니다.

---

## 리팩토링 기법

### 1. 메서드 추출 (Extract Method)
가장 많이 사용되는 리팩토링 기법입니다.

```java
// Before
public void printOwing() {
    printBanner();

    // print details
    System.out.println("name: " + name);
    System.out.println("amount: " + getOutstanding());
}

// After
public void printOwing() {
    printBanner();
    printDetails();
}

private void printDetails() {
    System.out.println("name: " + name);
    System.out.println("amount: " + getOutstanding());
}
```

### 2. 변수명 개선 (Rename Variable)
```java
// Bad
int d; // elapsed time in days
List<User> u;
String tmp;

// Good
int elapsedTimeInDays;
List<User> activeUsers;
String formattedAddress;
```

### 3. 메서드 인라인 (Inline Method)
간단한 위임만 하는 메서드는 제거합니다.

```java
// Before
public int getRating() {
    return moreThanFiveLateDeliveries() ? 2 : 1;
}

private boolean moreThanFiveLateDeliveries() {
    return numberOfLateDeliveries > 5;
}

// After
public int getRating() {
    return numberOfLateDeliveries > 5 ? 2 : 1;
}
```

### 4. 조건문 분해 (Decompose Conditional)
```java
// Before
if (date.before(SUMMER_START) || date.after(SUMMER_END)) {
    charge = quantity * winterRate + winterServiceCharge;
} else {
    charge = quantity * summerRate;
}

// After
if (isSummer(date)) {
    charge = summerCharge(quantity);
} else {
    charge = winterCharge(quantity);
}
```

### 5. 클래스 추출 (Extract Class)
```java
// Before
public class Person {
    private String name;
    private String officeAreaCode;
    private String officeNumber;

    public String getTelephoneNumber() {
        return "(" + officeAreaCode + ") " + officeNumber;
    }
}

// After
public class Person {
    private String name;
    private TelephoneNumber officeTelephone;

    public String getTelephoneNumber() {
        return officeTelephone.getTelephoneNumber();
    }
}

public class TelephoneNumber {
    private String areaCode;
    private String number;

    public String getTelephoneNumber() {
        return "(" + areaCode + ") " + number;
    }
}
```

### 6. 인터페이스 추출 (Extract Interface)
```java
// Before
public class OrderProcessor {
    public void processOrder(Order order) { }
    public void cancelOrder(Order order) { }
    public void refundOrder(Order order) { }
}

// After
public interface OrderOperations {
    void processOrder(Order order);
    void cancelOrder(Order order);
    void refundOrder(Order order);
}

public class OrderProcessor implements OrderOperations {
    @Override
    public void processOrder(Order order) { }

    @Override
    public void cancelOrder(Order order) { }

    @Override
    public void refundOrder(Order order) { }
}
```

### 7. 매직 넘버를 상수로 (Replace Magic Number with Symbolic Constant)
```java
// Before
double potentialEnergy(double mass, double height) {
    return mass * 9.81 * height;
}

// After
private static final double GRAVITATIONAL_CONSTANT = 9.81;

double potentialEnergy(double mass, double height) {
    return mass * GRAVITATIONAL_CONSTANT * height;
}
```

### 8. 조건문을 다형성으로 (Replace Conditional with Polymorphism)
```java
// Before
public class Bird {
    private BirdType type;

    public double getSpeed() {
        switch (type) {
            case EUROPEAN:
                return getBaseSpeed();
            case AFRICAN:
                return getBaseSpeed() - getLoadFactor();
            case NORWEGIAN_BLUE:
                return isNailed ? 0 : getBaseSpeed();
            default:
                throw new IllegalStateException();
        }
    }
}

// After
public abstract class Bird {
    public abstract double getSpeed();
}

public class EuropeanBird extends Bird {
    @Override
    public double getSpeed() {
        return getBaseSpeed();
    }
}

public class AfricanBird extends Bird {
    @Override
    public double getSpeed() {
        return getBaseSpeed() - getLoadFactor();
    }
}

public class NorwegianBlueBird extends Bird {
    @Override
    public double getSpeed() {
        return isNailed ? 0 : getBaseSpeed();
    }
}
```

### 9. Null 객체 도입 (Introduce Null Object)
```java
// Before
Customer customer = site.getCustomer();
String customerName;
if (customer == null) {
    customerName = "occupant";
} else {
    customerName = customer.getName();
}

// After
public class NullCustomer extends Customer {
    @Override
    public String getName() {
        return "occupant";
    }

    @Override
    public boolean isNull() {
        return true;
    }
}

Customer customer = site.getCustomer(); // never returns null
String customerName = customer.getName();
```

### 10. Optional 사용
```java
// Before
public User findUser(Long id) {
    User user = userRepository.findById(id);
    if (user == null) {
        throw new UserNotFoundException();
    }
    return user;
}

// After
public Optional<User> findUser(Long id) {
    return userRepository.findById(id);
}

// 사용
findUser(id)
    .map(User::getEmail)
    .ifPresent(email -> sendEmail(email));
```

---

## 객체지향 리팩토링 패턴

### 1. 일급 컬렉션 (First Class Collection)
컬렉션을 포함하는 클래스는 반드시 다른 멤버 변수가 없어야 합니다.

**목적**: 컬렉션에 대한 비즈니스 로직을 한 곳에 모으고, 불변성을 보장합니다.

```java
// Bad - 컬렉션을 그대로 노출
public class LottoGame {
    private List<Integer> numbers;
    private String gameName;
    private LocalDateTime purchaseTime;

    public void validateNumbers() {
        if (numbers.size() != 6) {
            throw new IllegalArgumentException("로또 번호는 6개여야 합니다.");
        }
        // 중복 검사, 범위 검사 등
    }
}

// Good - 일급 컬렉션
public class LottoNumbers {
    private static final int LOTTO_NUMBERS_SIZE = 6;
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 45;

    private final List<Integer> numbers;

    public LottoNumbers(List<Integer> numbers) {
        validateSize(numbers);
        validateRange(numbers);
        validateDuplication(numbers);
        this.numbers = new ArrayList<>(numbers);
    }

    private void validateSize(List<Integer> numbers) {
        if (numbers.size() != LOTTO_NUMBERS_SIZE) {
            throw new IllegalArgumentException("로또 번호는 6개여야 합니다.");
        }
    }

    private void validateRange(List<Integer> numbers) {
        if (numbers.stream().anyMatch(num -> num < MIN_NUMBER || num > MAX_NUMBER)) {
            throw new IllegalArgumentException(
                String.format("로또 번호는 %d부터 %d 사이여야 합니다.", MIN_NUMBER, MAX_NUMBER)
            );
        }
    }

    private void validateDuplication(List<Integer> numbers) {
        if (numbers.size() != new HashSet<>(numbers).size()) {
            throw new IllegalArgumentException("로또 번호는 중복될 수 없습니다.");
        }
    }

    public boolean contains(int number) {
        return numbers.contains(number);
    }

    public int countMatches(LottoNumbers other) {
        return (int) this.numbers.stream()
            .filter(other::contains)
            .count();
    }

    public List<Integer> getNumbers() {
        return new ArrayList<>(numbers); // 방어적 복사
    }
}

public class LottoGame {
    private final LottoNumbers numbers;
    private final String gameName;
    private final LocalDateTime purchaseTime;

    public LottoGame(List<Integer> numbers, String gameName) {
        this.numbers = new LottoNumbers(numbers);
        this.gameName = gameName;
        this.purchaseTime = LocalDateTime.now();
    }
}
```

**일급 컬렉션의 장점**:
- 비즈니스 로직이 한 곳에 모여 있어 유지보수가 쉽습니다.
- 컬렉션에 대한 검증 로직이 생성자에 집중됩니다.
- 컬렉션을 불변으로 관리할 수 있습니다.
- 컬렉션 관련 기능을 메서드로 제공할 수 있습니다.

**실전 예제**:
```java
// 주문 항목 일급 컬렉션
public class OrderItems {
    private static final int MAX_ITEMS = 100;

    private final List<OrderItem> items;

    public OrderItems(List<OrderItem> items) {
        validateMaxSize(items);
        this.items = new ArrayList<>(items);
    }

    private void validateMaxSize(List<OrderItem> items) {
        if (items.size() > MAX_ITEMS) {
            throw new IllegalArgumentException("주문 항목은 최대 " + MAX_ITEMS + "개까지 가능합니다.");
        }
    }

    public Money calculateTotalPrice() {
        return items.stream()
            .map(OrderItem::getPrice)
            .reduce(Money.ZERO, Money::add);
    }

    public int getTotalQuantity() {
        return items.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }
}
```

### 2. 원시값 포장 (Wrap Primitive Types)
Primitive Obsession 코드 스멜을 해결하는 패턴입니다.

**목적**: 원시 타입에 의미를 부여하고, 관련 로직을 한 곳에 모읍니다.

```java
// Bad - 원시값 직접 사용
public class User {
    private String email;
    private int age;
    private String phoneNumber;

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.email = email;
    }

    public void setAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age");
        }
        this.age = age;
    }
}

// Good - 원시값 포장
public class Email {
    private final String value;

    public Email(String value) {
        validateFormat(value);
        this.value = value;
    }

    private void validateFormat(String value) {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다: " + value);
        }
    }

    public String getDomain() {
        return value.substring(value.indexOf("@") + 1);
    }

    public String getLocalPart() {
        return value.substring(0, value.indexOf("@"));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

public class Age {
    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 150;

    private final int value;

    public Age(int value) {
        validateRange(value);
        this.value = value;
    }

    private void validateRange(int value) {
        if (value < MIN_AGE || value > MAX_AGE) {
            throw new IllegalArgumentException(
                String.format("나이는 %d부터 %d 사이여야 합니다.", MIN_AGE, MAX_AGE)
            );
        }
    }

    public boolean isAdult() {
        return value >= 18;
    }

    public boolean isSenior() {
        return value >= 65;
    }

    public int getValue() {
        return value;
    }
}

public class User {
    private final Email email;
    private final Age age;
    private final PhoneNumber phoneNumber;

    public User(Email email, Age age, PhoneNumber phoneNumber) {
        this.email = email;
        this.age = age;
        this.phoneNumber = phoneNumber;
    }

    public boolean canVote() {
        return age.isAdult();
    }
}
```

**원시값 포장의 장점**:
- 타입 안정성이 향상됩니다.
- 검증 로직이 한 곳에 모입니다.
- 비즈니스 의미가 명확해집니다.
- IDE의 자동 완성을 활용할 수 있습니다.

### 3. 값 객체 (Value Object)
값으로 취급되는 불변 객체입니다.

**특징**:
- 불변성 (Immutable)
- 동등성 비교 (equals/hashCode 구현)
- 자가 검증 (Self Validation)

```java
// 금액 값 객체
@Getter
public class Money {
    public static final Money ZERO = new Money(0);

    private final BigDecimal amount;

    public Money(long amount) {
        this(BigDecimal.valueOf(amount));
    }

    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 null일 수 없습니다.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("금액은 0보다 작을 수 없습니다.");
        }
        this.amount = amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}

// 사용 예
public class Product {
    private final String name;
    private final Money price;

    public Money calculateTotalPrice(int quantity) {
        return price.multiply(quantity);
    }

    public boolean isMoreExpensiveThan(Product other) {
        return this.price.isGreaterThan(other.price);
    }
}
```

**값 객체 예시**:
```java
// 주소 값 객체
@Getter
@EqualsAndHashCode
public class Address {
    private final String zipCode;
    private final String street;
    private final String city;
    private final String country;

    public Address(String zipCode, String street, String city, String country) {
        validateNotEmpty(zipCode, "우편번호");
        validateNotEmpty(street, "상세주소");
        validateNotEmpty(city, "도시");
        validateNotEmpty(country, "국가");

        this.zipCode = zipCode;
        this.street = street;
        this.city = city;
        this.country = country;
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 비어있을 수 없습니다.");
        }
    }

    public String getFullAddress() {
        return String.format("%s %s, %s %s", street, city, country, zipCode);
    }

    public boolean isSameCity(Address other) {
        return this.city.equals(other.city);
    }
}

// 기간 값 객체
@Getter
public class Period {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Period(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일과 종료일은 필수입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean overlaps(Period other) {
        return !this.endDate.isBefore(other.startDate)
            && !other.endDate.isBefore(this.startDate);
    }
}
```

### 4. 불변 객체 (Immutable Object)
생성 후 상태가 변경되지 않는 객체입니다.

**장점**:
- 스레드 안전성
- 방어적 복사 불필요
- 예측 가능한 동작
- 캐싱 가능

```java
// Bad - 가변 객체
public class MutablePerson {
    private String name;
    private int age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // 예측 불가능한 동작
    public void celebrateBirthday() {
        this.age++; // 상태가 변경됨
    }
}

// Good - 불변 객체
@Getter
public final class ImmutablePerson {
    private final String name;
    private final Age age;
    private final List<String> hobbies;

    public ImmutablePerson(String name, Age age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = new ArrayList<>(hobbies); // 방어적 복사
    }

    // 새로운 객체를 반환
    public ImmutablePerson celebrateBirthday() {
        return new ImmutablePerson(
            this.name,
            new Age(this.age.getValue() + 1),
            this.hobbies
        );
    }

    public ImmutablePerson withName(String newName) {
        return new ImmutablePerson(newName, this.age, this.hobbies);
    }

    public List<String> getHobbies() {
        return new ArrayList<>(hobbies); // 방어적 복사
    }
}
```

**불변 객체 생성 방법**:
```java
// 1. final 클래스와 final 필드
public final class Product {
    private final String name;
    private final Money price;

    public Product(String name, Money price) {
        this.name = name;
        this.price = price;
    }
}

// 2. Lombok @Value 사용
@Value
public class Product {
    String name;
    Money price;
}

// 3. Java 14+ Record 사용
public record Product(String name, Money price) {
    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (price == null) {
            throw new IllegalArgumentException("가격은 필수입니다.");
        }
    }
}
```

**컬렉션 불변화**:
```java
public class Order {
    private final List<OrderItem> items;

    public Order(List<OrderItem> items) {
        // 방어적 복사 + 불변 컬렉션
        this.items = List.copyOf(items);
    }

    public List<OrderItem> getItems() {
        // 이미 불변이므로 그대로 반환 가능
        return items;
    }

    // 또는 Collections.unmodifiableList 사용
    public Order(List<OrderItem> items) {
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }
}
```

### 5. 정적 팩토리 메서드 (Static Factory Method)
생성자 대신 정적 팩토리 메서드를 사용합니다.

**장점**:
- 메서드명으로 의도를 표현할 수 있습니다.
- 호출할 때마다 새 객체를 생성하지 않아도 됩니다.
- 하위 타입 객체를 반환할 수 있습니다.

```java
// Bad - 생성자만 사용
public class User {
    private final String email;
    private final String password;
    private final UserType type;

    public User(String email, String password, UserType type) {
        this.email = email;
        this.password = password;
        this.type = type;
    }
}

// 사용
User admin = new User("admin@example.com", "1234", UserType.ADMIN);
User guest = new User("guest@example.com", "", UserType.GUEST);

// Good - 정적 팩토리 메서드
public class User {
    private final String email;
    private final String password;
    private final UserType type;

    private User(String email, String password, UserType type) {
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public static User createAdmin(String email, String password) {
        validateAdminPassword(password);
        return new User(email, password, UserType.ADMIN);
    }

    public static User createRegularUser(String email, String password) {
        validateUserPassword(password);
        return new User(email, password, UserType.REGULAR);
    }

    public static User createGuest() {
        return new User("guest@example.com", "", UserType.GUEST);
    }

    private static void validateAdminPassword(String password) {
        if (password.length() < 12) {
            throw new IllegalArgumentException("관리자 비밀번호는 최소 12자 이상이어야 합니다.");
        }
    }

    private static void validateUserPassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
    }
}

// 사용
User admin = User.createAdmin("admin@example.com", "verysecurepass123");
User guest = User.createGuest();
```

**캐싱 예제**:
```java
public class Status {
    private static final Map<String, Status> CACHE = new ConcurrentHashMap<>();

    public static final Status PENDING = Status.of("PENDING");
    public static final Status COMPLETED = Status.of("COMPLETED");

    private final String value;

    private Status(String value) {
        this.value = value;
    }

    public static Status of(String value) {
        return CACHE.computeIfAbsent(value, Status::new);
    }
}
```

---

## SOLID 원칙

### 1. Single Responsibility Principle (단일 책임 원칙)
클래스는 하나의 책임만 가져야 합니다.

```java
// Bad
public class UserService {
    public void registerUser(User user) { }
    public void sendEmail(String to, String message) { }
    public void logActivity(String activity) { }
    public void generateReport() { }
}

// Good
public class UserService {
    private EmailService emailService;
    private ActivityLogger activityLogger;
    private ReportGenerator reportGenerator;

    public void registerUser(User user) {
        // user registration logic
        emailService.sendWelcomeEmail(user);
        activityLogger.log("User registered: " + user.getId());
    }
}
```

### 2. Open/Closed Principle (개방-폐쇄 원칙)
확장에는 열려 있고, 수정에는 닫혀 있어야 합니다.

```java
// Bad
public class PaymentProcessor {
    public void processPayment(Payment payment) {
        if (payment.getType().equals("CREDIT_CARD")) {
            // credit card logic
        } else if (payment.getType().equals("PAYPAL")) {
            // paypal logic
        }
        // 새로운 결제 방법 추가 시 이 메서드를 수정해야 함
    }
}

// Good
public interface PaymentMethod {
    void process(Payment payment);
}

public class CreditCardPayment implements PaymentMethod {
    @Override
    public void process(Payment payment) {
        // credit card logic
    }
}

public class PayPalPayment implements PaymentMethod {
    @Override
    public void process(Payment payment) {
        // paypal logic
    }
}

public class PaymentProcessor {
    public void processPayment(Payment payment, PaymentMethod method) {
        method.process(payment);
        // 새로운 결제 방법 추가 시 이 클래스를 수정할 필요 없음
    }
}
```

### 3. Liskov Substitution Principle (리스코프 치환 원칙)
하위 타입은 상위 타입으로 대체 가능해야 합니다.

```java
// Bad
public class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // 정사각형이므로 높이도 같이 변경
    }

    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height;
    }
}

// 문제 발생
Rectangle rect = new Square();
rect.setWidth(5);
rect.setHeight(10);
// 정사각형의 경우 예상과 다른 결과

// Good
public interface Shape {
    int getArea();
}

public class Rectangle implements Shape {
    private int width;
    private int height;

    @Override
    public int getArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private int side;

    @Override
    public int getArea() {
        return side * side;
    }
}
```

### 4. Interface Segregation Principle (인터페이스 분리 원칙)
클라이언트는 사용하지 않는 메서드에 의존하지 않아야 합니다.

```java
// Bad
public interface Worker {
    void work();
    void eat();
    void sleep();
}

public class Robot implements Worker {
    @Override
    public void work() { /* ... */ }

    @Override
    public void eat() { /* 로봇은 먹지 않음 */ }

    @Override
    public void sleep() { /* 로봇은 자지 않음 */ }
}

// Good
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public class Human implements Workable, Eatable, Sleepable {
    @Override
    public void work() { /* ... */ }

    @Override
    public void eat() { /* ... */ }

    @Override
    public void sleep() { /* ... */ }
}

public class Robot implements Workable {
    @Override
    public void work() { /* ... */ }
}
```

### 5. Dependency Inversion Principle (의존성 역전 원칙)
고수준 모듈은 저수준 모듈에 의존하지 않아야 합니다. 둘 다 추상화에 의존해야 합니다.

```java
// Bad
public class OrderService {
    private MySQLDatabase database = new MySQLDatabase();

    public void saveOrder(Order order) {
        database.save(order);
    }
}

// Good
public interface Database {
    void save(Order order);
}

public class MySQLDatabase implements Database {
    @Override
    public void save(Order order) {
        // MySQL specific implementation
    }
}

public class OrderService {
    private final Database database;

    public OrderService(Database database) {
        this.database = database;
    }

    public void saveOrder(Order order) {
        database.save(order);
    }
}
```

---

## 리팩토링 체크리스트

### 리팩토링 전

- [ ] 충분한 단위 테스트가 작성되어 있는가?
- [ ] 모든 테스트가 통과하는가?
- [ ] 변경 사항을 백업했는가? (git commit)
- [ ] 리팩토링 범위를 명확히 정의했는가?
- [ ] 어떤 코드 스멜을 해결할 것인지 파악했는가?

### 리팩토링 중

- [ ] 한 번에 하나의 리팩토링만 수행하는가?
- [ ] 각 변경 후 테스트를 실행하는가?
- [ ] 기능이 변경되지 않았는가?
- [ ] 코드가 더 읽기 쉬워졌는가?
- [ ] 불필요한 복잡성을 추가하지 않았는가?

### 리팩토링 후

- [ ] 모든 테스트가 통과하는가?
- [ ] 테스트 커버리지가 유지되거나 향상되었는가?
- [ ] 코드 리뷰를 받았는가?
- [ ] 성능이 저하되지 않았는가?
- [ ] 문서가 업데이트되었는가? (필요한 경우)
- [ ] 변경 사항을 커밋했는가?

---

## 추천 도구

### IntelliJ IDEA 리팩토링 기능
- `Ctrl + Alt + M`: Extract Method
- `Shift + F6`: Rename
- `Ctrl + Alt + V`: Extract Variable
- `Ctrl + Alt + C`: Extract Constant
- `Ctrl + Alt + P`: Extract Parameter
- `Ctrl + Alt + F`: Extract Field
- `F6`: Move
- `Ctrl + F6`: Change Signature

### 정적 분석 도구
- **SonarLint**: IDE에서 실시간 코드 품질 체크
- **Checkstyle**: 코딩 규칙 준수 확인
- **PMD**: 잠재적인 버그와 코드 스멜 탐지
- **SpotBugs**: 버그 패턴 탐지

---

## 참고 자료

- Martin Fowler - "Refactoring: Improving the Design of Existing Code"
- Robert C. Martin - "Clean Code"
- Joshua Bloch - "Effective Java"
- Gang of Four - "Design Patterns"

---

## 마무리

리팩토링은 코드 품질을 지속적으로 개선하는 과정입니다. 작은 변경을 자주 수행하고, 항상 테스트를 통해 검증하세요. 완벽한 코드는 없지만, 지속적인 개선을 통해 더 나은 코드를 만들 수 있습니다.
