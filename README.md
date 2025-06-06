## Содержание
- [Введение](#что-это)
- [Как пользоваться](#как-пользоваться)
- [Структура проекта](#структура-проекта)
- [Тесты](#тесты)
## Что это
Приложение, моделирующее простую банковскую систему с базовыми возможностями: создание пользователей, операции со счетом (внесение, снятие, перевод), просмотр профиля и истории транзакций.

**Используемые зависимости**:
* JUnit5
* Mockito
* Lombok
* Jackson
* Picocli
* Allure

## Как пользоваться
1. По нажатию на Enter выводится список доступных команд:
```
Commands:
  reg         Регистрация пользователя
  login       Авторизация пользователя
  history     View transaction history of an account
  profile     Show user profile
  acc-create  Create account for current user
  deposit     Deposit money into an account
  withdraw    Withdraw money from an account
  transfer    Transfer money between accounts
```
3. После ввода команды и нажатия на Enter выводятся доступные аргументы
```
> reg
Missing required options: '--username=<username>', '--email=<email>', '--password=<password>', '--confirmPassword=<confirmPassword>'
Usage: zbank reg -cp=<confirmPassword> -e=<email> -p=<password> -u=<username>
Регистрация пользователя
      -cp, --confirmPassword=<confirmPassword>

  -e, --email=<email>
  -p, --password=<password>

  -u, --username=<username>
```
4. Теперь для демонстрации создадим двух пользователей, пополним их депозиты и переведём средства. Сначала регистрация и авторизация
```
reg -u user1 -e email1@mail.com -p pass1 -cp pass1
reg -u user2 -e email2@mail.com -p pass2 -cp pass2
```
Теперь логинимся:
```
> login -e email1@mail.com -p pass1
Login successful!
```
После создания аккаунта необходимо создать новый счёт. Для примера создадим депозитный:
``acc-create -t DEBIT``.

Для пополнения нужно посмотреть в профиле id созданного счёта (в нашем случае это 86ccf11e-1e7b-4994-8491-53a97cdc3f7c)
```
> profile

???????????????????????????????????????????????????????????????????
?                       USER PROFILE                              ?
???????????????????????????????????????????????????????????????????
? Name                : user1                                    ?
? Email               : email1@mail.com                          ?
? User ID             : eb2b5925-e01c-44ea-89b8-f6228bb303fa     ?
???????????????????????????????????????????????????????????????????
?                            ACCOUNTS                             ?
???????????????????????????????????????????????????????????????????
? ID                                   ? Type           ? Balance ?
???????????????????????????????????????????????????????????????????
? 86ccf11e-1e7b-4994-8491-53a97cdc3f7c ? DEBIT           ?       0.00 ?
???????????????????????????????????????????????????????????????????
```
Пополняем баланс:
``deposit -m 1000 -n 86ccf11e-1e7b-4994-8491-53a97cdc3f7c``.

5. Для второго пользователя процесс полностью аналогичный
6. Осуществлять перевод можно только со своего счёта:
```
> login -e email1@mail.com -p pass1
Login successful!
> transfer -t 881bb562-f968-4f93-8633-7ade18e12fb5 -f 86ccf11e-1e7b-4994-8491-53a97cdc3f7c -m 500
Transferred 500.0 from account 86ccf11e-1e7b-4994-8491-53a97cdc3f7c to account 881bb562-f968-4f93-8633-7ade18e12fb5
```
7. Посмотреть историю переводов можно комадной *history*. Все транзакции сохраняются в папке database/, в файле transactions.json:
![image](https://github.com/user-attachments/assets/2d6d2c6f-316c-4ecd-8cd0-918f1755d35c)

## Структура проекта
* **database/** - директория, в которой хранятся данные о пользователях, счетах и транзакциях в json-формате
* **.github/** - содержит workflow
* **AccountService / AccountRepository** - содержит бинес-логику для работы со счетами (создание, сохранение и поиск)
* **UserService / UserRepository** - с пользователями (регистрация, авторизация, сохранение и поиск)
* **BankService** - бизнес-логика, отвечающая за взаимодействие пользователей со своими и чужими счетами
* **TransactionRepository** - сохранение и поиск транзакций в базе

## Тесты
1. Для каждого сервиса и репозитория создан свой набор тестов
2. Тесты, в которых необходима работа с файловой системой, используют временные директории
3. Для создания отчётности подключен allure
4. При пуше или создании pr в ветки test или master автоматически запускается пайплайн (вкладка Actions), который выполняет все тесты и генерирует отчёт в формате Allure.
